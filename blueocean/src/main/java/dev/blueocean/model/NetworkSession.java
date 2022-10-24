package dev.blueocean.model;

import dev.blueocean.resources.ServerResources;

import java.util.HashMap;
import java.util.Map;

public class NetworkSession {
    String guid;
    Long time;
    Map<String, Object> attributes;

    public NetworkSession(Long time, String guid, ServerResources serverResources){
        this.guid = guid;
        this.time = time;
        this.attributes = new HashMap<>();
    }

    public NetworkSession(Long time, String guid){
        this.guid = guid;
        this.time = time;
        this.attributes = new HashMap<>();
    }

    public NetworkSession(Long time, ServerResources serverResources){
        this.guid = serverResources.getGuid(24);
        this.time = time;
        this.attributes = new HashMap<>();
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    public boolean set(String key, Object value){
        this.attributes.put(key, value);
        return true;
    }

    public Object get(String key){
        if(this.attributes.containsKey(key)){
            return this.attributes.get(key);
        }
        return "";
    }

    public boolean remove(String key){
        this.attributes.remove(key);
        return true;
    }

    public Map<String, Object> getAttributes(){
        return this.attributes;
    }

}
