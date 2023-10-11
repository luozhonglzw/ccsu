package cn.ccsu.cecs.common.exception;

public class RepeatSubmitException extends RuntimeException {
    public RepeatSubmitException(String message) {
        super(message);
    }
}
