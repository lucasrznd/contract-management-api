package br.com.lucasrznd.contractmanagementapi.controllers.exceptions.handler;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.StandardError;
import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ValidationException;
import br.com.lucasrznd.contractmanagementapi.storage.StorageException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger logger = LogManager.getLogger();

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleNotFoundException(final ResourceNotFoundException ex, final HttpServletRequest request) {
        return ResponseEntity.status(NOT_FOUND).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now()).status(NOT_FOUND.value())
                        .error(NOT_FOUND.getReasonPhrase()).message(ex.getMessage())
                        .path(request.getRequestURI()).build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationException> handleArgumentNotValidException(final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        var error = ValidationException.builder()
                .timestamp(LocalDateTime.now())
                .status(BAD_REQUEST.value())
                .error(BAD_REQUEST.getReasonPhrase())
                .message("Exception in validation attributes")
                .errors(new ArrayList<>())
                .path(request.getRequestURI()).build();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolationException(final DataIntegrityViolationException ex, final HttpServletRequest request) {
        return ResponseEntity.status(CONFLICT).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now()).status(CONFLICT.value())
                        .error(CONFLICT.getReasonPhrase()).message(ex.getMessage())
                        .path(request.getRequestURI()).build()
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<StandardError> handleMaxUploadSizeExceededException(final MaxUploadSizeExceededException ex, final HttpServletRequest request) {
        return ResponseEntity.status(PAYLOAD_TOO_LARGE).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now()).status(PAYLOAD_TOO_LARGE.value())
                        .error(PAYLOAD_TOO_LARGE.getReasonPhrase()).message("File exceeds the maximum allowed size")
                        .path(request.getRequestURI()).build()
        );
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<StandardError> handleStorageException(final StorageException ex, final HttpServletRequest request) {
        logger.error(ex.getMessage(), ex);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now()).status(INTERNAL_SERVER_ERROR.value())
                        .error(INTERNAL_SERVER_ERROR.getReasonPhrase()).message(ex.getMessage())
                        .path(request.getRequestURI()).build()
        );
    }

}
