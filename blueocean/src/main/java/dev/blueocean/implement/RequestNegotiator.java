package dev.blueocean.implement;

import dev.blueocean.model.NetworkRequest;
import dev.blueocean.model.NetworkResponse;

public interface RequestNegotiator {
    void intercept(NetworkRequest request, NetworkResponse response);
}
