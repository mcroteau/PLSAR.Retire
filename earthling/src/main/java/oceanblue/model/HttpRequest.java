package oceanblue.model;

import oceanblue.RouteAttributes;
import oceanblue.resources.ServerResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    String uriPath;
    String verb;
    String requestBody;
    HttpSession httpSession;
    Map<String, String> headers;
    ServerResources serverResources;
    Map<String, RequestComponent> requestComponents;
    RouteAttributes routeAttributes;

    public HttpRequest(String verb, String uriPath, ServerResources serverResources, Map<String, String> sessionRouteRegistry) {
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

    public HttpSession getSession(boolean existingSession) {
        if(!existingSession) {
            Long time = serverResources.getTime(0);
            this.httpSession = new HttpSession(time, serverResources);
        }
        return this.httpSession;
    }

    public void setSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public void setRequestComponent(String key, RequestComponent requestComponent){
        this.requestComponents.put(key, requestComponent);
    }

    public String value(String key){
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

}
