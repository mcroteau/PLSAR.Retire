package io.informant;

import dev.blueocean.*;
import dev.blueocean.drivers.Drivers;
import dev.blueocean.environments.Environments;
import dev.blueocean.schemes.RenderingScheme;
import dev.blueocean.security.renderer.AuthenticatedRenderer;
import dev.blueocean.security.renderer.GuestRenderer;
import dev.blueocean.security.renderer.UserRenderer;
import io.informant.model.User;
import io.informant.model.UserFollow;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Informant {

    public static void main(String[] args){
        PersistenceConfig persistenceConfig = new PersistenceConfig();
        persistenceConfig.setDriver(Drivers.H2);
        persistenceConfig.setUrl("jdbc:h2:~/BLUE_OCEAN_DB");
        persistenceConfig.setUser("sa");
        persistenceConfig.setPassword("");

        SchemaConfig schemaConfig = new SchemaConfig();
        schemaConfig.setSchema("schema.sql");
        schemaConfig.setEnvironment(Environments.DEVELOPMENT);

        BlueOcean blueOcean = new BlueOcean(1234);
        blueOcean.setNumberOfPartitions(7);
        blueOcean.setNumberOfRequestExecutors(10);

        blueOcean.setPersistenceConfig(persistenceConfig);
        blueOcean.setSchemaConfig(schemaConfig);

        ViewConfig viewConfig = new ViewConfig();
        viewConfig.setResourcesPath("resources");
        viewConfig.setViewExtension(".jsp");
        blueOcean.setViewConfig(viewConfig);

        blueOcean.addViewRenderer(AuthenticatedRenderer.class);
        blueOcean.addViewRenderer(GuestRenderer.class);
        blueOcean.addViewRenderer(UserRenderer.class);

        blueOcean.setSecurityAccess(AuthSecurityAccess.class);
        blueOcean.setPageRenderingScheme(RenderingScheme.PAGE_CACHE);
        blueOcean.start();
    }

    public Long getDate(int days){
        LocalDateTime ldt = LocalDateTime.now().minusDays(days);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(getDateFormat());
        String date = dtf.format(ldt);
        return Long.valueOf(date);
    }

    public String getPhone(String phone){
        if(phone != null)
            return phone
                    .replaceAll("[^a-zA-Z0-9]", "")
                    .replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        return "";
    }

    public String getEmail(String email){
        if(email != null)
            return email.replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        return "";
    }

    public String getExtension(final String path) {
        String result = null;
        if (path != null) {
            result = "";
            if (path.lastIndexOf('.') != -1) {
                result = path.substring(path.lastIndexOf('.'));
                if (result.startsWith(".")) {
                    result = result.substring(1);
                }
            }
        }
        return result;
    }

    public String getBasePrefix(String name) {
        String ext = getExtension(name);
        if(ext.equals("jpeg"))return "data:image/jpeg;base64,";
        if(ext.equals("jpg"))return "data:image/jpeg;base64,";
        if(ext.equals("png"))return "data:image/png;base64,";
        if(ext.equals("gif"))return "data:image/gif;base64,";
        return "";
    }

    public static String getGuid(int n) {
        String CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
        StringBuilder uuid = new StringBuilder();
        Random rnd = new Random();
        while (uuid.length() < n) {
            int index = (int) (rnd.nextFloat() * CHARS.length());
            uuid.append(CHARS.charAt(index));
        }
        return uuid.toString();
    }

    public static String toIds(User authdUser, List<UserFollow> userFollows){
        Set<Long> ids = new HashSet<>();
        ids.add(authdUser.getId());
        for(UserFollow userFollow : userFollows) {
            ids.add(userFollow.getFollowingId());
        }
        return StringUtils.join(ids, ",");
    }

    public static String getEncoded(String completePhotoPath) {
        try {
            File file = new File(completePhotoPath);
            FileInputStream stream = new FileInputStream(file);

            int bufLength = 2048;
            byte[] buffer = new byte[2048];
            byte[] data;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int readLength;
            while ((readLength = stream.read(buffer, 0, bufLength)) != -1) {
                out.write(buffer, 0, readLength);
            }

            data = out.toByteArray();

            out.close();
            stream.close();

            return Base64.getEncoder().withoutPadding().encodeToString(data);

        }catch(IOException ioex){
            ioex.printStackTrace();
        }
        return "";
    }

    public String getDateFormat(){
        return "yyyyMMddHHmmssSSS";
    }

    public String getDelimeter(){
        return ":::::::amadeus:::::::";
    }

}