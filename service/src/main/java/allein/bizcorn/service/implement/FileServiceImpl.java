package allein.bizcorn.service.implement;

import allein.bizcorn.common.cache.ICacheAccessor;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.model.entity.User;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.captcha.CaptchaResult;
import allein.bizcorn.service.db.mysql.dao.UserDAO;
import allein.bizcorn.service.facade.ICommonService;
import allein.bizcorn.service.facade.IFileService;
import allein.bizcorn.service.security.config.SecurityConstants;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RefreshScope
public class FileServiceImpl implements IFileService {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations operations;
    @Autowired
    private GridFSBucket gridFSBucket;

    @Value("${file.uploadRoot}")
    private String FileRoot;


    @Override
    @PreAuthorize("hasRole('USER')")
    public Result upload() {
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String username= SecurityUtil.getUserName();
        logger.debug("upload by {}",username);
//        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
//        resolver.resolveMultipart(request);
        //
        MultipartHttpServletRequest multipartRequest =  ((MultipartHttpServletRequest) request);


        Map<String, MultipartFile> fileMap =((StandardMultipartHttpServletRequest) multipartRequest).getFileMap();
//        MultipartFile file = null;
        BufferedOutputStream stream = null;
        Result r= Result.successWithMessage("Success!");
        HashMap<String,Result > result=new HashMap<>();

        for (MultipartFile file:fileMap.values()) {
            if (!file.isEmpty()) {
                try {
                    String uploadFilePath = file.getOriginalFilename();
                    logger.debug("uploadFlePath:{}" , uploadFilePath);
                    // 截取上传文件的文件名
                    String uploadFileName = uploadFilePath
                            .substring(uploadFilePath.lastIndexOf('\\') + 1,
                                    uploadFilePath.indexOf('.'));
                    logger.debug("multiReq.getFile {}" , uploadFileName);
                    // 截取上传文件的后缀
                    String uploadFileSuffix = uploadFilePath.substring(
                            uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
                    logger.debug("uploadFileSuffix:{}" , uploadFileSuffix);
                    stream = new BufferedOutputStream(new FileOutputStream(new File(
                            FileRoot + uploadFileName + "." + uploadFileSuffix)));


                    String md5Name= DigestUtils.md5DigestAsHex(file.getBytes());
                    GridFSFile gridFSFile=gridFsTemplate.findOne(Query.query(GridFsCriteria.whereFilename() .is(md5Name)));

                    if(gridFSFile!=null) {
                        result.put(file.getOriginalFilename(),Result.successWithData(((BsonObjectId) gridFSFile.getId()).getValue().toString()));
                    }
                    else{
                        InputStream ins = file.getInputStream();
                        String contentType = file.getContentType();
                        ObjectId gridFSFileId = gridFsTemplate.store(ins, md5Name, contentType);
                        result.put(file.getOriginalFilename(),Result.successWithData(gridFSFileId.toString()));
                    }
//
//                    byte[] bytes = file.getBytes();
//                    stream.write(bytes,0,bytes.length);
                } catch (Exception e) {
                    logger.debug("上传文件错误:",e);
                    result.put(file.getOriginalFilename(),Result.failWithException(e));
                } finally {
                    try {
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (IOException e) {
                        logger.debug("上传文件错误:",e);
                    }
                }
            } else {
                logger.debug("上传文件为空");
            }
        }

        return Result.successWithData(result);
    }
    @Override
    public void downloadById(@RequestParam String fileId) throws IOException {
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        Query query = Query.query(Criteria.where("_id").is(fileId));
// 查询单个文件
        GridFSFile gfsfile = gridFsTemplate.findOne(query);
        if (gfsfile == null) {
            return;
        }
        String fileName = gfsfile.getFilename().replace(",", "");
        //处理中文文件名乱码
        if (request.getHeader("User-Agent").toUpperCase().contains("MSIE") ||
                request.getHeader("User-Agent").toUpperCase().contains("TRIDENT")
                || request.getHeader("User-Agent").toUpperCase().contains("EDGE")) {
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        } else {
            //非IE浏览器的处理：
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }
        // 通知浏览器进行文件下载
        response.setContentType(gfsfile.getContentType());
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
//        GridFSDownloadStream in = gridFSBucket.openDownloadStream(gfsfile.getObjectId());

//        GridFsResource gridFsResource=new GridFsResource(gfsfile,in);

        gridFSBucket.downloadToStream(gfsfile.getObjectId(), response.getOutputStream());
//        byte[] b = new byte[1024];
//        int  i = 0;
//        while (-1!=(i=in.read(b))){
//            response.getOutputStream().write(b,0,i);
//        }


        logger.debug("success");
    }

    @Override
    public Result deleteById(String fileId) throws IOException {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(fileId)));
        return Result.successWithData(fileId);
    }


}
