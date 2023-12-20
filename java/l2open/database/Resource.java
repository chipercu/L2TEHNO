package l2open.database;

import l2open.database.exceptions.ResourceProvideException;

import java.util.List;

public interface Resource<T extends DataBaseTable<T>, B extends ResourceBuilder<T>> {

    T find(Filter filter);

    List<T> findAll();

    List<T> findList(Filter filter);

    T update(T entity, String field, Object value) throws NoSuchFieldException, IllegalAccessException;

    T create(B builder, boolean... log) throws ResourceProvideException;
}
