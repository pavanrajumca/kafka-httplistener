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
public class PatchRequestValidator implements Processor {

	private static final ILogger LOG = LoggerFactory.getBusinessLogger(PatchRequestValidator.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void process(Exchange exchange) throws Exception {
		LOG.info("Validating PATCH Request.");
		String baseRequest = exchange.getIn().getBody(String.class);
		LOG.debug("Base Request:{}", baseRequest);
		ApnConfigRequest request = objectMapper.readValue(baseRequest, ApnConfigRequest.class);
		RequestValidatorUtil.validateRequestId(request, HeaderConstants.PATCH);
		RequestValidatorUtil.validateServiceName(request, HeaderConstants.PATCH);
		RequestValidatorUtil.validateServiceCharacteristic(request, HeaderConstants.PATCH);
		RequestValidatorUtil.validateApnContextSvc(request, HeaderConstants.PATCH);
		RequestValidatorUtil.validateApnDefaultContextSvc(request, HeaderConstants.PATCH);
		LOG.info("Validation Successful.");
	}

}
