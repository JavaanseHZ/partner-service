package de.ruv.opentec.kafka.endpoint;

import de.ruv.opentec.kafka.model.Partner;
import de.ruv.opentec.kafka.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partner")
public class PartnerRestController {

    private final PartnerRepository partnerRepository;

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
        partnerRepository.delete(partnerRepository.findById(id).get());
    }

    @PostMapping("/save")
    public Partner saveParter(@RequestBody Partner partner) {
        partnerRepository.save(partner);
        return partner;
    }

}