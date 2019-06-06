/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade.gate;

import allein.bizcorn.model.input.SoundChannelIO;
import allein.bizcorn.model.input.SoundMessageIO;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSONObject;
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

    @RequestMapping(value="/sound/msg/{id}")
    @ResponseBody
    public Result msg(@PathVariable("id") String msgId);

    @RequestMapping(value="/sound/msg/copy/{id}")
    @ResponseBody
    /*
    @Description:
    @Param:[messageId 当messageId='all'时，置所有接收方为当前用户的状态status不为copied的对象为copied]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/6/6
    @Time:11:16 AM
    */
    public Result msgCopy(@PathVariable("id") String  messageId);

    @RequestMapping(value="/sound/msg/list")
    @ResponseBody
    /*
    @Description:
    @Param:[filter]
    {
        from:1, 跳过记录序号，如果是1，则表明跳过一条记录，从第二条记录开始读取
        size:10, 读取记录数
        filters:[ {key:'status',op:'is',val:'INIT'}...], 过滤器
        sorters:[ {key:'createTime',dir:'desc'}...] 排序字段
        setCopied:0/1  是否标记为已读
    }
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/6/6
    @Time:10:00 AM
    */
    public Result msgList(@RequestBody JSONObject filter);

    @RequestMapping("/admin/sound/channel/list")
    @ResponseBody
    public Result adminGetSoundChannels();

    @RequestMapping("/admin/sound/channel/add")
    @ResponseBody
    public Result adminAddSoundChannel(@RequestBody JSONObject channel);

    @RequestMapping("/admin/sound/channel/update")
    @ResponseBody
    public Result adminUpdateSoundChannel(@RequestBody JSONObject channel);

    @RequestMapping("/admin/sound/channel/delete")
    @ResponseBody
    public Result adminDeleteSoundChannel(@RequestBody JSONObject channel);

    @RequestMapping("/admin/sound/audit/list")
    @ResponseBody
    public Result adminGetSounds(@RequestBody JSONObject filter);

    @RequestMapping("/admin/sound/audit")
    @ResponseBody
    public Result adminAuditSound(@RequestBody JSONObject data);
}
