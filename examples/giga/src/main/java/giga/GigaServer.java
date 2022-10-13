package giga;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

        ExecutorService executors = Executors.newFixedThreadPool(1);
        executors.execute(new SiteInterpreter("http://www.getongrazie.com", executors));
    }

//todo: reuben st. amad
    public static class SiteInterpreter implements Runnable{

        String siteUrl;
        ExecutorService executors;

        public SiteInterpreter(String siteUrl, ExecutorService executors){
            this.siteUrl = siteUrl;
            this.executors = executors;
        }

        @Override
        public void run() {

            try {

                URL url = new URL(siteUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("accept", "text/html");
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(9701);
                connection.setInstanceFollowRedirects(true);
                int statusCode = connection.getResponseCode();
                InputStream inputStream = connection.getInputStream();

                List<Integer> redirectsCodes = Arrays.asList(new Integer[]{301, 302, 303, 308, 702});

                if(redirectsCodes.contains(statusCode)){
                    String redirectSite = connection.getHeaderField("Location");
                    if(redirectSite.startsWith("https:")){
                        System.out.println("302ssl:" + redirectSite);
                        executors.execute(new SslSiteInterpreter(redirectSite, executors));
                    }else{
                        System.out.println("302:" + redirectSite);
                        executors.execute(new SiteInterpreter(redirectSite, executors));
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

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static class SslSiteInterpreter implements Runnable{

        String siteUrl;
        ExecutorService executors;

        public SslSiteInterpreter(String siteUrl, ExecutorService executors){
            this.siteUrl = siteUrl;
            this.executors = executors;
        }

        @Override
        public void run() {

            try {

                SSLContext sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, new TrustManager[] { new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs,
                                                   String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs,
                                                   String authType) { }
                } }, new SecureRandom());


                HttpsURLConnection.setDefaultSSLSocketFactory(
                        sslContext.getSocketFactory());

                HttpsURLConnection
                        .setDefaultHostnameVerifier(new HostnameVerifier() {
                            public boolean verify(String arg0, SSLSession arg1) {
                                return true;
                            }
                        });

                URL url = new URL(siteUrl);
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
                    String redirectSite = connection.getHeaderField("Location");
                    if(redirectSite.startsWith("https:")){
                        System.out.println("302ssl:" + redirectSite);
                        executors.execute(new SslSiteInterpreter(redirectSite, executors));
                    }else{
                        System.out.println("302:" + redirectSite);
                        executors.execute(new SiteInterpreter(redirectSite, executors));
                    }
                }

                if (statusCode == 200) {

                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    for (int length; (length = inputStream.read(buffer)) != -1; ) {
                        result.write(buffer, 0, length);
                    }
                    String output = result.toString("UTF-8");

                    ConcurrentMap<String, Boolean> refs = getDocumentRefs(output);
                    for(Map.Entry<String, Boolean> refEntry : refs.entrySet()){
                        String link = refEntry.getKey();
                        if(!refs.get(link) && !link.startsWith("http:") && !link.startsWith("https:")){
                            String ref = siteUrl + link;
                            executors.execute(new SiteInterpreter(ref, executors));
                            refs.put(link, true);
                        }
                    }
                    String document = cleanupWriteDocument(output);

                    Path filePath = Paths.get("src", "main", "resources", "grazie.txt");
                    File file = new File(filePath.toAbsolutePath().toString());
                    if(!file.exists())file.createNewFile();

                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(document);
                    fileWriter.close();
                }

                connection.disconnect();

            } catch (KeyManagementException | NoSuchAlgorithmException | ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static ConcurrentMap<String, Boolean> getDocumentRefs(String document){
        ConcurrentMap<String, Boolean> refs = new ConcurrentHashMap<>();
        Pattern refPattern = Pattern.compile("href=\"[\\w\\W]+?\"");
        Matcher refMatcher = refPattern.matcher(document);
        while(refMatcher.find()){
            String ref = refMatcher.group();
            String link = ref.replace("href=\"", "")
                    .replace("\"", "");
            refs.put(link, false);
        }
        return refs;
    }


    public static String cleanupWriteDocument(String document){
        String stylePattern = "(<style>[\\w\\W\\s\\n]+?<\\/style>)|(<style\\s[\\w\\W]+?>[\\w\\W\\s\\n]+?<\\/style>)";
        String removedStyleDocument = replaceAttributes(stylePattern, document);

        String scriptPattern = "(<script>[\\w\\W\\s\\n]+?<\\/script>)|(<script\\s[\\w\\W]+?>[\\w\\W\\s\\n]+?<\\/script>)";
        String removedScriptDocument = replaceAttributes(scriptPattern, removedStyleDocument);

        String elementsPattern = "<[\\w]+>";
        String removedElementsDocument = replaceAttributes(elementsPattern, removedScriptDocument);

        String elementsWithAttributes = "<[\\w]+\\s[\\w\\W]+?>";
        String removedElementsWithAttributesDocument = replaceAttributes(elementsWithAttributes, removedElementsDocument);

        String endElementsPattern = "(</[\\w]+>)|(<[\\w]+/>)";
        String removedEndElementsDocument = replaceAttributes(endElementsPattern, removedElementsWithAttributesDocument);

        String documentPre = replaceAttributes("(\\s{2})|(\n{2})|(\r{2})", removedEndElementsDocument);
        String documentReady = createNewLines(documentPre);

        return documentReady;
    }

    static String replaceAttributes(String attributesPattern, String document) {
        Pattern withContextPattern = Pattern.compile(attributesPattern);
        Matcher urlMatcher = withContextPattern.matcher(document);
        while(urlMatcher.find()){
            String element = urlMatcher.group();
            String replace = escapeMetaCharacters(element);
            document = document.replaceAll(replace, " ");
        }
        return document;
    }

    static String createNewLines(String document) {
        Pattern withContextPattern = Pattern.compile("(\\.)|(\\!)");
        Matcher urlMatcher = withContextPattern.matcher(document);
        while(urlMatcher.find()){
            String element = urlMatcher.group();
            String replace = escapeMetaCharacters(element);
            document = document.replaceAll(replace, "\n").trim();
        }
        return document;
    }

    public static String escapeMetaCharacters(String inputString){
        final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};

        for (int i = 0 ; i < metaCharacters.length ; i++){
            if(inputString.contains(metaCharacters[i])){
                inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
            }
        }
        return inputString;
    }
}