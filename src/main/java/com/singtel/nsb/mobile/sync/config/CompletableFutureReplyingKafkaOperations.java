package com.singtel.nsb.mobile.sync.config;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CompletableFutureReplyingKafkaOperations<K, V, R> {
	
	CompletableFuture<R> requestReplyDefault(K key, V value);

	CompletableFuture<R> requestReplyWithHeaders(String requestTopic, V value, Map<String, String> headers);

}