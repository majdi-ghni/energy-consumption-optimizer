package de.adesso.energyconsumptionoptimizer.model.electricitypriceandgreenindex;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ElectricityPriceAndGreenIndexMapper {
    ElectricityPriceAndGreenIndexDto electricityPriceAndGreenIndexEntityToDto(ElectricityPriceAndGreenIndex electricityPriceAndGreenIndex);

    ElectricityPriceAndGreenIndex electricityPriceAndGreenIndexDtoToEntity(ElectricityPriceAndGreenIndexDto electricityPriceAndGreenIndexDto);

    List<ElectricityPriceAndGreenIndexDto> electricityPriceAndGreenIndexEntityListToDtoList(List<ElectricityPriceAndGreenIndex> electricityPriceAndGreenIndexList);

    List<ElectricityPriceAndGreenIndex> electricityPriceAndGreenIndexDtoListToEntity(List<ElectricityPriceAndGreenIndexDto> electricityPriceAndGreenIndexDtoList);
}
