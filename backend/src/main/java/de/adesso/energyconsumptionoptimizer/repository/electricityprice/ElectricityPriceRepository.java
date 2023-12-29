package de.adesso.energyconsumptionoptimizer.repository.electricityprice;

import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ElectricityPriceRepository extends JpaRepository<ElectricityPrice, UUID> {
    @Query("SELECT ep FROM ElectricityPrice ep WHERE ep.zipCode = :zipCode")
    List<ElectricityPrice> findByZipcode(String zipCode);

    @Query("SELECT ep FROM ElectricityPrice ep WHERE ep.zipCode = :zipCode AND ep.startTimeStamp >= :startTime AND ep.endTimeStamp <= :endTime")
    List<ElectricityPrice> findPricesByZipCodeAndTimeRange(String zipCode, Instant startTime, Instant endTime);

    @Query("SELECT ep FROM ElectricityPrice ep WHERE ep.zipCode = :zipCode AND ep.startTimeStamp >= CURRENT_TIMESTAMP")
    List<ElectricityPrice> findPriceByZipCodeStartingFromNow(String zipCode);

}
