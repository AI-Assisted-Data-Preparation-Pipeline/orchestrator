package orchestrator.exceptions;

public class CriticalException extends RuntimeException {

    public CriticalException(String message, Exception e) {
        super(message, e);
    }
}
