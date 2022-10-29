package net.plsar.security.renderer;

import net.plsar.implement.ViewRenderer;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityManager;
import net.plsar.security.SecurityManagerHelper;

public class GuestRenderer implements ViewRenderer {

    public boolean truthy(NetworkRequest networkRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        SecurityManager security = securityManagerHelper.getSecurityManager(networkRequest);
        return !security.isAuthenticated(networkRequest);
    }

    public String render(NetworkRequest networkRequest){
        return "";
    }

    public String getKey() { return "ocean:isAnonymous"; }

    public Boolean isEval() {
        return true;
    }
}
