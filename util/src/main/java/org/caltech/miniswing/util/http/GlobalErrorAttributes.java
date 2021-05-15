package org.caltech.miniswing.util.http;

import org.caltech.miniswing.exception.DataIntegrityViolationException;
import org.caltech.miniswing.exception.IllegalServiceStatusException;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.exception.NotFoundDataException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(request, options);

        Throwable ex = getError(request);
        map.put("exception", ex.getClass().getSimpleName());
        map.put("message", ex.getMessage());

        if (ex instanceof NotFoundDataException) {
            map.put("status", HttpStatus.NOT_FOUND);
        } else if (ex instanceof InvalidInputException) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else if (ex instanceof DataIntegrityViolationException ||
                   ex instanceof IllegalServiceStatusException) {
            map.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            map.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return map;
    }
}
