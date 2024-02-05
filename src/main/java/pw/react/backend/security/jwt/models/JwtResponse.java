package pw.react.backend.security.jwt.models;

import pw.react.backend.web.UserDto;

import java.io.Serial;
import java.io.Serializable;

public record JwtResponse(String jwttoken, UserDto user) implements Serializable {
    @Serial
    private static final long serialVersionUID = -8091879091924046844L;
}
