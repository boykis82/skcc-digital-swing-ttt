package org.caltech.miniswing.util.http;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.time.ZonedDateTime;

public class HttpErrorInfo {
    private final ZonedDateTime timestamp;
    private final String path;
    private final HttpStatus httpStatus;
    private final String message;

    @Builder
    protected HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
        this.timestamp = ZonedDateTime.now();
        this.httpStatus = httpStatus;
        this.path = path;
        this.message = message;
    }

    protected int getStatus() {
        return httpStatus.value();
    }

    protected String getError() {
        return httpStatus.getReasonPhrase();
    }

    public static HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, ServerHttpRequest request, Throwable ex) {
        final String path = request.getPath().pathWithinApplication().value();
        final String message = ex.getMessage();

        return HttpErrorInfo.builder().httpStatus(httpStatus).path(path).message(message).build();
    }
}