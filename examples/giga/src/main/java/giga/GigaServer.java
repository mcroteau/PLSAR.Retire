package plsar;

import net.plsar.PLSAR;
import net.plsar.PersistenceConfig;
import net.plsar.SchemaConfig;
import net.plsar.drivers.Drivers;
import net.plsar.environments.Environments;
import net.plsar.security.renderer.AuthenticatedRenderer;
import net.plsar.security.renderer.GuestRenderer;
import net.plsar.security.renderer.UserRenderer;
import plsar.assist.AuthSecurityAccess;

public class Main {
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
                id | search | site

                users
                id | phone | email

                user_sites
                id | user_id | site_id
         */



    }
}