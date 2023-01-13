package pw.react.backend.web;

import jakarta.validation.constraints.Email;
import pw.react.backend.models.User;

public record UserDto(Long id, String username, String password, @Email String email) {

    public static UserDto valueFrom(User user) {
        return new UserDto(user.getId(), user.getUsername(), null, user.getEmail());
    }

    public static User convertToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.id());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());
        return user;
    }
}
