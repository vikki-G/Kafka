package com.kafka.client;

import org.apache.log4j.Logger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {
	private static final Logger LOGGER = Logger.getLogger("Kafka");
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		
		LOGGER.info("Application Started");
		return application.sources(KafkaApplication.class);
		

	}

}
