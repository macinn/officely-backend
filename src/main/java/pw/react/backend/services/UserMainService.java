package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class UserMainService implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserMainService.class);

    protected final UserRepository userRepository;
    protected final PasswordEncoder passwordEncoder;

    public UserMainService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User validateAndSave(User user) {
        if (isValidUser(user)) {
            log.info("User is valid");
            Optional<User> dbUser = userRepository.findByUsername(user.getUsername());
            if (dbUser.isPresent()) {
                log.info("User already exists. Updating it.");
                user.setId(dbUser.get().getId());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            user = userRepository.save(user);
            log.info("User was saved.");
        }
        return user;
    }

    private boolean isValidUser(User user) {
        if (user != null) {
            if (isValid(user.getUsername())) {
                log.error("Empty username.");
                throw new UserValidationException("Empty username.");
            }
            if (isValid(user.getPassword())) {
                log.error("Empty user password.");
                throw new UserValidationException("Empty user password.");
            }
            if (isValid(user.getEmail())) {
                log.error("UEmpty email.");
                throw new UserValidationException("Empty email.");
            }
            return true;
        }
        log.error("User is null.");
        throw new UserValidationException("User is null.");
    }

    private boolean isValid(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public User updatePassword(User user, Authentication authentication) {
        if (user != null && authentication != null && isValidUser(user)) {
            User authUser = (User) authentication.getPrincipal();
            if (authUser.getId().equals(user.getId())) {
                Optional<User> dbUser = userRepository.findById(user.getId());
                if (dbUser.isPresent()) {
                    user.setId(dbUser.get().getId());
                    return userRepository.save(user);
                } else {
                    log.error("User with id {} does not exist.", user.getId());
                    throw new UserValidationException("User with id " + user.getId() + " does not exist.");
                }
            } else {
                log.error("User does not match provided ID");
                throw new UserValidationException("User does not match provided ID");
            }
        } else {
            log.error("User or authentication is null");
            throw new UserValidationException("User or authentication is null");
        }
    }

    @Override
    public Collection<User> batchSave(Collection<User> users) {
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                isValidUser(user);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            return userRepository.saveAll(users);
        } else {
            log.warn("User collection is empty or null.");
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<User> getUsersWithIdGreaterThan1(int pageSize, int pageNum) {
        return userRepository.findAll(PageRequest.of(pageNum, pageSize)).toList();
    }

    @Override
    public boolean authenticate(Long userId, Authentication authentication) {
        if(authentication != null)
        {
            User user = (User) authentication.getPrincipal();
            if(Objects.equals(user.getId(), userId) || user.isAdmin()) {
                return true;
            }
            else {
                log.error("User does not match provided ID");
                return false;
            }
        }
        log.error("Authentication is null");
        return false;
    }
}
