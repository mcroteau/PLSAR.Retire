package dev.blueocean.security.negotiator;

import dev.blueocean.implement.RequestNegotiator;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.model.NetworkResponse;

//////// Thank you Apache Shiro! ////////
public class AuthNegotiator implements RequestNegotiator {
    @Override
    public void intercept(NetworkRequest request, NetworkResponse response) {
        ThreadLocal<NetworkRequest> requestStorage = new InheritableThreadLocal<>();
        requestStorage.set(request);

        ThreadLocal<NetworkResponse> responseStorage = new InheritableThreadLocal<>();
        responseStorage.set(response);
    }
}
