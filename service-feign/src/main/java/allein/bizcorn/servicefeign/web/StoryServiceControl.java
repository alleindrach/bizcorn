package allein.bizcorn.servicefeign.web;


import allein.bizcorn.model.output.Result;
import allein.bizcorn.servicefeign.proxy.FileServiceProxy;
import allein.bizcorn.servicefeign.proxy.StoryServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RefreshScope

public class StoryServiceControl {

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
}
