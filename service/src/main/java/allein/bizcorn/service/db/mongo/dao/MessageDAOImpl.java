/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.mongo.Message;
import allein.bizcorn.model.mongo.Story;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class MessageDAOImpl extends   BaseDAOImpl<Message> implements MessageDAO  {


    @Override
    public List<Message> selectUncopied(String uid,int page, int limit){
        List<Message> unCopiedMessages=this.mongoTemplate.find(Query.query(Criteria.where("status").lt(2)).with(Sort.by(DESC, "createDate")).limit(limit).skip(limit*page),Message.class);
        return  unCopiedMessages;
    }

    @Override
    public long getUncopiedCount(String uid) {
        return this.mongoTemplate.count(Query.query(Criteria.where("status").lt(2)),Message.class);
    }
}
