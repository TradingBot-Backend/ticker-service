package com.tradingbot.tickerservice.config;

import com.tradingbot.tickerservice.ticker.domain.Ticker;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class TickerProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;
    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String JAAS_CONFIG;
    @Value("${spring.kafka.security.protocol}")
    private String SECURITY_PROTOCOL;
    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String SASL_MECHANISM;
    @Value("${spring.kafka.ssl.endpoint}")
    private String SSL_ENDPOINT;


    @Bean
    public DirectChannel producerChannel() {
        return new DirectChannel();
    }

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,SECURITY_PROTOCOL);
        properties.put(SaslConfigs.SASL_JAAS_CONFIG,JAAS_CONFIG);
        properties.put(SaslConfigs.SASL_MECHANISM,SASL_MECHANISM);
        properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SSL_ENDPOINT);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        return properties;
    }

    @Bean
    public ProducerFactory<String, Ticker> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, Ticker> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
