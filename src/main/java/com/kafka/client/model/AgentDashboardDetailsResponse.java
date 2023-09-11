package com.kafka.client.model;

import java.util.List;

public class AgentDashboardDetailsResponse {
	private String agentId;
	private String agentMeasures;
	private List<AccountMeasures> agentAccountMeasures;
	private List<RoutingServiceByAgentMeasures> routingServiceMeasures;
	private List<AgentAUXCodeMeasures> agentAUXCodeMeasures;
	
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
	public List<RoutingServiceByAgentMeasures> getRoutingServiceMeasures() {
		return routingServiceMeasures;
	}
	public void setRoutingServiceMeasures(List<RoutingServiceByAgentMeasures> routingServiceMeasures) {
		this.routingServiceMeasures = routingServiceMeasures;
	}
	public List<AgentAUXCodeMeasures> getAgentAUXCodeMeasures() {
		return agentAUXCodeMeasures;
	}
	public void setAgentAUXCodeMeasures(List<AgentAUXCodeMeasures> agentAUXCodeMeasures) {
		this.agentAUXCodeMeasures = agentAUXCodeMeasures;
	}
	
	
}
