package de.ruv.opentec.kafka.producer;

import de.ruv.opentec.kafka.model.Address;
import de.ruv.opentec.kafka.model.Name;
import de.ruv.opentec.kafka.model.Partner;
import de.ruv.opentec.kafka.model.PartnerCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartnerCreatedAvroKafkaProducer {

    @Value("${kafka.message.topic.created}")
    private String topic;

    @Autowired
    private KafkaTemplate<Long, PartnerCreated> kafkaTemplatePartnerCreated;

//    @Transactional
    public void sendEvent(Long key, Partner partner) {
        PartnerCreated partnerCreatedChunkName = new PartnerCreated();
        partnerCreatedChunkName.setId(partner.getId());
        partnerCreatedChunkName.setName(new Name(partner.getFirstname(), partner.getLastname()));

        PartnerCreated partnerCreatedChunkAddress = new PartnerCreated();
        partnerCreatedChunkAddress.setId(partner.getId());
        partnerCreatedChunkAddress.setAddress(new Address(partner.getStreet(), partner.getCity()));
//        kafkaTemplatePartnerCreated.send(topic, key, partnerCreatedChunkName);
//        kafkaTemplatePartnerCreated.send(topic, key, partnerCreatedChunkAddress);
        kafkaTemplatePartnerCreated.executeInTransaction(t -> {
            t.send(topic, key, partnerCreatedChunkName);
            t.send(topic, key, partnerCreatedChunkAddress);
            return true;
        });
    }
}