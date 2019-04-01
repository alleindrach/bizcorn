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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@RefreshScope
public class FileServiceImpl implements IFileService {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    @Value("${file.uploadRoot}")
    private String FileRoot;

    @Override
    @PreAuthorize("hasRole('USER')")
    public Result upload(HttpServletRequest request) {
        String username= SecurityUtil.getUserName();
        logger.debug("upload by {}",username);
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        Result r= Result.successWithMessage("Success!");
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
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
                    byte[] bytes = file.getBytes();
                    stream.write(bytes,0,bytes.length);
                } catch (Exception e) {
                    logger.debug("上传文件错误:",e);
                    r= Result.failWithException(e);
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

        return r;
    }
    @Override
    public void download(@RequestParam String fileName,@RequestParam HttpServletResponse response) {

        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(new File(FileRoot
                    + fileName)));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            logger.debug("下载错误",e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.debug("下载错误",e);
                }
            }
        }
        logger.debug("success");
    }


}
