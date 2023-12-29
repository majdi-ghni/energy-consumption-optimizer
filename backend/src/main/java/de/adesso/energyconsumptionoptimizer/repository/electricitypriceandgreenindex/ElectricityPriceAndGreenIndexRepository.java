package de.adesso.energyconsumptionoptimizer.repository.electricitypriceandgreenindex;

import de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex.ElectricityPriceAndGreenIndex;
import de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ElectricityPriceAndGreenIndexRepository extends JpaRepository<ElectricityPriceAndGreenIndex, UUID> {
    @Query("SELECT ep FROM ElectricityPriceAndGreenIndex ep WHERE ep.zipCode = :zipCode")
    List<ElectricityPriceAndGreenIndex> findPriceAndGreenIndexByZipcode(String zipCode);

    @Query("SELECT ep FROM ElectricityPriceAndGreenIndex ep WHERE ep.zipCode = :zipCode AND ep.startTimeStamp >= :startTime AND ep.endTimeStamp <= :endTime")
    List<ElectricityPriceAndGreenIndex> findPriceAndGreenIndexByZipCodeAndTimeRange(String zipCode, Instant startTime, Instant endTime);

    @Query("SELECT ep FROM ElectricityPriceAndGreenIndex ep WHERE ep.zipCode = :zipCode AND ep.startTimeStamp >= CURRENT_TIMESTAMP")
    List<ElectricityPriceAndGreenIndex> findPriceAndGreenIndexByZipCodeStartingFromNow(String zipCode);

    @Query("SELECT ep FROM ElectricityPriceAndGreenIndex ep WHERE ep.zipCode = :zipCode AND ep.startTimeStamp >= CURRENT_TIMESTAMP AND ep.price = (SELECT MIN(ep2.price) FROM ElectricityPriceAndGreenIndex ep2 WHERE ep2.zipCode = :zipCode AND ep2.startTimeStamp >= CURRENT_TIMESTAMP)")
    List<ElectricityPriceAndGreenIndex> findCheapestPriceByZipCodeStartingFromNow(String zipCode);


    @Query("SELECT ep FROM ElectricityPriceAndGreenIndex ep WHERE ep.zipCode = :zipCode AND ep.startTimeStamp >= CURRENT_TIMESTAMP AND ep.gsi = (SELECT MIN(ep2.gsi) FROM ElectricityPriceAndGreenIndex ep2 WHERE ep2.zipCode = :zipCode AND ep2.startTimeStamp >= CURRENT_TIMESTAMP)")
    List<ElectricityPriceAndGreenIndex> findGreenHour(String zipCode);

    @Query("SELECT ep FROM ElectricityPriceAndGreenIndex ep WHERE ep.zipCode = :zipCode AND ep.startTimeStamp >= CURRENT_TIMESTAMP AND ep.price = (SELECT MAX(ep2.price) FROM ElectricityPriceAndGreenIndex ep2 WHERE ep2.zipCode = :zipCode AND ep2.startTimeStamp >= CURRENT_TIMESTAMP)")
    List<ElectricityPriceAndGreenIndex> findHighestPriceByZipCodeStartingFromNow(String zipCode);

    @Query("SELECT ep FROM ElectricityPriceAndGreenIndex ep WHERE ep.zipCode = :zipCode AND ep.startTimeStamp >= CURRENT_TIMESTAMP AND ep.gsi = (SELECT MAX(ep2.gsi) FROM ElectricityPriceAndGreenIndex ep2 WHERE ep2.zipCode = :zipCode AND ep2.startTimeStamp >= CURRENT_TIMESTAMP)")
    List<ElectricityPriceAndGreenIndex> findHighestEmissionsHour(String zipCode);

}
