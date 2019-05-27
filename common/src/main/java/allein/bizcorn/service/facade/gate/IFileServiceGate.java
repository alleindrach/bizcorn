/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade.gate;

import allein.bizcorn.common.web.BizResponseEntity;
import allein.bizcorn.model.output.Result;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileServiceGate {

    @RequestMapping(value = "/files",method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result upload(@RequestPart MultipartFile[] files) ;

    @RequestMapping(value = "/file",method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result upload(@RequestPart MultipartFile file) ;

    @RequestMapping(value = "/file/{id}",method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadById(@PathVariable("id") String fileId)
            throws IOException;
    @RequestMapping(value = "/file/small/{id}",method = RequestMethod.GET)
    public ResponseEntity<byte[]> thumbById(@PathVariable("id") String fileId)
            throws IOException;

    @RequestMapping(value = "/file/byname/{name}",method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadByName(@PathVariable("name") String fileName)
            throws IOException;

    @RequestMapping(value = "/file/small/byname/{name}",method = RequestMethod.GET)
    public ResponseEntity<byte[]> thumbByName(@PathVariable("name") String fileName)
            throws IOException;


    @RequestMapping(value = "/file/{id}",method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable("id") String fileId)
            throws IOException;
}
