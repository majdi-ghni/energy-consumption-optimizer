package de.adesso.energyconsumptionoptimizer.model.greenelectricityindex;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface GreenElectricityIndexMapper {
    GreenElectricityIndexDto greenElectricityIndexEntityToDto(GreenElectricityIndex greenElectricityIndex);

    GreenElectricityIndex greenElectricityIndexDtoToEntity(GreenElectricityIndexDto greenElectricityIndexDto);

    List<GreenElectricityIndexDto> greenElectricityIndexEntityListToDto(List<GreenElectricityIndex> greenElectricityIndexList);

    List<GreenElectricityIndex> greenElectricityIndexDtoListToEntity(List<GreenElectricityIndexDto> greenElectricityIndexDtoList);
}
