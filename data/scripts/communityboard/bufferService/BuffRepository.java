package communityboard.bufferService;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;

import java.sql.ResultSet;
import java.util.*;

/**
 * Created by a.kiperku
 * Date: 19.10.2023
 */

public class BuffRepository {

    private ThreadConnection con = null;
    private FiltredPreparedStatement statement = null;

    public List<Buff> getAllBuffs() {
        final ArrayList<Buff> buffModels = new ArrayList<>();
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM community_perform_buffs";
            rs = con.prepareStatement(stmt).executeQuery();
            while (rs.next()) {
                final Buff buffModel = new Buff(
                        rs.getInt("id"),
                        rs.getInt("display_level"),
                        rs.getString("name"),
                        rs.getLong("duration"),
                        rs.getInt("price"),
                        rs.getInt("price_item"),
                        rs.getInt("minLevel"),
                        rs.getInt("maxLevel"),
                        rs.getString("icon"),
                        rs.getString("type_")

                );
                buffModels.add(buffModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return buffModels;
    }

    public Optional<Buff> getBuff(long id) {
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM community_perform_buffs WHERE id=?";
            statement = con.prepareStatement(stmt);
            statement.setLong(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                Buff buffModel = new Buff(
                        rs.getInt("id"),
                        rs.getInt("display_level"),
                        rs.getString("name"),
                        rs.getLong("duration"),
                        rs.getInt("price"),
                        rs.getInt("price_item"),
                        rs.getInt("minLevel"),
                        rs.getInt("maxLevel"),
                        rs.getString("icon"),
                        rs.getString("type_")
                );
                return Optional.of(buffModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return Optional.empty();
    }

    public Optional<Buff> createBuff(Buff buffModel) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "INSERT INTO community_perform_buffs (id,display_level,name,duration,price,price_item,minLevel,maxLevel,icon,type_) VALUES(?,?,?,?,?,?,?,?,?,?)";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, buffModel.getId());
            statement.setInt(2, buffModel.getDisplay_level());
            statement.setString(3, buffModel.getName());
            statement.setLong(4, buffModel.getDuration());
            statement.setInt(5, buffModel.getPrice());
            statement.setInt(6, buffModel.getPrice_item());
            statement.setInt(7, buffModel.getMinLevel());
            statement.setInt(8, buffModel.getMaxLevel());
            statement.setString(9, buffModel.getIcon());
            statement.setString(10, buffModel.getType());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return Optional.of(buffModel);
    }
    public void updateBuff(Buff buffModel, Buff newBuffModel) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "UPDATE community_perform_buffs SET display_level = ?,name = ?, duration = ?, price = ?, price_item = ?, minLevel = ?,maxLevel = ?,icon = ?,type_ = ? WHERE id=? AND type_=?";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, newBuffModel.getDisplay_level());
            statement.setString(2, newBuffModel.getName());
            statement.setLong(3, newBuffModel.getDuration());
            statement.setInt(4, newBuffModel.getPrice());
            statement.setInt(5, newBuffModel.getPrice_item());
            statement.setInt(6, newBuffModel.getMinLevel());
            statement.setInt(7, newBuffModel.getMaxLevel());
            statement.setString(8, newBuffModel.getIcon());
            statement.setString(9, newBuffModel.getType());

            statement.setInt(10, buffModel.getId());
            statement.setString(11, newBuffModel.getType());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public void removeBuff(Buff buffModel) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM community_perform_buffs WHERE id=? AND type_=?");
            statement.setInt(1, buffModel.getId());
            statement.setString(2, buffModel.getType());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeConnection(con);
        }
    }

}
