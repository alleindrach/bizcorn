package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.facade.IStory;
import allein.bizcorn.model.mongo.SlideStory;
import allein.bizcorn.model.mongo.Story;
import allein.bizcorn.model.mongo.User;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class StoryDAOImpl extends   BaseDAOImpl<Story> implements StoryDAO  {

//
//    @Override
//    public List<SlideStory> selectByUid(String uid) {
//        return this.find(new Query(where("author.id").is(uid)));
//    }

    @Override
    public  Boolean isSotryIncludeFileExists(String fid) {
        Query query= Query.query(Criteria.where("scenes").elemMatch(new Criteria().orOperator(  Criteria.where("imageSource").is(fid), Criteria.where("soundSource").is(fid))));
        Story story= this.findOne(query);
        if(story==null)
            return false;
        return true;
    }

}
