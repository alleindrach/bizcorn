package allein.bizcorn.service.db.mongo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class BaseDAOImpl<T extends Serializable> implements  BaseDAO<T>{

    @Autowired
    @Qualifier("mongoTemplate")
    protected MongoTemplate mongoTemplate;




    @Override
    public T save(T bean) {
        mongoTemplate.save(bean);
        return bean;
    }

    @Override
    public void deleteById(T t) {
        mongoTemplate.remove(t);
    }

    @Override
    public void deleteByCondition(T t) {
        Query query = buildBaseQuery(t);
        mongoTemplate.remove(query, getEntityClass());
    }

    @Override
    public void update(Query query, Update update) {
        mongoTemplate.updateMulti(query, update, this.getEntityClass());
    }

    @Override
    public void updateById(String id, T t) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        Update update = buildBaseUpdate(t);
        update(query, update);
    }


    @Override
    public List find(Query query) {
        return mongoTemplate.find(query, this.getEntityClass());
    }

    @Override
    public List<T> findByCondition(T t) {
        Query query = buildBaseQuery(t);
        return mongoTemplate.find(query, getEntityClass());
    }

    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, this.getEntityClass());
    }

    @Override
    public T get(String id) {
        return mongoTemplate.findById(id, this.getEntityClass());
    }

    @Override
    public T get(String id, String collectionName) {
        return mongoTemplate.findById(id, this.getEntityClass(), collectionName);
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    // 根据 vo 构建查询条件 Query
    private Query buildBaseQuery(T t) {
        Query query = new Query();
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(t);
                if (value != null) {
                    org.springframework.data.mongodb.core.mapping.Field queryField = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
                    if (queryField != null) {
                        query.addCriteria(Criteria.where(queryField.value()).is(value));
                    }else{
                        query.addCriteria(Criteria.where(field.getName()).is(value));
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return query;
    }
    private Update buildBaseUpdate(T t) {
        Update update = new Update();

        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(t);
                if (value != null) {
                    update.set(field.getName(), value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return update;
    }
    // 获取需要操作的实体类 class
    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass() {
        return ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
