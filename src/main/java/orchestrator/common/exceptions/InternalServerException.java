package orchestrator.common.exceptions;

public class InternalServerException extends AppException {

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(String message) {
        super(message);
    }
}
