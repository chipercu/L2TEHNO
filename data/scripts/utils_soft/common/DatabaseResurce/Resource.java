package utils_soft.common.DatabaseResurce;

import java.util.List;

public interface Resource<T extends DataBaseTable> {

    T find(Filter filter);

    List<T> findAll();

    List<T> findList(Filter filter);

    T update(T entity, String field, Object value) throws NoSuchFieldException, IllegalAccessException;

    T create(FiledSet... fieldsSet) throws InstantiationException, IllegalAccessException;
}
