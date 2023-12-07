package l2open.gameserver.tables;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.cache.InfoCache;
import l2open.gameserver.instancemanager.CatacombSpawnManager;
import l2open.gameserver.model.L2DropData;
import l2open.gameserver.model.L2MinionData;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2TamedBeastInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.DropList;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NpcTable {
    private static final Logger _log = Logger.getLogger(NpcTable.class.getName());

    private static NpcTable _instance;

    private static L2NpcTemplate[] _npcs;
    private static HashMap<Integer, StatsSet> ai_params;
    private static GArray<L2NpcTemplate>[] _npcsByLevel;
    private static HashMap<String, L2NpcTemplate> _npcsNames;
    private static boolean _initialized = false;

    public static NpcTable getInstance() {
        if (_instance == null)
            _instance = new NpcTable();

        return _instance;
    }

    @SuppressWarnings("unchecked")
    private NpcTable() {
        _npcsByLevel = new GArray[100];
        _npcsNames = new HashMap<String, L2NpcTemplate>();
        ai_params = new HashMap<Integer, StatsSet>();
        RestoreNpcData();
    }

    public double[] getHprateskill() {
        return hprateskill;
    }

    public static HashMap<Integer, StatsSet> getAi_params() {
        return ai_params;
    }

    public static L2NpcTemplate[] getNpcs() {
        return _npcs;
    }

    public static GArray<L2NpcTemplate>[] getNpcsByLevel() {
        return _npcsByLevel;
    }

    public static HashMap<String, L2NpcTemplate> getNpcsNames() {
        return _npcsNames;
    }

    private final double[] hprateskill = new double[]{0, 1, 1.2, 1.3, 2, 2, 4, 4, 0.25, 0.5, 2, 3, 4, 5, 6, 7, 8, 9,
            10, 11, 12};

    private void RestoreNpcData() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();

            try {
                statement = con.prepareStatement("SELECT * FROM ai_params");
                rs = statement.executeQuery();
                LoadAIParams(rs);
            } catch (Exception e) {
            } finally {
                DatabaseUtils.closeDatabaseSR(statement, rs);
            }

            try {
                statement = con.prepareStatement("SELECT * FROM npc AS c LEFT JOIN npc_element AS cs ON (c.id=cs.id) WHERE ai_type IS NOT NULL");
                rs = statement.executeQuery();
                fillNpcTable(rs);
            } catch (Exception e) {
                _log.log(Level.SEVERE, "error while creating npc table ", e);
            } finally {
                DatabaseUtils.closeDatabaseSR(statement, rs);
            }

		/*	try
			{
				statement = con.prepareStatement("SELECT npc_id, skill_id FROM npc_skill_parse");
				rs = statement.executeQuery();
				L2NpcTemplate npcDat2;

				List<Integer> unimpl = new ArrayList<Integer>();
				int counter = 0;
				while(rs.next())
				{
					int mobId = rs.getInt("npc_id");
					npcDat2 = _npcs[mobId];
					if(npcDat2 == null)
						continue;
					short skillId = rs.getShort("skill_id");

					if(skillId == 5462) // baseFireRes
					{
						if(npcDat2.baseFireRes > npcDat2.baseWaterRes || npcDat2.baseFireRes > npcDat2.baseWindRes || npcDat2.baseFireRes > npcDat2.baseEarthRes || npcDat2.baseFireRes > npcDat2.baseHolyRes || npcDat2.baseFireRes > npcDat2.baseDarkRes)
							_log.info("NpcTable1["+npcDat2.baseFireRes+"]["+npcDat2.baseWaterRes+"]["+npcDat2.baseWindRes+"]["+npcDat2.baseEarthRes+"]["+npcDat2.baseHolyRes+"]["+npcDat2.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5463) // baseWaterRes
					{
						if(npcDat2.baseWaterRes > npcDat2.baseFireRes || npcDat2.baseWaterRes > npcDat2.baseWindRes || npcDat2.baseWaterRes > npcDat2.baseEarthRes || npcDat2.baseWaterRes > npcDat2.baseHolyRes || npcDat2.baseWaterRes > npcDat2.baseDarkRes)
							_log.info("NpcTable2["+npcDat2.baseFireRes+"]["+npcDat2.baseWaterRes+"]["+npcDat2.baseWindRes+"]["+npcDat2.baseEarthRes+"]["+npcDat2.baseHolyRes+"]["+npcDat2.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5464) // baseWindRes
					{
						if(npcDat2.baseWindRes > npcDat2.baseWaterRes || npcDat2.baseWindRes > npcDat2.baseFireRes || npcDat2.baseWindRes > npcDat2.baseEarthRes || npcDat2.baseWindRes > npcDat2.baseHolyRes || npcDat2.baseWindRes > npcDat2.baseDarkRes)
							_log.info("NpcTable3["+npcDat2.baseFireRes+"]["+npcDat2.baseWaterRes+"]["+npcDat2.baseWindRes+"]["+npcDat2.baseEarthRes+"]["+npcDat2.baseHolyRes+"]["+npcDat2.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5465) // baseEarthRes
					{
						if(npcDat2.baseEarthRes > npcDat2.baseWaterRes || npcDat2.baseEarthRes > npcDat2.baseWindRes || npcDat2.baseEarthRes > npcDat2.baseFireRes || npcDat2.baseEarthRes > npcDat2.baseHolyRes || npcDat2.baseEarthRes > npcDat2.baseDarkRes)
							_log.info("NpcTable4["+npcDat2.baseFireRes+"]["+npcDat2.baseWaterRes+"]["+npcDat2.baseWindRes+"]["+npcDat2.baseEarthRes+"]["+npcDat2.baseHolyRes+"]["+npcDat2.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5466) // baseHolyRes
					{
						if(npcDat2.baseHolyRes > npcDat2.baseWaterRes || npcDat2.baseHolyRes > npcDat2.baseWindRes || npcDat2.baseHolyRes > npcDat2.baseEarthRes || npcDat2.baseHolyRes > npcDat2.baseFireRes || npcDat2.baseHolyRes > npcDat2.baseDarkRes)
							_log.info("NpcTable5["+npcDat2.baseFireRes+"]["+npcDat2.baseWaterRes+"]["+npcDat2.baseWindRes+"]["+npcDat2.baseEarthRes+"]["+npcDat2.baseHolyRes+"]["+npcDat2.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5467) // baseDarkRes
					{
						if(npcDat2.baseDarkRes > npcDat2.baseFireRes)
							_log.info("NpcTable6-F["+npcDat2.baseDarkRes+"]["+npcDat2.baseFireRes+"]: "+mobId);
						if(npcDat2.baseDarkRes > npcDat2.baseWaterRes)
							_log.info("NpcTable6-Wa["+npcDat2.baseDarkRes+"]["+npcDat2.baseWaterRes+"]: "+mobId);
						if(npcDat2.baseDarkRes > npcDat2.baseWindRes)
							_log.info("NpcTable6-Wi["+npcDat2.baseDarkRes+"]["+npcDat2.baseWindRes+"]: "+mobId);
						if(npcDat2.baseDarkRes > npcDat2.baseEarthRes)
							_log.info("NpcTable6-E["+npcDat2.baseDarkRes+"]["+npcDat2.baseEarthRes+"]: "+mobId);
						if(npcDat2.baseDarkRes > npcDat2.baseHolyRes)
							_log.info("NpcTable6-H["+npcDat2.baseDarkRes+"]["+npcDat2.baseHolyRes+"]: "+mobId);

						if(npcDat2.baseDarkRes > npcDat2.baseWaterRes || npcDat2.baseDarkRes > npcDat2.baseWindRes || npcDat2.baseDarkRes > npcDat2.baseEarthRes || npcDat2.baseDarkRes > npcDat2.baseHolyRes || npcDat2.baseDarkRes > npcDat2.baseFireRes)
							_log.info("NpcTable6["+npcDat2.baseFireRes+"]["+npcDat2.baseWaterRes+"]["+npcDat2.baseWindRes+"]["+npcDat2.baseEarthRes+"]["+npcDat2.baseHolyRes+"]["+npcDat2.baseDarkRes+"]: "+mobId);
					}
					counter++;
				}
				//new File("log/game/unimplemented_npc_skills.txt").delete();
				//for(Integer i : unimpl)
				//	Log.add("[" + i + "] " + SkillTable.getInstance().getInfo(i, 1), "unimplemented_npc_skills", "");
				//_log.info("Loaded " + counter + " npc skills.");
			}
			catch(Exception e)
			{
				_log.log(Level.SEVERE, "error while reading npcskills table ", e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseSR(statement, rs);
			}*/

            try {
                statement = con.prepareStatement("SELECT npcid, skillid, level FROM npcskills");
                rs = statement.executeQuery();
                L2NpcTemplate npcDat;
                L2Skill npcSkill;

                List<Integer> unimpl = new ArrayList<Integer>();
                int counter = 0;
                while (rs.next()) {
                    int mobId = rs.getInt("npcid");
                    npcDat = _npcs[mobId];
                    if (npcDat == null)
                        continue;
                    short skillId = rs.getShort("skillid");
                    int level = rs.getByte("level");

					/*if(skillId == 5462) // baseFireRes
					{
						if(npcDat.baseFireRes > npcDat.baseWaterRes || npcDat.baseFireRes > npcDat.baseWindRes || npcDat.baseFireRes > npcDat.baseEarthRes || npcDat.baseFireRes > npcDat.baseHolyRes || npcDat.baseFireRes > npcDat.baseDarkRes)
							_log.info("NpcTable1["+npcDat.baseFireRes+"]["+npcDat.baseWaterRes+"]["+npcDat.baseWindRes+"]["+npcDat.baseEarthRes+"]["+npcDat.baseHolyRes+"]["+npcDat.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5463) // baseWaterRes
					{
						if(npcDat.baseWaterRes > npcDat.baseFireRes || npcDat.baseWaterRes > npcDat.baseWindRes || npcDat.baseWaterRes > npcDat.baseEarthRes || npcDat.baseWaterRes > npcDat.baseHolyRes || npcDat.baseWaterRes > npcDat.baseDarkRes)
							_log.info("NpcTable2["+npcDat.baseFireRes+"]["+npcDat.baseWaterRes+"]["+npcDat.baseWindRes+"]["+npcDat.baseEarthRes+"]["+npcDat.baseHolyRes+"]["+npcDat.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5464) // baseWindRes
					{
						if(npcDat.baseWindRes > npcDat.baseWaterRes || npcDat.baseWindRes > npcDat.baseFireRes || npcDat.baseWindRes > npcDat.baseEarthRes || npcDat.baseWindRes > npcDat.baseHolyRes || npcDat.baseWindRes > npcDat.baseDarkRes)
							_log.info("NpcTable3["+npcDat.baseFireRes+"]["+npcDat.baseWaterRes+"]["+npcDat.baseWindRes+"]["+npcDat.baseEarthRes+"]["+npcDat.baseHolyRes+"]["+npcDat.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5465) // baseEarthRes
					{
						if(npcDat.baseEarthRes > npcDat.baseWaterRes || npcDat.baseEarthRes > npcDat.baseWindRes || npcDat.baseEarthRes > npcDat.baseFireRes || npcDat.baseEarthRes > npcDat.baseHolyRes || npcDat.baseEarthRes > npcDat.baseDarkRes)
							_log.info("NpcTable4["+npcDat.baseFireRes+"]["+npcDat.baseWaterRes+"]["+npcDat.baseWindRes+"]["+npcDat.baseEarthRes+"]["+npcDat.baseHolyRes+"]["+npcDat.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5466) // baseHolyRes
					{
						if(npcDat.baseHolyRes > npcDat.baseWaterRes || npcDat.baseHolyRes > npcDat.baseWindRes || npcDat.baseHolyRes > npcDat.baseEarthRes || npcDat.baseHolyRes > npcDat.baseFireRes || npcDat.baseHolyRes > npcDat.baseDarkRes)
							_log.info("NpcTable5["+npcDat.baseFireRes+"]["+npcDat.baseWaterRes+"]["+npcDat.baseWindRes+"]["+npcDat.baseEarthRes+"]["+npcDat.baseHolyRes+"]["+npcDat.baseDarkRes+"]: "+mobId);
					}
					else if(skillId == 5467) // baseDarkRes
					{
						if(npcDat.baseDarkRes > npcDat.baseFireRes)
							_log.info("NpcTable6-F["+npcDat.baseDarkRes+"]["+npcDat.baseFireRes+"]: "+mobId);
						if(npcDat.baseDarkRes > npcDat.baseWaterRes)
							_log.info("NpcTable6-Wa["+npcDat.baseDarkRes+"]["+npcDat.baseWaterRes+"]: "+mobId);
						if(npcDat.baseDarkRes > npcDat.baseWindRes)
							_log.info("NpcTable6-Wi["+npcDat.baseDarkRes+"]["+npcDat.baseWindRes+"]: "+mobId);
						if(npcDat.baseDarkRes > npcDat.baseEarthRes)
							_log.info("NpcTable6-E["+npcDat.baseDarkRes+"]["+npcDat.baseEarthRes+"]: "+mobId);
						if(npcDat.baseDarkRes > npcDat.baseHolyRes)
							_log.info("NpcTable6-H["+npcDat.baseDarkRes+"]["+npcDat.baseHolyRes+"]: "+mobId);

						if(npcDat.baseDarkRes > npcDat.baseWaterRes || npcDat.baseDarkRes > npcDat.baseWindRes || npcDat.baseDarkRes > npcDat.baseEarthRes || npcDat.baseDarkRes > npcDat.baseHolyRes || npcDat.baseDarkRes < npcDat.baseFireRes)
							_log.info("NpcTable6["+npcDat.baseFireRes+"]["+npcDat.baseWaterRes+"]["+npcDat.baseWindRes+"]["+npcDat.baseEarthRes+"]["+npcDat.baseHolyRes+"]["+npcDat.baseDarkRes+"]: "+mobId);
					}*/

                    // Для определения расы используется скилл 4416
                    if (skillId == 4416)
                        npcDat.setRace(level);

                    if (skillId >= 4290 && skillId <= 4302) {
                        _log.info("Warning! Skill " + skillId + " not used, use 4416 instead.");
                        continue;
                    }

                    if (skillId == 4408)
                        if (CatacombSpawnManager._monsters.contains(mobId)) {
                            level = ConfigValue.AltCatacombMonstersMultHP + 8;
                            npcDat.setRateHp(hprateskill[level]);
                            //if(ConfigValue.AltCatacombMonstersMultHP != 4)
                            //npcDat.addSkill(SkillTable.getInstance().getInfo(4417, ConfigValue.AltCatacombMonstersMultHP));
                        } else
                            npcDat.setRateHp(hprateskill[level]);

                    npcSkill = SkillTable.getInstance().getInfo(skillId, level);

                    if (!unimpl.contains(Integer.valueOf(skillId)) && (npcSkill == null || npcSkill.getSkillType() == SkillType.NOTDONE || npcSkill.getSkillType() == SkillType.NOTUSED) && npcDat.type.equals("L2Pet"))
                        unimpl.add(Integer.valueOf(skillId));

                    if (npcSkill == null)
                        continue;

                    npcDat.addSkill(npcSkill);
                    counter++;
                }
                new File("log/game/unimplemented_npc_skills.txt").delete();
                for (Integer i : unimpl)
                    Log.add("[" + i + "] " + SkillTable.getInstance().getInfo(i, 1), "unimplemented_npc_skills", "");
                _log.info("Loaded " + counter + " npc skills.");
            } catch (Exception e) {
                _log.log(Level.SEVERE, "error while reading npcskills table ", e);
            } finally {
                DatabaseUtils.closeDatabaseSR(statement, rs);
            }

            try {
                statement = con.prepareStatement("SELECT * FROM droplist ORDER BY mobId, category, chance DESC");
                rs = statement.executeQuery();
                L2DropData dropDat = null;
                L2NpcTemplate npcDat = null;

                while (rs.next()) {
                    int mobId = rs.getInt("mobId");
                    npcDat = _npcs[mobId];
                    if (npcDat != null) {
                        dropDat = new L2DropData();

                        int id = rs.getInt("itemId");
                        if (ItemTemplates.getInstance().getTemplate(id).isCommonItem()) {
                            dropDat.setItemId(id);
                            dropDat.setChance(rs.getInt("chance") * ConfigValue.RateDropCommonItems);
                        } else {
                            dropDat.setItemId(id);
                            dropDat.setChance(rs.getInt("chance"));
                        }
                        dropDat.setMinDrop(rs.getLong("min"));
                        dropDat.setMaxDrop(rs.getLong("max"));
                        dropDat.setSweep(rs.getInt("sweep") == 1);
                        if (dropDat.getItem().isArrow() || dropDat.getItemId() == 1419)
                            dropDat.setGroupId(Byte.MAX_VALUE); // группа для нерейтуемых предметов, сюда же надо всякую фигню
                        else
                            dropDat.setGroupId(rs.getInt("category"));

                        if (ConfigValue.EnableModDrop) {
                            dropDat.setIsRate(rs.getBoolean("is_rate"));
                            dropDat.setIsPremium(rs.getBoolean("is_premium"));
                        }

                        npcDat.addDropData(dropDat);
                    }
                }

				/*for(L2NpcTemplate temp : _npcs)
					if(temp != null && temp.getDropData() != null)
						if(!temp.getDropData().validate())
							_log.warning("Problems with droplist for " + temp.toString());*/

                if (ConfigValue.AltShowDroplist && !ConfigValue.AltGenerateDroplistOnDemand)
                    FillDropList();
                else
                    _log.info("Players droplist load skipped");

                loadKillCount();
            }
			/*try
			{
				statement = con.prepareStatement("SELECT mobId, itemId, min, max, sweep, chance, groupChance, category FROM droplist ORDER BY mobId, category");
				rs = statement.executeQuery();
				L2DropData dropDat = null;
				L2NpcTemplate npcDat = null;

				while(rs.next())
				{
					int mobId = rs.getInt("mobId");
					npcDat = _npcs[mobId];
					if(npcDat != null)
					{
						dropDat = new L2DropData();

						int id = rs.getShort("itemId");
						if(ItemTemplates.getInstance().getTemplate(id).isCommonItem())
						{
                            dropDat.setItemId(id);
                            dropDat.setChance(rs.getInt("chance") * ConfigValue.RateDropCommonItems);
						}
						else
						{
							dropDat.setItemId(id);
							dropDat.setChance(rs.getDouble("chance"));
						}
						dropDat.setMinDrop(rs.getInt("min"));
						dropDat.setMaxDrop(rs.getInt("max"));
						dropDat.setSweep(rs.getInt("sweep") == 1);
						dropDat.setGroupId(rs.getInt("category"));
						dropDat.setChanceInGroup(rs.getDouble("groupChance"));
						npcDat.addDropData(dropDat);
					}
				}

				for(L2NpcTemplate temp : _npcs)
					if(temp != null && temp.getDropData() != null)
						if(!temp.getDropData().validate())
							_log.warning("Problems with droplist for " + temp.toString());

				if(ConfigValue.AltShowDroplist && !ConfigValue.AltGenerateDroplistOnDemand)
					FillDropList();
				else
					_log.info("Players droplist load skipped");

				loadKillCount();
			}*/ catch (Exception e) {
                _log.log(Level.SEVERE, "error reading npc drops ", e);
            } finally {
                DatabaseUtils.closeDatabaseSR(statement, rs);
            }

            try {
                statement = con.prepareStatement("SELECT boss_id, minion_id, amount FROM minions");
                rs = statement.executeQuery();
                L2MinionData minionDat = null;
                L2NpcTemplate npcDat = null;
                int cnt = 0;

                while (rs.next()) {
                    int raidId = rs.getInt("boss_id");
                    npcDat = _npcs[raidId];
                    minionDat = new L2MinionData();
                    minionDat.setMinionId(rs.getInt("minion_id"));
                    minionDat.setAmount(rs.getByte("amount"));
                    npcDat.addRaidData(minionDat);
                    cnt++;
                }

                _log.info("NpcTable: Loaded " + cnt + " Minions.");
            } catch (Exception e) {
                _log.log(Level.SEVERE, "error loading minions", e);
            } finally {
                DatabaseUtils.closeDatabaseSR(statement, rs);
            }

            try {
                statement = con.prepareStatement("SELECT npc_id, class_id FROM skill_learn");
                rs = statement.executeQuery();
                L2NpcTemplate npcDat = null;
                int cnt = 0;

                while (rs.next()) {
                    npcDat = _npcs[rs.getInt(1)];
                    npcDat.addTeachInfo(ClassId.values()[rs.getInt(2)]);
                    cnt++;
                }

                _log.info("NpcTable: Loaded " + cnt + " SkillLearn entrys.");
            } catch (Exception e) {
                _log.log(Level.SEVERE, "error loading minions", e);
            } finally {
                DatabaseUtils.closeDatabaseSR(statement, rs);
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "Cannot find connection to database");
        } finally {
            DatabaseUtils.closeConnection(con);
        }

        _initialized = true;

        Scripts.getInstance();
    }

    public static void LoadAIParams(ResultSet AIData) throws Exception {
        int ai_params_counter = 0;
        StatsSet set = null;
        int npc_id;
        String param, value;
        while (AIData.next()) {
            npc_id = AIData.getInt("npc_id");
            param = AIData.getString("param");
            value = AIData.getString("value");
            if (ai_params.containsKey(npc_id))
                set = ai_params.get(npc_id);
            else {
                set = new StatsSet();
                ai_params.put(npc_id, set);
            }
            set.set(param, value);
            ai_params_counter++;
        }
        _log.info("NpcTable: Loaded " + ai_params_counter + " AI params for " + ai_params.size() + " NPCs.");
    }

    private static void setAIField(L2NpcTemplate templ, String fieldName, String value) {
        try {
            Field f = templ.getClass().getField(fieldName);
            setToType(templ, fieldName, value, "NpcTable(363): TODO::Warning text...");
            f = null;
            fieldName = null;
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private static void setToType(L2NpcTemplate nem, String fieldName, String value, String text) {
        try {
            Field f = nem.getClass().getField(fieldName);
            if (f.getType().getName().equals("int"))
                f.setInt(nem, Integer.parseInt(value));
            else if (f.getType().getName().equals("boolean"))
                f.setBoolean(nem, Boolean.parseBoolean(value));
            else if (f.getType().getName().equals("byte"))
                f.setByte(nem, Byte.parseByte(value));
            else if (f.getType().getName().equals("double"))
                f.setDouble(nem, Double.parseDouble(value));
            else if (f.getType().getName().equals("float"))
                f.setFloat(nem, Float.parseFloat(value));
            else if (f.getType().getName().equals("long"))
                f.setLong(nem, Long.parseLong(value));
            else if (f.getType().getName().equals("short"))
                f.setShort(nem, Short.parseShort(value));
            else if (f.getType().getName().equals("java.lang.String"))
                f.set(nem, value);
            else if (f.getType().getName().equals("[J"))
                f.set(f, Util.parseCommaSeparatedLongArray(value.replace(" ", "")));
            else if (f.getType().getName().equals("[I"))
                f.set(nem, Util.parseCommaSeparatedIntegerArray(value));
            else if (f.getType().getName().equals("[D"))
                f.set(nem, Util.parseCommaSeparatedDoubleArray(value));
            else if (f.getType().getName().startsWith("[F"))
                f.set(nem, Util.parseCommaSeparatedFloatArray(value));
            else if (f.getType().getName().startsWith("[Ljava.lang.String"))
                f.set(nem, value);
        } catch (Exception e) {
            _log.warning(text);
            e.printStackTrace();
        }
    }

    public static StatsSet fillNpcTable(ResultSet NpcData) throws Exception {
        StatsSet npcDat = null;
        GArray<L2NpcTemplate> temp = new GArray<L2NpcTemplate>(10000);
        int maxId = 0;
        while (NpcData.next()) {
            npcDat = new StatsSet();
            int id = NpcData.getInt("id");
            int level = NpcData.getByte("level");

            if (maxId < id)
                maxId = id;

            npcDat.set("npcId", id);
            npcDat.set("displayId", NpcData.getInt("displayId"));
            npcDat.set("level", level);
            npcDat.set("jClass", NpcData.getString("class"));
            npcDat.set("race", NpcData.getString("race"));
            npcDat.set("name", NpcData.getString("name"));
            npcDat.set("title", NpcData.getString("title"));
            npcDat.set("collision_radius", NpcData.getDouble("collision_radius"));
            npcDat.set("collision_height", NpcData.getDouble("collision_height"));
            npcDat.set("sex", NpcData.getString("sex"));
            npcDat.set("type", NpcData.getString("type"));
            npcDat.set("ai_type", NpcData.getString("ai_type"));
            npcDat.set("baseAtkRange", NpcData.getInt("attackrange"));
            npcDat.set("revardExp", NpcData.getInt("exp"));
            npcDat.set("revardSp", NpcData.getInt("sp"));
            npcDat.set("basePAtkSpd", NpcData.getInt("atkspd"));
            npcDat.set("baseMAtkSpd", NpcData.getInt("matkspd"));
            npcDat.set("aggroRange", NpcData.getShort("aggro"));
            npcDat.set("rhand", NpcData.getInt("rhand"));
            npcDat.set("lhand", NpcData.getInt("lhand"));
            npcDat.set("armor", NpcData.getInt("armor"));
            npcDat.set("baseWalkSpd", NpcData.getInt("walkspd"));
            npcDat.set("baseRunSpd", NpcData.getInt("runspd"));

            npcDat.set("baseHpReg", NpcData.getDouble("base_hp_regen"));
            npcDat.set("baseCpReg", 0);
            npcDat.set("baseMpReg", NpcData.getDouble("base_mp_regen"));

            npcDat.set("baseSTR", NpcData.getInt("str"));
            npcDat.set("baseCON", NpcData.getInt("con"));
            npcDat.set("baseDEX", NpcData.getInt("dex"));
            npcDat.set("baseINT", NpcData.getInt("int"));
            npcDat.set("baseWIT", NpcData.getInt("wit"));
            npcDat.set("baseMEN", NpcData.getInt("men"));

            npcDat.set("baseHpMax", NpcData.getInt("hp"));
            npcDat.set("baseCpMax", 0);
            npcDat.set("baseMpMax", NpcData.getInt("mp"));
            npcDat.set("basePAtk", NpcData.getInt("patk"));
            npcDat.set("basePDef", NpcData.getInt("pdef"));
            npcDat.set("baseMAtk", NpcData.getInt("matk"));
            npcDat.set("baseMDef", NpcData.getInt("mdef"));

            npcDat.set("baseShldDef", NpcData.getInt("shield_defense"));
            npcDat.set("baseShldRate", NpcData.getInt("shield_defense_rate"));

            if (NpcData.getString("type").equalsIgnoreCase("L2Pet"))
                if (NpcData.getString("name").equalsIgnoreCase("Cursed Man"))
                    npcDat.set("baseCritRate", 80);
                else
                    npcDat.set("baseCritRate", 44);
            else
                npcDat.set("baseCritRate", Math.max(1, NpcData.getInt("base_critical")) * 10);

            String factionId = NpcData.getString("faction_id");
            if (factionId != null)
                factionId.trim();
            npcDat.set("factionId", factionId);
            npcDat.set("factionRange", factionId == null || factionId.equals("") ? 0 : NpcData.getShort("faction_range"));

            npcDat.set("isDropHerbs", NpcData.getBoolean("isDropHerbs"));

            npcDat.set("shots", NpcData.getString("shots"));

            npcDat.set("AtkElement", NpcData.getInt("AtkElement"));
            npcDat.set("elemAtkPower", NpcData.getInt("elemAtkPower"));

            npcDat.set("FireRes", NpcData.getInt("FireRes"));
            npcDat.set("WindRes", NpcData.getInt("WindRes"));
            npcDat.set("WaterRes", NpcData.getInt("WaterRes"));
            npcDat.set("EarthRes", NpcData.getInt("EarthRes"));
            npcDat.set("DarkRes", NpcData.getInt("DarkRes"));
            npcDat.set("HolyRes", NpcData.getInt("HolyRes"));

            npcDat.set("agro_range", NpcData.getInt("agro_range"));
            npcDat.set("event_flag", NpcData.getInt("event_flag"));
            npcDat.set("undying", NpcData.getInt("undying"));
            npcDat.set("can_be_attacked", NpcData.getInt("can_be_attacked"));
            npcDat.set("corpse_time", NpcData.getInt("corpse_time"));
            npcDat.set("base_attack_type", NpcData.getString("base_attack_type"));

            /**
             str=40	int=21	dex=30	wit=20	con=43	men=20
             org_hp=39.74519
             org_hp_regen=2
             org_mp=40
             org_mp_regen=0.9
             base_physical_attack=8.47458
             base_attack_speed=253
             base_magic_attack=5.78704
             base_defend=44.44444
             base_magic_defend=29.5916164000214
             **/

            // Для уебищных игроков, которые считают себя тру папками и дрочат на л2кс ибо по их мыслям это офф база.
            if (ConfigValue.EnableBugNpcStat && !NpcData.getString("type").equalsIgnoreCase("L2Pet")) {
                double STR = Formulas.STRbonus[NpcData.getInt("str")];
                double CON = Formulas.CONbonus[NpcData.getInt("con")];
                double DEX = Formulas.DEXbonus[NpcData.getInt("dex")];
                double INT = Formulas.INTbonus[NpcData.getInt("int")];
                double MEN = Formulas.MENbonus[NpcData.getInt("men")];
                double WIT = Formulas.WITbonus[NpcData.getInt("wit")];

                double level_mod = ConfigValue.BugNpcStatLevelMod ? (89. + level) / 100.0 : 1;

                int org_hp = (int) (NpcData.getInt("hp") / CON);
                int org_mp = (int) (NpcData.getInt("mp") / MEN + 0.5);
                double org_hp_regen = NpcData.getInt("base_hp_regen") / CON;
                double org_mp_regen = NpcData.getInt("base_mp_regen") / MEN;
                int base_magic_attack = (int) ((int) (NpcData.getInt("matk") / INT / INT) / level_mod / level_mod);
                int base_physical_attack = (int) (NpcData.getInt("patk") / STR / level_mod);
                int base_magic_defend = (int) (NpcData.getInt("mdef") / MEN / level_mod + 0.5);
                int base_defend = (int) (NpcData.getInt("pdef") / level_mod + 0.5);
                int base_attack_speed = (int) (NpcData.getInt("atkspd") / DEX + 0.5);
                int base_m_attack_speed = (int) (NpcData.getInt("matkspd") / WIT);
                int base_critical = (int) (Math.max(1, NpcData.getInt("base_critical")) * 10 / DEX);
                int shield_defense_rate = (int) (NpcData.getInt("shield_defense_rate") / DEX);

                npcDat.set("baseHpMax", org_hp);
                npcDat.set("baseMpMax", org_mp);
                npcDat.set("baseHpReg", org_hp_regen);
                npcDat.set("baseMpReg", org_mp_regen);
                npcDat.set("baseMAtk", base_magic_attack);
                npcDat.set("basePAtk", base_physical_attack);
                npcDat.set("baseMDef", base_magic_defend);
                npcDat.set("basePDef", base_defend);
                npcDat.set("basePAtkSpd", base_attack_speed);
                npcDat.set("baseMAtkSpd", base_m_attack_speed);
                npcDat.set("baseCritRate", base_critical);
                npcDat.set("baseShldRate", shield_defense_rate);

                // npcDat.set("baseRunSpd", runspd);
            }

            L2NpcTemplate template = new L2NpcTemplate(npcDat);

            // TODO:!!! Эту поебень еще и в АИ мобов нужно впездывать!!!
            if (ai_params.containsKey(id)) {
                HashMap<String, Object> set = ai_params.get(id).getSet();
                String name = "";
                String values = "";
                for (Object obj : set.keySet()) {
                    name = (String) obj;
                    setAIField(template, name, String.valueOf(set.get(name)));
                }
            }
            temp.add(template);
            if (_npcsByLevel[level] == null)
                _npcsByLevel[level] = new GArray<L2NpcTemplate>();
            _npcsByLevel[level].add(template);
            _npcsNames.put(NpcData.getString("name").toLowerCase(), template);
        }
        _npcs = new L2NpcTemplate[maxId + 1];
        for (L2NpcTemplate template : temp)
            _npcs[template.npcId] = template;
        _log.info("NpcTable: Loaded " + temp.size() + " Npc Templates.");
        return npcDat;
    }

    public static void reloadNpc(int id) {
        ThreadConnection con = null;
        FiltredPreparedStatement st = null;
        ResultSet rs = null;
        try {
            // save a copy of the old data
            L2NpcTemplate old = getTemplate(id);
            HashMap<Integer, L2Skill> skills = new HashMap<Integer, L2Skill>();
            if (old.getSkills() != null)
                skills.putAll(old.getSkills());
			/*
			 Contact with Styx to understand this commenting
			 GArray<L2DropData> drops = new GArray<L2DropData>();
			 if(old.getDropData() != null)
			 drops.addAll(old.getDropData());
			 */
            ClassId[] classIds = null;
            if (old.getTeachInfo() != null)
                classIds = old.getTeachInfo().clone();
            GArray<L2MinionData> minions = new GArray<L2MinionData>();
            minions.addAll(old.getMinionData());

            // reload the NPC base data
            con = L2DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("SELECT * FROM npc AS c LEFT JOIN npc_element AS cs ON (c.id=cs.id) WHERE id=?");
            st.setInt(1, id);
            rs = st.executeQuery();
            fillNpcTable(rs);

            // restore additional data from saved copy
            L2NpcTemplate created = getTemplate(id);
            for (L2Skill skill : skills.values())
                created.addSkill(skill);
			/*
			 for(L2DropData drop : drops)
			 created.addDropData(drop);
			 */
            if (classIds != null)
                for (ClassId classId : classIds)
                    created.addTeachInfo(classId);
            for (L2MinionData minion : minions)
                created.addRaidData(minion);
        } catch (Exception e) {
            _log.warning("cannot reload npc " + id + ": " + e);
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, st, rs);
        }
    }

    public static StatsSet getNpcStatsSet(int id) {
        StatsSet dat = null;

        ThreadConnection con = null;
        FiltredPreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("SELECT * FROM npc AS c LEFT JOIN npc_element AS cs ON (c.id=cs.id) WHERE id=?");
            st.setInt(1, id);
            rs = st.executeQuery();
            dat = fillNpcTable(rs);
        } catch (Exception e) {
            _log.warning("cannot load npc stats for " + id + ": " + e);
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, st, rs);
        }
        return dat;
    }

    public static StatsSet getAIParam(int id) {
        return ai_params.get(id);
    }

    // just wrapper
    @SuppressWarnings("unchecked")
    public void reloadAllNpc() {
        _npcsByLevel = new GArray[100];
        _npcsNames = new HashMap<String, L2NpcTemplate>();
        ai_params = new HashMap<Integer, StatsSet>();
        RestoreNpcData();
    }

    public void saveNpc(StatsSet npc) {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        String query = "";
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            HashMap<String, Object> set = npc.getSet();
            String name = "";
            String values = "";
            for (Object obj : set.keySet()) {
                name = (String) obj;
                if (!name.equalsIgnoreCase("npcId")) {
                    if (!values.equals(""))
                        values += ", ";
                    values += name + " = '" + set.get(name) + "'";
                }
            }
            query = "UPDATE npc SET " + values + " WHERE id = ?";
            statement = con.prepareStatement(query);
            statement.setInt(1, npc.getInteger("npcId"));
            statement.execute();
        } catch (Exception e1) {
            // problem with storing spawn
            _log.warning("npc data couldnt be stored in db, query is :" + query + " : " + e1);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public static boolean isInitialized() {
        return _initialized;
    }

    public static void replaceTemplate(L2NpcTemplate npc) {
        _npcs[npc.npcId] = npc;
        _npcsNames.put(npc.name.toLowerCase(), npc);
    }

    public static L2NpcTemplate getTemplate(int id) {
        return _npcs[id];
    }

    public static L2NpcTemplate getTemplateByName(String name) {
        return _npcsNames.get(name.toLowerCase());
    }

    public static GArray<L2NpcTemplate> getAllOfLevel(int lvl) {
        return _npcsByLevel[lvl];
    }

    public static L2NpcTemplate[] getAll() {
        return _npcs;
    }

    public void FillDropList() {
        for (L2NpcTemplate npc : _npcs)
            if (npc != null)
                InfoCache.addToDroplistCache(npc.npcId, DropList.generateDroplist(npc, null, 1, 1, null));
        _log.info("Players droplist was cached");
    }

    public void applyServerSideTitle() {
        if (ConfigValue.ServerSideNpcTitleWithLvl)
            for (L2NpcTemplate npc : _npcs)
                if (npc != null && npc.isInstanceOf(L2MonsterInstance.class) && !npc.isInstanceOf(L2TamedBeastInstance.class)) {
                    String title = "L" + npc.level;
                    if (npc.aggroRange != 0 || npc.factionRange != 0)
                        title += " " + (npc.aggroRange != 0 ? "A" : "") + (npc.factionRange != 0 ? "S" : "");
                    title += " ";
                    npc.title = title + npc.title;
                }
    }

    public static void storeKillsCount() {
        ThreadConnection con = null;
        FiltredStatement fs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            fs = con.createStatement();

            for (L2NpcTemplate t : NpcTable.getAll())
                if (t != null && t.killscount > 0) {
                    StringBuilder sb = new StringBuilder();
                    fs.addBatch(sb.append("REPLACE INTO `killcount` SET `npc_id`=").append(t.npcId).append(", `count`=").append(t.killscount).append(", `char_id`=-1").toString());
                }
            fs.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCS(con, fs);
        }
    }

    public void loadKillCount() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet list = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("SELECT * FROM `killcount` WHERE `char_id`=-1");
            list = statement.executeQuery();
            while (list.next()) {
                L2NpcTemplate t = NpcTable.getTemplate(list.getInt("npc_id"));
                if (t != null)
                    t.killscount = list.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, list);
        }
    }

    public static void reloadNpcDrop(int npc_id) {
        L2NpcTemplate npcDat = _npcs[npc_id];
        if (npcDat != null) {
            npcDat.clearDropData();

            ThreadConnection con = null;
            FiltredPreparedStatement statement = null;
            ResultSet rs = null;
            try {
                con = L2DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("SELECT * FROM droplist WHERE mobId='" + npc_id + "' ORDER BY category, chance DESC");
                rs = statement.executeQuery();
                L2DropData dropDat = null;

                while (rs.next()) {
                    dropDat = new L2DropData();

                    int id = rs.getInt("itemId");
                    if (ItemTemplates.getInstance().getTemplate(id).isCommonItem()) {
                        dropDat.setItemId(id);
                        dropDat.setChance(rs.getInt("chance") * ConfigValue.RateDropCommonItems);
                    } else {
                        dropDat.setItemId(id);
                        dropDat.setChance(rs.getInt("chance"));
                    }
                    dropDat.setMinDrop(rs.getLong("min"));
                    dropDat.setMaxDrop(rs.getLong("max"));
                    dropDat.setSweep(rs.getInt("sweep") == 1);
                    if (dropDat.getItem().isArrow() || dropDat.getItemId() == 1419)
                        dropDat.setGroupId(Byte.MAX_VALUE); // группа для нерейтуемых предметов, сюда же надо всякую фигню
                    else
                        dropDat.setGroupId(rs.getInt("category"));

                    if (ConfigValue.EnableModDrop) {
                        dropDat.setIsRate(rs.getBoolean("is_rate"));
                        dropDat.setIsPremium(rs.getBoolean("is_premium"));
                    }

                    npcDat.addDropData(dropDat);
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, "error reading npc drops ", e);
            } finally {
                DatabaseUtils.closeDatabaseCSR(con, statement, rs);
            }
        }
    }

    public static void unload() {
        if (_npcs != null)
            _npcs = null;
        if (ai_params != null) {
            ai_params.clear();
            ai_params = null;
        }
        if (_npcsByLevel != null)
            _npcsByLevel = null;
        if (_npcsNames != null) {
            _npcsNames.clear();
            _npcsNames = null;
        }
        if (_instance != null)
            _instance = null;
    }

    public HashMap<Integer, StatsSet> getAIParams() {
        return ai_params;
    }
}