package de.opentect.kafka.repository;

import de.opentect.kafka.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    Optional<Partner> findByLastName(String lastName);

    Optional<Partner> findByPartnerUUID(String partnerUUID);
}
