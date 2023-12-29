package de.adesso.energyconsumptionoptimizer.model.electricityusage;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", imports = {ElectricityUsageDto.class, ElectricityUsage.class})
public interface ElectricityUsageMapper {
    ElectricityUsage dtoToEntity(ElectricityUsageDto electricityUsageDto);
    ElectricityUsageDto entityToDto(ElectricityUsage electricityUsage);

}
