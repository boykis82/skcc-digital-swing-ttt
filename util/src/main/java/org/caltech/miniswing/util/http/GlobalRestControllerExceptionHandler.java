package org.caltech.miniswing.util.http;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.exception.DataIntegrityViolationException;
import org.caltech.miniswing.exception.IllegalServiceStatusException;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.exception.NotFoundDataException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalRestControllerExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundDataException.class)
    public @ResponseBody
    HttpErrorInfo handleNotFoundExceptions(ServerHttpRequest request, Exception ex) {
        return HttpErrorInfo.createHttpErrorInfo(NOT_FOUND, request, ex);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(InvalidInputException.class)
    public @ResponseBody HttpErrorInfo handleInvalidInputException(ServerHttpRequest request, Exception ex) {
        return HttpErrorInfo.createHttpErrorInfo(BAD_REQUEST, request, ex);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler({DataIntegrityViolationException.class, IllegalServiceStatusException.class})
    public @ResponseBody HttpErrorInfo handleDataIntegrityViolationException(ServerHttpRequest request, Exception ex) {
        return HttpErrorInfo.createHttpErrorInfo(INTERNAL_SERVER_ERROR, request, ex);
    }
}
