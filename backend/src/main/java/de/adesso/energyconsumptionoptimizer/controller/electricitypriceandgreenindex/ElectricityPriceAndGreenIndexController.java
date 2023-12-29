package de.adesso.energyconsumptionoptimizer.controller.electricitypriceandgreenindex;

import de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexDto;
import de.adesso.energyconsumptionoptimizer.service.electricitypriceandgreenindex.ElectricityPriceAndGreenIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("electricity-price-and-green-index")
public class ElectricityPriceAndGreenIndexController {
    private final ElectricityPriceAndGreenIndexService electricityPriceAndGreenIndexService;

    /**
     * Retrieves the electric price and green index from DB starting from now and
     * up to the next 4 days (it could be less) in 1 hour rate
     *
     * @param zipCode postcode
     * @return list of electric price and green index
     */
    @GetMapping("/forecast/{zipCode}")
    public List<ElectricityPriceAndGreenIndexDto> getElectricityPriceAndGreenIndexForecast(@PathVariable String zipCode) {
        return electricityPriceAndGreenIndexService.getElectricityPriceAndGreenIndexForecast(zipCode);
    }

    /**
     * Retrieves actual price and green index
     *
     * @return The actual ElectricityPriceAndGreenIndexDto object representing the price information.
     */
    @GetMapping("/actual")
    public ElectricityPriceAndGreenIndexDto getActualPriceAndGreenIndex(@RequestParam String zipCode) {
        return electricityPriceAndGreenIndexService.getActualPriceAndGreenIndex(zipCode);
    }

    /**
     * Retrieves the price and green index for a specific hour
     *
     * @param time The specific hour for which the data will be fetched.
     *             The time parameter should be in the format of "yyyy-MM-dd'T'HH:mm:ss" (e.g., "2023-06-03T18:00:00").
     *             The time should be in UTC or have a timezone offset. It will be then converted to Timestamp.
     * @return The ElectricityPriceAndGreenIndexDto object representing the price information for the specified hour.
     */
    @GetMapping("/of-hour")
    public ElectricityPriceAndGreenIndexDto getPriceAndGreenIndexOfOneHour(@RequestParam String time, @RequestParam String zipCode) {
        return electricityPriceAndGreenIndexService.getPriceAndGreenIndexOfOneHour(time, zipCode);
    }

    /**
     * Retrieves prices and green electricity index of a given period. if the start and end date are given.
     * if just the start date is given, it will be return a list for the next
     * 24 hours starting from the given start date.
     *
     * @param start   start time
     * @param end     end time
     * @param zipCode zip code
     * @return list of ElectricPriceAndGreenIndexDto of the given period.
     */
    @GetMapping("/of-period/")
    public List<ElectricityPriceAndGreenIndexDto> getPriceAndGreenIndexOfPeriod(@RequestParam String start, @RequestParam String end, @RequestParam String zipCode) {
        return electricityPriceAndGreenIndexService.getPriceAndGreenIndexOfPeriod(start, end, zipCode);
    }

    @GetMapping("/cheapest-hour/{zipCode}")
    public ElectricityPriceAndGreenIndexDto getCheapestHour(@PathVariable String zipCode){
        return electricityPriceAndGreenIndexService.getCheapestHour(zipCode);
    }

    @GetMapping("/expensive-hour/{zipCode}")
    public ElectricityPriceAndGreenIndexDto getExpensiveHour(@PathVariable String zipCode){
        return electricityPriceAndGreenIndexService.getExpensiveHour(zipCode);
    }

    @GetMapping("/green-hour/{zipCode}")
    public ElectricityPriceAndGreenIndexDto getGreenHour(@PathVariable String zipCode){
        return electricityPriceAndGreenIndexService.getGreenHour(zipCode);
    }

    @GetMapping("/highest-emissions-hour/{zipCode}")
    public ElectricityPriceAndGreenIndexDto getHighestEmissionsHour(@PathVariable String zipCode){
        return electricityPriceAndGreenIndexService.getHighestEmissionsHour(zipCode);
    }



    // TODO: implement getting best hour & period

    /**
     * Fetches the best hour of using electricity in the future (maximal up to 4 days in the future)
     *
     * @return best hour
     */
    @GetMapping("/best-hour/{zipCode}")
    public ElectricityPriceAndGreenIndexDto getBestHour(@PathVariable String zipCode){
        return electricityPriceAndGreenIndexService.getBestHour(zipCode);
    }


    /**
     * Gets the best period for using electricity with the lowest price. This period could be dynamically given: 1h, 2hs, ... etc.
     *
     * @param zipCode postal code
     * @param hours   number of hours
     * @return best period to use the electricity with considering the desired duration of use
     */
    @GetMapping("/best-period")
    public List<ElectricityPriceAndGreenIndexDto> getBestPeriod(@RequestParam String zipCode, @RequestParam int hours){
        return electricityPriceAndGreenIndexService.getBestPeriod(zipCode, hours);
    }

    @GetMapping("/get/{id}")
    public ElectricityPriceAndGreenIndexDto getById(@PathVariable UUID id) {
        return electricityPriceAndGreenIndexService.getById(id);
    }

}
