package de.ruv.opentec.kafka.repository;

import de.ruv.opentec.kafka.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
}
