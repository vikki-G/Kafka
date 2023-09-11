package com.kafka.client.model;

import java.util.List;

public class AgentAccountDetailsResponse {
	private String agentId;
	private String agentMeasures;
	private List<AccountMeasures> agentAccountMeasures;
	private List<ContactDetailMeasures> contactDetailMeasures;
	
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public String getAgentMeasures() {
		return agentMeasures;
	}
	public void setAgentMeasures(String agentMeasures) {
		this.agentMeasures = agentMeasures;
	}
	public List<AccountMeasures> getAgentAccountMeasures() {
		return agentAccountMeasures;
	}
	public void setAgentAccountMeasures(List<AccountMeasures> agentAccountMeasures) {
		this.agentAccountMeasures = agentAccountMeasures;
	}
	public List<ContactDetailMeasures> getContactDetailMeasures() {
		return contactDetailMeasures;
	}
	public void setContactDetailMeasures(List<ContactDetailMeasures> contactDetailMeasures) {
		this.contactDetailMeasures = contactDetailMeasures;
	}
	
	

}
