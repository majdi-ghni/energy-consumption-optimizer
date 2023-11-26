package de.adesso.energyconsumptionoptimizer.repository;

import de.adesso.energyconsumptionoptimizer.model.user.Appliance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplianceRepository extends JpaRepository<Appliance, UUID> {
}
