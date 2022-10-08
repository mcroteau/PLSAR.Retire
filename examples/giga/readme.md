Giga
====

### Run Me.

```mvn package jetty:run```

then browse to : 

```http://localhost:3000```

Signin : 

username : 

    $ 9079878652

password : 

    $ password


### Requirements

* Java 11 or newer
* Maven



### Fixes
Authenticated user shipment created fix on checkout
Stripe onboarding complete 
Affliate Signup
Cleanup



## Release 
clean properties file
create instead of create,drop
remove mock data
run it bare



## Todo:

already an account found
reset password
customer reset process
my account to store front
align categories right of price


#### backlog
pricing groups
product groups
disable affiliate site
delete affiliate site
url for business listing using id instead of uri (pages, category, item)
delete a category + items - if no sales are attached 
newsletter signup creates a prospect
update all items on import to specific category
sitemaps for category items view
sitemaps for item
sitemaps for pages
sitemaps for item


#### done
shopping cart shipping info require email!


### H2 Allow for External Connections
java -jar ~/.m2/repository/com/h2database/h2/1.4.200/h2-1.4.200.jar -tcpAllowOthers
