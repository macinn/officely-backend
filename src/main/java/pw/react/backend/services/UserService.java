package pw.react.backend.services;

import pw.react.backend.models.User;

import java.util.Collection;

public interface UserService {
    User validateAndSave(User user);
    User updatePassword(User user, String password);
    Collection<User> batchSave(Collection<User> users);
}
