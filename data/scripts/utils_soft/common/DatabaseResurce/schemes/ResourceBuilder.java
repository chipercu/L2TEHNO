package utils_soft.common.DatabaseResurce.schemes;

import l2open.gameserver.templates.StatsSet;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;

import java.util.NoSuchElementException;

/**
 * Created by a.kiperku
 * Date: 19.12.2023
 */
public abstract class ResourceBuilder<T extends DataBaseTable<T>> {

    protected Class<T> resourceClass;
    protected T resource;
    protected final StatsSet statsSet = new StatsSet();

    public T build() throws InstantiationException, IllegalAccessException{
        final Table annotation = resourceClass.getAnnotation(Table.class);
        final Column[] columns = annotation.fields();

        for (Column column : columns) {
            final Object object = statsSet.getObject(column.name());
            if (object == null) {
                if (!column.is_null().isNullable() && (column.defValue().equals("null") || column.defValue().isEmpty())) {
                    throw new NoSuchElementException(resourceClass.getSimpleName() + "{ " + column.name() + " not defined in builder }");
                }
                resource.getSTAT_SET().set(column.name(), column.defValue());
            } else {
                resource.getSTAT_SET().set(column.name(), object);
            }
        }
        return resource;
    }

}
