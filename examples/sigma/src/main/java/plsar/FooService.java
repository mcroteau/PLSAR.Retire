package plsar;

import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;

public class FooService {
    public String foo(Cache cache, HttpRequest request, SecurityManager securityManager){
        System.out.println("foo");
        return "foo.bar";
    }
}
