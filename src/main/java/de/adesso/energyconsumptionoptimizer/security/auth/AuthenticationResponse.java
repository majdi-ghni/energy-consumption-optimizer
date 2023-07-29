package de.adesso.energyconsumptionoptimizer.security.auth;

import de.adesso.energyconsumptionoptimizer.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationResponse {
    private String username;
    private Role role;
    private Map<String, String> tokens;
}
