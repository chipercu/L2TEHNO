package utils_soft.common.DatabaseResurce;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.templates.StatsSet;
import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.schemes.ResourceBuilder;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by a.kiperku
 * Date: 15.12.2023
 */

public class ResourceProvider<T extends DataBaseTable<T>, B extends ResourceBuilder<T>> implements Resource<T, B> {

    private String GET_QUERY = "SELECT * FROM %s %s";
    private String UPDATE_QUERY = "UPDATE %s SET %s = %s %s";
    private String DELETE_QUERY = "DELETE FROM %s %s";
    private String INSERT_QUERY = "INSERT INTO  %s (%s) VALUES (%s)";

    private final Field fieldStatSet;
    private final Field resource_provider;
    private final String TABLE_NAME;
    private final Class<T> resource;
    protected ThreadConnection con = null;
    protected FiltredPreparedStatement statement = null;

    public ResourceProvider(Class<T> resource) {
        this.resource = resource;
        try {
            final Table annotation = resource.getAnnotation(Table.class);
            TABLE_NAME = annotation.name();
            fieldStatSet = resource.getSuperclass().getDeclaredField("STAT_SET");
            resource_provider = resource.getSuperclass().getDeclaredField("RESOURCE_PROVIDER");
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

    public String getINSERT_QUERY() {
        return INSERT_QUERY;
    }

    public T update(T entity, String field, Object value) throws NoSuchFieldException, IllegalAccessException {

        final Table annotation = resource.getAnnotation(Table.class);
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

    public T create(B builder)  {
        T build = null;
        try {
            build = builder.build();
            build.RESOURCE_PROVIDER = this;
            final Table annotation = resource.getAnnotation(Table.class);
            final String columns = Arrays.stream(annotation.fields()).map(Column::name).collect(Collectors.joining(","));

            List<String> values = new ArrayList<>();
            for (Column column: annotation.fields()){
               values.add("'" + build.STAT_SET.getObject(column.name()) + "'");
            }
            final String join = String.join(", ", values);

            final String query = String.format(INSERT_QUERY, TABLE_NAME, columns, join);
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.execute();
        } catch (Exception ignored) {
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return build;
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
                T instance = resource.newInstance();
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
                T instance = resource.newInstance();
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
                instance = resource.newInstance();
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
}
