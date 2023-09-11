package com.kafka.client.service;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.kafka.client.Config;
import com.kafka.client.common.Subscription;
import com.kafka.client.model.SubscriptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafKaConsumerService 
{
	private static final Logger LOGGER = Logger.getLogger("Kafka");

    private final ObjectMapper mapper;
    private final Subscription subscription;

	public KafKaConsumerService(ObjectMapper mapper, Subscription subscription) {
		super();
		this.mapper = mapper;
		this.subscription = subscription;
	}

	/**
	 * @param rawResponse
	 */
	@KafkaListener(topics = Config.TOPIC_SUBSCRIPTION_RESPONSE)
	public void process(String rawResponse) {
		LOGGER.info("Realtime subscription response received: " + rawResponse);
		SubscriptionResponse response = null;
		try {
			
			response = mapper.readValue(rawResponse, SubscriptionResponse.class);
			
			if (!Config.SUCCESS.equalsIgnoreCase(response.getResult())) {
				String errorMsg = "Subscription request " + response.getSubscriptionRequestId()
				+ " ends up with failure: " + response.getReason();
				LOGGER.error(errorMsg);
				return;
			}
			if (response.getMeasuresStream() == null) {
				LOGGER.error("Subscription measure stream is empty for the active subscriptions - " +
						response.getSubscriptionRequestId());
				return;
			}
			
			if (subscription != null) {
				if (response.getSubscribedToStreams().contains(response.getMeasuresStream())) {
					subscription.eventReceiver(response.getTopic());
				} else {
					subscription.eventCloser();
				}
			} else {
				LOGGER.info("No subscription found");
			}
		} catch (IOException e) {
			LOGGER.error("Unable to deserialize subscription response from incoming json", e);
			return;
		} 
		catch (RuntimeException e) {
			LOGGER.error("Unable to process subscription response", e);
		}
	}
}
