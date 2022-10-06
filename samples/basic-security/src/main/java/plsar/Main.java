package plsar;

import net.plsar.PLSAR;
import net.plsar.PersistenceConfig;
import net.plsar.SchemaConfig;
import net.plsar.security.renderer.AuthenticatedRenderer;
import net.plsar.security.renderer.GuestRenderer;
import net.plsar.security.renderer.UserRenderer;
import plsar.assist.AuthSecurityAccess;

public class Main {
    public static void main(String[] args) {
        PersistenceConfigHelper persistenceConfigHelper = new PersistenceConfigHelper();
        PersistenceConfig persistenceConfig = persistenceConfigHelper.getPersistenceConfig();

        SchemaConfig schemaConfig = new SchemaConfig();
        schemaConfig.setSchema("schema.sql");
        schemaConfig.setEnvironment(Environments.DEVELOPMENT);

        PLSAR plsar = new PLSAR(8080);
        plsar.setPersistenceConfig(persistenceConfig);
        plsar.setSchemaConfig(schemaConfig);

        plsar.addViewRenderer(AuthenticatedRenderer.class);
        plsar.addViewRenderer(GuestRenderer.class);
        plsar.addViewRenderer(UserRenderer.class);

        plsar.setSecurityAccess(AuthSecurityAccess.class);
        plsar.start();
    }
}