package net.plsar;

import java.util.Map;

public class RouteResponse {

    byte[] responseBytes;
    String contentType;
    String responseCode;

    Map<String, Object> redirectAttributes;

    public byte[] getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(byte[] responseBytes) {
        this.responseBytes = responseBytes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, Object> getRedirectAttributes() {
        return redirectAttributes;
    }

    public void setRedirectAttributes(Map<String, Object> redirectAttributes) {
        this.redirectAttributes = redirectAttributes;
    }

    public RouteResponse(Map<String, Object> redirectAttributes) {
        this.redirectAttributes = redirectAttributes;
    }

    public RouteResponse(byte[] responseBytes, String responseCode, String contentType) {
        this.responseBytes = responseBytes;
        this.responseCode = responseCode;
        this.contentType = contentType;
    }

    public RouteResponse() { }

}
