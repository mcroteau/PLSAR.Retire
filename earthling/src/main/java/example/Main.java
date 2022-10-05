package example;

import oceanblue.OceanBlue;
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

        OceanBlue oceanBlue = new OceanBlue(8080);
        oceanBlue.addViewRenderer(new AuthenticatedRenderer());
        oceanBlue.addViewRenderer(new GuestRenderer());
        oceanBlue.addViewRenderer(new UserRenderer());
        oceanBlue.setPersistenceConfig(persistenceConfig);
        oceanBlue.setSchemaConfig(schemaConfig);

        oceanBlue.start();
    }
}