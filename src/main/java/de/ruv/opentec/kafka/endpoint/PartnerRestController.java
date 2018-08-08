package de.ruv.opentec.kafka.endpoint;

import de.ruv.opentec.kafka.model.Address;
import de.ruv.opentec.kafka.model.Name;
import de.ruv.opentec.kafka.model.Partner;
import de.ruv.opentec.kafka.model.PartnerCreated;
import de.ruv.opentec.kafka.producer.PartnerCreatedAvroKafkaProducer;
import de.ruv.opentec.kafka.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partner")
public class PartnerRestController {

    private final PartnerRepository partnerRepository;

    @Autowired
    private PartnerCreatedAvroKafkaProducer partnerCreatedAvroKafkaProducer;

    @Autowired
    PartnerRestController(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @GetMapping("/load")
    @ResponseBody
    public List<Partner> getPartnerListe() {
        return partnerRepository.findAll();
    }


    @PostMapping("/create")
    public Partner createParter(@RequestBody Partner partner) {
        partnerCreatedAvroKafkaProducer.sendEvent(partner.getId(), partner);
        return partner;
    }

}