package io.service;

import qio.annotate.Property;
import qio.annotate.Service;

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
