package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.facade.IBundle;
import allein.bizcorn.model.mongo.Bundle;

import java.util.List;


public interface StoryDAO extends  BaseDAO<Bundle> {

    List<IBundle> selectByUid(String uid);

}
