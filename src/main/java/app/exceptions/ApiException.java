package app.exceptions;

import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiException extends RuntimeException {
    private int code;
    private static final Logger logger = LoggerFactory.getLogger(ApiException.class);

    public ApiException(int code, String msg){
        super(msg);
        this.code = code;
        if (code >= 500) {
            logger.error("ApiException (code={}): {}", code, msg);
        } else {
            logger.warn("ApiException (code={}): {}", code, msg);
        }
    }

    public ApiException (HttpStatus status, String msg) {
        this(status.getCode(), msg);
    }

    public int getCode(){
        return code;
    }
}