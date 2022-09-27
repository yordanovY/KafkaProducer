package com.Producer002.Producer002;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

@RestController
@RequestMapping("kafka")
@PropertySource("producer002.properties")
//@PropertySource("log4j.properties")
@EnableAutoConfiguration
public class FileProducer {
	 
	@Value("${topic_1}")
	public String topic_1;
	
	@Value("${topic_2}")
	public String topic_2;
	
	@Value("${topic_3}")
	public String topic_3;
	
	@Value("${topic_3_DLQ}")
	public String topic_3_DLQ;
	
	@Autowired
	public KafkaTemplate<String, String> templateKafka;
	private static final Logger logger = LogManager.getLogger(FileProducer.class);//"HelloWorld");

	//Process and sends the files in folder
	@GetMapping("/file_publish")
	public void sendMessage() {
		String timeStamp = DateFormat.YEAR_FIELD + DateFormat.MONTH_FIELD + DateFormat.DAY_OF_WEEK_FIELD + DateFormat.HOUR_OF_DAY0_FIELD + "_" +  DateFormat.MILLISECOND_FIELD;
		logger.info("File processing started at" + System.currentTimeMillis());
		//ensure that no file duplicates are sent
		Vector<String> usedFile = new Vector<String>();
		while(true) {
			//directory of files to be sent
			File directoryPath = new File("Outgoing");
			for(File currentFile : directoryPath.listFiles()){
				if(!usedFile.contains(currentFile.getName())) {
					//dead letter queue server
					Boolean toDLQ = false;
					usedFile.add(currentFile.getName());
					String messageToServer = "";
					List<Header> headers = new ArrayList<>();
					try {
						//send xml files
						if(currentFile.getName().contains(".xml"))
						{
							XMLitem xmlItem = new XMLitem(currentFile);
							if(!xmlItem.getSender().isEmpty()) {
								headers.add(new RecordHeader("Sender", xmlItem.getSender().getBytes()));
							}else {
								toDLQ = true;
								logger.warn("Missing Sender from " + currentFile.getName());
							}
							if(!xmlItem.getDestination().isEmpty()) {
								headers.add(new RecordHeader("Destination", xmlItem.getDestination().getBytes()));
							}else {
								toDLQ = true;
								logger.warn("Missing Destination from " + currentFile.getName());
							}
							if(!xmlItem.getServer().isEmpty()) {
								headers.add(new RecordHeader("Server", xmlItem.getServer().getBytes()));
							}else {
								toDLQ = true;
								logger.warn("Missing Server from " + currentFile.getName());
							}
							messageToServer = xmlItem.getContent();
						}
						//send json files
						else if(currentFile.getName().contains(".json"))
						{
							JSONitem jsonItem = new JSONitem(currentFile);
							if(!jsonItem.getSender().isEmpty()) {
							headers.add(new RecordHeader("Sender", jsonItem.getSender().getBytes()));
							}else {
								toDLQ = true;
								logger.warn("Missing Sender from " + currentFile.getName());
							}
							if(!jsonItem.getDestination().isEmpty()) {
							headers.add(new RecordHeader("Destination", jsonItem.getDestination().getBytes()));
							}else {
								toDLQ = true;
								logger.warn("Missing Destination from " + currentFile.getName());
							}
							if(!jsonItem.getServer().isEmpty()) {
							headers.add(new RecordHeader("Server", jsonItem.getServer().getBytes()));
							}else {
								toDLQ = true;
								logger.warn("Missing Server from " + currentFile.getName());
							}
							messageToServer = jsonItem.getContent();
						} else {
							logger.error("Invalid file " + currentFile.getName());
							toDLQ = true;
						}
						if(!toDLQ) {
							//send files to archiving folder
							FileUtils.copyFile(currentFile, new File("Archive//"+currentFile.getName()));
							ProducerRecord<String, String> sentRecord = new ProducerRecord<>(topic_3, 0, timeStamp, messageToServer, headers);
				    		templateKafka.send(sentRecord);
				    		//delete used file
							FileUtils.delete(currentFile);
						}else {
							ProducerRecord<String, String> sentRecord = new ProducerRecord<>(topic_3_DLQ, 0, timeStamp, messageToServer, headers);
							templateKafka.send(sentRecord);
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					}	
				}
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
		}
	
	
}
