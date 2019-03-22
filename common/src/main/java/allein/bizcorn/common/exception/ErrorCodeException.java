package allein.bizcorn.common.exception;


import java.io.Serializable;

public class ErrorCodeException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = -1178568153654761967L;
    private ErrorCode errorCode;

    public ErrorCodeException() {
        this.errorCode = new ErrorCode();
        this.errorCode.setRetcode("ErrorCodeException");
    }

    public ErrorCodeException(ErrorCode errorCode) {
        super(errorCode == null ? "" : errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCodeException(Throwable cause) {
        super(cause);
        this.errorCode = new ErrorCode();
        this.errorCode.setRetcode(cause.getClass().getSimpleName());
    }

    public ErrorCodeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCodeException(String retcode, String retmsg) {
        super(retmsg);
        this.errorCode = new ErrorCode();
        this.errorCode.setRetcode(retcode);
        this.errorCode.setCode(this.getClass().getSimpleName());
        this.errorCode.setMessage(retmsg);
    }

    public ErrorCodeException(String msg) {
        super(msg);
        this.errorCode = new ErrorCode();
        this.errorCode.setCode(this.getClass().getSimpleName());
        this.errorCode.setMessage(msg);
    }

    public ErrorCodeException(String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = new ErrorCode();
        this.errorCode.setCode(cause.getClass().getSimpleName());
        this.errorCode.setMessage(msg);
    }

    public ErrorCodeException(String retcode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = new ErrorCode();
        this.errorCode.setRetcode(retcode);
        this.errorCode.setCode(cause.getClass().getSimpleName());
        this.errorCode.setMessage(msg);
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
