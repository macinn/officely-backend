package pw.react.backend.security.controllers;

import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.security.models.JwtRequest;
import pw.react.backend.security.models.JwtResponse;
import pw.react.backend.security.services.JwtTokenService;
import pw.react.backend.security.services.JwtUserDetailsService;

@RestController
@RequestMapping(path = JwtAuthenticationController.AUTHENTICATION_PATH)
@Profile({"jwt"})
public class JwtAuthenticationController {

    public static final String AUTHENTICATION_PATH = "/authenticate";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final JwtUserDetailsService userDetailsService;

    public JwtAuthenticationController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, JwtUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.username(), authenticationRequest.password());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username());

        final String token = jwtTokenService.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
