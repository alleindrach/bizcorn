package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IStoryService {

    @PostMapping("/story/sync")
    @ResponseBody
    public Result syncStory(@RequestParam("id") String id,@RequestParam("work") String work);


    @PostMapping("/story/{id}")
    @ResponseBody
    public Result getOneStory(@PathVariable("id") String id);

    @PostMapping("/story/of/{username}")
    @ResponseBody
    public Result getAllStory(@PathVariable("username") String username);

    @DeleteMapping("/story/{id}")
    @ResponseBody
    public Result deleteOneStory( @PathVariable("id") String id);

}
