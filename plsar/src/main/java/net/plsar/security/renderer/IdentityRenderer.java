package net.plsar.security.renderer;

import net.plsar.implement.ViewRenderer;
import net.plsar.model.HttpRequest;
import net.plsar.security.PlsarSecurityManager;
import net.plsar.example.SecurityManagerHelper;

public class IdentityRenderer implements ViewRenderer {

    public boolean truthy(HttpRequest httpRequest){
        return true;
    }

    public String render(HttpRequest httpRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        PlsarSecurityManager security = securityManagerHelper.getSecurityManager();
        return security.get("userId", httpRequest);
    }

    public String getKey() {
        return "gigante:id";
    }

    public Boolean isEval() {
        return false;
    }
}
