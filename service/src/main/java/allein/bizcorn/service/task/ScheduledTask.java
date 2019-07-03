package allein.bizcorn.service.task;

import allein.bizcorn.model.mongo.Story;
import allein.bizcorn.service.db.mongo.dao.SoundChannelDAO;
import allein.bizcorn.service.db.mongo.dao.StoryDAO;
import allein.bizcorn.service.facade.IStoryService;
import com.mongodb.Block;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Configurable
@EnableScheduling
@EnableAsync
public class ScheduledTask {
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations operations;
    @Autowired
    private GridFSBucket gridFSBucket;


    @Autowired
    private StoryDAO storyDAO;
    @Autowired
    private SoundChannelDAO soundChannelDAO;

    @Autowired
    private IStoryService storyService;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    private static final SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//    @Scheduled(fixedRate = 5000)
    public void scheduledDemo() {
        logger.info("scheduled - fixedRate - print time every 5 seconds:{}", formate.format(new Date()));
    }

    /**
     * "0/5 * *  * * ?"   每5秒触发
     * "0 0 12 * * ?"    每天中午十二点触发
     * "0 15 10 ? * *"    每天早上10：15触发
     * "0 15 10 * * ?"    每天早上10：15触发
     * "0 15 10 * * ? *"    每天早上10：15触发
     * "0 15 10 * * ? 2005"    2005年的每天早上10：15触发
     * "0 * 14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发
     * "0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发
     * "0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发
     * "0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发
     * "0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发
     * "0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发
     * http://cron.qqe2.com/
     */
//    @Scheduled(cron = "0 0 0/4 * * ? ")
//    @Async("ScheduleTaskAsyncExecutor")
    public void scheduledImageFileRecycleTask() {
        logger.info("scheduled - scheduledImageFileRecycleTask ");
        GridFSFindIterable filesIterable= gridFsTemplate.find(Query.query(Criteria.where("uploadDate").lt(new Date(System.currentTimeMillis()-1*86400000))).with(Sort.by(new Sort.Order(Sort.Direction.ASC,"uploadDate"))));
        filesIterable.forEach((Block)(gridFSFile -> {
            String md5name = ((GridFSFile) gridFSFile).getFilename();
            if (!md5name.contains(".small") ) {
                Boolean  isFileOccupied = storyDAO.isSotryIncludeFileExists(((BsonObjectId) ((GridFSFile) gridFSFile).getId()).getValue().toString())
                        || soundChannelDAO.isChannelIncludeFileExists(((BsonObjectId) ((GridFSFile) gridFSFile).getId()).getValue().toString());
                if (!isFileOccupied) {
                    logger.info("[FileDelete>>>>>>] {}",((GridFSFile) gridFSFile).getId());
                    gridFsTemplate.delete(Query.query(GridFsCriteria.whereFilename().regex("^"+md5name+"")));
                }
            }
        }
        ));
    }
    @Scheduled(fixedRate = 5000)
    public void scheduledSoundStoryAuditTask() {
        logger.info("scheduled - scheduledSoundStoryAuditTask");
        storyService.auditStory();
    }
}
