package de.adesso.energyconsumptionoptimizer.service.scheduling;

import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPrice;
import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPriceDto;
import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPriceMapper;
import de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex.ElectricityPriceAndGreenIndex;
import de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexDto;
import de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexMapper;
import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndex;
import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndexDto;
import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndexMapper;
import de.adesso.energyconsumptionoptimizer.repository.electricityprice.ElectricityPriceRepository;
import de.adesso.energyconsumptionoptimizer.repository.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexRepository;
import de.adesso.energyconsumptionoptimizer.repository.greenelectricityindex.GreenElectricityIndexRepository;
import de.adesso.energyconsumptionoptimizer.repository.user.UserRepository;
import de.adesso.energyconsumptionoptimizer.service.electricitygreenindex.GreenElectricityIndexService;
import de.adesso.energyconsumptionoptimizer.service.electricityprice.ElectricityPriceService;
import de.adesso.energyconsumptionoptimizer.service.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SchedulingService {

    // The number of threads to keep in the pool, even if they are idle
    private final int corePoolSize = 1;

    // A scheduled executor service for running tasks in the future
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize);
    private final UserRepository userRepository;
    private final ElectricityPriceService electricityPriceService;
    private final ElectricityPriceRepository electricityPriceRepository;
    private final ElectricityPriceMapper electricityPriceMapper;
    private final GreenElectricityIndexService greenElectricityIndexService;
    private final GreenElectricityIndexRepository greenElectricityIndexRepository;
    private final GreenElectricityIndexMapper greenElectricityIndexMapper;
    private final ElectricityPriceAndGreenIndexRepository electricityPriceAndGreenIndexRepository;
    private final ElectricityPriceAndGreenIndexMapper electricityPriceAndGreenIndexMapper;
    private final ElectricityPriceAndGreenIndexService electricityPriceAndGreenIndexService;

    // Minimum hours of the price forecast before calling corrently api
    @Value("${MINIMUM_FORECAST_HOURS}")
    private int MINIMUM_FORECAST_HOURS;

    public void init() {
        // Get all postal codes from the DB
        List<String> zipCodes = userRepository.findDistinctZipCodes();

        if (zipCodes != null || !zipCodes.isEmpty()) {
            zipCodes.stream().forEach(zip -> {
                electricityPriceInit(zip);
                greenElectricityIndexInit(zip);
                electricityAndGreenIndexInit(zip);
            });
        }
    }

    /**
     * Check if there is data of postal code of registered user and fetch it, if there is no data
     *
     * @param zipCode postal code of the new user
     */
    public void initUserRegister(String zipCode) {
        List<String> zipCodes = new ArrayList<>();
        zipCodes.add(zipCode);

        if (zipCodes != null || !zipCodes.isEmpty()) {
            zipCodes.forEach(zip -> {
                electricityPriceInit(zip);
                greenElectricityIndexInit(zip);
                electricityAndGreenIndexInit(zip);
            });
        }
    }

    /**
     * Check if there is data for electricity prices of the given zipCode in DB for at least 5 hours in the future
     * if not then it send request to corrently api to get the prices and persist them in DB. The existed timestamps
     * in DB will be escaped to avoid duplication
     *
     * @param zip Postcode
     */
    private void electricityPriceInit(String zip) {

        // ToDo: How far in advance users typically want recommendations? is it better to fetch prices daily? -> when asking for the best price, if the prices in DB for less than 24 hours -> get prices from corrently api
        // Get the electricity prices for this postal code form DB
        List<ElectricityPrice> electricityPrices = electricityPriceRepository.findByZipcode(zip);

        // If there are no prices for this postal code
        if (electricityPrices == null || electricityPrices.isEmpty()) {
            List<ElectricityPriceDto> electricityPricesDto = electricityPriceService.getElectricityPricesOfLocation(zip);
            electricityPrices = electricityPriceMapper.electricityPriceDtoListToEntity(electricityPricesDto);
        }

        // If the end time of the last price is less than 8 hours in the future,
        // fetch new prices from the API and save them to the repository
        else if (
                electricityPrices.get(electricityPrices.size() - 1)
                        .getEndTimeStamp()
                        .isBefore(Instant.now().plus(MINIMUM_FORECAST_HOURS, ChronoUnit.HOURS))
        ) {

            // Calling price from external API (Corrently)
            List<ElectricityPriceDto> electricityPricesDto = electricityPriceService.getElectricityPricesOfLocation(zip);

            // Get the newest forecast date from electricityPrices list
            ElectricityPrice electricityPrice = electricityPrices.stream()
                    .max(Comparator.comparing(ElectricityPrice::getEndTimeStamp))
                    .get();

            // Get forecasts with startTimeStamp >= electricityPrice.getEndTimeStamp() to avoid duplicate persistence
            List<ElectricityPriceDto> filteredPricesDto = electricityPricesDto.stream().filter(ep -> ep.getStartTimeStamp().equals(electricityPrice.getEndTimeStamp())
                            || ep.getStartTimeStamp().isAfter(electricityPrice.getEndTimeStamp()))
                    .collect(Collectors.toList());

            electricityPrices = electricityPriceMapper.electricityPriceDtoListToEntity(filteredPricesDto);
        }

        electricityPriceRepository.saveAll(electricityPrices);

        // Get the end time of the last price minus MINIMUM_FORECAST_HOURS in the future
        Instant endTime = electricityPrices.get(electricityPrices.size() - 1).getEndTimeStamp().minus(MINIMUM_FORECAST_HOURS, ChronoUnit.HOURS);
        // Calculate the delay until this end time
        long delay = Duration.between(Instant.now(), endTime).toMillis();
        // Schedule a task to fetch new prices at this end time
        scheduledExecutorService.schedule(() -> electricityPriceInit(zip), delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Check if there is data for electricity prices of the given zipCode in DB for at least 5 hours in the future
     * if not then it send request to corrently api to get the prices and persist them in DB. The existed timestamps
     * in DB will be escaped to avoid duplication
     *
     * @param zip Postcode
     */
    private void greenElectricityIndexInit(String zip) {

        // ToDo: How far in advance users typically want recommendations? is it better to fetch indexes daily? -> when asking for the best index, if the indexes in DB for less than 24 hours -> get indexes from corrently api
        // Get the green electricity index for this postal code form DB
        List<GreenElectricityIndex> electricityIndexes = greenElectricityIndexRepository.findByZipcode(zip);

        if (electricityIndexes == null || electricityIndexes.isEmpty()) {
            List<GreenElectricityIndexDto> greenElectricityIndexDtoList = greenElectricityIndexService.getGreenElectricIndexOFLocation(zip);
            electricityIndexes = greenElectricityIndexMapper.greenElectricityIndexDtoListToEntity(greenElectricityIndexDtoList);
        }

        // If the end time of the last index is less than 8 hours in future
        // fetch new prices from the API and save them to the repository and there is data of indexes in DB
        else if (
                electricityIndexes.get(electricityIndexes.size() - 1)
                        .getEndTimeStamp()
                        .isBefore(Instant.now().plus(MINIMUM_FORECAST_HOURS, ChronoUnit.HOURS))
        ) {

            List<GreenElectricityIndexDto> greenElectricityIndexDtoList = greenElectricityIndexService.getGreenElectricIndexOFLocation(zip);

            // Get the newest forecast date from electricityIndexes list
            GreenElectricityIndex greenElectricityIndex = electricityIndexes.stream()
                    .max(Comparator.comparing(GreenElectricityIndex::getEndTimeStamp))
                    .get();

            // Get forecasts with startTime >= greenElectricityIndex.getEndTime() to avoid duplicate persistence
            List<GreenElectricityIndexDto> filteredIndexesDtoList = greenElectricityIndexDtoList.stream().filter(gei -> gei.getStartTimeStamp().equals(greenElectricityIndex.getEndTimeStamp())
                            || gei.getStartTimeStamp().isAfter(greenElectricityIndex.getEndTimeStamp()))
                    .collect(Collectors.toList());
            electricityIndexes = greenElectricityIndexMapper.greenElectricityIndexDtoListToEntity(filteredIndexesDtoList);

        }
        greenElectricityIndexRepository.saveAll(electricityIndexes);

        // Get the end time of the last green electricity index minus MINIMUM_FORECAST_HOURS in the future
        Instant endTime = electricityIndexes.get(electricityIndexes.size() - 1).getEndTimeStamp().minus(MINIMUM_FORECAST_HOURS, ChronoUnit.HOURS);
        // Calculate the delay until this end time
        long delay = Duration.between(Instant.now(), endTime).toMillis();
        // Schedule a task to fetch new green electricity indexes at this end time
        scheduledExecutorService.schedule(() -> greenElectricityIndexInit(zip), delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Persist a list of objects contains a combination between green electricity index and electricity price
     * Those objects will be used to simplify the task of getting the best hour
     * of using electricity based on both the Green Index and the price
     *
     * @param zip Postcode
     */
    private void electricityAndGreenIndexInit(String zip) {
        int zipCodeLength = 5;
        if (zip.length() != zipCodeLength) {
            zip = "10115";
        }
        List<ElectricityPriceAndGreenIndex> electricityPriceAndGreenIndexList = electricityPriceAndGreenIndexRepository.findPriceAndGreenIndexByZipCodeStartingFromNow(zip);

        // Preparing green electricity index & price lists. Both of lists starts from same start time stamp.
        List<ElectricityPriceDto> electricityPriceDtoList = electricityPriceMapper.electricityPriceEntityListToDtoList(electricityPriceRepository.findPriceByZipCodeStartingFromNow(zip));
        List<ElectricityPriceDto> hourlyPriceDtoList = electricityPriceService.calculateHourlyAveragePrices(electricityPriceDtoList);
        List<GreenElectricityIndexDto> greenElectricityIndexDtoList = greenElectricityIndexMapper.greenElectricityIndexEntityListToDto(greenElectricityIndexRepository.findGeiByZipCodeStartingFromNow(zip));
        List<ElectricityPriceAndGreenIndexDto> electricityPriceAndGreenIndexDtoList = electricityPriceAndGreenIndexService.createElectricityPriceAndGreenIndexList(hourlyPriceDtoList, greenElectricityIndexDtoList);

        // If there are no data for this postal code
        if (electricityPriceAndGreenIndexList == null || electricityPriceAndGreenIndexList.isEmpty()) {
            // Preparing green electricity index & price lists. Both of lists starts from same start time stamp.

            electricityPriceAndGreenIndexList = electricityPriceAndGreenIndexMapper.electricityPriceAndGreenIndexDtoListToEntity(electricityPriceAndGreenIndexDtoList);
        }

        // If the end time of the last object is less than 8 hours in the future,
        // fetch new prices from the API and save them to the repository and there is prices in DB
        else if (
                electricityPriceAndGreenIndexList.get(electricityPriceAndGreenIndexList.size() - 1)
                        .getEndTimeStamp()
                        .isBefore(Instant.now().plus(MINIMUM_FORECAST_HOURS, ChronoUnit.HOURS))
        ) {

            // Get the newest date from electricityPriceAndGreenIndex list
            ElectricityPriceAndGreenIndex electricityPriceAndGreenIndex = electricityPriceAndGreenIndexList.stream()
                    .max(Comparator.comparing(ElectricityPriceAndGreenIndex::getEndTimeStamp))
                    .get();

            // Get data with startTimeStamp >= electricityPrice.getEndTimeStamp() to avoid duplicate persistence
            List<ElectricityPriceAndGreenIndexDto> filteredPricesAndGreenIndexDtoList = electricityPriceAndGreenIndexDtoList.stream().filter(ep -> ep.getStartTimeStamp().equals(electricityPriceAndGreenIndex.getEndTimeStamp())
                            || ep.getStartTimeStamp().isAfter(electricityPriceAndGreenIndex.getEndTimeStamp()))
                    .collect(Collectors.toList());

            electricityPriceAndGreenIndexList = electricityPriceAndGreenIndexMapper.electricityPriceAndGreenIndexDtoListToEntity(filteredPricesAndGreenIndexDtoList);
        }

        electricityPriceAndGreenIndexRepository.saveAll(electricityPriceAndGreenIndexList);

        // Get the end time of the last price minus MINIMUM_FORECAST_HOURS in the future
        Instant endTime = electricityPriceAndGreenIndexList.get(electricityPriceAndGreenIndexList.size() - 1).getEndTimeStamp().minus(MINIMUM_FORECAST_HOURS, ChronoUnit.HOURS);
        // Calculate the delay until this end time
        long delay = Duration.between(Instant.now(), endTime).toMillis();
        // Schedule a task to fetch new prices at this end time
        String finalZip = zip;
        scheduledExecutorService.schedule(() -> electricityAndGreenIndexInit(finalZip), delay, TimeUnit.MILLISECONDS);
    }


/*
    /**
     * Fetches new prices from the API and schedules the next task to fetch prices
     *
     * @param zipCode postal code

    private void fetchPricesFromApiAndSaveThemInDatabase(String zipCode) {
        // Fetch new prices from the API and save them to the repository
        List<ElectricityPriceDto> pricesDtoList = electricityPriceService.getElectricityPricesOfLocation(zipCode);
        List<ElectricityPrice> electricityPrices = electricityPriceMapper.electricityPriceDtoListToEntity(pricesDtoList);
        electricityPriceRepository.saveAll(electricityPrices);

        // Get the end time of the last price minus MINIMUM_FORECAST_HOURS in the future
        Instant endTime = pricesDtoList.get(pricesDtoList.size() - 1).getEndTimeStamp().minus(MINIMUM_FORECAST_HOURS, ChronoUnit.HOURS);
        // Calculate the delay until this end time
        long delay = Duration.between(Instant.now(), endTime).toMillis();
        // Schedule the next task at this end time
        scheduledExecutorService.schedule(() -> fetchPricesFromApiAndSaveThemInDatabase(zipCode), delay, TimeUnit.MILLISECONDS);

    }

    /**
     * Fetches new green electricity indexes from the API and schedules the next task to fetch green electricity indexes
     *
     * @param zipCode postal code

    private void fetchGreenElectricityIndexesFromApiAndSaveThemInDatabase(String zipCode) {
        // Fetch new green electricity indexes from the API and save them to the repository
        List<GreenElectricityIndexDto> greenElectricityIndexDtoList = greenElectricityIndexService.getGreenElectricIndexOFLocation(zipCode);
        List<GreenElectricityIndex> greenElectricityIndexList = greenElectricityIndexMapper.greenElectricityIndexDtoListToEntity(greenElectricityIndexDtoList);

        greenElectricityIndexRepository.saveAll(greenElectricityIndexList);
        // Get the end time of the last green electricity index
        Instant endTime = greenElectricityIndexDtoList.get(greenElectricityIndexDtoList.size() - 1).getEndTimeStamp().minus(MINIMUM_FORECAST_HOURS, ChronoUnit.HOURS);
        // Calculate the delay until this end time
        long delay = Duration.between(Instant.now(), endTime).toMillis();
        // Schedule the next task at this end time
        scheduledExecutorService.schedule(() -> fetchGreenElectricityIndexesFromApiAndSaveThemInDatabase(zipCode), delay, TimeUnit.MILLISECONDS);
    }

 */
}
