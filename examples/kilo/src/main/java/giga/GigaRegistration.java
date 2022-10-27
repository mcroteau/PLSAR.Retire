package giga;

import dev.blueocean.RouteAttributes;
import dev.blueocean.annotations.RouteRegistration;
import dev.blueocean.implement.RouteRegister;

@RouteRegistration
public class GigaRegistration implements RouteRegister {

    @Override
    public RouteAttributes register() {
        RouteAttributes routeAttributes = new RouteAttributes();
        routeAttributes.set("sms.key", "");
        routeAttributes.set("easypost.key", "");
        routeAttributes.set("cloud.key", "");
        routeAttributes.set("cloud.secret", "");
        routeAttributes.set("stripe.key", "");
        return routeAttributes;
    }

}
