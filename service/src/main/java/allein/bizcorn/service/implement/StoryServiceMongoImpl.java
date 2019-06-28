package allein.bizcorn.service.implement;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.common.util.UrlUtil;
import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.model.facade.IStory;
import allein.bizcorn.model.input.SoundChannelIO;
import allein.bizcorn.model.input.SoundMessageIO;
import allein.bizcorn.model.mongo.*;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.db.mongo.dao.SoundChannelDAO;
import allein.bizcorn.service.db.mongo.dao.StoryDAO;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IFileService;
import allein.bizcorn.service.facade.IMessageBrokerService;
import allein.bizcorn.service.facade.IStoryService;
import allein.bizcorn.service.facade.IUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import static allein.bizcorn.model.mongo.StoryType.*;

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
    IFileService fileService;
    @Autowired
    IUserService userService;

    @Value("${bizcorn.filebase}")
    private String filebase;

    @Autowired
    private IMessageBrokerService messageBrokerService;
    //   story: {
//        id:
//      name:'作品',
//      desc:'作品描述',
//      likes:90,
//      comments:10,
//      createDate:'timestamp',
//      channel:xx,
//      type: 0:声音变变变，1 超级变变变
//      scenes:[
//            {
//                img:'22.jpg',
//                snd:'test1.mp3'
//            }
//                      ,
//            {
//                img:'23.jpg',
//                snd:'test2.mp3'
//             },
//             {
//                img:'23.jpg',
//                snd:'test3.mp3'
//             }
//      ]
//
//    }

    @Value("${bizcorn.debug}")
    private Boolean isDebug;
    private Story process(Result uploadResult , Story story)
    {
        if(story.getType()==StoryType.SLIDE) {
            for (Scene scene :(List<Scene>) story.getData()) {

                scene.setImg(fileService.getFileID(uploadResult, scene.getImg()));
                scene.setSnd(fileService.getFileID(uploadResult, scene.getSnd()));

            }
            return story;
        }else if(story.getType()==StoryType.SOUND) {
            String snd=(String)story.getData();
            story.setData(fileService.getFileID(uploadResult, snd));
            return story;
        }
        return null;
    }

    private Story checkStoryParam(JSONObject info){
        Integer storyType=info.getInteger("type");
        if(storyType==null )
        {
//            info.put("type",StoryType.SOUND.getValue());
            storyType=0;
        }
        if (storyType==SOUND.getValue())
        {
            SoundStory soundStory=JSON.parseObject(info.toJSONString(),SoundStory.class);
            return soundStory;
        }else if  (storyType==SLIDE.getValue())
        {
           SlideStory slideStory=JSON.parseObject(info.toJSONString(),SlideStory.class);
        }

        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result tell(@RequestPart() MultipartFile[] files,@RequestParam("info") String info){

        User user=userService.getUserFromSession();

        JSONObject infoJso=JSON.parseObject(info);


        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }

        Result uploadResult=fileService.upload(files);
        if(!uploadResult.isSuccess())
            return Result.failWithException(new CommonException(ExceptionEnum.FILE_UPLOAD_FAIL));

        Story story= checkStoryParam(infoJso);
        story=process(uploadResult,story);


//        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

        if(story.getId()==null || story.getId().isEmpty()) {
            //新建
            story.setTalker(user);
            story.setTalkee(user.getCurPartner());
            story.setCreateDate(new Date());
            if(infoJso.getBoolean("publish")) {
                story.setAuditStatus(AuditStatus.PENDING);
            }
            storyDAO.save((SoundStory) story);
        }
        else
        {

            Story storyInDB= storyDAO.get(story.getId());
            if(storyInDB==null || storyInDB.getType()!=story.getType())
            {
                throw new CommonException(ExceptionEnum.FILE_UPLOAD_STORY_ERROR);
            }
            if(!storyInDB.isValidOwner(user))
            {
                throw new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED);
            }
            storyInDB.setData(story.getData());
            if(story.getChannel()!=null)
                storyInDB.setChannel(story.getChannel());
            if(story.getDesc()!=null)
                storyInDB.setDesc(story.getDesc());
            if(story.getName()!=null)
                storyInDB.setName(story.getName());
            if(story.getTags()!=null)
                storyInDB.setTags(story.getTags());
            if(story.getType()!=null)
                storyInDB.setTags(story.getTags());
            if(infoJso.getBoolean("publish")) {
                storyInDB.setAuditStatus(AuditStatus.PENDING);
            }
            storyDAO.save( storyInDB);

        }



        if(infoJso.getBoolean("sync")) {
            if(user instanceof  User && story.getTalkee() instanceof  Kid)
                userService.rebind(user,(Kid)story.getTalkee());
            Message wsMsg = Message.StoryArrivedMessage(story);
            messageBrokerService.send(wsMsg);
            wsMsg=Message.StoryAckMessage(story);
            messageBrokerService.send(wsMsg);
        }
        if(isDebug && infoJso.getBoolean("echo")){
            story.setTalkee(story.getTalker());
            Message wsMsg = Message.StoryArrivedMessage(story);
            messageBrokerService.send(wsMsg);
            wsMsg=Message.StoryAckMessage(story);
            messageBrokerService.send(wsMsg);
        }
        return Result.successWithData(story.toString());

    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result get(@PathVariable("id") String id) {
        String username= SecurityUtil.getUserName();

        User user=userDAO.selectByName(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        Story story=storyDAO.get(id);
        if(!story.isPublished()&& !story.isValidOwner(user))
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
        }
        return Result.successWithData(story);
    }


    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result getSoundChannels() {
        List<SoundChannel> soundChannels= soundChannelDAO.find(new Query());

        if(soundChannels==null || soundChannels.size()<4)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.CHANNELS_NOT_INITED));
        }
        JSONArray channels=new JSONArray();

        return Result.successWithData(soundChannels);
    }
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    @Override
    public Result setSoundChannels(@RequestPart MultipartFile[] files,@RequestParam("channels") String  channelsJson) {
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

//    @Override
//    @PreAuthorize("hasAnyRole('USER','user')")
//    public Result msgUp(@RequestPart MultipartFile[] files,@RequestParam("message") String msgJson) {
//        User user=userDAO.select(SecurityUtil.getUserName());
//        if(user==null)
//        {
//            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
//        }
//        if(user.getCurPartner()==null)
//        {
//            return Result.failWithException(new CommonException(ExceptionEnum.BIND_NOT_EXIST));
//        }
//
//        User talkee=user.getCurPartner();
//        if(talkee instanceof LazyLoadingProxy)
//            talkee= (User) ((LazyLoadingProxy) talkee).getTarget();
//
//        Result fileResult=fileService.upload(files);
//        SoundMessageIO msg=JSON.parseObject(msgJson,SoundMessageIO.class);
//        String fileID=fileService.getFileID(fileResult,msg.getSnd());
//
//        msg.setSnd(fileID);
//
//        userService.rebind(user,(Kid)talkee);
//
//        SoundMessage soundMessage=new SoundMessage();
//        soundMessage.setChannel(msg.getChannel());
//        soundMessage.setCreateDate(new Date());
//        soundMessage.setTalker(user);
//        soundMessage.setTalkee(user.getCurPartner());
//        soundMessage.setSnd(fileID);
//        soundMessage.setStatus(MessageStatus.INIT);
//        SoundMessage savedSoundMessage= soundMessageDAO.save(soundMessage);
//
//        if(msg.getSync()) {
//            Message wsMsg = Message.SoundMorphyArrivedMessage(soundMessage);
//            messageBrokerService.send(wsMsg);
//
//        }
//        if(isDebug && msg.getEcho()){
//            soundMessage.setTalkee(soundMessage.getTalker());
//            Message wsMsg = Message.SoundMorphyArrivedMessage(soundMessage);
//            messageBrokerService.send(wsMsg);
//        }
//        return  Result.successWithData(savedSoundMessage.getId());
//
//    }

//    @Override
//    @PreAuthorize("hasAnyRole('USER','user')")
//    public Result msg(@PathVariable("id") String messageId) {
//        User user=userDAO.select(SecurityUtil.getUserName());
//        if(user==null)
//        {
//            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
//        }
//
//        SoundStory savedSoundMessage= soundStoryDAO.get(messageId);
//        if(savedSoundMessage!=null){
//            if(user.getId().compareToIgnoreCase(savedSoundMessage.getTalkee().getId())!=0)
//            {
//                return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
//            }
////            SerializeConfig config=new SerializeConfig();
////            config.put(User.class,new User.SimpleSerializer());
////            config.put(Kid.class,new User.SimpleSerializer());
//
//            return Result.successWithData(savedSoundMessage);
//        }
//        return Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_NOT_EXIST));
//    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result action(@PathVariable("action") String action,@PathVariable("id") String id,@RequestBody(required=false) JSONObject param) {
        User user=userService.getUserFromSession();
        Story story=storyDAO.get(id);
        if(story==null)
            return Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_NOT_EXIST));
        if(action!=null)
        {
           if(action.compareToIgnoreCase("delete")==0 )
           {
                if(!story.isValidOwner(user)){
                    return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
                }
               storyDAO.deleteById(story);
               return Result.successWithMessage("");
           }else if(action.compareToIgnoreCase("send")==0)
           {
               if(story.getTalker().getId().compareToIgnoreCase(user.getId())==0){
                   //私有对话重发
                   Message wsMsg = Message.StoryArrivedMessage(story);
                   messageBrokerService.send(wsMsg);
                   wsMsg = Message.StoryAckMessage(story);
                   messageBrokerService.send(wsMsg);
                   return Result.successWithMessage("");
               }
               else if(story.isPublished())
               {
                   //从库里转发
                   Story soundMessageReplicated= null;
                   try {
                       soundMessageReplicated = story.getClass().newInstance();
                   } catch (InstantiationException e) {
                       return Result.failWithException(e);
                   } catch (IllegalAccessException e) {
                       return Result.failWithException(e);
                   }

                   BeanUtils.copyProperties(story,soundMessageReplicated);
                   soundMessageReplicated.setTalker(user);
                   soundMessageReplicated.setTalkee(user.getCurPartner());
                   soundMessageReplicated.setCreateDate(new Date());
                   soundMessageReplicated.setAuditDate(null);
                   soundMessageReplicated.setAuditStatus(AuditStatus.NONE);
                   soundMessageReplicated.setDeliverDate(new Date());
                   storyDAO.save(soundMessageReplicated);
                   Message wsMsg = Message.StoryArrivedMessage(soundMessageReplicated);
                   messageBrokerService.send(wsMsg);
                   wsMsg = Message.StoryAckMessage(soundMessageReplicated);
                   messageBrokerService.send(wsMsg);
                   return Result.successWithData(soundMessageReplicated.getId());
               }

           }else if(action.compareToIgnoreCase("publish")==0)
           {
               if(!story.isValidOwner(user)){
                   return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
               }
               if(story.getAuditStatus()==AuditStatus.NONE)
               {
                   story.setAuditFireDate(new  Date());
                   story.setAuditStatus(AuditStatus.PENDING);
                   if(param!=null) {
                       if(param.getString("name")!=null)
                           story.setName(param.getString("name"));
                       if(param.getString("desc")!=null)
                           story.setDesc(param.getString("desc"));
                       if(param.getJSONArray("tags")!=null) {
                           story.setTags(param.getJSONArray("tags").toJavaList(String.class));
                       }
                   }
                   storyDAO.save(story);
                   return Result.successWithMessage("");
               }
               else
               {
                   return Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_AUDIT_STATUS_ERROR));
               }

           }else if(action.compareToIgnoreCase("edit")==0)
           {
               if(!story.isValidOwner(user)){
                   return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
               }
               story.setAuditFireDate(new  Date());
               story.setAuditStatus(AuditStatus.PENDING);
               if(param!=null) {
                   if(param.getString("name")!=null)
                       story.setName(param.getString("name"));
                   if(param.getString("desc")!=null)
                       story.setDesc(param.getString("desc"));
                   if(param.getJSONArray("tags")!=null) {
                       story.setTags(param.getJSONArray("tags").toJavaList(String.class));
                   }
               }
               //修改过的mesg，审核状态置为未审核，未发布
               story.setAuditStatus(AuditStatus.NONE);
               storyDAO.save(story);
               return Result.successWithMessage("");
           }
        }
        return  Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_ACTION_ERROR));
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result copy(@PathVariable("id") String  messageId) {
        User user=userService.getUserFromSession();
        if(messageId.compareToIgnoreCase("all")==0)
        {
            storyDAO.update(Query.query(Criteria.where("talkee.$id").is(user.getId()).andOperator(Criteria.where("status").ne(MessageStatus.COPIED))),Update.update("status",MessageStatus.COPIED));
            return Result.successWithData(messageId);
        }
        Story savedStory= storyDAO.get(messageId);
        if(savedStory!=null){
            if(user.getId().compareToIgnoreCase(savedStory.getTalkee().getId())!=0)
            {
                return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
            }
            savedStory.setCreateDate(new Date());
            savedStory.setStatus(MessageStatus.COPIED);
            storyDAO.save(savedStory);
            return Result.successWithData(messageId);
        }
        return Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_NOT_EXIST));

    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result list(@RequestBody JSONObject params)
    {
        User user=userService.getUserFromSession();
        if(user==null)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
        }

        JSONArray filters=params.getJSONArray("filters");

        if(filters==null)
            filters=new JSONArray();

        if(params.getInteger("repo")==null||params.getInteger("repo")==0)
        {
            JSONObject filter=new JSONObject();
            filter.put("op","or");
            JSONArray subFilters=new JSONArray();
            subFilters.add(JSON.toJSON(new Filter("talkee.$id","is",user.getId())));
            subFilters.add(JSON.toJSON(new Filter("talker.$id","is",user.getId())));
            filter.put("val",subFilters);
            filters.add(filter);
        }
        else
        {
            filters.add(JSON.toJSON(new Filter("auditStatus","is",AuditStatus.APPROVED)));
        }

        if(params.getInteger("type")==1)
        {
            filters.add(JSON.toJSON(new Filter("type","is",StoryType.SLIDE)));
        }
        else
            filters.add(JSON.toJSON(new Filter("type","is",StoryType.SOUND)));

        params.put("filters",filters);


        JSONObject messages=storyDAO.list(params);
        if(params.getInteger("setCopied")!=null && params.getInteger("setCopied")==1)
        {
            Criteria criteria=storyDAO.buildCriteria(filters);
            storyDAO.update(new Query(criteria),Update.update("status",MessageStatus.COPIED));
        }


        SerializeConfig config=new SerializeConfig();
        config.put(User.class,new User.SimpleSerializer());
        config.put(Kid.class,new User.SimpleSerializer());
        messages.put("list",JSONArray.parse( JSON.toJSONString(messages.get("list"),config)));
        return Result.successWithData(messages);

    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    @ResponseBody
    public Result adminGetSoundChannels() {
        List<SoundChannel> soundChannels= soundChannelDAO.find(new Query());

        return Result.successWithData(soundChannels);

    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    @ResponseBody
    public Result adminAddSoundChannel(@RequestBody  JSONObject channel) {
        SoundChannel soundChannel= (SoundChannel) soundChannelDAO.findOne(new Query(Criteria.where("index").is(channel.getLong("index"))));
        if(soundChannel!=null)
            return Result.failWithException(new CommonException(ExceptionEnum.ADMIN_SOUND_CHANNEL_OCCUPIED));
        soundChannel=new SoundChannel();
        soundChannel.setImg(channel.getString("img"));
        soundChannel.setIndex(channel.getInteger("index"));
        soundChannel.setDesc(channel.getString("desc"));
        soundChannel.setName(channel.getString("name"));
        soundChannel=soundChannelDAO.save(soundChannel);
        return Result.successWithData(soundChannel);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    @ResponseBody
    public Result adminUpdateSoundChannel(@RequestBody  JSONObject channel) {
        SoundChannel soundChannel= (SoundChannel) soundChannelDAO.get(channel.getString("id"));
        if(soundChannel==null)
            return Result.failWithException(new CommonException(ExceptionEnum.ADMIN_SOUND_CHANNEL_NOT_EXIST));
        soundChannel.setImg(channel.getString("img"));
        soundChannel.setIndex(channel.getInteger("index"));
        soundChannel.setDesc(channel.getString("desc"));
        soundChannel.setName(channel.getString("name"));
        soundChannel=soundChannelDAO.save(soundChannel);
        return Result.successWithData(soundChannel);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    @ResponseBody
    public Result adminDeleteSoundChannel(@RequestBody  JSONObject channel) {
        SoundChannel soundChannel= (SoundChannel) soundChannelDAO.get(channel.getString("id"));
        if(soundChannel==null)
            return Result.failWithException(new CommonException(ExceptionEnum.ADMIN_SOUND_CHANNEL_NOT_EXIST));

        soundChannelDAO.deleteById(soundChannel);
        return Result.successWithData(soundChannel);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminGetSounds(@RequestBody  JSONObject params) {
        JSONArray filters=params.getJSONArray("filters");

        if(filters==null)
            filters=new JSONArray();

//        filters.add(JSON.toJSON(new Filter("auditStatus","is",AuditStatus.PENDING)));

//        params.put("filters",filters);

        JSONObject result=storyDAO.list(params);
        SerializeConfig config=new SerializeConfig();
        config.put(User.class,new User.SimpleSerializer());
        config.put(Kid.class,new User.SimpleSerializer());
        result.put("list",JSONArray.parse( JSON.toJSONString(result.get("list"),config)));
        return Result.successWithData(result);

    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','admin')")
    public Result adminAuditSound(@RequestBody  JSONObject data) {
        JSONArray ids=data.getJSONArray("ids");

        Boolean audit=data.getBoolean("audit");
        storyDAO.update(Query.query(Criteria.where("_id").in(ids.toJavaList(String.class))),
                Update.update("auditStatus",audit?AuditStatus.APPROVED:AuditStatus.REJECTED).set("auditDate",new Date()));

        List<Story> messages=storyDAO.find(Query.query(Criteria.where("_id").in(ids.toJavaList(String.class))));
        SerializeConfig config=new SerializeConfig();
        config.put(User.class,new User.SimpleSerializer());
        config.put(Kid.class,new User.SimpleSerializer());
        JSONArray result= JSONArray.parseArray( JSON.toJSONString(messages,config));
        return Result.successWithData(result);
    }
}
