package com.kafka.client.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MessageReader implements MessageListener<String, String>{
	private static final Logger LOGGER = Logger.getLogger("Kafka");
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void onMessage(ConsumerRecord<String, String> realtimeDataRecord) {
		LOGGER.info("Consumer realtime data received : " + realtimeDataRecord.value());

		try {
			Object json = mapper.readValue(realtimeDataRecord.value(), Object.class);
			String prettyJson = mapper.writeValueAsString(json);
		} catch (IOException e) {
			LOGGER.error("Unable to output realtime data", e);
		}

	}
	
}
