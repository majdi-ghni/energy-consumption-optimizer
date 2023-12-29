package de.adesso.energyconsumptionoptimizer.controller.electricityprice;

import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPrice;
import de.adesso.energyconsumptionoptimizer.model.elecetricityprice.ElectricityPriceDto;
import de.adesso.energyconsumptionoptimizer.service.electricityprice.ElectricityPriceService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Data
@RestController
@RequestMapping("electricity-price")
@RequiredArgsConstructor
public class ElectricityPriceController {

    private final ElectricityPriceService electricPriceService;

/*
    // TODO: is it better to use awattar api or corrently api? corrently returns the market price & local price up to the next 4 days in 15 minutes rate

    /**
     * Retrieves the electric price from awattar api starting from now and
     * up to the next 24 hours (it could be less) in 60 minutes rate
     *
     * @return list holding electric price for the next 24 hours (day_ahead market price)

    @GetMapping("/price/next-24h")
    public List<ElectricityPriceDto> getElectricityPriceOfToday() {
        return electricPriceService.getElectricityPriceOfToday();
    }

    /**
     * Retrieves the price for a specific hour
     *
     * @param time The specific hour for which the price will be fetched.
     *             The time parameter should be in the format of "yyyy-MM-dd'T'HH:mm:ss" (e.g., "2023-06-03T18:00:00").
     *             The time should be in UTC or have a timezone offset. It will be then converted to Timestamp.
     * @return The ElectricityPriceDto object representing the price information for the specified hour.
    @GetMapping("/price/of-hour")
    public ElectricityPriceDto getPriceOfOneHour(@RequestParam String time) throws JsonProcessingException {
        return electricPriceService.getPriceOfOneHour(time);
    }

    /**
     * Retrieves electric prices for a given period. if the start and end date are given.
     * if just the start date is given, it will be return a list of prices for the next
     * 24 hours starting from the given start date.
     *
     * @return list of ElectricPriceDto holding the prices for the given period.

    @GetMapping("/price/")
    public List<ElectricityPriceDto> getPriceOfPeriod(@RequestParam String start, @RequestParam(required = false) String end) {
        return electricPriceService.getPriceOfPeriod(start, end);
    }

 */

    /**
     * Retrieves the electric price from corrently api starting from now and
     * up to the next 4 days (it could be less) in 15 minutes rate
     *
     * @param zipCode postcode
     * @return list of electric price
     */
    @GetMapping("/forecast")
    public List<ElectricityPriceDto> getElectricityPriceForecast(String zipCode) {
        return electricPriceService.getElectricityPricesOfLocation(zipCode);
    }

    /**
     * Retrieves the price for a specific hour
     *
     * @param time The specific hour for which the price will be fetched.
     *             The time parameter should be in the format of "yyyy-MM-dd'T'HH:mm:ss" (e.g., "2023-06-03T18:00:00").
     *             The time should be in UTC or have a timezone offset. It will be then converted to Timestamp.
     * @return The ElectricityPriceDto object representing the price information for the specified hour.
     */
    @GetMapping("/of-hour")
    public ElectricityPriceDto getPriceOfOneHour(@RequestParam String time, @RequestParam String zipCode) {
        return electricPriceService.getPriceOfOneHour(time, zipCode);
    }

    /**
     * Retrieves electric prices of a given period. if the start and end date are given.
     * if just the start date is given, it will be return a list of prices for the next
     * 24 hours starting from the given start date.
     *
     * @param start   start time
     * @param end     end time
     * @param zipCode zip code
     * @return list of ElectricPriceDto holding the prices for the given period.
     */

    @GetMapping("/of-period/")
    public List<ElectricityPriceDto> getPriceOfPeriod(@RequestParam String start, @RequestParam String end, @RequestParam String zipCode) {
        return electricPriceService.getPriceOfPeriod(start, end, zipCode);
    }


    /**
     * Adds electricity prices for the user, representing the information about the
     * electricity consumed during a specific period.
     *
     * @param id                the unique identifier of the user
     * @param electricityPrices the list of electricity price objects for the consumption period
     * @return the updated list of electricity prices for the user
     */
    @PostMapping("/add-price-to-user/{id}")
    public ResponseEntity<String> addElectricityPriceToUser(@PathVariable UUID id, @RequestBody List<ElectricityPrice> electricityPrices, @PathVariable String zipCode) {
        //TODO: implement or delete
        return electricPriceService.addElectricityPriceToUSer(id, electricityPrices);
    }

    /**
     * Fetches the best hour of using electricity in the future (maximal up to 4 days in the future)
     *
     * @return best hour
     */
    @GetMapping("/best-hour")
    public ElectricityPriceDto getBestHour(@RequestParam String zipCode) throws Exception {
        return electricPriceService.getBestHour(zipCode);
    }

    /**
     * Gets the best period for using electricity with the lowest price. This period could be dynamically given: 1h, 2hs, ... etc.
     *
     * @param zipCode postal code
     * @param hours   number of hours
     * @return best period to use the electricity with considering the desired duration of use
     */
    @GetMapping("/best-period")
    public List<ElectricityPriceDto> getBestPeriod(@RequestParam String zipCode, @RequestParam int hours) throws Exception {
        return electricPriceService.getBestPeriod(zipCode, hours);
    }

}
