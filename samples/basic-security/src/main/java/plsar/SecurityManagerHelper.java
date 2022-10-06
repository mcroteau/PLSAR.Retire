package plsar;

import net.plsar.Persistence;
import net.plsar.PersistenceConfig;
import net.plsar.security.DatabaseAccess;
import net.plsar.security.PlsarSecurityManager;
import net.plsar.example.assist.AuthSecurityAccess;

public class SecurityManagerHelper {
    public PlsarSecurityManager getSecurityManager(){
        PersistenceConfigHelper persistenceConfigHelper = new PersistenceConfigHelper();
        PersistenceConfig persistenceConfig = persistenceConfigHelper.getPersistenceConfig();
        Persistence persistence = new Persistence(persistenceConfig);
        UserRepo userRepo = new UserRepo(persistence);
        DatabaseAccess authAccess = new AuthSecurityAccess(userRepo);
        return new PlsarSecurityManager(authAccess);
    }
}
