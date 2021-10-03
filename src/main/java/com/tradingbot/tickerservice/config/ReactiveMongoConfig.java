package com.tradingbot.tickerservice.config;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
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
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;



@Configuration
public class ReactiveMongoConfig extends AbstractReactiveMongoConfiguration {
    @Value("${spring.mongodb.username}")
    private String username;
    @Value("${spring.mongodb.password}")
    private String password;

    @Override
    public MongoClient reactiveMongoClient(){ConnectionString connString = new ConnectionString("mongodb+srv://"+username+":"+password+"@cluster0.hncbk.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabase mongoDatabase(){
        return reactiveMongoClient().getDatabase("myFirstDatabase");
    }
    @Override
    protected String getDatabaseName() {
        return "myFirstDatabase";
    }
}
