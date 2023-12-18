package utils_soft.common.DatabaseResurce;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.templates.StatsSet;
import org.apache.commons.lang.ArrayUtils;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by a.kiperku
 * Date: 15.12.2023
 */

public abstract class DataBaseTable <T>{
    protected static Logger _log = Logger.getLogger(DataBaseTable.class.getName());
    protected StatsSet STAT_SET;
    protected ThreadConnection con = null;
    protected FiltredPreparedStatement statement = null;
    protected Class<T> _class;
    protected ResourceProvider<?> RESOURCE_PROVIDER;
    protected T entity;


    public DataBaseTable(){
    }

    public DataBaseTable(Class<T> tClass) {
        _class = tClass;
    }

    protected <R> R get(String field, Class<R> type){
        R result = null;

        final Table tableAnnotation = _class.getAnnotation(Table.class);
        final Field[] fields = tableAnnotation.fields();

        final Field fieldAnnotation = Arrays.stream(fields)
                .filter(annotation -> annotation.name().equals(field))
                .findFirst().orElse(null);
        if (fieldAnnotation == null){
            throw new NullPointerException("Field " + field + " not present in " + _class.getName());
        }
        final String key = fieldAnnotation.name();

        if (type == String.class){
            result =  (R) String.valueOf(STAT_SET.getString(key, fieldAnnotation.defValue().String()));
        } else if (type == Integer.class){
            result = (R) Integer.valueOf(STAT_SET.getInteger(key, fieldAnnotation.defValue().Integer()));
        } else if (type == Double.class) {
            result = (R) Double.valueOf(STAT_SET.getDouble(key, fieldAnnotation.defValue().Double()));
        } else if (type == Long.class) {
            result = (R) Long.valueOf(STAT_SET.getLong(key, fieldAnnotation.defValue().Long()));
        } else if (type == Boolean.class) {
            result = (R) Boolean.valueOf(STAT_SET.getBool(key, fieldAnnotation.defValue().Boolean()));
        }
        return result;
    }

    protected void set(String field, Object value){
        update(field, value);
        getSTAT_SET().set(field, value);
    }

    private Filter FILTER(String field){
        final Table annotation = _class.getAnnotation(Table.class);
        final Filter filter = new Filter();
        final List<String> primary_key = Arrays.stream(annotation.primary_key()).collect(Collectors.toList());

        for (int i = 0; i < primary_key.size(); i++) {
            if (i == 0) {
                filter.WHERE(primary_key.get(i), getSTAT_SET().getObject(primary_key.get(i)));
            } else {
                filter.AND(primary_key.get(i), getSTAT_SET().getObject(primary_key.get(i)));
            }
        }
        return filter.build().isEmpty() ? new Filter().WHERE(field, getSTAT_SET().getObject(field)) : filter;
    }
    private Filter FILTER() throws Exception {
        final Table annotation = _class.getAnnotation(Table.class);
        if (ArrayUtils.isEmpty(annotation.primary_key())){
            throw new Exception("Scheme " + annotation.name() + " not available primaryKey");
        }
        final Filter filter = new Filter();
        final List<String> primary_key = Arrays.stream(annotation.primary_key()).collect(Collectors.toList());
        for (int i = 0; i < primary_key.size(); i++) {
            if (i == 0) {
                filter.WHERE(primary_key.get(i), getSTAT_SET().getObject(primary_key.get(i)));
            } else {
                filter.AND(primary_key.get(i), getSTAT_SET().getObject(primary_key.get(i)));
            }
        }
        if (filter.build().isEmpty()){
            throw new Exception("Warring! " + annotation.name() + " | empty filter");
        }

        return filter;
    }


    public void update(String field, Object value){
        try {
            final String query = String.format(RESOURCE_PROVIDER.getUPDATE_QUERY(), RESOURCE_PROVIDER.getTABLE_NAME(), field, value, FILTER(field).build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.execute();
        } catch (Exception ignored) {
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public void delete(){
        try {
            final String query = String.format(RESOURCE_PROVIDER.getDELETE_QUERY(), RESOURCE_PROVIDER.getTABLE_NAME(), FILTER().build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.execute();
        } catch (Exception ignored) {
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }
    public StatsSet getSTAT_SET() {
        return STAT_SET;
    }

}
