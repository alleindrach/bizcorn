/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.implement;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.input.GiftAction;
import allein.bizcorn.model.mongo.*;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.db.mongo.dao.FrameDAO;
import allein.bizcorn.service.db.mongo.dao.GiftDAO;
import allein.bizcorn.service.db.mongo.dao.PackageBoxDAO;
import allein.bizcorn.service.facade.IFileService;
import allein.bizcorn.service.facade.IGiftService;
import allein.bizcorn.service.facade.IMessageBrokerService;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.facade.gate.IGiftServiceGate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import allein.bizcorn.model.input.GiftAction;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static allein.bizcorn.model.mongo.GiftActionType.TAKE_PICTURE;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-06-28 14:08
 **/
public class GiftSeviceMongoImpl implements IGiftService {
    @Resource
    private FrameDAO frameDAO;
    @Resource
    private PackageBoxDAO packageBoxDAO;
    @Resource
    private GiftDAO giftDAO;
    @Resource
    private IUserService userService;
    @Resource
    private IFileService fileService;
    @Resource
    private IMessageBrokerService messageBrokerService;
    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminGetFrames(@RequestBody JSONObject params) {
        return getFrames(params);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminAddOrUpdateFrame(@RequestBody JSONObject frameJson) {
        try {
            Frame frame = (Frame) JSON.toJSON(frameJson);
            frame=frameDAO.save(frame);
            return Result.successWithData(frame);
        }catch(Exception ex)
        {
            return Result.failWithException(ex);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminDeleteFrame(@PathVariable("id")String id) {
        Frame frame=frameDAO.get(id);
        if(frame!=null){
            frame.setDeleted(true);
            frameDAO.save(frame);
            return Result.successWithData(id);
        }
        return Result.failWithMessage("无此相框");
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminGetPackageBoxs(@RequestBody JSONObject params) {

        return getPackageBoxs(params);

    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminAddOrUpdatePackageBox(@RequestBody JSONObject packageboxJson)
    {
        try {
            PackageBox packageBox = (PackageBox) JSON.toJSON(packageboxJson);
            packageBox=packageBoxDAO.save(packageBox);
            return Result.successWithData(packageBox);
        }catch(Exception ex)
        {
            return Result.failWithException(ex);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminDeletePackageBox(@PathVariable("id") String id)
    {
        PackageBox packageBox=packageBoxDAO.get(id);
        if(packageBox!=null){
            packageBox.setDeleted(true);
            packageBoxDAO.save(packageBox);
            return Result.successWithData(id);
        }
        return Result.failWithMessage("无此包装盒");
    }

    @Override
    @ResponseBody
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result getFrames( @RequestBody JSONObject params) {

//        User user=userService.getUserFromSession();

        JSONObject frames=frameDAO.list(params);

//        SerializeConfig config=new SerializeConfig();
//        config.put(User.class,new User.SimpleSerializer());
//        config.put(Kid.class,new User.SimpleSerializer());
//        frames.put("list",JSONArray.parse( JSON.toJSONString(frames.get("list"),config)));
        return Result.successWithData(frames);

    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result getPackageBoxs( @RequestBody JSONObject params) {


//        User user=userService.getUserFromSession();

        JSONObject packageboxs=packageBoxDAO.list(params);

//        SerializeConfig config=new SerializeConfig();
//        config.put(User.class,new User.SimpleSerializer());
//        config.put(Kid.class,new User.SimpleSerializer());
//        packageboxs.put("list",JSONArray.parse( JSON.toJSONString(packageboxs.get("list"),config)));
        return Result.successWithData(packageboxs);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result make(@RequestPart MultipartFile[] files, @RequestParam("action") String actionInString) {

        User user=userService.getUserFromSession();
        if(user.getCurPartner()==null)
            return Result.failWithMessage("爱分享需要两个人玩哦");
        GiftAction action=JSON.parseObject(actionInString, GiftAction.class);
        Result uploadResult=null;
        if(files!=null) {
            uploadResult = fileService.upload(files);
            if (!uploadResult.isSuccess())
                return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));
        }

        switch (action.getMove()){
            case TAKE_PICTURE:{
                Gift gift=new Gift();
                if(uploadResult==null )
                    return Result.failWithMessage("没有选择礼物的图片");
                String fileId=fileService.getFileID(uploadResult,action.getPayload());
                if(fileId==null)
                    return Result.failWithMessage("礼物的图片名字错了");
                gift.setPicture(fileId);
                gift.setCreateDate(new Date());
                gift.setOperation(action.getMove().getValue());
                gift.setOperator(user);
                Kid kid=user.getKid();
                if(kid==null)
                    return Result.failWithMessage("礼物没有小主人");
                gift.setOwner(kid);
                giftDAO.save(gift);
//                向另一方发消息
                Message msg=Message.GiftMessage(gift,user.getUsername(),user.getCurPartner().getUsername());
                messageBrokerService.send(msg);

                return Result.successWithData(gift);
            }

            case CHOOSE_FRAME: {
                if (action.getId() == null)
                    return Result.failWithMessage("礼物不见了。。。");
                Gift gift = giftDAO.get(action.getId());
                if(action.getPayload()==null )
                    return Result.failWithMessage("没有选择礼物的相框");
                Frame frame=(frameDAO.get(action.getPayload()));
                if(frame==null)
                    return Result.failWithMessage("选择的礼相框不对哦");
                gift.setFrame(frame);
                gift.setOperation(action.getMove().getValue());
                gift.setOperator(user);
                Message msg=Message.GiftMessage(gift,user.getUsername(),user.getCurPartner().getUsername());
                messageBrokerService.send(msg);
                return Result.successWithData(gift);
            }

            case RECORD_VOICE:{
                if (action.getId() == null)
                    return Result.failWithMessage("礼物不见了。。。");
                Gift gift = giftDAO.get(action.getId());
                if(uploadResult==null )
                    return Result.failWithMessage("没有留言哦");
                String fileId=fileService.getFileID(uploadResult,action.getPayload());
                if(fileId==null)
                    return Result.failWithMessage("没有留言哦");
                gift.setSndMsg(fileId);

                gift.setOperation(action.getMove().getValue());
                gift.setOperator(user);

                Message msg=Message.GiftMessage(gift,user.getUsername(),user.getCurPartner().getUsername());
                messageBrokerService.send(msg);
                return Result.successWithData(gift);
            }


            case CHOOSE_BOX:{
                if (action.getId() == null)
                    return Result.failWithMessage("礼物不见了。。。");
                Gift gift = giftDAO.get(action.getId());
                if(action.getPayload()==null )
                    return Result.failWithMessage("没有选择礼物的包装");
                PackageBox box=(packageBoxDAO.get(action.getPayload()));
                if(box==null)
                    return Result.failWithMessage("选择的礼包装不对哦");
                gift.setPackageBox(box);
                gift.setOperation(action.getMove().getValue());
                gift.setOperator(user);
                Message msg=Message.GiftMessage(gift,user.getUsername(),user.getCurPartner().getUsername());
                messageBrokerService.send(msg);
                return Result.successWithData(gift);
            }

            case RELEASE:
                break;
        }
        return  Result.failWithMessage("无效操作");


    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result get(@PathVariable("id") String id) {

        User user=userService.getUserFromSession();
        Gift gift = giftDAO.get(id);

        return Result.successWithData(gift);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result list(@RequestBody JSONObject params) {

        JSONObject gifts=frameDAO.list(params);

//        SerializeConfig config=new SerializeConfig();
//        config.put(User.class,new User.SimpleSerializer());
//        config.put(Kid.class,new User.SimpleSerializer());
//        frames.put("list",JSONArray.parse( JSON.toJSONString(frames.get("list"),config)));
        return Result.successWithData(gifts);

    }
}
