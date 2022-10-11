package kilo;

import net.plsar.RouteAttributes;
import net.plsar.annotations.ServerStartup;
import net.plsar.implement.ServerListener;

@ServerStartup
public class GigaStartup implements ServerListener {
    @Override
    public RouteAttributes startup() {
        RouteAttributes routeAttributes = new RouteAttributes();
        routeAttributes.getAttributes().put("storage.key", "");
        routeAttributes.getAttributes().put("storage.secret", "");
        routeAttributes.getAttributes().put("easypost.key", "");
        routeAttributes.getAttributes().put("stripe.key", "");
        routeAttributes.getAttributes().put("sms.key", "");
        return routeAttributes;
    }
}