package com.kafka.client.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.kafka.client.Config;
import com.kafka.client.model.PumpupRequest;
import com.kafka.client.model.SubscriptionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafKaProducerService 
{
	private static final Logger LOGGER = Logger.getLogger("Kafka");
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper mapper;

	public KafKaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper mapper) {
		super();
		this.kafkaTemplate = kafkaTemplate;
		this.mapper = mapper;
	}
	
	public void subscribe(String measureStream, String username, String password) {
		sendRequest(measureStream, username, password, "SUBSCRIBE");
	}

	public void unsubscribe(String measureStream, String username, String password) {
		sendRequest(measureStream, username, password, "UNSUBSCRIBE");
	}

	public void list(String username, String password) {
		sendRequest(null, username, password, "LIST");
	}

	private void sendRequest(String measureStream, String username, String password,
			String requestType) {
		LOGGER.trace("Inside send " + requestType + " request method");
		if (!StringUtils.isNotBlank(measureStream)) {
			LOGGER.trace(requestType + " Request measure stream is empty");
			return;
		}
		if (StringUtils.isAnyEmpty(username, password)) {
			LOGGER.trace(requestType + " Request for measure stream is failed due to empty user credentials");
			return;
		}
		SubscriptionRequest request = new SubscriptionRequest();
		request.setMeasuresStream(measureStream);
		request.setPassword(password);
		request.setRequest(requestType);
		request.setSubscriptionRequestId(RandomStringUtils.randomAlphanumeric(9));
		request.setUserName(username);
		request.setVersion(Config.VERSION);
		
		try {
			
			String jsonRequest = mapper.writeValueAsString(request);
			
			LOGGER.debug("Sending "+ requestType + " request -> "+ jsonRequest);
			kafkaTemplate.send(Config.TOPIC_SUBSCRIPTION_REQUEST, jsonRequest).get();
			
			LOGGER.debug(requestType + " request sent for -> " + request.getSubscriptionRequestId());
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Unable to serialize subscription request to json: " + e.getMessage(), e);
		} catch (CancellationException | InterruptedException | ExecutionException e) {
			throw new RuntimeException("Unable to send subscription request: " +e.getMessage(), e);
		}
	}
	
	public void sendPumpup(String measureStream, String username, String password) {
		LOGGER.error("Inside send pumpup request method");
		if (!StringUtils.isNotBlank(measureStream)) {
			LOGGER.error("Pumpup Request measure stream is empty");
			return;
		}
		if (StringUtils.isAnyEmpty(username, password)) {
			LOGGER.error("Pumpup Request for measure stream is failed due to empty user credentials");
			return;
		}
        PumpupRequest request = new PumpupRequest();
        request.setPassword(password);
        request.setUserName(username);
        Set<String> ms = new HashSet<>(Arrays.asList(measureStream));
        request.setMeasuresStreams(ms);

        try {
           
            String jsonRequest = mapper.writeValueAsString(request);
            LOGGER.debug("Sending pumpup request {} -> " + jsonRequest);
            LOGGER.trace("Serialized request -> " + jsonRequest);
            kafkaTemplate.send(Config.TOPIC_PUMPUP_REQUEST,jsonRequest).get();
            LOGGER.debug("Pumpup request sent for -> " + measureStream);
        } catch (JsonProcessingException e) {
        	throw new RuntimeException("Unable to serialize pumpup request to json: " + e.getMessage(), e);
        } catch (CancellationException | ExecutionException | InterruptedException e) {
            throw new RuntimeException("Unable to send pumpup request: " + e.getMessage(), e);
        }
    }
}
