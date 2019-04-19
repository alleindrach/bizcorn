package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.output.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CommonServiceHystric implements  CommonServiceProxy{
    @Override
    public ResponseEntity<byte[]> captcha() {
        return null;
    }

    @Override
    public Result mobileCaptcha(HttpServletRequest request, HttpServletResponse response, String mobile, String captcha) {
        return null;
    }

}
