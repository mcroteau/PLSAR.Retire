package oceanblue;

import oceanblue.implement.ViewRenderer;
import oceanblue.model.HttpSession;

import java.util.HashMap;
import java.util.Map;

public class RouteAttributes {
    RouteAttributes routeAttributes;
    PersistenceConfig persistenceConfig;

    Map<String, Object> attributes;
    Map<String, HttpSession> sessions;
    Map<String, ViewRenderer> viewRenderers;
    RouteEndpointHolder routeEndpointHolder;

    public Object get(String key){
        if(this.attributes.containsKey(key)){
            return this.attributes.get(key);
        }
        return null;
    }

    public RouteAttributes getRouteAttributes() {
        return routeAttributes;
    }

    public void setRouteAttributes(RouteAttributes routeAttributes) {
        this.routeAttributes = routeAttributes;
    }

    public PersistenceConfig getPersistenceConfig() {
        return persistenceConfig;
    }

    public void setPersistenceConfig(PersistenceConfig persistenceConfig) {
        this.persistenceConfig = persistenceConfig;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, HttpSession> getSessions() {
        return sessions;
    }

    public void setSessions(Map<String, HttpSession> sessions) {
        this.sessions = sessions;
    }

    public Map<String, ViewRenderer> getViewRenderers() {
        return viewRenderers;
    }

    public void setViewRenderers(Map<String, ViewRenderer> viewRenderers) {
        this.viewRenderers = viewRenderers;
    }

    public RouteEndpointHolder getRouteEndpointHolder() {
        return routeEndpointHolder;
    }

    public void setRouteEndpointHolder(RouteEndpointHolder routeEndpointHolder) {
        this.routeEndpointHolder = routeEndpointHolder;
    }

    public RouteAttributes(){
        this.attributes = new HashMap<>();
        this.sessions = new HashMap<>();
        this.routeEndpointHolder = new RouteEndpointHolder();
    }

}
