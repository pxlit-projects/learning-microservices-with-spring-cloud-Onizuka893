package be.pxl.services.exceptions;

import org.springframework.http.HttpStatus;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(FeignException.class)
        public void handleFeignStatusException(FeignException e) {
            // Forward the FeignException status and message to the caller
            throw new ResponseStatusException(
                HttpStatus.valueOf(e.status()),
                e.getMessage()
            );
        }

        @ExceptionHandler(ProductClientException.class)
        public ResponseEntity<String> handleResponseStatusException(ProductClientException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        }
    }
