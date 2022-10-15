package net.plsar.model;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {
    String redirectLocation;
    String contentType;
    String content;
    List<SecurityAttribute> securityAttributes;
    OutputStream responseStream;

    public String getRedirectLocation() {
        return redirectLocation;
    }

    public void setRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public HttpResponse(){
        this.securityAttributes = new ArrayList();
    }

    public void removeRedirectLocation() {
        this.redirectLocation = null;
    }

    public void send(String url) {
    }
}

