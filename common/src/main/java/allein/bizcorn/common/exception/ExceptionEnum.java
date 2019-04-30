package allein.bizcorn.common.exception;

public enum ExceptionEnum {

    //Bit 1-2： 大类，00=登录，
    //Bit 3-4: 子类：00=需要重新登录的情况，01=权限受限
    //Bit 5-6: 详细信息
    LOST_CONNECTION_TO_SERVER("999999", "服务失败"),

    USER_NOT_LOGIN("000001", "用户未登录"),
    USER_ACCOUNT_ID_INVALID("000003", "用户id获取失败"),
    USER_ACCOUNT_NOT_EXIST("000004","用户账户错误"),
    USER_ACCOUNT_LOGIN_FAIL("000005","用户名或者密码错误"),
    USER_NOT_AUHTORIZED("000102", "当前用户无权限"),
    USER_EXISTS ("000201", "此用户名已经被占用"),
    USER_REGISTER_FAIL("000202", "用户注册失败"),
    CAPTCH_COOKIE_KEY_INVALID("000201", "图片验证码凭据错误"),
    CAPTCH_INVALID("000202", "图片验证码错误"),

    FILE_UPLOAD_FAIL("010001", "素材上传错误"),
    FILE_NOT_EXISTS("010002", "素材不存在"),
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