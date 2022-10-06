package example;

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

        PLSAR PLSAR = new PLSAR(8080);
        PLSAR.addViewRenderer(new AuthenticatedRenderer());
        PLSAR.addViewRenderer(new GuestRenderer());
        PLSAR.addViewRenderer(new UserRenderer());
        PLSAR.setPersistenceConfig(persistenceConfig);
        PLSAR.setSchemaConfig(schemaConfig);

        PLSAR.start();
    }
}