package allein.bizcorn.servicefeign.web;


import allein.bizcorn.model.input.SoundChannelIO;
import allein.bizcorn.model.input.SoundMessageIO;
import allein.bizcorn.model.mongo.SoundChannel;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.facade.gate.IStoryServiceGate;
import allein.bizcorn.servicefeign.proxy.FileServiceProxy;
import allein.bizcorn.servicefeign.proxy.StoryServiceProxy;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RefreshScope

public class StoryServiceControl implements IStoryServiceGate {

    private static final Logger logger = LoggerFactory.getLogger(StoryServiceControl.class);

    @Autowired
    StoryServiceProxy storyService;

    @Override
    @ResponseBody
    public Result adminGetSoundChannels() {
        return storyService.adminGetSoundChannels();
    }

    @Override
    @ResponseBody
    public Result adminAddSoundChannel(@RequestBody  JSONObject channel) {
        return storyService.adminAddSoundChannel(channel);
    }

    @Override
    @ResponseBody
    public Result adminUpdateSoundChannel(@RequestBody  JSONObject channel) {
        return storyService.adminUpdateSoundChannel(channel);
    }

    @Override
    @ResponseBody
    public Result adminDeleteSoundChannel(@RequestBody  JSONObject channel) {
        return storyService.adminDeleteSoundChannel(channel);
    }

    @Override
    public Result adminGetStories(@RequestBody  JSONObject filter) {
        return storyService.adminGetStories(filter);
    }

    @Override
    public Result adminAuditStory(@RequestBody JSONObject data) {
        return storyService.adminAuditStory(data);
    }


    @ResponseBody
    public Result tell(@RequestPart() MultipartFile[] files,@RequestParam("info") String info){
        return    storyService.tell(files,info);
    }

    @ResponseBody
    public Result get(@PathVariable("id") String id)
    {
        return storyService.get(id);
    }

    @Override
    public Result action(@PathVariable("action") String action,@PathVariable("id") String id,@RequestBody(required = false) JSONObject param){

        return storyService.action(action,id,param);
    }

    @Override
    public Result copy(@PathVariable("id") String  messageId) {
        return storyService.copy(messageId);
    }

    @Override
    public Result list(@RequestBody JSONObject filter) {
        return storyService.list(filter);
    }


    public Result getSoundChannels() {
        return storyService.getSoundChannels();
    }

    @Override
    public Result setSoundChannels(@RequestPart MultipartFile[] files,@RequestParam("channels")String channelsJson) {
        return storyService.setSoundChannels(files,channelsJson);
    }
}
