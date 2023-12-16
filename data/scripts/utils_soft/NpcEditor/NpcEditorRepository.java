package utils_soft.NpcEditor;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import utils_soft.common.DatabaseResurce.schemes.DropListTable;
import utils_soft.common.DatabaseResurce.schemes.NpcElementTable;
import utils_soft.common.DatabaseResurce.schemes.NpcModel;
import utils_soft.common.DatabaseResurce.schemes.SpawnListTable;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NpcEditorRepository {
    protected static Logger _log = Logger.getLogger(NpcEditorRepository.class.getName());



    public static void updateNpcStat(NpcModel npcModel, String stat){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "UPDATE npc SET " + stat + " = " + npcModel.getRace() + " WHERE id = ?";

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npcModel.getId());
            statement.execute();
        } catch (Exception e1) {
            _log.warning("npc data couldnt be stored in db, query is :" + query + " : " + e1);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }


    public static NpcElementTable getNpcElement(int npcId){
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "SELECT * FROM npc_element WHERE id = ?";
        NpcElementTable npcElementModel = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npcId);
            rs = statement.executeQuery();
            if (rs.next()) {
                npcElementModel = new NpcElementTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return npcElementModel;
    }

    public static NpcModel getNpc(int npcId) {
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "SELECT * FROM npc WHERE id = ?";
        NpcModel npcModel = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npcId);
            rs = statement.executeQuery();
            if (rs.next()) {
                npcModel = new NpcModel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return npcModel;
    }


    public static List<NpcModel> getNpcList(int offset) {
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "SELECT * FROM npc ORDER BY ordinal LIMIT 17 OFFSET ?";
        List<NpcModel> list = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, offset);
            rs = statement.executeQuery();
            while (rs.next()) {
                NpcModel npcModel = new NpcModel();
                list.add(npcModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public static int maxValue(String table, String column){
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "SELECT MAX(`"+column+"`) AS max_value FROM " + table;
        int value = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()) {
                value = rs.getInt("max_value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return value;
    }

    public static List<NpcModel> getNpcListByLike(String column, String value, int offset) {
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "SELECT * FROM npc WHERE `" + column + "` LIKE '%" + value + "%' ORDER BY ordinal LIMIT 17 OFFSET " + offset;
        List<NpcModel> list = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                NpcModel npcModel = new NpcModel();
                list.add(npcModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public static List<NpcModel> getNpcListByLikeName(String name, int offset) {
        return getNpcListByLike("name", name, offset);
    }

    public static List<NpcModel> getNpcListByLikeId(String npcId, int offset) {
        return getNpcListByLike("id", String.valueOf(npcId), offset);
    }

    public static List<NpcModel> getNpcListByLikeType(String type, int offset) {
        return getNpcListByLike("type", String.valueOf(type), offset);
    }

    public static void addSkill(L2NpcInstance npc, L2Skill skill) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "INSERT INTO npcskills (npcid,skillid,level) VALUES(?,?,?)";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npc.getNpcId());
            statement.setInt(2, skill.getId());
            statement.setInt(3, skill.getLevel());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void removeSkill(L2NpcInstance npc, int skillId) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "DELETE FROM npcskills WHERE npcid=? AND skillid=?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npc.getNpcId());
            statement.setInt(2, skillId);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static List<DropListTable> getDropList(int npcId) {
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "SELECT * FROM droplist WHERE mobid=?";
        List<DropListTable> list = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npcId);
            rs = statement.executeQuery();
            while (rs.next()) {
                DropListTable dropItem = new DropListTable();
                list.add(dropItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public static void addDrop(int npcId, int itemId, int min, int max, int chance, int category, boolean isSpoil) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "INSERT INTO droplist (mobId,itemId,min,max,sweep,chance,category) VALUES(?,?,?,?,?,?,?)";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npcId);
            statement.setInt(2, itemId);
            statement.setInt(3, min);
            statement.setInt(4, max);
            statement.setInt(5, isSpoil ? 1 : 0);
            statement.setInt(6, chance);
            statement.setInt(7, category);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void removeDrop(int npcId, int itemId, boolean isSpoil) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "DELETE FROM droplist WHERE mobId=? AND itemId=? AND sweep=?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npcId);
            statement.setInt(2, itemId);
            statement.setInt(2, isSpoil ? 1 : 0);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void updateVisualStats(L2NpcInstance npc) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "UPDATE npc SET name = ?, title = ?, rhand = ?, lhand = ?, displayId = ? WHERE id = ?";
        final L2NpcTemplate template = npc.getTemplate();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, template.name);
            statement.setString(2, template.title);
            statement.setInt(3, template.rhand);
            statement.setInt(4, template.lhand);
            statement.setInt(5, template.displayId);
            statement.setInt(6, template.getNpcId());
            statement.execute();
        } catch (Exception e1) {
            _log.warning("npc data couldnt be stored in db, query is :" + query + " : " + e1);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void updateElements(NpcElementTable model, String stat) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "UPDATE npc_element SET " + stat + " = " + model.getDarkRes() + " WHERE id = ?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, model.getId());
            statement.execute();
        } catch (Exception e1) {
            _log.warning("npc data couldnt be stored in db, query is :" + query + " : " + e1);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void updateBaseStats(L2NpcInstance npc) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "UPDATE npc SET str = ?, con = ?, dex = ?, int = ?, wit = ?, men = ? WHERE id = ?";
        final L2NpcTemplate template = npc.getTemplate();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, template.baseSTR);
            statement.setInt(2, template.baseCON);
            statement.setInt(3, template.baseDEX);
            statement.setInt(4, template.baseINT);
            statement.setInt(5, template.baseWIT);
            statement.setInt(6, template.baseMEN);
            statement.setInt(7, template.getNpcId());
            statement.execute();
        } catch (Exception e1) {
            _log.warning("npc data couldnt be stored in db, query is :" + query + " : " + e1);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void updateMainStats(L2NpcInstance npc) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "UPDATE npc SET level = ?, hp = ?, mp = ?, patk = ?, pdef = ?, matk = ?, mdef = ?, atkspd = ?, matkspd = ?, walkspd = ?, runspd = ?, exp = ?, sp = ? WHERE id = ?";
        final L2NpcTemplate template = npc.getTemplate();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, template.level);
            statement.setFloat(2, template.baseHpMax);
            statement.setFloat(3, template.baseMpMax);
            statement.setInt(4, template.basePAtk);
            statement.setInt(5, template.basePDef);
            statement.setInt(6, template.baseMAtk);
            statement.setInt(7, template.baseMDef);
            statement.setInt(8, template.basePAtkSpd);
            statement.setFloat(9, template.baseMAtkSpd);
            statement.setInt(10, template.baseWalkSpd);
            statement.setInt(11, template.baseRunSpd);
            statement.setInt(12, template.revardExp);
            statement.setInt(13, template.revardSp);
            statement.setInt(14, template.getNpcId());
            statement.execute();
        } catch (Exception e1) {
            _log.warning("npc data couldnt be stored in db, query is :" + query + " : " + e1);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static SpawnListTable getSpawn(L2NpcInstance npc) {
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        SpawnListTable spawnModel = null;
        String query = "SELECT * FROM spawnlist WHERE npc_templateid=? AND locx=? AND locy=? AND locz=?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npc.getNpcId());
            statement.setInt(2, npc.getSpawnedLoc().x);
            statement.setInt(3, npc.getSpawnedLoc().y);
            statement.setInt(4, npc.getSpawnedLoc().z);
            rs = statement.executeQuery();
            if (rs.next()) {
                spawnModel = new SpawnListTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return spawnModel;
    }

    public static void updateLocation(SpawnListTable spawnModel, SpawnListTable newSpawnModel) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "UPDATE npc SET locx = ?, locy = ?, locz = ?, heading = ?, respawn_delay = ?, respawn_delay_rnd = ?, periodOfDay = ? WHERE npc_templateid=? AND locx=? AND locy=? AND locz=?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, newSpawnModel.getLocX());
            statement.setInt(2, newSpawnModel.getLocY());
            statement.setInt(3, newSpawnModel.getLocZ());
            statement.setInt(4, newSpawnModel.getHeading());
            statement.setInt(5, newSpawnModel.getRespawnDelay());
            statement.setInt(6, newSpawnModel.getRespawnDelayRnd());
            statement.setInt(7, newSpawnModel.getPeriodOfDay());
            statement.setInt(8, spawnModel.getNpcTemplateId());
            statement.setFloat(9, spawnModel.getLocX());
            statement.setInt(10, spawnModel.getLocY());
            statement.setInt(11, spawnModel.getLocZ());
            statement.execute();
        } catch (Exception e1) {
            _log.warning("npc data couldnt be stored in db, query is :" + query + " : " + e1);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void updateOtherStats(L2NpcInstance npc) {


    }


}
