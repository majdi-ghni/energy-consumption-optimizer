package de.adesso.energyconsumptionoptimizer.service.user;

import de.adesso.energyconsumptionoptimizer.model.user.User;
import de.adesso.energyconsumptionoptimizer.model.user.UserDto;
import de.adesso.energyconsumptionoptimizer.model.user.UserMapper;
import de.adesso.energyconsumptionoptimizer.repository.user.UserRepository;
import de.adesso.energyconsumptionoptimizer.security.auth.AuthenticationResponse;
import de.adesso.energyconsumptionoptimizer.security.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
     * @param id hold user id
     * @return user
    */
    public UserDto getUser(UUID id) {
    return this.userMapper.userEntityToDto(this.userRepository.findById(id).get());
    }

    public UserDto getByUsername(String username) {
        return this.userMapper.userEntityToDto(this.userRepository.findByUsername(username).get());
    }
}
