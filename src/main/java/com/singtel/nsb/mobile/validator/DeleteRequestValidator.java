package com.singtel.nsb.mobile.validator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.nsb.mobile.constants.HeaderConstants;
import com.singtel.nsb.mobile.model.ApnConfigRequest;
import com.singtel.nsb.mobile.util.RequestValidatorUtil;
import com.singtel.nsb.mobile.util.logger.ILogger;
import com.singtel.nsb.mobile.util.logger.LoggerFactory;

@Component
public class DeleteRequestValidator implements Processor {

	private static final ILogger LOG = LoggerFactory.getBusinessLogger(DeleteRequestValidator.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void process(Exchange exchange) throws Exception {
		LOG.info("Validating DELETE Request");
		String baseRequest = exchange.getIn().getBody(String.class);
		ApnConfigRequest request = objectMapper.readValue(baseRequest, ApnConfigRequest.class);
		RequestValidatorUtil.validateRequestId(request, HeaderConstants.DELETE);
		RequestValidatorUtil.validateServiceName(request, HeaderConstants.DELETE);
		LOG.info("Validation Successfull.");
	}

}
