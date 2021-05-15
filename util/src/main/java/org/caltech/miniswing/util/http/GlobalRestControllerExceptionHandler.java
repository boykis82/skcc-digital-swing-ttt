package org.caltech.miniswing.util.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.exception.DataIntegrityViolationException;
import org.caltech.miniswing.exception.IllegalServiceStatusException;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.exception.NotFoundDataException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/*
@RestControllerAdvice
@Slf4j
public class GlobalRestControllerExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundDataException.class)
    public @ResponseBody HttpErrorInfo handleNotFoundExceptions(ServerHttpRequest request, Exception ex) {
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


 */

//-- https://medium.com/@akhil.bojedla/exception-handling-spring-webflux-b11647d8608
/*
@Configuration
@Order(-2)
public class GlobalRestControllerExceptionHandler implements ErrorWebExceptionHandler {

    private ObjectMapper objectMapper;

    public GlobalRestControllerExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        DataBufferFactory dbf = exchange.getResponse().bufferFactory();
        DataBuffer buffer = null;
        HttpStatus httpStatus = null;

        if (ex instanceof NotFoundDataException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof InvalidInputException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof DataIntegrityViolationException ||
                   ex instanceof IllegalServiceStatusException) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        exchange.getResponse().setStatusCode(httpStatus);

        try {
            buffer = dbf.wrap( objectMapper.writeValueAsBytes(
                    HttpErrorInfo.createHttpErrorInfo(httpStatus, exchange.getRequest(), ex) ));
        } catch (JsonProcessingException e) {
            buffer = dbf.wrap( "".getBytes() );
            e.printStackTrace();
        }
        return exchange.getResponse().writeWith( Mono.just(buffer) );
    }
}
 */

public class GlobalRestControllerExceptionHandler  {

}