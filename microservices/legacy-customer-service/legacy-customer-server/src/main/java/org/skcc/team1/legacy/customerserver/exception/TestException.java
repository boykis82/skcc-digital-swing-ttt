package org.skcc.team1.legacy.customerserver.exception;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class TestException extends RuntimeException{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public TestException() {
        super();
    }

    public TestException(String message) {
        super(message);

        JSONObject error_message = new JSONObject();
        error_message.put("date", LocalDateTime.now());
        error_message.put("error_message", message);
        error_message.put("error_id", TestException.class.getName());

        System.out.println(error_message.toString());

        logger.error(error_message.toString());
    }

    public TestException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestException(Throwable cause) {
        super(cause);
    }

    protected TestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
