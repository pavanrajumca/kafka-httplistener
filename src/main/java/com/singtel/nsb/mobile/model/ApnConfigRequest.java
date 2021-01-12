package com.singtel.nsb.mobile.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "requestId", "description", "name", "serviceType", "serviceCharacteristic", "state" })
public class ApnConfigRequest {

	@JsonProperty("requestId")
	private String requestId;
	@JsonProperty("description")
	private String description;
	@JsonProperty("name")
	private String name;
	@JsonProperty("serviceType")
	private String serviceType;
	@JsonProperty("serviceCharacteristic")
	private List<ServiceCharacteristic> serviceCharacteristic;
	@JsonProperty("state")
	private String state;
	@JsonIgnore
	private Map<String, Object> apnConfigRequestProperties = new HashMap<>();

	@JsonGetter("requestId")
	public String getRequestId() {
		return requestId;
	}

	@JsonProperty("requestId")
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@JsonGetter("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonGetter("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonGetter("serviceType")
	public String getServiceType() {
		return serviceType;
	}

	@JsonProperty("serviceType")
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@JsonGetter("serviceCharacteristic")
	public List<ServiceCharacteristic> getServiceCharacteristic() {
		return serviceCharacteristic;
	}

	@JsonProperty("serviceCharacteristic")
	public void setServiceCharacteristic(List<ServiceCharacteristic> serviceCharacteristic) {
		this.serviceCharacteristic = serviceCharacteristic;
	}

	@JsonGetter("state")
	public String getState() {
		return state;
	}

	@JsonProperty("state")
	public void setState(String state) {
		this.state = state;
	}

}