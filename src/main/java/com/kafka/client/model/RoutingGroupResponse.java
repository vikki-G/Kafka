package com.kafka.client.model;

public class RoutingGroupResponse {
	
	private String typeDescription;
	private String serviceName;
	
	public RoutingGroupResponse() {
		
	}
	
	@Override
	public String toString() {
		return "RoutingGroupResponse [typeDescription=" + typeDescription + ", serviceName=" + serviceName + "]";
	}

	public RoutingGroupResponse(String typeDescription, String serviceName) {
		super();
		this.typeDescription = typeDescription;
		this.serviceName = serviceName;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
