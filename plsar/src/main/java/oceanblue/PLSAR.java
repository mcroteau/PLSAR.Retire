package oceanblue;

import oceanblue.implement.ViewRenderer;
import oceanblue.model.*;
import oceanblue.resources.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PLSAR {

    static Logger Log = Logger.getLogger(PLSAR.class.getName());

    Integer port;
    SchemaConfig schemaConfig;
    Integer NUMBER_PARTITIONS = 2;
    Integer NUMBER_REQUEST_EXECUTORS = 4;
    PersistenceConfig persistenceConfig;
    List<ViewRenderer> viewRenderers;

    public PLSAR(int port){
        this.port = port;
        this.viewRenderers = new ArrayList<>();
    }

    public void start(){
        try {
            Integer TOTAL_NUMBER_EXECUTORS = NUMBER_PARTITIONS * NUMBER_REQUEST_EXECUTORS;

            DatabaseEnvironmentManager databaseEnvironmentManager = new DatabaseEnvironmentManager();
            databaseEnvironmentManager.configure(schemaConfig, persistenceConfig);

            ServerResources serverResources = new ServerResources();
            AnnotationInspector annotationInspector = new AnnotationInspector(new AnnotationElementHolder());
            annotationInspector.retroInspect();
            AnnotationElementHolder annotationElementHolder = annotationInspector.getAnnotationElementHolder();

            Log.info("Registering route directors, please wait...");
            List<RouteDirector> routeDirectors = getRouteNegotiators(TOTAL_NUMBER_EXECUTORS, serverResources, annotationElementHolder);
            ConcurrentMap<String, String> sessionRouteRegistry = new ConcurrentHashMap<>(0, 3, 63010);
            ConcurrentMap<String, RouteDirector> routeDirectorRegistry = registerRouteDirectors(routeDirectors);

            RedirectRegistry redirectRegistry = new RedirectRegistry();

            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setPerformancePreferences(0, 1, 2);
            ExecutorService executors = Executors.newFixedThreadPool(NUMBER_PARTITIONS);
            executors.execute(new PartitionedExecutor(NUMBER_REQUEST_EXECUTORS, serverSocket, redirectRegistry, sessionRouteRegistry, routeDirectorRegistry, viewRenderers));

            Log.info("Ready!");

        }catch(IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ex){
            ex.printStackTrace();
        }
    }

    ConcurrentMap<String, RouteDirector> registerRouteDirectors(List<RouteDirector> routeDirectors) {
        ConcurrentMap<String, RouteDirector> routeDirectorRegistry = new ConcurrentHashMap<>(0, 3, 63010);
        for(RouteDirector routeDirector : routeDirectors){
            routeDirectorRegistry.put(routeDirector.getGuid(), routeDirector);
        }
        return routeDirectorRegistry;
    }

    List<RouteDirector> getRouteNegotiators(Integer TOTAL_NUMBER_EXECUTORS, ServerResources serverResources, AnnotationElementHolder annotationElementHolder) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<RouteDirector> routeDirectors = new ArrayList();
        for(Integer activeIndex = 0; activeIndex < TOTAL_NUMBER_EXECUTORS; activeIndex++){//todo:set guid
            AnnotationElement serverStartup = annotationElementHolder.getServerStartup();
            Method startupMethod = serverStartup.getKlass().getMethod("startup");

            RouteAttributes routeAttributes = (RouteAttributes) startupMethod.invoke(serverResources.getObject(serverStartup.getKlass()));

            RouteEndpointsResolver routeEndpointsResolver = new RouteEndpointsResolver(serverResources);
            RouteEndpointHolder routeEndpointHolder = routeEndpointsResolver.resolve();
            routeAttributes.setRouteEndpointHolder(routeEndpointHolder);

            PersistenceConfig persistenceConfig = new PersistenceConfig();
            persistenceConfig.setDriver(this.persistenceConfig.getDriver());
            persistenceConfig.setUrl(this.persistenceConfig.getUrl());
            persistenceConfig.setUser(this.persistenceConfig.getUser());
            persistenceConfig.setConnections(this.persistenceConfig.getConnections());
            persistenceConfig.setPassword(this.persistenceConfig.getPassword());

            routeAttributes.setPersistenceConfig(persistenceConfig);

            RouteDirector routeDirector = new RouteDirector();
            routeDirector.setRouteAttributes(routeAttributes);

            routeDirectors.add(routeDirector);
        }
        return routeDirectors;
    }

    public static class PartitionedExecutor implements Runnable{
        Integer numberOfExecutors;
        ServerSocket serverSocket;
        ConcurrentMap<String, String> sessionRouteRegistry;
        ConcurrentMap<String, RouteDirector> routeDirectorRegistry;
        RedirectRegistry redirectRegistry;
        List<ViewRenderer> viewRenderers;

        public PartitionedExecutor(Integer numberOfExecutors, ServerSocket serverSocket, RedirectRegistry redirectRegistry, ConcurrentMap<String, String> sessionRouteRegistry, ConcurrentMap<String, RouteDirector> routeDirectorRegistry, List<ViewRenderer> viewRenderers) {
            this.numberOfExecutors = numberOfExecutors;
            this.serverSocket = serverSocket;
            this.redirectRegistry = redirectRegistry;
            this.sessionRouteRegistry = sessionRouteRegistry;
            this.routeDirectorRegistry = routeDirectorRegistry;
            this.viewRenderers = viewRenderers;
        }

        @Override
        public void run() {
            ExecutorService executors = Executors.newFixedThreadPool(numberOfExecutors);
            executors.execute(new HttpRequestIngester(executors, serverSocket, redirectRegistry, sessionRouteRegistry, routeDirectorRegistry, viewRenderers));
        }
    }

    public static class HttpRequestIngester implements Runnable {

        final String BREAK = "\r\n";
        final String SPACE = " ";
        final String DOUBLEBREAK = "\r\n\r\n";

        final Integer REQUEST_METHOD = 0;
        final Integer REQUEST_PATH = 1;
        final Integer VERSION = 2;

        Socket socketClient;
        ExecutorService executors;
        ServerSocket serverSocket;
        ConcurrentMap<String, String> sessionRouteRegistry;
        ConcurrentMap<String, RouteDirector> routeDirectorRegistry;
        RedirectRegistry redirectRegistry;
        List<ViewRenderer> viewRenderers;

        public HttpRequestIngester(ExecutorService executors, ServerSocket serverSocket, RedirectRegistry redirectRegistry, ConcurrentMap<String, String> sessionRouteRegistry, ConcurrentMap<String, RouteDirector> routeDirectorRegistry, List<ViewRenderer> viewRenderers){
            this.executors = executors;
            this.serverSocket = serverSocket;
            this.redirectRegistry = redirectRegistry;
            this.sessionRouteRegistry = sessionRouteRegistry;
            this.routeDirectorRegistry = routeDirectorRegistry;
            this.viewRenderers = viewRenderers;
        }

        @Override
        public void run() {
            try {

                ServerResources serverResources = new ServerResources();

                socketClient = serverSocket.accept();
                Thread.sleep(139);
                InputStream requestInputStream = socketClient.getInputStream();

                OutputStream clientOutput = socketClient.getOutputStream();

                if(requestInputStream.available() == 0) {
                    requestInputStream.close();
                    executors.execute(new HttpRequestIngester(executors, serverSocket, redirectRegistry, sessionRouteRegistry, routeDirectorRegistry, viewRenderers));
                    return;
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int bytesRead;
                while((bytesRead = requestInputStream.read(byteBuffer.array())) != -1){
                    byteArrayOutputStream.write(byteBuffer.array(), 0, bytesRead);
                    if(requestInputStream.available() == 0)break;
                }

                String completeRequestContent = byteArrayOutputStream.toString();
                String[] requestBlocks = completeRequestContent.split(DOUBLEBREAK, 2);

                String headerComponent = requestBlocks[0];
                String[] methodPathComponentsLookup = headerComponent.split(BREAK);
                String methodPathComponent = methodPathComponentsLookup[0];

                String[] methodPathVersionComponents = methodPathComponent.split("\\s");

                String requestVerb = methodPathVersionComponents[REQUEST_METHOD];
                String requestPath = methodPathVersionComponents[REQUEST_PATH];

                HttpRequest httpRequest = new HttpRequest(requestVerb, requestPath, serverResources, sessionRouteRegistry);
                HttpResponse httpResponse = new HttpResponse();
                httpResponse.setResponseOutput(clientOutput);
                httpResponse.setContentType("text/html");

                ComponentCompiler requestComponentCompiler = new ComponentCompiler(byteArrayOutputStream.toByteArray(), httpRequest);
                requestComponentCompiler.ingestRequest();

                String[] headerComponents = headerComponent.split(BREAK);
                for(String headerLine : headerComponents){
                    String[] headerLineComponents = headerLine.split(":");
                    if(headerLineComponents.length == 2) {
                        String fieldKey = headerLineComponents[0].trim();
                        String content = headerLineComponents[1].trim();
                        httpRequest.getHeaders().put(fieldKey.toLowerCase(), content);
                    }
                }

                Long time = serverResources.getTime(0);

                String sessionGuid = serverResources.getCookie(httpRequest.getHeaders());
                if(sessionGuid == null) sessionGuid = serverResources.getGuid(24);

                String routeDirectorGuid = sessionRouteRegistry.get(sessionGuid);
                RouteDirector routeDirector = null;
                if(routeDirectorGuid != null) {
                    routeDirector = routeDirectorRegistry.get(routeDirectorGuid);
                }
                if(routeDirector == null){
                    routeDirector = getRouteDirector(routeDirectorRegistry);
                }

                HttpSession activeHttpSession = routeDirector.getRouteAttributes().getSessions().get(sessionGuid);
                if(activeHttpSession == null) activeHttpSession = new HttpSession(time, sessionGuid);
                httpRequest.setSession(activeHttpSession);

                Cache cache = new Cache();
                routeDirectorGuid = routeDirector.getGuid();
                if(redirectRegistry.getRegistry().containsKey(routeDirectorGuid) &&
                        redirectRegistry.getRegistry().get(routeDirectorGuid).containsKey(HTTPREQUEST)) {
                    HttpRequest storedHttpRequest = (HttpRequest) redirectRegistry.getRegistry().get(routeDirectorGuid).get(HTTPREQUEST);
                    httpResponse = (HttpResponse) redirectRegistry.getRegistry().get(routeDirectorGuid).get(HTTPRESPONSE);
                    cache = (Cache) redirectRegistry.getRegistry().get(routeDirectorGuid).get(CACHE);
                    activeHttpSession = storedHttpRequest.getSession(true);
                    httpRequest.setSession(activeHttpSession);
                    httpRequest.setVerb("get");
                }

                setSessionAttributesCache(cache, activeHttpSession);

                RouteAttributes routeAttributes = routeDirector.getRouteAttributes();
                httpRequest.setRouteAttributes(routeAttributes);
                RouteResponse routeResponse = routeDirector.direct(cache, httpRequest, httpResponse, redirectRegistry, viewRenderers);

                sessionGuid = httpRequest.getSession(true).getGuid();
                if(!routeDirector.getRouteAttributes().getSessions().containsKey(sessionGuid)){
                    routeDirector.getRouteAttributes().getSessions().put(sessionGuid, activeHttpSession);
                }else{
                    routeDirector.getRouteAttributes().getSessions().replace(sessionGuid, activeHttpSession);
                }

                sessionRouteRegistry.put(sessionGuid, routeDirector.getGuid());
                routeDirectorRegistry.replace(routeDirector.getGuid(), routeDirector);

                StringBuilder sessionValues = new StringBuilder();
                sessionValues.append(serverResources.getSessionId()).append("=").append(httpRequest.getSession(true).getGuid() + ";");
                for(SecurityAttribute securityAttribute : httpResponse.getSecurityAttributes()){
                    sessionValues.append(securityAttribute.getName()).append("=").append(securityAttribute.getValue());
                }

                String redirectLocation = httpResponse.getRedirectLocation();
                if(redirectLocation == null || redirectLocation.equals("")){
                    redirectRegistry.getRegistry().remove(routeDirectorGuid);
                    String content = routeResponse.getContent();
                    StringBuilder response = new StringBuilder();
                    response.append("HTTP/1.1 200 OK" + BREAK);
                    response.append("Content-Length:" + content.getBytes().length + BREAK);
                    response.append("Content-Type:" + httpResponse.getContentType() + BREAK);
                    response.append("Set-Cookie:" + sessionValues + BREAK);
                    response.append(BREAK);
                    response.append(content + BREAK);
                    response.append(DOUBLEBREAK);
                    clientOutput.write(response.toString().getBytes());
                }else{
                    Map<String, Object> redirectAttributes = new HashMap<>();
                    redirectAttributes.put(PLSAR.HTTPREQUEST, httpRequest);
                    redirectAttributes.put(PLSAR.HTTPRESPONSE, httpResponse);
                    redirectAttributes.put(PLSAR.CACHE, cache);
                    redirectRegistry.getRegistry().put(routeDirectorGuid, redirectAttributes);
                    StringBuilder response = new StringBuilder();
                    response.append("HTTP/1.1 307\r\n");
                    response.append("Content-Type:" + httpResponse.getContentType() + BREAK);
                    response.append("Location:" + redirectLocation + SPACE + BREAK);
                    response.append("Content-Length: -1" + BREAK);
                    httpResponse.removeRedirectLocation();
                    clientOutput.write(response.toString().getBytes());
                }


                clientOutput.close();
                socketClient.close();

                executors.execute(new HttpRequestIngester(executors, serverSocket, redirectRegistry, sessionRouteRegistry, routeDirectorRegistry, viewRenderers));

            }catch(IOException ex){
                ex.printStackTrace();
            }catch(InterruptedException ioException){
                ioException.printStackTrace();
            }
        }

        void setSessionAttributesCache(Cache cache, HttpSession httpSession) {
            for(Map.Entry<String, Object> sessionEntry : httpSession.getAttributes().entrySet()){
                cache.set(sessionEntry.getKey(), sessionEntry.getValue());
            }
        }

        void clearRedirectRegistry(String routeDirectorGuid) {
            List<String> redirectKeys = new ArrayList<>();
            if(redirectRegistry.getRegistry().containsKey(routeDirectorGuid)) {
                for (Map.Entry<String, Object> redirectEntry : redirectRegistry.getRegistry().get(routeDirectorGuid).entrySet()) {
                    redirectKeys.add(redirectEntry.getKey());
                }
                for (String redirectKey : redirectKeys) {
                    redirectRegistry.getRegistry().get(routeDirectorGuid).remove(redirectKey);
                }//start doing the right thing and life will be easier.
            }
        }

        RouteDirector getRouteDirector(ConcurrentMap<String, RouteDirector> routeDirectorRegistry){
            for(Map.Entry<String, RouteDirector> routeDirectorEntry : routeDirectorRegistry.entrySet()){
                return routeDirectorEntry.getValue();
            }
            return null;
        }

    }


    public void setSchemaConfig(SchemaConfig schemaConfig) {
        this.schemaConfig = schemaConfig;
    }

    public PLSAR addViewRenderer(ViewRenderer viewRenderer){
        this.viewRenderers.add(viewRenderer);
        return this;
    }

    public PLSAR setPersistenceConfig(PersistenceConfig persistenceConfig) {
        this.persistenceConfig = persistenceConfig;
        return this;
    }

    final static String HTTPREQUEST = "http-request";
    final static String HTTPRESPONSE = "http-response";
    final static String CACHE = "cache";

}