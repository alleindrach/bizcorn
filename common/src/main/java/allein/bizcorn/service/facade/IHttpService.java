package allein.bizcorn.service.facade;

import allein.bizcorn.model.util.HttpClientCallResult;

import java.io.IOException;
import java.util.Map;

public interface IHttpService {
    HttpClientCallResult httpClientPostCallNeedStatus(
            String allUrl, Map<String, String> params, int timeout,
            String charset) throws IOException;
}
