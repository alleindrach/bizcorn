package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public interface IFileService {

    @RequestMapping(value = "/file/upload",method = RequestMethod.POST)
    public Result upload(HttpServletRequest request) ;

    @RequestMapping(value = "/file/download",method = RequestMethod.GET)
    public void download(@RequestParam String filename,@RequestParam HttpServletResponse response) ;


}
