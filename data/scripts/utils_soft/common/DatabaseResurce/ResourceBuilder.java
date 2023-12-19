package utils_soft.common.DatabaseResurce;

import l2open.gameserver.templates.StatsSet;
import utils_soft.NpcEditor.NpcEditorComponent;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.exceptions.ResourceProvideException;

import java.lang.reflect.Field;
import java.time.Year;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * Created by a.kiperku
 * Date: 19.12.2023
 */
public abstract class ResourceBuilder<T extends DataBaseTable<T>> {

    protected static Logger _log = Logger.getLogger(ResourceBuilder.class.getName());

    protected Class<T> resourceClass;
    protected T resource;
    protected final StatsSet statsSet = new StatsSet();

    protected void with(String fieldName, Object value){
        statsSet.set(fieldName, value);
    }

    public T build() throws InstantiationException, IllegalAccessException, ResourceProvideException {
        final Table annotation = resourceClass.getAnnotation(Table.class);
        final Column[] columns = annotation.fields();

        for (Column column : columns) {
            final Object object = statsSet.getObject(column.name());
            if (object == null || object.toString().isEmpty()) {
                final String defValue = column.defValue();
                if (column.is_null() == IS_NULLABLE.NO && (defValue.equalsIgnoreCase("null") || defValue.isEmpty()) || defValue.equalsIgnoreCase("null") || defValue.isEmpty()) {
                    _log.warning(resourceClass.getSimpleName() + "{column '" + column.name() + "' not defined in builder}");
                    throw new ResourceProvideException(resourceClass.getSimpleName() + "{column '" + column.name() + "' not defined in builder}");
                }
                resource.getSTAT_SET().set(column.name(), column.defValue());
            } else {
                resource.getSTAT_SET().set(column.name(), object);
            }
        }
        return resource;
    }

}
