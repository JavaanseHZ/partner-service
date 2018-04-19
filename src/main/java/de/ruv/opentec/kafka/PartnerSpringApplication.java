package de.ruv.opentec.kafka;

import de.ruv.opentec.kafka.model.Partner;
import de.ruv.opentec.kafka.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class PartnerSpringApplication {

    @Autowired
    private PartnerRepository partnerRepository;

    public static void main(String[] args) {
        SpringApplication.run(PartnerSpringApplication.class, args);
    }

    @PostConstruct
    private void loadInitialData() {
        Partner uwe = new Partner();
        uwe.setId(1);
        uwe.setVorname("Uwe");
        uwe.setNachname("Bein");
        partnerRepository.save(uwe);

        Partner andi = new Partner();
        andi.setId(2);
        andi.setVorname("Andi");
        andi.setNachname("Moeller");
        partnerRepository.save(andi);

        Partner tony = new Partner();
        tony.setId(3);
        tony.setVorname("Tony");
        tony.setNachname("Yeboah");
        partnerRepository.save(tony);
    }
}

