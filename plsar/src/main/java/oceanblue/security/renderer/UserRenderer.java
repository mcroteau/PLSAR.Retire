package oceanblue.security.renderer;

import oceanblue.implement.ViewRenderer;
import oceanblue.model.HttpRequest;
import oceanblue.security.PlsarSecurityManager;
import example.SecurityManagerHelper;

public class UserRenderer implements ViewRenderer {

    public boolean truthy(HttpRequest httpRequest){ return true; }

    public String render(HttpRequest httpRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        PlsarSecurityManager security = securityManagerHelper.getSecurityManager();
        return security.get("user", httpRequest);
    }

    public String getKey() {
        return "gigante:user";
    }

    public Boolean isEval() {
        return false;
    }
}