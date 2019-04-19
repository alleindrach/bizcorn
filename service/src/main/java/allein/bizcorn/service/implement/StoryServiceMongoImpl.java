package allein.bizcorn.service.implement;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.model.mongo.*;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.db.mongo.dao.StoryDAO;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IFileService;
import allein.bizcorn.service.facade.IStoryService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class StoryServiceMongoImpl implements IStoryService{
    @Autowired
    UserDAO userDAO;
    @Autowired
    StoryDAO storyDAO;
    @Autowired
    IFileService fileService;

//    {
//      titleIcon:{source},
//      title:'作品',
//      titleDescription:'作品描述',
//      likes:90,
//      comments:10,
//      age:'11小时前',
//      channel:xx,
//      scenes:[
//            {
//                img:'https://www.xinrong.com/webapp2.0/webapp3.0/images/banner/22.jpg',
//                        snd:'http://192.168.2.149/s/test.mp3'
//            }
//                      ,
//            {
//                img:'https://www.xinrong.com/webapp2.0/webapp3.0/images/banner/23.jpg',
//                            snd:'http://192.168.2.149/s/test.mp3'
//             },
//             {
//                img:'https://www.xinrong.com/webapp2.0/webapp3.0/images/banner/20.jpg',
//                            snd:'http://192.168.2.149/s/test.mp3'
//             }
//      ]
//
//    }
    private String getUploadFileSource(Result uploadResult,String filename)
    {
        String fileSource=null;
        Object dbFileUploadResult=((HashMap<String, String>) uploadResult.getData()).get(filename);
        if(dbFileUploadResult!=null && ((Result)dbFileUploadResult).isSuccess())
        {
            fileSource=(String)((Result)dbFileUploadResult).getData();
        }
        if(fileSource==null){
            fileSource=filename;
        }
        return fileSource;
    }
    private Story process(Result uploadResult ,String detail)
    {
        Story story=new Story();
        JSONObject jsonDetail=JSONObject.parseObject(detail);

        story.setChannel(jsonDetail.getString("channel"));

        for (Object sceneObj:jsonDetail.getJSONArray("scenes").toArray()) {
            JSONObject jsonScene=(JSONObject) sceneObj;
            List<Scene> sceneList= story.getSceneList();
            if(sceneList==null)
            {
                sceneList=new ArrayList<Scene>();
                story.setSceneList(sceneList);
            }
            Scene scene=new Scene();
            sceneList.add(scene);
            scene.setTitle(jsonScene.getString("title"));
            String fileSource=null;
            scene.setImageSource(getUploadFileSource(uploadResult,jsonScene.getString("img")));
            scene.setSoundSource(getUploadFileSource(uploadResult,jsonScene.getString("snd")));

        }
        return story;
    }
//    @Override
//    @PreAuthorize("hasRole('USER')")
//    public Result syncStory(@RequestParam HttpServletRequest request, @RequestParam HttpServletResponse response ,
//                            @RequestParam("work") String detail);
//        Bundle bundle;
//        String username= SecurityUtil.getUserName();
//
//        User user=userDAO.selectByName(username);
//        if (user == null) {
//            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
//        }
//        Result uploadResult=fileService.upload(request);
//        if(!uploadResult.isSuccess())
//            return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));
//        bundle=process(uploadResult,detail);
//        bundle.setAuthor(user);
//        bundle=storyDAO.save(bundle);
//        return Result.successWithData(bundle.getId());
//
//    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Result syncStory(HttpServletRequest request, HttpServletResponse response ,
                            String id,String detail){
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }

        Story story= null;
        if(id!=null && id.isEmpty())
            story=storyDAO.get(id);
        if(story==null)
        {
            Result uploadResult=fileService.upload(request);
            if(!uploadResult.isSuccess())
                return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));
            story=process(uploadResult,detail);
            story.setAuthor(user);
            story=storyDAO.save(story);
        }
        else
        {
            if(story.getAuthor().getUsername().compareToIgnoreCase(username)!=0)
            {
                throw new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED);
            }
            Result uploadResult=fileService.upload(request);
            if(!uploadResult.isSuccess())
                return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));
            Story bundle2=process(uploadResult,detail);
            bundle2.setAuthor(user);
            storyDAO.updateById(id,bundle2);
        }


        return Result.successWithData(story.getId());

    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Result getOneStory(HttpServletRequest request, HttpServletResponse response, String id) {
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        Story bundle=storyDAO.get(id);
        return Result.successWithData(bundle);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Result getAllStory(HttpServletRequest request, HttpServletResponse response, String un) {
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        List<Story> bundles=storyDAO.find(Query.query(Criteria.where("author.username").is(un)));
        return Result.successWithData(bundles);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Result deleteOneStory(HttpServletRequest request, HttpServletResponse response,  String id) {
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        Story bundle=storyDAO.get(id);
        if(bundle==null)
            return  Result.failWithException(new CommonException(ExceptionEnum.BUNDLE_NOT_EXISTS));

        if(bundle.getAuthor().getUsername().compareToIgnoreCase(username)!=0)
        {
            throw new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED);
        }

        storyDAO.deleteById(bundle);
        return Result.successWithData(id);
    }
}