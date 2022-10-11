package giga;

import java.net.HttpURLConnection;
import java.net.URL;

//todo:11:33
public class GigaServer {
    public static void main(String[] args) {
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

        URL url = new URL(project.getUri());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(6701);
        int statusCode = connection.getResponseCode();
        connection.disconnect();

    }


    
}