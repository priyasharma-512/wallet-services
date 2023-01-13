package com.example.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.Properties;

@Configuration
public class Config {


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
        //producer
        //serialising the key
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //serialising the value
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //now pass the bootstrap server
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        //consumer
        //deserialising the key
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //deserialising the value
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //now pass the bootstrap server
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        return properties;
    }

    @Bean
        //make an object of producer factory
    ProducerFactory<String ,String> getproducerFactory()
    {
        return new DefaultKafkaProducerFactory(kafkaProperties());
    }

    @Bean
    ConsumerFactory<String,String> getConsumerFactoy()
    {
        return new DefaultKafkaConsumerFactory(kafkaProperties());
    }

    //multiple consumers can consume concurrently from 1 topic
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory()
    {
        ConcurrentKafkaListenerContainerFactory<String ,String> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(getConsumerFactoy());
        return concurrentKafkaListenerContainerFactory;
    }

    @Bean
    KafkaTemplate<String,String> getKafkaTemplate()
    {
        return  new KafkaTemplate<>(getproducerFactory());
    }
}
