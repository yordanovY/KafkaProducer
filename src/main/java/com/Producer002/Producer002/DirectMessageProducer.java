package com.Producer002.Producer002;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("kafka")
@PropertySource("producer002.properties")
//@PropertySource("log4j.properties")
public class DirectMessageProducer {
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
	private static final Logger logger = LogManager.getLogger(DirectMessageProducer.class);//"HelloWorld");
	
	//send message through Rest service
	@GetMapping("/message_publish/{message}")
	public void sendMessage2(@PathVariable("message") final String message) {
		//timestamp and file names
		String timeStamp = DateFormat.YEAR_FIELD + DateFormat.MONTH_FIELD + DateFormat.DAY_OF_WEEK_FIELD + DateFormat.HOUR_OF_DAY0_FIELD + "_" +  DateFormat.MILLISECOND_FIELD;
		String fileName = "message_"+timeStamp;
		File outputFile = new File("Archive//"+ fileName);
		//header
		List<Header> headers = new ArrayList<>();
		headers.add(new RecordHeader("timeOfSending:", timeStamp.getBytes()));
		try {
			//write to file
	        FileWriter myWriter = new FileWriter(outputFile);
	        myWriter.write(message);
	        myWriter.close();
	    } catch (IOException e) {
	    	logger.error("An error occurred in generating file" + fileName + ".");
	        e.printStackTrace();
	    }
		ProducerRecord<String, String> sentRecord = new ProducerRecord<>(topic_3, 0, timeStamp, message, headers);
		templateKafka.send(sentRecord);
	}
}
