package dev.blueocean;

import dev.blueocean.model.*;
import dev.blueocean.renderers.Renderers;
import dev.blueocean.resources.*;
import dev.blueocean.security.SecurityManager;
import dev.blueocean.security.SecurityAccess;
import lu.ewelohd.jconsoleimage.core.ConsoleImage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class BlueOcean {

    static Logger Log = Logger.getLogger(BlueOcean.class.getName());

    Integer port;
    String RENDERER;

    ViewConfig viewConfig;
    SchemaConfig schemaConfig;
    Integer numberOfPartitions = 2;
    Integer numberOfRequestExecutors = 4;
    PersistenceConfig persistenceConfig;
    Class<?> securityAccessClass;
    List<Class<?>> viewRenderers;


    public BlueOcean(int port){
        this.port = port;
        this.RENDERER = Renderers.PAGE_CACHE;
        this.viewConfig = new ViewConfig();
        this.viewRenderers = new ArrayList<>();
    }

    public void start(){
        try {
            Integer TOTAL_NUMBER_EXECUTORS = numberOfPartitions * numberOfRequestExecutors;

            DatabaseEnvironmentManager databaseEnvironmentManager = new DatabaseEnvironmentManager();
            databaseEnvironmentManager.configure(schemaConfig, persistenceConfig);

            ServerResources serverResources = new ServerResources();
            StartupAnnotationInspector startupAnnotationInspector = new StartupAnnotationInspector(new ComponentsHolder());
            startupAnnotationInspector.inspect();
            ComponentsHolder componentsHolder = startupAnnotationInspector.getComponentsHolder();
            AnnotationComponent routeRegistration = componentsHolder.getRouteRegistration();
            AnnotationComponent serverStartup = componentsHolder.getServerStartup();

            String resourcesDirectory = viewConfig.getResourcesPath();
            ConcurrentMap<String, byte[]> viewBytesMap = serverResources.getViewBytesMap(viewConfig);

            Log.info("Running startup routine, please wait...");
            Method startupMethod = serverStartup.getKlass().getMethod("startup");
            startupMethod.invoke(serverResources.getInstance(serverStartup.getKlass()));

            Log.info("Registering route negotiators, please wait...");
            List<RouteNegotiator> routeNegotiators = getRouteNegotiators(TOTAL_NUMBER_EXECUTORS, serverResources, routeRegistration);
            ConcurrentMap<String, RouteNegotiator> routeDirectorRegistry = registerRouteDirectors(routeNegotiators);

            RedirectRegistry redirectRegistry = new RedirectRegistry();

            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setPerformancePreferences(0, 1, 2);
            ExecutorService executors = Executors.newFixedThreadPool(numberOfPartitions);
            executors.execute(new PartitionedExecutor(RENDERER, numberOfRequestExecutors, resourcesDirectory, viewBytesMap, serverSocket, redirectRegistry, routeDirectorRegistry, viewRenderers));

            Log.info("Ready!");
            
        }catch(IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException | BlueOceanException ex){
            ex.printStackTrace();
        }
    }


    ConcurrentMap<String, RouteNegotiator> registerRouteDirectors(List<RouteNegotiator> routeNegotiators) {
        ConcurrentMap<String, RouteNegotiator> routeDirectorRegistry = new ConcurrentHashMap<>(0, 3, 63010);
        for(RouteNegotiator routeNegotiator : routeNegotiators){
            routeDirectorRegistry.put(routeNegotiator.getGuid(), routeNegotiator);
        }
        return routeDirectorRegistry;
    }

    List<RouteNegotiator> getRouteNegotiators(Integer TOTAL_NUMBER_EXECUTORS, ServerResources serverResources, AnnotationComponent routeRegistration) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<RouteNegotiator> routeNegotiators = new ArrayList();
        for(Integer activeIndex = 0; activeIndex < TOTAL_NUMBER_EXECUTORS; activeIndex++){//todo:set guid

            RouteAttributes routeAttributes = new RouteAttributes();
            if(routeRegistration != null) {
                Method startupMethod = routeRegistration.getKlass().getMethod("register");
                routeAttributes = (RouteAttributes) startupMethod.invoke(serverResources.getInstance(routeRegistration.getKlass()));
            }

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

            Dao dao = new Dao(persistenceConfig);
            SecurityAccess securityAccessInstance = (SecurityAccess) securityAccessClass.getConstructor().newInstance();
            Method setPersistence = securityAccessInstance.getClass().getMethod("setDao", Dao.class);
            setPersistence.invoke(securityAccessInstance, dao);
            SecurityManager securityManager = new SecurityManager(securityAccessInstance);
            routeAttributes.setSecurityManager(securityManager);

            routeAttributes.setSecurityAccess(this.securityAccessClass);

            ComponentAnnotationInspector componentAnnotationInspector = new ComponentAnnotationInspector(new ComponentsHolder());
            componentAnnotationInspector.inspect();
            ComponentsHolder componentsHolder = componentAnnotationInspector.getComponentsHolder();

            RouteNegotiator routeNegotiator = new RouteNegotiator();
            routeNegotiator.setRouteAttributes(routeAttributes);
            routeNegotiator.setComponentsHolder(componentsHolder);

            routeNegotiators.add(routeNegotiator);
        }
        return routeNegotiators;
    }

    public static class PartitionedExecutor implements Runnable{
        String guid;
        String RENDERER;
        String resourcesDirectory;
        Integer numberOfExecutors;
        ServerSocket serverSocket;
        ConcurrentMap<String, RouteNegotiator> routeDirectorRegistry;
        RedirectRegistry redirectRegistry;
        List<Class<?>> viewRenderers;
        ConcurrentMap<String, String> sessionRouteRegistry;
        ConcurrentMap<String, byte[]> viewBytesMap;

        public PartitionedExecutor(String RENDERER, Integer numberOfExecutors, String resourcesDirectory, ConcurrentMap<String, byte[]> viewBytesMap, ServerSocket serverSocket, RedirectRegistry redirectRegistry, ConcurrentMap<String, RouteNegotiator> routeDirectorRegistry, List<Class<?>> viewRenderers) {
            Random random = new Random();
            this.RENDERER = RENDERER;
            this.viewBytesMap = viewBytesMap;
            this.numberOfExecutors = numberOfExecutors;
            this.serverSocket = serverSocket;
            this.redirectRegistry = redirectRegistry;;
            this.routeDirectorRegistry = routeDirectorRegistry;
            this.viewRenderers = viewRenderers;
            this.guid = String.valueOf(random.nextFloat());
            this.resourcesDirectory = resourcesDirectory;
            this.sessionRouteRegistry = new ConcurrentHashMap<>();
        }

        @Override
        public void run() {
            ExecutorService executors = Executors.newFixedThreadPool(numberOfExecutors);
            executors.execute(new NetworkRequestIngester(RENDERER, resourcesDirectory, viewBytesMap, executors, serverSocket, redirectRegistry, sessionRouteRegistry, routeDirectorRegistry, viewRenderers));
         }
    }

    public static class NetworkRequestIngester implements Runnable {

        final String IGNORE_CHROME = "/favicon.ico";
        final String BREAK = "\r\n";
        final String SPACE = " ";
        final String DOUBLEBREAK = "\r\n\r\n";

        final Integer REQUEST_METHOD = 0;
        final Integer REQUEST_PATH = 1;
        final Integer REQUEST_VERSION = 2;

        String RENDERER;

        String resourcesDirectory;
        Socket socketClient;
        ExecutorService executors;
        ServerSocket serverSocket;
        ConcurrentMap<String, String> sessionRouteRegistry;
        ConcurrentMap<String, RouteNegotiator> routeDirectorRegistry;
        RedirectRegistry redirectRegistry;
        List<Class<?>> viewRenderers;
        ConcurrentMap<String, byte[]> viewBytesMap;

        public NetworkRequestIngester(String RENDERER, String resourcesDirectory, ConcurrentMap<String, byte[]> viewBytesMap, ExecutorService executors, ServerSocket serverSocket, RedirectRegistry redirectRegistry, ConcurrentMap<String, String> sessionRouteRegistry, ConcurrentMap<String, RouteNegotiator> routeDirectorRegistry, List<Class<?>> viewRenderers){
            this.RENDERER = RENDERER;
            this.resourcesDirectory = resourcesDirectory;
            this.viewBytesMap = viewBytesMap;
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
                Thread.sleep(19);
                InputStream requestInputStream = socketClient.getInputStream();

                OutputStream clientOutput = socketClient.getOutputStream();

                if(requestInputStream.available() == 0) {
                    requestInputStream.close();
                    clientOutput.flush();
                    clientOutput.close();
                    executors.execute(new NetworkRequestIngester(RENDERER, resourcesDirectory, viewBytesMap, executors, serverSocket, redirectRegistry, sessionRouteRegistry, routeDirectorRegistry, viewRenderers));
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
                String requestVersion = methodPathVersionComponents[REQUEST_VERSION];

                NetworkRequest networkRequest = new NetworkRequest(requestVerb, requestPath, serverResources);
                if(networkRequest.getUriPath().equals(IGNORE_CHROME)){
                    requestInputStream.close();
                    clientOutput.flush();
                    clientOutput.close();
                    executors.execute(new NetworkRequestIngester(RENDERER, resourcesDirectory, viewBytesMap, executors, serverSocket, redirectRegistry, sessionRouteRegistry, routeDirectorRegistry, viewRenderers));
                    return;
                }

                NetworkResponse networkResponse = new NetworkResponse();
                networkResponse.setResponseStream(clientOutput);

                ComponentCompiler requestComponentCompiler = new ComponentCompiler(byteArrayOutputStream.toByteArray(), networkRequest);
                requestComponentCompiler.ingestRequest();

                String[] headerComponents = headerComponent.split(BREAK);
                for(String headerLine : headerComponents){
                    String[] headerLineComponents = headerLine.split(":");
                    if(headerLineComponents.length == 2) {
                        String fieldKey = headerLineComponents[0].trim();
                        String content = headerLineComponents[1].trim();
                        networkRequest.getHeaders().put(fieldKey.toLowerCase(), content);
                    }
                }

                Long time = serverResources.getTime(0);

                String sessionGuid = serverResources.getCookie(networkRequest.getHeaders());
                if(sessionGuid == null) sessionGuid = serverResources.getGuid(24);

                String routeDirectorGuid = sessionRouteRegistry.get(sessionGuid);
                RouteNegotiator routeNegotiator = null;
                if(routeDirectorGuid != null) {
                    routeNegotiator = routeDirectorRegistry.get(routeDirectorGuid);
                }
                if(routeNegotiator == null){
                    routeNegotiator = getRouteDirector(routeDirectorRegistry);
                }

                NetworkSession activeNetworkSession = routeNegotiator.getRouteAttributes().getSessions().get(sessionGuid);
                if(activeNetworkSession == null) activeNetworkSession = new NetworkSession(time, sessionGuid);
                networkRequest.setSession(activeNetworkSession);

                Cache cache = new Cache();
                routeDirectorGuid = routeNegotiator.getGuid();
                if(redirectRegistry.getRegistry().containsKey(routeDirectorGuid) &&
                        redirectRegistry.getRegistry().get(routeDirectorGuid).containsKey(HTTPREQUEST)) {
                    NetworkRequest storedNetworkRequest = (NetworkRequest) redirectRegistry.getRegistry().get(routeDirectorGuid).get(HTTPREQUEST);
                    networkResponse = (NetworkResponse) redirectRegistry.getRegistry().get(routeDirectorGuid).get(HTTPRESPONSE);
                    cache = (Cache) redirectRegistry.getRegistry().get(routeDirectorGuid).get(CACHE);
                    activeNetworkSession = storedNetworkRequest.getSession(true);
                    networkRequest.setSession(activeNetworkSession);
                    networkRequest.setVerb("get");
                }

                setSessionAttributesCache(cache, activeNetworkSession);

                RouteAttributes routeAttributes = routeNegotiator.getRouteAttributes();
                networkRequest.setRouteAttributes(routeAttributes);
                SecurityManager securityManager = routeAttributes.getSecurityManager();
                RouteResponse routeResponse = routeNegotiator.negotiate(RENDERER, resourcesDirectory, cache, networkRequest, networkResponse, securityManager, viewRenderers, viewBytesMap);

                sessionGuid = networkRequest.getSession(true).getGuid();
                if(!routeNegotiator.getRouteAttributes().getSessions().containsKey(sessionGuid)){
                    routeNegotiator.getRouteAttributes().getSessions().put(sessionGuid, activeNetworkSession);
                }else{
                    routeNegotiator.getRouteAttributes().getSessions().replace(sessionGuid, activeNetworkSession);
                }

                sessionRouteRegistry.put(sessionGuid, routeNegotiator.getGuid());
                routeDirectorRegistry.replace(routeNegotiator.getGuid(), routeNegotiator);

                StringBuilder sessionValues = new StringBuilder();
                sessionValues.append(serverResources.getSessionId()).append("=").append(networkRequest.getSession(true).getGuid() + "; path=/;");
                for(SecurityAttribute securityAttribute : networkResponse.getSecurityAttributes()){
                    sessionValues.append(securityAttribute.getName()).append("=").append(securityAttribute.getValue());
                }

                String redirectLocation = networkResponse.getRedirectLocation();
                if(redirectLocation == null || redirectLocation.equals("")){
                    redirectRegistry.getRegistry().remove(routeDirectorGuid);

                    clientOutput.write("HTTP/1.1 ".getBytes());
                    clientOutput.write(routeResponse.getResponseCode().getBytes());
                    clientOutput.write(BREAK.getBytes());

                    Integer bytesLength = routeResponse.getResponseBytes().length;
                    byte[] contentLengthBytes = ("Content-Length:" + bytesLength + BREAK).getBytes();
                    clientOutput.write(contentLengthBytes);

                    clientOutput.write("Content-Type:".getBytes());
                    clientOutput.write(routeResponse.getContentType().getBytes());
                    clientOutput.write(BREAK.getBytes());

                    clientOutput.write("Set-Cookie:".getBytes());
                    clientOutput.write(sessionValues.toString().getBytes());

                    clientOutput.write(DOUBLEBREAK.getBytes());
                    clientOutput.write(routeResponse.getResponseBytes());
                }else{
                    Map<String, Object> redirectAttributes = new HashMap<>();
                    redirectAttributes.put(BlueOcean.HTTPREQUEST, networkRequest);
                    redirectAttributes.put(BlueOcean.HTTPRESPONSE, networkResponse);
                    redirectAttributes.put(BlueOcean.CACHE, cache);
                    redirectRegistry.getRegistry().put(routeDirectorGuid, redirectAttributes);
                    StringBuilder response = new StringBuilder();
                    response.append("HTTP/1.1 307\r\n");
                    response.append("Content-Type:text/html" + BREAK);
                    response.append("Location:" + redirectLocation + SPACE + BREAK);
                    response.append("Content-Length: -1" + BREAK);
                    networkResponse.removeRedirectLocation();
                    clientOutput.write(response.toString().getBytes());
                }


                clientOutput.close();
                socketClient.close();

                executors.execute(new NetworkRequestIngester(RENDERER, resourcesDirectory, viewBytesMap, executors, serverSocket, redirectRegistry, sessionRouteRegistry, routeDirectorRegistry, viewRenderers));

            }catch(IOException ex){
                ex.printStackTrace();
            }catch(InterruptedException ioException){
                ioException.printStackTrace();
            }
        }

        void setSessionAttributesCache(Cache cache, NetworkSession networkSession) {
            for(Map.Entry<String, Object> sessionEntry : networkSession.getAttributes().entrySet()){
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

        RouteNegotiator getRouteDirector(ConcurrentMap<String, RouteNegotiator> routeDirectorRegistry){
            for(Map.Entry<String, RouteNegotiator> routeDirectorEntry : routeDirectorRegistry.entrySet()){
                return routeDirectorEntry.getValue();
            }
            return null;
        }

    }


    public void setRenderer(String renderer) {
        this.RENDERER = renderer;
    }

    public void setViewConfig(ViewConfig viewConfig) {
        this.viewConfig = viewConfig;
    }

    public void setSecurityAccess(Class<?> securityAccess) {
        this.securityAccessClass = securityAccess;
    }

    public void setSchemaConfig(SchemaConfig schemaConfig) {
        this.schemaConfig = schemaConfig;
    }

    public void setNumberOfPartitions(int numberOfPartitions){
        this.numberOfPartitions = numberOfPartitions;
    }

    public void setNumberOfRequestExecutors(int numberOfRequestExecutors){
        this.numberOfRequestExecutors = numberOfRequestExecutors;
    }

    public BlueOcean addViewRenderer(Class<?> viewRenderer){
        this.viewRenderers.add(viewRenderer);
        return this;
    }

    public BlueOcean setPersistenceConfig(PersistenceConfig persistenceConfig) {
        this.persistenceConfig = persistenceConfig;
        return this;
    }

    final static String HTTPREQUEST = "http-request";
    final static String HTTPRESPONSE = "http-response";
    final static String CACHE = "cache";

}