package allein.bizcorn.common.exception;

public enum ExceptionEnum {

    USER_NOT_LOGIN("0001", "用户名或密码不符"),
    USER_NOT_AUHTORIZED("0002", "权限受限"),
    USER_ACCOUNT_ID_INVALID("0003", "用户id获取失败"),
    USER_ACCOUNT_NOT_EXIST("0004","用户名或者密码错误"),
    LOST_CONNECTION_TO_SERVER("1000", "服务失败"),
    CAPTCH_COOKIE_KEY_INVALID("2001", "图片验证码凭据错误"),
    CAPTCH_INVALID("2002", "图片验证码错误"),
    FILE_UPLOAD_FAIL("3001", "文件传输错误"),
    BUNDLE_NOT_EXISTS("3002", "素材包不存在"),
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