package com.kafka.client;


import java.time.LocalDateTime;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config 
{
	public static final String TOPIC_SUBSCRIPTION_RESPONSE = "realtimesubscriptionresponse";
	public static final String TOPIC_SUBSCRIPTION_REQUEST = "realtimesubscriptionrequest";
	public static final String TOPIC_PUMPUP_REQUEST = "realtimemeasurespumpuprequest";

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";

	public static String VERSION;
	public static String[] MEASURESTREAM;
	public static String USER;
	public static String PWD;
	public static String ENCRYPTIONKEY;

	public static String HEARTBEAT_THRESHOLD;
	public static String THREAD_SLEEP;
	
	public static volatile LocalDateTime LASTHEARTBEATTIME;

	@Value("${avaya.analytics.oi.version}") 
	private String version;
	@Value("${avaya.analytics.measure-stream}") 
	private String[] measureStream;
	@Value("${avaya.analytics.user}") 
	private String user;
	@Value("${avaya.analytics.password}") 
	private String pwd;
	@Value("${password.encryption.secret-key}")
	private String encryptionKey;
	
	@Value("${analytics.hearbeat.threshold}") 
	private String heartbeatThreshold;
	@Value("${analytics.scheduler.sleeptime}")
	private String threadSleep;

	@PostConstruct
	public void init(){
		Config.VERSION = this.version;
		Config.MEASURESTREAM = this.measureStream;
		Config.USER = this.user;
		Config.PWD = this.pwd;
		Config.ENCRYPTIONKEY = this.encryptionKey;
		Config.HEARTBEAT_THRESHOLD = this.heartbeatThreshold;
		Config.THREAD_SLEEP = this.threadSleep;
	}
}
