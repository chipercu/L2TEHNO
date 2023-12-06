package utils_soft.NpcEditor;

import communityboard.models.buffer.Scheme;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class NpcRepository {
    protected static Logger _log = Logger.getLogger(NpcRepository.class.getName());


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

    public static void removeSkill(L2NpcInstance npc, int skillId){
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

    public static List<DropItem> getDropList(int npcId){
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "SELECT * FROM  WHERE owner=?";
        List<DropItem> list = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npcId);
            rs = statement.executeQuery();
            while (rs.next()) {
                final boolean sweep = rs.getInt("sweep") == 1;
                DropItem dropItem = new DropItem(
                        rs.getInt("itemId"),
                        rs.getInt("min"),
                        rs.getInt("max"),
                        rs.getInt("category"),
                        rs.getInt("chance"),
                        rs.getInt("sweep") == 1
                );
                list.add(dropItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public static void addDrop(L2NpcInstance npc, int itemId, int min, int max, int chance, int category, boolean isSpoil){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "INSERT INTO droplist (mobId,itemId,min,max,sweep,chance,category) VALUES(?,?,?,?,?,?,?)";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npc.getNpcId());
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

    public static void removeDrop(L2NpcInstance npc, int itemId, boolean isSpoil){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "DELETE FROM droplist WHERE mobId=? AND itemId=? AND sweep=?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npc.getNpcId());
            statement.setInt(2, itemId);
            statement.setInt(2, isSpoil? 1 : 0);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void updateVisualStats(L2NpcInstance npc){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "UPDATE npc_element SET name = ?, title = ?, rhand = ?, lhand = ?, displayId = ? WHERE id = ?";
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

    public static void updateElements(L2NpcInstance npc) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "UPDATE npc_element SET AtkElement = ?, elemAtkPower = ?, FireRes = ?, WaterRes = ?, WindRes = ?, EarthRes = ?, HolyRes = ?, DarkRes = ? WHERE id = ?";
        final L2NpcTemplate template = npc.getTemplate();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, template.atkElement);
            statement.setInt(2, template.elemAtkPower);
            statement.setInt(3, template.baseFireRes);
            statement.setInt(4, template.baseWaterRes);
            statement.setInt(5, template.baseWindRes);
            statement.setInt(6, template.baseEarthRes);
            statement.setInt(7, template.baseHolyRes);
            statement.setInt(8, template.baseDarkRes);
            statement.setInt(9, template.getNpcId());
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

    public static void updateLocation(L2NpcInstance npc) {


    }

    public static void updateOtherStats(L2NpcInstance npc) {


    }
}
