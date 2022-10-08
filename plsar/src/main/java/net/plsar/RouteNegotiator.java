package net.plsar;

import net.plsar.annotations.Inject;
import net.plsar.annotations.Metadata;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.model.HttpResponse;
import net.plsar.resources.ComponentsHolder;
import net.plsar.security.SecurityManager;
import net.plsar.resources.ServerResources;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RouteNegotiator {

    String guid;//love.
    RouteAttributes routeAttributes;
    ComponentsHolder componentsHolder;

    public RouteResponse negotiate(Cache cache, HttpRequest httpRequest, HttpResponse httpResponse, SecurityManager securityManager, List<Class<?>> viewRenderers){

        try {
            ServerResources serverResources = new ServerResources();
            UserExperienceManager userExperienceManager = new UserExperienceManager();

            RouteAttributes routeAttributes = httpRequest.getRouteAttributes();
            RouteEndpointHolder routeEndpointHolder = routeAttributes.getRouteEndpointHolder();

            String routeUriPath = httpRequest.getUriPath();
            String routeVerb = httpRequest.getVerb();

            RouteEndpoint routeEndpoint = serverResources.getRouteEndpoint(routeVerb, routeUriPath, routeEndpointHolder);
            Object[] signature = serverResources.getRouteParameters(routeUriPath, routeEndpoint, cache, httpRequest, httpResponse, securityManager);
            Method routeMethod = routeEndpoint.getRouteMethod();

            String design = null, title = null, keywords = null, description = null;
            if(routeMethod.isAnnotationPresent(Metadata.class)){
                Metadata metadataAnnotation = routeMethod.getAnnotation(Metadata.class);
                title = metadataAnnotation.title();
                keywords = metadataAnnotation.keywords();
                description = metadataAnnotation.description();
            }

            routeMethod.setAccessible(true);
            Object routeInstance = routeEndpoint.getKlass().getConstructor().newInstance();

            PersistenceConfig persistenceConfig = routeAttributes.getPersistenceConfig();

            Dao routeDao = new Dao(persistenceConfig);

            Field[] routeFields = routeInstance.getClass().getDeclaredFields();
            for(Field routeField : routeFields){
                if(routeField.isAnnotationPresent(Inject.class)){
                    String fieldKey = routeField.getName().toLowerCase();
                    Class<?> componentKlass = componentsHolder.getComponents().get(fieldKey);
                    Constructor<?> componentKlassConstructor = componentKlass.getConstructor(Dao.class);
                    Object componentInstance = componentKlassConstructor.newInstance(routeDao);
                    routeField.setAccessible(true);
                    routeField.set(routeInstance, componentInstance);
                }
            }

            try {
                Method setPersistenceMethod = routeInstance.getClass().getMethod("setPersistence", Dao.class);
                setPersistenceMethod.invoke(routeInstance, new Dao(persistenceConfig));
            }catch(NoSuchMethodException nsme){ }

            String methodResponse = String.valueOf(routeMethod.invoke(routeInstance, signature));
            if(methodResponse == null){
                return new RouteResponse("404");
            }

            if(methodResponse.startsWith("[redirect]")) {
                String redirectRouteUri = serverResources.getRedirect(methodResponse);
                httpResponse.setRedirectLocation(redirectRouteUri);
                return new RouteResponse("redirect:");
            }

            Path webPath = Paths.get("src", "webapp");
            if(methodResponse.startsWith("/")){
                methodResponse = methodResponse.replaceFirst("/", "");
            }

            String htmlPath = webPath.toFile().getAbsolutePath().concat(File.separator + methodResponse);
            File viewFile = new File(htmlPath);
            ByteArrayOutputStream unebaos = new ByteArrayOutputStream();
            String pageContent;

            InputStream pageInput = new FileInputStream(viewFile);
            byte[] bytes = new byte[1024 * 13];
            int unelength;
            while ((unelength = pageInput.read(bytes)) != -1) {
                unebaos.write(bytes, 0, unelength);
            }
            pageContent = unebaos.toString(StandardCharsets.UTF_8.name());//todo? ugly


            if(design != null) {
                String designPath = "/webapp/" + design;
                InputStream designInput = this.getClass().getResourceAsStream(designPath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                String designContent;
                bytes = new byte[1024 * 13];
                int length;
                while ((length = designInput.read(bytes)) != -1) {
                    baos.write(bytes, 0, length);
                }
                designContent = baos.toString(StandardCharsets.UTF_8.name());

                if(designContent == null){
                    return new RouteResponse("501");
                }

                if(!designContent.contains("<plsar:content/>")){
                    return new RouteResponse("Your html template file is missing <plsar:content/>");
                }

                String[] bits = designContent.split("<plsar:content/>");
                String header = bits[0];
                String bottom = "";
                if(bits.length > 1) bottom = bits[1];

                header = header + pageContent;
                String completePage = header + bottom;
                completePage = completePage.replace("${title}", title);

                if(keywords != null) {
                    completePage = completePage.replace("${keywords}", keywords);
                }
                if(description != null){
                    completePage = completePage.replace("${description}", description);
                }

                String designOutput = userExperienceManager.execute(completePage, cache, httpRequest, viewRenderers);
                return new RouteResponse(designOutput);

            }else{
                String pageOutput = userExperienceManager.execute(pageContent, cache, httpRequest, viewRenderers);
                return new RouteResponse(pageOutput);
            }

        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (PlsarException e) {
            e.printStackTrace();
        }

        return new RouteResponse("404");
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
