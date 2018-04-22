package de.ruv.opentec.kafka.producer;

import de.ruv.opentec.kafka.model.PartnerDeleted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PartnerDeletedAvroKafkaProducer {

    @Value("${kafka.message.topic.deleted}")
    private String topic;

    @Autowired
    private KafkaTemplate<Long, PartnerDeleted> kafkaTemplateDeleted;

    public void sendEvent(Long key, PartnerDeleted partnerDeleted) {
        kafkaTemplateDeleted.send(topic, key, partnerDeleted);
    }
}