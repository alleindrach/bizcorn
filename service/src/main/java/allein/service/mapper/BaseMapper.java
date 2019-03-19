package allein.service.mapper;

public interface BaseMapper<T> {
    T selectById(int id);
    int insert(T entity);
    int update(T entity);
    int delete(int id);
}
