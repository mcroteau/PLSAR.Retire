package net.plsar.resources;

public class AnnotationElementHolder {
    AnnotationElement routeNegotiator;
    AnnotationElement serverStartup;

    public AnnotationElement getRouteNegotiator() {
        return routeNegotiator;
    }

    public void setRouteNegotiator(AnnotationElement routeNegotiator) {
        this.routeNegotiator = routeNegotiator;
    }

    public AnnotationElement getServerStartup() {
        return serverStartup;
    }

    public void setServerStartup(AnnotationElement serverStartup) {
        this.serverStartup = serverStartup;
    }

}
