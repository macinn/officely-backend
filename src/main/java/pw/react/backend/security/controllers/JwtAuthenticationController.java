package pw.react.backend.security.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
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

    public static final String AUTHENTICATION_PATH = "/auth";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final JwtUserDetailsService userDetailsService;

    public JwtAuthenticationController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, JwtUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest,
                                                       HttpServletRequest request) throws Exception {

        authenticate(authenticationRequest.username(), authenticationRequest.password());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username());

        final String token = jwtTokenService.generateToken(userDetails, request);

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

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> invalidateToken(HttpServletRequest request) {
        boolean result = jwtTokenService.invalidateToken(request);
        return result ? ResponseEntity.accepted().build() : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeInvalidTokens() {
        jwtTokenService.removeTokens();
        return ResponseEntity.accepted().build();
    }
}
