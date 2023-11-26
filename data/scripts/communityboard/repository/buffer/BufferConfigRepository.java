package communityboard.repository.buffer;

import communityboard.config.BufferConfig;
import communityboard.models.buffer.Buff;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.instancemanager.ServerVariables;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by a.kiperku
 * Date: 19.10.2023
 */

public class BufferConfigRepository {

    private ThreadConnection con = null;
    private FiltredPreparedStatement statement = null;

    public BufferConfig getConfig() {
        BufferConfig bufferConfig = BufferConfig.getInstance();
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM community_perform_buffs_config";
            rs = con.prepareStatement(stmt).executeQuery();

            while (rs.next()) {
                final String name = rs.getString("name");
                final int value = rs.getInt("value");
                switch (name){
                    case "defaultSimpleBuffPrice" : {bufferConfig.setDefaultSimpleBuffPrice(value);break;}
                    case "defaultSimpleBuffItem" : {bufferConfig.setDefaultSimpleBuffItem(value);break;}
                    case "defaultPremiumBuffPrice" : {bufferConfig.setDefaultPremiumBuffPrice(value);break;}
                    case "defaultPremiumBuffItem" : {bufferConfig.setDefaultPremiumBuffItem(value);break;}
                    case "buffLimit" : {bufferConfig.setBuffLimit(value);break;}
                    case "songLimit" : {bufferConfig.setSongLimit(value);break;}
                    case "minLevel" : {bufferConfig.setMinLevel(value);break;}
                    case "maxLevel" : {bufferConfig.setMaxLevel(value);break;}
                    case "buffTime" : {bufferConfig.setBuffTime(value);break;}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return bufferConfig;
    }

    public BufferConfig updateConfig(String configName, int value){
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "UPDATE community_perform_buffs_config SET value = ? WHERE name=?";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, value);
            statement.setString(2, configName);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return getConfig();
    }


}
