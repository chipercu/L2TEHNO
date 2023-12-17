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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceProvider<T extends DataBaseTable> implements Resource<T> {

    private String GET_QUERY = "SELECT * FROM %s %s";
    private String UPDATE_QUERY = "UPDATE %s SET %s = %s %s";
    private String DELETE_QUERY = "DELETE FROM %s %s";

    private final Field fieldStatSet;
    private final Field resource_provider;
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
            resource_provider = _class.getSuperclass().getDeclaredField("RESOURCE_PROVIDER");
            fieldStatSet.setAccessible(true);
            resource_provider.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public String getGET_QUERY() {
        return GET_QUERY;
    }

    public String getUPDATE_QUERY() {
        return UPDATE_QUERY;
    }

    public String getDELETE_QUERY() {
        return DELETE_QUERY;
    }

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public T update(T entity, String field, Object value) throws NoSuchFieldException, IllegalAccessException {

        final Table annotation = _class.getAnnotation(Table.class);
        final Filter filter = new Filter();
        final List<String> primary_key = Arrays.stream(annotation.primary_key()).collect(Collectors.toList());

        for (int i = 0; i < primary_key.size(); i++) {
            if (i == 0) {
                filter.WHERE(primary_key.get(i), entity.getSTAT_SET().getObject(primary_key.get(i)));
            } else {
                filter.AND(primary_key.get(i), entity.getSTAT_SET().getObject(primary_key.get(i)));
            }
        }

        try {
            final String query = String.format(UPDATE_QUERY, TABLE_NAME, field, value, filter.build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.execute();
        } catch (Exception ignored) {
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }

        return find(filter);
    }

    @Override
    public T create() {
        T instance = null;
        try {
            instance = _class.newInstance();
            fieldStatSet.set(instance, new StatsSet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }


    public List<T> findAll() {
        List<T> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            final String query = String.format(GET_QUERY, TABLE_NAME, "");
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                T instance = _class.newInstance();
                fieldStatSet.set(instance, new StatsSet(rs));
                resource_provider.set(instance, this);
                list.add(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public List<T> findList(Filter filter) {
        List<T> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            final String query = String.format(GET_QUERY, TABLE_NAME, filter.build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                T instance = _class.newInstance();
                fieldStatSet.set(instance, new StatsSet(rs));
                resource_provider.set(instance, this);
                list.add(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public T find(Filter filter) {
        T instance = null;
        ResultSet rs = null;
        try {
            final String query = String.format(GET_QUERY, TABLE_NAME, filter.build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()) {
                instance = _class.newInstance();
                fieldStatSet.set(instance, new StatsSet(rs));
                resource_provider.set(instance, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return instance;
    }

    private StatsSet getStatSet(Filter filter) {
        ResultSet rs = null;
        StatsSet statsSet = new StatsSet();
        try {
            final String query = String.format(GET_QUERY, TABLE_NAME, filter.build());
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()) {
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
