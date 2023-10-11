package cn.ccsu.cecs.common.exception;


public class QueryTimeOutException extends RuntimeException {
    public QueryTimeOutException(String message) {
        super(message);
    }
}
