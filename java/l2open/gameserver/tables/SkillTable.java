package l2open.gameserver.tables;

import l2open.config.*;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Skill.SkillTargetType;
import l2open.gameserver.serverpackets.FlyToLocation.FlyType;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.enums.*;
import l2open.util.GArray;
import l2open.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.io.File;
import java.sql.ResultSet;

public class SkillTable
{
	private static SkillTable _instance;

	private L2Skill[][] skills;
	private int[] _baseLevels = new int[MAX_SKILL_COUNT];
	private int[] _maxSQLLevels = new int[MAX_SKILL_COUNT];

	public static SkillTable getInstance()
	{
		if(_instance == null)
			_instance = new SkillTable();
		return _instance;
	}

	//TODO если происходит ArrayIndexOutOfBounds то поднять лимит(ы)
	public static final int MAX_SKILL_COUNT = 28000;
	public static final int MAX_SKILL_LEVELS = 259;
	static Logger _log = Logger.getLogger(SkillTable.class.getName());

	private SkillTable()
	{
		SkillsEngine.reload();
		skills = SkillsEngine.getInstance().loadAllSkills(MAX_SKILL_COUNT, MAX_SKILL_LEVELS);
		/*for(Integer id : DocumentSkill._list1.keySet())
			_log.info("id: "+id+" name: "+DocumentSkill._list1.get(id));
		_log.info("-----------------------------------");
		for(String name : DocumentSkill._list2.keySet())
			_log.info("name: "+name);*/
		loadBaseLevels();
		loadSqlSkills();
	}

	public void reload()
	{
		_instance = new SkillTable();
	}

	public L2Skill getInfo(int magicId, int level)
	{
		magicId--;
		level--;
		return magicId < 0 || level < 0 || magicId >= skills.length || skills[magicId] == null || level >= skills[magicId].length ? null : skills[magicId][level];
	}

	public L2Skill getInfo(int[] info)
	{
		int magicId = info[0]-1;
		int level = info[1]-1;
		return magicId < 0 || level < 0 || magicId >= skills.length || skills[magicId] == null || level >= skills[magicId].length ? null : skills[magicId][level];
	}

	public int getMaxLevel(int magicId)
	{
		magicId--;
		return skills[magicId] == null ? 0 : skills[magicId].length;
	}

	public L2Skill[] getAllLevels(int magicId)
	{
		magicId--;
		return skills[magicId];
	}

	public int getBaseLevel(int magicId)
	{
		return _baseLevels[magicId];
	}

	private void loadBaseLevels()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT id, MAX(level) AS level FROM skills WHERE level < 100 GROUP BY id");
			rset = statement.executeQuery();
			while(rset.next())
				_baseLevels[rset.getInt("id")] = rset.getInt("level");

			DatabaseUtils.closeDatabaseSR(statement, rset);

			statement = con.prepareStatement("SELECT id, MAX(level) AS level FROM skills GROUP BY id");
			rset = statement.executeQuery();
			while(rset.next())
				_maxSQLLevels[rset.getInt("id")] = rset.getInt("level");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private void loadSqlSkills()
	{
		new File("log/game/sql_skill_levels.txt").delete();
		new File("log/game/sql_skill_enchant_levels.txt").delete();
		new File("log/game/sql_skill_display_levels.txt").delete();
		new File("log/game/skills_not_standart.txt").delete();

		GArray<Integer> _incorrectSkills = new GArray<Integer>();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM skills ORDER BY id, level ASC");
			rset = statement.executeQuery();

			int lastid = 0, lastmaxlearn = 0;
			System.out.println("start");
			final long currentTimeMillis = System.currentTimeMillis();
			while(rset.next())
			{
				try
				{
				int id = rset.getInt("id");
				int display_level = rset.getInt("level");
				String name = rset.getString("name");
				String affect_limit = rset.getString("affect_limit");
				String fan_range = rset.getString("fan_range");

				int is_magic = rset.getInt("is_magic");
				int mp_consume1 = rset.getInt("mp_consume1");
				int mp_consume2 = rset.getInt("mp_consume2");
				int hp_consume = rset.getInt("hp_consume");
				int cast_range = rset.getInt("cast_range");
				int affect_range = rset.getInt("affect_range");
				int hit_time = rset.getInt("skill_hit_time");
				int cool_time = rset.getInt("cool_time");
				int hit_cancel_time = rset.getInt("hit_cancel_time");
				int power = rset.getInt("power");
				int learn = rset.getInt("learn");
				int reuse = rset.getInt("reuse");
				int magic_level = rset.getInt("magic_level");
				int activate_rate = rset.getInt("activate_rate");
				int effectPoint = rset.getInt("effectPoint");
				boolean isOlympiadUse = rset.getInt("olympiad_use") > 0;
				boolean isStaticHitTime = rset.getInt("static_hittime") > 0;
				boolean isStaticReuse = rset.getInt("static_reuse") > 0;
				String nextAction = rset.getString("next_action");
				String saveVs = rset.getString("basic_property");
				int lv_bonus_rate = rset.getInt("lv_bonus_rate");
				boolean is_enchant = !rset.getString("enchant").isEmpty();
				String icon = rset.getString("icon");
				
				int effective_range = rset.getInt("effective_range");
				int effect_point = rset.getInt("effect_point");
				int irreplaceable_buff = rset.getInt("irreplaceable_buff");
				int abnormal_lv = rset.getInt("abnormal_lv");
				int abnormal_time = rset.getInt("abnormal_time");
				int abnormal_instant = rset.getInt("abnormal_instant");
				int buff_protect_level = rset.getInt("buff_protect_level");
				
				SkillTrait trait = SkillTrait.valueOf(rset.getString("trait"));
				SkillAbnormalType abnormal_type = SkillAbnormalType.valueOf(rset.getString("abnormal_type"));
				String abnormal_visual_effect = rset.getString("abnormal_visual_effect");
				OperateType operate_type = OperateType.valueOf(rset.getString("operate_type"));

				TargetType target_type = TargetType.valueOf(rset.getString("target_type"));
				AffectObject affect_object = AffectObject.valueOf(rset.getString("affect_object"));
				AffectScope affect_scope = AffectScope.valueOf(rset.getString("affect_scope"));

				if(lastid != id)
				{
					lastid = id;
					lastmaxlearn = learn;
				}
				lastmaxlearn = Math.max(lastmaxlearn, learn);

				int baseLevel = _baseLevels[id];
				L2Skill base = getInfo(id, 1);
				if(base == null)
				{
					_incorrectSkills.add(id);
					Log.add("Incorrect skill base for id: " + id, "sql_skill_levels", "");
					continue;
				}

				int level = SkillTreeTable.convertEnchantLevel(baseLevel, display_level, base.getEnchantLevelCount());
				L2Skill skill = getInfo(id, level);

				if(skill == null)
				{
					if(!_incorrectSkills.contains(id))
					{
						_incorrectSkills.add(id);
						if(display_level < 100)
							Log.add("Incorrect skill levels for id: " + id + ", level = " + level + ", display_level = " + display_level, "sql_skill_levels", "");
						else
							Log.add("Not found enchant for skill id: " + id + ", level = " + level + ", display_level = " + display_level, "sql_skill_enchant_levels", "");
					}
					continue;
				}

				skill._isStandart = true;
				int maxSQL = _maxSQLLevels[id];

				for(int i = level; i <= MAX_SKILL_LEVELS && (i > maxSQL || i == level); i++)
				{
					skill = getInfo(id, i);
					if(skill == null)
						continue;

					// Загружаем реюз
					if(reuse > -1)
						skill.setReuseDelay(reuse);

					// Корректируем уровни скиллов, в основном для энчантов
					if(skill.getDisplayLevel() != display_level)
						Log.add("Incorrect display level: id = " + id + ", level = ["+skill.getDisplayLevel()+"]["+display_level+"]["+level+"]", "sql_skill_display_levels", "");

					if(skill.getPower() == 0 && power > 0 || skill.getPower() < power)
						skill.setPower(power);
					else if(power > 0 && skill.getPower() != power)
						Log.add("Incorrect power for skill id: " + id + ", level = " + level+" power=["+skill.getPower()+"]["+power+"]", "sql_skill_power", "");

					skill.setBaseLevel((short) baseLevel);

					skill.setMagicLevel(magic_level);

					String[] affect_l = affect_limit.split(";");
					skill.affect_limit_p = Integer.parseInt(affect_l[0]);
					skill.affect_limit_n = Integer.parseInt(affect_l[1]);
					skill.affect_limit_s = affect_l.length == 3 ? Integer.parseInt(affect_l[2]) : 0;

					String[] fan_r = fan_range.split(";");
					skill.fan_range_s = Integer.parseInt(fan_r[1]);
					skill.fan_range_h = Integer.parseInt(fan_r[2]);
					skill.fan_range_l = Integer.parseInt(fan_r[3]);

					skill.target_type = target_type;
					skill.affect_object = affect_object;
					skill.affect_scope = affect_scope;

					skill.setAffectRange(affect_range);

					skill.setCastRange(cast_range);

					if(skill.getFlyType() != FlyType.NONE && skill.getFlyRadius() > 0 && skill.getCastRange() > 0 && skill.getCastRange() < skill.getFlyRadius())
						skill.setCastRange(skill.getFlyRadius());

					skill.setName(name);
					
					skill.setActivateRate(activate_rate);

					if(skill.isToggle())
						skill.setHitTime(0);
					else
						skill.setHitTime(hit_time);

					skill.setCoolTime(cool_time);

					skill.setHitCancelTime(hit_cancel_time);
					
					skill.setReuseDelayPermanent(isStaticReuse);
					skill.setSkillTimePermanent(isStaticHitTime);

					skill.setIsMagic(is_magic);
					skill.setIsOlympiadUse(isOlympiadUse);
					skill.setOverhit(skill.isOverhit() || is_magic != 1 && ConfigValue.AltAllPhysSkillsOverhit);

					skill.setHpConsume(hp_consume);
					skill.setMpConsume1(mp_consume1);
					skill.setMpConsume2(mp_consume2);
					
					skill.setEffectPoint(effectPoint); //- пока врубить.
					skill.setLevelLearn(learn);
					
					skill.setTrait(trait);
					skill.setAbnormalType(abnormal_type);
					skill.setOperateType(operate_type);

					if(nextAction.equals("none"))
						skill.setNextAction(L2Skill.NextAction.NONE);
					if(nextAction.equals("attack"))
						skill.setNextAction(L2Skill.NextAction.ATTACK);

					if(saveVs.equals("int"))
						skill.setSavevs(1);
					else if(saveVs.equals("wit"))
						skill.setSavevs(2);
					else if(saveVs.equals("men"))
						skill.setSavevs(3);
					else if(saveVs.equals("con"))
						skill.setSavevs(4);
					else if(saveVs.equals("dex"))
						skill.setSavevs(5);
					else if(saveVs.equals("str"))
						skill.setSavevs(6);

					skill.setLevelModifier(lv_bonus_rate);
					skill.setEffectiveRange(effective_range);
					//skill.setEffectPoint(effect_point*-1); //- пока отрубить.
					skill.setEffPoint(effect_point);
					skill.setIrreplaceableBuff(irreplaceable_buff);
					skill.setAbnormalLv(abnormal_lv);
					skill.setIcon(icon);

					if(ConfigValue.EnableModifySkillDuration)
					{
						if(ConfigSystem.SKILL_DURATION_LIST.containsKey(id) && abnormal_time > 0)
						{
							if(display_level < 100)
								abnormal_time = ConfigSystem.SKILL_DURATION_LIST.get(id);
							else if(display_level >= 100 && display_level < 140)
								abnormal_time += ConfigSystem.SKILL_DURATION_LIST.get(id);
							else if(display_level > 140)
								abnormal_time = ConfigSystem.SKILL_DURATION_LIST.get(id);
						}
					}

					skill.setAbnormalTime(abnormal_time*1000);
					skill.setAbnormalInstant(abnormal_instant);
					skill.setBuffProtectLevel(buff_protect_level);

					if(abnormal_visual_effect.contains(";"))
					{
						String[] param = abnormal_visual_effect.split(";");
						skill.abnormal_visual_effect = AbnormalVisualEffect.valueOf(param[0]);
						skill.abnormal_visual_effect2 = AbnormalVisualEffect.valueOf(param[1]);
					}
					else
						skill.abnormal_visual_effect = AbnormalVisualEffect.valueOf(abnormal_visual_effect);

					if(skill.getTargetType() == SkillTargetType.TARGET_ALLY || skill.getTargetType() == SkillTargetType.TARGET_CLAN || skill.getTargetType() == SkillTargetType.TARGET_PARTY || skill.getTargetType() == SkillTargetType.TARGET_CLAN_ONLY || skill.getTargetType() == SkillTargetType.TARGET_COMMAND_CHANEL)
					{
						if(affect_range < 1 && display_level == 1)
							_log.info("L2Skill1: ["+id+"]["+display_level+"]["+skill.getTargetType()+"]");
					}
					if(skill.getTargetType() == SkillTargetType.TARGET_AURA || skill.getTargetType() == SkillTargetType.TARGET_MULTIFACE_AURA)
					{
						if(affect_range < 1 && display_level == 1 /*&& skill.fan_range_h < 1*/)
							_log.info("L2Skill2: ["+id+"]["+display_level+"]["+skill.getTargetType()+"]");
					}
					if(skill.getTargetType() == SkillTargetType.TARGET_AREA || skill.getTargetType() == SkillTargetType.TARGET_AREA_AIM_CORPSE || skill.getTargetType() == SkillTargetType.TARGET_MULTIFACE || skill.getTargetType() == SkillTargetType.TARGET_TUNNEL)
					{
						if(affect_range < 1 && display_level == 1 && skill.fan_range_h < 1)
							_log.info("L2Skill3: ["+id+"]["+display_level+"]["+skill.getTargetType()+"]");
					}
					if(skill.affect_scope == AffectScope.fan && display_level == 1)
					{
						//if(affect_range < 1 && display_level == 1 && skill.fan_range_h < 1)
						if(skill.getTargetType() != SkillTargetType.TARGET_AREA && skill.getTargetType() != SkillTargetType.TARGET_AURA && skill.getTargetType() != SkillTargetType.TARGET_NONE && skill.getTargetType() != SkillTargetType.TARGET_MULTIFACE)
							_log.info("L2Skill4: ["+id+"]["+display_level+"]["+skill.getTargetType()+"]["+skill.target_type+"]["+skill.affect_object+"]");
					}
					if(skill.target_type == TargetType.self && display_level == 1)
					{
						//if(affect_range < 1 && display_level == 1 && skill.fan_range_h < 1)
						if(skill.getTargetType() != SkillTargetType.TARGET_SELF && skill.getTargetType() != SkillTargetType.TARGET_NONE && skill.getTargetType() != SkillTargetType.TARGET_AURA && skill.getTargetType() != SkillTargetType.TARGET_PARTY && skill.getTargetType() != SkillTargetType.TARGET_CLAN && skill.getTargetType() != SkillTargetType.TARGET_OWNER && skill.getTargetType() != SkillTargetType.TARGET_ITEM && skill.getTargetType() != SkillTargetType.TARGET_TUNNEL_SELF && skill.getTargetType() != SkillTargetType.TARGET_COMMAND_CHANEL && skill.getTargetType() != SkillTargetType.TARGET_CLAN_ONLY  )
							_log.info("L2Skill5: ["+id+"]["+display_level+"]["+skill.getTargetType()+"]["+skill.affect_scope+"]["+skill.affect_object+"]");
					}
				}
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}


			System.out.println("finish");
			System.out.println(System.currentTimeMillis() - currentTimeMillis);

			for(L2Skill[] sl : skills)
				if(sl != null)
					for(L2Skill s : sl)
						if(s != null)
							if(!s._isStandart)
							{
								//Log.add("INSERT INTO `skills` VALUES ('"+s.getId()+"', '"+s.getLevel()+"', '0', 'A2', '"+s.getName()+"', '1', '20', '-1', '1', '0', '0', '0', '-1', '0', '0', '500', '0', '', '0', '1', '1', '1', '"+s.getAbnormalTime2()+"', 's_"+s.getId()+"', '"+s.getLevel()+"', '0', '0', 'trait_none', '0', 'none', 'none', '-1', '1', '0', 'ave_none', 'icon.skill"+s.getId()+"');", "skills_not_standart", "");
								//Log.add("	s_" + s.getId() + ",", "skills_not_standart", "");
								Log.add("Not found SQL skill id: " + s.getId() + ", level = " + s.getLevel() + ", display " + s.getDisplayLevel(), "skills_not_standart", "");
							}
							//else if(s.getLevel() == 1)
							//	break;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public List<L2Skill> getSkills(L2Skill.SkillType skillType){
		List<L2Skill> l2Skills = new ArrayList<>();
		for (L2Skill[] sl : skills){
			if(sl != null){
				for (L2Skill skill: sl){
					if (skill.getSkillType() == skillType){
						l2Skills.add(skill);
					}
				}
			}
		}
		return l2Skills;
	}


	/*
	operate_type
	0 - в основном физ.
	1 - в основном маг.
	2 - в основном селфы, иногда бафы.
	3 - дебафы.
	4 - герои, нублесы, скиллы захвата замка.
	5 - рыбные, предметные, аугментация, трансформация, SA.
	6 - ауры
	7 - трансформации
	11 - пассивки игроков
	12 - еще пассивки игроков, и мобов
	13 - спец. пассивки игроков, характерные только для конкретной расы. Расы мобов.
	14 - аналогично, только еще Divine Inspiration, пассивные Final скиллы, и всякая фигня намешана
	15 - клановые скиллы, скиллы фортов
	16 - сеты, эпики, SA, аугментация, все пассивное
	
	OP_PASSIVE: 11, 12, 13, 14, 15, 16
	OP_ACTIVE: 0, 1, 2, 3, 4, 5, 7
	OP_TOGGLE: 6
	OP_ON_ATTACK: 5
	OP_ON_CRIT: 5
	OP_ON_MAGIC_ATTACK: 5
	OP_ON_UNDER_ATTACK: 5
	OP_ON_MAGIC_SUPPORT: 5
	*/

	public static void unload()
	{
		if(_instance != null)
			_instance = null;
	}
}