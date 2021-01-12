package com.singtel.nsb.mobile.util;

import org.apache.camel.Exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.nsb.framework.exception.NsbException;
import com.singtel.nsb.framework.exception.base.NsbErrorCode;
import com.singtel.nsb.mobile.model.Error;

public class ErrorUtil {

	private ErrorUtil() {

	}

	public static String constructError(Exchange exchange) throws JsonProcessingException {
		Object exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
		NsbException nsbException = (NsbException) exception;
		Error error = new Error();
		error.setCode(1010);
		error.setMessage(nsbException.getErrorDescription());
		error.setOrderId(nsbException.getTransactionId());
		error.setReason(NsbErrorCode.INVALID_REQUEST_PAYLOAD.toString());
		error.setState("FAILED");
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(error);
	}

}
