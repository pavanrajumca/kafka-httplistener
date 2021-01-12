package com.singtel.nsb.mobile.sync.config;

import java.util.Map;

import org.springframework.kafka.requestreply.ReplyingKafkaOperations;
import org.springframework.kafka.requestreply.RequestReplyFuture;

public interface PartitionAwareReplyingKafkaOperations<K, V, R> extends ReplyingKafkaOperations<K, V, R> {

	RequestReplyFuture<K, V, R> sendAndReceiveDefault(K key, V data);
	
	RequestReplyFuture<K, V, R> sendAndReceive(String topic, K key, V data);
	
	RequestReplyFuture<K, V, R> sendAndReceiveWithHeaders(String topic, V data, Map<String, String> headers);

}