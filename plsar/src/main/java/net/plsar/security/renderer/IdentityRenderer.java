package net.plsar.security.renderer;

import net.plsar.implement.ViewRenderer;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;
import net.plsar.security.SecurityManagerHelper;

public class IdentityRenderer implements ViewRenderer {

    public boolean truthy(HttpRequest httpRequest){
        return true;
    }

    public String render(HttpRequest httpRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        SecurityManager security = securityManagerHelper.getSecurityManager(httpRequest);
        return security.get("userId", httpRequest);
    }

    public String getKey() {
        return "plsar:id";
    }

    public Boolean isEval() {
        return false;
    }
}
