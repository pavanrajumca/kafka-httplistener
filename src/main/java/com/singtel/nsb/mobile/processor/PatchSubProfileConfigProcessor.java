package com.singtel.nsb.mobile.processor;

import static com.singtel.nsb.mobile.constants.HeaderConstants.CONSUMER;
import static com.singtel.nsb.mobile.constants.HeaderConstants.HTTP_METHOD;
import static com.singtel.nsb.mobile.constants.HeaderConstants.ID;
import static com.singtel.nsb.mobile.constants.HeaderConstants.NSB;
import static com.singtel.nsb.mobile.constants.HeaderConstants.PATCH;
import static com.singtel.nsb.mobile.constants.HeaderConstants.REQUEST_HEADERS;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.singtel.nsb.mobile.util.logger.ILogger;
import com.singtel.nsb.mobile.util.logger.LoggerFactory;

@Component
public class PatchSubProfileConfigProcessor implements Processor {

	private static final ILogger LOG = LoggerFactory.getBusinessLogger(PatchSubProfileConfigProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		LOG.info("PATCH API Processing Started");

		String imsi = exchange.getIn().getHeader(ID, String.class);
		LOG.info("Imsi{}", imsi);
		String request = exchange.getIn().getBody(String.class);
		LOG.info("PATCH Request:{}", request);

		Map<String, String> headers = new HashMap<>();
		headers.put(HTTP_METHOD, PATCH);
		headers.put(ID, imsi);
		headers.put(CONSUMER, NSB);

		exchange.getIn().setHeader(REQUEST_HEADERS, headers);
	}

}
