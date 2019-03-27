package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class CommonServiceHystric implements  CommonServiceProxy{

    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        return ;
    }
    public ResponseEntity<byte[]> captcha2() {
        return  null;
    }
    public Result mobileCaptcha(String mobile) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

}
