package com.tradingbot.tickerservice.config;
import com.mongodb.*;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.reactivestreams.client.MongoClient;

import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import static java.util.Collections.singletonList;

@Configuration
public class ReactiveMongoConfig extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Value("${spring.data.mongodb.username}")
    private String userName;

    @Value("${spring.data.mongodb.password}")
    private String passWord;

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Override
    public MongoClient reactiveMongoClient(){
        MongoCredential credential = MongoCredential.createCredential(userName, mongoDatabase, passWord.toCharArray());
        Block<ClusterSettings.Builder> localhost = builder -> builder.hosts(singletonList(new ServerAddress(host, 27017)));
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(localhost)
                .credential(credential)
                .build();
        return MongoClients.create(settings);
    }


    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(){
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }
}
