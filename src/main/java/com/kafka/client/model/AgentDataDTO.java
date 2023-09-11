package com.kafka.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentDataDTO {
    public String getAgentNotReadyDuration() {
		return agentNotReadyDuration;
	}

	public void setAgentNotReadyDuration(String agentNotReadyDuration) {
		this.agentNotReadyDuration = agentNotReadyDuration;
	}

	public String getAgentActiveDuration() {
		return agentActiveDuration;
	}

	public void setAgentActiveDuration(String agentActiveDuration) {
		this.agentActiveDuration = agentActiveDuration;
	}

	public String getAgentBlendedActiveDuration() {
		return agentBlendedActiveDuration;
	}

	public void setAgentBlendedActiveDuration(String agentBlendedActiveDuration) {
		this.agentBlendedActiveDuration = agentBlendedActiveDuration;
	}

	public String getAgentIdleDuration() {
		return agentIdleDuration;
	}

	public void setAgentIdleDuration(String agentIdleDuration) {
		this.agentIdleDuration = agentIdleDuration;
	}

	public String getAgentAcwDuration() {
		return agentAcwDuration;
	}

	public void setAgentAcwDuration(String agentAcwDuration) {
		this.agentAcwDuration = agentAcwDuration;
	}

	public String getAgentState() {
		return agentState;
	}

	public void setAgentState(String agentState) {
		this.agentState = agentState;
	}

	public String getAgentWorkState() {
		return agentWorkState;
	}

	public void setAgentWorkState(String agentWorkState) {
		this.agentWorkState = agentWorkState;
	}

	public String getAgentLastLoginTime() {
		return agentLastLoginTime;
	}

	public void setAgentLastLoginTime(String agentLastLoginTime) {
		this.agentLastLoginTime = agentLastLoginTime;
	}

	public String getAgentNotReadyTimeStamp() {
		return agentNotReadyTimeStamp;
	}

	public void setAgentNotReadyTimeStamp(String agentNotReadyTimeStamp) {
		this.agentNotReadyTimeStamp = agentNotReadyTimeStamp;
	}

	public String getAgentWorkStateTimeStamp() {
		return agentWorkStateTimeStamp;
	}

	public void setAgentWorkStateTimeStamp(String agentWorkStateTimeStamp) {
		this.agentWorkStateTimeStamp = agentWorkStateTimeStamp;
	}

	public String getAgentVoiceCallState() {
		return agentVoiceCallState;
	}

	public void setAgentVoiceCallState(String agentVoiceCallState) {
		this.agentVoiceCallState = agentVoiceCallState;
	}

	public String getAgentActiveInteractions() {
		return agentActiveInteractions;
	}

	public void setAgentActiveInteractions(String agentActiveInteractions) {
		this.agentActiveInteractions = agentActiveInteractions;
	}

	public String getAgentLastStateTimeStamp() {
		return agentLastStateTimeStamp;
	}

	public void setAgentLastStateTimeStamp(String agentLastStateTimeStamp) {
		this.agentLastStateTimeStamp = agentLastStateTimeStamp;
	}

	public String getAgentLastWorkStateTimeStamp() {
		return agentLastWorkStateTimeStamp;
	}

	public void setAgentLastWorkStateTimeStamp(String agentLastWorkStateTimeStamp) {
		this.agentLastWorkStateTimeStamp = agentLastWorkStateTimeStamp;
	}

	public String getAgentLastWorkState() {
		return agentLastWorkState;
	}

	public void setAgentLastWorkState(String agentLastWorkState) {
		this.agentLastWorkState = agentLastWorkState;
	}

	@JsonProperty("agentNotReadyTimeDuration")
    private String agentNotReadyDuration;

    @JsonProperty("totalActiveTimeDuration")
    private String agentActiveDuration;

    @JsonProperty("blendedActiveDuration")
    private String agentBlendedActiveDuration;

    @JsonProperty("idleTimeDuration")
    private String agentIdleDuration;

    @JsonProperty("acwDuration")
    private String agentAcwDuration;

    @JsonProperty("agentState")
    private String agentState;

    @JsonProperty("workState")
    private String agentWorkState;

    @JsonProperty("agentLoginTimeStamp")
    private String agentLastLoginTime;

    @JsonProperty("agentNrReasonTimeStamp")
    private String agentNotReadyTimeStamp;

    @JsonProperty("agentWorkStateTimeStamp")
    private String agentWorkStateTimeStamp;

    @JsonProperty("agentVoiceCallState")
    private String agentVoiceCallState;

    @JsonProperty("agentActiveInteractions")
    private String agentActiveInteractions;

    @JsonProperty("agentLastStateTimeStamp")
    private String agentLastStateTimeStamp;

    @JsonProperty("agentLastWorkStateTimeStamp")
    private String agentLastWorkStateTimeStamp;

    @JsonProperty("agentLastWorkState")
    private String agentLastWorkState;

    // Getters and setters
    // You can generate these using your IDE or manually implement them
}
