package de.adesso.energyconsumptionoptimizer.controller.greenelectricityindex;

import de.adesso.energyconsumptionoptimizer.model.greenelectricityindex.GreenElectricityIndexDto;
import de.adesso.energyconsumptionoptimizer.service.electricitygreenindex.GreenElectricityIndexService;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/green-electricity-index")
@Data
public class GreenElectricityIndexController {

    private final GreenElectricityIndexService greenElectricityIndexService;

    /**
     * Gets the green electricity index for the next 3-4 days starting from 1 or 2 hors before current time
     *
     * @param zipCode holds the postal code value
     * @return a list of the green electricity index
     */
    @GetMapping("/forecast")
    public List<GreenElectricityIndexDto> getActualGreenElectricIndexOFLocation(@RequestParam String zipCode) {
        return greenElectricityIndexService.getGreenElectricIndexOFLocation(zipCode);
    }

    /**
     * Gets the best hour to use the electricity. At the provided hour the co2-emissions are minimum
     *
     * @param zipCode holds the postal code value
     * @return best hour to use electricity with minimum co2-footprint
     */
    @GetMapping("/best-hour")
    public GreenElectricityIndexDto getBestHour(@RequestParam String zipCode) {
        return greenElectricityIndexService.getBestHour(zipCode);
    }

    /**
     * Gets the best period for using electricity with minimum co2-footprint. This period could be dynamically given: 1h, 2hs, ... etc.
     *
     * @param zipCode postal code
     * @param hours   number of hours
     * @return best period to use the electricity with considering the desired duration of use
     */
    @GetMapping("/best-period")
    public List<GreenElectricityIndexDto> getBestPeriod(@RequestParam String zipCode, @RequestParam int hours) {
        return greenElectricityIndexService.getBestPeriod(zipCode, hours);
    }


    // TODO: calculate how much co2-emissions could the user saves on desired hour


    // TODO: Create user, user id to get the infos of how much did the users saves from start point of using the app


}
