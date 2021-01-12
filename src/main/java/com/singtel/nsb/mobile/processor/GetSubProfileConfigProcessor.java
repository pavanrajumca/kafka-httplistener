package com.singtel.nsb.mobile.processor;

import static com.singtel.nsb.mobile.constants.HeaderConstants.CONSUMER;
import static com.singtel.nsb.mobile.constants.HeaderConstants.EXTERNAL_TRANSACTION_ID;
import static com.singtel.nsb.mobile.constants.HeaderConstants.GET;
import static com.singtel.nsb.mobile.constants.HeaderConstants.HTTP_METHOD;
import static com.singtel.nsb.mobile.constants.HeaderConstants.ID;
import static com.singtel.nsb.mobile.constants.HeaderConstants.NAME;
import static com.singtel.nsb.mobile.constants.HeaderConstants.NSB;
import static com.singtel.nsb.mobile.constants.HeaderConstants.REQUEST_HEADERS;
import static com.singtel.nsb.mobile.constants.HeaderConstants.SERVICE_NAME;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.singtel.nsb.mobile.util.logger.ILogger;
import com.singtel.nsb.mobile.util.logger.LoggerFactory;

@Component
public class GetSubProfileConfigProcessor implements Processor {

	private ILogger logger = LoggerFactory.getBusinessLogger(GetSubProfileConfigProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("GET API Processing Started.");
		String apnId = exchange.getIn().getHeader(ID, String.class);
		String serviceName = exchange.getIn().getHeader(NAME, String.class);
		String reqId = exchange.getIn().getHeader(EXTERNAL_TRANSACTION_ID, String.class);

		logger.info("SubscriberProfile IMSI:{}", apnId);
		logger.info("Service name:{}", serviceName);
		logger.info("ExternalTransactionId:{}", reqId);

		Map<String, String> headers = new HashMap<>();
		headers.put(HTTP_METHOD, GET);
		headers.put(SERVICE_NAME, serviceName);
		headers.put(EXTERNAL_TRANSACTION_ID, reqId);
		headers.put(CONSUMER, NSB);
		headers.put(ID, apnId);

		exchange.getIn().setBody(apnId);
		exchange.getIn().setHeader(REQUEST_HEADERS, headers);
	}

}
