package pw.react.backend.security.common;

public interface AuthenticationService {
    void authenticate(String username, String password) throws Exception;
}
