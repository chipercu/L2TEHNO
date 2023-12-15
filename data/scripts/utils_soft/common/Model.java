package utils_soft.common;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.templates.StatsSet;
import utils_soft.NpcEditor.models.NpcElementModel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by a.kiperku
 * Date: 15.12.2023
 */

public abstract class Model {
    private String GET_QUERY = "SELECT * FROM npc_element WHERE id = ?";

    private String UPDATE_QUERY = "";
    private String DELETE_QUERY = "";

    protected final StatsSet statsSet;
    protected final String TABLE_NAME;
    protected final String INDEX;

    protected ThreadConnection con = null;
    protected FiltredPreparedStatement statement = null;

    public Model(String table, String INDEX) {
        this.TABLE_NAME = table;
        this.INDEX = INDEX;
        this.statsSet = new StatsSet(getResultSet(INDEX));
    }

    private ResultSet getResultSet(String findIndex){
        ResultSet rs = null;
        String query = "SELECT * FROM ? WHERE id = ?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, TABLE_NAME);
            statement.setString(1, findIndex);
            rs = statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return rs;
    }




    public StatsSet getStatsSet() {
        return statsSet;
    }


    protected <T> T getStat(String key, T defValue){
        final Object object = getStatsSet().getObject(key);
        return object != null ? (T) getStatsSet().getObject(key) : defValue;
    }

    public void setStat(String key, String value){
        getStatsSet().set(key, value);
    }
    public void setStat(String key, int value){
        getStatsSet().set(key, value);
    }
    public void setStat(String key, double value){
        getStatsSet().set(key, value);
    }
    public void setStat(String key, long value){
        getStatsSet().set(key, value);
    }
    public void setStat(String key, boolean value){
        getStatsSet().set(key, value);
    }

}
