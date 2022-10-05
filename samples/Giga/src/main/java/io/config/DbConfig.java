package io.config;

import io.support.Papi;
import qio.annotate.Config;
import qio.annotate.Dependency;
import qio.annotate.Property;
import qio.jdbc.BasicDataSource;

import javax.sql.DataSource;

@Config
public class DbConfig {

    @Property("db.url")
    String url;

    @Property("db.user")
    String user;

    @Property("db.pass")
    String pass;

    @Property("db.driver")
    String driver;

    @Dependency
    public DataSource dataSource(){
        return new Papi.New()
                .connections(20)
                .driver(driver)
                .url(url)
                .user(user)
                .password(pass)
                .make();
    }

//    @Dependency
//    public DataSource dataSource(){
//        return new BasicDataSource.Builder()
//                .driver(driver)
//                .url(url)
//                .username(user)
//                .password(pass)
//                .build();
//    }

}
