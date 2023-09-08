package ru.hogwarts.school.helpers.exceptionHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.hogwarts.school.exceptions.EntryAlreadyExistsException;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(EntryAlreadyExistsException.class)
    public ResponseEntity<String> handleEntryAlreadyExistsException (WebRequest request, EntryAlreadyExistsException ex) {
        logger.warn(request.getDescription(true) + " Attempt to create already existing entity");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException (WebRequest request, NoSuchElementException ex) {
        logger.warn(request.getDescription(true) + " Attempt to reach nonexistent entity");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException (WebRequest request, IllegalArgumentException ex) {
        logger.warn(request.getDescription(true) + " Bad arguments provided");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
