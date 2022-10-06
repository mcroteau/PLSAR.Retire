package oceanblue;

import java.util.Map;

public class RouteResponse {

    String content;
    Map<String, Object> redirectAttributes;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public RouteResponse(String content) {
        this.content = content;
    }

    public RouteResponse() { }

}
