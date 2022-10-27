package dev.blueocean;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ViewConfig {


    public ViewConfig(){
        this.viewExtension = ".jsp";
        this.resourcesPath = Paths.get("src", "main", "webapp", "resources");
        this.viewsPath = Paths.get("src", "main", "webapp");
    }

    Path viewsPath;
    Path resourcesPath;
    String viewExtension;

    public Path getViewsPath() {
        return viewsPath;
    }

    public void setViewsPath(Path viewsPath) {
        this.viewsPath = viewsPath;
    }

    public Path getResourcesPath() {
        return resourcesPath;
    }

    public void setResourcesPath(Path resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    public String getViewExtension() {
        return viewExtension;
    }

    public void setViewExtension(String viewExtension) {
        this.viewExtension = viewExtension;
    }
}
