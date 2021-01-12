package com.singtel.nsb.mobile.processor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.singtel.nsb.mobile.constants.HeaderConstants;
import com.singtel.nsb.mobile.constants.StatusCodes;
import com.singtel.nsb.mobile.sync.config.CompletableFutureReplyingKafkaOperations;
import com.singtel.nsb.mobile.util.logger.ILogger;
import com.singtel.nsb.mobile.util.logger.LoggerFactory;

@Component
public class ProducerProccessor implements Processor {

	private ILogger logger = LoggerFactory.getBusinessLogger(ProducerProccessor.class);

	@Value("${kafka.producer.topic}")
	private String producerTopic;
	@Value("${kafka.consumer.topic}")
	private String replyTopic;
	@Autowired
	private CompletableFutureReplyingKafkaOperations<String, String, String> syncKafkaTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		String request = exchange.getIn().getBody(String.class);
		Map<String, String> headers = exchange.getIn().getHeader(HeaderConstants.REQUEST_HEADERS, Map.class);
		try {
			CompletableFuture<String> result = syncKafkaTemplate.requestReplyWithHeaders(producerTopic, request, headers);
			String response = result.get();
			logger.info("Response Received: {}", response);
			exchange.getIn().setBody(response);
			if(StringUtils.contains(response, "\"FAILED\"")) {
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
			} else {
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCodes.getStatusCode(headers.get(HeaderConstants.HTTP_METHOD)));
			}
		} catch (Exception ex) {
			logger.error("Timeout Error", ex);
			ObjectNode errorObject = objectMapper.createObjectNode();
			errorObject.put("code", "error");
			errorObject.put("description", "Something went wrong. Please try after sometime.");
			exchange.getIn().setBody(objectMapper.writeValueAsString(errorObject));
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
