package com.singtel.nsb.mobile.sync.config;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.GenericMessageListenerContainer;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.singtel.nsb.mobile.util.logger.ILogger;
import com.singtel.nsb.mobile.util.logger.LoggerFactory;

public class CompletableFutureReplyingKafkaTemplate<K, V, R> extends PartitionAwareReplyingKafkaTemplate<K, V, R>
		implements CompletableFutureReplyingKafkaOperations<K, V, R> {

	private static final ILogger log = LoggerFactory.getBusinessLogger(CompletableFutureReplyingKafkaTemplate.class);

	public CompletableFutureReplyingKafkaTemplate(ProducerFactory<K, V> producerFactory,
			GenericMessageListenerContainer<K, R> replyContainer) {
		super(producerFactory, replyContainer);
	}

	@Override
	public CompletableFuture<R> requestReplyDefault(K key, V value) {
		return adapt(sendAndReceiveDefault(key, value));
	}

	@Override
	public CompletableFuture<R> requestReplyWithHeaders(String requestTopic, V value, Map<String, String> headers) {
		return adapt(sendAndReceiveWithHeaders(requestTopic, value, headers));
	}

	private CompletableFuture<R> adapt(RequestReplyFuture<K, V, R> requestReplyFuture) {
		CompletableFuture<R> completableResult = new CompletableFuture<R>() {
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				boolean result = requestReplyFuture.cancel(mayInterruptIfRunning);
				super.cancel(mayInterruptIfRunning);
				return result;
			}
		};
		// Add callback to the request sending result
		requestReplyFuture.getSendFuture().addCallback(new ListenableFutureCallback<SendResult<K, V>>() {
			@Override
			public void onSuccess(SendResult<K, V> sendResult) {
				log.debug("Response Received within time.");
			}

			@Override
			public void onFailure(Throwable t) {
				completableResult.completeExceptionally(t);
			}
		});
		// Add callback to the reply
		requestReplyFuture.addCallback(new ListenableFutureCallback<ConsumerRecord<K, R>>() {
			@Override
			public void onSuccess(ConsumerRecord<K, R> result) {
				completableResult.complete(result.value());
			}

			@Override
			public void onFailure(Throwable t) {
				completableResult.completeExceptionally(t);
			}
		});
		return completableResult;
	}

}
