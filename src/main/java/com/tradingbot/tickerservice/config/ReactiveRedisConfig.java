package com.tradingbot.tickerservice.config;

import com.tradingbot.tickerservice.ticker.domain.Ticker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.Transactional;


@Configuration
public class ReactiveRedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
   /* @Value("${spring.redis.password}")
    private String password;*/



    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host,port);
        //redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfigurationBuilder.build());
    }
    @Bean
    ReactiveHashOperations<String, String, String> reactiveHashOperations(){
        RedisSerializationContext.RedisSerializationContextBuilder<String, Ticker> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, Ticker> context = builder.build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory(),context).opsForHash();
    }


    @Bean
    ReactiveHashOperations<String, String, Ticker> reactiveTickerHashOperations(){
        Jackson2JsonRedisSerializer<Ticker> serializer = new Jackson2JsonRedisSerializer<>(Ticker.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Ticker> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, Ticker> context = builder.hashValue(serializer).build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory(), context).opsForHash(context);
    }
}
