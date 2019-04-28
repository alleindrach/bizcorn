package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IStoryService {

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
    public Result deleteStory( @PathVariable("id") String id);

}
