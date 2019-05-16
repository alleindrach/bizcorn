package allein.bizcorn.common.util;


import allein.bizcorn.model.util.HttpClientCallResult;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static HttpConnectionManager multiThreadedHttpConnectionManager=new MultiThreadedHttpConnectionManager();
    private static HttpClient httpClient=new HttpClient(multiThreadedHttpConnectionManager);
    static{
        HttpConnectionManagerParams httpConnectionManagerParams = multiThreadedHttpConnectionManager.getParams();
        httpConnectionManagerParams.setDefaultMaxConnectionsPerHost(200);
        httpConnectionManagerParams.setMaxTotalConnections(500);
        httpConnectionManagerParams.setSoTimeout(300000);
        httpConnectionManagerParams.setConnectionTimeout(20000);
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(300000);
        httpClient.getParams().setContentCharset("GBK");
    }
    public static HttpClientCallResult httpClientPostCallNeedStatus(
            String allUrl, Map<String, String> params, int timeout,
            String charset) throws IOException {
        HttpClientCallResult httpClientCallResult = new HttpClientCallResult();
        //log 记录调用的链接
        logger.info("call url: " + allUrl);
        //创建post对象
        PostMethod post = new PostMethod(allUrl);
        //设置请求参数
        if (params != null) {
            for (String key : params.keySet()) {
                String value = params.get(key);
                if (key != null && value != null)
                    post.setParameter(key, params.get(key));
            }
        }
        //设置参数的文本格式
        post.getParams().setContentCharset(charset);
        //设置链接关闭
        post.setRequestHeader("Connection", "close");
        //设置超时时间
        httpClient.getHttpConnectionManager().getParams()
                .setConnectionTimeout(timeout);
        post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, timeout);
        //进行post请求
        int statusCode = httpClient.executeMethod(post);
        httpClientCallResult.setHttpStatus(statusCode);
        if (statusCode != HttpStatus.SC_OK) {
            //log记录返回的
            logger.warn("HttpClient excuteMethod failed. Status: "
                    + statusCode);
            return httpClientCallResult;
        }
        try {
            httpClientCallResult.setRetString(new String(
                    post.getResponseBody(), charset));
        } catch (IOException e) {
            //log记录返回的
            logger.warn("httpClientPostCallNeedStatus ex:" + allUrl);
            throw e;
        }finally {
            //释放链接的资源
            post.releaseConnection();
        }
        logger.info("call url result: "
                        + httpClientCallResult
                        .getRetString());
        //log记录返回的
        return httpClientCallResult;
    }



}
