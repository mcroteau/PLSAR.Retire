package net.plsar.resources;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ComponentsHolder {
    AnnotationComponent serverStartup;
    ConcurrentMap<String, Class<?>> components;

    public ComponentsHolder(){
        this.components = new ConcurrentHashMap<>();
    }

    public ConcurrentMap<String, Class<?>> getComponents() {
        return components;
    }

    public void setComponents(ConcurrentMap<String, Class<?>> components) {
        this.components = components;
    }

    public AnnotationComponent getServerStartup() {
        return serverStartup;
    }

    public void setServerStartup(AnnotationComponent serverStartup) {
        this.serverStartup = serverStartup;
    }

}
