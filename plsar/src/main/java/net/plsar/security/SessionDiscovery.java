package net.plsar.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

//this is going to be open source, this means the source code will be available to download, modify etc.
public class SessionDiscovery {
    public static ConcurrentMap<String, Boolean> sessionRegistry = new ConcurrentHashMap<>(7000, 7, 2300);
}
