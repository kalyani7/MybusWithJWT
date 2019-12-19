package com.mybus.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mybus.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

@Configuration
@EnableMongoRepositories(basePackages = "com.mybus")
@EnableMongoAuditing
public class MongodbDataSourceConfig extends AbstractMongoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MongodbDataSourceConfig.class);

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private Environment env;

    @Override
    public String getDatabaseName(){
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            logger.info("************* Using db name "+ systemProperties.getProperty("mongodb.test.database"));
            return systemProperties.getProperty("mongodb.test.database");
        } else {
            logger.info("************* Using db name "+ systemProperties.getProperty("mongodb.database"));

            return systemProperties.getProperty("mongodb.database");
        }
    }

    /*@Override
    @Bean
    public MongoClient mongoClient() {
        ServerAddress serverAddress = new ServerAddress(systemProperties.getProperty("mongodb.host"));
        MongoClientOptions options = new MongoClientOptions.Builder().build();
        return new MongoClient(serverAddress, options);
    }*/

    @Override
    @Bean
    public MongoClient mongoClient() {
        ServerAddress serverAddress = new ServerAddress(systemProperties.getProperty("mongodb.host"));
        MongoClientOptions options = new MongoClientOptions.Builder().build();
        String userName = systemProperties.getProperty("mongodb.username");
        String password = systemProperties.getProperty("mongodb.password");
        if (userName != null && password != null && !userName.trim().isEmpty() && !password.trim().isEmpty()) {
            logger.info("******* using the authentication credentials for mongo ***************");
            return new MongoClient(serverAddress,
                    MongoCredential.createCredential(userName, getDatabaseName(), password.toCharArray()), options);
        } else {
            logger.info("**** no authentication found " + userName +"   " + password);
            return new MongoClient(serverAddress, options);
        }

    }


}