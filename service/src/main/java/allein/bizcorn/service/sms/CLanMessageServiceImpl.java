package allein.bizcorn.service.sms;


import allein.bizcorn.model.util.HttpClientCallResult;
import allein.bizcorn.service.facade.IHttpService;
import allein.bizcorn.service.facade.IMessageService;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Service
public class CLanMessageServiceImpl implements IMessageService {
    @Value("${smsmessage.clan.url}")
    private String CLanUrl;
    @Value("${smsmessage.clan.account}")
    private String CLanAccount;
    @Value("${smsmessage.clan.pwd}")
    private String CLanPwd;
    @Autowired
    IHttpService httpClientServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(CLanMessageServiceImpl.class);
    @Override
    public String sendMsg(String phoneNumber, String content) {
        Map<String,String> data=new HashMap<String,String>();
        data.put("account",CLanAccount);
        data.put("pswd", CLanPwd);
        data.put("mobile", phoneNumber);
        data.put("needstatus", "false");
        data.put("msg", content);
        String url=CLanUrl;
        logger.info("clan data"+data.toString()+" "+CLanUrl);
        try {
            HttpClientCallResult result= httpClientServiceImpl.httpClientPostCallNeedStatus(url,data,300000,"utf-8");
            if(result!=null&&result.getHttpStatus()== HttpStatus.SC_OK){
                return result.getRetString();
            }
        } catch (IOException e) {

        }
        return null;
    }
}
