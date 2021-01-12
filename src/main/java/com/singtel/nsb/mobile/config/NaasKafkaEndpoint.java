package com.singtel.nsb.mobile.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.nsb.mobile.sync.config.CompletableFutureReplyingKafkaOperations;
import com.singtel.nsb.mobile.sync.config.CompletableFutureReplyingKafkaTemplate;

@Configuration
@EnableKafka
public class NaasKafkaEndpoint {

	@Value("${kafka.broker}")
	private String bootstrapServers;
	@Value("${kafka.group-id}")
	private String groupId;
	@Value("${kafka.producer.topic}")
	private String producerTopic;
	@Value("${kafka.consumer.topic}")
	private String replyTpoic;
	@Value("${kafka.request-reply.timeout-ms}")
	private Long replyTimeout;

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

		return props;
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		return props;
	}
	
	@Bean
	public CompletableFutureReplyingKafkaOperations<String, String, String> syncKafkaTemplate() {
		CompletableFutureReplyingKafkaTemplate<String, String, String> requestReplyKafkaTemplate = new CompletableFutureReplyingKafkaTemplate<>(
				requestProducerFactory(), getReplyListenerContainer());
		requestReplyKafkaTemplate.setDefaultTopic(producerTopic);
		requestReplyKafkaTemplate.setReplyTimeout(replyTimeout);
		return requestReplyKafkaTemplate;
	}
	
	@Bean
	public ProducerFactory<String, String> requestProducerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public ConsumerFactory<String, String> baseReplyConsumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new StringDeserializer());
	}

	@Bean
	public KafkaMessageListenerContainer<String, String> getReplyListenerContainer() {
		ContainerProperties containerProperties = new ContainerProperties(replyTpoic);
		return new KafkaMessageListenerContainer<>(baseReplyConsumerFactory(), containerProperties);
	}
	
	@Bean
	public KafkaAdmin admin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic replyTopic() {
		Map<String, String> configs = new HashMap<>();
		configs.put("retention.ms", replyTimeout.toString());
		return new NewTopic(replyTpoic, 2, (short) 2).configs(configs);
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
}
