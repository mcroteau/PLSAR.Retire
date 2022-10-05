package example;

import oceanblue.Persistence;
import oceanblue.PersistenceConfig;
import oceanblue.security.DatabaseAccess;
import oceanblue.security.EarthlingSecurityManager;
import example.assist.AuthAccess;

public class SecurityManagerHelper {
    public EarthlingSecurityManager getSecurityManager(){
        PersistenceConfigHelper persistenceConfigHelper = new PersistenceConfigHelper();
        PersistenceConfig persistenceConfig = persistenceConfigHelper.getPersistenceConfig();
        Persistence persistence = new Persistence(persistenceConfig);
        UserRepo userRepo = new UserRepo(persistence);
        DatabaseAccess authAccess = new AuthAccess(userRepo);
        return new EarthlingSecurityManager(authAccess);
    }
}
