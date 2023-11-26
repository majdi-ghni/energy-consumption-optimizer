package de.adesso.energyconsumptionoptimizer.controller.appliance;

import de.adesso.energyconsumptionoptimizer.model.user.Appliance;
import de.adesso.energyconsumptionoptimizer.service.ApplianceService;
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
    public Appliance addAppliance(@RequestBody Appliance appliance, @PathVariable UUID userId) {
        return applianceService.addAppliance(appliance, userId);
    }
    @PostMapping("/addMany/{userId}")
    public List<Appliance> addAppliances(@RequestBody List<Appliance> appliances, @PathVariable UUID userId) {
        return applianceService.addAppliances(appliances, userId);
    }

    @PutMapping("/update")
    public Appliance updateAppliance(@RequestBody Appliance appliance) {
        return this.applianceService.updateAppliance(appliance);
    }

    @DeleteMapping("/delete")
    public void deleteAppliance(@RequestBody Appliance appliance) {
        this.applianceService.deleteAppliance(appliance);
    }
}