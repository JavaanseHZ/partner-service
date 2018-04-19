package de.ruv.opentec.kafka.endpoint;

import de.ruv.opentec.kafka.model.Partner;
import de.ruv.opentec.kafka.model.PartnerDeleted;
import de.ruv.opentec.kafka.producer.PartnerDeletedAvroKafkaProducer;
import de.ruv.opentec.kafka.producer.PartnerSavedKafkaProducer;
import de.ruv.opentec.kafka.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partner")
public class PartnerRestController {

    private final PartnerRepository partnerRepository;

    @Autowired
    private PartnerDeletedAvroKafkaProducer partnerDeletedAvroKafkaProducer;

    @Autowired
    private PartnerSavedKafkaProducer partnerSavedKafkaProducer;

    @Autowired
    PartnerRestController(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @GetMapping("/load")
    @ResponseBody
    public List<Partner> getPartnerListe() {
        return partnerRepository.findAll();
    }


    @DeleteMapping("/delete/{id}")
    public void deletePartner(@PathVariable long id) {
        Partner partner = partnerRepository.findById(id).get();
        partnerRepository.delete(partner);
        PartnerDeleted partnerDeleted = new PartnerDeleted(partner.getId(), partner.getVorname(), partner.getNachname());
        partnerDeletedAvroKafkaProducer.sendEvent(partner.getId(), partnerDeleted);

    }

    @PostMapping("/save")
    public Partner saveParter(@RequestBody Partner partner) {
        partnerRepository.save(partner);
        partnerSavedKafkaProducer.sendEvent(partner.getId(), partner);
        return partner;
    }

}