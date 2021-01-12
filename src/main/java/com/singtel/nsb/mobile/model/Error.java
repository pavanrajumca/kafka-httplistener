package com.singtel.nsb.mobile.model;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Used when an API throws an Error
 */
@ApiModel(description = "Used when an API throws an Error")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-12-06T14:17:12.233+08:00")
public class Error {
	@SerializedName("code")
	private Integer code = null;

	@SerializedName("reason")
	private String reason = null;

	@SerializedName("message")
	private String message = null;

	@SerializedName("state")
	private String state = null;

	@SerializedName("orderId")
	private String orderId = null;

	/**
	 * Application specific error status code. Refer ApiErrorCodes
	 * 
	 * @return code
	 **/
	@ApiModelProperty(required = true, value = "Application specific error status code. Refer ApiErrorCodes")
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Error reason(String reason) {
		this.reason = reason;
		return this;
	}

	/**
	 * Application specific Error Message. Refer ApiErrorCodes
	 * 
	 * @return reason
	 **/
	@ApiModelProperty(required = true, value = "Application specific Error Message. Refer ApiErrorCodes")
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * More details and corrective actions related to the error which can be shown
	 * to a client user.
	 * 
	 * @return message
	 **/
	@ApiModelProperty(required = true, value = "More details and corrective actions related to the error which can be shown to a client user. ")
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Response State(State Type:SUBMITTED/SUCCESS/FAILED)
	 * 
	 * @return state
	 **/
	@ApiModelProperty(required = true, value = "Response State(State Type:SUBMITTED/SUCCESS/FAILED)")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Order id , this is to track the order, unique identifier, helps in order
	 * recovery
	 * 
	 * @return orderId
	 **/
	@ApiModelProperty(required = true, value = "Order id , this is to track the order, unique identifier, helps in order recovery")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
