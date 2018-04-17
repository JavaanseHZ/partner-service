package de.opentect.kafka.endpoint;

import de.opentect.kafka.model.Partner;
import de.opentect.kafka.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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


    @DeleteMapping("/delete/{partnerUUID}")
    public void deletePartner(@PathVariable String partnerUUID) {
        partnerRepository.delete(partnerRepository.findByPartnerUUID(partnerUUID).get());
    }

    @PostMapping("/save")
    public Partner createPartner(@RequestBody Partner partner) {
        partner.setLastChanged(new Date());
        if (StringUtils.isEmpty(partner.getPartnerUUID())) {
            partner.setPartnerUUID(UUID.randomUUID().toString());
        }
        partnerRepository.save(partner);
        return partner;
    }

}