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

    @RequestMapping("/sound/channels/down")
    @ResponseBody
    public Result getSoundChannels();

    @RequestMapping(value="/sound/channels/up",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Result setSoundChannels(@RequestPart MultipartFile[] files,@RequestParam("channels")String channelsJson);

    @PostMapping(value="/story",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Result tell(@RequestPart MultipartFile[] files, @RequestParam("info") String info);


    @GetMapping("/story/{id}")
    @ResponseBody
    public Result get(@PathVariable("id") String id);

/*
@Description:消息操作
@Param:
action: delete 删除，只能是talker或者talker的父母方
        publish 发布，只能是talker或者talker的父母方
        send 发给绑定方，可以是公共库转发
        edit 编辑，只能是talker或者talker的父母方
param:
        delete: null
        publish：{
            name: 命名
            desc: 描述
            tags: [标签数组]
        }
        send:null
        edit: 同publish

@Return:
@Author:Alleindrach@gmail.com
@Date:2019/6/24
@Time:9:03 AM
*/
    @RequestMapping(value="/story/{action}/{id}")
    @ResponseBody
    public Result action(@PathVariable("action") String action,@PathVariable("id") String msgId,@RequestBody(required = false) JSONObject param);


    @RequestMapping(value="/story/copy/{id}")
    @ResponseBody
    /*
    @Description:
    @Param:[messageId 当messageId='all'时，置所有接收方为当前用户的状态status不为copied的对象为copied]
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/6/6
    @Time:11:16 AM
    */
    public Result copy(@PathVariable("id") String  messageId);

    @RequestMapping(value="/story/list")
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
        repo: 0=私有对话,1=公有库,2=其他，自定义
    }
    @Return:allein.bizcorn.model.output.Result
    @Author:Alleindrach@gmail.com
    @Date:2019/6/6
    @Time:10:00 AM
    */
    public Result list(@RequestBody JSONObject filter);

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

    @RequestMapping("/admin/story/audit/list")
    @ResponseBody
    public Result adminGetStories(@RequestBody JSONObject filter);

    @RequestMapping("/admin/story/audit")
    @ResponseBody
    public Result adminAuditStory(@RequestBody JSONObject data);

}
