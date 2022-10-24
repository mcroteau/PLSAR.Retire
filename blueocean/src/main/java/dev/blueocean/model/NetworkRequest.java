package dev.blueocean.model;

import dev.blueocean.RouteAttributes;
import dev.blueocean.resources.ServerResources;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkRequest {
    String uriPath;
    String verb;
    String requestBody;
    NetworkSession networkSession;
    Map<String, String> headers;
    ServerResources serverResources;
    Map<String, RequestComponent> requestComponents;
    RouteAttributes routeAttributes;

    public NetworkRequest(String verb, String uriPath, ServerResources serverResources) {
        this.uriPath = uriPath;
        this.verb = verb;
        this.headers = new HashMap<>();
        this.requestComponents = new HashMap<>();
        this.serverResources = serverResources;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public void addHeader(String fieldKey, String headerValue){
        this.headers.put(fieldKey, headerValue);
    }

    public void removeHeader(String fieldKey) {
        this.headers.remove(fieldKey);
    }

    public String getVerb() {
        return verb.toLowerCase();
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getHeader(String fieldKey){
        if(headers.containsKey(fieldKey)){
            return headers.get(fieldKey);
        }
        return null;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void put(String elementName, RequestComponent requestComponent){
        this.requestComponents.put(elementName, requestComponent);
    }

    public NetworkSession getSession(boolean existingSession) {
        if(!existingSession) {
            Long time = serverResources.getTime(0);
            this.networkSession = new NetworkSession(time, serverResources);
        }
        return this.networkSession;
    }

    public void setSession(NetworkSession networkSession) {
        this.networkSession = networkSession;
    }

    public void setRequestComponent(String key, RequestComponent requestComponent){
        this.requestComponents.put(key, requestComponent);
    }

    public String getValue(String key){
        if(requestComponents.containsKey(key)){
            return requestComponents.get(key).getValue();
        }
        return null;
    }

    public RequestComponent getRequestComponent(String key){
        if(requestComponents.containsKey(key)){
            return requestComponents.get(key);
        }
        return null;
    }

    public List<RequestComponent> getRequestComponents(){
        List<RequestComponent> components = new ArrayList<>();
        for(Map.Entry<String, RequestComponent> requestComponentEntry : requestComponents.entrySet()){
            components.add(requestComponentEntry.getValue());
        }
        return components;
    }

    public RouteAttributes getRouteAttributes() {
        return routeAttributes;
    }

    public void setRouteAttributes(RouteAttributes routeAttributes) {
        this.routeAttributes = routeAttributes;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setValues(String parameters) {
        String[] keyValues = parameters.split("&");
        for(String keyValue : keyValues){
            String[] parts = keyValue.split("=");
            if(parts.length > 1){
                String key = parts[0];
                String value = parts[1];
                RequestComponent requestComponent = new RequestComponent();
                requestComponent.setName(key);
                requestComponent.setValue(value);
                requestComponents.put(key, requestComponent);
            }
        }
    }

    public Object inflect(Class<?> klass){
        Object classInstance =  null;
        try {
            classInstance = klass.getConstructor().newInstance();
            Field[] classInstanceFields = klass.getDeclaredFields();
            for(Field classInstanceField : classInstanceFields){
                String fieldName = classInstanceField.getName();
                String fieldValue = getValue(fieldName);
                if(fieldValue != null &&
                        !fieldValue.equals("")){

                    classInstanceField.setAccessible(true);
                    Type fieldType = classInstanceField.getType();

                    if (fieldType.getTypeName().equals("int") ||
                            fieldType.getTypeName().equals("java.lang.Integer")) {
                        classInstanceField.set(classInstance, Integer.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("double") ||
                            fieldType.getTypeName().equals("java.lang.Double")) {
                        classInstanceField.set(classInstance, Double.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("float") ||
                            fieldType.getTypeName().equals("java.lang.Float")) {
                        classInstanceField.set(classInstance, Float.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("long") ||
                            fieldType.getTypeName().equals("java.lang.Long")) {
                        classInstanceField.set(classInstance, Long.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("boolean") ||
                            fieldType.getTypeName().equals("java.lang.Boolean")) {
                        classInstanceField.set(classInstance, Boolean.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("java.math.BigDecimal")) {
                        classInstanceField.set(classInstance, new BigDecimal(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("java.lang.String")) {
                        classInstanceField.set(classInstance, fieldValue);
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return classInstance;
    }

}
