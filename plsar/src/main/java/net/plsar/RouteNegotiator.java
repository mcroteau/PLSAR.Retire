package net.plsar;

import net.plsar.annotations.*;
import net.plsar.model.PageCache;
import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;
import net.plsar.model.RouteAttribute;
import net.plsar.schemes.RenderingScheme;
import net.plsar.resources.ComponentsHolder;
import net.plsar.resources.MimeResolver;
import net.plsar.security.SecurityManager;
import net.plsar.resources.ServerResources;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteNegotiator {

    String guid;//love.
    RouteAttributes routeAttributes;
    ComponentsHolder componentsHolder;

    public RouteResponse negotiate(String RENDERER, String resourcesDirectory, PageCache pageCache, NetworkRequest networkRequest, NetworkResponse networkResponse, SecurityManager securityManager, List<Class<?>> viewRenderers, ConcurrentMap<String, byte[]> viewBytesMap){

        String completePageRendered = "";
        String errorMessage = "";

        try {
            ServerResources serverResources = new ServerResources();
            ExperienceManager experienceManager = new ExperienceManager();

            RouteAttributes routeAttributes = networkRequest.getRouteAttributes();
            RouteEndpointHolder routeEndpointHolder = routeAttributes.getRouteEndpointHolder();

            String routeUriPath = networkRequest.getUriPath();
            String routeVerb = networkRequest.getVerb();

            if(routeUriPath.startsWith("/" + resourcesDirectory + "/")) {

                MimeResolver mimeGetter = new MimeResolver(routeUriPath);

                if (RENDERER.equals(RenderingScheme.CACHE_REQUESTS)) {

                    ByteArrayOutputStream outputStream = serverResources.getViewFileCopy(routeUriPath, viewBytesMap);
                    if (outputStream == null) {
                        return new RouteResponse("404".getBytes(), "404", "text/html");
                    }
                    return new RouteResponse(outputStream.toByteArray(), "200 OK", mimeGetter.resolve());

                }else{

                    String assetsPath = Paths.get("src", "main", "webapp").toString();
                    String filePath = assetsPath.concat(routeUriPath);
                    File staticResourcefile = new File(filePath);
                    InputStream fileInputStream = new FileInputStream(staticResourcefile);

                    if (fileInputStream != null && routeVerb.equals("get")) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] bytes = new byte[(int) staticResourcefile.length()];
                        int bytesRead;
                        try {
                            while ((bytesRead = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
                                outputStream.write(bytes, 0, bytesRead);
                            }
                            fileInputStream.close();
                            outputStream.flush();
                            outputStream.close();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        return new RouteResponse(outputStream.toByteArray(), "200 OK", mimeGetter.resolve());
                    }
                }

            }

            RouteEndpoint routeEndpoint = null;
            routeUriPath = routeUriPath.toLowerCase().trim();

            if(routeUriPath.equals("")){
                routeUriPath = "/";
                String routeKey = routeVerb.toLowerCase() + routeUriPath.toLowerCase();
                routeEndpoint = routeEndpointHolder.getRouteEndpoints().get(routeKey);
            }

            if(routeEndpoint == null) {
                if (routeUriPath.length() > 1 && routeUriPath.endsWith("/")) {
                    int endIndex = routeUriPath.indexOf("/", routeUriPath.length() - 1);
                    routeUriPath = routeUriPath.substring(0, endIndex);
                }

                if (routeEndpointHolder.getRouteEndpoints().containsKey(routeVerb + ":" + routeUriPath)) {
                    routeEndpoint = routeEndpointHolder.getRouteEndpoints().get(routeVerb + ":" + routeUriPath);
                }
            }

            if(routeEndpoint == null) {
                for (Map.Entry<String, RouteEndpoint> routeEndpointEntry : routeEndpointHolder.getRouteEndpoints().entrySet()) {
                    RouteEndpoint activeRouteEndpoint = routeEndpointEntry.getValue();
                    Matcher routeEndpointMatcher = Pattern.compile(activeRouteEndpoint.getRegexRoutePath()).matcher(routeUriPath);
                    if (routeEndpointMatcher.matches() &&
                            getRouteVariablesMatch(routeUriPath, activeRouteEndpoint) &&
                            activeRouteEndpoint.isRegex()) {
                        routeEndpoint = activeRouteEndpoint;
                    }
                }
            }

            if(routeEndpoint == null){
                return new RouteResponse("404".getBytes(), "404", "text/html");
            }

            List<Object> routeMethodAttributes = getRouteMethodAttributes(routeUriPath, pageCache, networkRequest, networkResponse, securityManager, routeEndpoint);
            Method routeMethod = routeEndpoint.getRouteMethod();

            String title = null, keywords = null, description = null;
            if(routeMethod.isAnnotationPresent(Meta.class)){
                Meta metaAnnotation = routeMethod.getAnnotation(Meta.class);
                title = metaAnnotation.title();
                keywords = metaAnnotation.keywords();
                description = metaAnnotation.description();
            }

            routeMethod.setAccessible(true);
            Object routeInstance = routeEndpoint.getKlass().getConstructor().newInstance();

            PersistenceConfig persistenceConfig = routeAttributes.getPersistenceConfig();

            Dao routeDao = new Dao(persistenceConfig);

            Field[] routeFields = routeInstance.getClass().getDeclaredFields();
            for(Field routeField : routeFields){
                if(routeField.isAnnotationPresent(Bind.class)){
                    String fieldKey = routeField.getName().toLowerCase();

                    if(componentsHolder.getServices().containsKey(fieldKey)){
                        Class<?> serviceKlass = componentsHolder.getServices().get(fieldKey);
                        Constructor<?> serviceKlassConstructor = serviceKlass.getConstructor();
                        Object serviceInstance = serviceKlassConstructor.newInstance();

                        Field[] repoFields = serviceInstance.getClass().getDeclaredFields();
                        for(Field repoField : repoFields) {
                            if (repoField.isAnnotationPresent(Bind.class)) {
                                String repoFieldKey = repoField.getName().toLowerCase();

                                if(componentsHolder.getRepositories().containsKey(repoFieldKey)){
                                    Class<?> repositoryKlass = componentsHolder.getRepositories().get(repoFieldKey);
                                    Constructor<?> repositoryKlassConstructor = repositoryKlass.getConstructor(Dao.class);
                                    Object repositoryInstance = repositoryKlassConstructor.newInstance(routeDao);
                                    repoField.setAccessible(true);
                                    repoField.set(serviceInstance, repositoryInstance);
                                }
                            }
                        }

                        routeField.setAccessible(true);
                        routeField.set(routeInstance, serviceInstance);
                    }

                    if(componentsHolder.getRepositories().containsKey(fieldKey)){
                        Class<?> componentKlass = componentsHolder.getRepositories().get(fieldKey);
                        Constructor<?> componentKlassConstructor = componentKlass.getConstructor(Dao.class);
                        Object componentInstance = componentKlassConstructor.newInstance(routeDao);
                        routeField.setAccessible(true);
                        routeField.set(routeInstance, componentInstance);
                    }
                }
            }

            try {
                Method setPersistenceMethod = routeInstance.getClass().getMethod("setDao", Dao.class);
                setPersistenceMethod.invoke(routeInstance, new Dao(persistenceConfig));
            }catch(NoSuchMethodException nsme){ }

            String methodResponse = String.valueOf(routeMethod.invoke(routeInstance, routeMethodAttributes.toArray()));
            if(methodResponse == null){
                return new RouteResponse("404".getBytes(), "404", "text/html");
            }

            if(methodResponse.startsWith("redirect:")) {
                String redirectRouteUri = serverResources.getRedirect(methodResponse);
                networkResponse.setRedirectLocation(redirectRouteUri);
                return new RouteResponse("307".getBytes(), "307", "text/html");
            }


            if(routeMethod.isAnnotationPresent(JsonOutput.class)){
                return new RouteResponse(methodResponse.getBytes(), "200 OK", "application/json");
            }

            if(routeMethod.isAnnotationPresent(Text.class)){
                return new RouteResponse(methodResponse.getBytes(), "200 OK", "text/html");
            }

            if(RENDERER.equals(RenderingScheme.CACHE_REQUESTS)) {

                ByteArrayOutputStream unebaos = serverResources.getViewFileCopy(methodResponse, viewBytesMap);
                if(unebaos == null){
                    return new RouteResponse("404".getBytes(), "404", "text/html");
                }
                completePageRendered = unebaos.toString(StandardCharsets.UTF_8.name());

            }else{

                Path webPath = Paths.get("src", "main", "webapp");
                if (methodResponse.startsWith("/")) {
                    methodResponse = methodResponse.replaceFirst("/", "");
                }

                String htmlPath = webPath.toFile().getAbsolutePath().concat(File.separator + methodResponse);
                File viewFile = new File(htmlPath);
                ByteArrayOutputStream unebaos = new ByteArrayOutputStream();


                InputStream pageInput = new FileInputStream(viewFile);
                byte[] bytes = new byte[(int) viewFile.length()];
                int pageBytesLength;
                while ((pageBytesLength = pageInput.read(bytes)) != -1) {
                    unebaos.write(bytes, 0, pageBytesLength);
                }
                completePageRendered = unebaos.toString(StandardCharsets.UTF_8.name());//todo? ugly
            }





            String designUri = null;
            if(routeMethod.isAnnotationPresent(Design.class)){
                Design annotation = routeMethod.getAnnotation(Design.class);
                designUri = annotation.value();
            }

            if(designUri != null) {
                String designContent;
                if(RENDERER.equals(RenderingScheme.CACHE_REQUESTS)) {

                    ByteArrayOutputStream baos = serverResources.getViewFileCopy(designUri, viewBytesMap);
                    designContent = baos.toString(StandardCharsets.UTF_8.name());

                }else{

                    Path designPath = Paths.get("src", "main", "webapp", designUri);
                    File designFile = new File(designPath.toString());
                    InputStream designInput = new FileInputStream(designFile);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] bytes = new byte[(int) designFile.length()];
                    int length;
                    while ((length = designInput.read(bytes)) != -1) {
                        baos.write(bytes, 0, length);
                    }
                    designContent = baos.toString(StandardCharsets.UTF_8.name());

                }

                if(designContent == null){
                    return new RouteResponse("design not found.".getBytes(), "200 OK", "text/html");
                }

                if(!designContent.contains("<ocean:content/>")){
                    return new RouteResponse("Your html template file is missing <ocean:content/>".getBytes(), "200 OK", "text/html");
                }

                String[] bits = designContent.split("<ocean:content/>");
                String header = bits[0];
                String bottom = "";
                if(bits.length > 1) bottom = bits[1];

                header = header + completePageRendered;
                completePageRendered = header + bottom;

                if(title != null) {
                    completePageRendered = completePageRendered.replace("${title}", title);
                }
                if(keywords != null) {
                    completePageRendered = completePageRendered.replace("${keywords}", keywords);
                }
                if(description != null){
                    completePageRendered = completePageRendered.replace("${description}", description);
                }

                completePageRendered = experienceManager.execute(completePageRendered, pageCache, networkRequest, viewRenderers);
                return new RouteResponse(completePageRendered.getBytes(), "200 OK", "text/html");

            }else{
                completePageRendered = experienceManager.execute(completePageRendered, pageCache, networkRequest, viewRenderers);
                return new RouteResponse(completePageRendered.getBytes(), "200 OK", "text/html");
            }

        }catch (IllegalAccessException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (NoSuchFieldException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (IOException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (PlsarException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        }
        String erroredPageRendered = errorMessage + completePageRendered;
        return new RouteResponse(erroredPageRendered.getBytes(), "404", "text/html");
    }


    List<Object> getRouteMethodAttributes(String routeUriPath, PageCache pageCache, NetworkRequest networkRequest, NetworkResponse networkResponse, SecurityManager securityManager, RouteEndpoint routeEndpoint) {
        List<Object> routeMethodAttributes = new ArrayList<>();
        Type[] methodAttributes = routeEndpoint.getRouteMethod().getParameterTypes();
        String routeUriPathClean = routeUriPath.replaceFirst("/", "");
        String[] routePathUriAttributes = routeUriPathClean.split("/");
        Integer PATH_VARIABLE_INDEX = 0;
        for(int foo = 0; foo < methodAttributes.length; foo++){
            Type methodAttribute = methodAttributes[foo];
            RouteAttribute routeAttribute = routeEndpoint.getRouteAttributes().get(foo);//todo:do we do anything?
            PATH_VARIABLE_INDEX = routeAttribute.getRoutePosition() != null ? routeAttribute.getRoutePosition() : 0;
            if(methodAttribute.getTypeName().equals("net.plsar.security.SecurityManager")){
                routeMethodAttributes.add(securityManager);
            }
            if(methodAttribute.getTypeName().equals("net.plsar.model.NetworkRequest")){
                routeMethodAttributes.add(networkRequest);
            }
            if(methodAttribute.getTypeName().equals("net.plsar.model.NetworkResponse")){
                routeMethodAttributes.add(networkResponse);
            }
            if(methodAttribute.getTypeName().equals("net.plsar.model.PageCache")){
                routeMethodAttributes.add(pageCache);
            }
            if(methodAttribute.getTypeName().equals("java.lang.Integer")){
                routeMethodAttributes.add(Integer.valueOf(routePathUriAttributes[PATH_VARIABLE_INDEX]));
            }
            if(methodAttribute.getTypeName().equals("java.lang.Long")){
                routeMethodAttributes.add(Long.valueOf(routePathUriAttributes[PATH_VARIABLE_INDEX]));
            }
            if(methodAttribute.getTypeName().equals("java.lang.String")){
                routeMethodAttributes.add(String.valueOf(routePathUriAttributes[PATH_VARIABLE_INDEX]));
            }
        }
        return routeMethodAttributes;
    }

    boolean getRouteVariablesMatch(String routeUriPath, RouteEndpoint routeEndpoint) {
        String[] routeUriParts = routeUriPath.split("/");
        String[] routeEndpointParts = routeEndpoint.getRoutePath().split("/");
        if(routeUriParts.length != routeEndpointParts.length)return false;
        return true;
    }

    public RouteNegotiator(){
        this.guid = new ServerResources().getGuid(24);
    }

    public String getGuid() {
        return guid;
    }

    public RouteAttributes getRouteAttributes() {
        return routeAttributes;
    }

    public void setRouteAttributes(RouteAttributes routeAttributes) {
        this.routeAttributes = routeAttributes;
    }

    public ComponentsHolder getComponentsHolder() {
        return componentsHolder;
    }

    public void setComponentsHolder(ComponentsHolder componentsHolder) {
        this.componentsHolder = componentsHolder;
    }
}
