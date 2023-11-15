package de.adesso.energyconsumptionoptimizer.service.electricityprice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPrice;
import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPriceDeserializer;
import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPriceDto;
import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPriceMapper;
import de.adesso.energyconsumptionoptimizer.repository.electricityprice.ElectricityPriceRepository;
import de.adesso.energyconsumptionoptimizer.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ElectricityPriceService {
    private final ElectricityPriceMapper electricityPriceMapper;
    private final UserRepository userRepository;
    private final ElectricityPriceRepository electricityPriceRepository;

    @Value("${AWATTAR_ELECTRIC_MARKET_DATA_URL}")
    private String awattarUrl;
    @Value("${CORRENTLY_ELECTRIC_MARKET_DATA_URL}")
    private String correntlyUrl;

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
            return Instant.now();
        }
        Instant adjustedInstant = Instant.parse(input).truncatedTo(ChronoUnit.HOURS);
        return adjustedInstant;
    }

    /**
     * Gets the price for the next 1-4 days starting from 1 or 2 hors before current time
     * from corrently api
     *
     * @param zipCode holds the postal code value
     * @return a list of price
     */
    public List<ElectricityPriceDto> getElectricityPricesOfLocation(String zipCode) {
        try {
            String correntlyGetElectricityPriceEndPoint = String.format(correntlyUrl, zipCode);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(correntlyGetElectricityPriceEndPoint, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {

                JSONObject jsonObject = new JSONObject(response.getBody());
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                ObjectMapper objectMapper = new ObjectMapper();
                // Register the custom deserializer
                SimpleModule module = new SimpleModule();
                module.addDeserializer(ElectricityPriceDto.class, new ElectricityPriceDeserializer());
                objectMapper.registerModule(module);
                TypeFactory typeFactory = objectMapper.getTypeFactory();

                List<ElectricityPriceDto> electricityPricesDto = objectMapper.readValue(jsonArray.toString(),
                        typeFactory.constructCollectionType(List.class, ElectricityPriceDto.class));
                return electricityPricesDto;
            } else {
                throw new RuntimeException("Unexpected response status: " + response.getStatusCodeValue());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Error occurs while communicating with corrently API", e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the electric price for desired time (price for 1-hour)
     *
     * @param time holds the desired time
     * @return ElectricityPriceDto object which holds the price of the desired time
     * @throws JsonProcessingException
     */
    // TODO: share the zip or user id to get the zip and get the localprice of desired hour
    public ElectricityPriceDto getPriceOfOneHour(String time, String zipCode) {
        // setting up the date
        Instant startTime = getStringAsInstant(time);
        Instant endTime = startTime.plus(1, ChronoUnit.HOURS); // the end time after one hour of the start time;

        List<ElectricityPriceDto> electricityPricesDto = electricityPriceMapper.electricityPriceEntityListToDtoList(electricityPriceRepository.findPricesByZipCodeAndTimeRange(zipCode, startTime, endTime));
        if (!electricityPricesDto.isEmpty()) {
            // calculate the price average of desired hour => 15 min intervall means we have 4 prices
            boolean isLocalPrice = true;
            double localElectricityPriceOfHour = getAveragePriceOfAnHour(electricityPricesDto, isLocalPrice);
            double marketElectricityPriceOfHour = getAveragePriceOfAnHour(electricityPricesDto, !isLocalPrice);

            String city = electricityPricesDto.get(0).getCity();
            String unit = electricityPricesDto.get(0).getUnit();
            Instant start = electricityPricesDto.get(0).getStartTimeStamp();
            Instant end = electricityPricesDto.get(electricityPricesDto.size() - 1).getEndTimeStamp();
            var electricityPriceDto = ElectricityPriceDto.builder()
                    .localPrice(localElectricityPriceOfHour)
                    .marketPrice(marketElectricityPriceOfHour)
                    .city(city)
                    .unit(unit)
                    .startTimeStamp(start)
                    .endTimeStamp(end)
                    .zipCode(zipCode)
                    .build();
            return electricityPriceDto;
        } else {
            throw new RuntimeException("There is no price founded for the given period!");
        }
    }

    /**
     * calculate the price average of desired hour
     *
     * @param electricityPrices
     * @param isLocalPrice
     * @return
     */

    private double getAveragePriceOfAnHour(List<ElectricityPriceDto> electricityPrices, boolean isLocalPrice) {
        double priceAverage;
        if (isLocalPrice) {
            priceAverage = electricityPrices.stream()
                    .mapToDouble(ElectricityPriceDto::getLocalPrice)
                    .average().getAsDouble();
        } else {
            priceAverage = electricityPrices.stream()
                    .mapToDouble(ElectricityPriceDto::getMarketPrice)
                    .average().getAsDouble();
        }

        return priceAverage;
    }

    /**
     * Retrieves electric prices of a given period
     *
     * @param start holds the start date
     * @param end   holds the end date
     * @return list of ElectricPriceDto holding the prices for the given period
     */
    public List<ElectricityPriceDto> getPriceOfPeriod(String start, String end, String zipCode) {

        Instant startTime = getStringAsInstant(start);
        Instant endTime = getStringAsInstant(end);

        List<ElectricityPrice> electricPrices = electricityPriceRepository.findPricesByZipCodeAndTimeRange(zipCode, startTime, endTime);
        if (electricPrices == null || electricPrices.isEmpty()) {
            throw new RuntimeException("There are no prices for the desired period are found.");
        } else {
            List<ElectricityPriceDto> electricPricesDto = electricityPriceMapper.electricityPriceEntityListToDtoList(electricPrices);

            return electricPricesDto;
        }
    }

    public ResponseEntity<String> addElectricityPriceToUSer(UUID id, List<ElectricityPrice> electricityPrices) {
        return null;
    }

    /**
     * Fetches the best hour of using electricity in the future (maximal up to 4 days in the future)
     *
     * @return best hour
     */
    public ElectricityPriceDto getBestHour(String zipCode) throws Exception {

        List<ElectricityPriceDto> electricityPrices = electricityPriceMapper.electricityPriceEntityListToDtoList(electricityPriceRepository.findPriceByZipCodeStartingFromNow(zipCode));

        // List To Hold the prices in 1 hour intervall
        List<ElectricityPriceDto> pricesInOneHourIntervall = pricesInOneHourIntervall(electricityPrices);

        // getting minimum price
        ElectricityPriceDto bestHourDto = pricesInOneHourIntervall.stream()
                .min(Comparator.comparing(ElectricityPriceDto::getLocalPrice))
                .orElse(null);

        return bestHourDto;
    }


    /**
     * Fetches the best period (more than 1 hour) of using electricity in the future (maximal up to 4 days in the future)
     *
     * @return best hour
     */
    public List<ElectricityPriceDto> getBestPeriod(String zipCode, int hours) throws Exception {

        // if the period is 1 hour or less we return the best hour
        if (hours <= 1) {
            List<ElectricityPriceDto> electricityPriceDtoList = new ArrayList<>();
            electricityPriceDtoList.add(getBestHour(zipCode));
            return electricityPriceDtoList;
        }

        List<ElectricityPriceDto> electricityPriceDtoList = electricityPriceMapper.electricityPriceEntityListToDtoList(
                electricityPriceRepository.findPriceByZipCodeStartingFromNow(zipCode)
        );

        // to save the elements of best green electricity index in list
        List<ElectricityPriceDto> bestPeriodList = new ArrayList<>();

        // List To Hold the prices in 1 hour intervall
        List<ElectricityPriceDto> pricesInOneHourIntervall = pricesInOneHourIntervall(electricityPriceDtoList);

        int maxIterationIndex = pricesInOneHourIntervall.size() - hours - 1; // to avoid out of bounds index
        double lowestPrice = Integer.MAX_VALUE;
        int bestPeriodStartIndex = -1; // saves where the best period index started

        for (int i = 0; i < maxIterationIndex; i++) {
            double temp = 0.0;

            for (int j = 0; j < hours; j++) {
                temp += pricesInOneHourIntervall.get(i + j).getLocalPrice();
            }

            // get average of electricity price for the desired period
            temp = temp / hours;

            // checking for the lowest price
            if (temp < lowestPrice) {
                bestPeriodStartIndex = i; // update index
                lowestPrice = temp; // update value
            }
        }

        //getting the forecasts of electricity price from the list of price
        for (int i = 0; i < hours; i++) {
            // TODO: handle empty list
            bestPeriodList.add(pricesInOneHourIntervall.get(bestPeriodStartIndex + i));
        }

        return bestPeriodList;
    }

    /**
     * Returns prices in 1 hour interval
     * Iterates within given electricityPriceDtoList and calculates average price of 1 hour starting from
     * startTimeStamp of each object in electricityPriceDtoList
     *
     * @param electricityPriceDtoList list of prices in 15 minutes interval
     * @return list of prices in 1 hour interval
     * @throws Exception
     */
    private List<ElectricityPriceDto> pricesInOneHourIntervall(List<ElectricityPriceDto> electricityPriceDtoList) throws Exception {

        List<ElectricityPriceDto> pricesInOneHourIntervall = new ArrayList<>();
        // get hourly prices
        // number of prices in 1 hour -> price intervall is 15 minutes -> each hour contains 4 prices
        int pricesInHour = 4;
        for (int i = 0; i + pricesInHour <= electricityPriceDtoList.size(); i++) {
            int end = i + pricesInHour;

            // Get the subset of price forecasts for this hour (4 subsets, 15 minutes intervall)
            List<ElectricityPriceDto> hourlyPrices = electricityPriceDtoList.subList(i, end);

            // get average price of hour
            ElectricityPriceDto tempElectricityPriceDto = calculateAveragePriceOfHour(hourlyPrices);
            pricesInOneHourIntervall.add(tempElectricityPriceDto);
        }
        return pricesInOneHourIntervall;
    }

    /**
     * Returns prices in 1 hour interval
     * Iterates within given electricityPriceDtoList and calculates average price of 1 complete hour minutes: 00 -> 00
     *
     * @param electricityPriceDtoList list of prices in 15 minutes interval
     * @return list of prices in 1 hour interval
     * @throws Exception
     */
    public List<ElectricityPriceDto> calculateHourlyAveragePrices(List<ElectricityPriceDto> electricityPriceDtoList) {

        List<ElectricityPriceDto> hourlyAveragePrices = new ArrayList<>();

        // The number of price intervals in an hour (since each interval is 15 minutes)
        int pricesInHour = 4;

        // The number to add to i index. At first, we add 1 until the startTimeStamp minutes = 00, then we chang its value and add 4 to i
        int iterationRange = 1;
        // Use a ZoneId based on your data's timezone
        ZoneId zoneId = ZoneId.systemDefault();

        // Iterate in steps of 4 (representing 1 hour)
        for (int i = 0; i + pricesInHour <= electricityPriceDtoList.size(); i += iterationRange) {

            List<ElectricityPriceDto> subset = electricityPriceDtoList.subList(i, i + pricesInHour);

            // Convert the Instants to LocalDateTime to get the minute values
            int startMinute = LocalDateTime.ofInstant(subset.get(0).getStartTimeStamp(), zoneId).getMinute();
            int endMinute = LocalDateTime.ofInstant(subset.get(pricesInHour - 1).getEndTimeStamp(), zoneId).getMinute();

            // Check if the subset covers a whole hour from minute 00 to minute 00
            if (startMinute == 0 && endMinute == 0) {
                iterationRange = 4;
                // If the subset covers a whole hour, calculate the average and add to the result list
                ElectricityPriceDto averagePrice = calculateAveragePriceOfHour(subset);
                hourlyAveragePrices.add(averagePrice);
            }
        }

        return hourlyAveragePrices;
    }

    /**
     * calculates average electricity price of an hour
     *
     * @param hourlyPrices prices in 15 minutes interval
     * @return electricity price object with average price of an hour
     */
    public ElectricityPriceDto calculateAveragePriceOfHour(List<ElectricityPriceDto> hourlyPrices) {
        boolean isLocalPrice = true;
        double averageLocalePrice = getAveragePriceOfAnHour(hourlyPrices, isLocalPrice);
        double averageMarketPrice = getAveragePriceOfAnHour(hourlyPrices, !isLocalPrice);

        ElectricityPriceDto tempElectricityPriceDto = ElectricityPriceDto.builder()
                .localPrice(hourlyPrices.get(0).getLocalPrice())
                .marketPrice(hourlyPrices.get(0).getMarketPrice())
                .startTimeStamp(hourlyPrices.get(0).getStartTimeStamp())
                .endTimeStamp(hourlyPrices.get(0).getEndTimeStamp())
                .city(hourlyPrices.get(0).getCity())
                .unit(hourlyPrices.get(0).getUnit())
                .zipCode(hourlyPrices.get(0).getZipCode())
                .build();

        tempElectricityPriceDto.setLocalPrice(averageLocalePrice);
        tempElectricityPriceDto.setMarketPrice(averageMarketPrice);
        tempElectricityPriceDto.setStartTimeStamp(tempElectricityPriceDto.getStartTimeStamp());
        tempElectricityPriceDto.setEndTimeStamp(hourlyPrices.get(hourlyPrices.size() - 1).getEndTimeStamp()); // difference between start & end -timestamp is 1 hour

        return tempElectricityPriceDto;
    }

    /*
    /**
     * Retrieves price of electric for the next 24 hours
     *
     * @return The ElectricityPriceDto object representing the price information for the specified hour.

    public List<ElectricityPriceDto> getElectricityPriceOfToday() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.getForEntity(awattarUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {

                JSONObject jsonObject = new JSONObject(response.getBody());
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                ObjectMapper objectMapper = new ObjectMapper();
                // Register the custom deserializer
                SimpleModule module = new SimpleModule();
                module.addDeserializer(ElectricityPriceDto.class, new ElectricityPriceDeserializer());
                objectMapper.registerModule(module);
                TypeFactory typeFactory = objectMapper.getTypeFactory();

                List<ElectricityPriceDto> electricityPricesDto = objectMapper.readValue(jsonArray.toString(),
                        typeFactory.constructCollectionType(List.class, ElectricityPriceDto.class));
                return electricityPricesDto;
            } else {
                throw new RuntimeException("Unexpected response status: " + response.getStatusCodeValue());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Error occurs while communicating with awattar API", e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the electric price for desired time (price for 1 hour)
     *
     * @param time holds the desired time
     * @return ElectricityPriceDto object which holds the price of the desired time
     * @throws JsonProcessingException

    public ElectricityPriceDto getPriceOfOneHour(String time) throws JsonProcessingException {
        // setting up the date
        Instant startTime = getStringAsInstant(time);
        Instant endDate = startTime.plus(1, ChronoUnit.HOURS); // the end time after one hour of the start time;
        long startTimestamp = startTime.toEpochMilli();
        long endTimestamp = endDate.toEpochMilli();

        String awattarUrl = String.format("https://api.awattar.de/v1/marketdata?start=%d&end=%d", startTimestamp, endTimestamp);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(awattarUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.getBody());
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                ObjectMapper objectMapper = new ObjectMapper();
                // Register the custom deserializer
                SimpleModule module = new SimpleModule();
                module.addDeserializer(ElectricityPriceDto.class, new ElectricityPriceDeserializer());
                objectMapper.registerModule(module);
                TypeFactory typeFactory = objectMapper.getTypeFactory();

                List<ElectricityPriceDto> electricityPriceDtoList = objectMapper.readValue(jsonArray.toString(),
                        typeFactory.constructCollectionType(List.class, ElectricityPriceDto.class));
                //TODO: Check if the list contains more than 1 item. Check the start and end Date

                if (!electricityPriceDtoList.isEmpty()) {
                    ElectricityPriceDto electricityPriceDto = electricityPriceDtoList.get(0);
                    return electricityPriceDto;
                } else {
                    throw new RuntimeException("There is no price founded for the given period!");
                }
            } else {
                throw new RuntimeException("Unexpected response status: " + response.getStatusCodeValue());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves electric prices for a given period
     *
     * @param start holds the start date
     * @param end   holds the end date
     * @return list of ElectricPriceDto holding the prices for the given period

    public List<ElectricityPriceDto> getPriceOfPeriod(String start, String end) {
        // setting up the date
        Instant startTime = getStringAsInstant(start);
        Instant endDate = getStringAsInstant(end);
        // TODO: see why there is 1h added to end date?
        long startTimestamp = startTime.toEpochMilli();
        long endTimestamp = endDate.toEpochMilli();
        String awattarUrl;

        if (end == null || end.isEmpty()) {
            // in case there is no end date given we retrieve data from given time to the next 24 hours
            awattarUrl = String.format("https://api.awattar.de/v1/marketdata?start=%d", startTimestamp);

        } else {
            // in case start & end date given we retrieve data from given time to the next 24 hours
            awattarUrl = String.format("https://api.awattar.de/v1/marketdata?start=%d&end=%d", startTimestamp, endTimestamp);
        }

        try {

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(awattarUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {

                JSONObject jsonObject = new JSONObject(response.getBody());
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                ObjectMapper objectMapper = new ObjectMapper();
                // Register the custom deserializer
                SimpleModule module = new SimpleModule();
                module.addDeserializer(ElectricityPriceDto.class, new ElectricityPriceDeserializer());
                objectMapper.registerModule(module);

                TypeFactory typeFactory = objectMapper.getTypeFactory();
                List<ElectricityPriceDto> electricPricesDto = objectMapper.readValue(jsonArray.toString(),
                        typeFactory.constructCollectionType(List.class, ElectricityPriceDto.class));
                return electricPricesDto;
            } else {
                throw new RuntimeException("Unexpected response status: " + response.getStatusCodeValue());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds electricity prices for the user, representing the information about the
     * electricity consumed during a specific period.
     *
     * @param id                the unique identifier of the user
     * @param electricityPrices the list of electricity price objects for the consumption period
     * @return the updated list of electricity prices for the user

    public ResponseEntity<String> bookPriceToUser(UUID id, List<ElectricityPrice> electricityPrices) {
        User user = userRepository.getReferenceById(id);

        if (user != null) {
            electricityPrices.stream().forEach(ep -> {
                ep.setUser(user);
                electricityPriceRepository.save(ep);
            });
            user.setElectricityPrices(electricityPrices);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }
*/

}
