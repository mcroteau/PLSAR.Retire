package example;

import oceanblue.PersistenceConfig;
import oceanblue.drivers.Drivers;

public class PersistenceConfigHelper {
    public PersistenceConfig getPersistenceConfig(){
        PersistenceConfig persistenceConfig = new PersistenceConfig();
        persistenceConfig.setConnections(3);
        persistenceConfig.setDriver(Drivers.H2);
        persistenceConfig.setUrl("jdbc:h2:~/devDb");
        persistenceConfig.setUser("sa");
        persistenceConfig.setPassword("");
        return persistenceConfig;
    }
}
