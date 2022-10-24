package plsar;

import dev.blueocean.RouteAttributes;
import dev.blueocean.annotations.ServerStartup;
import dev.blueocean.implement.ServerListener;

@ServerStartup
public class BazStartup implements ServerListener {
    @Override
    public RouteAttributes startup() {

        return new RouteAttributes();
    }
}
