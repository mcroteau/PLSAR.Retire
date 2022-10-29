package net.plsar;

import net.plsar.annotations.Controller;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Delete;
import net.plsar.annotations.http.Post;
import net.plsar.model.RouteAttribute;
import net.plsar.resources.ServerResources;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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

                if(klass.isAnnotationPresent(Controller.class)) {
                    Method[] routeMethods = klass.getDeclaredMethods();
                    for(Method routeMethod: routeMethods){

                        if(routeMethod.isAnnotationPresent(Get.class)){
                            Get annotation = routeMethod.getAnnotation(Get.class);
                            String routePath = annotation.value();
                            RouteEndpoint routeEndpoint = getCompleteRouteEndpoint("get", routePath, routeMethod, klass);
                            String routeKey = routeEndpoint.getRouteVerb() + ":" + routeEndpoint.getRoutePath().toLowerCase();
                            routeEndpointHolder.getRouteEndpoints().put(routeKey, routeEndpoint);
                        }
                        if(routeMethod.isAnnotationPresent(Post.class)){
                            Post annotation = routeMethod.getAnnotation(Post.class);
                            String routePath = annotation.value();
                            RouteEndpoint routeEndpoint = getCompleteRouteEndpoint("post", routePath, routeMethod, klass);
                            String routeKey = routeEndpoint.getRouteVerb() + ":" + routeEndpoint.getRoutePath();
                            routeEndpointHolder.getRouteEndpoints().put(routeKey, routeEndpoint);
                        }
                        if(routeMethod.isAnnotationPresent(Delete.class)){
                            Delete annotation = routeMethod.getAnnotation(Delete.class);
                            String routePath = annotation.value();
                            RouteEndpoint routeEndpoint = getCompleteRouteEndpoint("delete", routePath, routeMethod, klass);
                            String routeKey = routeEndpoint.getRouteVerb() + ":" + routeEndpoint.getRoutePath();
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
        routeEndpoint.setRoutePath(routePath.toLowerCase());
        routeEndpoint.setRouteMethod(routeMethod);
        routeEndpoint.setKlass(klass);

        String routeRegex = new String();
        String[] routeParts = routePath.split("/");
        for(String routePart : routeParts){
            if(routePart.equals(""))continue;
            routeRegex += "/";
            if(routePart.contains("{") && routePart.contains("}")){
                routeRegex += "[a-zA-Z0-9]*";
            }else{
                routeRegex += routePart;
            }
        }

        routeEndpoint.setRegexRoutePath(routeRegex);

        if(routeEndpoint.getRegexRoutePath().contains("["))routeEndpoint.setRegex(true);
        if(!routeEndpoint.getRegexRoutePath().contains("["))routeEndpoint.setRegex(false);

        Type[] parameterAttributes = routeMethod.getGenericParameterTypes();

        int index = 0;
        for(Type parameterAttribute : parameterAttributes){
            RouteAttribute routeAttribute = new RouteAttribute();
            routeAttribute.setTypeKlass(parameterAttribute.getTypeName());
            if(!parameterAttribute.getTypeName().startsWith("net.plsar")){
                routeEndpoint.setRegex(true);
                routeAttribute.setRouteVariable(true);
            }else{
                routeAttribute.setRouteVariable(false);
            }
            routeEndpoint.getRouteAttributes().put(index, routeAttribute);index++;
        }

        Map<Integer, Boolean> processed = new HashMap<>();
        for(Map.Entry<Integer, RouteAttribute> routeAttributeEntry : routeEndpoint.getRouteAttributes().entrySet()){
            int pathIndex = 0;
            for(String routePart : routeParts){
                if(routePart.equals(""))continue;
                RouteAttribute routeAttribute = routeAttributeEntry.getValue();
                if(routePart.contains("{") && routePart.contains("}") && routeAttribute.getRouteVariable()){
                    if(!processed.containsKey(routeAttribute.getRoutePosition())){
                        processed.put(routeAttribute.getRoutePosition(), true);
                        routeAttribute.setRoutePosition(pathIndex);
                    }
                }
                pathIndex++;
            }
        }

        return routeEndpoint;
    }

}
