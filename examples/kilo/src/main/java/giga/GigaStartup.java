package giga;

import giga.service.StartupService;
import net.plsar.annotations.ServerStartup;
import net.plsar.implement.ServerListener;

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
