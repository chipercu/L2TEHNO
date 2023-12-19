package utils_soft.common.DatabaseResurce;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.templates.StatsSet;
import org.apache.commons.lang.ArrayUtils;
import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    protected ResourceProvider<?, ?> RESOURCE_PROVIDER;
    protected T entity;


    public DataBaseTable(){
    }

    public DataBaseTable(Class<T> resource)  {
        _class = resource;
        STAT_SET = new StatsSet();
    }

    protected <R> R get(String field){
        R result = null;

        final Table tableAnnotation = _class.getAnnotation(Table.class);
        final Column[] columns = tableAnnotation.fields();

        final Column columnAnnotation = Arrays.stream(columns)
                .filter(annotation -> annotation.name().equals(field))
                .findFirst().orElse(null);
        if (columnAnnotation == null){
            throw new NullPointerException("Field " + field + " not present in " + _class.getName());
        }
        final String key = columnAnnotation.name();
        final Class<?> type = columnAnnotation.type().getType();
        final String defValue = columnAnnotation.defValue();

        if (type.equals(Integer.class)) {
            result =  (R) Integer.valueOf(STAT_SET.getInteger(key, Integer.parseInt(defValue)));
        } else if (type.equals(Double.class)) {
            result =  (R) Double.valueOf(STAT_SET.getDouble(key, Double.parseDouble(defValue)));
        } else if (type.equals(Long.class)) {
            result =  (R) Long.valueOf(STAT_SET.getLong(key, Long.parseLong(defValue)));
        } else if (type.equals(Boolean.class)) {
            result =  (R) Boolean.valueOf(STAT_SET.getBool(key, Boolean.parseBoolean(defValue)));
        } else if (type.equals(String.class)) {
            result = (R) STAT_SET.getString(key, defValue);
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
            getSTAT_SET().getObject(field);
            final String query = String.format(RESOURCE_PROVIDER.getUPDATE_QUERY(), RESOURCE_PROVIDER.getTABLE_NAME(), field, value, FILTER(field).build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.execute();
            _log.info(this.getClass().getSimpleName() + " -> Update scheme " + _class.getSimpleName() + " : " + getSTAT_SET().getObject(field) + " -> " + value);
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
            _log.info(this.getClass().getSimpleName() + " -> Delete " + _class.getSimpleName() + " : " + FILTER().build());
        } catch (Exception ignored) {
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }
    public StatsSet getSTAT_SET() {
        return STAT_SET;
    }

    @Override
    public String toString() {
        final Table annotation = _class.getAnnotation(Table.class);
        List<String> values = new ArrayList<>();
        for (Column column: annotation.fields()){
            values.add(column.name()+ "=" + STAT_SET.getObject(column.name()));
        }
        final String join = String.join(", ", values);

        final String collect = STAT_SET.getSet().keySet().stream().map(e -> e + "=" + STAT_SET.getSet().get(e)).collect(Collectors.joining(", "));
        return _class.getSimpleName() + "{" + join + "}";
    }
}
