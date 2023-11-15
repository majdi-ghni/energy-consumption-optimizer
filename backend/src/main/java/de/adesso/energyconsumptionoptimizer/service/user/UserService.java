package de.adesso.energyconsumptionoptimizer.service.user;

import de.adesso.energyconsumptionoptimizer.model.user.UserDto;
import de.adesso.energyconsumptionoptimizer.model.user.UserMapper;
import de.adesso.energyconsumptionoptimizer.repository.user.UserRepository;
import de.adesso.energyconsumptionoptimizer.security.auth.AuthenticationResponse;
import de.adesso.energyconsumptionoptimizer.security.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;

    public AuthenticationResponse createUser(UserDto userDto) {
        return authenticationService.register(userDto);
    }

    /**
     * Persist user in the DB
     *
     * @param userDto hold the user object
     * @return created user

    public AuthenticationResponse createUser(UserDto userDto) {
    User user = userMapper.userDtoToEntity(userDto);
    user.setPassword("changeme");
    return null;
    }
     */
}
