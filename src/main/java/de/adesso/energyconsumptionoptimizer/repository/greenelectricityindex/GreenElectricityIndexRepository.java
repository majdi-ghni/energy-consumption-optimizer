package de.adesso.energyconsumptionoptimizer.repository.greenelectricityindex;

import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface GreenElectricityIndexRepository extends JpaRepository<GreenElectricityIndex, UUID> {
    @Query("SELECT gei FROM GreenElectricityIndex gei WHERE gei.zipCode = :zipCode")
    List<GreenElectricityIndex> findByZipcode(String zipCode);

    @Query("SELECT gei FROM GreenElectricityIndex gei WHERE gei.zipCode = :zipCode AND gei.startTimeStamp >= :startTime AND gei.endTimeStamp <= :endTime")
    List<GreenElectricityIndex> findGeiByZipCodeAndTimeRange(String zipCode, Instant startTime, Instant endTime);

    @Query("SELECT gei FROM GreenElectricityIndex gei WHERE gei.zipCode = :zipCode AND gei.startTimeStamp >= CURRENT_TIMESTAMP")
    List<GreenElectricityIndex> findGeiByZipCodeStartingFromNow(String zipCode);
}
