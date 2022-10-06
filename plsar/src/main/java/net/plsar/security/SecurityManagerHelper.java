package net.plsar.security;

import net.plsar.Persistence;
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
            Persistence persistence = new Persistence(persistenceConfig);
            Class<?> securityAccessClass = routeAttributes.getSecurityAccess();
            SecurityAccess securityAccessInstance = (SecurityAccess) securityAccessClass.getConstructor().newInstance();
            Method setPersistence = securityAccessInstance.getClass().getMethod("setPersistence", Persistence.class);
            setPersistence.invoke(persistence);
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
