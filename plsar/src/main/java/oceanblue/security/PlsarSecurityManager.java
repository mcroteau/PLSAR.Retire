package oceanblue.security;

import oceanblue.model.HttpRequest;
import oceanblue.model.HttpResponse;
import oceanblue.model.HttpSession;
import oceanblue.model.SecurityAttribute;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public class PlsarSecurityManager {
    
    DatabaseAccess databaseAccess;

    public PlsarSecurityManager(DatabaseAccess databaseAccess){
        this.databaseAccess = databaseAccess;
    }

    public DatabaseAccess getDatabaseAccess() {
        return databaseAccess;
    }

    public void setDatabaseAccess(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public boolean hasRole(String role, HttpRequest httpRequest){
        String user = getUser(httpRequest);
        if(user != null) {
            Set<String> roles = databaseAccess.getRoles(user);
            if(roles.contains(role)){
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(String permission, HttpRequest httpRequest){
        String user = getUser(httpRequest);
        if(user != null) {
            Set<String> permissions = databaseAccess.getPermissions(user);
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
        String password = databaseAccess.getPassword(username);

        if(!userIsAuthenticated(httpRequest) &&
                password.equals(hashed)){

            HttpSession oldHttpSession = httpRequest.getSession(true);
            if(oldHttpSession != null){
                expireHttpSession(oldHttpSession, httpResponse);
                SessionDiscovery.sessionRegistry.remove(oldHttpSession.getGuid());
            }

            HttpSession httpSession = httpRequest.getSession(false);
            SessionDiscovery.sessionRegistry.put(httpSession.getGuid(), true);

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
            SessionDiscovery.sessionRegistry.remove(oldHttpSession.getGuid());
        }
        return true;
    }

    public void expireHttpSession(HttpSession oldHttpSession, HttpResponse httpResponse){
        SecurityAttribute securityAttribute = new SecurityAttribute("gigante-sessions", oldHttpSession.getGuid());
        httpResponse.getSecurityAttributes().add(securityAttribute);
    }

    public boolean userIsAuthenticated(HttpRequest httpRequest){
        HttpSession httpSession = httpRequest.getSession(true);

        if(httpSession != null) {
            return SessionDiscovery.sessionRegistry.containsKey(httpSession.getGuid());
        }
        return false;
    }

    public boolean configure(DatabaseAccess databaseAccess){
        this.databaseAccess = databaseAccess;
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
