package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IStoryService {

    @RequestMapping("/story/sync")
    @ResponseBody
    public Result syncStory(HttpServletRequest request,@RequestParam("id") String id,@RequestParam("work") String work);


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
