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

    @Value("${bizcorn.debug}")
    private Boolean isDebug;
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
            channelJson.put("img",channel.getImg());
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
        if(talkee instanceof LazyLoadingProxy)
            talkee= (User) ((LazyLoadingProxy) talkee).getTarget();

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

        if(msg.getSync()) {
            Message wsMsg = Message.SoundMorphyArrivedMessage(soundMessage);
            messageBrokerService.send(wsMsg);

        }
        if(isDebug && msg.getEcho()){
            soundMessage.setTalkee(soundMessage.getTalker());
            Message wsMsg = Message.SoundMorphyArrivedMessage(soundMessage);
            messageBrokerService.send(wsMsg);
        }
        return  Result.successWithData(savedSoundMessage.getId());

    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result msg(@PathVariable("id") String messageId) {
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
//            SerializeConfig config=new SerializeConfig();
//            config.put(User.class,new User.SimpleSerializer());
//            config.put(Kid.class,new User.SimpleSerializer());

            return Result.successWithData(savedSoundMessage);
        }
        return Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_NOT_EXIST));
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result msgAction(@PathVariable("action") String action,@PathVariable("id") String msgId,@RequestBody(required=false) JSONObject param) {
        User user=userService.getUserFromSession();
        SoundMessage soundMessage=soundMessageDAO.get(msgId);
        if(soundMessage==null)
            return Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_NOT_EXIST));
        if(action!=null)
        {
           if(action.compareToIgnoreCase("delete")==0 )
           {
                if(!soundMessage.isValidOwner(user)){
                    return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
                }
               soundMessageDAO.deleteById(soundMessage);
               return Result.successWithMessage("");
           }else if(action.compareToIgnoreCase("send")==0)
           {
               if(soundMessage.getTalker().getId().compareToIgnoreCase(user.getId())==0){
                   //私有对话重发
                   Message wsMsg = Message.SoundMorphyArrivedMessage(soundMessage);
                   messageBrokerService.send(wsMsg);
                   return Result.successWithMessage("");
               }
               else if(soundMessage.isPublished())
               {
                   //从库里转发
                   SoundMessage soundMessageReplicated=new SoundMessage();
                   BeanUtils.copyProperties(soundMessage,soundMessageReplicated);
                   soundMessageReplicated.setTalker(user);
                   soundMessageReplicated.setTalkee(user.getCurPartner());
                   soundMessageReplicated.setCreateDate(new Date());
                   soundMessageReplicated.setAuditDate(null);
                   soundMessageReplicated.setAuditStatus(AuditStatus.NONE);
                   soundMessageReplicated.setDeliverDate(new Date());
                   soundMessageDAO.save(soundMessageReplicated);
                   Message wsMsg = Message.SoundMorphyArrivedMessage(soundMessageReplicated);
                   messageBrokerService.send(wsMsg);
                   return Result.successWithData(soundMessageReplicated.getId());
               }

           }else if(action.compareToIgnoreCase("publish")==0)
           {
               if(!soundMessage.isValidOwner(user)){
                   return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
               }
               if(soundMessage.getAuditStatus()==AuditStatus.NONE)
               {
                   soundMessage.setAuditFireDate(new  Date());
                   soundMessage.setAuditStatus(AuditStatus.PENDING);
                   if(param!=null) {
                       if(param.getString("name")!=null)
                        soundMessage.setName(param.getString("name"));
                       if(param.getString("desc")!=null)
                           soundMessage.setDesc(param.getString("desc"));
                       if(param.getJSONArray("tags")!=null) {
                           soundMessage.setTags(param.getJSONArray("tags").toJavaList(String.class));
                       }
                   }
                   soundMessageDAO.save(soundMessage);
                   return Result.successWithMessage("");
               }
               else
               {
                   return Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_AUDIT_STATUS_ERROR));
               }

           }else if(action.compareToIgnoreCase("edit")==0)
           {
               if(!soundMessage.isValidOwner(user)){
                   return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
               }
               soundMessage.setAuditFireDate(new  Date());
               soundMessage.setAuditStatus(AuditStatus.PENDING);
               if(param!=null) {
                   if(param.getString("name")!=null)
                       soundMessage.setName(param.getString("name"));
                   if(param.getString("desc")!=null)
                       soundMessage.setDesc(param.getString("desc"));
                   if(param.getJSONArray("tags")!=null) {
                       soundMessage.setTags(param.getJSONArray("tags").toJavaList(String.class));
                   }
               }
               //修改过的mesg，审核状态置为未审核，未发布
               soundMessage.setAuditStatus(AuditStatus.NONE);
               soundMessageDAO.save(soundMessage);
               return Result.successWithMessage("");
           }
        }
        return  Result.failWithException(new CommonException(ExceptionEnum.MESSAGE_ACTION_ERROR));
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result msgCopy(@PathVariable("id") String  messageId) {
        User user=userDAO.select(SecurityUtil.getUserName());
        if(user==null)
        {
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
        }
        if(messageId.compareToIgnoreCase("all")==0)
        {
            soundMessageDAO.update(Query.query(Criteria.where("talkee.$id").is(user.getId()).andOperator(Criteria.where("status").ne(MessageStatus.COPIED))),Update.update("status",MessageStatus.COPIED));
            return Result.successWithData(messageId);
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
    @PreAuthorize("hasAnyRole('USER','user')")
    public Result msgList(@RequestBody JSONObject params)
    {
        User user=userDAO.select(SecurityUtil.getUserName());
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


        params.put("filters",filters);


        JSONObject messages=soundMessageDAO.list(params);
        if(params.getInteger("setCopied")!=null && params.getInteger("setCopied")==1)
        {
            Criteria criteria=soundMessageDAO.buildCriteria(filters);
            soundMessageDAO.update(new Query(criteria),Update.update("status",MessageStatus.COPIED));
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

        JSONObject result=soundMessageDAO.list(params);
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
        soundMessageDAO.update(Query.query(Criteria.where("_id").in(ids.toJavaList(String.class))),
                Update.update("auditStatus",audit?AuditStatus.APPROVED:AuditStatus.REJECTED).set("auditDate",new Date()));

        List<SoundMessage> messages=soundMessageDAO.find(Query.query(Criteria.where("_id").in(ids.toJavaList(String.class))));
        SerializeConfig config=new SerializeConfig();
        config.put(User.class,new User.SimpleSerializer());
        config.put(Kid.class,new User.SimpleSerializer());
        JSONArray result= JSONArray.parseArray( JSON.toJSONString(messages,config));
        return Result.successWithData(result);
    }
}
