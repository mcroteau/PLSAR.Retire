package net.plsar.security;

import net.plsar.Dao;
import net.plsar.PersistenceConfig;
import net.plsar.RouteAttributes;
import net.plsar.model.HttpRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SecurityManagerHelper {

    public SecurityManager getSecurityManager(HttpRequest httpRequest){
        SecurityManager security = null;
        try {
            RouteAttributes routeAttributes = httpRequest.getRouteAttributes();
            PersistenceConfig persistenceConfig = routeAttributes.getPersistenceConfig();
            Dao dao = new Dao(persistenceConfig);
            Class<?> securityAccessClass = routeAttributes.getSecurityAccess();
            SecurityAccess securityAccessInstance = (SecurityAccess) securityAccessClass.getConstructor().newInstance();
            Method setPersistence = securityAccessInstance.getClass().getMethod("setPersistence", Dao.class);
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
