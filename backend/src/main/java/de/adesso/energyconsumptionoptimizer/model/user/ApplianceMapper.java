package de.adesso.energyconsumptionoptimizer.model.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = UserMapper.class, imports = UserMapper.class)
public interface ApplianceMapper {
    ApplianceDto entityToDto(Appliance appliance);

    Appliance dtoToEntity(ApplianceDto applianceDto);
}
