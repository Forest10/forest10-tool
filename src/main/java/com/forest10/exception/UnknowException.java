package com.forest10.exception;

/**
 * @author Forest10
 * @date 2018/9/18 下午12:28
 */
public class UnknowException extends RuntimeException {

    public UnknowException() {
    }

    public UnknowException(String message) {
        super(message);
    }

    public UnknowException(String msgTemplate, Object... args) {
        super(String.format(msgTemplate, args));
    }

    public UnknowException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknowException(Throwable cause, String msgTemplate, Object... args) {
        super(String.format(msgTemplate, args), cause);
    }

    public UnknowException(Throwable cause) {
        super(cause);
    }

}