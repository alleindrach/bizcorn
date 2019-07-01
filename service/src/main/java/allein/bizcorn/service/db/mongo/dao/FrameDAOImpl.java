/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.mongo.Frame;
import allein.bizcorn.model.mongo.Story;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class FrameDAOImpl extends   BaseDAOImpl<Frame> implements FrameDAO  {

//
//    @Override
//    public List<SlideStory> selectByUid(String uid) {
//        return this.find(new Query(where("author.id").is(uid)));
//    }


}
