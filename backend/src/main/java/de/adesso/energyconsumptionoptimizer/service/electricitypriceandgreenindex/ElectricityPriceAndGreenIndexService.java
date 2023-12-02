package de.adesso.energyconsumptionoptimizer.service.electricitypriceandgreenindex;

import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPriceDto;
import de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexDto;
import de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexMapper;
import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndexDto;
import de.adesso.energyconsumptionoptimizer.repository.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ElectricityPriceAndGreenIndexService {

    private final ElectricityPriceAndGreenIndexRepository electricityPriceAndGreenIndexRepository;
    private final ElectricityPriceAndGreenIndexMapper electricityPriceAndGreenIndexMapper;

    /**
     * checks if start date was given and convert it from string to Instant. It also reset the minutes and seconds to zero
     * because awattar API gives the price  starting at the 0th minute of each hour
     *
     * @param input holds the date as string
     * @return actual date if input was null or empty. otherwise it will return the
     * converted given string
     */
    public static Instant getStringAsInstant(String input) {
        if (input == null || input.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            Instant nowInstant = now.atZone(ZoneId.of("Europe/Berlin")).toInstant().truncatedTo(ChronoUnit.HOURS);
            return nowInstant;
        }
        Instant adjustedInstant = Instant.parse(input).truncatedTo(ChronoUnit.HOURS);
        return adjustedInstant;
    }


    /**
     * Retrieves the price and green index for a specific hour
     *
     * @param time The specific hour for which the data will be fetched.
     *             The time parameter should be in the format of "yyyy-MM-dd'T'HH:mm:ss" (e.g., "2023-06-03T18:00:00").
     *             The time should be in UTC or have a timezone offset. It will be then converted to Timestamp.
     * @return The ElectricityPriceAndGreenIndexDto object representing the price information for the specified hour.
     */
    public ElectricityPriceAndGreenIndexDto getPriceAndGreenIndexOfOneHour(String time, String zipCode) {
        Instant startTime = getStringAsInstant(time);
        Instant endTime = startTime.plus(1, ChronoUnit.HOURS); // the end time after one hour of the start time;

        List<ElectricityPriceAndGreenIndexDto> electricityPriceAndGreenIndexDtoList = electricityPriceAndGreenIndexMapper.electricityPriceAndGreenIndexEntityListToDtoList(
                electricityPriceAndGreenIndexRepository.findPriceAndGreenIndexByZipCodeAndTimeRange(zipCode, startTime, endTime)
        );

        if (!electricityPriceAndGreenIndexDtoList.isEmpty()) {
            return electricityPriceAndGreenIndexDtoList.get(0);
        } else {
            throw new RuntimeException("There is no price and green electricity index founded for the given time!");
        }
    }

    /**
     * Retrieves prices and green electricity index of a given period.
     *
     * @param start   start time
     * @param end     end time
     * @param zipCode zip code
     * @return list of ElectricPriceAndGreenIndexDto of the given period.
     */
    public List<ElectricityPriceAndGreenIndexDto> getPriceAndGreenIndexOfPeriod(String start, String end, String zipCode) {
        Instant startTime = getStringAsInstant(start);
        Instant endTime = getStringAsInstant(end);

        List<ElectricityPriceAndGreenIndexDto> electricityPriceAndGreenIndexDtoList = electricityPriceAndGreenIndexMapper.electricityPriceAndGreenIndexEntityListToDtoList(
                electricityPriceAndGreenIndexRepository.findPriceAndGreenIndexByZipCodeAndTimeRange(zipCode, startTime, endTime)
        );

        if (!electricityPriceAndGreenIndexDtoList.isEmpty()) {
            return electricityPriceAndGreenIndexDtoList;
        } else {
            throw new RuntimeException("There is no price and green electricity index founded for the given period!");
        }
    }

    //TODO:
    public ElectricityPriceAndGreenIndexDto getBestHour(String zipCode) {
        return null;
    }

    public List<ElectricityPriceAndGreenIndexDto> getBestPeriod(String zipCode, int hours) {
        return null;
    }


    /**
     * Creates an object contains a combination between green electricity index and electricity price
     * Those objects will be used to simplify the task of getting the best hour
     * of using electricity based on both the Green Index and the price
     *
     * @param hourlyPriceDtoList           Hourly electricity prices list
     * @param greenElectricityIndexDtoList Green Electricity index list
     */
    public List<ElectricityPriceAndGreenIndexDto> createElectricityPriceAndGreenIndexList(List<ElectricityPriceDto> hourlyPriceDtoList, List<GreenElectricityIndexDto> greenElectricityIndexDtoList) {
        List<ElectricityPriceAndGreenIndexDto> electricityPriceAndGreenIndexDtoList = new ArrayList<>();

        // Get index the size of the shortest list, to combine both lists until the end of the shortest list
        int shortestList = Integer.min(hourlyPriceDtoList.size(), greenElectricityIndexDtoList.size());


        for (int i = 0; i < shortestList; i++) {
            ElectricityPriceAndGreenIndexDto priceAndGreenIndexDto = ElectricityPriceAndGreenIndexDto.builder()
                    .startTimeStamp(hourlyPriceDtoList.get(i).getStartTimeStamp())
                    .endTimeStamp(greenElectricityIndexDtoList.get(i).getEndTimeStamp())
                    .price(hourlyPriceDtoList.get(i).getLocalPrice())
                    .priceUnit(hourlyPriceDtoList.get(i).getUnit())
                    .gsi(greenElectricityIndexDtoList.get(i).getGsi())
                    .standardElectricityCo2InGram(greenElectricityIndexDtoList.get(i).getStandardElectricityCo2InGram())
                    .city(hourlyPriceDtoList.get(i).getCity())
                    .zipCode(hourlyPriceDtoList.get(i).getZipCode())
                    .build();
            electricityPriceAndGreenIndexDtoList.add(priceAndGreenIndexDto);
        }
        return electricityPriceAndGreenIndexDtoList;
    }

    /**
     * Retrieves the electric price and green index from DB starting from now and
     * up to the next 4 days (it could be less) in 1 hour rate
     *
     * @param zipCode postcode
     * @return list of electric price and green index
     */
    public List<ElectricityPriceAndGreenIndexDto> getElectricityPriceAndGreenIndexForecast(String zipCode) {
        List<ElectricityPriceAndGreenIndexDto> electricityPriceAndGreenIndexDtoList = electricityPriceAndGreenIndexMapper.electricityPriceAndGreenIndexEntityListToDtoList(
                electricityPriceAndGreenIndexRepository.findPriceAndGreenIndexByZipCodeStartingFromNow(zipCode)
        );
        return electricityPriceAndGreenIndexDtoList;
    }

    public ElectricityPriceAndGreenIndexDto getActualPriceAndGreenIndex(String zipCode) {
        return getPriceAndGreenIndexOfOneHour("", zipCode);
    }
}
