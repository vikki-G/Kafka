package com.kafka.client.model;

public class SubscriptionRequest {

	private String subscriptionRequestId;
	private String userName;
	private String password;
	private String measuresStream;
	private String request;
	private String version;
	
	public void setSubscriptionRequestId(String subscriptionRequestId) {
		this.subscriptionRequestId = subscriptionRequestId;
	}
	public String getSubscriptionRequestId() {
		return subscriptionRequestId;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setMeasuresStream(String measuresStream) {
		this.measuresStream = measuresStream;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getRequest() {
		return request;
	}
	public String getMeasuresStream() {
		return measuresStream;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVersion() {
		return version;
	}
	@Override
	public String toString() {
		return "SubscriptionRequest [subscriptionRequestId=" + subscriptionRequestId + ", userName=" + userName
				+ ", password=" + password + ", measuresStream=" + measuresStream + ", request=" + request
				+ ", version=" + version + "]";
	}
}
