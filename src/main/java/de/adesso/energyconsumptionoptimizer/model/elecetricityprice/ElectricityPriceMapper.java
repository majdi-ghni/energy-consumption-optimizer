package de.adesso.energyconsumptionoptimizer.model.elecetricityprice;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", imports = {ElectricityPriceDto.class, ElectricityPrice.class})
public interface ElectricityPriceMapper {


    ElectricityPriceDto electricityPriceEntityToDto(ElectricityPrice electricityPrice);

    ElectricityPrice electricityPriceDtoToEntity(ElectricityPriceDto electricityPriceDto);

    List<ElectricityPriceDto> electricityPriceEntityListToDtoList(List<ElectricityPrice> electricityPrices);

    List<ElectricityPrice> electricityPriceDtoListToEntity(List<ElectricityPriceDto> electricityPriceDtoList);
}
