package de.adesso.energyconsumptionoptimizer.service.user;

import de.adesso.energyconsumptionoptimizer.model.electricityusage.ElectricityUsage;
import de.adesso.energyconsumptionoptimizer.model.electricityusage.ElectricityUsageDto;
import de.adesso.energyconsumptionoptimizer.model.electricityusage.ElectricityUsageMapper;
import de.adesso.energyconsumptionoptimizer.repository.electricityusage.ElectricityUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ElectricityUsageService {
    private final ElectricityUsageRepository electricityUsageRepository;
    private final ElectricityUsageMapper electricityUsageMapper;
    public ElectricityUsageDto getElectricityUsageObjectById(UUID id) {
        return this.electricityUsageMapper.entityToDto(this.electricityUsageRepository.getReferenceById(id));
    }

    public ElectricityUsageDto createElectricityUsageObject(ElectricityUsage electricityUsage) {
        return this.electricityUsageMapper.entityToDto(this.electricityUsageRepository.save(electricityUsage));
    }

    public ElectricityUsageDto updateElectricityUsageObject(ElectricityUsage electricityUsage) {
        return this.electricityUsageMapper.entityToDto(this.electricityUsageRepository.save(electricityUsage));
    }

    public void deleteElectricityUsageObject(UUID id) {
        ElectricityUsage electricityUsage = this.electricityUsageRepository.getReferenceById(id);
        this.electricityUsageRepository.delete(electricityUsage);
    }

    public List<ElectricityUsageDto> getAllUsagesByUserId(UUID userId) {
        return this.electricityUsageRepository.findAllByUserId(userId).stream().map(object -> this.electricityUsageMapper.entityToDto(object)).collect(Collectors.toList());
    }

    public List<ElectricityUsageDto> getAllUsagesByUsagePeriodId(UUID usagePeriodId) {
        return this.electricityUsageRepository.getAllUsagesByUsagePeriodId(usagePeriodId).stream().map(object -> this.electricityUsageMapper.entityToDto(object)).collect(Collectors.toList());
    }

    public List<ElectricityUsageDto> getAllUsagesByUsageApplianceId(UUID applianceId) {
        return this.electricityUsageRepository.getAllUsagesByUsageApplianceIda(applianceId).stream().map(object -> this.electricityUsageMapper.entityToDto(object)).collect(Collectors.toList());
    }
}
