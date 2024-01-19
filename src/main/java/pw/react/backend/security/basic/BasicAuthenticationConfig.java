package pw.react.backend.security.basic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.security.common.AuthenticationService;
import pw.react.backend.security.common.CommonAuthenticationService;
import pw.react.backend.security.common.CommonUserDetailsService;

@Profile({"!jwt"})
public class BasicAuthenticationConfig {

    @Bean
    public CommonUserDetailsService userDetailsService(UserRepository userRepository) {
        return new CommonUserDetailsService(userRepository);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new DefaultBasicAuthenticationEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OncePerRequestFilter basicAuthenticationRequestFilter(UserDetailsService defaultUserDetailsService, PasswordEncoder passwordEncoder) {
        return new BasicAuthenticationRequestFilter(defaultUserDetailsService, passwordEncoder);
    }

    @Bean
    public AuthenticationService securityService(AuthenticationManager authenticationManager) {
        return new CommonAuthenticationService(authenticationManager);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           AuthenticationEntryPoint authenticationEntryPoint,
                                           OncePerRequestFilter basicAuthenticationRequestFilter
    ) throws Exception {
        return httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint))
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                .requestMatchers(HttpMethod.POST, "/reservations/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/offices/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/users/**").permitAll()

                                .requestMatchers(HttpMethod.PUT, "/reservations/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/offices/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.DELETE, "/reservations/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/offices/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/favicon.ico").permitAll()
                                .anyRequest().authenticated()
                )
                // Add a filter to validate the tokens with every request
                .addFilterBefore(basicAuthenticationRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
