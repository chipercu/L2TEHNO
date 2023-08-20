package communityboard.manager;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.*;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.barahlo.CBBuffSch;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.serverpackets.MagicSkillLaunched;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.ResultSet;
import java.util.*;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class BuffBBSManager extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	static final Logger _log = Logger.getLogger(BuffBBSManager.class.getName());
	private static BuffBBSManager _instance = null;
	private static List<Integer> _bfList = new ArrayList<Integer>();

	private static enum Commands
	{
		_bbsbuff
	}

	private static int[] notSaveBuff = { 0 };
	private static int[][] buffs = {
			{ 1251, 2, 5 },
			{ 1252, 3, 5 },
			{ 1253, 3, 5 },
			{ 1284, 3, 5 },
			{ 1308, 3, 5 },
			{ 1309, 3, 5 },
			{ 1310, 4, 5 },
			{ 1362, 1, 5 },
			{ 1363, 1, 5 },
			{ 1390, 3, 5 },
			{ 1391, 3, 5 },
			{ 264, 1, 4 },
			{ 265, 1, 4 },
			{ 266, 1, 4 },
			{ 267, 1, 4 },
			{ 268, 1, 4 },
			{ 269, 1, 4 },
			{ 270, 1, 4 },
			{ 304, 1, 4 },
			{ 305, 1, 4 },
			{ 306, 1, 4 },
			{ 308, 1, 4 },
			{ 349, 1, 4 },
			{ 363, 1, 4 },
			{ 364, 1, 4 },
			{ 271, 1, 3 },
			{ 272, 1, 3 },
			{ 273, 1, 3 },
			{ 274, 1, 3 },
			{ 275, 1, 3 },
			{ 276, 1, 3 },
			{ 277, 1, 3 },
			{ 307, 1, 3 },
			{ 309, 1, 3 },
			{ 310, 1, 3 },
			{ 311, 1, 3 },
			{ 365, 1, 3 },
			{ 7059, 1, 2 },
			{ 4356, 3, 2 },
			{ 4355, 3, 2 },
			{ 4352, 1, 2 },
			{ 4346, 4, 2 },
			{ 4351, 6, 2 },
			{ 4342, 2, 2 },
			{ 4347, 6, 2 },
			{ 4348, 6, 2 },
			{ 4344, 3, 2 },
			{ 7060, 1, 2 },
			{ 4350, 4, 2 },
			{ 7057, 1, 1 },
			{ 4345, 3, 1 },
			{ 4344, 3, 1 },
			{ 4349, 2, 1 },
			{ 4342, 2, 1 },
			{ 4347, 6, 1 },
			{ 4357, 2, 1 },
			{ 4359, 3, 1 },
			{ 4358, 3, 1 },
			{ 4360, 3, 1 },
			{ 4354, 4, 1 },
			{ 4346, 4, 1 } };

	public static BuffBBSManager getInstance()
	{
		return _instance;
	}

	private boolean confirmBuff(String path, int skill_id, int skill_lvl)
	{
		long ptsId = skill_id * 65536 + skill_lvl;
		LineNumberReader lnr = null;
		boolean conf = false;
		if(!Files.cacheCBSkill.containsKey(ptsId))
		{
			try
			{
				File data = new File(ConfigValue.DatapackRoot, ConfigValue.CommunityBoardHtmlRoot + path + ".htm");
				lnr = new LineNumberReader(new FileReader(data));
				String line;
				while((line = lnr.readLine()) != null)
				{
					int index = line.indexOf("_bbsbuff");
					if(line.startsWith("_bbsbuff;buff;" + path + ";" + skill_id + ";" + skill_lvl + ";", index))
					{
						conf = true;
						Files.cacheCBSkill.put(ptsId, true);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(lnr != null)
						lnr.close();
				}
				catch(Exception e1)
				{ /* ignore problems */}
				if(!conf)
					Files.cacheCBSkill.put(ptsId, false);
			}
		}
		else if(Files.cacheCBSkill.get(ptsId))
			conf = true;
		return conf;
	}

	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(!ConfigValue.AllowCBBInAbnormalState)
		{
			if(!player.isGM() && (player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isInVehicle() || player.isFlying() || player.isInFlyingTransform()))
			{
				FailBBSManager.getInstance().parsecmd(command, player);
				return;
			}
		}

		if(ConfigValue.BufferAffterRes)
		{
			long time = (player.getResTime() + (ConfigValue.BufferAffterResTime * 1000) - System.currentTimeMillis());
			if(time > 0)
			{
				int wait = (int) (time / 1000);
				player.sendMessage(new CustomMessage("common.not.yet.wait", player).addNumber(wait <= 0 ? 1 : wait).addString(DifferentMethods.declension(player, wait, "Second")));
				return;
			}
		}

		if(player.is_block)
			return;
		if(!check(player))
			return;

		if(command.equals("_bbsbuff;"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "60.htm", player);
			content = content.replace("%sch%", showBuffList(player));
			separateAndSend(addCustomReplace(content), player);
		}
		else if(command.startsWith("_bbsbuff;buff;"))
		{
			StringTokenizer stBuff = new StringTokenizer(command, ";");
			String BuffTarget = "";
			String path = "";
			int skill_id = 0;
			int skill_lvl = 0;
			try
			{
				stBuff.nextToken();
				stBuff.nextToken();
				path = stBuff.nextToken();
				skill_id = Integer.parseInt(stBuff.nextToken());
				skill_lvl = Integer.parseInt(stBuff.nextToken());
				BuffTarget = stBuff.nextToken();
			}
			catch(NoSuchElementException e)
			{
				BuffTarget = " Player";
			}
			if(confirmBuff(path, skill_id, skill_lvl))
				doBuff(skill_id, skill_lvl, BuffTarget, player);
			else
				Log.IllegalPlayerAction(player, "This player: " + player.getName() + " is cheater, please baned.", 0);
		}
		else if(command.startsWith("_bbsbuff;grp;"))
		{
			StringTokenizer stBuffGrp = new StringTokenizer(command, ";");
			String BuffTarget = "";
			int id_groups = 0;
			try
			{
				stBuffGrp.nextToken();
				stBuffGrp.nextToken();
				id_groups = Integer.parseInt(stBuffGrp.nextToken());
				BuffTarget = stBuffGrp.nextToken();
			}
			catch(NoSuchElementException e)
			{
				BuffTarget = " Player";
			}
			doBuffGroup(id_groups, BuffTarget, player);
		}
		else if(command.equals("_bbsbuff;cancel"))
		{
			if(!checkCondition(player))
				return;
			L2Summon pet = player.getPet();
			if(player.getEffectList().getEffectsBySkillId(L2Skill.SKILL_RAID_CURSE) == null && player.getEffectList().getEffectsBySkillId(5076) == null)
			{
				for(L2Effect e : player.getEffectList().getAllCancelableEffects(0))
					if(e != null)
						e.exit(false, false);
				player.updateEffectIcons();
				player.setMassUpdating(false);
				player.sendChanges();
				player.updateEffectIcons();
			}
			//player.getEffectList().stopAllEffects();
			if(pet != null)
				if(pet.getEffectList().getEffectsBySkillId(L2Skill.SKILL_RAID_CURSE) == null && pet.getEffectList().getEffectsBySkillId(5076) == null)
				{
					for(L2Effect e : pet.getEffectList().getAllCancelableEffects(0))
						if(e != null)
							e.exit(false, false);
					pet.updateEffectIcons();
					pet.setMassUpdating(false);
					pet.sendChanges();
					pet.updateEffectIcons();
				}
		}
		else if(command.equals("_bbsbuff;regmp"))
		{
			if(!checkCondition(player) || player.isInEvent() == 5)
				return;
			player.setCurrentMp(player.getMaxMp());
		}
		else if(command.equals("_bbsbuff;regmpPet"))
		{
			if(!checkCondition(player) || player.isInEvent() == 5)
				return;
			L2Summon pet = player.getPet();
			if(pet != null)
				pet.setCurrentMp(pet.getMaxMp());
		}
		else if(command.equals("_bbsbuff;reghp"))
		{
			if(!checkCondition(player) || player.isInEvent() == 5)
				return;
			player.setCurrentHp(player.getMaxHp(), false);
		}
		else if(command.equals("_bbsbuff;reghpPet"))
		{
			if(!checkCondition(player) || player.isInEvent() == 5)
				return;
			L2Summon pet = player.getPet();
			if(pet != null)
				pet.setCurrentHp(pet.getMaxHp(), false);
		}
		else if(command.equals("_bbsbuff;regcp"))
		{
			if(!checkCondition(player) || player.isInEvent() == 5)
				return;
			player.setCurrentCp(player.getMaxCp());
		}
		else if(command.equals("_bbsbuff;regall"))
		{
			if(!checkCondition(player) || player.isInEvent() == 5)
				return;
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp(), false);
			player.setCurrentMp(player.getMaxMp());
			L2Summon pet = player.getPet();
			if(pet != null)
			{
				pet.setCurrentHp(pet.getMaxHp(), false);
				pet.setCurrentMp(pet.getMaxMp());
			}
		}
		else if(command.equals("_bbsbuff;regallPet"))
		{
			if(!checkCondition(player) || player.isInEvent() == 5)
				return;
			L2Summon pet = player.getPet();
			if(pet != null)
			{
				pet.setCurrentHp(pet.getMaxHp(), false);
				pet.setCurrentMp(pet.getMaxMp());
			}
		}
		else if(command.startsWith("_bbsbuff;save;"))
		{
			if(!ConfigValue.restoreBuff)
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.communitybbs.Manager.BuffBBSManager.SaveBuff", player));
				return;
			}
			StringTokenizer stAdd = new StringTokenizer(command, ";");
			stAdd.nextToken();
			stAdd.nextToken();
			String SchNameAdd = null;
			if(stAdd.hasMoreTokens())
				SchNameAdd = stAdd.nextToken().trim();
			if(SchNameAdd == null || SchNameAdd.isEmpty())
			{
				player.sendMessage("Вы не ввели имя закладки.");
				return;
			}
			else if(!Util.isMatchingRegexp(SchNameAdd, "([0-9A-Za-z]{1,16})|([0-9\u0410-\u044f]{1,16})"))
			{
				player.sendMessage("Символы запрещены.");
				return;
			}
			SAVE(player, SchNameAdd);
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "60.htm", player);
			content = content.replace("%sch%", showBuffList(player));
			separateAndSend(addCustomReplace(content), player);
		}
		else if(command.startsWith("_bbsbuff;restore;"))
		{
			if(!ConfigValue.restoreBuff)
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.communitybbs.Manager.BuffBBSManager.RestorBuff", player));
				return;
			}
			StringTokenizer stBuff = new StringTokenizer(command, ";");
			stBuff.nextToken();
			stBuff.nextToken();
			int schameId = Integer.parseInt(stBuff.nextToken());
			String BuffTarget = stBuff.hasMoreTokens() ? stBuff.nextToken() : " Player";
			RESTOR(player, schameId, BuffTarget);
		}
		else if(command.startsWith("_bbsbuff;delete;"))
		{
			try
			{
				StringTokenizer stBuff = new StringTokenizer(command, ";");
				stBuff.nextToken();
				stBuff.nextToken();
				int schameId = Integer.parseInt(stBuff.nextToken());
				delschame(player, schameId);
				String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "60.htm", player);
				content = content.replace("%sch%", showBuffList(player));
				separateAndSend(addCustomReplace(content), player);
			}
			catch(Exception e)
			{}
		}
		else
			ShowBoard.separateAndSend("<html><body><br><br><center>В bbsbuff функция: " + command + " пока не реализована</center><br><br></body></html>", player);
	}

	public void doBuff(int skill_id, int skill_lvl, String BuffTarget, L2Player player)
	{
		L2Summon pet = player.getPet();

		if(!checkCondition(player))
			return;

		if(player.getLevel() > ConfigValue.LevelFreeBuff)
			if(player.getAdena() < ConfigValue.OneBuffPrice)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

		try
		{
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(Util.contains(ConfigValue.BufferOnlyPaBuffs, skill.getId()) && !canBuff(player))
				return;

			if(BuffTarget.startsWith(" Player"))
				buffSkill(player, skill);
			else if(BuffTarget.startsWith(" Pet") && pet != null)
				buffSkill(pet, skill);
			if(player.getLevel() > ConfigValue.LevelFreeBuff)
				player.reduceAdena(ConfigValue.OneBuffPrice, true);
			if(BuffTarget.startsWith(" Player"))
				player.updateEffectIcons();
			else if(pet != null)
				pet.updateEffectIcons();
		}
		catch(Exception e)
		{
			player.sendMessage("Invalid skill!");
		}
	}

	public void doBuffGroup(int id_groups, String BuffTarget, L2Player player)
	{
		L2Summon pet = player.getPet();

		if(!checkCondition(player))
			return;

		if(player.getLevel() > ConfigValue.LevelFreeBuff)
			if(player.getAdena() < ConfigValue.OneBuffPrice * ConfigValue.GroupBuffPriceModifier)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		if(player.getLevel() > ConfigValue.LevelFreeBuff)
			player.reduceAdena((ConfigValue.OneBuffPrice * ConfigValue.GroupBuffPriceModifier), true);

		for(int[] buff : buffs)
		{
			if(buff[2] != id_groups)
				continue;
			L2Skill skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
			if(BuffTarget.startsWith(" Player"))
				buffSkill(player, skill);
			else if(BuffTarget.startsWith(" Pet") && pet != null)
				buffSkill(pet, skill);
		}
		if(BuffTarget.startsWith(" Player"))
			player.updateEffectIcons();
		else if(pet != null)
			pet.updateEffectIcons();
	}

	private String showBuffList(L2Player player)
	{
		StringBuilder html = new StringBuilder();
		html.append("<table width=150>");
		for(CBBuffSch sch : player._buffSchem.values())
		{
			html.append("<tr>");
			html.append("<td>");
			html.append("<button value=\"" + sch.SchName + "\" action=\"bypass -h _bbsbuff;restore;" + sch.id + "; $tvari \" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			html.append("</td>");
			html.append("<td>");
			html.append("<button value=\"Удалить\" action=\"bypass -h _bbsbuff;delete;" + sch.id + "\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			html.append("</td>");
			html.append("</tr>");
		}
		html.append("</table>");
		return html.toString();
	}

	private void delschame(L2Player player, int shameid)
	{
		player._buffSchem.remove(shameid);
		ThreadConnection conDel = null;
		FiltredPreparedStatement statementDel = null;
		try
		{
			conDel = L2DatabaseFactory.getInstance().getConnection();
			statementDel = conDel.prepareStatement("DELETE FROM community_skillsave WHERE charId=? AND schameid=?");
			statementDel.setInt(1, player.getObjectId());
			statementDel.setInt(2, shameid);
			statementDel.execute();
		}
		catch(Exception e)
		{
			_log.warning("data error on Delete Teleport: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeConnection(conDel);
		}
	}

	private void SAVE(L2Player player, String SchName)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			if(player._buffSchem.size() < ConfigValue.maxBuffSchem)
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				String allbuff = "";
				L2Effect skill[] = player.getEffectList().getAllFirstEffects();
				long[] sch = new long[0];
				boolean _name = true;

				for(int i = 0; i < skill.length; i++)
				{
					long ptsId = skill[i].getSkill().getId() * 65536 + skill[i].getSkill().getLevel();
					allbuff = new StringBuilder().append(allbuff).append(ptsId + ";").toString();
					sch = ArrayUtils.add(sch, ptsId);
				}

				for(CBBuffSch sch1 : player._buffSchem.values())
					if(sch1.SchName.equalsIgnoreCase(SchName))
						_name = false;
				if(_name)
				{
					statement = con.prepareStatement("INSERT INTO community_skillsave (charId,name,skills) VALUES(?,?,?)");
					statement.setInt(1, player.getObjectId());
					statement.setString(2, SchName);
					statement.setString(3, allbuff);
					statement.execute();

					statement = con.prepareStatement("SELECT schameid FROM community_skillsave WHERE charId=? AND name=?;");
					statement.setInt(1, player.getObjectId());
					statement.setString(2, SchName);
					rs = statement.executeQuery();
					rs.next();
					int id = rs.getInt(1);
					player._buffSchem.put(id, new CBBuffSch(id, SchName, sch));
					sch = null;
				}
				else
					player.sendMessage("Это название уже занято.");
			}
			else
				player.sendMessage("Вы уже сохранили максимальное количество схем.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	public void RESTOR(L2Player player, int schameId, String BuffTarget)
	{
		L2Summon pet = player.getPet();

		if(!checkCondition(player))
			return;

		if(player.getLevel() > ConfigValue.LevelFreeBuff)
			if(player.getAdena() < ConfigValue.OneBuffPrice * ConfigValue.GroupBuffPriceModifier)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		if(player.getLevel() > ConfigValue.LevelFreeBuff)
			player.reduceAdena((ConfigValue.OneBuffPrice * ConfigValue.GroupBuffPriceModifier), true);

		L2Skill skill;
		if(player._buffSchem != null && player._buffSchem.containsKey(schameId))
		{
			for(long ptsId : player._buffSchem.get(schameId)._buffList)
			{
				int skillId = 0;
				int level = 0;
				if(ptsId > 65536)
				{
					skillId = (int) (ptsId / 65536);
					level = (int) (ptsId - skillId * 65536);
					int lvl = SkillTable.getInstance().getBaseLevel(skillId);
					if(lvl < level)
						level = lvl;
				}
				else
				{
					// Для совместимости со старыми схемами.
					skillId = (int) ptsId;
					level = SkillTable.getInstance().getBaseLevel((int) ptsId);
				}
				skill = SkillTable.getInstance().getInfo(skillId, level);
				if(_bfList.contains(skillId))
				{
					if(BuffTarget.startsWith(" Player"))
						buffSkill(player, skill);
					else if(BuffTarget.startsWith(" Pet") && pet != null)
						buffSkill(pet, skill);
				}
			}
		}
		if(BuffTarget.startsWith(" Player"))
			player.updateEffectIcons();
		else if(pet != null)
			pet.updateEffectIcons();
	}

	public boolean checkCondition(L2Player player)
	{
		if(player == null)
			return false;
		else if(player.isGM() || player.isInEvent() > 0 && check_event(player) && !player.isDead())
			return true;
		else if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return false;
		}
		else if(player.getReflectionId() != 0 && !ConfigValue.ALlowCBBufferInInstance && !check_event(player))
		{
			player.sendMessage("Бафф доступен только в обычном мире.");
			return false;
		}
		else if(!ConfigValue.pvpBoardBuffer)
		{
			player.sendMessage("Функция баффа отключена.");
			return false;
		}
		else if(player.getLevel() > ConfigValue.CommBufferMaxLvl || player.getLevel() < ConfigValue.CommBufferMinLvl)
		{
			player.sendMessage("Ваш уровень не отвечает требованиям!");
			return false;
		}
		else if(!ConfigValue.AllowCBBufferOnEvent && player.getTeam() > 0 && player.isInEvent() != 5 && !check_event(player))
		{
			player.sendMessage("Нельзя использовать бафф во время эвентов.");
			return false;
		}
		else if(!ConfigValue.AllowCBBufferOnSiege)
		{
			Residence castle = TownManager.getInstance().getClosestTown(player).getCastle();
			Siege siege = castle.getSiege();
			if(siege != null && siege.isInProgress())
			{
				player.sendMessage("Нельзя использовать бафф во время осады.");
				return false;
			}
		}
		return true;
	}

	private void buffSkill(L2Playable playable, L2Skill buff)
	{
		if(Util.contains(ConfigValue.BufferOnlyPaBuffs, buff.getId()) && !canBuff(playable.getPlayer()))
			return;
		int buffTime = 0;
		final double hp = playable.getCurrentHp();
		final double mp = playable.getCurrentMp();
		final double cp = playable.getCurrentCp();
		if(buff != null)
			buffTime = buff.isMusic() ? ConfigValue.DanceAndSongTime : ConfigValue.BuffTime;
		if(!buff.checkSkillAbnormal(playable) && !buff.isBlockedByChar(playable, buff))
		{
			for(EffectTemplate et : buff.getEffectTemplates())
			{
				int result;
				Env env = new Env(playable, playable, buff);
				L2Effect effect = et.getEffect(env);
				if(effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle())
				{
					// Эффекты однократного действия не шедулятся, а применяются немедленно
					// Как правило это побочные эффекты для скиллов моментального действия
					effect.onStart();
					effect.onActionTime();
					effect.onExit();
				}
				else if(effect != null && !effect.getEffected().p_block_buff.get())
				{
					if(buffTime > 0)
						effect.setPeriod(buffTime);
					if((result = playable.getEffectList().addEffect(effect)) > 0)
					{
						if((result & 2) == 2)
							playable.setCurrentHp(hp, false);
						if((result & 4) == 4)
							playable.setCurrentMp(mp);
						if((result & 8) == 8)
							playable.setCurrentCp(cp);
					}
				}
			}
		}
	}

	private void loadBuff()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT skillID FROM `communitybuff`");
			rs = statement.executeQuery();
			while(rs.next())
				_bfList.add(rs.getInt("skillID"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{}

	public class EndPetBuff extends l2open.common.RunnableImpl
	{
		L2Character _buffer;
		L2Skill _skill;
		L2Summon _target;

		public EndPetBuff(L2Character buffer, L2Skill skill, L2Summon target)
		{
			_buffer = buffer;
			_skill = skill;
			_target = target;
		}

		public void runImpl()
		{
			_skill.getEffects(_buffer, _target, false, false);
			_buffer.broadcastSkill(new MagicSkillLaunched(_buffer.getObjectId(), _skill.getId(), _skill.getLevel(), _target, _skill.isOffensive()), true);
		}
	}

	public class BeginPetBuff extends l2open.common.RunnableImpl
	{
		L2Character _buffer;
		L2Skill _skill;
		L2Summon _target;

		public BeginPetBuff(L2Character buffer, L2Skill skill, L2Summon target)
		{
			_buffer = buffer;
			_skill = skill;
			_target = target;
		}

		public void runImpl()
		{
			_buffer.broadcastSkill(new MagicSkillUse(_buffer, _target, _skill.getDisplayId(), _skill.getLevel(), 0, 0), true);
			ThreadPoolManager.getInstance().schedule(new BuffBBSManager.EndPetBuff(_buffer, _skill, _target), 0);
		}
	}

	public class EndBuff extends l2open.common.RunnableImpl
	{
		L2Character _buffer;
		L2Skill _skill;
		L2Player _target;

		public EndBuff(L2Character buffer, L2Skill skill, L2Player target)
		{
			_buffer = buffer;
			_skill = skill;
			_target = target;
		}

		public void runImpl()
		{
			_skill.getEffects(_buffer, _target, false, false);
			_buffer.broadcastSkill(new MagicSkillLaunched(_buffer.getObjectId(), _skill.getId(), _skill.getLevel(), _target, _skill.isOffensive()), true);
		}
	}

	public class BeginBuff extends l2open.common.RunnableImpl
	{
		L2Character _buffer;
		L2Skill _skill;
		L2Player _target;

		public BeginBuff(L2Character buffer, L2Skill skill, L2Player target)
		{
			_buffer = buffer;
			_skill = skill;
			_target = target;
		}

		public void runImpl()
		{
			if(_target.isInOlympiadMode())
				return;
			_buffer.broadcastSkill(new MagicSkillUse(_buffer, _target, _skill.getDisplayId(), _skill.getLevel(), 0, 0), true);
			ThreadPoolManager.getInstance().schedule(new BuffBBSManager.EndBuff(_buffer, _skill, _target), 0);
		}
	}

	/**
	 * <b>1</b> - <font color=red>Fight Club</font><br> 
	 * <b>2</b> - <font color=red>Last Hero</font><br> 
	 * <b>3</b> - <font color=red>Capture The Flag</font><br> 
	 * <b>4</b> - <font color=red>Team vs Team</font><br>
	 * <b>5</b> - <font color=red>Tournament</font><br>
	 **/
	private boolean check_event(L2Player player)
	{
		switch(player.isInEvent())
		{
			case 1:
				return ConfigValue.FightClubBattleUseBuffer;
			case 2:
				return ConfigValue.LastHeroBattleUseBuffer;
			case 3:
				return ConfigValue.CaptureTheFlagBattleUseBuffer;
			case 4:
				return ConfigValue.TeamvsTeamBattleUseBuffer;
			case 5:
				return ConfigValue.TournamentBattleUseBuffer;
			case 6:
				return ConfigValue.EventBoxUseBuffer;
			case 11:
				return player.getEventMaster().state == 1;
			case 12:
				return ConfigValue.Tournament_UseBuffer;
			case 13:
				return ConfigValue.DeathMatchUseBuffer;
		}
		return ConfigValue.OtherEventUseBuffer;
	}

	private boolean check(L2Player player)
	{
		if(player == null)
			return false;
		else if(player.isGM() || player.isInEvent() > 0 && check_event(player) && !player.isDead())
			return true;
		else if(player.isInOlympiadMode())
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.getReflection().getId() != ReflectionTable.DEFAULT && !ConfigValue.ALlowCBBufferInInstance && !check_event(player))
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isInDuel())
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isInCombat() && !ConfigValue.BufferInCombat && !check_event(player))
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if((player.isOnSiegeField() || player.isInZoneBattle()) && !ConfigValue.BufferOnSiege && player.isInEvent() != 5 && !check_event(player))
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isInEvent() > 0 && !check_event(player))
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isFlying())
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isInWater() && !ConfigValue.BufferInWater)
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isDead() || player.isMovementDisabled() || player.isAlikeDead() || player.isCastingNow() || player.isAttackingNow() || player.getVar("jailed") != null || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(ConfigValue.BufferOnlyPeace && !player.isInZone(ZoneType.peace_zone) && !player.isInZone(ZoneType.epic) && player.getReflection().getId() == ReflectionTable.DEFAULT)
		{
			player.sendMessage("Функция доступна только в мирной зоне, эпик зоне, а так же в инстансах.");
			return false;
		}
		else
			return true;
	}

	private boolean canBuff(L2Player player)
	{
		return ConfigValue.BufferUsePremiumItem <= 0 && player.hasBonus() || ConfigValue.BufferUsePremiumItem > 0 && player.getInventory().getCountOf(ConfigValue.BufferUsePremiumItem) > 0;
	}

	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
		loadBuff();
	}

	public void onReload()
	{
		_bfList.clear();
		loadBuff();
	}

	public void onShutdown()
	{}

	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}
}