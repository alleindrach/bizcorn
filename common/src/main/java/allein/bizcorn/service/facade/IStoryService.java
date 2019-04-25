package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;

public interface IStoryService {

    @PostMapping("/story/sync")
    @ResponseBody
    public Result syncStory(@RequestParam("id") String id,@RequestParam("work") String work);


    @PostMapping("/story/{id}")
    @ResponseBody
    public Result getOneStory(@PathParam("id") String id);
    @PostMapping("/story/of/{username}")
    @ResponseBody
    public Result getAllStory(@PathParam("username") String username);

    @DeleteMapping("/story/{id}")
    @ResponseBody
    public Result deleteOneStory( @PathParam("id") String id);

}
