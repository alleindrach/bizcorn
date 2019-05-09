/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.mongo.SoundChannel;
import allein.bizcorn.model.mongo.Story;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class SoundChannelDAOImpl extends   BaseDAOImpl<SoundChannel> implements SoundChannelDAO  {


    @Override
    public SoundChannel selectByIndex(Integer index) {
        return this.findOne(new Query(where("index").is(index)));
    }

    @Override
    public  Boolean isChannelIncludeFileExists(String fid) {
        Query query= Query.query(Criteria.where("bgPictureId").is(fid));
        SoundChannel soundChannel= this.findOne(query);
        if(soundChannel==null)
            return false;
        return true;
    }

}
