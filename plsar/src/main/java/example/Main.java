package example;

import example.assist.AuthAccess;
import oceanblue.PLSAR;
import oceanblue.SchemaConfig;
import oceanblue.PersistenceConfig;
import oceanblue.security.renderer.AuthenticatedRenderer;
import oceanblue.security.renderer.GuestRenderer;
import oceanblue.security.renderer.UserRenderer;

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

        plsar.setSecurityAccess(AuthAccess.class);
        plsar.start();
    }
}