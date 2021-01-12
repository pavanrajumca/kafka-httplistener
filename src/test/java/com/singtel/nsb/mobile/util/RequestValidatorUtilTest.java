package com.singtel.nsb.mobile.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.nsb.framework.exception.NsbException;
import com.singtel.nsb.mobile.constants.HeaderConstants;
import com.singtel.nsb.mobile.model.ApnConfigRequest;
import com.singtel.nsb.mobile.model.ServiceCharacteristic;

public class RequestValidatorUtilTest {

	@Test(expected = NsbException.class)
	public void given_ApnConfigRequest_Without_RequestId_Then_Throw_NsbException()
			throws JsonParseException, JsonMappingException, IOException, NsbException {
		ApnConfigRequest request = getApnConfigRequest();
		request.setRequestId(null);
		RequestValidatorUtil.validateRequestId(request, HeaderConstants.PATCH);
	}

	@Test(expected = NsbException.class)
	public void given_ApnConfigRequest_Without_ServiceName_Then_Throw_NsbException()
			throws JsonParseException, JsonMappingException, IOException, NsbException {
		ApnConfigRequest request = getApnConfigRequest();
		request.setName(null);
		RequestValidatorUtil.validateServiceName(request, HeaderConstants.PATCH);
	}

	@Test(expected = NsbException.class)
	public void given_ApnConfigRequest_Without_ServiceCharacteristic_Then_Throw_NsbException()
			throws JsonParseException, JsonMappingException, IOException, NsbException {
		ApnConfigRequest request = getApnConfigRequest();
		request.setServiceCharacteristic(null);
		RequestValidatorUtil.validateServiceCharacteristic(request, HeaderConstants.PATCH);
	}

	@Test(expected = NsbException.class)
	public void given_ApnConfigRequest_With_Invalid_ApnContextId_Then_Throw_NsbException()
			throws JsonParseException, JsonMappingException, IOException, NsbException {
		ApnConfigRequest request = getApnConfigRequest();
		ServiceCharacteristic apnCtxSvc = request.getServiceCharacteristic().stream()
				.filter(svc -> StringUtils.equalsIgnoreCase("apnContext", svc.getName())).findAny().get();
		apnCtxSvc.setValue("A12");
		RequestValidatorUtil.validateApnContextSvc(request, HeaderConstants.PATCH);
	}

	@Test(expected = NsbException.class)
	public void given_ApnConfigRequest_With_Invalid_ApnDefaultContextId_Then_Throw_NsbException()
			throws JsonParseException, JsonMappingException, IOException, NsbException {
		ApnConfigRequest request = getApnConfigRequest();
		ServiceCharacteristic apnDefaultCtxSvc = request.getServiceCharacteristic().stream()
				.filter(svc -> StringUtils.equalsIgnoreCase("apnDefaultContext", svc.getName())).findAny().get();
		apnDefaultCtxSvc.setValue("A121");
		RequestValidatorUtil.validateApnDefaultContextSvc(request, HeaderConstants.PATCH);
	}

	@Test(expected = NsbException.class)
	public void given_ApnConfigRequest_Without_Imsi_Then_Throw_NsbException()
			throws JsonParseException, JsonMappingException, IOException, NsbException {
		ApnConfigRequest request = getApnConfigRequest();
		ServiceCharacteristic apnDefaultCtxSvc = request.getServiceCharacteristic().stream()
				.filter(svc -> StringUtils.equalsIgnoreCase("imsi", svc.getName())).findAny().get();
		apnDefaultCtxSvc.setValue(null);
		RequestValidatorUtil.validateImsiSvc(request, HeaderConstants.POST);
	}

	@Test(expected = NsbException.class)
	public void given_ApnConfigRequest_With_Invalid_MaxUplinkIpFlow_Then_Throw_NsbException()
			throws JsonParseException, JsonMappingException, IOException, NsbException {
		ApnConfigRequest request = getApnConfigRequest();
		ServiceCharacteristic apnDefaultCtxSvc = request.getServiceCharacteristic().stream()
				.filter(svc -> StringUtils.equalsIgnoreCase("maxUplinkIpFlow", svc.getName())).findAny().get();
		apnDefaultCtxSvc.setValue("1A");
		RequestValidatorUtil.validateMaxUplink(request, HeaderConstants.PATCH);
	}

	@Test(expected = NsbException.class)
	public void given_ApnConfigRequest_With_Invalid_MaxDownlinkIpFlow_Then_Throw_NsbException()
			throws JsonParseException, JsonMappingException, IOException, NsbException {
		ApnConfigRequest request = getApnConfigRequest();
		ServiceCharacteristic apnDefaultCtxSvc = request.getServiceCharacteristic().stream()
				.filter(svc -> StringUtils.equalsIgnoreCase("maxDownlinkIpFlow", svc.getName())).findAny().get();
		apnDefaultCtxSvc.setValue("2A");
		RequestValidatorUtil.validateMaxDownlink(request, HeaderConstants.PATCH);
	}

	public ApnConfigRequest getApnConfigRequest() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		String request = "{\"requestId\": \"00147952_500828\",\"name\": \"mobileActivate\",\"serviceCharacteristic\": [{\"name\": \"imsi\",\"value\": \"525016143034299\"},{\"name\": \"profileId\",\"value\": \"HSS-EsmDefaultAutomaticProfile\"},{\"name\": \"userType\",\"value\": \"epcEMBB\"},{\"name\": \"apnContext\",\"value\": \"644\"},{\"name\": \"apnDefaultContext\",\"value\": \"644\"},{\"name\": \"maxUplinkIpFlow\",\"value\": \"100000000\"},{\"name\": \"maxDownlinkIpFlow\",\"value\": \"500000000\"}]}";
		return objectMapper.readValue(request, ApnConfigRequest.class);
	}

}
