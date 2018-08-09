package de.ruv.opentec.kafka;

import de.ruv.opentec.kafka.model.Partner;
import de.ruv.opentec.kafka.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import javax.annotation.PostConstruct;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EnableKafka
@EnableKafkaStreams
public class PartnerSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartnerSpringApplication.class, args);
    }
}

