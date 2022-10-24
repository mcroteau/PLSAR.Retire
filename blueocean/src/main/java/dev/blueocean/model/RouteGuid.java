package dev.blueocean.model;

import dev.blueocean.resources.ServerResources;

public class RouteGuid {
    String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public RouteGuid(ServerResources serverResources){
        this.guid = serverResources.getGuid(24);
    }
}
