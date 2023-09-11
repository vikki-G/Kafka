package com.kafka.client.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.client.Config;
import com.kafka.client.Decryption;
import com.kafka.client.service.KafKaProducerService;

@RestController
@RequestMapping(value = "/producer")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class KafkaProducerController {
	
	private static final Logger LOGGER = Logger.getLogger("Kafka");
	private final KafKaProducerService producerService;
	
	@Autowired
	public KafkaProducerController(KafKaProducerService producerService) {
		this.producerService = producerService;
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void startSubscribeRequest() {
		LOGGER.info("ApplicationReadyEvent - Subscribe method - Start"); 
		try {
			startSubscribe();
			startSchedulerTask();
		}
		catch(Exception e) {
			LOGGER.error("Subscribe & pumpup request ends with an error.");
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("ApplicationReadyEvent - Subscribe method - Start");
	}
	
	public void startSubscribe() {
		LOGGER.info("startSubscribe method - Start"); 
		try {
			Thread.sleep(10000);
			if(Config.MEASURESTREAM.length > 0) {
				for(String stream : Config.MEASURESTREAM) {
					LOGGER.info("Initiating Subscribtion Request for MeasureStream: " + stream); 
					producerService.subscribe(stream, Config.USER, Decryption.getDecryptedData(Config.PWD));
				}
				LOGGER.info("Subscribtion Request completed for All MeasureStreams");		
				
				// Commented, since the pumpup is not ready
				Thread.sleep(5000);
			 	for(String stream : Config.MEASURESTREAM) {
					LOGGER.info("Initiating Pumpup Request for MeasureStream: " + stream); 
					producerService.sendPumpup(stream, Config.USER, Decryption.getDecryptedData(Config.PWD));
				}
				LOGGER.info("Pumpup Request completed for All MeasureStreams");
				
			}
			LOGGER.info("Subscrition & Pumpup request successfully initiated");
		}
		catch(Exception e) {
			LOGGER.error("Subscribtion & pumpup request ends with an error.");
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("startSubscribe method - End");
	}
	
	public void startSchedulerTask() {
		LOGGER.error("startSchedulerTask - Start");
		try {
			while ( true )
			{
				LocalDateTime time = LocalDateTime.now();
				long difference = ChronoUnit.MINUTES.between(time, Config.LASTHEARTBEATTIME) / 60;
				if((difference >= Long.parseLong(Config.HEARTBEAT_THRESHOLD))) {
					startSubscribe();
				}
				Thread.sleep(Long.parseLong(Config.THREAD_SLEEP) * 60 * 1000);
			} 
		}
		catch (InterruptedException e) {
			
			LOGGER.error("startSchedulerTask error - " + e.getMessage());
		}
		catch (Exception e) {
			
			LOGGER.error("startSchedulerTask error - " + e.getMessage());
		}
	}
	
	
	@PreDestroy
	public void unsubscribeAllRequest() {
		LOGGER.info("unsubscribeAllRequest method");
		try {
			
			if(Config.MEASURESTREAM.length > 0) {
				for(String stream : Config.MEASURESTREAM) {
					LOGGER.info("Initiating Unsubscribtion Request for MeasureStream: " + stream);		
					producerService.unsubscribe(stream, Config.USER, Decryption.getDecryptedData(Config.PWD));
					Thread.sleep(2000);
				}
			}
			LOGGER.info("Unsubscribtion request successfully initiated for all MeasureStreams");
		}
		catch(Exception e) {
			LOGGER.error("unsubscribeAllRequest ends with an error.");
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("Exiting from unsubscribeAllRequest method");
	}
	
	@GetMapping(value = "/subscribeAll")
	public void subscribeRequest() {
		LOGGER.info("Manual Subscribe Method - Start");
		try {
			LOGGER.info("Initiating Subscribe & Pumpup Request for all measure stream");
			startSubscribe();
		}
		catch (Exception e) {
			LOGGER.error("Subscribe & pumpup request ends with an error.");
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("Manual Subscribe Method - End");
	}
	
	
	@PostMapping(value = "/subscribe")
	public void subscribeRequest(@RequestParam("measureStream") String measureStream, @RequestParam("username") String username, @RequestParam("password") String password) {
		LOGGER.info("Entered into Kafka Producer Subscribe Request Method");
		try {
			LOGGER.info("Subscribtion Request with - MeasureStream: " + measureStream +", User: " +username);
			producerService.subscribe(measureStream, username, password);
		}
		catch (Exception e) {
			LOGGER.error("Subscribtion request ends with an error.");
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("Exit from Kafka Producer Subscribe Request Method");
	}
	
	@PostMapping(value = "/unsubscribe")
	public void unsubscribeRequest(@RequestParam("measureStream") String measureStream, @RequestParam("username") String username, @RequestParam("password") String password) {
		LOGGER.info("Entered into Kafka Producer Unsubscribe Request Method");
		try {
			LOGGER.info("Unsubscribtion Request with - MeasureStream: " + measureStream +", User: " +username);
			producerService.unsubscribe(measureStream, username, password);
		}catch (Exception e) {
			LOGGER.error("Unsubscribtion request ends with an error.");
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("Exit from Kafka Producer Unsubscribe Request Method");
	}
	
	@PostMapping(value = "/list")
	public void listRequest(@RequestParam("username") String username, @RequestParam("password") String password) {
		LOGGER.info("Entered into Kafka Producer List Request Method");
		try {
			LOGGER.info("Subscription List Request with - User: " +username);
			producerService.list(username, password);
		}catch (Exception e) {
			LOGGER.error("Subscription List request ends with an error.");
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("Exit from Kafka Subscription List Request Method");
	}
	
	@PostMapping(value = "/pumpuprequest")
	public void kafkaPumpupRequest(@RequestParam("measureStream") String measureStream, @RequestParam("username") String username, @RequestParam("password") String password) {
		LOGGER.info("Entered into Kafka Producer Pumpup Request Method");
		try {
			LOGGER.info("Producer Pumpup Request with - User: " + username );
			producerService.sendPumpup(measureStream, username, password);
		}
		catch(Exception e) {
			LOGGER.info("Pumpup request ends with an error.");
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("Exit from Kafka Producer Pumpup Request Method");
	}
}