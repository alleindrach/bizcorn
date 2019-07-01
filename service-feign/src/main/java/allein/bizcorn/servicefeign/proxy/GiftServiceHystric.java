/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.servicefeign.proxy;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class GiftServiceHystric implements  GiftServiceProxy{


    @Override
    public Result adminGetFrames(JSONObject params) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }
    @Override
    public Result adminAddOrUpdateFrame(JSONObject framejson) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result adminDeleteFrame(String id) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result adminGetPackageBoxs(JSONObject params) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result adminAddOrUpdatePackageBox(JSONObject packagebox) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result adminDeletePackageBox(String id) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result getFrames(JSONObject params) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result getPackageBoxs(JSONObject params) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result make(MultipartFile[] files, String action) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result get(String id) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }

    @Override
    public Result list(JSONObject filter) {
        return Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }
}
