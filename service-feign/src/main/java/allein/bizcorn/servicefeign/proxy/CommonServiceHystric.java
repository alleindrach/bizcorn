package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.output.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CommonServiceHystric implements  CommonServiceProxy{
    public ResponseEntity<byte[]> captcha2() {
        return  null;
    }
    public Result mobileCaptcha(String mobile) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public ResponseEntity<byte[]> captcha() {
        return null;
    }

}
