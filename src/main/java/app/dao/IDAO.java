package app.dao;

import java.util.List;
import java.util.Optional;

public interface IDAO <T, ID> {
    T create(T entity);
    Optional<T> getById(ID id);
    List<T> getAll();
    T update(T entity);
    void delete(ID id);
}
