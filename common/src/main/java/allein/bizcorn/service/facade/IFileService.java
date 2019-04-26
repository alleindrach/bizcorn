package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public interface IFileService {

    @RequestMapping(value = "/files",method = RequestMethod.POST)
    public Result upload(HttpServletRequest request) ;

    @RequestMapping(value = "/file/{id}",method = RequestMethod.GET)
    public void downloadById(@PathVariable("id") String fileId)
            throws IOException;

    @RequestMapping(value = "/file/{id}",method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable("id") String fileId)
            throws IOException;
}
