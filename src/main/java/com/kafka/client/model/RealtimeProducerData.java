package com.kafka.client.model;

import java.util.Map;

public class RealtimeProducerData {
	private Map<String, String> dimension;
	private Map<String, Object> realtimeData;
	private Boolean pumpup;
	private Boolean pumpupComplete;
	private Boolean heartbeat;

	public void setDimension(Map<String, String> dimension) {
		this.dimension = dimension;
	}
	public Map<String, String> getDimension() {
		return dimension;
	}
	public void setRealtimeData(Map<String, Object> realtimeData) {
		this.realtimeData = realtimeData;
	}
	public Map<String, Object> getRealtimeData() {
		return realtimeData;
	}
	public void setPumpup(Boolean pumpup) {
		this.pumpup = pumpup;
	}
	public Boolean getPumpup() {
		return pumpup;
	}
	public void setPumpupComplete(Boolean pumpupComplete) {
		this.pumpupComplete = pumpupComplete;
	}
	public Boolean getPumpupComplete() {
		return pumpupComplete;
	}
	public void setHeartbeat(Boolean heartbeat) {
		this.heartbeat = heartbeat;
	}
	public Boolean getHeartbeat() {
		return heartbeat;
	}
	@Override
	public String toString() {
		return "RealtimeProducerData [dimension=" + dimension + ", realtimeData=" + realtimeData + ", pumpup=" + pumpup
				+ ", pumpupComplete=" + pumpupComplete + ", heartbeat=" + heartbeat + "]";
	}
	
}
