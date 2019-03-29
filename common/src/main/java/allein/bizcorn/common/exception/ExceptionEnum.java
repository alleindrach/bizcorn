package allein.bizcorn.common.exception;

public enum ExceptionEnum {

    USER_NOT_LOGIN("0001", "登录验证失败,请重新登录"),
    USER_NOT_AUHTORIZED("0002", "权限受限"),
    USER_ACCOUNT_ID_INVALID("0003", "用户id获取失败"),
    USER_ACCOUNT_NOT_EXIST("0004","用户名或者密码错误"),
    LOST_CONNECTION_TO_SERVER("1000", "服务失败"),
    CAPTCH_COOKIE_KEY_INVALID("2001", "图片验证码凭据错误"),
    ;


    private final String code;
    private final String desc;

    ExceptionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ExceptionEnum fromCode(String xcode) {
        for (ExceptionEnum ec : ExceptionEnum.values()) {
            if (xcode.equals(ec.code())) {
                return ec;
            }
        }
        return null;
    }

    public String code() {
        return code;
    }

    public String desc() {
        return desc;
    }

}