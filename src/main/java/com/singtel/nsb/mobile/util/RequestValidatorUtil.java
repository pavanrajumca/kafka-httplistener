package com.singtel.nsb.mobile.util;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.singtel.nsb.framework.exception.NsbException;
import com.singtel.nsb.framework.exception.NsbExceptionBuilder;
import com.singtel.nsb.framework.exception.base.NsbErrorCode;
import com.singtel.nsb.framework.exception.base.NsbErrorType;
import com.singtel.nsb.mobile.model.ApnConfigRequest;
import com.singtel.nsb.mobile.model.ServiceCharacteristic;

public class RequestValidatorUtil {

	private RequestValidatorUtil() {

	}

	public static NsbException throwNsbException(String service, String uuid, String transactionId, String errorMessage)
			throws NsbException {
		throw new NsbExceptionBuilder(service, NsbErrorType.BUSINESS, uuid, transactionId)
				.causeByNsbDefinedError(NsbErrorCode.INVALID_REQUEST_PAYLOAD, errorMessage)
				.withHttpResponse(400, errorMessage).buildException();
	}

	public static void validateRequestId(ApnConfigRequest request, String service) throws NsbException {
		String requestId = Optional.ofNullable(request).map(ApnConfigRequest::getRequestId).orElse(null);
		if (StringUtils.isEmpty(requestId)) {
			throwNsbException(service, UUID.randomUUID().toString(), null, "RequestId missing");
		}
	}

	public static void validateServiceName(ApnConfigRequest request, String service) throws NsbException {
		String name = Optional.ofNullable(request).map(ApnConfigRequest::getName).orElse(null);
		if (StringUtils.isEmpty(name)) {
			throwNsbException(service, UUID.randomUUID().toString(), request.getRequestId(), "ServiceName missing");
		}
	}

	public static void validateServiceCharacteristic(ApnConfigRequest request, String service) throws NsbException {
		List<ServiceCharacteristic> svcs = Optional.ofNullable(request).map(ApnConfigRequest::getServiceCharacteristic)
				.orElse(null);
		if (CollectionUtils.isEmpty(svcs)) {
			throwNsbException(service, UUID.randomUUID().toString(), request.getRequestId(),
					"ServiceCharacteristics missing");
		}
	}

	public static void validateApnContextSvc(ApnConfigRequest request, String service) throws NsbException {
		ServiceCharacteristic apnCtxSvc = getSvc("apnContext", request);
		if (null != apnCtxSvc && StringUtils.isNotEmpty(apnCtxSvc.getValue())) {
			try {
				Stream.of(apnCtxSvc.getValue().split(",")).map(Integer::parseInt).collect(Collectors.toList());
			} catch (Exception ex) {
				throwNsbException(service, UUID.randomUUID().toString(), request.getRequestId(),
						"Invalid ApnContextId provided");
			}
		}
	}

	public static void validateApnDefaultContextSvc(ApnConfigRequest request, String service) throws NsbException {
		ServiceCharacteristic apnDefaultCtxSvc = getSvc("apnDefaultContext", request);
		if (isNotNumeric(apnDefaultCtxSvc)) {
			throwNsbException(service, UUID.randomUUID().toString(), request.getRequestId(),
					"Invalid ApnDefaultContextId provided");
		}
	}

	public static void validateImsiSvc(ApnConfigRequest request, String service) throws NsbException {
		ServiceCharacteristic imsiCtxSvc = getSvc("imsi", request);
		if (null == imsiCtxSvc || StringUtils.isEmpty(imsiCtxSvc.getValue())) {
			throwNsbException(service, UUID.randomUUID().toString(), request.getRequestId(), "IMSI missing");
		}
	}

	public static void validateMaxUplink(ApnConfigRequest request, String service) throws NsbException {
		ServiceCharacteristic maxUplinkSvc = getSvc("maxUplinkIpFlow", request);
		if (isNotNumeric(maxUplinkSvc)) {
			throwNsbException(service, UUID.randomUUID().toString(), request.getRequestId(), "MaxUpLinkIpFlow missing");
		}
	}

	public static void validateMaxDownlink(ApnConfigRequest request, String service) throws NsbException {
		ServiceCharacteristic maxDownlinkSvc = getSvc("maxDownlinkIpFlow", request);
		if (isNotNumeric(maxDownlinkSvc)) {
			throwNsbException(service, UUID.randomUUID().toString(), request.getRequestId(),
					"MaxDownlinkIpFlow missing");
		}
	}

	private static ServiceCharacteristic getSvc(String str, ApnConfigRequest request) {
		return request.getServiceCharacteristic().stream()
				.filter(svc -> StringUtils.equalsIgnoreCase(str, svc.getName())).findAny().orElse(null);
	}

	private static boolean isNotNumeric(ServiceCharacteristic svc) {
		return null != svc && StringUtils.isNotEmpty(svc.getValue())
				&& !svc.getValue().chars().allMatch(Character::isDigit);
	}

}
