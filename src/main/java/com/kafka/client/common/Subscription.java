package com.kafka.client.common;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import com.kafka.client.Config;
import com.kafka.client.model.RealtimeProducerData;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Subscription {
	private static final Logger LOGGER = Logger.getLogger("Kafka");

	private final MessageListener<String, String> messageListener;
	private final ConsumerFactory<String, String> kafkaConsumerFactory;
	private volatile MessageListenerContainer container;
	private ObjectMapper mapper = new ObjectMapper();
	private final RealtimeDataProcessor processor;
	//private final KafKaProducerService producerService;
	
	//private volatile LocalDate lastHeartBeatTime;

	@Autowired
	public Subscription(ConsumerFactory<String, String> kafkaConsumerFactory,
			MessageListener<String, String> messageListener,RealtimeDataProcessor processor) {
		this.kafkaConsumerFactory = kafkaConsumerFactory;
		this.messageListener = messageListener;
		this.processor = processor;
	}


	public void eventReceiver(String topic) {
		LOGGER.info("Inside Subscribe method");

		LOGGER.debug(String.format("Making subscription to " + topic));
		ContainerProperties containerProperties = new ContainerProperties(topic);
		containerProperties.setMessageListener(messageListener);
		containerProperties.setClientId("oi-java-ref-client-" + UUID.randomUUID());

		container = new ConcurrentMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);
		// setup a Kafka message listener 
		container.setupMessageListener(new MessageListener<String, String>() {
			@Override
			public void onMessage(ConsumerRecord<String, String> record) {
				LOGGER.debug("realtime data received = " + record.value().toString());
				try {
					RealtimeProducerData data = mapper.readValue(record.value(), RealtimeProducerData.class);
					if(!data.getHeartbeat()) {
						processor.processData(data);
					}
					else {
						Config.LASTHEARTBEATTIME = LocalDateTime.now();
					}
				} catch (IOException e) {
					LOGGER.error("Unable to get output realtime data", e);
				}
			}
		});
		container.start();
		LOGGER.debug("MessageListenerContainer started for topic : "+ topic);
	}


	public void eventCloser() {
		LOGGER.info("Inside Unsubscribe method");
		if (container != null) {
			String topic = container.getContainerProperties().getTopics()[0];
			if (container.isRunning() || container.isContainerPaused() || container.isPauseRequested()) {
				container.stop();
				container = null;
				LOGGER.debug("MessageListenerContainer has been stopped for topic : " + topic);
			} else {
				LOGGER.debug("MessageListenerContainer already stopped for topic : "+ topic);
			}
		}
	}

	
	public MessageListener<String, String> getListener() {
		return messageListener;
	}
}
