package net.plsar.security.renderer;

import net.plsar.implement.ViewRenderer;
import net.plsar.model.HttpRequest;
import net.plsar.security.PlsarSecurityManager;
import net.plsar.example.SecurityManagerHelper;

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