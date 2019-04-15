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
//        channel:xx,
//        scenes:[
//        {
//          name:scene1,
//          title:hello,
//          materials:[
//            { name:m1,
//              type:background,
//              fileid:xxx
//            }
//            { name:m2,
//              type:backImage,
//              fileid:xxx
//            }
//          ]
//        },
//        {
//          name:scene2,
//          title:open,
//          materials:[
//            { name:m1,
//              type:background,
//              file:xxx
//            }
//            { name:m2,
//              type:backImage,
//              file:xxx
//            }
//          ]
//        }//
//      ]
//
//    }
    private Bundle process(Result uploadResult ,String detail)
    {
        Bundle bundle=new Bundle();
        JSONObject jsonDetail=JSONObject.parseObject(detail);

        bundle.setChannel(jsonDetail.getString("channel"));

        for (Object sceneObj:jsonDetail.getJSONArray("scenes").toArray()) {
            JSONObject jsonScene=(JSONObject) sceneObj;
            List<Scene> sceneList= bundle.getSceneList();
            if(sceneList==null)
            {
                sceneList=new ArrayList<Scene>();
                bundle.setSceneList(sceneList);
            }
            Scene scene=new Scene();
            sceneList.add(scene);
            scene.setTitle(jsonScene.getString("title"));
            for (Object mat:jsonScene.getJSONArray("materials").toArray()) {
                JSONObject jsonMat = (JSONObject) mat;
                Material material = new Material();
                material.setType(jsonMat.getString("type"));
                material.setFileId(((HashMap<String, String>) uploadResult.getData()).get(jsonMat.getString("file")));
                List<Material> materialList = scene.getMaterialList();
                if (materialList == null) {
                    materialList = new ArrayList<>();
                    scene.setMaterialList(materialList);
                }
                materialList.add(material);
            }
        }
        return bundle;
    }
    @Override
    @PreAuthorize("hasRole('USER')")
    public Result newStory(HttpServletRequest request, HttpServletResponse response, String detail) {
        Bundle bundle;
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        Result uploadResult=fileService.upload(request);
        if(!uploadResult.isSuccess())
            return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));
        bundle=process(uploadResult,detail);
        bundle.setAuthor(user);
        bundle=storyDAO.save(bundle);
        return Result.successWithData(bundle.getId());

    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Result updateStory(HttpServletRequest request, HttpServletResponse response, String id,String detail) {
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        Bundle bundle=storyDAO.get(id);
        if(bundle==null)
        {
            Result uploadResult=fileService.upload(request);
            if(!uploadResult.isSuccess())
                return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));
            bundle=process(uploadResult,detail);
            bundle.setAuthor(user);
            bundle=storyDAO.save(bundle);
        }
        else
        {
            if(bundle.getAuthor().getUsername().compareToIgnoreCase(username)!=0)
            {
                throw new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED);
            }
            Result uploadResult=fileService.upload(request);
            if(!uploadResult.isSuccess())
                return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));
            Bundle bundle2=process(uploadResult,detail);
            bundle2.setAuthor(user);
            storyDAO.updateById(id,bundle2);
        }


        return Result.successWithData(bundle.getId());

    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Result getOneStory(HttpServletRequest request, HttpServletResponse response, String id) {
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        Bundle bundle=storyDAO.get(id);
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
        List<Bundle> bundles=storyDAO.find(Query.query(Criteria.where("author.username").is(un)));
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
        Bundle bundle=storyDAO.get(id);
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
