package de.adesso.energyconsumptionoptimizer.service.electricitygreenindex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndexDeserializer;
import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndexDto;
import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndexMapper;
import de.adesso.energyconsumptionoptimizer.repository.greenelectricityindex.GreenElectricityIndexRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GreenElectricityIndexService {

    private final GreenElectricityIndexRepository greenElectricityIndexRepository;
    private final GreenElectricityIndexMapper greenElectricityIndexMapper;

    /**
     * Gets the green electricity index for the next 3-4 days starting from 1 or 2 hors before current time
     * from corrently api
     *
     * @param zipCode holds the postal code value
     * @return a list of the green electricity index
     */
    public List<GreenElectricityIndexDto> getGreenElectricIndexOFLocation(String zipCode) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String correntlyUrl = String.format("https://api.corrently.io/v2.0/gsi/prediction?zip=%s", zipCode);
            ResponseEntity<String> response = restTemplate.getForEntity(correntlyUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.getBody());
                JSONArray jsonArray = jsonObject.getJSONArray("forecast");
                ObjectMapper objectMapper = new ObjectMapper();
                // Register the custom deserializer
                SimpleModule module = new SimpleModule();
                module.addDeserializer(GreenElectricityIndexDto.class, new GreenElectricityIndexDeserializer());
                objectMapper.registerModule(module);
                TypeFactory typeFactory = objectMapper.getTypeFactory();
                List<GreenElectricityIndexDto> greenElectricityIndexDtoList = objectMapper.readValue(
                        jsonArray.toString(), typeFactory.constructCollectionType(List.class, GreenElectricityIndexDto.class));
                return greenElectricityIndexDtoList;
            } else {
                throw new RuntimeException("Unexpected response status: " + response.getStatusCodeValue());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the best hour to use the electricity. At the provided hour the co2-emissions are minimum
     *
     * @param zipCode holds the postal code value
     * @return best hour to use electricity with minimum co2-footprint
     */

    public GreenElectricityIndexDto getBestHour(String zipCode) {

        List<GreenElectricityIndexDto> greenElectricityIndexDtoList = greenElectricityIndexMapper.greenElectricityIndexEntityListToDto(
                greenElectricityIndexRepository.findGeiByZipCodeStartingFromNow(zipCode)
        );

        // Green electricity index intervall is 1 hour, so we just need to pick the highest green electricity index
        GreenElectricityIndexDto bestHour = greenElectricityIndexDtoList.stream()
                .max(Comparator.comparingDouble(GreenElectricityIndexDto::getGsi)).get();

        if (bestHour == null) {
            throw new RuntimeException("cant find the best hour of green electricity ");
        } else {
            return bestHour;
        }
    }

    /**
     * Gets the best period for using electricity with minimum co2-footprint. This period could be dynamically given: 1h, 2hs, ... etc.
     *
     * @param zipCode postal code
     * @param hours   number of hours
     * @return best period to use the electricity with considering the desired duration of use
     */
    public List<GreenElectricityIndexDto> getBestPeriod(String zipCode, int hours) {

        // if the period is 1 hour or less we return the best hour
        if (hours <= 1) {
            List<GreenElectricityIndexDto> greenElectricityIndexDto = new LinkedList<>();
            greenElectricityIndexDto.add(getBestHour(zipCode));
            return greenElectricityIndexDto;
        }

        List<GreenElectricityIndexDto> greenElectricityIndexDtoList = greenElectricityIndexMapper.greenElectricityIndexEntityListToDto(
                greenElectricityIndexRepository.findGeiByZipCodeStartingFromNow(zipCode)
        );

        // to save the elements of best green electricity index in list
        List<GreenElectricityIndexDto> bestPeriodList = new LinkedList<>();


        // summing and comparing the green electricity index of the desired duration of usage to get the min co2-emissions
        // if hours = 3 => (co2-emissions1 + co2-emissions2 + co2-emissions3), (co2-emissions2 + co2-emissions3 + co2-emissions4), ... etc.
        int maxIterationIndex = greenElectricityIndexDtoList.size() - hours - 1; // to avoid out of bounds index
        double highestGreenIndex = Integer.MIN_VALUE;
        int bestPeriodStartIndex = -1; // saves where the best period index started

        for (int i = 0; i < maxIterationIndex; i++) {

            double temp = 0;

            for (int j = 0; j < hours; j++) {
                temp += greenElectricityIndexDtoList.get(i + j).getGsi();
            }

            // get average of green electricity index for the desired period
            temp = temp / hours;

            // checking for the max green electricity index
            if (temp > highestGreenIndex) {
                bestPeriodStartIndex = i; // update index
                highestGreenIndex = temp; // update value
            }
        }

        //getting the forecasts of green electricity index from the response list of gsi
        for (int i = 0; i < hours; i++) {
            bestPeriodList.add(greenElectricityIndexDtoList.get(bestPeriodStartIndex + i));
        }
        return bestPeriodList;
    }
}
