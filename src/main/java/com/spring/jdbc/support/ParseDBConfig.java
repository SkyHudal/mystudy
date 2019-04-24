package com.spring.jdbc.support;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

public class ParseDBConfig {

    private static final String DRIVER = "DRIVER";
    private static final String URL = "URL";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    public static DriverManagerDataSource getDataSource(String dbPath) {
        InputStream in = ParseDBConfig.class.getClassLoader().getResourceAsStream(dbPath);
        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("load DB resources exception", e.getCause());
        }
        Set<String> propertyNames = properties.stringPropertyNames();
        propertyNames.forEach(property -> {
            if (property.toUpperCase(Locale.US).endsWith(DRIVER)) {
                driver = properties.getProperty(property);
            }
            if (property.toUpperCase(Locale.US).endsWith(URL)) {
                url = properties.getProperty(property);
            }
            if (property.toUpperCase(Locale.US).endsWith(USERNAME)) {
                username = properties.getProperty(property);
            }
            if (property.toUpperCase(Locale.US).endsWith(PASSWORD)) {
                password = properties.getProperty(property);
            }
        });
        if (driver == null || url == null || username == null || password == null) {
            throw new RuntimeException("db config have error,please check resources configuration!!!");
        }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }


}
