package de.adesso.energyconsumptionoptimizer.model.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", imports = {UserDto.class, User.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User updateUserDto(UserDto source, @MappingTarget User target);

    UserDto userEntityToDto(User user);

    User userDtoToEntity(UserDto userDto);
}
