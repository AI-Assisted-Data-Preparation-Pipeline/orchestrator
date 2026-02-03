package orchestrator.exceptions;

public class ExternalServerException extends AppException {

    public ExternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalServerException(String message) {
        super(message);
    }
}
