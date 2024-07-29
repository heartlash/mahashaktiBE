package com.mahashakti.mahashaktiBE.exception;


import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<MahashaktiException> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {

        MahashaktiException mahashaktiException = new MahashaktiException();
        mahashaktiException.setCode("MSBE403");
        mahashaktiException.setMessage(String.format("Not authorised: %s", request.getDescription(false)));
        mahashaktiException.setStatus("FAILURE");
        mahashaktiException.setErrorMessage(ex.toString());
        return new ResponseEntity<>(mahashaktiException, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MahashaktiException> handleAccessDeniedHandler(AccessDeniedException ex, WebRequest request) {

        MahashaktiException mahashaktiException = new MahashaktiException();
        mahashaktiException.setCode("MSBE403");
        mahashaktiException.setMessage(String.format("Access denied: %s", request.getDescription(false)));
        mahashaktiException.setStatus("FAILURE");
        mahashaktiException.setErrorMessage(ex.toString());
        return new ResponseEntity<>(mahashaktiException, HttpStatus.FORBIDDEN);

    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MahashaktiException> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {

        MahashaktiException mahashaktiException = new MahashaktiException();
        mahashaktiException.setCode("MSBE404");
        mahashaktiException.setMessage("Resource Not Found");
        mahashaktiException.setStatus("FAILURE");
        mahashaktiException.setErrorMessage(ex.getMessage());
        return new ResponseEntity<>(mahashaktiException, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(MismatchException.class)
    public ResponseEntity<MahashaktiException> handleMismatchException(MismatchException ex, WebRequest request) {

        MahashaktiException mahashaktiException = new MahashaktiException();
        mahashaktiException.setCode("MSBE400");
        mahashaktiException.setMessage("Mismatch");
        mahashaktiException.setStatus("FAILURE");
        mahashaktiException.setErrorMessage(ex.getMessage());
        return new ResponseEntity<>(mahashaktiException, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(InvalidDataStateException.class)
    public ResponseEntity<MahashaktiException> handleInvalidDataStateException(InvalidDataStateException ex, WebRequest request) {

        MahashaktiException mahashaktiException = new MahashaktiException();
        mahashaktiException.setCode("MSBE400");
        mahashaktiException.setMessage("Invalid Data");
        mahashaktiException.setStatus("FAILURE");
        mahashaktiException.setErrorMessage(ex.getMessage());
        return new ResponseEntity<>(mahashaktiException, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<MahashaktiException> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {

        MahashaktiException mahashaktiException = new MahashaktiException();
        mahashaktiException.setCode("MSBE403");
        mahashaktiException.setMessage("Expired JWT");
        mahashaktiException.setStatus("FAILURE");
        mahashaktiException.setErrorMessage(ex.toString());
        return new ResponseEntity<>(mahashaktiException, HttpStatus.FORBIDDEN);

    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<MahashaktiException> handleGeneralExceptions(Exception ex, WebRequest request) {
        MahashaktiException mahashaktiException = new MahashaktiException();
        mahashaktiException.setCode("MSBE500");
        mahashaktiException.setMessage("Unexpected Failure");
        mahashaktiException.setStatus("FAILURE");
        mahashaktiException.setErrorMessage(ex.toString());
        return new ResponseEntity<>(mahashaktiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
