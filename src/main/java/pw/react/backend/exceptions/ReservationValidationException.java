package pw.react.backend.exceptions;

public class ReservationValidationException extends RuntimeException {
    private final String resourcePath;

    public ReservationValidationException(String message, String resourcePath) {
        super(message);
        this.resourcePath = resourcePath;
    }

    public ReservationValidationException(String message) {
        this(message, null);
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
