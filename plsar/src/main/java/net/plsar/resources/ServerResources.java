package net.plsar.resources;

import net.plsar.model.*;
import net.plsar.RouteEndpoint;
import net.plsar.RouteEndpointHolder;
import net.plsar.model.Cache;
import net.plsar.security.SecurityManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerResources {

    public Object getInstance(Class<?> klass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object object = klass.getConstructor().newInstance();
        return object;
    }

    public String getGuid(int n) {
        String CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
        StringBuilder uuid = new StringBuilder();
        int divisor = n/6;
        Random rnd = new Random();
        for(int z = 0; z < n;  z++) {
            if( z % divisor == 0 && z > 0) {
                uuid.append("-");
            }
            int index = (int) (rnd.nextFloat() * CHARS.length());
            uuid.append(CHARS.charAt(index));
        }
        return uuid.toString();
    }

    public Long getTime(int days){
        LocalDateTime ldt = LocalDateTime.now().minusDays(days);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(this.getDateFormat());
        String date = dtf.format(ldt);
        return Long.valueOf(date);
    }

    public String getCookie(Map<String, String> headers){
        String value = null;
        String sessionId = getSessionId();
        String cookies = headers.get("cookie");
        if(cookies != null) {
            String[] bits = cookies.split(";");
            for (String completes : bits) {
                String[] parts = completes.split("=");
                String key = parts[0].trim();
                if (parts.length == 2) {
                    System.out.println("z" + key + ":" + sessionId);
                    if (key.equals(sessionId)) {
                        value = parts[1].trim();
                    }
                }
            }
        }
        return value;
    }

    public String getSessionId() {
        return "gigante.sessions";
    }

    public String getDateFormat() {
        return "yyyyMMddHHmmssSSS";
    }

    private String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

    public RouteEndpoint getRouteEndpoint(String routeVerb, String routeUri, RouteEndpointHolder routeEndpointHolder){
        String[] routeBreaks = routeUri.split("/");
        if(routeUri.equals("")){
            routeUri = "/";
            String routeKey = routeVerb.toLowerCase() + routeUri.toLowerCase();
            return routeEndpointHolder.getRouteEndpoints().get(routeKey);
        }

        if(routeBreaks.length > 1 && routeUri.endsWith("/")){
            int endIndex = routeUri.indexOf("/", routeUri.length() - 1);
            routeUri = routeUri.substring(0, endIndex);
        }
        for (Map.Entry<String, RouteEndpoint> routeEntry : routeEndpointHolder.getRouteEndpoints().entrySet()) {
            RouteEndpoint routeEndpoint = routeEntry.getValue();
            Matcher routeEndpointMatcher = Pattern.compile(routeEndpoint.getRegexRoutePath()).matcher(routeUri);
            if(routeEndpointMatcher.matches() &&
                    routeBreaksMatchUp(routeUri, routeEndpoint) &&
                    routeVariablesMatchUp(routeUri, routeEndpoint) &&
                    routeVerb.toLowerCase().equals(routeEndpoint.getRouteVerb().toLowerCase())){
                return routeEndpoint;
            }
        }
        return null;
    }

    protected boolean routeBreaksMatchUp(String routeUri, RouteEndpoint routeEndpoint){
        String[] uriBits = routeUri.split("/");
        String[] mappingBits = routeEndpoint.getRoutePath().split("/");
        return uriBits.length == mappingBits.length;
    }

    protected boolean routeVariablesMatchUp(String routeUri, RouteEndpoint routeEndpoint){
        List<String> bits = Arrays.asList(routeUri.split("/"));

        UrlBitFeatures urlBitFeatures = routeEndpoint.getUrlBitFeatures();
        List<UrlBit> urlBits = urlBitFeatures.getUrlBits();

        Class[] typeParameters = routeEndpoint.getRouteMethod().getParameterTypes();
        List<String> parameterTypes = getParameterTypes(typeParameters);

        int idx = 0;
        for(int q = 0; q < urlBits.size(); q++){
            UrlBit urlBit = urlBits.get(q);
            if(urlBit.isVariable()){

                try {
                    String methodType = parameterTypes.get(idx);
                    String bit = bits.get(q);
                    if (!bit.equals("")) {
                        if (methodType.equals("java.lang.Boolean")) {
                            Boolean.valueOf(bit);
                        }
                        if (methodType.equals("java.lang.Integer")) {
                            Integer.parseInt(bit);
                        }
                        if(methodType.equals("java.lang.Long")){
                            Long.parseLong(bit);
                        }
                    }
                    idx++;

                }catch(Exception ex){
                    return false;
                }
            }
        }
        return true;
    }

    public List<String> getParameterTypes(Class[] clsParamaters){
        List<String> parameterTypes = new ArrayList<>();
        for(Class<?> klass : clsParamaters){
            String type = klass.getTypeName();
            if(!type.contains("HttpRequest") &&
                    !type.contains("HttpResponse") &&
                    !type.contains("Cache")){
                parameterTypes.add(type);
            }
        }
        return parameterTypes;
    }

    public Object[] getRouteParameters(String requestUri, RouteEndpoint routeEndpoint, Cache cache, HttpRequest httpRequest, HttpResponse httpResponse, SecurityManager securityManager){

        List<VariablePosition> endpointValues = getRouteVariablePositions(requestUri, routeEndpoint);
        List<Object> params = new ArrayList<>();
        List<String> typeNames = routeEndpoint.getTypeNames();
        int currentIndex = 0;//todo:
        for(int activeIndex = 0; activeIndex <  typeNames.size(); activeIndex++){
            String type = typeNames.get(activeIndex);
            if(type.equals("net.plsar.security.SecurityManager")){
                params.add(httpRequest);
            }
            if(type.equals("net.plsar.model.HttpRequest")){
                params.add(httpRequest);
            }
            if(type.equals("net.plsar.model.HttpResponse")){
                params.add(httpResponse);
            }
            if(type.equals("net.plsar.model.Cache")){
                params.add(cache);
            }
            if(type.equals("java.lang.Integer")){
                params.add(Integer.valueOf(endpointValues.get(currentIndex).getValue()));
                currentIndex++;
            }
            if(type.equals("java.lang.Long")){
                params.add(Long.valueOf(endpointValues.get(currentIndex).getValue()));
                currentIndex++;
            }
            if(type.equals("java.math.BigDecimal")){
                params.add(new BigDecimal(endpointValues.get(currentIndex).getValue()));
                currentIndex++;
            }
            if(type.equals("java.lang.String")){
                params.add(endpointValues.get(currentIndex).getValue());
                currentIndex++;
            }
        }

        return params.toArray();
    }

    protected List<VariablePosition> getRouteVariablePositions(String uri, RouteEndpoint routeEndpoint){
        List<String> pathParts = Arrays.asList(uri.split("/"));
        List<String> regexParts = Arrays.asList(routeEndpoint.getRegexRoutePath().split("/"));
        List<VariablePosition> httpValues = new ArrayList<>();

        for(int activeIndex = 0; activeIndex < regexParts.size(); activeIndex++){
            String regex = regexParts.get(activeIndex);
            if(regex.contains("A-Za-z0-9")){
                httpValues.add(new VariablePosition(activeIndex, pathParts.get(activeIndex)));
            }
        }
        return httpValues;
    }

    public String getRedirect(String uri){
        String[] redirectBits = uri.split("]");
        if(redirectBits.length > 1)return redirectBits[1];
        return null;
    }
}
