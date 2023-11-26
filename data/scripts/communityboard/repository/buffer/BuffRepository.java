package communityboard.repository.buffer;

import communityboard.models.buffer.Buff;
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
                final Buff buff = new Buff(
                        rs.getInt("id"),
                        rs.getInt("skill_id"),
                        rs.getInt("skill_level"),
                        rs.getInt("display_level"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getInt("price_item"),
                        rs.getInt("minLevel"),
                        rs.getInt("maxLevel"),
                        rs.getString("icon"),
                        rs.getString("type_")
                );
                buffModels.add(buff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return buffModels;
    }

    public Optional<Buff> getBuff(int skill_id, int skill_level, String type) {
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM community_perform_buffs WHERE skill_id=? AND skill_level=? AND type_=?";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, skill_id);
            statement.setInt(2, skill_level);
            statement.setString(3, type);
            rs = statement.executeQuery();
            if (rs.next()) {
                Buff buffModel = new Buff(
                        rs.getInt("id"),
                        rs.getInt("skill_id"),
                        rs.getInt("skill_level"),
                        rs.getInt("display_level"),
                        rs.getString("name"),
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


    public Buff createBuff(Buff buff) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "INSERT INTO community_perform_buffs (skill_id,skill_level,display_level,name,price,price_item,minLevel,maxLevel,icon,type_) VALUES(?,?,?,?,?,?,?,?,?,?)";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, buff.getSkill_id());
            statement.setInt(2, buff.getSkill_level());
            statement.setInt(3, buff.getDisplay_level());
            statement.setString(4, buff.getName());
            statement.setInt(5, buff.getPrice());
            statement.setInt(6, buff.getPrice_item());
            statement.setInt(7, buff.getMinLevel());
            statement.setInt(8, buff.getMaxLevel());
            statement.setString(9, buff.getIcon());
            statement.setString(10, buff.getType());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return getBuff(buff.getSkill_id(), buff.getSkill_level(), buff.getType()).orElse(null);
    }

    public Buff updateBuff(Buff buffModel) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "UPDATE community_perform_buffs SET skill_id = ?, skill_level = ?, display_level = ?,name = ?, price = ?, price_item = ?, minLevel = ?,maxLevel = ?,icon = ?,type_ = ? WHERE id=? AND type_=?";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, buffModel.getSkill_id());
            statement.setInt(2, buffModel.getSkill_level());
            statement.setInt(3, buffModel.getDisplay_level());
            statement.setString(4, buffModel.getName());
            statement.setInt(5, buffModel.getPrice());
            statement.setInt(6, buffModel.getPrice_item());
            statement.setInt(7, buffModel.getMinLevel());
            statement.setInt(8, buffModel.getMaxLevel());
            statement.setString(9, buffModel.getIcon());
            statement.setString(10, buffModel.getType());

            statement.setLong(11, buffModel.getId());
            statement.setString(12, buffModel.getType());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return getBuff(buffModel.getSkill_id(), buffModel.getSkill_level(), buffModel.getType()).orElse(null);

    }

    public void removeBuff(int buffId, String type) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM community_perform_buffs WHERE id=? AND type_=?");
            statement.setInt(1, buffId);
            statement.setString(2, type);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeConnection(con);
        }
    }

}
