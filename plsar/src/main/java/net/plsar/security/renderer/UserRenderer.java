package net.plsar.security.renderer;

import net.plsar.implement.ViewRenderer;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;
import net.plsar.security.SecurityManagerHelper;

public class UserRenderer implements ViewRenderer {

    public boolean truthy(HttpRequest httpRequest){ return true; }

    public String render(HttpRequest httpRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        SecurityManager security = securityManagerHelper.getSecurityManager(httpRequest);
        return security.get("user", httpRequest);
    }

    public String getKey() {
        return "plsar:user";
    }

    public Boolean isEval() {
        return false;
    }
}