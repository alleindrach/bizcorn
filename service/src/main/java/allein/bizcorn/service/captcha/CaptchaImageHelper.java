package allein.bizcorn.service.captcha;

import allein.bizcorn.common.cache.CacheAccessor;
import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.service.captcha.config.CaptchaProperties;
import allein.bizcorn.service.captcha.message.CaptchaMessageSource;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisAccessor;
import org.springframework.http.*;
import org.springframework.web.util.WebUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CaptchaImageHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaImageHelper.class);

    @Autowired
    private DefaultKaptcha captchaProducer;
    @Autowired
    private CaptchaProperties captchaProperties;
    @Autowired
    private ICacheAccessor cacheAccessor;


    /**
     * 生成验证码并输出图片到指定输出流，验证码的key为UUID，设置到Cookie中，key和验证码将缓存到Redis中
     *
     * @param response HttpServletResponse
     * @param captchaCachePrefix 缓存验证码的前缀
     */
    public void generateAndWriteCaptchaImage(HttpServletRequest request, HttpServletResponse response, String captchaCachePrefix) {
        HttpSession httpSession =request.getSession();

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        ServletOutputStream out = null;
        try {
            String captcha = captchaProducer.createText();

            String captchaKey = CaptchaGenerator.generateCaptchaKey();
            Cookie cookie = new Cookie(CaptchaResult.FIELD_CAPTCHA_KEY, captchaKey);
            cookie.setPath(StringUtils.defaultIfEmpty("/", "/"));
            cookie.setMaxAge(-1);
            response.addCookie(cookie);
//            httpSession.setAttribute(CaptchaResult.FIELD_CAPTCHA_KEY,captchaKey);


            // cache
            cacheAccessor.put(captchaCachePrefix + ":captcha:" + captchaKey, captcha,captchaProperties.getImage().getExpire()*60L);

            // output
            BufferedImage bi = captchaProducer.createImage(captcha);
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
            out.flush();
        } catch (Exception e) {
            LOGGER.info("create captcha fail: {}", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    LOGGER.info("captcha output close fail: {}", e);
                }
            }
        }
    }
    private String getCookieHeader(Cookie cookie) {
        StringBuilder buf = new StringBuilder();
        buf.append(cookie.getName()).append('=').append(cookie.getValue() == null ? "" : cookie.getValue());
        if (org.springframework.util.StringUtils.hasText(cookie.getPath())) {
            buf.append("; Path=").append(cookie.getPath());
        }
        if (org.springframework.util.StringUtils.hasText(cookie.getDomain())) {
            buf.append("; Domain=").append(cookie.getDomain());
        }
        int maxAge = cookie.getMaxAge();
        if (maxAge >= 0) {
            buf.append("; Max-Age=").append(maxAge);
            buf.append("; Expires=");
            HttpHeaders headers = new HttpHeaders();
            headers.setExpires(maxAge > 0 ? System.currentTimeMillis() + 1000L * maxAge : 0);
            buf.append(headers.getFirst(HttpHeaders.EXPIRES));
        }

        if (cookie.getSecure()) {
            buf.append("; Secure");
        }
        if (cookie.isHttpOnly()) {
            buf.append("; HttpOnly");
        }
        return buf.toString();
    }
    public ResponseEntity<byte[]> captchaImage(String captchaCachePrefix) {

        ResponseEntity<byte[]> entity = null;
        HttpHeaders headers = new HttpHeaders();
        try {
            String captcha = captchaProducer.createText();
            String captchaKey = CaptchaGenerator.generateCaptchaKey();
            Cookie cookie = new Cookie(CaptchaResult.FIELD_CAPTCHA_KEY, captchaKey);
            cookie.setPath(StringUtils.defaultIfEmpty("/", "/"));
            cookie.setMaxAge(-1);

            cacheAccessor.put(captchaCachePrefix + ":captcha:" + captchaKey, captcha,captchaProperties.getImage().getExpire()*60L);
            headers.add(HttpHeaders.EXPIRES,"0");
            CacheControl cacheControl=CacheControl.noStore();
            headers.setCacheControl(CacheControl.noStore());
            headers.setCacheControl(CacheControl.noCache());
            headers.setPragma("no-cache");
            headers.setContentType(MediaType.IMAGE_JPEG);
//            headers.setCacheControl("Cache-Control", "no-store, no-cache, must-revalidate,post-check=0, pre-check=0");
            headers.set(HttpHeaders.SET_COOKIE,getCookieHeader(cookie));

            // output
            BufferedImage bi = captchaProducer.createImage(captcha);

            HttpStatus status = HttpStatus.OK;
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ImageIO.write(bi,"jpg",bos);
//            bos.flush();
            byte[] imageData= bos.toByteArray();
            entity = new ResponseEntity<byte[]>(imageData, headers, status);

        } catch (Exception e) {
            LOGGER.info("create captcha fail: {}", e);
        } finally {

        }
        return entity;
    }
    /**
     * 校验验证码
     *
     * @param request HttpServletRequest
     * @param captcha captcha
     * @param captchaCachePrefix captcha cache prefix
     */
    public CaptchaResult checkCaptcha(HttpServletRequest request, String captcha, String captchaCachePrefix) {
        Cookie captchaKeyCookie = WebUtils.getCookie(request, CaptchaResult.FIELD_CAPTCHA_KEY);
        if (captchaKeyCookie == null) {
            throw new CommonException(ExceptionEnum.CAPTCH_COOKIE_KEY_INVALID);
        }
//        HttpSession httpSession =request.getSession();
//        String captchaKeyStored=httpSession.getAttribute(CaptchaResult.FIELD_CAPTCHA_KEY)!=null?httpSession.getAttribute(CaptchaResult.FIELD_CAPTCHA_KEY).toString():"";

        CaptchaResult captchaResult = new CaptchaResult();
        if (StringUtils.isBlank(captcha)) {
            captchaResult.setSuccess(false);
            captchaResult.setMessage(CaptchaMessageSource.getMessage("captcha.validate.captcha.notnull"));
            return captchaResult;
        }
        String captchaKey = captchaKeyCookie.getValue();
        String cacheCaptcha = cacheAccessor.get(captchaCachePrefix + ":captcha:" + captchaKey);
        cacheAccessor.del(captchaCachePrefix + ":captcha:" + captchaKey);
        if (!StringUtils.equalsIgnoreCase(cacheCaptcha, captcha)) {
            captchaResult.setSuccess(false);
            captchaResult.setMessage(CaptchaMessageSource.getMessage("captcha.validate.captcha.incorrect"));
            return captchaResult;
        }
        captchaResult.setSuccess(true);
        return captchaResult;
    }

    /**
     * 从request cookie 中获取验证码
     *
     * @param request HttpServletRequest
     * @param captchaCachePrefix captcha cache prefix
     */
    public String getCaptcha(HttpServletRequest request, String captchaCachePrefix) {
        Cookie captchaKeyCookie = WebUtils.getCookie(request, CaptchaResult.FIELD_CAPTCHA_KEY);
        if (captchaKeyCookie == null) {
            return null;
        }
        String captchaKey = captchaKeyCookie.getValue();
        String captcha = cacheAccessor.get(captchaCachePrefix + ":captcha:" + captchaKey);
        cacheAccessor.del(captchaCachePrefix + ":captcha:" + captchaKey);
        return captcha;
    }

    /**
     * 获取验证码
     *
     * @param captchaCachePrefix 缓存前缀
     * @param captchaKey 验证码KEY
     * @return 验证码
     */
    public String getCaptcha(String captchaCachePrefix, String captchaKey) {
        String captcha = cacheAccessor.get(captchaCachePrefix + ":captcha:" + captchaKey);
        cacheAccessor.del(captchaCachePrefix + ":captcha:" + captchaKey);
        return captcha;
    }

}