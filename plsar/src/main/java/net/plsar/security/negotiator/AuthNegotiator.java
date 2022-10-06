package net.plsar.security.negotiator;

import net.plsar.implement.RequestNegotiator;
import net.plsar.model.HttpRequest;
import net.plsar.model.HttpResponse;

//////// Thank you Apache Shiro! ////////
public class AuthNegotiator implements RequestNegotiator {
    @Override
    public void intercept(HttpRequest request, HttpResponse response) {
        ThreadLocal<HttpRequest> requestStorage = new InheritableThreadLocal<>();
        requestStorage.set(request);

        ThreadLocal<HttpResponse> responseStorage = new InheritableThreadLocal<>();
        responseStorage.set(response);
    }
}
