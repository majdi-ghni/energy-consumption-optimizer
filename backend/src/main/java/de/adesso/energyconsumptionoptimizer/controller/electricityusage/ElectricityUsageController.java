package de.adesso.energyconsumptionoptimizer.controller.electricityusage;

import de.adesso.energyconsumptionoptimizer.model.electricityusage.ElectricityUsage;
import de.adesso.energyconsumptionoptimizer.model.electricityusage.ElectricityUsageDto;
import de.adesso.energyconsumptionoptimizer.service.user.ElectricityUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("electricity-usage")
@RestController
public class ElectricityUsageController {
    private final ElectricityUsageService electricityUsageService;

    @GetMapping("/get/{id}")
    public ElectricityUsageDto getElectricityUsageObjectById(@PathVariable UUID id) {
        return this.electricityUsageService.getElectricityUsageObjectById(id);
    }

    @PostMapping("/create")
    public ElectricityUsageDto createElectricityUsageObject(@RequestBody ElectricityUsage electricityUsage) {
        return this.electricityUsageService.createElectricityUsageObject(electricityUsage);
    }

    @PutMapping("/update")
    public ElectricityUsageDto updateElectricityUsageObject(@RequestBody ElectricityUsage electricityUsage) {
        return this.electricityUsageService.updateElectricityUsageObject(electricityUsage);
    }

    @GetMapping("/get/all/by-user/{userId}")
    public List<ElectricityUsageDto> getAllUsagesByUserId(@PathVariable UUID userId) {
        return this.electricityUsageService.getAllUsagesByUserId(userId);
    }

    @GetMapping("/get/all/by-usagePeriodId/{usagePeriodId}")
    public List<ElectricityUsageDto> getAllUsagesByUsagePeriodId(@PathVariable UUID usagePeriodId) {
        return this.electricityUsageService.getAllUsagesByUsagePeriodId(usagePeriodId);
    }

    @GetMapping("/get/all/by-applianceId/{applianceId}")
    public List<ElectricityUsageDto> getAllUsagesByUsageApplianceId(@PathVariable UUID applianceId) {
        return this.electricityUsageService.getAllUsagesByUsageApplianceId(applianceId);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteElectricityUsageObject(@PathVariable UUID id) {
        this.electricityUsageService.deleteElectricityUsageObject(id);
    }

}
