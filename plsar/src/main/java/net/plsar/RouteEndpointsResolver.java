package net.plsar;

import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Component;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Delete;
import net.plsar.annotations.http.Post;
import net.plsar.model.TypeAttributes;
import net.plsar.model.UrlBit;
import net.plsar.model.UrlBitFeatures;
import net.plsar.resources.ServerResources;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RouteEndpointsResolver {
    ServerResources serverResources;
    ClassLoader klassLoader;
    RouteEndpointHolder routeEndpointHolder;

    public RouteEndpointsResolver(ServerResources serverResources){
        this.serverResources = serverResources;
        this.routeEndpointHolder = new RouteEndpointHolder();
        klassLoader = Thread.currentThread().getContextClassLoader();
    }

    public RouteEndpointHolder resolve() {
        Path filePath = Paths.get("build");//requires build directory
        String completeFilePath = filePath.toAbsolutePath().toString();
        inspectFilePath(completeFilePath);
        return routeEndpointHolder;
    }

    public void inspectFilePath(String filePath){
        File pathFile = new File(filePath);

        File[] files = pathFile.listFiles();
        for (File file : files) {

            if (file.isDirectory()) {
                inspectFilePath(file.getPath());
                continue;
            }

            try {

                if(!file.getPath().endsWith(".class"))continue;

                String separator = System.getProperty("file.separator");
                String regex = "classes" + "\\" + separator;
                String[] klassPathParts = file.getPath().split(regex);
                String klassPathSlashesRemoved =  klassPathParts[1].replace("\\", ".");
                String klassPathPeriod = klassPathSlashesRemoved.replace("/",".");
                String klassPathBefore = klassPathPeriod.replace("."+ "class", "");

                String klassPath = klassPathBefore.replaceFirst("java.", "").replaceFirst("main.", "");
                Class<?> klass = klassLoader.loadClass(klassPath);

                if (klass.isAnnotation() || klass.isInterface()) continue;

                if(klass.isAnnotationPresent(HttpRouter.class)) {
                    Method[] routeMethods = klass.getDeclaredMethods();
                    for(Method routeMethod: routeMethods){

                        if(routeMethod.isAnnotationPresent(Get.class)){
                            Get annotation = routeMethod.getAnnotation(Get.class);
                            String routePath = annotation.value();
                            RouteEndpoint routeEndpoint = getCompleteRouteEndpoint("get", routePath, routeMethod, klass);
                            String routeKey = routeEndpoint.getRouteVerb().toLowerCase() + routeEndpoint.getRoutePath().toLowerCase();
                            routeEndpointHolder.getRouteEndpoints().put(routeKey, routeEndpoint);
                        }
                        if(routeMethod.isAnnotationPresent(Post.class)){
                            Post annotation = routeMethod.getAnnotation(Post.class);
                            String routePath = annotation.value();
                            RouteEndpoint routeEndpoint = getCompleteRouteEndpoint("post", routePath, routeMethod, klass);
                            String routeKey = routeEndpoint.getRouteVerb() + routeEndpoint.getRoutePath();
                            routeEndpointHolder.getRouteEndpoints().put(routeKey, routeEndpoint);
                        }
                        if(routeMethod.isAnnotationPresent(Delete.class)){
                            Delete annotation = routeMethod.getAnnotation(Delete.class);
                            String routePath = annotation.value();
                            RouteEndpoint routeEndpoint = getCompleteRouteEndpoint("delete", routePath, routeMethod, klass);
                            String routeKey = routeEndpoint.getRouteVerb() + routeEndpoint.getRoutePath();
                            routeEndpointHolder.getRouteEndpoints().put(routeKey, routeEndpoint);
                        }
                    }
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    RouteEndpoint getCompleteRouteEndpoint(String routeVerb, String routePath, Method routeMethod, Class<?> klass) throws Exception {
        RouteEndpoint routeEndpoint = new RouteEndpoint();
        routeEndpoint.setRouteVerb(routeVerb);
        routeEndpoint.setRouteMethod(routeMethod);
        routeEndpoint.setKlass(klass);


        Type[] types = routeMethod.getGenericParameterTypes();
        for(Type type : types)routeEndpoint.getTypeNames().add(type.getTypeName());

        Annotation[][] paramAnnotations = routeMethod.getParameterAnnotations();
        Class<?>[] paramTypes = routeMethod.getParameterTypes();
        for (int foo = 0; foo < paramAnnotations.length; foo++) {
            for (Annotation annotation : paramAnnotations[foo]) {
                if (annotation instanceof Component) {
                    TypeAttributes typeAttributes = new TypeAttributes();
                    typeAttributes.setQualifiedName(paramTypes[foo].getTypeName());
                    typeAttributes.setTypeKlass(paramTypes[foo].getTypeName());
                    routeEndpoint.getTypeDetails().add(typeAttributes);
                }
            }
        }

        StringBuilder regexRoutePath = new StringBuilder();
        regexRoutePath.append("\\/(");
        int count = 0;
        String[] parts = routePath.split("/");
        for(String part: parts){
            count++;
            if(!part.equals("")) {
                if (part.matches("(\\{[a-zA-Z]*\\})")) {
                    regexRoutePath.append("(.*[A-Za-z0-9])");
                    routeEndpoint.getVariablePositions().add(count - 1);
                } else {
                    regexRoutePath.append("(" + part.toLowerCase() + "){1}");
                }
                if (count < parts.length) {
                    regexRoutePath.append("\\/");
                }
            }
        }
        regexRoutePath.append(")$");

        if(routeEndpointHolder.getRouteEndpoints().containsKey(regexRoutePath)){
            throw new Exception("request path + " + routePath + " exists multiple times.");//todo:
        }

        routeEndpoint.setRegexRoutePath(regexRoutePath.toString());
        routeEndpoint.setRoutePath(routePath);

        String[] bits = routePath.split("/");
        UrlBitFeatures urlBitFeatures = new UrlBitFeatures();
        List<UrlBit> urlBits = new ArrayList<>();
        for(String bit : bits){
            UrlBit urlBit = new UrlBit();
            if(bit.contains("{") && bit.contains("}")){
                urlBit.setVariable(true);
            }else{
                urlBit.setVariable(false);
            }
            urlBits.add(urlBit);
        }
        urlBitFeatures.setUrlBits(urlBits);
        routeEndpoint.setUrlBitFeatures(urlBitFeatures);
        return routeEndpoint;
    }

}
