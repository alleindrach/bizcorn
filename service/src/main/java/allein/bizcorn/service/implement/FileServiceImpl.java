package allein.bizcorn.service.implement;

import allein.bizcorn.common.annotation.CacheMethod30S;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.misc.MetaData;
import allein.bizcorn.common.mq.Topic;
import allein.bizcorn.common.util.FileUtil;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.common.util.UrlUtil;
import allein.bizcorn.common.web.BizResponseEntity;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.facade.IFileService;
import allein.bizcorn.service.facade.IMessageQueueService;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FilenameUtils;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;

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
    @Autowired
    private IMessageQueueService messageQueueService;
    @Value("${bizcorn.filebase}")
    private String filebase;
//    @Value("${file.uploadRoot}")
//    private String FileRoot;


    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result upload(@RequestPart MultipartFile[] files) {
//        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String username= SecurityUtil.getUserName();
        logger.debug("upload by {}",username);


        HashMap<String,Result > result=new HashMap<>();

        for (MultipartFile file:files) {

            result.put(file.getOriginalFilename(),processOneFile(file));

        }
        return Result.successWithData(result);
    }

    private Result  processOneFile(MultipartFile file)
    {
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
//                    stream = new BufferedOutputStream(new FileOutputStream(new File(
//                            FileRoot + uploadFileName + "." + uploadFileSuffix)));


                String md5Name= DigestUtils.md5DigestAsHex(file.getBytes());
                GridFSFile gridFSFile=gridFsTemplate.findOne(Query.query(GridFsCriteria.whereFilename() .is(md5Name)));

                if(gridFSFile!=null) {
                    return Result.successWithData(((BsonObjectId) gridFSFile.getId()).getValue().toString());
                }
                else{
                    InputStream ins = file.getInputStream();
                    String contentType = file.getContentType();
                    Document metaData=new Document();
                    metaData.append(MetaData.CONTENT_TYPE.getValue(),contentType);
                    metaData.append(MetaData.SUFFIX.getValue(),uploadFileSuffix);
                    metaData.append(MetaData.ORIGIN_FILENAME.getValue(),uploadFilePath);
                    ObjectId gridFSFileId = gridFsTemplate.store(ins, md5Name, metaData);

                    MimeType mimeType=MimeTypeUtils.parseMimeType(contentType);
                    if(mimeType.isCompatibleWith(MimeType.valueOf("image/*"))){
                        messageQueueService.send(Topic.IMAGE_THUMB,gridFSFileId.toString());
                    }
                    return Result.successWithData(gridFSFileId.toString());
                }
//
//                    byte[] bytes = file.getBytes();
//                    stream.write(bytes,0,bytes.length);
            } catch (Exception e) {
                logger.debug("上传文件错误:",e);
                return Result.failWithException(e);
            } finally {
//                    try {
////                        if (stream != null) {
////                            stream.close();
////                        }
//                    } catch (IOException e) {
//                        logger.debug("上传文件错误:",e);
//                    }
            }
        } else {
            logger.debug("上传文件为空");
        }
        return Result.failWithMessage("上传失败");
    }
    @Override
//    @PreAuthorize("hasAnyRole('USER','user')")
    public Result upload(@RequestPart  MultipartFile file) {
        String username= SecurityUtil.getUserName();
        logger.debug("upload by {}",username);

        return processOneFile(file);
    }
    @Override
    public JSONObject getFileEntity(String fileId) throws FileNotFoundException, UnsupportedEncodingException {
        ResponseEntity<byte[]> entity = null;
        HttpHeaders headers = new HttpHeaders();

        Query query = Query.query(Criteria.where("_id").is(fileId));
// 查询单个文件
        GridFSFile gfsfile = gridFsTemplate.findOne(query);
        if (gfsfile == null) {
            throw new FileNotFoundException();
        }
        String fileName = gfsfile.getFilename().replace(",", "");

        fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        // 通知浏览器进行文件下载
        try {
            String fileOriginName=(String)gfsfile.getMetadata().get(MetaData.ORIGIN_FILENAME.getValue());

            MimeType type= MimeTypeUtils.parseMimeType((String) gfsfile.getMetadata().get("_contentType"));
            headers.setContentType(new MediaType(type.getType(),type.getSubtype()));
            headers.setContentLength(gfsfile.getLength());
            headers.set("Content-MD5",gfsfile.getMD5());
            headers.setContentDispositionFormData("attachment",fileOriginName);
//            if(gfsfile.getContentType()!=null)
//                headers.setContentType(new MediaType(gfsfile.getContentType()));
        }catch(Exception ex)
        {
            logger.debug("gfsfile:{} has no content type",gfsfile.getId());
        }
        HttpStatus status = HttpStatus.OK;
//        OutputStream sos = response.getOutputStream();
//        GridFSDBFile gridFSDBFile = (GridFSDBFile)gfsfile;
        byte[] fileBuffer = new byte[(int) gfsfile.getLength()];
        int pos=0;
        GridFSDownloadStream in = gridFSBucket.openDownloadStream(gfsfile.getObjectId());
        while(true) {
            pos += in.read(fileBuffer, pos, 1024);
            if(pos>=gfsfile.getLength())
                break;
        }
        in.close();
        JSONObject result=new JSONObject();
        result.put("headers",headers);
        result.put("body",fileBuffer);
        return result;
    }
    @Override
//    @Cacheable(value="method", keyGenerator = "MethodKeyGeneratorCache30S")
    public BizResponseEntity<byte[]> downloadById(@PathVariable("id") String fileId) throws IOException {
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        JSONObject jsoResult=this.getFileEntity(fileId);
        BizResponseEntity entity = new BizResponseEntity<byte[]>(
                jsoResult.getBytes("body"),(HttpHeaders) jsoResult.get("headers"),HttpStatus.OK);

        return entity;
    }

    @Override
    public BizResponseEntity<byte[]> thumbById(@PathVariable("id") String fileId) throws IOException {
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        GridFSFile file=getFile(fileId);
        if(file==null)
        {
            throw new FileNotFoundException();
        }
        GridFSFile thumbFile=getFileByName(file.getFilename()+".small");
        if(thumbFile==null)
        {
            throw new FileNotFoundException();
        }
        return downloadById(((BsonObjectId) thumbFile.getId()).getValue().toString());
    }

    @Override
    public BizResponseEntity<byte[]> downloadByName(@PathVariable("name") String fileName) throws IOException {
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        GridFSFile file=getFileByName(fileName);
        if(file==null)
        {
            throw new FileNotFoundException();
//            response.sendError(HttpServletResponse.SC_GONE);
//            return  null ;//Result.failWithException(new CommonException(ExceptionEnum.FILE_NOT_EXISTS));
        }
        return this.downloadById(((BsonObjectId) file.getId()).getValue().toString());
    }

    @Override
    public BizResponseEntity<byte[]> thumbByName(@PathVariable("name") String oriFileName) throws IOException {

        return downloadByName(oriFileName+".small");
    }

    @Override
    public GridFSFile getFileByName(String fileName) throws IOException {
        GridFSFile gridFSFile=gridFsTemplate.findOne(Query.query(GridFsCriteria.whereFilename().is(fileName)));
        return gridFSFile;
    }

    @Override
    public GridFSFile getFile(String id) throws IOException {
        Query query = Query.query(Criteria.where("_id").is(id));
// 查询单个文件
        GridFSFile gfsfile = gridFsTemplate.findOne(query);
        return gfsfile;
    }

    @Override
    public Result deleteById(@PathVariable("id") String fileId) throws IOException {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(fileId)));
        return Result.successWithData(fileId);
    }
    @Override
    public String getFileUrl(String id) {
        if (!UrlUtil.isUrl(id)) {
            String imageSource = filebase + id;
            return imageSource;
        }
        return id;
    }

    @Override
    public String getFileID(Result multiUploadResult, String filename) {
        String fileSource=null;
        String filenameDup=filename;
        if(filename.startsWith(filebase))
            fileSource=filename.replaceFirst(filebase,"");
        String fileBaseName = FilenameUtils.getName(filename);

        Object dbFileUploadResult=((HashMap<String, String>) multiUploadResult.getData()).get(fileBaseName);
        if(dbFileUploadResult!=null && ((Result)dbFileUploadResult).isSuccess())
        {
            fileSource=(String)((Result)dbFileUploadResult).getData();
        }
        if(fileSource==null && UrlUtil.isUrl(filenameDup)){
            fileSource=filenameDup;
        }
        return fileSource;
    }

}
