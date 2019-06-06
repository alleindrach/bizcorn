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
    @RequestMapping("/story/sync")
    @ResponseBody
    public Result syncStory(@RequestPart MultipartFile [] files, @RequestParam("id") String id, @RequestParam("work") String work)
    {
        return    storyService.syncStory(files,id,work);
    }


    @RequestMapping("/story/{id}")
    @ResponseBody
    public Result getOneStory(@PathVariable("id") String id)
    {
        return storyService.getOneStory(id);
    }

    @RequestMapping("/story/of/{username}")
    @ResponseBody
    public Result getAllStory(@PathVariable("username") String username)
    {
        return storyService.getAllStory(username);
    }

    @DeleteMapping("/story/{id}")
    @ResponseBody
    public Result deleteStory( @PathVariable("id") String id)
    {
        return storyService.deleteStory(id);
    }

    public Result getSoundChannelBGs() {
        return storyService.getSoundChannelBGs();
    }

    @Override
    public Result msgUp(@RequestPart MultipartFile[] files,@RequestParam("message") String messageJson) {
        return storyService.msgUp(files,messageJson);
    }

    @Override
    public Result msg(@PathVariable("id") String msgId) {
        return storyService.msg(msgId);
    }

    @Override
    public Result msgCopy(@PathVariable("id") String  messageId) {
        return storyService.msgCopy(messageId);
    }

    @Override
    public Result msgList(@RequestBody  JSONObject filter) {
        return  storyService.msgList(filter);
    }

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
    public Result adminGetSounds(@RequestBody  JSONObject filter) {
        return storyService.adminGetSounds(filter);
    }

    @Override
    public Result adminAuditSound(@RequestBody JSONObject data) {
        return storyService.adminAuditSound(data);
    }

    @Override
    public Result setSoundChannelBG(@RequestPart MultipartFile[] files,@RequestParam("channels")String channelsJson) {
        return storyService.setSoundChannelBG(files,channelsJson);
    }
}
