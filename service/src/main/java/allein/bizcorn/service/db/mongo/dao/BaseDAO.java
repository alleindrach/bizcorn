package allein.bizcorn.service.db.mongo.dao;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.util.List;

public interface BaseDAO<T > {
    // 保存一个对象到 mongodb
    public T save(T bean);

    // 根据 id 删除对象
    public void deleteById(T t);

    // 根据对象的属性删除
    public void deleteByCondition(T t);

    public void deleteByQuery(Query q);
    // 通过条件查询更新数据
    public void update(Query query, Update update);

    // 根据 id 进行更新
    public void updateById(String id, T t);

    // 通过条件查询实体 (集合)
    public List<T> find(Query query);

    public List<T> findByCondition(T t);

    // 通过一定的条件查询一个实体
    public T findOne(Query query);

    // 通过 ID 获取记录
    public T get(String id);

    // 通过 ID 获取记录, 并且指定了集合名 (表的意思)
    public T get(String id, String collectionName);

    public MongoTemplate getMongoTemplate();
}
