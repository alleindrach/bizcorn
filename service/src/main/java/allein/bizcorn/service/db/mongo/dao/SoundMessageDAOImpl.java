/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.mongo.SoundChannel;
import allein.bizcorn.model.mongo.SoundMessage;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class SoundMessageDAOImpl extends   BaseDAOImpl<SoundMessage> implements SoundMessageDAO  {

}
