package net.plsar.implement;

import net.plsar.model.HttpRequest;
import net.plsar.model.HttpResponse;

public interface RequestNegotiator {
    void intercept(HttpRequest request, HttpResponse response);
}
