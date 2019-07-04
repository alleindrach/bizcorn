/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.mongo.Complaint;
import allein.bizcorn.model.mongo.Frame;
import org.springframework.stereotype.Component;

@Component
public class ComplaintDAOImpl extends   BaseDAOImpl<Complaint> implements ComplaintDAO  {

//
//    @Override
//    public List<SlideStory> selectByUid(String uid) {
//        return this.find(new Query(where("author.id").is(uid)));
//    }


}
