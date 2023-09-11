package com.kafka.client.model;

import java.util.Set;

public class PumpupRequest {
	private String userName;
	private String password;
	private Set<String> measuresStreams;

	public void setMeasuresStreams(Set<String> measuresStreams) {
		this.measuresStreams = measuresStreams;
	}
	public Set<String> getMeasuresStreams() {
		return measuresStreams;
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
	@Override
	public String toString() {
		return "PumpupRequest [userName=" + userName + ", password=" + password + ", measuresStreams=" + measuresStreams
				+ "]";
	}

}
