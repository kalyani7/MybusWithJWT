package com.mybus.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mybus.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@Profile("test")
@EnableMongoRepositories(basePackages = "com.mybus")
@PropertySource(name = "mongoProperties", value = "classpath:test-mongo-config.properties")
@EnableMongoAuditing

public class ApplicationDataTestConfig extends AbstractMongoConfiguration {

    @Autowired
    private SystemProperties systemProperties;

    @Override
    public String getDatabaseName(){
        return systemProperties.getProperty("mongodb.test.database");
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ServerAddress serverAddress = new ServerAddress(systemProperties.getProperty("mongodb.host"));
        MongoClientOptions options = new MongoClientOptions.Builder().build();
        return new MongoClient(serverAddress, options);
    }
}
