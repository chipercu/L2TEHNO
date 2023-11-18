package communityboard.bufferService;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by a.kiperku
 * Date: 19.10.2023
 */

public class SchemeRepository {

    private static final String SCHEME_BUFF_TABLE = "community_perform_buffs_schemebuff";
    private static final String SCHEME_TABLE = "community_perform_buffs_scheme";

    private ThreadConnection con = null;
    private FiltredPreparedStatement statement = null;

    public List<Scheme> getAllScheme() {
        final ArrayList<Scheme> schemes = new ArrayList<>();
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM " + SCHEME_TABLE;
            rs = con.prepareStatement(stmt).executeQuery();
            while (rs.next()) {
                final Scheme scheme = new Scheme(
                        rs.getLong("id"),
                        rs.getLong("owner"),
                        rs.getString("name")
                );

                schemes.add(scheme);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return schemes;
    }

    public Optional<Scheme> byId(int id) {
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM " + SCHEME_TABLE +" WHERE id=?";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                Scheme scheme = new Scheme(
                        rs.getLong("id"),
                        rs.getLong("owner"),
                        rs.getString("name")
                );
                return Optional.of(scheme);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return Optional.empty();
    }

    public Optional<Scheme> byOwnerName(long owner, String name) {
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM " + SCHEME_TABLE +" WHERE owner=? AND name=?";
            statement = con.prepareStatement(stmt);
            statement.setLong(1, owner);
            statement.setString(2, name);
            rs = statement.executeQuery();
            if (rs.next()) {
                Scheme scheme = new Scheme(
                        rs.getLong("id"),
                        rs.getLong("owner"),
                        rs.getString("name")
                );
                return Optional.of(scheme);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return Optional.empty();
    }

    public List<SchemeBuff> getBuffsIdsBySchemeId(long scheme_id) {
        ResultSet rs = null;
        List<SchemeBuff> buffs_ids = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM " + SCHEME_BUFF_TABLE +" WHERE scheme_id=?";
            statement = con.prepareStatement(stmt);
            statement.setLong(1, scheme_id);
            rs = statement.executeQuery();
            while (rs.next()) {
                final SchemeBuff schemeBuff = new SchemeBuff(
                        rs.getLong("scheme_id"),
                        rs.getLong("buff_id"),
                        rs.getInt("index_")
                );
                buffs_ids.add(schemeBuff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return buffs_ids;
    }

    public void addBuffInScheme(Scheme scheme, Buff buff){
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "INSERT INTO " + SCHEME_BUFF_TABLE + " (scheme_id,buff_id,index_) VALUES(?,?,?)";
            statement = con.prepareStatement(stmt);
            statement.setLong(1, scheme.getId());
            statement.setInt(2, buff.getId());
            statement.setInt(3, scheme.getBuffs().size());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public Optional<Scheme> createScheme(Scheme scheme) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "INSERT INTO " + SCHEME_TABLE + " (owner,name) VALUES(?,?)";
            statement = con.prepareStatement(stmt);
            statement.setLong(1, scheme.getOwner());
            statement.setString(2, scheme.getName());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return Optional.of(scheme);
    }

    public void removeScheme(Scheme scheme) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM " + SCHEME_TABLE + " WHERE id=?");
            statement.setLong(1, scheme.getId());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeConnection(con);
        }
    }

    public void removeBuffsInScheme(Scheme scheme, Buff buff) {


    }
}
