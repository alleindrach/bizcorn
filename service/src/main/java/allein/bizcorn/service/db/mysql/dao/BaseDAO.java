package allein.bizcorn.service.db.mysql.dao;

public interface BaseDAO<T> {
    T selectById(int id);

    int insert(T entity);

    int update(T entity);

    int delete(int id);
}
