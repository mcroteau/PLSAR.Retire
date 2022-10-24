package dev.blueocean.security.renderer;

import dev.blueocean.implement.ViewRenderer;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;
import dev.blueocean.security.SecurityManagerHelper;

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
