package utils_soft.common.DatabaseResurce;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.templates.StatsSet;
import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.logging.Logger;

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
            result =  (R) String.valueOf(STAT_SET.getString(key, fieldAnnotation.defValue().STRING()));
        } else if (type == Integer.class){
            result = (R) Integer.valueOf(STAT_SET.getInteger(key, fieldAnnotation.defValue().INT()));
        } else if (type == Double.class) {
            result = (R) Double.valueOf(STAT_SET.getDouble(key, fieldAnnotation.defValue().DOUBLE()));
        } else if (type == Long.class) {
            result = (R) Long.valueOf(STAT_SET.getLong(key, fieldAnnotation.defValue().LONG()));
        } else if (type == Boolean.class) {
            result = (R) Boolean.valueOf(STAT_SET.getBool(key, fieldAnnotation.defValue().BOOLEAN()));
        }
        return result;
    }

    protected void set(String field, Object value){
        getSTAT_SET().set(field, value);

    }





//    private void update(String stat, String value){
//        try {
//            final String query = String.format(UPDATE_QUERY, TABLE_NAME, stat);
//            con = L2DatabaseFactory.getInstance().getConnection();
//            statement = con.prepareStatement(query);
//            statement.setString(1, value);
//            statement.execute();
//        } catch (Exception e1) {
//            _log.warning("npc data couldn't be stored in db, query is :" + UPDATE_QUERY + " : " + e1);
//        } finally {
//            DatabaseUtils.closeDatabaseCS(con, statement);
//        }
//    }

//
//    public void delete(){
//        try {
//            final String query = String.format(DELETE_QUERY, TABLE_NAME, FILTER);
//            con = L2DatabaseFactory.getInstance().getConnection();
//            statement = con.prepareStatement(query);
//            statement.execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            DatabaseUtils.closeDatabaseCS(con, statement);
//        }
//    }

    protected String getStat(String key, String defValue){
        return getSTAT_SET().getString(key, defValue);
    }
    protected int getStat(String key, int defValue){
        return getSTAT_SET().getInteger(key, defValue);
    }
    protected double getStat(String key, double defValue){
        return getSTAT_SET().getDouble(key, defValue);
    }
    protected long getStat(String key, long defValue){
        return getSTAT_SET().getLong(key, defValue);
    }
    protected boolean getStat(String key, boolean defValue){
        return getSTAT_SET().getBool(key, defValue);
    }
    protected <T extends Enum<T>> T getStat(String key,Class<T> enumClass, T defValue){
        return getSTAT_SET().getEnum(key, enumClass , defValue);
    }

    protected void setStat(String key, String value){
        set(key, value);
    }
    protected void setStat(String key, int value){
        set(key, String.valueOf(value));
    }
    protected void setStat(String key, double value){
        set(key, String.valueOf(value));
    }
    protected void setStat(String key, long value){
        set(key, String.valueOf(value));
    }
    protected void setStat(String key, boolean value){
        set(key, String.valueOf(value));
    }
    protected void setStat(String key, Enum<?> value){
        set(key, String.valueOf(value));
    }

    private StatsSet getSTAT_SET() {
        return STAT_SET;
    }

}
