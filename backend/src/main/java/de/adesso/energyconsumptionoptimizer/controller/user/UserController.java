package de.adesso.energyconsumptionoptimizer.controller.user;

import de.adesso.energyconsumptionoptimizer.model.user.UserDto;
import de.adesso.energyconsumptionoptimizer.repository.user.UserRepository;
import de.adesso.energyconsumptionoptimizer.service.scheduling.SchedulingService;
import de.adesso.energyconsumptionoptimizer.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("user")
@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final SchedulingService schedulingService;


    /**
     * Persist user in the DB
     *
     * @param userDto       hold the user object
     * @param bindingResult Extends the Errors interface for error registration capabilities,
     *                      allowing for a Validator to be applied, and adds binding-specific analysis
     *                      and model building.
     * @return ResponseEntity to tell if the user is successfully persisted or not
     */
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The form validation failed");
        } else if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exist");
        } else if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exist");
        } else {
            userService.createUser(userDto);
            schedulingService.initUserRegister(userDto.getAddress().getZipCode());
            return ResponseEntity.status(HttpStatus.CREATED).body("Uer created successfully");
        }
    }

    @GetMapping("get/{id}")
    public UserDto getUser(@PathVariable UUID id) {
       return this.userService.getUser(id);
    }

    @GetMapping("byUsername/{username}")
    public UserDto getByUsername(@PathVariable String username) {
        return this.userService.getByUsername(username);
    }
}
