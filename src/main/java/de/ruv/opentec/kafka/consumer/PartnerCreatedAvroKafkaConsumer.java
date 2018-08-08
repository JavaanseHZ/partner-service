package de.ruv.opentec.kafka.consumer;


import de.ruv.opentec.kafka.model.Address;
import de.ruv.opentec.kafka.model.Name;
import de.ruv.opentec.kafka.model.PartnerCreated;
import de.ruv.opentec.kafka.repository.PartnerRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PartnerCreatedAvroKafkaConsumer {

    Logger logger = LoggerFactory.getLogger(PartnerCreatedAvroKafkaConsumer.class);

    @Autowired
    private PartnerRepository partnerRepository;

    @KafkaListener(topics = "${kafka.message.topic.created}", containerFactory = "partnerCreatedKafkaListenerContainerFactory")
    public void receiveCreated(ConsumerRecord<Long, PartnerCreated> partnerRecord) {
        Address address = partnerRecord.value().getAddress();
        Name name = partnerRecord.value().getName();

        if(address != null) {
            logger.info(address.getCity());
        }
        if(name != null) {
            logger.info(name.getFirstname());
        }
    }
}