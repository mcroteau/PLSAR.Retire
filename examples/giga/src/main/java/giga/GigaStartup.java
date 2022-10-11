package giga;

import net.plsar.RouteAttributes;
import net.plsar.annotations.ServerStartup;
import net.plsar.implement.ServerListener;

@ServerStartup
public class GigaStartup implements ServerListener {
    @Override
    public RouteAttributes startup() {
        return new RouteAttributes();
    }
}
