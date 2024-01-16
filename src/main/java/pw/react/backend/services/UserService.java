package pw.react.backend.services;

import org.springframework.security.core.Authentication;
import pw.react.backend.models.User;

import java.util.Collection;

public interface UserService {
    User validateAndSave(User user);
    User updatePassword(User user, Authentication auth);
    Collection<User> batchSave(Collection<User> users);
    public Collection<User> getUsersWithIdGreaterThan1(int pageSize, int pageNum);

    boolean authenticate(Long userId, Authentication authentication);
}
