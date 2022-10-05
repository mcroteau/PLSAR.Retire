package oceanblue.security.renderer;

import oceanblue.implement.ViewRenderer;
import oceanblue.model.HttpRequest;
import oceanblue.security.EarthlingSecurityManager;
import example.SecurityManagerHelper;

public class AuthenticatedRenderer implements ViewRenderer {

    public boolean truthy(HttpRequest httpRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        EarthlingSecurityManager security = securityManagerHelper.getSecurityManager();
        return security.userIsAuthenticated(httpRequest);
    }

    public String render(HttpRequest httpRequest){
        return "";
    }

    public String getKey() {
        return "gigante:authenticated";
    }

    public Boolean isEval() {
        return true;
    }
}
