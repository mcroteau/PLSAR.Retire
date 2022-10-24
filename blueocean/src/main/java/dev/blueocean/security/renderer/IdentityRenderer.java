package dev.blueocean.security.renderer;

import dev.blueocean.implement.ViewRenderer;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;
import dev.blueocean.security.SecurityManagerHelper;

public class IdentityRenderer implements ViewRenderer {

    public boolean truthy(NetworkRequest networkRequest){
        return true;
    }

    public String render(NetworkRequest networkRequest){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        SecurityManager security = securityManagerHelper.getSecurityManager(networkRequest);
        return security.get("userId", networkRequest);
    }

    public String getKey() {
        return "ocean:userid";
    }

    public Boolean isEval() {
        return false;
    }
}
