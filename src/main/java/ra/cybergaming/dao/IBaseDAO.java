package ra.cybergaming.dao;

import java.util.List;

public interface IBaseDAO<T> {
    boolean create(T entity);
    T findById(int id);
    List<T> findAll();
    boolean update(T entity);
    boolean delete(int id);
    List<T> search(String keyword);
}
