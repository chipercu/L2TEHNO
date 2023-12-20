package communityboard.repository.buffer;

import communityboard.models.buffer.Buff;
import communityboard.models.buffer.Scheme;
import communityboard.models.buffer.SchemeBuff;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.database.Filter;
import l2open.database.Resource;
import l2open.database.ResourceProvider;
import l2open.database.schemes.builders.CommunityPerformBuffsSchemeBuilder;
import l2open.database.schemes.builders.CommunityPerformBuffsSchemebuffBuilder;
import l2open.database.schemes.resources.CommunityPerformBuffsSchemeResource;
import l2open.database.schemes.resources.CommunityPerformBuffsSchemebuffResource;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by a.kiperku
 * Date: 19.10.2023
 */

public class SchemeRepository {

    private static final String SCHEME_BUFF_TABLE = "community_perform_buffs_schemebuff";
    private static final String SCHEME_TABLE = "community_perform_buffs_scheme";

    private final Resource<CommunityPerformBuffsSchemeResource, CommunityPerformBuffsSchemeBuilder> schemeResource;
    private final Resource<CommunityPerformBuffsSchemebuffResource, CommunityPerformBuffsSchemebuffBuilder> schemeBuffResource;

    public SchemeRepository() {
        this.schemeResource = new ResourceProvider<>(CommunityPerformBuffsSchemeResource.class);
        this.schemeBuffResource = new ResourceProvider<>(CommunityPerformBuffsSchemebuffResource.class);
    }

    private ThreadConnection con = null;
    private FiltredPreparedStatement statement = null;

    public List<Scheme> getAllScheme() {
        final List<CommunityPerformBuffsSchemeResource> all = schemeResource.findAll();
        return all.stream()
                .map(scheme -> new Scheme(scheme.getId(), scheme.getOwner(), scheme.getName()))
                .collect(Collectors.toList());

//        final ArrayList<Scheme> schemes = new ArrayList<>();
//        ResultSet rs = null;
//        try {
//            con = L2DatabaseFactory.getInstance().getConnection();
//            String stmt = "SELECT * FROM " + SCHEME_TABLE;
//            rs = con.prepareStatement(stmt).executeQuery();
//            while (rs.next()) {
//                final Scheme scheme = new Scheme(
//                        rs.getInt("id"),
//                        rs.getInt("owner"),
//                        rs.getString("name")
//                );
//
//                schemes.add(scheme);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
//        }
//        return schemes;
    }

    public Optional<Scheme> createScheme(Scheme scheme) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "INSERT INTO " + SCHEME_TABLE + " (owner,name) VALUES(?,?)";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, scheme.getOwner());
            statement.setString(2, scheme.getName());
            statement.setVars();
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return getScheme(scheme.getOwner(), scheme.getName());
    }

    public Optional<Scheme> getScheme(int owner, String name) {

        final CommunityPerformBuffsSchemeResource buffsScheme = schemeResource.find(new Filter()
                .WHERE(CommunityPerformBuffsSchemeResource.OWNER, owner)
                .AND(CommunityPerformBuffsSchemeResource.NAME, name));

        return Optional.of(new Scheme(buffsScheme.getId(), buffsScheme.getOwner(), buffsScheme.getName()));

//        ResultSet rs = null;
//        try {
//            con = L2DatabaseFactory.getInstance().getConnection();
//            String stmt = "SELECT * FROM " + SCHEME_TABLE +" WHERE owner=? AND name=?";
//            statement = con.prepareStatement(stmt);
//            statement.setInt(1, owner);
//            statement.setString(2, name);
//            rs = statement.executeQuery();
//            if (rs.next()) {
//                Scheme scheme = new Scheme(
//                        rs.getInt("id"),
//                        rs.getInt("owner"),
//                        rs.getString("name")
//                );
//                return Optional.of(scheme);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
//        }
//        return Optional.empty();
    }

    public void removeScheme(int id) {
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM " + SCHEME_TABLE + " WHERE id=?");
            statement.setInt(1, id);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeConnection(con);
        }
    }

    public Optional<SchemeBuff> getSchemeBuff(int schemeId, int index){
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "SELECT * FROM " + SCHEME_BUFF_TABLE + " WHERE scheme_id=? AND index_=?";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, schemeId);
            statement.setInt(2, index);
            rs = statement.executeQuery();
            if (rs.next()) {
                SchemeBuff schemeBuff = new SchemeBuff(
                        rs.getInt("scheme_id"),
                        rs.getInt("buff_id"),
                        rs.getInt("index_")
                );
                return Optional.of(schemeBuff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return Optional.empty();
    }


    public List<SchemeBuff> getSchemeBuffs(long scheme_id) {
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
                        rs.getInt("scheme_id"),
                        rs.getInt("buff_id"),
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


    public SchemeBuff createSchemeBuff(Scheme scheme, Buff buff, int index){
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "INSERT INTO " + SCHEME_BUFF_TABLE + " (scheme_id,buff_id,index_) VALUES(?,?,?)";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, scheme.getId());
            statement.setInt(2, buff.getId());
            statement.setInt(3, index);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return getSchemeBuff(scheme.getId(), index).orElse(null);
    }

    public SchemeBuff updateSchemeBuff(Scheme scheme, Buff buff, int index){
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String stmt = "UPDATE " + SCHEME_BUFF_TABLE + " SET scheme_id=?, buff_id=?, index_=? WHERE scheme_id=? AND index_=?";
            statement = con.prepareStatement(stmt);
            statement.setInt(1, scheme.getId());
            statement.setInt(2, buff.getId());
            statement.setInt(3, index);

            statement.setInt(4, scheme.getId());
            statement.setInt(5, index);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return getSchemeBuff(scheme.getId(), index).orElse(null);
    }



    public void clearSchemeBuffs(int schemeId){
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM " + SCHEME_BUFF_TABLE + " WHERE scheme_id=?");
            statement.setInt(1, schemeId);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeConnection(con);
        }
    }

    public void removeSchemeBuff(int schemeId, int index) {
        schemeBuffResource.find(new Filter().WHERE(CommunityPerformBuffsSchemebuffResource.SCHEME_ID, schemeId)
                        .AND(CommunityPerformBuffsSchemebuffResource.INDEX_, index))
                .delete();

//        try {
//            con = L2DatabaseFactory.getInstance().getConnection();
//            statement = con.prepareStatement("DELETE FROM " + SCHEME_BUFF_TABLE + " WHERE scheme_id=? AND index_=?");
//            statement.setInt(1, schemeId);
//            statement.setInt(2, index);
//            statement.execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            DatabaseUtils.closeConnection(con);
//        }
    }
}
