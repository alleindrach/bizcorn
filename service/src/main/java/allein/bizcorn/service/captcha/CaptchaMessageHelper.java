package allein.bizcorn.service.captcha;


import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.model.security.CaptchaResult;
import allein.bizcorn.service.captcha.config.CaptchaProperties;
import allein.bizcorn.service.captcha.message.CaptchaMessageSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * 短信/邮箱验证码发送、校验
 *
 */
public class CaptchaMessageHelper {

    /**
     * KEY:验证码
     */
    public static final String KEY_CAPTCHA = "captcha:code";
    /**
     * KEY:间隔时间
     */
    public static final String KEY_INTERVAL = "captcha:interval";
    /**
     * KEY:发送次数
     */
    public static final String KEY_LIMIT_TIME = "captcha:time";
    /**
     * KEY:验证结果
     */
    public static final String KEY_CHECK_RESULT = "captcha:result";

    private long expire;
    private char[] charSource;
    private int charLength;
    private long interval;
    private long limitTime;
    private long limitInterval;

    @Autowired
    private ICacheAccessor cacheAccessor;

    public CaptchaMessageHelper(CaptchaProperties captchaProperties) {
        CaptchaProperties.Sms sms = captchaProperties.getSms();
        this.expire = sms.getExpire();
        String source = StringUtils.isBlank(sms.getCharSource()) ? sms.getCharSource() : "0123456789";
        this.charSource = source.toCharArray();
        this.charLength = sms.getCharLength();
        this.interval = sms.getInterval();
        this.limitTime = sms.getLimitTime();
        this.limitInterval = sms.getLimitInterval();
    }
    private boolean isMobile(String mobile)
    {
        return true;
    }
    private boolean isEmail(String mailbox)
    {
        return true;
    }
    /**
     * 生成手机验证码，缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     *
     * @param mobile 手机号
     * @param captchaCachePrefix 缓存前缀 SecurityConstants.SECURITY_KEY
     * @return Captcha 封装
     */
    public CaptchaResult generateMobileCaptcha(String mobile, String captchaCachePrefix) {
        CaptchaResult result = new CaptchaResult();
        if (!isMobile(mobile)) {
            result.setSuccess(false);
            result.setMessage(CaptchaMessageSource.getMessage("phone.format.incorrect", mobile));
            return result;
        }
        if (checkInterval(mobile, captchaCachePrefix, result)) {
            result.setSuccess(false);
            return result;
        }
        if (checkLimitTime(mobile, captchaCachePrefix)) {
            result.setSuccess(false);
            result.setMessage(CaptchaMessageSource.getMessage("captcha.send.time", String.valueOf(limitInterval)));
            return result;
        }

        // 生成验证码
        String captcha = CaptchaGenerator.generateNumberCaptcha(charLength, charSource);
        String captchaKey = CaptchaGenerator.generateCaptchaKey();

        // cache
        String group = captcha + "_" + mobile;
        cacheAccessor.put(captchaCachePrefix + ":" + KEY_CAPTCHA + ":" + captchaKey, group,expire*60);

        cacheAccessor.put(captchaCachePrefix + ":" + KEY_INTERVAL + ":" + mobile, String.valueOf(System.currentTimeMillis()),interval*60);

        if (limitTime > 0) {
            cacheAccessor.inc(captchaCachePrefix + ":" + KEY_LIMIT_TIME + ":" + mobile, 1L);
            cacheAccessor.expire(captchaCachePrefix + ":" + KEY_LIMIT_TIME + ":" + mobile, limitInterval, TimeUnit.HOURS);
        }

        result.setSuccess(true);
        result.setMessage(CaptchaMessageSource.getMessage("captcha.send.phone", encodeNumber(mobile), String.valueOf(expire)));
        result.setCaptcha(captcha);
        result.setCaptchaKey(captchaKey);

        return result;
    }

    /**
     * 生成邮件验证码，并缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     *
     * @param email 邮箱
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha
     */
    public CaptchaResult generateEmailCaptcha(String email, String captchaCachePrefix) {
        CaptchaResult result = new CaptchaResult();
        if (!isEmail(email)) {
            result.setSuccess(false);
            result.setMessage(CaptchaMessageSource.getMessage("email.format.incorrect", email));
            return result;
        }
        if (checkInterval(email, captchaCachePrefix, result)) {
            result.setSuccess(false);
            return result;
        }
        if (checkLimitTime(email, captchaCachePrefix)) {
            result.setSuccess(false);
            result.setMessage(CaptchaMessageSource.getMessage("captcha.send.time", String.valueOf(limitInterval)));
            return result;
        }

        // 生成验证码
        String captcha = CaptchaGenerator.generateNumberCaptcha(charLength, charSource);
        String captchaKey = CaptchaGenerator.generateCaptchaKey();

        // cache
        String group = captcha + "_" + email;
        cacheAccessor.put(captchaCachePrefix + ":" + KEY_CAPTCHA + ":" + captchaKey, group, expire, TimeUnit.MINUTES);
        cacheAccessor.put(captchaCachePrefix + ":" + KEY_INTERVAL + ":" + email, String.valueOf(System.currentTimeMillis()), interval, TimeUnit.SECONDS);
        if (limitTime > 0) {
            cacheAccessor.inc(captchaCachePrefix + ":" + KEY_LIMIT_TIME + ":" + email, 1L);
            cacheAccessor.expire(captchaCachePrefix + ":" + KEY_LIMIT_TIME + ":" + email, limitInterval, TimeUnit.HOURS);
        }

        result.setSuccess(true);
        result.setMessage(CaptchaMessageSource.getMessage("captcha.send.email", encodeNumber(email), String.valueOf(expire)));
        result.setCaptcha(captcha);
        result.setCaptchaKey(captchaKey);

        return result;
    }

    /**
     * 验证验证码，不检查手机号是否一致。验证通过返回success=true，否则返回相应的错误信息。
     *
     * @param captchaKey 缓存KEY
     * @param captcha 验证码
     * @param captchaCachePrefix 验证码缓存前缀
     * @param cacheCheckResult 是否缓存验证结果
     */
    public CaptchaResult checkCaptcha(String captchaKey, String captcha, String captchaCachePrefix, boolean cacheCheckResult) {
        return checkCaptchaWithNumber(captchaKey, captcha, null, captchaCachePrefix, cacheCheckResult);
    }

    /**
     * 验证验证码，必须传入手机号。验证通过返回success=true，否则返回相应的错误信息。
     *
     * @param captchaKey 缓存KEY uuid
     * @param captcha 验证码
     * @param number 手机号/邮箱
     * @param captchaCachePrefix 验证码缓存前缀 SecurityConstants.SECURITY_KEY
     * @param cacheCheckResult 是否缓存验证结果，缓存则返回缓存了验证结果的KEY
     */
    public CaptchaResult checkCaptcha(String captchaKey, String captcha, String number, String captchaCachePrefix, boolean cacheCheckResult) {
        if (StringUtils.isBlank(number)) {
            CaptchaResult result = new CaptchaResult();
            result.setSuccess(false);
            result.setMessage(CaptchaMessageSource.getMessage("captcha.validate.number-not-null"));
            return result;
        }
        return checkCaptchaWithNumber(captchaKey, captcha, number, captchaCachePrefix, cacheCheckResult);
    }

    private CaptchaResult checkCaptchaWithNumber(String captchaKey, String captcha, String number, String captchaCachePrefix, boolean cacheCheckResult) {
        String group = cacheAccessor.get(captchaCachePrefix + ":" + KEY_CAPTCHA + ":" + captchaKey);
        cacheAccessor.del(captchaCachePrefix + ":" + KEY_CAPTCHA + ":" + captchaKey);
        String[] groupArr = StringUtils.split(group, "_", 2);

        CaptchaResult result = new CaptchaResult();

        if (groupArr == null) {
            result.setSuccess(false);
            result.setMessage(CaptchaMessageSource.getMessage("captcha.validate.overdue"));
            return result;
        }

        // 校验发送验证码时的手机号与修改的手机号是否一致
        if (number != null) {
            if (!StringUtils.equals(groupArr[1], number)) {
                result.setSuccess(false);
                result.setMessage(CaptchaMessageSource.getMessage("captcha.validate.number-not-match", number));
                return result;
            }
        }

        // 验证码是否正确
        if (!StringUtils.equalsIgnoreCase(groupArr[0], captcha)) {
            result.setSuccess(false);
            result.setMessage(CaptchaMessageSource.getMessage("captcha.validate.incorrect"));
            return result;
        }

        if (cacheCheckResult) {
            return cacheCheckResult(captchaCachePrefix);
        }

        result.setSuccess(true);

        return result;
    }

    /**
     * 检查上一次验证码是否校验通过. 验证码正确，返回success=true，否则返回相应的message
     *
     * @param lastCheckKey 上一次校验的key
     * @param captchaCachePrefix 缓存前缀
     */
    public CaptchaResult checkLastResult(String lastCheckKey, String captchaCachePrefix) {
        String lastCheckResult = cacheAccessor.get(captchaCachePrefix + ":" + KEY_CHECK_RESULT + ":" + lastCheckKey);
        CaptchaResult result = new CaptchaResult();
        if (StringUtils.isBlank(lastCheckResult)) {
            result.setSuccess(false);
            result.setMessage(CaptchaMessageSource.getMessage("captcha.validate.last-check-incorrect"));
            return result;
        }

        result.setSuccess(true);

        return result;
    }

    /**
     * cache the validate result.
     *
     * @param cachePrefix cache prefix.
     * @return {@link CaptchaResult#FIELD_LAST_CHECK_KEY}
     */
    public CaptchaResult cacheCheckResult(String cachePrefix) {
        return cacheCheckResult(cachePrefix, "1");
    }

    /**
     * cache the validate result.
     *
     * @param cachePrefix cache prefix
     * @param result cache value
     * @return {@link CaptchaResult#FIELD_LAST_CHECK_KEY}
     */
    public CaptchaResult cacheCheckResult(String cachePrefix, String result) {
        String lastCheckKey = CaptchaGenerator.generateCaptchaKey();
        cacheAccessor.put(cachePrefix + ":" + KEY_CHECK_RESULT + ":" + lastCheckKey, result, expire * 2L, TimeUnit.MINUTES);
        CaptchaResult captchaResult = new CaptchaResult();
        captchaResult.setLastCheckKey(lastCheckKey);
        captchaResult.setSuccess(true);
        return captchaResult;
    }

    /**
     * 检查间隔时间
     *
     * @param number 手机号/邮箱
     * @param captchaCachePrefix 验证码缓存前缀
     */
    private boolean checkInterval(String number, String captchaCachePrefix, CaptchaResult result) {
        String key = captchaCachePrefix + ":" + KEY_INTERVAL + ":" + number;
        String intervalTime = cacheAccessor.get(key);
        if (StringUtils.isNotBlank(intervalTime)) {
            // 防止间隔时间内缓存未失效
            long surplus = interval - (System.currentTimeMillis() - Long.valueOf(intervalTime)) / 1000;
            if (surplus > 0) {
                result.setMessage(CaptchaMessageSource.getMessage("captcha.send.interval", String.valueOf(surplus)));
                return true;
            }
            cacheAccessor.del(key);
        }
        return false;
    }

    /**
     * 检查发送次数是否已达上限
     *
     * @param number 手机号/邮箱
     * @param captchaCachePrefix 验证码缓存前缀
     */
    private boolean checkLimitTime(String number, String captchaCachePrefix) {
        if (limitTime > 0) {
            String key = captchaCachePrefix + ":" + KEY_LIMIT_TIME + ":" + number;
            String time = cacheAccessor.get(key);
            if (StringUtils.isNotBlank(time) && Integer.valueOf(time) >= limitTime) {
                return true;
            }
        }
        return false;
    }

    public static String encodeNumber(String number) {
        return StringUtils.substring(number, 0, 3) + "*****" + StringUtils.substring(number, -3);
    }

}
