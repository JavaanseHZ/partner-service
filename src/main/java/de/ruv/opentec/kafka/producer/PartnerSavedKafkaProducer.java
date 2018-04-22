package de.ruv.opentec.kafka.producer;

import de.ruv.opentec.kafka.model.Partner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PartnerSavedKafkaProducer {

    @Value("${kafka.message.topic.saved}")
    private String topic;

    @Autowired
    private KafkaTemplate<Long, Partner> kafkaTemplate;

    public void sendEvent(Long key, Partner partner) {
        kafkaTemplate.send(topic, key, partner);
    }
}