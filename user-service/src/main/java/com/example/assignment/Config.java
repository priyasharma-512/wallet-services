package com.example.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Properties;

@Configuration
public class Config {

    @Bean
    LettuceConnectionFactory getConnection()
    {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
        return lettuceConnectionFactory;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate()
    {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //now we do serialisation for both key and value
        //serialising the key
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);

        //serialising the value
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);

        //since we pass User as HashMap we need to serialise hash also
        redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer);
        //set the connection in redis template
        redisTemplate.setConnectionFactory(getConnection());

        return redisTemplate;
    }

    //when we need to convert redis values to objects and viceversa we need object mapper
    @Bean
    ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }

    @Bean
    Properties kafkaProperties()
    {
        Properties properties = new Properties();
        //creating kafka properties for producer
        //firstly serialise the data since they are on producer side
        //serialising the key
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //serialising the value
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //now pass the bootstrap server
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        return properties;
    }

    //make an object of producer factory
    ProducerFactory<String ,String> getproducerFactory()
    {
        return new DefaultKafkaProducerFactory(kafkaProperties());
    }

    KafkaTemplate<String,String> getKafkaTemplate()
    {
        return  new KafkaTemplate<>(getproducerFactory());
    }
}
