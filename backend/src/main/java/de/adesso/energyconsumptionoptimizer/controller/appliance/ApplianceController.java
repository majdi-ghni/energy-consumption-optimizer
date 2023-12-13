package de.adesso.energyconsumptionoptimizer.controller.appliance;

import de.adesso.energyconsumptionoptimizer.model.user.Appliance;
import de.adesso.energyconsumptionoptimizer.model.user.ApplianceDto;
import de.adesso.energyconsumptionoptimizer.service.user.ApplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("appliance")
@RestController
public class ApplianceController {
    private final ApplianceService applianceService;

    @PostMapping("/add/{userId}")
    public ApplianceDto addAppliance(@RequestBody ApplianceDto appliance, @PathVariable UUID userId) {
        return applianceService.addAppliance(appliance, userId);
    }
    @PostMapping("/addMany/{userId}")
    public List<ApplianceDto> addAppliances(@RequestBody List<ApplianceDto> appliances, @PathVariable UUID userId) {
        return applianceService.addAppliances(appliances, userId);
    }

    @PutMapping("/update")
    public ApplianceDto updateAppliance(@RequestBody ApplianceDto appliance) {
        return this.applianceService.updateAppliance(appliance);
    }

    @DeleteMapping("/delete")
    public void deleteAppliance(@RequestBody ApplianceDto appliance) {
        this.applianceService.deleteAppliance(appliance);
    }

    @GetMapping("/get/{userId}")
    public List<ApplianceDto> getByUserId (@PathVariable UUID userId){
        return this.applianceService.getByUserId(userId);

    }

    @GetMapping("/get/{userId}/{deviceName}")
    public ApplianceDto getByUserIdAndDeviceName(@PathVariable UUID userId, @PathVariable String deviceName) throws Exception {
        return this.applianceService.getByUserIdAndDeviceName(userId, deviceName);
    }
}
