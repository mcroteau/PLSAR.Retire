package net.plsar.security.renderer;

import net.plsar.implement.ViewRenderer;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityManager;
import net.plsar.security.SecurityManagerHelper;

public class UserRenderer implements ViewRenderer {

    public boolean truthy(NetworkRequest networkRequest){ return true; }

    public String render(NetworkRequest networkRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        SecurityManager security = securityManagerHelper.getSecurityManager(networkRequest);
        return security.get("user", networkRequest);
    }

    public String getKey() {
        return "ocean:username";
    }

    public Boolean isEval() {
        return false;
    }
}