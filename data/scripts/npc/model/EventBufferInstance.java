package npc.model;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Files;
import l2open.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.ResultSet;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public final class EventBufferInstance extends L2NpcInstance
{
	static final Logger _log = Logger.getLogger(EventBufferInstance.class.getName());

	public EventBufferInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	private static int[] notSaveBuff = {0};
	private static int[][] buffs = 
	{
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
		{ 4346, 4, 1 } 
	};

	public class CBBuffSch
	{
		public int id = 0;
		public String SchName = "";
		public int PlayerId = 0;
	}

	public void onAction(L2Player player, boolean shift)
	{
		if(player.getTarget() != this)
		{
			player.setTarget(this);
			if(player.getTarget() == this)
				player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
			player.sendPacket(new ValidateLocation(this), Msg.ActionFail);
			return;
		}
	
		if(!isInRange(player, INTERACTION_DISTANCE))
		{
			if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
			return;
		}

		showChatWindow(player, getTemplate().npcId+".htm");
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom;
		if(val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;
		String temp = "data/html/buffer/" + pom + ".htm";
		File mainText = new File(temp);
		if(mainText.exists())
			return temp;
		return "data/html/npcdefault.htm";
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename = getHtmlPath(getTemplate().npcId, val);
		player.setLastFile(filename);
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		String content = Files.read(filename, player);
		content = content.replace("%sch%", showBuffList(player));
		html.setHtml(content);
		player.sendPacket(html);
	}

	@Override
	public void showChatWindow(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		String content = Files.read("data/html/buffer/"+file, player);
		player.setLastFile("data/html/buffer/"+file);
		content = content.replace("%sch%", showBuffList(player));
		html.setHtml(content);
		player.sendPacket(html);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(command.startsWith("buff;"))
		{
			StringTokenizer stBuff = new StringTokenizer(command, ";");
			String BuffTarget = "";
			int path = 0;
			int skill_id = 0;
			int skill_lvl = 0;
			try
			{
				stBuff.nextToken();
				path = Integer.parseInt(stBuff.nextToken());
				skill_id = Integer.parseInt(stBuff.nextToken());
				skill_lvl = Integer.parseInt(stBuff.nextToken());
				BuffTarget = stBuff.nextToken();
			}
			catch(NoSuchElementException e)
			{
				BuffTarget = " Player";
			}
			//if(confirmBuff(path, skill_id, skill_lvl))
				doBuff(skill_id, skill_lvl, BuffTarget, player);
			//else
			//	Log.IllegalPlayerAction(player, "This player: " + player.getName() + " is cheater, please baned.", 0);
		}
		else if(command.startsWith("grp;"))
		{
			StringTokenizer stBuffGrp = new StringTokenizer(command, ";");
			String BuffTarget = "";
			int id_groups = 0;
			try
			{
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
		else if(command.equals("cancel"))
		{
			if(!checkCondition(player))
				return;
			L2Summon pet = player.getPet();
			if(player.getEffectList().getEffectsBySkillId(L2Skill.SKILL_RAID_CURSE) == null)
			    player.getEffectList().stopAllEffects();
			if(pet != null)
				if(pet.getEffectList().getEffectsBySkillId(L2Skill.SKILL_RAID_CURSE) == null)
					pet.getEffectList().stopAllEffects();
		}
		else if(command.equals("regmp"))
		{
			if(!checkCondition(player))
				return;
			player.setCurrentMp(player.getMaxMp());
		}
		else if(command.equals("regmpPet"))
		{
			if(!checkCondition(player))
				return;
			L2Summon pet = player.getPet();
			if(pet != null)
				pet.setCurrentMp(pet.getMaxMp());
		}
		else if(command.equals("reghp"))
		{
			if(!checkCondition(player))
				return;
			player.setCurrentHp(player.getMaxHp(), false);
		}
		else if(command.equals("reghpPet"))
		{
			if(!checkCondition(player))
				return;
			L2Summon pet = player.getPet();
			if(pet != null)
				pet.setCurrentHp(pet.getMaxHp(), false);
		}
		else if(command.startsWith("save;"))
		{
			if(!ConfigValue.restoreBuff)
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.communitybbs.Manager.EventBufferInstance.SaveBuff", player));
				return;
			}
			StringTokenizer stAdd = new StringTokenizer(command, ";");
			stAdd.nextToken();
			String SchNameAdd = null;
			if(stAdd.hasMoreTokens())
				SchNameAdd = stAdd.nextToken();
			if(SchNameAdd == null || SchNameAdd.equals(""))
			{
				player.sendMessage("Вы не ввели имя закладки.");
				return;
			}
			SAVE(player, SchNameAdd);
		}
		else if(command.startsWith("restore;"))
		{
			if(!ConfigValue.restoreBuff)
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.communitybbs.Manager.EventBufferInstance.RestorBuff", player));
				return;
			}
			StringTokenizer stBuff = new StringTokenizer(command, ";");
			stBuff.nextToken();
			int schameId = Integer.parseInt(stBuff.nextToken());
			String BuffTarget = stBuff.nextToken();
			RESTOR(player, schameId, BuffTarget);
		}
		else if(command.startsWith("delete;"))
		{
			StringTokenizer stBuff = new StringTokenizer(command, ";");
			stBuff.nextToken();
			int schameId = Integer.parseInt(stBuff.nextToken());
			delschame(player, schameId);
		}
		else
			super.onBypassFeedback(player, command);

		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		String content = Files.read(player.getLastFile(), player);
		content = content.replace("%sch%", showBuffList(player));
		html.setHtml(content);
		player.sendPacket(html);
	}

	public void doBuff(int skill_id, int skill_lvl, String BuffTarget, L2Player player)
	{
		L2Summon pet = player.getPet();
		
		if (!checkCondition(player))
			return;

		if(player.getLevel() > ConfigValue.LevelFreeBuff)
			if (player.getAdena() < ConfigValue.OneBuffPrice)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

		try
		{
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			int buffTime = skill.isMusic() ? ConfigValue.DanceAndSongTime : ConfigValue.BuffTime;
			if(BuffTarget.startsWith(" Player"))
			{
				if(!skill.checkSkillAbnormal(player) && !skill.isBlockedByChar(player, skill))
					for (EffectTemplate et : skill.getEffectTemplates())
					{
						Env env = new Env(player, player, skill);
						L2Effect effect = et.getEffect(env);
						effect.setPeriod(buffTime);
						player.getEffectList().addEffect(effect);
					}
			}
			else if (BuffTarget.startsWith(" Pet"))
			{
				if (pet == null)
					return;

				if(!skill.checkSkillAbnormal(pet) && !skill.isBlockedByChar(pet, skill))
					for (EffectTemplate et : skill.getEffectTemplates())
					{
						Env env = new Env(pet, pet, skill);
						L2Effect effect = et.getEffect(env);
						effect.setPeriod(buffTime);
						pet.getEffectList().addEffect(effect);
					}
			}
			if(player.getLevel() > ConfigValue.LevelFreeBuff)
				player.reduceAdena(ConfigValue.OneBuffPrice, true);
			if(BuffTarget.startsWith(" Player"))
				player.updateEffectIcons();
			else if(pet != null)
				pet.updateEffectIcons();
		}
		catch (Exception e)
		{
			player.sendMessage("Invalid skill!");
		}
	}

	public void doBuffGroup(int id_groups, String BuffTarget, L2Player player)
	{
		L2Summon pet = player.getPet();

		if (!checkCondition(player))
			return;

		if(player.getLevel() > ConfigValue.LevelFreeBuff)
			if (player.getAdena() < ConfigValue.OneBuffPrice * ConfigValue.GroupBuffPriceModifier)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		if(player.getLevel() > ConfigValue.LevelFreeBuff)
			player.reduceAdena((ConfigValue.OneBuffPrice * ConfigValue.GroupBuffPriceModifier), true);

		for(int[] buff : buffs)
		{
			if (buff[2] != id_groups)
				continue;
			if(BuffTarget.startsWith(" Player"))
			{
				L2Skill skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
				int buffTime = skill.isMusic() ? ConfigValue.DanceAndSongTime : ConfigValue.BuffTime;
				if(!skill.checkSkillAbnormal(player) && !skill.isBlockedByChar(player, skill))
					for (EffectTemplate et : skill.getEffectTemplates())
					{
						Env env = new Env(player, player, skill);
						L2Effect effect = et.getEffect(env);
						effect.setPeriod(buffTime);
						player.getEffectList().addEffect(effect);
					}
			}
			if(!BuffTarget.startsWith(" Pet"))
				continue;
			if(pet == null)
				return;

			L2Skill skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
			int buffTime = skill.isMusic() ? ConfigValue.DanceAndSongTime : ConfigValue.BuffTime;
			if(!skill.checkSkillAbnormal(pet) && !skill.isBlockedByChar(pet, skill))
				for (EffectTemplate et : skill.getEffectTemplates())
				{
					Env env = new Env(pet, pet, skill);
					L2Effect effect = et.getEffect(env);
					effect.setPeriod(buffTime);
					pet.getEffectList().addEffect(effect);
				}
		}
		if(BuffTarget.startsWith(" Player"))
			player.updateEffectIcons();
		else if(pet != null)
			pet.updateEffectIcons();
	}

	private String showBuffList(L2Player player)
	{
		CBBuffSch sch;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM community_skillsave WHERE charId=?;");
			statement.setLong(1, player.getObjectId());
			rs = statement.executeQuery();
			StringBuilder html = new StringBuilder();
			html.append("<table width=150>");
			while(rs.next())
			{
				sch = new CBBuffSch();
				sch.PlayerId = rs.getInt("charId");
				sch.id = rs.getInt("schameid");
				sch.SchName = rs.getString("name");
				html.append("<tr>");
				html.append("<td>");
				html.append("<button value=\"" + sch.SchName + "\" action=\"bypass -h npc_%objectId%_restore;" + sch.id + "; $tvari \" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.append("</td>");
				html.append("<td>");
				html.append("<button value=\"Удалить\" action=\"bypass -h npc_%objectId%_delete;" + sch.id + "\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.append("</td>");
				html.append("</tr>");
			}
			html.append("</table>");
			return html.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
		return null;
	}

	private void delschame(L2Player player, int shameid)
	{
		ThreadConnection conDel = null;
		FiltredPreparedStatement statementDel = null;
		try
		{
			conDel = L2DatabaseFactory.getInstance().getConnection();
			statementDel = conDel.prepareStatement("DELETE FROM community_skillsave WHERE charId=? AND schameid=?;");
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
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT COUNT(*) FROM community_skillsave WHERE charId=?;");
			statement.setInt(1, player.getObjectId());
			rs = statement.executeQuery();
			rs.next();
			String allbuff = "";

			L2Effect skill[] = player.getEffectList().getAllFirstEffects();

			for(int i = 0; i < skill.length; i++)
			{
				//if(!ArrayUtils.contains(notSaveBuff, skill[i].getSkill().getId()))
					allbuff = new StringBuilder().append(allbuff).append(skill[i].getSkill().getId() + ";").toString();
			}
			if(rs.getInt(1) < ConfigValue.maxBuffSchem)
			{
				statement = con.prepareStatement("SELECT COUNT(*) FROM community_skillsave WHERE charId=? AND name=?;");
				statement.setInt(1, player.getObjectId());
				statement.setString(2, SchName);
				rs = statement.executeQuery();
				rs.next();
				if(rs.getInt(1) == 0)
				{
					statement = con.prepareStatement("INSERT INTO community_skillsave (charId,name,skills) VALUES(?,?,?)");
					statement.setInt(1, player.getObjectId());
					statement.setString(2, SchName);
					statement.setString(3, allbuff);
					statement.execute();
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

		ThreadConnection con = null;
		FiltredStatement community_skillsave_statement = null;
		FiltredPreparedStatement communitybuff_statement = null;
		ResultSet community_skillsave_rs = null; 
		ResultSet communitybuff_rs = null;
		L2Skill skill;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			community_skillsave_statement = con.createStatement();

			community_skillsave_rs = community_skillsave_statement.executeQuery("SELECT `charId`, `schameid`, `name`, `skills` FROM `community_skillsave` WHERE `charId`='" + player.getObjectId() + "' AND `schameid`='" + schameId + "'");

			if(!community_skillsave_rs.next())
				return;
			String allskills = community_skillsave_rs.getString(4);
			StringTokenizer stBuff = new StringTokenizer(allskills, ";");
			while(stBuff.hasMoreTokens())
			{
				int skilltoresatore = Integer.parseInt(stBuff.nextToken());
				int skilllevel = SkillTable.getInstance().getBaseLevel(skilltoresatore);
				skill = SkillTable.getInstance().getInfo(skilltoresatore, skilllevel);
				int buffTime = 0;
				if(skill != null)
					buffTime = skill.isMusic() ? ConfigValue.DanceAndSongTime : ConfigValue.BuffTime;
				if(communitybuff_statement == null) // инициируем только первую итерацию, а потом подставляем новые данные - на порядок быстрее 
					communitybuff_statement = con.prepareStatement("SELECT COUNT(*) FROM `communitybuff` WHERE `skillID`=?");

				communitybuff_statement.setInt(1, skilltoresatore);
				communitybuff_rs = communitybuff_statement.executeQuery();

				if(communitybuff_rs.next())
					if(communitybuff_rs.getInt(1) != 0)
					{
						if(BuffTarget.startsWith(" Pet"))
						{
							if(pet == null)
			            		return;

							if(!skill.checkSkillAbnormal(pet) && !skill.isBlockedByChar(pet, skill))
								for(EffectTemplate et : skill.getEffectTemplates())
								{
									Env env = new Env(pet, pet, skill);
									L2Effect effect = et.getEffect(env);
									effect.setPeriod(buffTime);
									pet.getEffectList().addEffect(effect);
								}
						}
						else
						{
							if(!skill.checkSkillAbnormal(player) && !skill.isBlockedByChar(player, skill))
								for(EffectTemplate et : skill.getEffectTemplates())
								{
									Env env = new Env(player, player, skill);
									L2Effect effect = et.getEffect(env);
									effect.setPeriod(buffTime);
									player.getEffectList().addEffect(effect);
								}
						}
					}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(BuffTarget.startsWith(" Player"))
				player.updateEffectIcons();
			else if(pet != null)
				pet.updateEffectIcons();
			DatabaseUtils.closeDatabaseCSR(con, community_skillsave_statement, community_skillsave_rs);
			DatabaseUtils.closeDatabaseSR(communitybuff_statement, communitybuff_rs);
		}
	}

	public boolean checkCondition(L2Player player)
	{
		if (player == null)
			return false;

		/*if(!ConfigValue.pvpBoardBuffer)
		{
			player.sendMessage("Функция баффа отключена.");
			return false;
		}*/

		if (player.getLevel() > ConfigValue.CommBufferMaxLvl || player.getLevel() < ConfigValue.CommBufferMinLvl)
		{
			player.sendMessage("Ваш уровень не отвечает требованиям!");
			return false;
		}

		if(!ConfigValue.AllowCBBufferOnSiege)
		{
			Residence castle = TownManager.getInstance().getClosestTown(player).getCastle();
			Siege siege = castle.getSiege();
			if (siege != null && siege.isInProgress())
			{
				player.sendMessage("Нельзя использовать бафф во время осады.");
				return false;
			}
		}
		return true;
	}
}