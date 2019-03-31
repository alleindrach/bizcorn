package allein.bizcorn.model.output;

import allein.bizcorn.common.exception.ErrorCodeException;

import java.io.Serializable;

public class Result<T> implements Serializable{
    final static public int FAILED = 0;
    final static public int SUCCESS = 1;
    int state = 0;
    T data;
    String message = null;
    String reason = null;

    public Result(int state, String message, String errorCode, T data) {
        this.state = state;
        this.message = message;
        this.data = data;
        this.reason = errorCode;
    }

    public Result(ErrorCodeException exception) {

        this.message = exception.getMessage();

        this.reason = exception.getErrorCode().getRetcode();
    }

    public Result(Exception exception) {

        this.message = exception.getMessage();

    }
    public Result() {

    }
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    static public Result successWithData(Object data)
    {
        return new Result(1,null,null,data);
    }
    static public Result successWithMessage(String msg)
    {
        return new Result(1,msg,null,null);
    }
    static public Result failWithException(Exception ex)
    {
        return new Result(0,ex.getMessage(),null,null);
    }
    static public Result failWithMessage(String message)
    {
        return new Result(0,message,null,null);
    }
}
