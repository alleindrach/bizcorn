package allein.bizcorn.service.implement;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.common.util.UrlUtil;
import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.model.input.SoundChannelIO;
import allein.bizcorn.model.input.SoundMessageIO;
import allein.bizcorn.model.mongo.*;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.db.mongo.dao.SoundChannelDAO;
import allein.bizcorn.service.db.mongo.dao.SoundMessageDAO;
import allein.bizcorn.service.db.mongo.dao.StoryDAO;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IFileService;
import allein.bizcorn.service.facade.IMessageBrokerService;
import allein.bizcorn.service.facade.IStoryService;
import allein.bizcorn.service.facade.IUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RefreshScope
public class StoryServiceMongoImpl implements IStoryService{
    @Autowired
    UserDAO userDAO;
    @Autowired
    StoryDAO storyDAO;
    @Autowired
    SoundChannelDAO soundChannelDAO;
    @Autowired
    SoundMessageDAO soundMessageDAO;
    @Autowired
    IFileService fileService;
    @Autowired
    IUserService userService;

    @Value("${bizcorn.filebase}")
    private String filebase;

    @Autowired
    private IMessageBrokerService messageBrokerService;
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



    private Story process(Result uploadResult , String detail)
    {
        Story story=new Story();
        JSONObject jsonDetail=JSONObject.parseObject(detail);

        story.setChannel(jsonDetail.getString("channel"));

        for (Object sceneObj:jsonDetail.getJSONArray("scenes").toArray()) {
            JSONObject jsonScene=(JSONObject) sceneObj;
            List<Scene> sceneList= story.getScenes();
            if(sceneList==null)
            {
                sceneList=new ArrayList<Scene>();
                story.setScenes(sceneList);
            }
            Scene scene=new Scene();
            sceneList.add(scene);
            scene.setTitle(jsonScene.getString("title"));
            String fileSource=null;
            scene.setImg(fileService.getFileID(uploadResult,jsonScene.getString("img")));
            scene.setSnd(fileService.getFileID(uploadResult,jsonScene.getString("snd")));

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
//`
//    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result syncStory(@RequestPart MultipartFile[] files, String id, String work){
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
//        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        Story story= null;
        if(id!=null && !id.isEmpty())
            story=storyDAO.get(id);
        if(story==null)
        {
            Result uploadResult=fileService.upload(files);
            if(!uploadResult.isSuccess())
                return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));
            story=process(uploadResult,work);
            story.setAuthor(user);
            story=storyDAO.save(story);
        }
        else
        {
            if(story.getAuthor().getUsername().compareToIgnoreCase(username)!=0)
            {
                throw new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED);
            }
            Result uploadResult=fileService.upload(files);
            if(!uploadResult.isSuccess())
                return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));
            Story bundle2=process(uploadResult,work);
            bundle2.setAuthor(user);
            storyDAO.updateById(id,bundle2);
        }
        return Result.successWithData(story.toString(this.filebase));

    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result getOneStory(@PathVariable("id") String id) {
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        Story bundle=storyDAO.get(id);
        return Result.successWithData(bundle);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result getAllStory(@PathVariable("username") String username) {
        if(username==null)
        {
            username= SecurityUtil.getUserName();
        }

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        List<Story> bundles=storyDAO.find(Query.query(Criteria.where("author.$id").is(new ObjectId(user.getId()))));
        return Result.successWithData(bundles);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result deleteStory(@PathVariable("id") String id) {
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        if(id.compareToIgnoreCase("*")!=0) {
            Story bundle = storyDAO.get(id);
            if (bundle == null)
                return Result.failWithException(new CommonException(ExceptionEnum.FILE_NOT_EXISTS));

            if (bundle.getAuthor().getUsername().compareToIgnoreCase(username) != 0) {
                throw new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED);
            }

            storyDAO.deleteById(bundle);
            return Result.successWithData(id);
        }else
        {
            storyDAO.deleteByQuery(new Query());
            return Result.successWithData(id);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result getSoundChannelBGs() {
        List<SoundChannel> soundChannels= soundChannelDAO.find(new Query());

        if(soundChannels==null || soundChannels.size()<4)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.CHANNELS_NOT_INITED));
        }
        JSONArray channels=new JSONArray();
        soundChannels.forEach(channel->{
            JSONObject channelJson=new JSONObject();
            channelJson.put("img",fileService.getFileUrl(channel.getImg()));
            channels.add(channelJson);

        });
        return Result.successWithData(channels);
    }
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    @Override
    public Result setSoundChannelBG(@RequestPart MultipartFile[] files,@RequestParam("channels") String  channelsJson) {
        Result fileResult=fileService.upload(files);
        HashMap<String, Result> entrys=null;
        if(fileResult.isSuccess()) {
            entrys= (HashMap<String, Result>) fileResult.getData();
        }
//        JSONArray jsonChannels= JSONArray.parseArray(channelsJson);
        List<SoundChannelIO> channels=JSONArray.parseArray(channelsJson,SoundChannelIO.class);
        int i=0;
        for(Iterator iterator = channels.iterator() ; iterator.hasNext();)
        {
            SoundChannelIO item = (SoundChannelIO) iterator.next();
            String filename=item.getImg();
            SoundChannel soundChannel = soundChannelDAO.selectByIndex(i);
            if (soundChannel == null) {
                soundChannel = new SoundChannel();
                soundChannel.setIndex(i);
            }
            String fileLoc=fileService.getFileID(fileResult,filename);
            soundChannel.setImg(fileLoc);
//            if (entrys != null && entrys.size() > 0) {
//                Result uploaded = entrys.get(filename) == null ? (Result) entrys.get(filename) : null;
//                if (uploaded != null) {
            item.setImg(fileService.getFileUrl(fileLoc));
//                    soundChannel.setBgPictureId((String) uploaded.getData());
//                }
//            }
            soundChannelDAO.save(soundChannel);
            i++;
        }
        return  Result.successWithData(JSON.toJSON(channels));

    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result msgUp(@RequestPart MultipartFile[] files,@RequestParam("message") String msgJson) {
        User user=userDAO.select(SecurityUtil.getUserName());
        if(user==null)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
        }
        if(user.getCurPartner()==null)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.BIND_NOT_EXIST));
        }

        User talkee=user.getCurPartner();

        Result fileResult=fileService.upload(files);
        SoundMessageIO msg=JSON.parseObject(msgJson,SoundMessageIO.class);
        String fileID=fileService.getFileID(fileResult,msg.getSnd());

        msg.setSnd(fileID);

        userService.rebind(user,(Kid)talkee);

        SoundMessage soundMessage=new SoundMessage();
        soundMessage.setChannel(msg.getChannel());
        soundMessage.setCreateDate(new Date());
        soundMessage.setTalker(user);
        soundMessage.setTalkee(user.getCurPartner());
        soundMessage.setSnd(fileID);
        soundMessage.setStatus(MessageStatus.INIT);
        SoundMessage savedSoundMessage= soundMessageDAO.save(soundMessage);


        Message wsMsg = Message.SoundMorphyArrivedMessage(soundMessage);
        messageBrokerService.send(wsMsg);

        return  Result.successWithData(savedSoundMessage.getId());

    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result msgCopy(@PathVariable("id") String  messageId) {
        User user=userDAO.select(SecurityUtil.getUserName());
        if(user==null)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
        }

        SoundMessage savedSoundMessage= soundMessageDAO.get(messageId);
        if(savedSoundMessage!=null){
            if(user.getId().compareToIgnoreCase(savedSoundMessage.getTalkee().getId())!=0)
            {
                return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
            }
            savedSoundMessage.setCreateDate(new Date());
            savedSoundMessage.setStatus(MessageStatus.COPIED);
            soundMessageDAO.save(savedSoundMessage);
            return Result.successWithData(messageId);
        }
        return Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_NOT_EXIST));

    }

    @Override
    public Result msgList(@RequestParam("criteria") String criteria, @RequestParam("page") Integer  pageIndex,@RequestParam("size") Integer  pageSize)
    {
        if(pageIndex==null)
            pageIndex=0;
        if(pageIndex<0)
            pageIndex=0;

        if(pageSize==null)
            pageSize=10;
        if(pageSize<0)
            pageSize=10;
        if(pageSize>20)
            pageSize=20;

        User user=userDAO.select(SecurityUtil.getUserName());
        if(user==null)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
        }
        List<SoundMessage> soundMessages=soundMessageDAO.find(Query.query(Criteria.where("status").ne(MessageStatus.COPIED).orOperator(Criteria.where("talkee").is(user.getId()),Criteria.where("talker").is(user.getId()))).with(Sort.by(Sort.Direction.DESC, "createDate")).skip(pageIndex*pageSize).limit(pageSize));
//        JSONObject[] resultData= (JSONObject[]) soundMessages.stream().map((SoundMessage x)->{return x.toResultJson();}).toArray();
        return Result.successWithData(soundMessages);

    }
}
