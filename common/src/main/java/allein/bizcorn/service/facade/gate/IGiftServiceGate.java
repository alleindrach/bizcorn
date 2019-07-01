/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade.gate;

import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-06-28 11:53
 **/
public interface IGiftServiceGate {


    @RequestMapping("/admin/gift/frame/list")
    @ResponseBody
    public Result adminGetFrames(@RequestBody JSONObject params);

    @RequestMapping("/admin/gift/frame/set")
    @ResponseBody
    public Result adminAddOrUpdateFrame(@RequestBody JSONObject framejson);

    @RequestMapping("/admin/gift/frame/delete/{id}")
    @ResponseBody
    public Result adminDeleteFrame( @PathVariable("id")String id);

    @RequestMapping("/admin/gift/packagebox/list")
    @ResponseBody
    public Result adminGetPackageBoxs( @RequestBody JSONObject params);

    @RequestMapping("/admin/gift/packagebox/set")
    @ResponseBody
    public Result adminAddOrUpdatePackageBox(@RequestBody JSONObject packagebox);

    @RequestMapping("/admin/gift/package/delete/{id}")
    @ResponseBody
    public Result adminDeletePackageBox(@PathVariable("id") String id);


    @RequestMapping("/gift/frames")
    @ResponseBody
    public Result getFrames( @RequestBody JSONObject params);

    @RequestMapping("/gift/packageboxs")
    @ResponseBody
    public Result getPackageBoxs(@RequestBody JSONObject params);

    /*
    @Description:制作一个礼物
    @Param:
    files: 当前文件列表
    action: 当前操作
      TAKE_PICTURE:宝宝拍礼物
      {
        action:'TAKE_PICTURE',
        image:文件名
      }
      CHOOSE_FRAME:家长选择相框
      {
        id:礼物id
        action:'CHOOSE_FRAME',
        frame:  frameid
      }
      RECORD_VOICE:宝宝给礼物录音
      {
        id:礼物id
        action:'RECORD_VOICE',
        sound:  声音文件名
      }
      CHOOSE_PACKAGEBOX:宝宝给选择礼物盒
      {
        id:礼物id
        action:'CHOOSE_PACKAGEBOX',
        packagebox:  packageboxID
      }
      RELEASE:家长上传礼物
      {
        id:礼物id
        action:'RELEASE'
      }
    @Return:
    @Author:Alleindrach@gmail.com
    @Date:2019/6/28
    @Time:4:44 PM
    */
    @RequestMapping(value="/gift" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Result make(@RequestPart MultipartFile[] files, @RequestParam("action")String action);

    @GetMapping(value="/gift/{id}")
    @ResponseBody
    public Result get(@PathVariable("id") String id);

    @RequestMapping(value="/gift/list")
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
}
