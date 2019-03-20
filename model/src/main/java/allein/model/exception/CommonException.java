package allein.model.exception;

public class CommonException extends ErrorCodeException {

    /**
     *
     */
    private static final long serialVersionUID = -3837659312079505063L;


    public CommonException(String errCode, String msg) {

        ErrorCode code = new ErrorCode();

        code.setRetcode(errCode);

        code.setMessage(msg);

        this.setErrorCode(code);

    }

    public CommonException(String errCode, String msg, Throwable cause) {

        ErrorCode code = new ErrorCode();

        code.setRetcode(errCode);

        code.setMessage(msg);

        this.setErrorCode(code);
    }

    public CommonException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommonException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public CommonException(ExceptionEnum exCode) {
        super(exCode.code(), exCode.desc());
    }

    public CommonException() {

    }
}
