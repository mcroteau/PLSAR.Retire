package net.plsar.security.renderer;

import net.plsar.implement.ViewRenderer;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;
import net.plsar.security.SecurityManagerHelper;

public class GuestRenderer implements ViewRenderer {

    public boolean truthy(HttpRequest httpRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        SecurityManager security = securityManagerHelper.getSecurityManager(httpRequest);
        return !security.isAuthenticated(httpRequest);
    }

    public String render(HttpRequest httpRequest){
        return "";
    }

    public String getKey() { return "plsar:guest"; }

    public Boolean isEval() {
        return true;
    }
}
