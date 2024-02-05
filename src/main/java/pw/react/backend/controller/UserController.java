package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.services.UserService;
import pw.react.backend.web.UserDto;

import java.util.Collection;


@RestController
@RequestMapping(path = UserController.USERS_PATH)
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    static final String USERS_PATH = "/users";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register new users",
            description = "Only admin can add new admin account")
    @PostMapping(path = "")
    @CrossOrigin
    public ResponseEntity<Collection<UserDto>> createUsers(Authentication authentication,
                                                           @RequestBody Collection<UserDto> users) {
        try {
            // check if admin is adding admin
            if (users.stream().anyMatch(UserDto::isAdmin))
                if (authentication == null ||
                        authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")))
                {
                        throw new UserValidationException("User cannot add admin account", USERS_PATH);
                }
            Collection<UserDto> newUsers = userService.batchSave(
                    users.stream().map(UserDto::convertToUser).toList())
                    .stream()
                    .map(UserDto::valueFrom)
                    .toList();

            log.info("Password is not going to be encoded");
            return ResponseEntity.status(HttpStatus.CREATED).body(newUsers);
        } catch (Exception ex) {
            throw new UserValidationException(ex.getMessage(), USERS_PATH);
        }
    }

    @Operation(summary = "Query users")
    @GetMapping(path = "")
    @CrossOrigin
    public ResponseEntity<Collection<UserDto>> getUsers(@RequestParam("pageSize") int pageSize,
                                                            @RequestParam("pageNum") int pageNum
    ) {
        try {
            Collection<UserDto> newUsers = userService.getUsersWithIdGreaterThan1(pageSize,pageNum)
                            .stream()
                    .map(UserDto::valueFrom)
                    .toList();
            log.info("Password is not going to be encoded");
            return ResponseEntity.status(HttpStatus.CREATED).body(newUsers);
        } catch (Exception ex) {
            throw new UserValidationException(ex.getMessage(), USERS_PATH);
        }
    }

    @Operation(summary = "Update user")
    @PutMapping(path = "/")
    @CrossOrigin
    public ResponseEntity<UserDto> updateUser(Authentication authentication, @RequestBody UserDto user) {
        try {
            UserDto newUser = UserDto.valueFrom(userService.updatePassword(UserDto.convertToUser(user), authentication));
            log.info("Password is not going to be encoded");
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception ex) {
            throw new UserValidationException(ex.getMessage(), USERS_PATH);
        }
    }
}
