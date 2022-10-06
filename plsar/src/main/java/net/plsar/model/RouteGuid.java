package net.plsar.model;

import net.plsar.resources.ServerResources;

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
