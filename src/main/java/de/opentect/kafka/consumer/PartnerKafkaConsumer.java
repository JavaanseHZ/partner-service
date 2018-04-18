package de.opentect.kafka.consumer;

import de.opentect.kafka.model.Partner;
import de.opentect.kafka.model.Vertrag;
import de.opentect.kafka.repository.PartnerRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@Service
public class PartnerKafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartnerKafkaConsumer.class);

    @Value("${kafka.message.topic}")
    private String topic;

    @Autowired
    private PartnerRepository partnerRepository;

    @KafkaListener(topics = "${kafka.message.topic}")
    public void receive(ConsumerRecord<String, Vertrag> consumerRecord) {
        Partner partner = consumerRecord.value().getPartner();
        partner.setLastChanged(new Date());
        if (StringUtils.isEmpty(partner.getPartnerUUID())) {
            partner.setPartnerUUID(UUID.randomUUID().toString());
        }
        partnerRepository.save(partner);
    }
}
