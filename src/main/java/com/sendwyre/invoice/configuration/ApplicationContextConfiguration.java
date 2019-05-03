package com.sendwyre.invoice.configuration;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;

@Configuration
public class ApplicationContextConfiguration {

    @Value("${embedded-redis.port}")
    private int redisPort;

    @Value("${embedded-redis.store-location}")
    private String storeLocation;

    @Autowired
    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() {
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
    }

    @Bean
    @SneakyThrows
    public RedisServer redisServer() {
        String redisDir = new File(System.getProperty("user.home"), storeLocation).getAbsolutePath();
        return RedisServer.builder()
                .redisExecProvider(RedisExecProvider.defaultProvider())
                .port(redisPort)
                .setting("save 5 1")
                .setting("dbfilename rbridgedump.rdb")
                .setting("dir " + redisDir)
                .build();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public RedisAtomicLong invoiceNumberCounter(JedisConnectionFactory connectionFactory) {
        return new RedisAtomicLong("invoiceNumberCounter", connectionFactory);
    }
}
