package com.kafka.client.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kafka.client.common.RealtimeDataProcessor;
import com.kafka.client.model.AgentAccountDetailsRequest;
import com.kafka.client.model.AgentAccountDetailsResponse;
import com.kafka.client.model.AgentDashboardDetailsResponse;
import com.kafka.client.model.ServiceDetailsResponse;
import com.kafka.client.model.AgentDetailsResponse;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RealtimeAgentDetailsController {

	private static final Logger LOGGER = Logger.getLogger("Kafka");
	private final RealtimeDataProcessor processor;

	
	public RealtimeAgentDetailsController(RealtimeDataProcessor processor) {
		super();
		this.processor = processor;
		
	}

	@GetMapping("/getTest")
	public String getTest()
	{
		
		return "Working Test";
	}
	
	@GetMapping("/getTest2")
	public void getTest2()
	{
		
		LOGGER.info("Entered into getAgentDetails Request Method");
		
		try
		{
			processor.processDataNew(null);
		} 
		catch (Exception e) 
		{
			
			LOGGER.error("Error while processing webservice request -" + e.toString());
			e.printStackTrace();
		}
		LOGGER.info("Exit from getAgentDetails Request Method");
	}

	@PostMapping("/getAgentDetails")
	public String getAgentDetails(@RequestBody String agentId) {
		LOGGER.debug("Entered into getAgentDetails Request Method");
		String response = null;
		try
		{
			LOGGER.debug("Get agent details for id : " + agentId);
			response= processor.getAgentDetails(agentId);
			if(response == null)
			{
				response = "{}";
				LOGGER.info("getAgentDetails - Agent Realtime data response null for Agent Id - " + agentId);
			}
			else 
			{	
				LOGGER.debug("Agent Realtime data retrieved successfully for agent - " + agentId);
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getAgentDetails - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getAgentDetails Request Method");
		return response;
	}
	
	@PostMapping("/getServiceDetails")
	public String getServiceDetails(@RequestBody String serviceName) {
		LOGGER.debug("Entered into getServiceDetails Request Method");
		String response = null;
		try
		{
			LOGGER.debug("Get service details for : " + serviceName);
			response= processor.getServiceDetails(serviceName);
			if(response == null)
			{
				response = "{}";
				LOGGER.info("getServiceDetails - Routing Service Realtime data response null for service - " + serviceName);
			}
			else 
			{	
				LOGGER.debug("Routing Service Realtime data retrieved successfully for service - " + serviceName);
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getServiceDetails - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getServiceDetails Request Method");
		return response;
	}
	
	@PostMapping("/getServiceDetailsList")
	public List<ServiceDetailsResponse>  getServiceDetailsList(@RequestBody String serviceName) {
		LOGGER.debug("Entered into getServiceDetailsList Request Method");
		List<ServiceDetailsResponse>  response = null;
		try
		{
			response= processor.getServiceDetailsList(serviceName);
			if(response == null)
			{
				LOGGER.info("getServiceDetailsList - Routing Service Realtime data response null for service name - " +serviceName);
			}
			else 
			{	
				LOGGER.debug("Routing Service Realtime data retrieved successfully");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getServiceDetailsList -" + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getServiceDetails Request Method");
		return response;
	}
	
	
	@PostMapping("/getAgentAccountDetails")
	public List<AgentAccountDetailsResponse> getAgentAccountDetails(@RequestBody AgentAccountDetailsRequest request) {
		LOGGER.debug("Entered into getAgentAccountDetails Request Method");
		List<AgentAccountDetailsResponse> response = null;
		try
		{
			response= processor.getAgentWithAccountDetails(request.getAgentId(), request.getSupervisorId());
			if(response == null)
			{
				LOGGER.info("getAgentAccountDetails - Agent Account Details response is null for supervisor - " + request.getSupervisorId());
			}
			else 
			{	
				LOGGER.debug("Agent Account Details retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getAgentAccountDetails - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getAgentAccountDetails Request Method");
		return response;
	}
	
	@PostMapping("/getAgentContactDetails")
	public List<AgentAccountDetailsResponse> getAgentContactDetails() {
		LOGGER.debug("Entered into getAgentContactDetails Request Method");
		List<AgentAccountDetailsResponse> response = null;
		try
		{
			response= processor.getAgentContactDetails();
			if(response == null)
			{
				LOGGER.info("getAgentContactDetails - Agent Contact Details response is null ");
			}
			else 
			{	
				LOGGER.debug("Agent Contact Details retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getAgentContactDetails - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getAgentAccountDetails Request Method");
		return response;
	}
	
	
	@GetMapping("/getAgentList")
	public List<String> getAgentList() {
		LOGGER.debug("Entered into getAgentList Request Method");
		List<String> response = null;
		try
		{
			response= processor.getAgentList();
			if(response == null)
			{
				LOGGER.info("getAgentList - Agent List response is null");
			}
			else 
			{	
				LOGGER.debug("Agent list retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getAgentList - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getAgentList Request Method");
		return response;
	}
	
	@GetMapping("/getServiceList")
	public List<String> getServiceList() {
		LOGGER.debug("Entered into getServiceList Request Method");
		List<String> response = null;
		try
		{
			response= processor.getAgentList();
			if(response == null)
			{
				LOGGER.info("getServiceList - Agent List response is null");
			}
			else 
			{	
				LOGGER.debug("Service list retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getServiceList - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getServiceList Request Method");
		return response;
	}
	

	@GetMapping("/getLoggedInAgentDetailList")
	public List<AgentDetailsResponse> getLoggedInAgentList() {
		LOGGER.debug("Entered into getLoggedInAgentList Request Method");
		List<AgentDetailsResponse> response = null;
		try
		{
			response= processor.getLoggedInAgentList();
			if(response == null)
			{
				LOGGER.info("getLoggedInAgentList - Agent List response is null");
			}
			else 
			{	
				LOGGER.debug("Logged in agents list retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getLoggedInAgentList - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getLoggedInAgentList Request Method");
		return response;
	}
	
	@GetMapping("/getAgentDetailList")
	public List<AgentDetailsResponse> getAgentDetailList() {
		LOGGER.debug("Entered into getAgentDetailList Request Method");
		List<AgentDetailsResponse> response = null;
		try
		{
			response= processor.getAgentDetailList();
			if(response == null)
			{
				LOGGER.info("getAgentDetailList - Agent List response is null");
			}
			else 
			{	
				LOGGER.debug("Logged in agents list retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getAgentDetailList - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getAgentDetailList Request Method");
		return response;
	}
	
	/**
	 * @param NIL
	 * @return AgentAccount Details Array List
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@GetMapping("/getAllAgentAccountDetails")
	public List<AgentAccountDetailsResponse> getAllAgentAccountDetails() {
		LOGGER.debug("Entered into getAgentAccountDetails Request Method");
		List<AgentAccountDetailsResponse> response = null;
		try
		{
			response= processor.getAllAgentAccountDetails();
			if(response == null)
			{
				LOGGER.info("getAgentAccountDetails - Agent Account Details response is null ");
			}
			else 
			{	
				LOGGER.debug("Agent Account Details retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getAgentAccountDetails - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getAgentAccountDetails Request Method");
		return response;
	}
	
	/**
	 * @param NIL
	 * @return AgentAccount Details Array List
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@GetMapping("/getAccountDetailsByAgent")
	public List<AgentAccountDetailsResponse> getAccountDetailsByAgent() {
		LOGGER.debug("Entered into getAccountDetailsByAgent Request Method");
		List<AgentAccountDetailsResponse> response = null;
		try
		{
			response= processor.getAllAgentAccountDetails();
			if(response == null)
			{
				LOGGER.info("getAccountDetailsByAgent - Agent Account Details response is null ");
			}
			else 
			{	
				LOGGER.debug("Agent Account Details retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getAccountDetailsByAgent - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getAccountDetailsByAgent Request Method");
		return response;
	}
	
	/**
	 * @param NIL
	 * @return AgentAccount Details Array List
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@GetMapping("/getRoutingServiceDetailsByAgent")
	public List<AgentAccountDetailsResponse> getRoutingServiceDetailsByAgent() {
		LOGGER.debug("Entered into getRoutingServiceDetailsByAgent Request Method");
		List<AgentAccountDetailsResponse> response = null;
		try
		{
			response= processor.getAllAgentAccountDetails();
			if(response == null)
			{
				LOGGER.info("getRoutingServiceDetailsByAgent - Agent Account Details response is null ");
			}
			else 
			{	
				LOGGER.debug("Agent Routing Service Details retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getRoutingServiceDetailsByAgent - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getAccountDetailsByAgent Request Method");
		return response;
	}
	
	/**
	 * @param NIL
	 * @return AgentAccount Details Array List
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@PostMapping("/getAgentDashboardDetails")
	public List<AgentDashboardDetailsResponse> getAgentDashboardDetails(@RequestBody String agentId) {
		LOGGER.debug("Entered into getRoutingServiceDetailsByAgent Request Method");
		List<AgentDashboardDetailsResponse> response = null;
		try
		{
			response= processor.getAgentDashboardDetails(agentId);
			if(response == null)
			{
				LOGGER.info("getAgentDashboardDetails - Agent Details response is null ");
			}
			else 
			{	
				LOGGER.debug("Agent Dashboard Details retrieved successfully for service");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error while processing webservice request - getAgentDashboardDetails - " + e.toString());
			e.printStackTrace();
		}
		LOGGER.debug("Exit from getAgentDashboardDetails Request Method");
		return response;
	}
}
