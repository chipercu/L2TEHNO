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

    public List<BuffModel> getAllBuffs() {
        final ArrayList<BuffModel> buffModels = new ArrayList<>();
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM community_perform_buffs";
            rs = con.prepareStatement(stmt).executeQuery();
            while (rs.next()) {
                final BuffModel buffModel = new BuffModel(
                        rs.getInt("buff_id"),
                        rs.getInt("buff_level"),
                        rs.getInt("display_level"),
                        rs.getString("name"),
                        rs.getString("enchant_name"),
                        rs.getLong("duration"),
                        rs.getInt("price"),
                        rs.getInt("price_item"),
                        rs.getString("icon"),
                        rs.getString("list_type")
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

    public List<BuffModel> getBuffsList(String list_type) {
        List<BuffModel> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM community_perform_buffs WHERE list_type=?";
            statement = con.prepareStatement(stmt);
            statement.setString(1, list_type);
            rs = statement.executeQuery();
            while (rs.next()) {
                final BuffModel buffModel = new BuffModel(
                        rs.getInt("buff_id"),
                        rs.getInt("buff_level"),
                        rs.getInt("display_level"),
                        rs.getString("name"),
                        rs.getString("enchant_name"),
                        rs.getLong("duration"),
                        rs.getInt("price"),
                        rs.getInt("price_item"),
                        rs.getString("icon"),
                        rs.getString("list_type")
                );
                list.add(buffModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public void removeBuffList(String list_type){
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM community_perform_buffs WHERE list_type=?");
            statement.setString(1, list_type);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeConnection(con);
        }
    }

    public Optional<BuffModel> getBuff(int buff_id, int buff_level, String list_type) {
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM community_perform_buffs WHERE buff_id=? AND buff_level=? AND list_type=?";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, buff_id);
            statement.setInt(2, buff_level);
            statement.setString(3, list_type);
            rs = statement.executeQuery();
            if (rs.next()) {
                BuffModel buffModel = new BuffModel(
                        rs.getInt("buff_id"),
                        rs.getInt("buff_level"),
                        rs.getInt("display_level"),
                        rs.getString("name"),
                        rs.getString("enchant_name"),
                        rs.getLong("duration"),
                        rs.getInt("price"),
                        rs.getInt("price_item"),
                        rs.getString("icon"),
                        rs.getString("list_type")
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

    public Optional<BuffModel> createBuff(BuffModel buffModel) {
        boolean execute = false;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "INSERT INTO community_perform_buffs (buff_id,buff_level,display_level,name,enchant_name,duration,price,price_item,icon,list_type) VALUES(?,?,?,?,?,?,?,?,?,?)";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, buffModel.getBuff_id());
            statement.setInt(2, buffModel.getBuff_level());
            statement.setInt(3, buffModel.getDisplay_level());
            statement.setString(4, buffModel.getName());
            statement.setString(5, buffModel.getEnchant_name());
            statement.setLong(6, buffModel.getDuration());
            statement.setInt(7, buffModel.getPrice());
            statement.setInt(8, buffModel.getPrice_item());
            statement.setString(9, buffModel.getIcon());
            statement.setString(10, buffModel.getList_type());
            execute = statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return execute ? Optional.of(buffModel) : Optional.empty();
    }
    public Optional<BuffModel> updateBuff(int buff_id, int buff_level, String list_type, BuffModel buffModel) {
        boolean execute = false;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "UPDATE community_perform_buffs SET buff_level = ?, display_level = ?, enchant_name = ?, duration = ?, price = ?, price_item = ? WHERE buff_id=? AND buff_level=? AND list_type=?";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, buffModel.getBuff_level());
            statement.setInt(2, buffModel.getDisplay_level());
            statement.setString(3, buffModel.getEnchant_name());
            statement.setLong(4, buffModel.getDuration());
            statement.setInt(5, buffModel.getPrice());
            statement.setInt(6, buffModel.getPrice_item());

            statement.setInt(7, buff_id);
            statement.setInt(8, buff_level);
            statement.setString(9, list_type);
            final int count = statement.executeUpdate();
            execute = count != 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }

        if (execute) {
            return Optional.of(buffModel);
        }
        return Optional.empty();
    }

    public boolean removeBuff(int buff_id, int buff_level, String list_type) {
        boolean execute = false;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM community_perform_buffs WHERE buff_id=? AND buff_level=? AND list_type=?");
            statement.setInt(1, buff_id);
            statement.setInt(2, buff_level);
            statement.setString(3, list_type);
            execute = statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeConnection(con);
        }
        return execute;
    }

}
