package plsar;

import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;

public class FooService {
    public String foo(Cache cache, NetworkRequest request, SecurityManager securityManager){
        System.out.println("foo");
        return "foo.bar";
    }
}
