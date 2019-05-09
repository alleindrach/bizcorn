/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.mongo.SoundChannel;


public interface SoundChannelDAO extends  BaseDAO<SoundChannel> {
    SoundChannel selectByIndex(Integer index);
    Boolean isChannelIncludeFileExists(String fid);
}
