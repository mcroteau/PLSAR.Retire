package dev.blueocean.resources;

import dev.blueocean.BlueOceanException;
import dev.blueocean.RouteEndpoint;
import dev.blueocean.ViewConfig;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerResources {

    public Object getInstance(Class<?> klass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object object = klass.getConstructor().newInstance();
        return object;
    }

    public String getGuid(int n) {
        String CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
        StringBuilder uuid = new StringBuilder();
        int divisor = n/6;
        Random rnd = new Random();
        for(int z = 0; z < n;  z++) {
            if( z % divisor == 0 && z > 0) {
                uuid.append("-");
            }
            int index = (int) (rnd.nextFloat() * CHARS.length());
            uuid.append(CHARS.charAt(index));
        }
        return uuid.toString();
    }

    public Long getTime(int days){
        LocalDateTime ldt = LocalDateTime.now().minusDays(days);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(this.getDateFormat());
        String date = dtf.format(ldt);
        return Long.valueOf(date);
    }

    public String getCookie(Map<String, String> headers){
        String value = null;
        String sessionId = getSessionId();
        String cookies = headers.get("cookie");
        if(cookies != null) {
            String[] bits = cookies.split(";");
            for (String completes : bits) {
                String[] parts = completes.split("=");
                String key = parts[0].trim();
                if (parts.length == 2) {
                    if (key.equals(sessionId)) {
                        value = parts[1].trim();
                    }
                }
            }
        }
        return value;
    }

    public String getSessionId() {
        return "blueocean.sessions";
    }

    public String getDateFormat() {
        return "yyyyMMddHHmmssSSS";
    }

    private String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

    public String getRedirect(String uri){
        String[] redirectBits = uri.split(":");
        if(redirectBits.length > 1)return redirectBits[1];
        return null;
    }

    public ByteArrayOutputStream getViewFileCopy(String viewKey, ConcurrentMap<String, byte[]> viewBytesMap) {
        if(viewBytesMap.containsKey(viewKey)){
            byte[] fileBytes = viewBytesMap.get(viewKey);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int bytesRead;
            try {
                while ((bytesRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
                    outputStream.write(bytes, 0, bytesRead);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException ex) {
            }
            return outputStream;
        }
        return null;
    }

    public ConcurrentMap<String, byte[]> getViewBytesMap(ViewConfig viewConfig) throws BlueOceanException, FileNotFoundException {
        ConcurrentMap<String, byte[]> viewFilesBytesMap = new ConcurrentHashMap<>();

        Path viewsPath = viewConfig.getViewsPath();
        File viewsDirectory = new File(viewsPath.toString());
        if(!viewsDirectory.isDirectory()){
            throw new BlueOceanException(viewConfig.getViewsPath() + " is not a directory");
        }

        File[] viewFiles = viewsDirectory.listFiles();
        ConcurrentMap<String, byte[]> viewFilesMap = getFileBytesMap(viewFiles, viewConfig);

        Path resourcesPath = viewConfig.getResourcesPath();
        File resourcesDirectory = new File(resourcesPath.toString());
        if(!resourcesDirectory.isDirectory()){
            throw new BlueOceanException(viewConfig.getResourcesPath() + " is not a directory");
        }

        File[] resourceFiles = resourcesDirectory.listFiles();
        ConcurrentMap<String, byte[]> resourceFilesMap = getFileBytesMap(resourceFiles, viewConfig);


        viewFilesBytesMap.putAll(viewFilesMap);
        viewFilesBytesMap.putAll(resourceFilesMap);

        return viewFilesBytesMap;
    }

    ConcurrentMap<String, byte[]> getFileBytesMap(File[] viewFiles, ViewConfig viewConfig) throws FileNotFoundException {
        ConcurrentMap<String, byte[]> viewFilesBytesMap = new ConcurrentHashMap<>();
        for (File viewFile : viewFiles) {

            if(viewFile.isDirectory()){
                getFileBytesMap(viewFile.listFiles(), viewConfig);
            }

            if(!viewFile.getName().endsWith(viewConfig.getViewExtension()))continue;
            InputStream fileInputStream = new FileInputStream(viewFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int bytesRead;
            try {
                while ((bytesRead = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
                    outputStream.write(bytes, 0, bytesRead);
                }

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                byte[] viewFileBytes = outputStream.toByteArray();
                String viewKey = viewFile.toString().replace("src/main/webapp/", "");
                viewFilesBytesMap.put(viewKey, viewFileBytes);

            } catch (IOException ex) {
            }
        }
        return viewFilesBytesMap;
    }
}
