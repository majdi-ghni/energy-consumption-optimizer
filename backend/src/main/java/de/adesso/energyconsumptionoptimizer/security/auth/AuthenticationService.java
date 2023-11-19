package de.adesso.energyconsumptionoptimizer.security.auth;

import de.adesso.energyconsumptionoptimizer.model.user.User;
import de.adesso.energyconsumptionoptimizer.model.user.UserDto;
import de.adesso.energyconsumptionoptimizer.repository.user.UserRepository;
import de.adesso.energyconsumptionoptimizer.security.config.JwtService;
import de.adesso.energyconsumptionoptimizer.security.token.TokenType;
import de.adesso.energyconsumptionoptimizer.security.token.accessToken.Token;
import de.adesso.energyconsumptionoptimizer.security.token.accessToken.TokenRepository;
import de.adesso.energyconsumptionoptimizer.security.token.refreshToken.RefreshTokenRepository;
import de.adesso.energyconsumptionoptimizer.security.token.refreshToken.Refreshtoken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private String tokenType = TokenType.BEARER.toString();

    /**
     * Persist user in the DB anc generates token for the user
     *
     * @param request
     * @return generated token in an AuthenticationResponse
     */
    public AuthenticationResponse register(UserDto request) {
        // getting user information from the request to create user variable
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .actualTariff(request.getActualTariff())
                .address(request.getAddress())
                .build();

        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().toString()));
        var savedUser = userRepository.save(user);

        String jwtAccessToken = jwtService.generateAccessToken(user);
        String jwtRefreshToken = jwtService.generateRefreshToken(user);

        Map<String, String> tokens = new HashMap<>();
        saveUserAccessToken(savedUser, jwtAccessToken);
        saveUserRefreshToken(savedUser, jwtRefreshToken);

        tokens.put(tokenType, jwtAccessToken);
        tokens.put(tokenType, jwtRefreshToken);

        return AuthenticationResponse.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .tokens(tokens)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().toString()));

        String jwtAccessToken = jwtService.generateAccessToken(user);
        String jwtRefreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put(tokenType, jwtAccessToken);
        tokens.put(tokenType, jwtRefreshToken);
        saveUserAccessToken(user, jwtAccessToken);
        saveUserRefreshToken(user, jwtRefreshToken);

        return AuthenticationResponse.builder()
                .tokens(tokens)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }


    private void saveUserAccessToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .type(tokenType)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void saveUserRefreshToken(User user, String jwtToken) {
        var refreshToken = Refreshtoken.builder()
                .user(user)
                .token(jwtToken)
                .type("BEARER")
                .expired(false)
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        var validUserRefreshTokens = refreshTokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty() || validUserRefreshTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        validUserRefreshTokens.forEach(refreshtoken -> {
            refreshtoken.setExpired(true);
            refreshtoken.setRevoked(true);
        });
        refreshTokenRepository.deleteAll(validUserRefreshTokens);
        tokenRepository.deleteAll(validUserTokens);
    }

    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            String username = jwtService.extractUsername(jwtToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow();
            revokeAllUserTokens(user);
        }
    }
}
