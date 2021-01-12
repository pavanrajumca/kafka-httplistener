package com.singtel.nsb.mobile.constants;

import static com.singtel.nsb.mobile.constants.HeaderConstants.DELETE;
import static com.singtel.nsb.mobile.constants.HeaderConstants.GET;
import static com.singtel.nsb.mobile.constants.HeaderConstants.PATCH;
import static com.singtel.nsb.mobile.constants.HeaderConstants.POST;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;

public enum StatusCodes {

	POST_SVC(POST, HttpStatus.SC_CREATED),
	PATCH_SVC(PATCH, HttpStatus.SC_OK),
	DELETE_SVC(DELETE, HttpStatus.SC_OK),
	GET_SVC(GET, HttpStatus.SC_OK),
	;

	private String method;
	private int statusCode;

	StatusCodes(String method, int statusCode) {
		this.method = method;
		this.statusCode = statusCode;
	}

	public String getMethod() {
		return method;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public static int getStatusCode(String method) {
		Optional<StatusCodes> status = Arrays.stream(StatusCodes.values()).filter(statusCode -> StringUtils.equalsIgnoreCase(statusCode.getMethod(), method)).findFirst();
		if(status.isPresent()) {
			return status.get().getStatusCode();
		}
		return 0;
	}

}
