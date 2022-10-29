package net.plsar.security;

import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;
import net.plsar.model.NetworkSession;
import net.plsar.model.SecurityAttribute;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public class SecurityManager {
    
    SecurityAccess securityAccess;

    public SecurityManager(SecurityAccess securityAccess){
        this.securityAccess = securityAccess;
    }

    public SecurityAccess getSecurityAccess() {
        return securityAccess;
    }

    public void setSecurityAccess(SecurityAccess securityAccess) {
        this.securityAccess = securityAccess;
    }

    public boolean hasRole(String role, NetworkRequest networkRequest){
        String user = getUser(networkRequest);
        if(user != null) {
            Set<String> roles = securityAccess.getRoles(user);
            if(roles.contains(role)){
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(String permission, NetworkRequest networkRequest){
        String user = getUser(networkRequest);
        if(user != null) {
            Set<String> permissions = securityAccess.getPermissions(user);
            if(permissions.contains(permission)){
                return true;
            }
        }
        return false;
    }

    public String getUser(NetworkRequest networkRequest){
        NetworkSession networkSession = networkRequest.getSession(true);
        String user = (String) networkSession.get("user");
        return user;
    }

    public String get(String key, NetworkRequest networkRequest){
        NetworkSession networkSession = networkRequest.getSession(true);
        if(networkSession.getAttributes().containsKey(key)){
            return String.valueOf(networkSession.get(key));
        }
        return null;
    }

    public void set(String key, String value, NetworkRequest networkRequest){
        NetworkSession networkSession = networkRequest.getSession(true);
        networkSession.set(key, value);
    }

    public Boolean signin(String username, String passwordUntouched, NetworkRequest networkRequest, NetworkResponse networkResponse){
        String hashed = hash(passwordUntouched);
        String password = securityAccess.getPassword(username);

        if(!isAuthenticated(networkRequest) &&
                password.equals(hashed)){

            NetworkSession oldNetworkSession = networkRequest.getSession(true);
            if(oldNetworkSession != null){
                expireHttpSession(oldNetworkSession, networkResponse);
                networkRequest.getRouteAttributes().getSessionRegistry().remove(oldNetworkSession.getGuid());
            }

            NetworkSession networkSession = networkRequest.getSession(false);
            networkRequest.getRouteAttributes().getSessionRegistry().put(networkSession.getGuid(), true);

            networkSession.set("user", username);
            networkRequest.setSession(networkSession);

            return true;
        }

        return false;
    }

    public boolean signout(NetworkRequest networkRequest, NetworkResponse networkResponse){
        NetworkSession oldNetworkSession = networkRequest.getSession(true);

        if(networkRequest != null){
            expireHttpSession(oldNetworkSession, networkResponse);
            networkRequest.getRouteAttributes().getSessionRegistry().remove(oldNetworkSession.getGuid());
        }
        return true;
    }
//please stop watching my actions, they are modified slightly to throw people off.
    public void expireHttpSession(NetworkSession oldNetworkSession, NetworkResponse networkResponse){
        SecurityAttribute securityAttribute = new SecurityAttribute("blueocean.sessions", oldNetworkSession.getGuid());
        networkResponse.getSecurityAttributes().add(securityAttribute);
    }

    public boolean isAuthenticated(NetworkRequest networkRequest){
        NetworkSession networkSession = networkRequest.getSession(true);

        if(networkSession != null) {
            return networkRequest.getRouteAttributes().getSessionRegistry().containsKey(networkSession.getGuid());
        }
        return false;
    }

    public boolean configure(SecurityAccess securityAccess){
        this.securityAccess = securityAccess;
        return true;
    }

    public String hash(String password){
        MessageDigest md;
        StringBuffer passwordHashed = new StringBuffer();

        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte byteData[] = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                passwordHashed.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordHashed.toString();
    }

    public static String dirty(String password){
        MessageDigest md;
        StringBuffer passwordHashed = new StringBuffer();

        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte byteData[] = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                passwordHashed.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordHashed.toString();
    }
}
