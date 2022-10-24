package dev.blueocean.security;

import dev.blueocean.Dao;
import dev.blueocean.PersistenceConfig;
import dev.blueocean.RouteAttributes;
import dev.blueocean.model.NetworkRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SecurityManagerHelper {

    public SecurityManager getSecurityManager(NetworkRequest networkRequest){
        SecurityManager security = null;
        try {
            RouteAttributes routeAttributes = networkRequest.getRouteAttributes();
            PersistenceConfig persistenceConfig = routeAttributes.getPersistenceConfig();
            Dao dao = new Dao(persistenceConfig);
            Class<?> securityAccessClass = routeAttributes.getSecurityAccess();
            SecurityAccess securityAccessInstance = (SecurityAccess) securityAccessClass.getConstructor().newInstance();
            Method setPersistence = securityAccessInstance.getClass().getMethod("setDao", Dao.class);
            setPersistence.invoke(securityAccessInstance, dao);
            security = new SecurityManager(securityAccessInstance);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return security;
    }
}
