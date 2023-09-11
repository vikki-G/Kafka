package com.kafka.client.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.kafka.client.model.AccountMeasures;
import com.kafka.client.model.AgentAUXCodeMeasures;
import com.kafka.client.model.AgentAccountDetailsRequest;
import com.kafka.client.model.AgentAccountDetailsResponse;
import com.kafka.client.model.AgentDashboardDetailsResponse;
import com.kafka.client.model.AgentDataDTO;
import com.kafka.client.model.ContactDetailMeasures;
import com.kafka.client.model.RealtimeProducerData;
import com.kafka.client.model.RoutingServiceByAgentMeasures;
import com.kafka.client.model.ServiceDetailsResponse;
import com.kafka.client.repositroy.DbConnection;
import com.kafka.client.service.UniqueIDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kafka.client.model.AgentDetailsResponse;

@Component
public class RealtimeDataProcessor {
	private DbConnection connect = new DbConnection();
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private static final Logger LOGGER = Logger.getLogger("Kafka");

	private final ObjectMapper mapper = new ObjectMapper();
	private final ConcurrentMap<String, String> agentDetailsMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Map<String, String>> agentAccountDetailsMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, String> serviceDetailsMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Map<String, String>> agentServiceDetailsMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Map<String, String>> agentNotReadyReasonDetailsMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Map<String, String>> agentContactDetailsMap = new ConcurrentHashMap<>();

	private volatile LocalDate resetTime = LocalDate.now();
	ObjectMapper objectMapper = new ObjectMapper();
	StringBuilder columns = new StringBuilder();
	StringBuilder values = new StringBuilder();

	/**
	 * @param rawData
	 */
	public void processData(RealtimeProducerData rawData) {
		LOGGER.debug("Processing realtime data");
		String keyName = "";

		String keyValue = "";
		String agentIdKey = "";
		String agentValueKey = "";
		// String agentByAccount = "";
		String contactAgentId = "";
		String contactState = "";
		String contactAcwIndicator = "";
		boolean routingServiceByAgent = false;

		try {
			if (rawData.getDimension() != null && rawData.getDimension().size() > 0 && !rawData.getPumpupComplete()
					&& !rawData.getHeartbeat()) {
				routingServiceByAgent = false;
				keyName = (String) (rawData.getDimension().keySet().toArray()[0]);
				keyValue = rawData.getDimension().get(keyName);
				if (rawData.getDimension().keySet().size() > 1) {
					if (keyName.equalsIgnoreCase("accountId")) {
						agentIdKey = (String) (rawData.getDimension().keySet().toArray()[1]);
						agentValueKey = rawData.getDimension().get(agentIdKey);

					} else if (keyName.equalsIgnoreCase("agentId")) {
						String keyName1 = (String) (rawData.getDimension().keySet().toArray()[1]);
						if (keyName1.equalsIgnoreCase("routingServiceName")) {
							agentIdKey = (String) (rawData.getDimension().keySet().toArray()[0]);
							agentValueKey = rawData.getDimension().get(agentIdKey);
							keyName = keyName1;
							keyValue = rawData.getDimension().get(keyName1);
							routingServiceByAgent = true;
						} else if (keyName1.equalsIgnoreCase("nrReasonCode")) {
							agentIdKey = (String) (rawData.getDimension().keySet().toArray()[0]);
							agentValueKey = rawData.getDimension().get(agentIdKey);
							keyName = keyName1;
							keyValue = rawData.getDimension().get(keyName1);
						}
					}
				}

				if (keyName.equals("segmentId")) {
					contactAgentId = (String) rawData.getRealtimeData().get("agentId");
					contactState = (String) rawData.getRealtimeData().get("state");
					contactAcwIndicator = (String) rawData.getRealtimeData().get("acwIndicator");
				}

				String newData = mapper.writeValueAsString(rawData.getRealtimeData());

				switch (keyName) {
				case "agentId":
					String agentData = agentDetailsMap.get(keyValue);
					if (StringUtils.isNotBlank(agentData)) {
						newData = updateDetails(agentData, newData);
					}
					agentDetailsMap.put(keyValue, newData);
					break;
				case "routingServiceName":
					if (!routingServiceByAgent) {
						String serviceData = serviceDetailsMap.get(keyValue);
						if (StringUtils.isNotBlank(serviceData)) {
							newData = updateDetails(serviceData, newData);
						}
						serviceDetailsMap.put(keyValue, newData);
					} else {
						Map<String, String> service = agentServiceDetailsMap.get(agentValueKey);
						if (service != null && service.size() > 0) {
							String serviceData = service.get(keyValue);
							if (StringUtils.isNotBlank(serviceData)) {
								newData = updateDetails(serviceData, newData);
							}
						} else {
							service = new HashMap<>();
						}
						service.put(keyValue, newData);
						agentServiceDetailsMap.put(agentValueKey, service);
					}
					break;
				case "accountId":
					Map<String, String> agentAccountData = agentAccountDetailsMap.get(agentValueKey);
					if (agentAccountData != null && agentAccountData.size() > 0) {
						String accountData = agentAccountData.get(keyValue);
						if (StringUtils.isNotBlank(accountData)) {
							newData = updateDetails(accountData, newData);
						}
					} else {
						agentAccountData = new HashMap<>();
					}
					agentAccountData.put(keyValue, newData);
					agentAccountDetailsMap.put(agentValueKey, agentAccountData);
					break;
				case "segmentId":
					Map<String, String> agentContactData = agentContactDetailsMap.get(contactAgentId);
					if (agentContactData != null && agentContactData.size() > 0) {
						String contactData = agentContactData.get(keyValue);
						if (StringUtils.isNotBlank(contactData)) {
							newData = updateDetails(contactData, newData);
						}
					} else {
						agentContactData = new HashMap<>();
					}
					if (contactState.equals("COMPLETE") && contactState.equals("UNKNOWN")
							&& (contactAcwIndicator.equals("COMPLETE") || contactAcwIndicator.equals("UNKNOWN"))) {
						agentContactData.remove(keyValue);
					} else {
						agentContactData.put(keyValue, newData);
					}
					agentContactDetailsMap.put(contactAgentId, agentContactData);
					break;
				case "nrReasonCode":
					Map<String, String> agentNotReady = agentNotReadyReasonDetailsMap.get(agentValueKey);
					if (agentNotReady != null && agentNotReady.size() > 0) {
						String notReadyData = agentNotReady.get(keyValue);
						if (StringUtils.isNotBlank(notReadyData)) {
							newData = updateDetails(notReadyData, newData);
						}
					} else {
						agentNotReady = new HashMap<>();
					}
					agentNotReady.put(keyValue, newData);
					agentNotReadyReasonDetailsMap.put(agentValueKey, agentNotReady);
					break;
				default:
					break;
				}
			} else {
				LOGGER.info("Empty Response from Kakfa realtime data");
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while processing response from Kakfa realtime data - " + e.getMessage());
		}
		LOGGER.debug("Exit from Realtime Processing realtime data");
	}
	// test method

	public static void main(String[] args) throws Exception {
		RealtimeDataProcessor pro = new RealtimeDataProcessor();
		pro.processDataNew(null);
	}
UniqueIDGenerator id =new UniqueIDGenerator();
	/**
	 * @param rawData
	 * @throws Exception
	 */
	public void processDataNew(RealtimeProducerData rawData) throws Exception {
		String newdatas = "";
		String Qurey = "";
		Connection connection = connect.getConnection();
		LOGGER.trace("Processing realtime data");
		// System.out.println("RealtimeDataProcessor method "+ agentDetailsMap);
		String keyName = "";
		String keyValue = "";
		String agentIdKey = "";
		String agentValueKey = "";

		String contactAgentId = "";
		String contactState = "";
		String contactAcwIndicator = "";
		boolean routingServiceByAgent = false;
		// String agentByAccount = "";
		LocalDate today = LocalDate.now();

		if (resetTime.isBefore(today)) {
			agentDetailsMap.clear();
			serviceDetailsMap.clear();
			agentAccountDetailsMap.clear();
			resetTime = LocalDate.now();
		}

		for (int i = 0; i < 20; i++) {
			// File file1 = new File("C:\\Users\\Admin\\Downloads\\test"+i+".txt");
			File file1 = new File("D:\\Disk C\\Users\\User\\Downloads\\Newtest\\Newtest\\test" + i + ".txt");
			try {
				routingServiceByAgent = false;
				Object reader1 = new JSONParser().parse(new FileReader(file1));
				rawData = mapper.convertValue(reader1, RealtimeProducerData.class);
				if (rawData.getDimension() != null && rawData.getDimension().size() > 0 && !rawData.getPumpupComplete()
						&& !rawData.getHeartbeat()) {

					keyName = (String) (rawData.getDimension().keySet().toArray()[0]);
					keyValue = rawData.getDimension().get(keyName);
					if (rawData.getDimension().keySet().size() > 1) {
						if (keyName.equalsIgnoreCase("accountId")) {
							agentIdKey = (String) (rawData.getDimension().keySet().toArray()[1]);
							agentValueKey = rawData.getDimension().get(agentIdKey);

						} else if (keyName.equalsIgnoreCase("agentId")) {
							String keyName1 = (String) (rawData.getDimension().keySet().toArray()[1]);
							if (keyName1.equalsIgnoreCase("routingServiceName")) {
								agentIdKey = (String) (rawData.getDimension().keySet().toArray()[0]);
								agentValueKey = rawData.getDimension().get(agentIdKey);
								keyName = keyName1;
								keyValue = rawData.getDimension().get(keyName1);
								routingServiceByAgent = true;
							} else if (keyName1.equalsIgnoreCase("nrReasonCode")) {
								agentIdKey = (String) (rawData.getDimension().keySet().toArray()[0]);
								agentValueKey = rawData.getDimension().get(agentIdKey);
								keyName = keyName1;
								keyValue = rawData.getDimension().get(keyName1);
							}

						}
					}

					if (keyName.equals("segmentId")) {
						contactAgentId = (String) rawData.getRealtimeData().get("agentId");
						contactState = (String) rawData.getRealtimeData().get("state");
						contactAcwIndicator = (String) rawData.getRealtimeData().get("acwIndicator");
					}

					String newData = mapper.writeValueAsString(rawData.getRealtimeData());
					
					switch (keyName) {
					case "agentId":
						String agentData = agentDetailsMap.get(keyValue);

						//if (StringUtils.isNotBlank(agentData)) {

						//	newData = updateDetails(agentData, newData);
						//}
						String[] datas = { "agentLogonDuration", "agentNotReadyDuration", "agentActiveDuration",
								"agentBlendedActiveDuration", "agentIdleDuration", "agentAcwDuration", "agentState",
								"agentWorkState", "agentLastLoginTime", "agentNotReadyTimeStamp",
								"agentWorkStateTimeStamp", "agentVoiceCallState", "agentActiveInteractions",
								"agentLastStateTimeStamp", "agentLastWorkStateTimeStamp", "agentLastWorkState" };
						System.out.println(newData);
						
						// JsonNode data=jsonNode.get(datas);

						JsonNode jsonNode = objectMapper.readTree(newData);

						// Create a new JSON object with only the desired fields
						ObjectMapper filteredObjectMapper = new ObjectMapper();
						//JsonNode filteredJsonNode = filteredObjectMapper.createObjectNode();
						
						JsonNode filteredJsonNode = Arrays.stream(datas)
							    .filter(field -> jsonNode.has(field))
							    .collect(() -> filteredObjectMapper.createObjectNode(), 
							             (node, field) -> ((ObjectNode) node).set(field, jsonNode.get(field)), 
							             (node1, node2) -> {}); 
						
						String filteredJsonString = filteredJsonNode.toString();
						System.out.println(filteredJsonString);

						System.out.println(newdatas);
						agentDetailsMap.put(keyValue, filteredJsonString);
                        String newid = id.idGenerate();
						String insertQuery = "INSERT INTO KAFKAREALTIMEDATA  (id, measure, filterdata) VALUES ('" +newid+ "','AGENTMEASURES' ,'" + filteredJsonString + "')";

						System.out.println(insertQuery);
						preparedStatement = connection.prepareStatement(insertQuery);
						preparedStatement.execute();

						System.out.println(agentDetailsMap);
						break;
					case "routingServiceName":

						if (!routingServiceByAgent) {

							String serviceData = serviceDetailsMap.get(keyValue);
							if (StringUtils.isNotBlank(serviceData)) {
								newData = updateDetails(serviceData, newData);
							}
							String[] datas1 = { "serviceDisplayName", "acw", "staffed", "predictiveWaitTime",
									"shortEngagements", "totalDuration", "ringTimeDuration", "longHolds", "maxStaffed",
									"completed", "consults" };
							System.out.println(newData);

							JsonNode jsonNode1 = objectMapper.readTree(newData);

							// Create a new JSON object with only the desired fields
							ObjectMapper filteredObjectMapper1 = new ObjectMapper();
							//JsonNode filteredJsonNode = filteredObjectMapper.createObjectNode();
							
							JsonNode filteredJsonNode1 = Arrays.stream(datas1)
								    .filter(field -> jsonNode1.has(field))
								    .collect(() -> filteredObjectMapper1.createObjectNode(), 
								             (node, field) -> ((ObjectNode) node).set(field, jsonNode1.get(field)), 
								             (node1, node2) -> {}); 
							
							// Convert the filtered JSON object to a string
							String filteredJsonString1 = filteredJsonNode1.toString();
							System.out.println(filteredJsonString1);
							serviceDetailsMap.put(keyValue, filteredJsonString1);
							System.out.println(serviceDetailsMap);
							String newid1 = id.idGenerate();
							String insertQuery1 = "INSERT INTO KAFKAREALTIMEDATA  (id, measure, filterdata) VALUES ('"+newid1+"','ROUTINGSERVICEMEASURES' ,'" + filteredJsonString1 + "')";
							System.out.println(insertQuery1);
							preparedStatement = connection.prepareStatement(insertQuery1);
							preparedStatement.execute();
							System.out.println("Data insert successfully");
						} else {
							Map<String, String> service = agentServiceDetailsMap.get(agentValueKey);
							if (service != null && service.size() > 0) {
								String serviceData = service.get(keyValue);
								if (StringUtils.isNotBlank(serviceData)) {
									newData = updateDetails(serviceData, newData);
								}
							} else {
								service = new HashMap<>();
							}
							service.put(keyValue, newData);
							agentServiceDetailsMap.put(agentValueKey, service);
							System.out.println(agentServiceDetailsMap);
						}
						break;
					case "accountId":
						Map<String, String> agentAccountData = agentAccountDetailsMap.get(agentValueKey);
						if (agentAccountData != null && agentAccountData.size() > 0) {
							String accountData = agentAccountData.get(keyValue);
							if (StringUtils.isNotBlank(accountData)) {
								newData = updateDetails(accountData, newData);
							}
						} else {
							agentAccountData = new HashMap<>();
						}
						String[] datas1 = { "accountState", "accountNotReadyTimeDuration", "channel", "predictiveWaitTime",
								"routingAttributeService", "acw", "agentFirstName", "accountNotReady", "supervisorFirstName",
								"shortEngagements", "adHoc" };
						System.out.println(newData);
						
						
						JsonNode jsonNode2 = objectMapper.readTree(newData);

						// Create a new JSON object with only the desired fields
						ObjectMapper filteredObjectMapper2 = new ObjectMapper();
						JsonNode filteredJsonNode2 = Arrays.stream(datas1)
							    .filter(field -> jsonNode2.has(field))
							    .collect(() -> filteredObjectMapper2.createObjectNode(), 
							             (node, field) -> ((ObjectNode) node).set(field, jsonNode2.get(field)), 
							             (node1, node2) -> {}); 
						
						String filteredJsonString2 = filteredJsonNode2.toString();
						System.out.println(filteredJsonString2);
						agentAccountData.put(keyValue, filteredJsonString2);
						agentAccountDetailsMap.put(agentValueKey, agentAccountData);
						String newid2 = id.idGenerate();
						String insertQuery2 = "INSERT INTO KAFKAREALTIMEDATA  (id, measure, filterdata) VALUES ('"+newid2+"','AGENTBYACCOUNTMEASURES' ,'" + filteredJsonString2 + "')";
						System.out.println(insertQuery2);
						preparedStatement = connection.prepareStatement(insertQuery2);
						preparedStatement.execute();
						System.out.println(agentAccountData);
						
						break;
						
					case "segmentId":
						Map<String, String> agentContactData = agentContactDetailsMap.get(contactAgentId);
						if (agentContactData != null && agentContactData.size() > 0) {
							String contactData = agentContactData.get(keyValue);
							if (StringUtils.isNotBlank(contactData)) {
								newData = updateDetails(contactData, newData);
							}
						} else {
							agentContactData = new HashMap<>();
						}
						if (contactState.equals("COMPLETE")
								&& (contactAcwIndicator.equals("COMPLETE") || contactAcwIndicator.equals("UNKNOWN"))) {
							agentContactData.remove(keyValue);
						} 
						String[] datas3 = { "firstName", "supervisorLastName", "customerEndTs", "nrReasonCode",
								"engagementId", "segmentType", "uniqueId", "segmentTypeEffectiveTs", "agentNotReady",
								"routingServiceName", "stateEffectiveTs" };
						JsonNode jsonNode3 = objectMapper.readTree(newData);

						// Create a new JSON object with only the desired fields
						ObjectMapper filteredObjectMapper3 = new ObjectMapper();
						JsonNode filteredJsonNode3 = Arrays.stream(datas3)
							    .filter(field -> jsonNode3.has(field))
							    .collect(() -> filteredObjectMapper3.createObjectNode(), 
							             (node, field) -> ((ObjectNode) node).set(field, jsonNode3.get(field)), 
							             (node1, node2) -> {}); 
						
						
						String filteredJsonString3 = filteredJsonNode3.toString();
						System.out.println(filteredJsonString3);
						agentContactData.put(keyValue, filteredJsonString3);
						String newid3 = id.idGenerate();
						String insertQuery3 = "INSERT INTO KAFKAREALTIMEDATA  (id, measure, filterdata) VALUES ('"+newid3+"','AGENTBYROUTINGSERVICEMEASURES' ,'" + filteredJsonString3 + "')";
						System.out.println(insertQuery3);
						preparedStatement = connection.prepareStatement(insertQuery3);
						preparedStatement.execute();
						
						agentContactDetailsMap.put(contactAgentId, agentContactData);
						System.out.println(agentContactDetailsMap);
						break;
					case "nrReasonCode":
						Map<String, String> agentNotReady = agentNotReadyReasonDetailsMap.get(agentValueKey);
						if (agentNotReady != null && agentNotReady.size() > 0) {
							String notReadyData = agentNotReady.get(keyValue);
							if (StringUtils.isNotBlank(notReadyData)) {
								newData = updateDetails(notReadyData, newData);
							}
						} else {
							agentNotReady = new HashMap<>();
						}
						String[] datas4 = { "nrReasonCodeOccurrence", "supervisorLastName", "agentDisplayName", "agentReady",
								"agentNotReady", "nrReasonCodeEffectiveDT", "nrReasonCode", "agentNRReasonCodeTimeDuration"};
						JsonNode jsonNode4 = objectMapper.readTree(newData);

						// Create a new JSON object with only the desired fields
						ObjectMapper filteredObjectMapper4 = new ObjectMapper();
					
						JsonNode filteredJsonNode4 = Arrays.stream(datas4)
							    .filter(field -> jsonNode4.has(field))
							    .collect(() -> filteredObjectMapper4.createObjectNode(), 
							             (node, field) -> ((ObjectNode) node).set(field, jsonNode4.get(field)), 
							             (node1, node2) -> {}); 
						
						String filteredJsonString4 = filteredJsonNode4.toString();
						
						
						agentNotReady.put(keyValue, filteredJsonString4);
						agentNotReadyReasonDetailsMap.put(agentValueKey, agentNotReady);
						String newid4 = id.idGenerate();
						String insertQuery4 = "INSERT INTO KAFKAREALTIMEDATA  (id, measure, filterdata) VALUES ('"+newid4+"','AGENTBYAUXCODEMEASURES' ,'" + filteredJsonString4 + "')";
						System.out.println(insertQuery4);
						preparedStatement = connection.prepareStatement(insertQuery4);
						preparedStatement.execute();
						
						System.out.println(agentNotReadyReasonDetailsMap);
						
						break;
					default:
						break;
					}
				}
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		connection.close();
		LOGGER.debug("Exit from Realtime Processing realtime data");
	}
	// test method

	/**
	 * common method for merging two json string objects this method updates the
	 * existing field value with the new values from newObj
	 * 
	 * @param oldObj
	 * @param newObj
	 */
	@SuppressWarnings("unchecked")
	public String updateDetails(String oldObj, String newObj) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject Obj1 = (JSONObject) parser.parse(oldObj);
			JSONObject Obj2 = (JSONObject) parser.parse(newObj);

			// updating all the parameter into existing object from the new data (newObj)
			// stream received from kafka event
			Obj1.putAll(Obj2);
			String _returnObject = Obj1.toJSONString();

			Obj1.clear();
			Obj2.clear();

			return _returnObject;

		} catch (Exception e) {
			throw new RuntimeException("updateAgentDetails - JSON Exception - " + e);
		}

	}

	/**
	 * @param agentId
	 * @return
	 */
	public String getAgentDetails(String agentId) {
		LOGGER.debug("Enter into getAgentDetails method");

		String response = null;
		if (StringUtils.isNotBlank(agentId)) {
			response = agentDetailsMap.get(agentId);
			return response;
		} else {
			LOGGER.debug("Agentid is empty could not process the request");
		}
		LOGGER.debug("Exit from getAgentDetails method");
		return response;
	}

	/**
	 * @param agentId
	 * @return
	 */
	public String getServiceDetails(String serviceName) {
		LOGGER.debug("Enter into getServiceDetails method");

		String response = null;
		if (StringUtils.isNotBlank(serviceName)) {
			response = serviceDetailsMap.get(serviceName);
			return response;
		} else {
			LOGGER.debug("serviceName is empty could not process the request");
		}
		LOGGER.debug("Exit from getServiceDetails method");
		return response;
	}

	/**
	 * @param agentId
	 * @return
	 */
	public List<ServiceDetailsResponse> getServiceDetailsList(String service) {
		LOGGER.debug("Enter into getServiceDetails method");
		List<ServiceDetailsResponse> response = new ArrayList<>();
		ServiceDetailsResponse serviceDetails = new ServiceDetailsResponse();
		String searchResult = null;

		if (service.equals("0") || service.equals("ALL") || StringUtils.isBlank(service)) {
			if (serviceDetailsMap != null && serviceDetailsMap.size() > 0) {
				for (Map.Entry<String, String> result : serviceDetailsMap.entrySet()) {
					serviceDetails = new ServiceDetailsResponse();
					serviceDetails.setServiceData(result.getValue());
					serviceDetails.setServiceName(result.getKey());
					response.add(serviceDetails);
				}

			}
		} else {
			String[] serviceList = service.split(",");
			for (int i = 0; i < serviceList.length; i++) {
				searchResult = serviceDetailsMap.get(serviceList[i]);
				if (StringUtils.isNotBlank(searchResult)) {
					serviceDetails = new ServiceDetailsResponse();
					serviceDetails.setServiceName(serviceList[i]);
					serviceDetails.setServiceData(searchResult);
					response.add(serviceDetails);

				}
			}
		}
		LOGGER.debug("Exit from getServiceDetails method");
		return response;
	}

	/**
	 * @param agentId
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public List<AgentAccountDetailsResponse> getAgentWithAccountDetails(String agentId, String supervisorId)
			throws JsonMappingException, JsonProcessingException {
		LOGGER.debug("Enter into getAgentWithAccountDetails method");

		List<AgentAccountDetailsResponse> response = new ArrayList<>();
		AgentAccountDetailsResponse agentAccountDetails = null;
		AccountMeasures accountMeasures = null;
		ContactDetailMeasures contactMeasures = null;
		List<AccountMeasures> lAcountMeasures = new ArrayList<>();
		List<ContactDetailMeasures> lContactMeasures = new ArrayList<>();
		String searchAgentResult = null;
		Map<String, String> searchAccountResult = null;
		Map<String, String> searchContactResult = null;

		if (agentId.equals("0") || agentId.equals("ALL") || StringUtils.isBlank(agentId)) {
			if (agentDetailsMap != null && agentDetailsMap.size() > 0) {
				for (ConcurrentMap.Entry<String, String> result : agentDetailsMap.entrySet()) {
					AgentAccountDetailsRequest details = mapper.readValue(result.getValue(),
							AgentAccountDetailsRequest.class);
					if (StringUtils.isNotBlank(supervisorId) && supervisorId.equals(details.getSupervisorId())) {
						agentAccountDetails = new AgentAccountDetailsResponse();
						agentAccountDetails.setAgentId(result.getKey());
						agentAccountDetails.setAgentMeasures(result.getValue());

						searchAccountResult = agentAccountDetailsMap.get(result.getKey());
						lAcountMeasures = new ArrayList<>();
						if (searchAccountResult != null && searchAccountResult.size() > 0) {

							for (ConcurrentMap.Entry<String, String> account : searchAccountResult.entrySet()) {
								accountMeasures = new AccountMeasures();
								accountMeasures.setAccountId(account.getKey());
								accountMeasures.setAccountMeasures(account.getValue());
								lAcountMeasures.add(accountMeasures);

							}
						}
						agentAccountDetails.setAgentAccountMeasures(lAcountMeasures);

						searchContactResult = agentContactDetailsMap.get(result.getKey());
						lContactMeasures = new ArrayList<>();
						if (searchContactResult != null && searchContactResult.size() > 0) {

							for (ConcurrentMap.Entry<String, String> contact : searchContactResult.entrySet()) {
								contactMeasures = new ContactDetailMeasures();
								contactMeasures.setSegmentId(contact.getKey());
								contactMeasures.setContactMeasure(contact.getValue());
								lContactMeasures.add(contactMeasures);

							}
						}
						agentAccountDetails.setContactDetailMeasures(lContactMeasures);
						response.add(agentAccountDetails);
					}

				}

			}

		} else {
			String[] agentList = agentId.split(",");
			for (int i = 0; i < agentList.length; i++) {
				searchAgentResult = agentDetailsMap.get(agentList[i]);
				if (StringUtils.isNotBlank(searchAgentResult)) {
					AgentAccountDetailsRequest details = mapper.readValue(searchAgentResult,
							AgentAccountDetailsRequest.class);
					if (supervisorId.equals(details.getSupervisorId())) {
						agentAccountDetails = new AgentAccountDetailsResponse();
						agentAccountDetails.setAgentId(agentList[i]);
						agentAccountDetails.setAgentMeasures(searchAgentResult);

						searchAccountResult = agentAccountDetailsMap.get(agentList[i]);
						if (searchAccountResult != null && searchAccountResult.size() > 0) {
							for (Map.Entry<String, String> account : searchAccountResult.entrySet()) {
								accountMeasures = new AccountMeasures();
								accountMeasures.setAccountId(account.getKey());
								accountMeasures.setAccountMeasures(account.getValue());
								lAcountMeasures.add(accountMeasures);

							}
						}
						agentAccountDetails.setAgentAccountMeasures(lAcountMeasures);

						searchContactResult = agentContactDetailsMap.get(agentList[i]);
						lContactMeasures = new ArrayList<>();
						if (searchContactResult != null && searchContactResult.size() > 0) {

							for (ConcurrentMap.Entry<String, String> contact : searchContactResult.entrySet()) {
								contactMeasures = new ContactDetailMeasures();
								contactMeasures.setSegmentId(contact.getKey());
								contactMeasures.setContactMeasure(contact.getValue());
								lContactMeasures.add(contactMeasures);

							}
						}
						agentAccountDetails.setContactDetailMeasures(lContactMeasures);

						response.add(agentAccountDetails);
					}
				}
			}
		}

		LOGGER.debug("Exit from getAgentWithAccountDetails method");
		return response;
	}

	/**
	 * @param agentId
	 * @return
	 * @throws SQLException 
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public List<AgentAccountDetailsResponse> getAgentContactDetails() throws SQLException {
		LOGGER.debug("Enter into getAgentContactDetails method");

		List<AgentAccountDetailsResponse> response = new ArrayList<>();
		AgentAccountDetailsResponse agentAccountDetails = null;
		AccountMeasures accountMeasures = null;
		ContactDetailMeasures contactMeasures = null;
		List<AccountMeasures> lAcountMeasures = new ArrayList<>();
		List<ContactDetailMeasures> lContactMeasures = new ArrayList<>();
		Map<String, String> searchAccountResult = null;
		Map<String, String> searchContactResult = null;

		if (agentDetailsMap != null && agentDetailsMap.size() > 0) {
			for (ConcurrentMap.Entry<String, String> result : agentDetailsMap.entrySet()) {
				agentAccountDetails = new AgentAccountDetailsResponse();
				agentAccountDetails.setAgentId(result.getKey());
				agentAccountDetails.setAgentMeasures(result.getValue());

				searchAccountResult = agentAccountDetailsMap.get(result.getKey());
				lAcountMeasures = new ArrayList<>();
				if (searchAccountResult != null && searchAccountResult.size() > 0) {

					for (ConcurrentMap.Entry<String, String> account : searchAccountResult.entrySet()) {
						accountMeasures = new AccountMeasures();
						accountMeasures.setAccountId(account.getKey());
						accountMeasures.setAccountMeasures(account.getValue());
						lAcountMeasures.add(accountMeasures);

					}
				}
				agentAccountDetails.setAgentAccountMeasures(lAcountMeasures);

				searchContactResult = agentContactDetailsMap.get(result.getKey());
				lContactMeasures = new ArrayList<>();
				if (searchContactResult != null && searchContactResult.size() > 0) {

					for (ConcurrentMap.Entry<String, String> contact : searchContactResult.entrySet()) {
						contactMeasures = new ContactDetailMeasures();
						contactMeasures.setSegmentId(contact.getKey());
						contactMeasures.setContactMeasure(contact.getValue());
						lContactMeasures.add(contactMeasures);

					}
				}
				agentAccountDetails.setContactDetailMeasures(lContactMeasures);
				response.add(agentAccountDetails);
			}

		}
        connection.close();
		LOGGER.debug("Exit from getAgentContactDetails method");
		return response;
	}

	/**
	 * @param agentId
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public List<AgentAccountDetailsResponse> getAllAgentAccountDetails() {
		LOGGER.debug("Enter into getAgentAccountDetails method");

		List<AgentAccountDetailsResponse> response = new ArrayList<>();
		AgentAccountDetailsResponse agentAccountDetails = null;
		AccountMeasures accountMeasures = null;

		List<AccountMeasures> lAcountMeasures = new ArrayList<>();

		Map<String, String> searchAccountResult = null;

		if (agentDetailsMap != null && agentDetailsMap.size() > 0) {
			for (ConcurrentMap.Entry<String, String> result : agentDetailsMap.entrySet()) {

				agentAccountDetails = new AgentAccountDetailsResponse();
				agentAccountDetails.setAgentId(result.getKey());
				agentAccountDetails.setAgentMeasures(result.getValue());

				searchAccountResult = agentAccountDetailsMap.get(result.getKey());
				lAcountMeasures = new ArrayList<>();
				if (searchAccountResult != null && searchAccountResult.size() > 0) {

					for (ConcurrentMap.Entry<String, String> account : searchAccountResult.entrySet()) {
						accountMeasures = new AccountMeasures();
						accountMeasures.setAccountId(account.getKey());
						accountMeasures.setAccountMeasures(account.getValue());
						lAcountMeasures.add(accountMeasures);

					}
				}
				agentAccountDetails.setAgentAccountMeasures(lAcountMeasures);

				response.add(agentAccountDetails);
			}
		}

		LOGGER.debug("Exit from getAgentAccountDetails method");
		return response;
	}

	/**
	 * @param
	 * @return
	 */
	public List<String> getAgentList() {
		LOGGER.debug("Enter into getAgentList method");

		List<String> response = new ArrayList<>();
		if (agentDetailsMap != null && agentDetailsMap.size() > 0) {
			response = new ArrayList<String>(agentDetailsMap.keySet());
			return response;
		} else {
			LOGGER.debug("AgentList is empty. Could not process the request");
		}
		LOGGER.debug("Exit from getAgentList method");
		return response;
	}

	/**
	 * @param
	 * @return
	 */
	public List<String> getServiceList() {
		LOGGER.debug("Enter into getServiceList method");

		List<String> response = new ArrayList<>();
		if (serviceDetailsMap != null && serviceDetailsMap.size() > 0) {
			response = new ArrayList<String>(serviceDetailsMap.keySet());
			return response;
		} else {
			LOGGER.debug("Service List is empty. Could not process the request");
		}
		LOGGER.debug("Exit from getServiceList method");
		return response;
	}

	/**
	 * @param
	 * @return
	 */
	public List<AgentDetailsResponse> getLoggedInAgentList() {
		LOGGER.debug("Enter into getLoggedInAgentList method");
		AgentDetailsResponse agentDetails = null;
		List<AgentDetailsResponse> response = new ArrayList<>();
		if (agentDetailsMap != null && agentDetailsMap.size() > 0) {
			for (ConcurrentMap.Entry<String, String> result : agentDetailsMap.entrySet()) {
				try {
					AgentAccountDetailsRequest details = mapper.readValue(result.getValue(),
							AgentAccountDetailsRequest.class);
					if (!details.getAgentState().equals("LOGGED_OUT")
							&& !("PENDING_LOGGED_OUT").equals(details.getAgentState())
							&& !details.getAgentState().equals("UNKNOWN")) {
						agentDetails = new AgentDetailsResponse();
						agentDetails.setAgentId(result.getKey());
						agentDetails.setAgentDetails(result.getValue());
						response.add(agentDetails);
					}
				} catch (Exception e) {
					LOGGER.error("Error while processing agent details - " + e.getMessage());
				}

			}
		} else {
			LOGGER.debug("AgentList is empty. Could not process the request");
		}
		LOGGER.debug("Exit from getLoggedInAgentList method");
		return response;
	}

	/**
	 * @param
	 * @return
	 */
	public List<AgentDetailsResponse> getAgentDetailList() {
		LOGGER.debug("Enter into getAgentDetailList method");
		AgentDetailsResponse agentDetails = null;
		List<AgentDetailsResponse> response = new ArrayList<>();
		if (agentDetailsMap != null && agentDetailsMap.size() > 0) {
			for (ConcurrentMap.Entry<String, String> result : agentDetailsMap.entrySet()) {

				agentDetails = new AgentDetailsResponse();
				agentDetails.setAgentId(result.getKey());
				agentDetails.setAgentDetails(result.getValue());
				response.add(agentDetails);

			}
		} else {
			LOGGER.debug("AgentList is empty. Could not process the request");
		}
		LOGGER.debug("Exit from getAgentDetailList method");
		return response;
	}

	/**
	 * @param agentId
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public List<AgentDashboardDetailsResponse> getAgentDashboardDetails(String agentId) {
		LOGGER.debug("Enter into getAgentAccountDetails method");

		List<AgentDashboardDetailsResponse> response = new ArrayList<>();
		AgentDashboardDetailsResponse agentDashboardDetails = null;
		AccountMeasures accountMeasures = null;
		AgentAUXCodeMeasures auxCodeMeasures = null;
		RoutingServiceByAgentMeasures serviceMeasures = null;
		List<AccountMeasures> lAcountMeasures = null;
		List<AgentAUXCodeMeasures> lAuxCodeMeasures = new ArrayList<>();
		List<RoutingServiceByAgentMeasures> lServiceMeasures = new ArrayList<>();
		Map<String, String> searchAccountResult = null;
		Map<String, String> searchServiceResult = null;
		Map<String, String> searchAuxCodeResult = null;

		if (StringUtils.isNotBlank(agentId) && agentId != null && !"ALL".equalsIgnoreCase(agentId)) {
			if (agentDetailsMap != null && agentDetailsMap.size() > 0) {
				String value = agentDetailsMap.get(agentId);
				agentDashboardDetails = new AgentDashboardDetailsResponse();
				agentDashboardDetails.setAgentId(agentId);
				agentDashboardDetails.setAgentMeasures(value);

				searchAccountResult = agentAccountDetailsMap.get(agentId);
				lAcountMeasures = new ArrayList<>();
				if (searchAccountResult != null && searchAccountResult.size() > 0) {

					for (ConcurrentMap.Entry<String, String> account : searchAccountResult.entrySet()) {
						accountMeasures = new AccountMeasures();
						accountMeasures.setAccountId(account.getKey());
						accountMeasures.setAccountMeasures(account.getValue());
						lAcountMeasures.add(accountMeasures);

					}
				}
				agentDashboardDetails.setAgentAccountMeasures(lAcountMeasures);

				searchServiceResult = agentServiceDetailsMap.get(agentId);
				lServiceMeasures = new ArrayList<>();
				if (searchServiceResult != null && searchServiceResult.size() > 0) {

					for (ConcurrentMap.Entry<String, String> contact : searchServiceResult.entrySet()) {
						serviceMeasures = new RoutingServiceByAgentMeasures();
						serviceMeasures.setRoutingServiceName(contact.getKey());
						serviceMeasures.setRoutingServiceMeasureData(contact.getValue());
						lServiceMeasures.add(serviceMeasures);

					}
				}
				agentDashboardDetails.setRoutingServiceMeasures(lServiceMeasures);

				searchAuxCodeResult = agentNotReadyReasonDetailsMap.get(agentId);
				lAuxCodeMeasures = new ArrayList<>();
				if (searchAuxCodeResult != null && searchAuxCodeResult.size() > 0) {

					for (ConcurrentMap.Entry<String, String> contact : searchAuxCodeResult.entrySet()) {
						auxCodeMeasures = new AgentAUXCodeMeasures();
						auxCodeMeasures.setAuxCode(contact.getKey());
						auxCodeMeasures.setAuxMeasureData(contact.getValue());
						lAuxCodeMeasures.add(auxCodeMeasures);

					}
				}
				agentDashboardDetails.setAgentAUXCodeMeasures(lAuxCodeMeasures);

				response.add(agentDashboardDetails);

			}

		} else {
			if (agentDetailsMap != null && agentDetailsMap.size() > 0) {
				for (ConcurrentMap.Entry<String, String> result : agentDetailsMap.entrySet()) {
					agentDashboardDetails = new AgentDashboardDetailsResponse();
					agentDashboardDetails.setAgentId(result.getKey());
					agentDashboardDetails.setAgentMeasures(result.getValue());

					searchAccountResult = agentAccountDetailsMap.get(result.getKey());
					lAcountMeasures = new ArrayList<>();
					if (searchAccountResult != null && searchAccountResult.size() > 0) {

						for (ConcurrentMap.Entry<String, String> account : searchAccountResult.entrySet()) {
							accountMeasures = new AccountMeasures();
							accountMeasures.setAccountId(account.getKey());
							accountMeasures.setAccountMeasures(account.getValue());
							lAcountMeasures.add(accountMeasures);

						}
					}
					agentDashboardDetails.setAgentAccountMeasures(lAcountMeasures);

					searchServiceResult = agentServiceDetailsMap.get(result.getKey());
					lServiceMeasures = new ArrayList<>();
					if (searchServiceResult != null && searchServiceResult.size() > 0) {

						for (ConcurrentMap.Entry<String, String> contact : searchServiceResult.entrySet()) {
							serviceMeasures = new RoutingServiceByAgentMeasures();
							serviceMeasures.setRoutingServiceName(contact.getKey());
							serviceMeasures.setRoutingServiceMeasureData(contact.getValue());
							lServiceMeasures.add(serviceMeasures);

						}
					}
					agentDashboardDetails.setRoutingServiceMeasures(lServiceMeasures);

					searchAuxCodeResult = agentNotReadyReasonDetailsMap.get(result.getKey());
					lAuxCodeMeasures = new ArrayList<>();
					if (searchAuxCodeResult != null && searchAuxCodeResult.size() > 0) {

						for (ConcurrentMap.Entry<String, String> contact : searchAuxCodeResult.entrySet()) {
							auxCodeMeasures = new AgentAUXCodeMeasures();
							auxCodeMeasures.setAuxCode(contact.getKey());
							auxCodeMeasures.setAuxMeasureData(contact.getValue());
							lAuxCodeMeasures.add(auxCodeMeasures);

						}
					}
					agentDashboardDetails.setAgentAUXCodeMeasures(lAuxCodeMeasures);
					response.add(agentDashboardDetails);

				}

			}
		}

		LOGGER.debug("Exit from getAgentDashboardDetails method");
		return response;
	}
}