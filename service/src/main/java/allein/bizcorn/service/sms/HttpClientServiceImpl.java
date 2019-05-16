package allein.bizcorn.service.sms;

import allein.bizcorn.common.util.HttpClientUtil;
import allein.bizcorn.model.util.HttpClientCallResult;
import allein.bizcorn.service.facade.IHttpService;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;
@Service
public class HttpClientServiceImpl implements IHttpService {
    @Override
    public HttpClientCallResult httpClientPostCallNeedStatus(String allUrl, Map<String, String> params, int timeout, String charset) throws IOException {
        return HttpClientUtil.httpClientPostCallNeedStatus(allUrl,params,timeout,charset);
    }
}
