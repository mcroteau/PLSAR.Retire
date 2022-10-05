package example;

import oceanblue.RouteAttributes;
import oceanblue.annotations.ServerStartup;
import oceanblue.implement.ServerListener;

@ServerStartup
public class BazStartup implements ServerListener {
    @Override
    public RouteAttributes startup() {
        return new RouteAttributes();
    }
}
