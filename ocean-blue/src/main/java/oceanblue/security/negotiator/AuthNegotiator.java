package oceanblue.security.negotiator;

import oceanblue.implement.RequestNegotiator;
import oceanblue.model.HttpRequest;
import oceanblue.model.HttpResponse;

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
