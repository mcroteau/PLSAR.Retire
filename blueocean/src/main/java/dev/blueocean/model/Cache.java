package dev.blueocean.model;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    Map<String, Object> cache;

    public void set(String key, Object value){
        this.cache.put(key, value);
    }
    public Object get(String key){
        if(this.cache.containsKey(key)){
            return this.cache.get(key);
        }
        return null;
    }
    public Map<String, Object> getCache() {
        return cache;
    }
    public void setCache(Map<String, Object> cache) {
        this.cache = cache;
    }
    public Cache(){
        this.cache = new HashMap<>();
    }
}
