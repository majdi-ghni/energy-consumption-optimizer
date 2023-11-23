package de.adesso.energyconsumptionoptimizer.security.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable() // disable security for these list of endpoints given in requestMatchers parameter (such like login or sign in end pages)
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**")
                .permitAll()
                .requestMatchers("/user/create")
                .permitAll()
                .requestMatchers("/user/get/**")
                .permitAll()
                .anyRequest() // any other request other than the above given request must be authenticated
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // for each request => each request must be authenticated
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler((request, response, ex) -> {
                    System.out.println("Access Denied: " + ex.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }); // we want to execute this filter before calling UsernamePasswordAuthenticationFilter
        return httpSecurity.build();

/*
httpSecurity.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest()
      .permitAll())
      .csrf(AbstractHttpConfigurer::disable);
    return httpSecurity.build(); */
    }
}
