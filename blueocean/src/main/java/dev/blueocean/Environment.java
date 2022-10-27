package dev.blueocean;

public class Environment {

    public Environment() {}

    public Environment(String environment){
        this.environment = environment;
    }

    String environment;


    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
