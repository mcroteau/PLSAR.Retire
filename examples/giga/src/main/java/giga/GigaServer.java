package giga;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//todo:11:33
public class GigaServer {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        /*
            ping each site getting a yes or no
            store the site
            scrape homepage
                -> get links
                -> get headings -> create 4-7 variance heading searches
                -> remove all html wrappers div, span, p, strong, b, em,
                -> go through document -> create 3 - 7 variance searches
                -> exclude copyright, privacy, site map
                -> get description

                search engine stats
                id | results_count | duration | time

                title variances
                id | site | variances

                site details
                id | site | description

                key words
                id | site | variance

                search variances
                id | site | variance

                site likes
                id | site | likes

                search stats
                id | user_id | search | site

                users
                id | phone | email

                user_sites
                id | user_id | site_id
         */

        getSiteContent("http://www.getongrazie.com");
    }


    public static void getSslSiteContent(String locationUrl) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = SSLContext.getInstance("SSL");

        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                System.out.println("getAcceptedIssuers =============");
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,
                                           String authType) {
                System.out.println("checkClientTrusted =============");
            }

            public void checkServerTrusted(X509Certificate[] certs,
                                           String authType) {
                System.out.println("checkServerTrusted =============");
            }
        } }, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(
                sslContext.getSocketFactory());

        HttpsURLConnection
                .setDefaultHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String arg0, SSLSession arg1) {
                        System.out.println("hostnameVerifier =============");
                        return true;
                    }
                });

        URL url = new URL(locationUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory() );
        connection.setRequestProperty("accept", "text/html");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(9701);
        connection.setInstanceFollowRedirects(true);
        int statusCode = connection.getResponseCode();
        InputStream inputStream = connection.getInputStream();

        List<Integer> redirectsCodes = Arrays.asList(new Integer[]{301, 302, 303, 308, 702});

        if(redirectsCodes.contains(statusCode)){
            String redirectLocation = connection.getHeaderField("Location");
            if(redirectLocation.startsWith("https:")){
                System.out.println("302ssl:" + redirectLocation);
                getSslSiteContent(redirectLocation);
            }else{
                System.out.println("302:" + redirectLocation);
                getSiteContent(redirectLocation);
            }
        }

        if (statusCode == 200) {

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            String output = result.toString("UTF-8");
            String document = cleanupWriteDocument(output);

            Path filePath = Paths.get("src", "main", "resources", "grazie.txt");
            File file = new File(filePath.toAbsolutePath().toString());
            if(!file.exists())file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(document);
            fileWriter.close();
        }

        connection.disconnect();
    }

    public static void getSiteContent(String locationUrl) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        URL url = new URL(locationUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "text/html");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(9701);
        connection.setInstanceFollowRedirects(true);
        int statusCode = connection.getResponseCode();
        InputStream inputStream = connection.getInputStream();

        if(statusCode == 301 || statusCode == 302 || statusCode == 702){
            String redirectLocation = connection.getHeaderField("Location");
            System.out.println(".'" + redirectLocation + "'");
            if(redirectLocation.startsWith("https:")){
                System.out.println("302ssl:" + redirectLocation);
                getSslSiteContent(redirectLocation);
            }else{
                System.out.println("302:" + redirectLocation);
                getSiteContent(redirectLocation);
            }
        }

        if (statusCode == 200) {

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            String output = result.toString("UTF-8");
            String document = cleanupWriteDocument(output);

            Path filePath = Paths.get("src", "main", "resources", "grazie.txt");
            File file = new File(filePath.toAbsolutePath().toString());
            if(!file.exists())file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(document);
            fileWriter.close();
        }

        connection.disconnect();
    }

    public static String cleanupWriteDocument(String document){
        String withContextPattern = "<a href=\"\\/([a-zA-Z0-9\\-\\_\\/]+){1,}\">([a-zA-Z0-9\\-\\_\\-\\s]+){1,}<\\/a>";
        String withoutContextPattern = "<a href=\"([a-zA-Z0-9\\-\\_\\/]+){1,}\">([a-zA-Z0-9\\-\\_\\-\\s]+){1,}<\\/a>";
        String contextCleanupDocument = removeHrefs(withContextPattern, document);
        String withoutContextCleanupDocument = removeHrefs(withoutContextPattern, contextCleanupDocument);
        return withoutContextCleanupDocument;
    }

    public static String removeHrefs(String hrefPattern, String document){
        Pattern withContextPattern = Pattern.compile(hrefPattern);
        Matcher urlMatcher = withContextPattern.matcher(document);
        while(urlMatcher.find()){
            String element = urlMatcher.group();
            System.out.println(element);
        }
        return document;
    }
}