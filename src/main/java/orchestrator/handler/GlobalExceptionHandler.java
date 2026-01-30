package orchestrator.handler;

import lombok.extern.slf4j.Slf4j;
import orchestrator.dto.response.ErrorResponse;
import orchestrator.exceptions.BadRequestException;
import orchestrator.exceptions.ExternalServerException;
import orchestrator.exceptions.InternalServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        log.warn(e.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(400, e.getMessage()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(500, "internal server error"));
    }

    @ExceptionHandler(ExternalServerException.class)
    public ResponseEntity<ErrorResponse> handleExternalServerException(ExternalServerException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(502) // BadGateway 상위 서버 문제
            .body(new ErrorResponse(502, "external server failure"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn(e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(400, e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn(e.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(400, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(500, "UNHANDLED INTERNAL SERVER EXCEPTION OCCURRED!!"));
    }
}
