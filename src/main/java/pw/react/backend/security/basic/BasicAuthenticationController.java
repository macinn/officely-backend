package pw.react.backend.security.basic;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.security.common.AuthenticationService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping(path = BasicAuthenticationController.AUTHENTICATION_PATH)
@Profile({"!jwt"})
public class BasicAuthenticationController {

    public static final String AUTHENTICATION_PATH = "/auth";

    private final AuthenticationService authenticationService;

    public BasicAuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestHeader HttpHeaders headers) throws Exception {

        String authorization = headers.get("Authorization").get(0);
        String[] split = new String(
                Base64.getDecoder().decode(authorization.substring("Basic ".length())),
                StandardCharsets.UTF_8)
                .split(":");

        authenticationService.authenticate(split[0], split[1]);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> invalidateToken(HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
