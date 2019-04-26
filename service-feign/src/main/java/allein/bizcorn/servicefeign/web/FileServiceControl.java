package allein.bizcorn.servicefeign.web;


import allein.bizcorn.model.output.Result;
import allein.bizcorn.servicefeign.proxy.CommonServiceProxy;
import allein.bizcorn.servicefeign.proxy.FileServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RefreshScope

public class FileServiceControl {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceControl.class);


    @Autowired
    FileServiceProxy fileService;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    @RequestMapping(value = "/files",method = RequestMethod.POST)
    public Result upload(HttpServletRequest request)
    {
        Result result=fileService.upload(request);
        return result;
    }
    @RequestMapping(value = "/file/{id}",method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadById(@PathVariable("id") String fileId)
            throws IOException
    {
        ResponseEntity<byte[]> result=fileService.downloadById(fileId);
        return result;
    }

    @RequestMapping(value = "/file/{id}",method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable("id") String fileId)
            throws IOException
    {
        return fileService.deleteById(fileId);
    }
}
