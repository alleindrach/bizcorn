package allein.bizcorn.service.implement;

import allein.bizcorn.service.facade.IConfigSerivce;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl implements IConfigSerivce {

    @Value("${foo}")
	String foo;

    public boolean isEnableCaptcha(Long errorTime)
    {
        return true;
    }

}