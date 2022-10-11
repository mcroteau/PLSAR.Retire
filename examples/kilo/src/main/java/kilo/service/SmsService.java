package kilo.service;

import com.google.gson.Gson;
import kilo.model.Sms;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SmsService {

    static final String URI = "https://textbelt.com/text";

    Gson gson = new Gson();

    public boolean send(String key, String phone, String message) {

        try {
            Sms sms = new Sms();
            sms.setKey(key);
            sms.setPhone(phone);
            sms.setMessage(message);

            String json = gson.toJson(sms);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(SmsService.URI);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            client.execute(httpPost);
            client.close();

        } catch (UnsupportedEncodingException uex) {
            uex.printStackTrace();
        } catch (ClientProtocolException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}