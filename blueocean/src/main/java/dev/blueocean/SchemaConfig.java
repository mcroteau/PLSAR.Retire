package dev.blueocean;

public class SchemaConfig {
    String schema;
    String environment;

    public String getSchema() {
        return schema;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}