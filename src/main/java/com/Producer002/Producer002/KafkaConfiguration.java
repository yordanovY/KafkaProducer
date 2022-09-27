package com.Producer002.Producer002;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@PropertySource("producer002.properties")
public class KafkaConfiguration {
	@Value("${kafka.bootstrap.server}")
	public String boostrapServer;
	@Configuration
	public class KafkaProducerConfig {
	    @Bean
	    public ProducerFactory<String, String> producerFactory_String() {
	        Map<String, Object> configProps = new HashMap<>();
	        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServer);
	        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	        return new DefaultKafkaProducerFactory<>(configProps);
	    }

	    @Bean
	    public KafkaTemplate<String, String> templateKafka() {
	        return new KafkaTemplate<>(producerFactory_String());
	    }
	    
	    
	}

}
