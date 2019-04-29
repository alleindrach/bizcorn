package allein.bizcorn.model.output;

import allein.bizcorn.common.exception.ErrorCode;
import allein.bizcorn.common.exception.ErrorCodeException;

import java.io.Serializable;

public class Result<T> implements Serializable{
    final static public int FAILED = 0;
    final static public int SUCCESS = 1;
    int state = 0;
    T data;
    String message = null;
    String reason = null;


    public Result(int state, String message, String reason, T data) {
        this.state = state;
        this.message = message;
        this.data = data;
        this.reason = reason;
    }

    public Result(ErrorCodeException exception) {

        this.message = exception.getMessage();
        this.reason = exception.getErrorCode().getRetcode();
        this.data=null;
    }

    public Result(Exception exception) {

        this.state=0;
        this.message = exception.getMessage();
        if(exception instanceof  ErrorCodeException)
        {
            this.reason=((ErrorCodeException) exception).getErrorCode().getRetcode();
        }
        this.data=null;

    }

    public Result() {


    }

    public boolean isSuccess(){return this.state==1;};
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
        return new Result(1,null,  null,data);
    }
    static public Result successWithMessage(String msg)
    {
        return new Result(1,msg, null,null);
    }
    static public Result failWithException(Exception ex)
    {
        return new Result(ex);
    }
    static public Result failWithMessage(String message)
    {
        return new Result(0,message, null,null);
    }
}
