/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade.gate;

import allein.bizcorn.model.output.Result;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @RequestMapping("/story/sound/channels")
    @ResponseBody
    public Result getSoundChannelBGs();

    @RequestMapping("/story/sound/channel/{index}")
    @ResponseBody
    public Result setSoundChannelBG(@PathVariable("index") Integer index,@RequestPart MultipartFile file);

}
