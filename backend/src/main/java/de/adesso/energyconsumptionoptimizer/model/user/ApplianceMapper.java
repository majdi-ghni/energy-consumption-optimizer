package de.adesso.energyconsumptionoptimizer.model.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = UserMapper.class, imports = UserMapper.class)
public interface ApplianceMapper {
    @Mapping(target = "userDto", source = "user")
    ApplianceDto entityToDto(Appliance appliance);

    @Mapping(target = "user", source = "userDto")
    Appliance dtoToEntity(ApplianceDto applianceDto);
}
