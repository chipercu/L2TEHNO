package utils_soft.NpcEditor;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.cache.InfoCache;
import l2open.gameserver.instancemanager.CatacombSpawnManager;
import l2open.gameserver.model.L2DropData;
import l2open.gameserver.model.L2MinionData;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.DropList;
import l2open.util.Location;
import l2open.util.Log;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static l2open.gameserver.tables.NpcTable.*;

public class NpcEditorRepository {
    protected static Logger _log = Logger.getLogger(NpcEditorRepository.class.getName());

    public static List<NpcModel> getNpcList(int offset){
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
                NpcModel npcModel = new NpcModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getInt("displayId"),
                        rs.getString("type"));
                list.add(npcModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public static List<NpcModel> getNpcListByLike(String column, String value, int offset){
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "SELECT * FROM npc WHERE `" + column +"` LIKE '%" + value + "%' ORDER BY ordinal LIMIT 17 OFFSET " + offset;
        List<NpcModel> list = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
//            statement.setString(1, column);
//            statement.setString(2, "%" + value + "%");
//            statement.setInt(3, offset);
            rs = statement.executeQuery();
            while (rs.next()) {
                NpcModel npcModel = new NpcModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getInt("displayId"),
                        rs.getString("type"));
                list.add(npcModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public static List<NpcModel> getNpcListByLikeName(String name, int offset){
        return getNpcListByLike("name", name, offset);
    }

    public static List<NpcModel> getNpcListByLikeId(String npcId, int offset){
        return getNpcListByLike("id", String.valueOf(npcId), offset);
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
        String query = "SELECT * FROM droplist WHERE mobid=?";
        List<DropItem> list = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npcId);
            rs = statement.executeQuery();
            while (rs.next()) {
                final int itemId = rs.getInt("itemId");
                final L2Item l2Item = ItemTemplates.getInstance().getTemplate(itemId);
//                if (l2Item.isHerb()){
//                    continue;
//                }
                DropItem dropItem = new DropItem(
                        itemId,
                        rs.getInt("min"),
                        rs.getInt("max"),
                        rs.getInt("category"),
                        rs.getInt("chance"),
                        rs.getInt("sweep") == 1,
                        l2Item.isHerb(),
                        l2Item.getIcon(),
                        l2Item.getName());
                list.add(dropItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return list;
    }

    public static void addDrop(int npcId, int itemId, int min, int max, int chance, int category, boolean isSpoil){
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

    public static void removeDrop(int npcId, int itemId, boolean isSpoil){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "DELETE FROM droplist WHERE mobId=? AND itemId=? AND sweep=?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npcId);
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

    public static SpawnModel getLocation(L2NpcInstance npc){
        ResultSet rs = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        SpawnModel spawnModel = null;
        String query = "SELECT * FROM spawnlist WHERE npc_templateid=? AND locx=? AND locy=? AND locz=?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, npc.getNpcId());
            statement.setInt(2, npc.getSpawnedLoc().x);
            statement.setInt(3, npc.getSpawnedLoc().y);
            statement.setInt(4, npc.getSpawnedLoc().z);
            rs = statement.executeQuery();
            if (rs.next()){
                spawnModel = new SpawnModel(
                        rs.getString("location"),
                        rs.getInt("count"),
                        rs.getInt("npc_templateid"),
                        rs.getInt("locx"),
                        rs.getInt("locy"),
                        rs.getInt("locz"),
                        rs.getInt("heading"),
                        rs.getInt("respawn_delay"),
                        rs.getInt("respawn_delay_rnd"),
                        rs.getInt("loc_id"),
                        rs.getInt("baned_loc_id"),
                        rs.getInt("periodOfDay"),
                        rs.getInt("reflection"),
                        rs.getInt("statParam"),
                        rs.getString("aiParam")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
        return spawnModel;
    }

    public static void updateLocation(SpawnModel spawnModel, SpawnModel newSpawnModel) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "UPDATE npc SET locx = ?, locy = ?, locz = ?, heading = ?, respawn_delay = ?, respawn_delay_rnd = ?, periodOfDay = ? WHERE npc_templateid=? AND locx=? AND locy=? AND locz=?";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            statement.setInt(1, newSpawnModel.getLocx());
            statement.setInt(2, newSpawnModel.getLocy());
            statement.setInt(3, newSpawnModel.getLocz());
            statement.setInt(4, newSpawnModel.getHeading());
            statement.setInt(5, newSpawnModel.getRespawn_delay());
            statement.setInt(6, newSpawnModel.getRespawn_delay_rnd());
            statement.setInt(7, newSpawnModel.getPeriodOfDay());
            statement.setInt(8, spawnModel.getNpc_templateid());
            statement.setFloat(9, spawnModel.getLocx());
            statement.setInt(10, spawnModel.getLocy());
            statement.setInt(11, spawnModel.getLocz());
            statement.execute();
        } catch (Exception e1) {
            _log.warning("npc data couldnt be stored in db, query is :" + query + " : " + e1);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static void updateOtherStats(L2NpcInstance npc) {


    }

    public static void restoreNpc(int npcId) {
            ThreadConnection con = null;
            FiltredPreparedStatement statement = null;
            ResultSet rs = null;
            try {
                con = L2DatabaseFactory.getInstance().getConnection();
                try {
                    statement = con.prepareStatement("SELECT * FROM ai_params WHERE npc_id = ?");
                    statement.setInt(1, npcId);
                    rs = statement.executeQuery();
                    LoadAIParams(rs);
                } catch(Exception ignored) {}
                finally {
                    DatabaseUtils.closeDatabaseSR(statement, rs);
                }

                try {
                    statement = con.prepareStatement("SELECT * FROM npc AS c LEFT JOIN npc_element AS cs ON (c.id=cs.id) WHERE ai_type IS NOT NULL AND c.id=?");
                    statement.setInt(1, npcId);
                    rs = statement.executeQuery();
                    fillNpcTable(rs);
                } catch(Exception e) {
                    _log.log(Level.SEVERE, "error while creating npc table ", e);
                } finally {
                    DatabaseUtils.closeDatabaseSR(statement, rs);
                }

                try {
                    statement = con.prepareStatement("SELECT npcid, skillid, level FROM npcskills WHERE npcid = ?");
                    statement.setInt(1, npcId);
                    rs = statement.executeQuery();
                    L2NpcTemplate npcDat;
                    L2Skill npcSkill;

                    List<Integer> unimpl = new ArrayList<Integer>();
                    int counter = 0;
                    while(rs.next()) {
                        int mobId = rs.getInt("npcid");
                        npcDat = getNpcs()[mobId];
                        if(npcDat == null){
                            continue;
                        }
                        short skillId = rs.getShort("skillid");
                        int level = rs.getByte("level");

                        // Для определения расы используется скилл 4416
                        if(skillId == 4416)
                            npcDat.setRace(level);

                        if(skillId >= 4290 && skillId <= 4302) {
                            _log.info("Warning! Skill " + skillId + " not used, use 4416 instead.");
                            continue;
                        }

                        if(skillId == 4408){
                            if(CatacombSpawnManager._monsters.contains(mobId)) {
                                level = ConfigValue.AltCatacombMonstersMultHP + 8;
                                npcDat.setRateHp(NpcTable.getInstance().getHprateskill()[level]);
                            } else{
                                npcDat.setRateHp(NpcTable.getInstance().getHprateskill()[level]);
                            }
                        }


                        npcSkill = SkillTable.getInstance().getInfo(skillId, level);

                        if(!unimpl.contains(Integer.valueOf(skillId)) && (npcSkill == null || npcSkill.getSkillType() == L2Skill.SkillType.NOTDONE || npcSkill.getSkillType() == L2Skill.SkillType.NOTUSED) && npcDat.type.equals("L2Pet")){
                            unimpl.add(Integer.valueOf(skillId));
                        }
                        if(npcSkill == null){
                            continue;
                        }
                        npcDat.addSkill(npcSkill);
                        counter++;
                    }
                    new File("log/game/unimplemented_npc_skills.txt").delete();
                    for(Integer i : unimpl){
                        Log.add("[" + i + "] " + SkillTable.getInstance().getInfo(i, 1), "unimplemented_npc_skills", "");
                    }
                    _log.info("Loaded " + counter + " npc skills.");
                } catch(Exception e) {
                    _log.log(Level.SEVERE, "error while reading npcskills table ", e);
                } finally {
                    DatabaseUtils.closeDatabaseSR(statement, rs);
                }

                try {
                    statement = con.prepareStatement("SELECT * FROM droplist WHERE mobId = ? ORDER BY mobId, category, chance DESC");
                    statement.setInt(1, npcId);
                    rs = statement.executeQuery();
                    L2DropData dropDat = null;
                    L2NpcTemplate npcDat = null;

                    while(rs.next()) {
                        int mobId = rs.getInt("mobId");
                        npcDat = getNpcs()[mobId];
                        if(npcDat != null) {
                            dropDat = new L2DropData();
                            int id = rs.getInt("itemId");
                            if(ItemTemplates.getInstance().getTemplate(id).isCommonItem()) {
                                dropDat.setItemId(id);
                                dropDat.setChance(rs.getInt("chance") * ConfigValue.RateDropCommonItems);
                            } else {
                                dropDat.setItemId(id);
                                dropDat.setChance(rs.getInt("chance"));
                            }

                            dropDat.setMinDrop(rs.getLong("min"));
                            dropDat.setMaxDrop(rs.getLong("max"));
                            dropDat.setSweep(rs.getInt("sweep") == 1);

                            if(dropDat.getItem().isArrow() || dropDat.getItemId() == 1419){
                                dropDat.setGroupId(Byte.MAX_VALUE); // группа для нерейтуемых предметов, сюда же надо всякую фигню
                            } else{
                                dropDat.setGroupId(rs.getInt("category"));
                            }

                            if(ConfigValue.EnableModDrop) {
                                dropDat.setIsRate(rs.getBoolean("is_rate"));
                                dropDat.setIsPremium(rs.getBoolean("is_premium"));
                            }
                            npcDat.addDropData(dropDat);
                        }
                    }


                    if(ConfigValue.AltShowDroplist && !ConfigValue.AltGenerateDroplistOnDemand){
                        final L2NpcTemplate npc = getNpcs()[npcId];
                        if(npc != null){
                            InfoCache.addToDroplistCache(npc.npcId, DropList.generateDroplist(npc, null, 1, 1, null));
                        }
                    } else{
                        _log.info("Players droplist load skipped");
                    }

//                    NpcTable.getInstance().loadKillCount();
                } catch(Exception e) {
                    _log.log(Level.SEVERE, "error reading npc drops ", e);
                } finally {
                    DatabaseUtils.closeDatabaseSR(statement, rs);
                }

                try {
                    statement = con.prepareStatement("SELECT boss_id, minion_id, amount FROM minions WHERE boss_id = ?");
                    statement.setInt(1, npcId);
                    rs = statement.executeQuery();
                    L2MinionData minionDat = null;
                    L2NpcTemplate npcDat = null;
                    int cnt = 0;

                    while(rs.next()) {
                        int raidId = rs.getInt("boss_id");
                        npcDat = getNpcs()[raidId];
                        minionDat = new L2MinionData();
                        minionDat.setMinionId(rs.getInt("minion_id"));
                        minionDat.setAmount(rs.getByte("amount"));
                        npcDat.addRaidData(minionDat);
                        cnt++;
                    }

                    _log.info("NpcTable: Loaded " + cnt + " Minions.");
                }
                catch(Exception e) {
                    _log.log(Level.SEVERE, "error loading minions", e);
                } finally {
                    DatabaseUtils.closeDatabaseSR(statement, rs);
                }

                try {
                    statement = con.prepareStatement("SELECT npc_id, class_id FROM skill_learn WHERE npc_id = ?");
                    statement.setInt(1, npcId);
                    rs = statement.executeQuery();
                    L2NpcTemplate npcDat = null;
                    int cnt = 0;

                    while(rs.next()) {
                        npcDat = getNpcs()[rs.getInt(1)];
                        npcDat.addTeachInfo(ClassId.values()[rs.getInt(2)]);
                        cnt++;
                    }

                    _log.info("NpcTable: Loaded " + cnt + " SkillLearn entrys.");
                } catch(Exception e) {
                    _log.log(Level.SEVERE, "error loading minions", e);
                } finally {
                    DatabaseUtils.closeDatabaseSR(statement, rs);
                }
            }
            catch(Exception e) {
                _log.log(Level.SEVERE, "Cannot find connection to database");
            } finally {
                DatabaseUtils.closeConnection(con);
            }
            Scripts.getInstance();
        }


}
