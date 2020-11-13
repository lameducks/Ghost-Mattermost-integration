package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestExceptionHandler extends ErrorHandler {

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, Exception.class})
    public ResponseEntity<ErrorResponse> handle(final Exception ex, final WebRequest request) {
        return handle(ex, request, (status, errorResponse) -> {
            return new ResponseEntity<ErrorResponse>(errorResponse, status);
        });
    }

}