package oceanblue.model;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {
    String redirectLocation;
    String contentType;
    String content;
    List<SecurityAttribute> securityAttributes;
    OutputStream responseOutput;

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

    public OutputStream getResponseOutput() {
        return responseOutput;
    }

    public void setResponseOutput(OutputStream responseOutput) {
        this.responseOutput = responseOutput;
    }

    public HttpResponse(){
        this.securityAttributes = new ArrayList();
    }

    public void removeRedirectLocation() {
        this.redirectLocation = null;
    }
}

