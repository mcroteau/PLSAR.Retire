package giga;

import giga.service.StartupService;
import dev.blueocean.annotations.ServerStartup;
import dev.blueocean.implement.ServerListener;

@ServerStartup
public class GigaStartup implements ServerListener {
    @Override
    public void startup() {
        try{
            StartupService startupService = new StartupService();
            startupService.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
