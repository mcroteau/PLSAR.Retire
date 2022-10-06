package net.plsar.security.renderer;

import net.plsar.implement.ViewRenderer;
import net.plsar.model.HttpRequest;
import net.plsar.security.PlsarSecurityManager;
import net.plsar.example.SecurityManagerHelper;

public class GuestRenderer implements ViewRenderer {

    public boolean truthy(HttpRequest httpRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        PlsarSecurityManager security = securityManagerHelper.getSecurityManager();
        return !security.userIsAuthenticated(httpRequest);
    }

    public String render(HttpRequest httpRequest){
        return "";
    }

    public String getKey() { return "gigante:guest"; }

    public Boolean isEval() {
        return true;
    }
}
