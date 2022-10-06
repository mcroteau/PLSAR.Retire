package net.plsar.security;

import net.plsar.model.HttpRequest;
import net.plsar.model.HttpResponse;
import net.plsar.model.HttpSession;
import net.plsar.model.SecurityAttribute;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SecurityManager {
    
    SecurityAccess securityAccess;
    ConcurrentMap<String, Boolean> sessionRegistry;

    public SecurityManager(SecurityAccess securityAccess){
        this.securityAccess = securityAccess;
        this.sessionRegistry = new ConcurrentHashMap<>();
    }

    public SecurityAccess getSecurityAccess() {
        return securityAccess;
    }

    public void setSecurityAccess(SecurityAccess securityAccess) {
        this.securityAccess = securityAccess;
    }

    public boolean hasRole(String role, HttpRequest httpRequest){
        String user = getUser(httpRequest);
        if(user != null) {
            Set<String> roles = securityAccess.getRoles(user);
            if(roles.contains(role)){
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(String permission, HttpRequest httpRequest){
        String user = getUser(httpRequest);
        if(user != null) {
            Set<String> permissions = securityAccess.getPermissions(user);
            if(permissions.contains(permission)){
                return true;
            }
        }
        return false;
    }

    public String getUser(HttpRequest httpRequest){
        HttpSession httpSession = httpRequest.getSession(true);
        String user = (String) httpSession.get("user");
        return user;
    }

    public String get(String key, HttpRequest httpRequest){
        HttpSession httpSession = httpRequest.getSession(true);
        if(httpSession.getAttributes().containsKey(key)){
            return String.valueOf(httpSession.get(key));
        }
        return null;
    }

    public void set(String key, String value, HttpRequest httpRequest){
        HttpSession httpSession = httpRequest.getSession(true);
        httpSession.set(key, value);
    }

    public Boolean signin(String username, String passwordUntouched, HttpRequest httpRequest, HttpResponse httpResponse){
        String hashed = hash(passwordUntouched);
        String password = securityAccess.getPassword(username);

        if(!userIsAuthenticated(httpRequest) &&
                password.equals(hashed)){

            HttpSession oldHttpSession = httpRequest.getSession(true);
            if(oldHttpSession != null){
                expireHttpSession(oldHttpSession, httpResponse);
                this.sessionRegistry.remove(oldHttpSession.getGuid());
            }

            HttpSession httpSession = httpRequest.getSession(false);
            this.sessionRegistry.put(httpSession.getGuid(), true);

            httpSession.set("user", username);
            httpRequest.setSession(httpSession);

            return true;
        }

        return false;
    }

    public boolean signout(HttpRequest httpRequest, HttpResponse httpResponse){
        HttpSession oldHttpSession = httpRequest.getSession(true);

        if(httpRequest != null){
            expireHttpSession(oldHttpSession, httpResponse);
            this.sessionRegistry.remove(oldHttpSession.getGuid());
        }
        return true;
    }

    public void expireHttpSession(HttpSession oldHttpSession, HttpResponse httpResponse){
        SecurityAttribute securityAttribute = new SecurityAttribute("plsar-sessions", oldHttpSession.getGuid());
        httpResponse.getSecurityAttributes().add(securityAttribute);
    }

    public boolean userIsAuthenticated(HttpRequest httpRequest){
        HttpSession httpSession = httpRequest.getSession(true);

        if(httpSession != null) {
            return this.sessionRegistry.containsKey(httpSession.getGuid());
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

}
