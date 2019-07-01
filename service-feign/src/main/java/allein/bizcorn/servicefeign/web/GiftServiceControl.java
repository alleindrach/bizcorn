/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.servicefeign.web;


import allein.bizcorn.common.config.SecurityConstants;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.Profile;
import allein.bizcorn.model.output.Result;

import allein.bizcorn.service.facade.gate.IGiftServiceGate;
import allein.bizcorn.servicefeign.proxy.GiftServiceProxy;
import allein.bizcorn.servicefeign.proxy.UserServiceProxy;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RefreshScope
public class GiftServiceControl implements IGiftServiceGate {

    @Autowired
    GiftServiceProxy giftServiceGate;
    @Override
    public Result adminGetFrames(@RequestBody JSONObject params) {
        return giftServiceGate.adminGetFrames(params);
    }

    @Override
    public Result adminAddOrUpdateFrame(@RequestBody JSONObject framejson) {
        return giftServiceGate.adminAddOrUpdateFrame(framejson);
    }

    @Override
    public Result adminDeleteFrame(@PathVariable("id") String id) {
        return giftServiceGate.adminDeleteFrame(id);
    }

    @Override
    public Result adminGetPackageBoxs(@RequestBody JSONObject params) {
        return giftServiceGate.adminGetPackageBoxs(params);
    }

    @Override
    public Result adminAddOrUpdatePackageBox(@RequestBody JSONObject packagebox) {
        return giftServiceGate.adminAddOrUpdatePackageBox(packagebox);
    }

    @Override
    public Result adminDeletePackageBox(@PathVariable("id") String id) {
        return giftServiceGate.adminDeletePackageBox(id);
    }

    @Override
    public Result getFrames(@RequestBody JSONObject params) {
        return giftServiceGate.getFrames(params);
    }

    @Override
    public Result getPackageBoxs(@RequestBody JSONObject params) {
        return giftServiceGate.getPackageBoxs(params);
    }

    @Override
    public Result make(@RequestPart  MultipartFile[] files, @RequestParam("action") String action) {
        return giftServiceGate.make(files,action);
    }

    @Override
    public Result get(@PathVariable("id")  String id) {
        return giftServiceGate.get(id);
    }

    @Override
    public Result list(@RequestBody  JSONObject filter) {
        return giftServiceGate.list(filter);
    }
}
