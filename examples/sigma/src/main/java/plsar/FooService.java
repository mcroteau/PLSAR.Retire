package plsar;

import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;

public class FooService {
    public String foo(Cache cache, NetworkRequest request, SecurityManager securityManager){
        System.out.println("foo");
        return "foo.bar";
    }
}
