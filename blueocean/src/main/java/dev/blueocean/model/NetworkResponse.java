package dev.blueocean.model;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class NetworkResponse {
    String redirectLocation;
    List<SecurityAttribute> securityAttributes;
    OutputStream responseStream;

    public String getRedirectLocation() {
        return redirectLocation;
    }

    public void setRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    public List<SecurityAttribute> getSecurityAttributes() {
        return securityAttributes;
    }

    public void setSecurityAttributes(List<SecurityAttribute> securityAttributes) {
        this.securityAttributes = securityAttributes;
    }

    public OutputStream getResponseStream() {
        return responseStream;
    }

    public void setResponseStream(OutputStream responseStream) {
        this.responseStream = responseStream;
    }

    public NetworkResponse(){
        this.securityAttributes = new ArrayList();
    }

    public void removeRedirectLocation() {
        this.redirectLocation = null;
    }

    public void send(String url) {
    }
}

