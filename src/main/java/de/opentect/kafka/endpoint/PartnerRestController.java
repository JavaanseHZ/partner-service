package de.opentect.kafka.endpoint;

import de.opentect.kafka.model.Partner;
import de.opentect.kafka.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;
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
    public List<Partner> getPartners() {
        return partnerRepository.findAll();
    }


    @DeleteMapping("/delete/{partnerID}")
    public void deletePartner(@PathVariable Long partnerID) {
        partnerRepository.delete(partnerRepository.findById(partnerID).get());
    }

    @PostMapping("/save")
    public Partner createPartner(@RequestBody Partner partner) {
        partner.setLastChanged(new Date());
        partnerRepository.save(partner);
        return partner;
    }

}