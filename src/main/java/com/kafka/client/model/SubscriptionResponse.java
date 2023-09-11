package com.kafka.client.model;

import java.util.Set;

public class SubscriptionResponse {

	private String subscriptionRequestId;
	private String userName;
	private String measuresStream;
	private String topic;
	private String result;
	private String reason;
	private String version;
	private Set<String> subscribedToStreams;
	
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
	public void setMeasuresStream(String measuresStream) {
		this.measuresStream = measuresStream;
	}
	public String getMeasuresStream() {
		return measuresStream;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getTopic() {
		return topic;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResult() {
		return result;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getReason() {
		return reason;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVersion() {
		return version;
	}
	public void setSubscribedToStreams(Set<String> subscribedToStreams) {
		this.subscribedToStreams = subscribedToStreams;
	}
	public Set<String> getSubscribedToStreams() {
		return subscribedToStreams;
	}
	public SubscriptionResponse() {
	}
	@Override
	public String toString() {
		return "SubscriptionResponse [subscriptionRequestId=" + subscriptionRequestId + ", userName=" + userName
				+ ", measuresStream=" + measuresStream + ", topic=" + topic + ", result=" + result + ", reason="
				+ reason + ", version=" + version + ", subscribedToStreams=" + subscribedToStreams + "]";
	}
}
