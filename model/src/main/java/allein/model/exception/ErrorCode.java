package allein.model.exception;

import java.io.Serializable;


public class ErrorCode implements Serializable {
    private static final long serialVersionUID = -7767059598469242922L;
    private static final ErrorCode success = new ErrorCode("common", Integer.valueOf(0));
    private String category;
    private Object code;
    private String[] args;
    private String message;
    private String retcode;

    public ErrorCode() {
        this.category = "default";
        this.code = "";
    }

    public ErrorCode(Object code) {
        this();
        this.code = code;
    }

    public ErrorCode(String category, Object code) {
        this(code);
        this.category = category;
    }

    public ErrorCode(String category, Object code, String[] args) {
        this(category, code);
        this.args = args;
    }

    public static ErrorCode success() {
        return success;
    }

    public static ErrorCode code(Object code) {
        return new ErrorCode(code);
    }

    public static ErrorCode code(String category, Object code) {
        return new ErrorCode(category, code);
    }

    public static ErrorCode code(String category, Object code, String[] args) {
        return new ErrorCode(category, code, args);
    }

    public String getRetcode() {

        return this.retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String[] getArgs() {
        return this.args;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Object getCode() {
        return this.code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public String getMessage() {
//        if (this.message == null) {
//            String retcodeMapping = ReloadableAppConfig.appConfig.get(this.code.toString());
//            if (retcodeMapping != null && retcodeMapping.length() > 0) {
//                String[] retcodeValue = retcodeMapping.split(",");
//                if (retcodeValue.length > 1) {
//                    return retcodeValue[1];
//                }
//            }
//
//            return this.code.toString();
//        } else {
        return this.message;
//        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return this.message;
//        return ToStringBuilder.reflectionToString(this);
    }
}
