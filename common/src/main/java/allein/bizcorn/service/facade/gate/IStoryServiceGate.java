/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade.gate;

import allein.bizcorn.model.input.SoundChannelIO;
import allein.bizcorn.model.input.SoundMessageIO;
import allein.bizcorn.model.output.Result;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IStoryServiceGate {

    @RequestMapping(value="/story/sync",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Result syncStory(@RequestPart MultipartFile[] files, @RequestParam("id") String id, @RequestParam("work") String work);


    @RequestMapping("/story/{id}")
    @ResponseBody
    public Result getOneStory(@PathVariable("id") String id);

    @RequestMapping("/story/of/{username}")
    @ResponseBody
    public Result getAllStory(@PathVariable("username") String username);

    @DeleteMapping("/story/{id}")
    @ResponseBody
    public Result deleteStory(@PathVariable("id") String id);

    @RequestMapping("/sound/channels/down")
    @ResponseBody
    public Result getSoundChannelBGs();

    @RequestMapping(value="/sound/channels/up",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Result setSoundChannelBG(@RequestPart MultipartFile[] files,@RequestParam("channels")String channelsJson);

    @RequestMapping(value="/sound/msg/up",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Result msgUp(@RequestPart MultipartFile[] files,@RequestParam("message") String messageJson);

    @RequestMapping(value="/sound/msg/copy/{id}")
    @ResponseBody
    public Result msgCopy(@PathVariable("id") String  messageId);

    @RequestMapping(value="/sound/msg/list")
    @ResponseBody
    public Result msgList(@RequestParam("criteria") String criteria, @RequestParam("page") Integer  pageIndex,@RequestParam("size") Integer  pageSize);

}
