package orchestrator.handler;

import lombok.extern.slf4j.Slf4j;
import orchestrator.dto.response.ErrorResponse;
import orchestrator.exceptions.CriticalException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CriticalException.class)
    public ResponseEntity<ErrorResponse> handleCriticalException(CriticalException e) {
        log.error("Critical Exception", e);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(500, "sorry internal server error"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn(e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(500, "sorry internal server error"));
    }
}
