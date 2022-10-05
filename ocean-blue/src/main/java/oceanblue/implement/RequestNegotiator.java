package oceanblue.implement;

import oceanblue.model.HttpRequest;
import oceanblue.model.HttpResponse;

public interface RequestNegotiator {
    void intercept(HttpRequest request, HttpResponse response);
}
