package pw.react.backend.exceptions;

public class OfficeValidationException extends RuntimeException {
    private final String resourcePath;

    public OfficeValidationException(String message, String resourcePath) {
        super(message);
        this.resourcePath = resourcePath;
    }

    public OfficeValidationException(String message) {
        this(message, null);
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
