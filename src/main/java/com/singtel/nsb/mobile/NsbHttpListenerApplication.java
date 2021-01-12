package com.singtel.nsb.mobile;

import org.apache.camel.opentracing.starter.CamelOpenTracing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;

@CamelOpenTracing
@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, KafkaAutoConfiguration.class})
@EnableKafka
public class NsbHttpListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NsbHttpListenerApplication.class);
	}

}