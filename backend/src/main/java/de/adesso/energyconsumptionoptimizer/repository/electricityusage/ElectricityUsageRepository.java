package de.adesso.energyconsumptionoptimizer.repository.electricityusage;

import de.adesso.energyconsumptionoptimizer.model.electricityusage.ElectricityUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ElectricityUsageRepository extends JpaRepository<ElectricityUsage, UUID> {

    @Query("SELECT up from ElectricityUsage up WHERE up.userId = :userId")
    List<ElectricityUsage> findAllByUserId(UUID userId);

    @Query("SELECT up from ElectricityUsage up WHERE up.usagePeriodId = :usagePeriodId")
    List<ElectricityUsage> getAllUsagesByUsagePeriodId(UUID usagePeriodId);

    @Query("SELECT up from ElectricityUsage up WHERE up.applianceId = :applianceId")
    List<ElectricityUsage> getAllUsagesByUsageApplianceIda(UUID applianceId);
}
