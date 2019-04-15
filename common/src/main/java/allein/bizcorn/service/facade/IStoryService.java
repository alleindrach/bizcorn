package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;

public interface IStoryService {
    @PutMapping("/story/{detail}")
    @ResponseBody
    public Result newStory(@RequestParam HttpServletRequest request,@RequestParam HttpServletResponse response ,
                           @PathParam("detail") String  detail);

    @PutMapping("/story/{id}/{detail}")
    @ResponseBody
    public Result updateStory(@RequestParam HttpServletRequest request,@RequestParam HttpServletResponse response ,
                              @PathParam("id") String id,@PathParam("detail") String detail);


    @PostMapping("/story/{id}")
    @ResponseBody
    public Result getOneStory(@RequestParam HttpServletRequest request,@RequestParam HttpServletResponse response,
                              @PathParam("id") String id);
    @PostMapping("/story/{username}/all")
    @ResponseBody
    public Result getAllStory(@RequestParam HttpServletRequest request,@RequestParam HttpServletResponse response ,@PathParam("username") String uid);

    @DeleteMapping("/story/{id}")
    @ResponseBody
    public Result deleteOneStory(@RequestParam HttpServletRequest request,@RequestParam HttpServletResponse response ,
                              @PathParam("id") String id);

}
