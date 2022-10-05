package io.support;

import io.service.StartupService;
import qio.Qio;
import qio.annotate.Events;
import qio.support.QioEvents;

@Events
public class Startup implements QioEvents {
    @Override
    public void setupComplete(Qio qio) {
        try {
            StartupService startupService = (StartupService) qio.getElement("startupservice");
            startupService.start();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}