package giga.service;

import net.plsar.annotations.Service;

@Service
public class MailService {

    public void send(String to, String subject, String body){
        try {
            /// -> send...
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
