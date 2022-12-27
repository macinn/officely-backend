package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.models.User;
import pw.react.backend.services.SecurityService;
import pw.react.backend.services.UserService;
import pw.react.backend.web.UserDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Profile({"!jwt"})
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final SecurityService securityService;

    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)) }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @PostMapping(path = "")
    public ResponseEntity<UserDto> createUser(@RequestHeader HttpHeaders headers, @RequestBody UserDto user) {
        if (securityService.isAuthorized(headers)) {
            User newUser = userService.validateAndSave(UserDto.convertToUser(user));
            log.info("Password is not going to be encoded");
            return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.valueFrom(newUser));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(summary = "Create new users")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Users created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(allOf = UserDto.class)) }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @PostMapping(path = "")
    public ResponseEntity<Collection<UserDto>> createUsers(@RequestHeader HttpHeaders headers, @RequestBody Collection<UserDto> users) {
        if (securityService.isAuthorized(headers)) {
            Collection<UserDto> newUsers = userService.batchSave(users.stream().map(UserDto::convertToUser).toList())
                    .stream()
                    .map(UserDto::valueFrom)
                    .toList();
            log.info("Password is not going to be encoded");
            return ResponseEntity.status(HttpStatus.CREATED).body(newUsers);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
