package utils_soft.common.DatabaseResurce;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.templates.StatsSet;
import utils_soft.common.DatabaseResurce.anotations.Table;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResourceProvider <T extends DataBaseTable> implements Resource<T> {

    private String GET_QUERY = "SELECT * FROM %s %s";
    private String UPDATE_QUERY = "UPDATE %s SET %s = ? WHERE %s = ?";
    private String DELETE_QUERY = "DELETE FROM %s %s";

    private final Field fieldStatSet;
    private final String TABLE_NAME;
    private Class<T> _class;
    protected ThreadConnection con = null;
    protected FiltredPreparedStatement statement = null;

    public ResourceProvider(Class<T> _class) {
        this._class = _class;
        try {
            final Table annotation = _class.getAnnotation(Table.class);
            TABLE_NAME = annotation.name();
            fieldStatSet = _class.getSuperclass().getDeclaredField("STAT_SET");
            fieldStatSet.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> findAll(){
        List<T> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            final String query = String.format(GET_QUERY, TABLE_NAME, "");
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()){
                T instance = _class.newInstance();
                fieldStatSet.set(instance, new StatsSet(rs));
                list.add(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public List<T> findList(Filter filter){
        List<T> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            final String query = String.format(GET_QUERY, TABLE_NAME, filter.build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()){
                T instance = _class.newInstance();
                fieldStatSet.set(instance, new StatsSet(rs));
                list.add(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public T find(Filter filter){
        T instance = null;
        ResultSet rs = null;
        try {
            final String query = String.format(GET_QUERY, TABLE_NAME, filter.build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()){
                instance = _class.newInstance();
                fieldStatSet.set(instance, new StatsSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return instance;
    }

    private StatsSet getStatSet(Filter filter){
        ResultSet rs = null;
        StatsSet statsSet = new StatsSet();
        try {
            final String query = String.format(GET_QUERY, TABLE_NAME, filter.build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()){
                statsSet = new StatsSet(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return statsSet;
    }


}
