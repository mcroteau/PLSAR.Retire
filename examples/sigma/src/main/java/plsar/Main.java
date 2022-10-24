package plsar;

import dev.blueocean.BlueOcean;
import dev.blueocean.PersistenceConfig;
import dev.blueocean.SchemaConfig;
import dev.blueocean.drivers.Drivers;
import dev.blueocean.environments.Environments;
import dev.blueocean.security.renderer.AuthenticatedRenderer;
import dev.blueocean.security.renderer.GuestRenderer;
import dev.blueocean.security.renderer.UserRenderer;
import plsar.assist.AuthSecurityAccess;

public class Main {
    public static void main(String[] args) {
        PersistenceConfig persistenceConfig = new PersistenceConfig();
        persistenceConfig.setDriver(Drivers.H2);
        persistenceConfig.setUrl("jdbc:h2:~/devDb");
        persistenceConfig.setUser("sa");
        persistenceConfig.setPassword("");

        SchemaConfig schemaConfig = new SchemaConfig();
        schemaConfig.setSchema("schema.sql");
        schemaConfig.setEnvironment(Environments.DEVELOPMENT);

        BlueOcean blueOcean = new BlueOcean(1234);
        blueOcean.setNumberOfPartitions(40);
        blueOcean.setNumberOfRequestExecutors(100);

        blueOcean.setPersistenceConfig(persistenceConfig);
        blueOcean.setSchemaConfig(schemaConfig);

        blueOcean.addViewRenderer(AuthenticatedRenderer.class);
        blueOcean.addViewRenderer(GuestRenderer.class);
        blueOcean.addViewRenderer(UserRenderer.class);

        blueOcean.setSecurityAccess(AuthSecurityAccess.class);
        blueOcean.start();
    }
}