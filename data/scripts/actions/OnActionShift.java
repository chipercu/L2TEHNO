package actions;

import commands.admin.AdminEditChar;
import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.InfoCache;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.skills.Stats;
import l2open.util.DropList;
import l2open.util.Files;
import l2open.util.Util;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import java.sql.ResultSet;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OnActionShift extends Functions implements ScriptFile
{
	public void onLoad()
	{
		_log.info("OnActionShift Loaded");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean OnActionShift_L2NpcInstance(L2Player player, L2Object object)
	{
		if(player == null || object == null)
			return false;
		if(!ConfigValue.AllowShiftClick && !player.isGM())
		{
			if(ConfigValue.AltShowDroplist && object.isNpc())
			{
				L2NpcInstance npc = (L2NpcInstance) object;
				if(npc.isDead())
					return false;
				droplist(player, npc);
			}
			return false;
		}
		if(object.isNpc())
		{
			L2NpcInstance npc = (L2NpcInstance) object;

			// Для мертвых мобов не показываем табличку, иначе спойлеры плачут
			if(npc.isDead())
				return false;

			String dialog;

			if(ConfigValue.AltFullStatsPage)
			{
				dialog = Files.read("data/scripts/actions/player.L2NpcInstance.onActionShift.full.htm", player);
				dialog = dialog.replaceFirst("%class%", String.valueOf(npc.getClass().getSimpleName().replaceFirst("Instance", "")));
				dialog = dialog.replace("%id%", String.valueOf(npc.getNpcId()));
				dialog = dialog.replaceFirst("%respawn%", String.valueOf(npc.getSpawn() != null ? Util.formatTime(npc.getSpawn().getRespawnDelay()) : "0"));
				dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(npc.getWalkSpeed()));
				dialog = dialog.replaceFirst("%evs%", String.valueOf(npc.getEvasionRate(null)));
				dialog = dialog.replaceFirst("%acc%", String.valueOf(npc.getAccuracy()));
				dialog = dialog.replaceFirst("%crt%", String.valueOf(npc.getCriticalHit(null, null)));
				dialog = dialog.replaceFirst("%aspd%", String.valueOf(npc.getPAtkSpd()));
				dialog = dialog.replaceFirst("%cspd%", String.valueOf(npc.getMAtkSpd()));
				dialog = dialog.replaceFirst("%loc%", String.valueOf(npc.getSpawn() != null ? npc.getSpawn().getLocation() : "0"));
				dialog = dialog.replaceFirst("%dist%", String.valueOf((int) npc.getDistance3D(player)));
				dialog = dialog.replaceFirst("%killed%", String.valueOf(npc.getTemplate().killscount));
				dialog = dialog.replaceFirst("%spReward%", String.valueOf(npc.getSpReward()));
				dialog = dialog.replaceFirst("%xyz%", npc.getLoc().x + " " + npc.getLoc().y + " " + npc.getLoc().z);
				dialog = dialog.replaceFirst("%objectId%", String.valueOf(npc.getObjectId()));

				if(npc.getSpawnedLoc() != null)
					dialog = dialog.replaceFirst("%xyzSp%", npc.getSpawnedLoc().x + " " + npc.getSpawnedLoc().y + " " + npc.getSpawnedLoc().z);
				else
					dialog = dialog.replaceFirst("%xyzSp%", " -0- ");
				dialog = dialog.replaceFirst("%ai_type%", npc.getAI().getL2ClassShortName());
				dialog = dialog.replaceFirst("%direction%", Util.getDirectionTo(npc, player).toString().toLowerCase());
			}
			else
				dialog = Files.read("data/scripts/actions/player.L2NpcInstance.onActionShift.htm", player);

			dialog = dialog.replaceFirst("%name%", npc.getName());
			dialog = dialog.replaceFirst("%level%", String.valueOf(npc.getLevel()));
			dialog = dialog.replaceFirst("%factionId%", npc.getFactionId().equals("") ? "<font color=ff0000>none</font>" : npc.getFactionId());
			dialog = dialog.replaceFirst("%aggro%", String.valueOf(npc.getAggroRange()));
			dialog = dialog.replaceFirst("%maxHp%", String.valueOf(npc.getMaxHp()));
			dialog = dialog.replaceFirst("%maxMp%", String.valueOf(npc.getMaxMp()));
			dialog = dialog.replaceFirst("%pDef%", String.valueOf(npc.getPDef(null)));
			dialog = dialog.replaceFirst("%mDef%", String.valueOf(npc.getMDef(null, null)));
			dialog = dialog.replaceFirst("%pAtk%", String.valueOf(npc.getPAtk(null)));
			dialog = dialog.replaceFirst("%mAtk%", String.valueOf(npc.getMAtk(null, null)));
			dialog = dialog.replaceFirst("%expReward%", String.valueOf(npc.getExpReward()));
			dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(npc.getRunSpeed()));

			int points = RaidBossSpawnManager.getInstance().getPoinstForRaid(npc.getNpcId());
			dialog = dialog.replaceFirst("%raid_points%", String.valueOf(points));

			dialog = dialog.replaceFirst("%pts_ai%", "<font color=ff0000>none</font>");

			// Дополнительная инфа для ГМов
			if(player.isGM())
				dialog = dialog.replaceFirst("%AI%", String.valueOf(npc.getAI()) + ",<br1>active: " + npc.getAI().isActive() + ",<br1>intention: " + npc.getAI().getIntention());
			else
				dialog = dialog.replaceFirst("%AI%", "");

			show(dialog, player, npc);
		}
		player.sendActionFailed();
		return true;
	}

	public String getNpcRaceById(int raceId)
	{
		switch(raceId)
		{
			case 1:
				return "Undead";
			case 2:
				return "Magic Creatures";
			case 3:
				return "Beasts";
			case 4:
				return "Animals";
			case 5:
				return "Plants";
			case 6:
				return "Humanoids";
			case 7:
				return "Spirits";
			case 8:
				return "Angels";
			case 9:
				return "Demons";
			case 10:
				return "Dragons";
			case 11:
				return "Giants";
			case 12:
				return "Bugs";
			case 13:
				return "Fairies";
			case 14:
				return "Humans";
			case 15:
				return "Elves";
			case 16:
				return "Dark Elves";
			case 17:
				return "Orcs";
			case 18:
				return "Dwarves";
			case 19:
				return "Others";
			case 20:
				return "Non-living Beings";
			case 21:
				return "Siege Weapons";
			case 22:
				return "Defending Army";
			case 23:
				return "Mercenaries";
			case 24:
				return "Unknown Creature";
			case 25:
				return "Kamael";
			default:
				return "Not defined";
		}
	}

	public void droplist()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		droplist(player, npc);
	}

	public void droplist(L2Player player, L2NpcInstance npc)
	{
		if(player == null || npc == null)
			return;
		if(!ConfigValue.AltGenerateDroplistOnDemand)
			show(InfoCache.getFromDroplistCache(npc.getNpcId()), player, npc);
		else
		{
			int diff = npc.calculateLevelDiffForDrop(player.isInParty() ? player.getParty().getLevel() : player.getLevel(), false);
			double mult = 1;
			if(diff > 0)
				mult = Experience.penaltyModifier(diff, 9);
			mult = npc.calcStat(Stats.DROP, mult, null, null);

			double mod_adena = npc.calcStat(Stats.ADENA, 1., null, null);

			
			show(DropList.generateDroplist(npc.getTemplate(), npc.isMonster() ? (L2MonsterInstance) npc : null, mult, mod_adena, player), player, npc);
		}
	}

	/**
		<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color="LEVEL">Type:</font></td><td align=right width=170><font color=999999>%class%</font></td></tr></table></td></tr>
		<tr><td><table width=270 border=0><tr><td width=100><font color="LEVEL">Territory</font></td><td align=right width=170>%spawn%</td></tr></table></td></tr>
	**/
	public void fields()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		StringBuilder dialog = new StringBuilder("<html><body>");
		dialog.append("<table width=290 border=0>");

		dialog.append("</table><br>");
		dialog.append("</body></html>");
		show(dialog.toString(), player, npc);
	}

	public void quests()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(npc.getName()).append("<br></font></center><br>");

		Quest[] list = npc.getTemplate().getEventQuests(QuestEventType.MOBKILLED);
		if(list != null && list.length != 0)
		{
			dialog.append("On kill:<br>");
			for(Quest q : list)
				dialog.append(q.getDescr(player)).append("<br1>");
		}

		dialog.append("</body></html>");
		show(dialog.toString(), player, npc);
	}

	/**
	<br><center>Active:</center><br>
	<center>
		<table border=0 cellspacing=4 cellpadding=2 width=280>
			<tr>
				<td width=40 height=32>
					<img src="icon.skill0034" width=32 height=32>
				</td>
				<td width=210 height=32>
					<br><font color="LEVEL">Test <font color="00ff00">[1034:1]</font></font>
				</td>
			</tr>
		</table>
	</center>
**/
	public void skills()
	{
		L2Player player = (L2Player) getSelf();
		L2Character npc = getNpc();
		if(player == null)
			return;
		else if(npc == null)
		{
			npc = (L2Character)player.getTarget();
			if(npc == null || (!player.isGM() && npc.isPlayable() && npc.getPlayer() != player))
				return;
		}

		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"0\">");
		dialog.append(npc.getName()).append("<br></font></center>");

		Collection<L2Skill> list = npc.getAllSkills();
		if(list != null && !list.isEmpty())
		{
			dialog.append("<br><center>Active:</center><br1>");
			dialog.append("	<center>");
			dialog.append("		<table border=0 cellspacing=4 cellpadding=2 width=295>");
			for(L2Skill s : list)
				if(s.isActive())
				{
					dialog.append("			<tr>");
					dialog.append("				<td width=36 height=32>");
					dialog.append("					<img src=\""+s.getIcon()+"\" width=32 height=32>");
					dialog.append("				</td>");
					dialog.append("				<td>");
					dialog.append("					<br><font color=LEVEL>"+s.getName()+" <font color=00ff00>["+s.getId()+":"+s.getDisplayLevel()+"]</font></font>");
					dialog.append("				</td>");
					dialog.append("			</tr>");
			}
			dialog.append("		</table>");
			dialog.append("	</center>");
			// ------------------------------------------------------
			dialog.append("<br><center>Passive:</center><br1>");
			dialog.append("	<center>");
			dialog.append("		<table border=0 cellspacing=4 cellpadding=2 width=295>");
			for(L2Skill s : list)
				if(!s.isActive())
				{
					dialog.append("			<tr>");
					dialog.append("				<td width=36 height=32>");
					dialog.append("					<img src=\""+s.getIcon()+"\" width=32 height=32>");
					dialog.append("				</td>");
					dialog.append("				<td>");
					dialog.append("					<br><font color=LEVEL>"+s.getName()+" <font color=00ff00>["+s.getId()+":"+s.getDisplayLevel()+"]</font></font>");
					dialog.append("				</td>");
					dialog.append("			</tr>");
			}
			dialog.append("		</table>");
			dialog.append("	</center>");
		}

		dialog.append("</body></html>");
		show(dialog.toString(), player, null);
	}

	public void effects()
	{
		L2Player player = (L2Player) getSelf();
		L2Character npc = getNpc();
		if(player == null)
			return;
		else if(npc == null)
		{
			npc = (L2Character)player.getTarget();
			if(npc == null || (!player.isGM() && npc.isPlayable() && npc.getPlayer() != player))
				return;
		}

		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(npc.getName()).append("<br></font></center><br>");

		dialog.append("	<center>");
		dialog.append("		<table border=0 cellspacing=4 cellpadding=2 width=295>");
		ConcurrentLinkedQueue<L2Skill> list = npc.getEffectList().getAllSkills(false);
		if(list != null && !list.isEmpty())
			for(L2Skill s : list)
			{
				dialog.append("			<tr>");
				dialog.append("				<td width=36 height=32>");
				dialog.append("					<img src=\""+s.getIcon()+"\" width=32 height=32>");
				dialog.append("				</td>");
				dialog.append("				<td>");
				dialog.append("					<br><font color=LEVEL>"+s.getName()+" <font color=00ff00>["+s.getId()+":"+s.getDisplayLevel()+"]["+0+"s]</font></font>");
				dialog.append("				</td>");
				dialog.append("			</tr>");
			}
		dialog.append("		</table>");
		dialog.append("	</center>");
		dialog.append("<br><center><button value=\"");
		dialog.append(player.isLangRus() ? "Обновить" : "Refresh"); 
		dialog.append("\" action=\"bypass -h scripts_actions.OnActionShift:effects\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center></body></html>");

		show(dialog.toString(), player, null);
	}

	public void stats()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		String dialog = Files.read("data/scripts/actions/player.L2NpcInstance.stats.htm", player);
		dialog = dialog.replaceFirst("%name%", npc.getName());
		dialog = dialog.replaceFirst("%level%", String.valueOf(npc.getLevel()));
		dialog = dialog.replaceFirst("%factionId%", npc.getFactionId());
		dialog = dialog.replaceFirst("%aggro%", String.valueOf(npc.getAggroRange()));
		dialog = dialog.replaceFirst("%race%", getNpcRaceById(npc.getTemplate().getRace()));
		dialog = dialog.replaceFirst("%herbs%", String.valueOf(npc.getTemplate().isDropHerbs));
		dialog = dialog.replaceFirst("%maxHp%", String.valueOf(npc.getMaxHp()));
		dialog = dialog.replaceFirst("%maxMp%", String.valueOf(npc.getMaxMp()));
		dialog = dialog.replaceFirst("%pDef%", String.valueOf(npc.getPDef(null)));
		dialog = dialog.replaceFirst("%mDef%", String.valueOf(npc.getMDef(null, null)));
		dialog = dialog.replaceFirst("%pAtk%", String.valueOf(npc.getPAtk(null)));
		dialog = dialog.replaceFirst("%mAtk%", String.valueOf(npc.getMAtk(null, null)));
		dialog = dialog.replaceFirst("%accuracy%", String.valueOf(npc.getAccuracy()));
		dialog = dialog.replaceFirst("%evasionRate%", String.valueOf(npc.getEvasionRate(null)));
		dialog = dialog.replaceFirst("%criticalHit%", String.valueOf(npc.getCriticalHit(null, null)));
		dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(npc.getRunSpeed()));
		dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(npc.getWalkSpeed()));
		dialog = dialog.replaceFirst("%pAtkSpd%", String.valueOf(npc.getPAtkSpd()));
		dialog = dialog.replaceFirst("%mAtkSpd%", String.valueOf(npc.getMAtkSpd()));
		show(dialog, player, npc);
	}

	public void resists()
	{
		L2Player player = (L2Player) getSelf();
		L2Character npc = getNpc();
		if(player == null)
			return;
		else if(npc == null)
		{
			npc = (L2Character)player.getTarget();
			if(npc == null || (!player.isGM() && npc.isPlayable() && npc.getPlayer() != player))
				return;
		}

		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(npc.getName()).append("<br></font></center><table width=\"80%\">");

		int FIRE_RECEPTIVE = (int) npc.calcStat(Stats.FIRE_RECEPTIVE, 0, null, null);
		if(FIRE_RECEPTIVE != 0)
			dialog.append("<tr><td>Fire</td><td>").append(-FIRE_RECEPTIVE).append("</td></tr>");

		int WIND_RECEPTIVE = (int) npc.calcStat(Stats.WIND_RECEPTIVE, 0, null, null);
		if(WIND_RECEPTIVE != 0)
			dialog.append("<tr><td>Wind</td><td>").append(-WIND_RECEPTIVE).append("</td></tr>");

		int WATER_RECEPTIVE = (int) npc.calcStat(Stats.WATER_RECEPTIVE, 0, null, null);
		if(WATER_RECEPTIVE != 0)
			dialog.append("<tr><td>Water</td><td>").append(-WATER_RECEPTIVE).append("</td></tr>");

		int EARTH_RECEPTIVE = (int) npc.calcStat(Stats.EARTH_RECEPTIVE, 0, null, null);
		if(EARTH_RECEPTIVE != 0)
			dialog.append("<tr><td>Earth</td><td>").append(-EARTH_RECEPTIVE).append("</td></tr>");

		int SACRED_RECEPTIVE = (int) npc.calcStat(Stats.SACRED_RECEPTIVE, 0, null, null);
		if(SACRED_RECEPTIVE != 0)
			dialog.append("<tr><td>Light</td><td>").append(-SACRED_RECEPTIVE).append("</td></tr>");

		int UNHOLY_RECEPTIVE = (int) npc.calcStat(Stats.UNHOLY_RECEPTIVE, 0, null, null);
		if(UNHOLY_RECEPTIVE != 0)
			dialog.append("<tr><td>Darkness</td><td>").append(-UNHOLY_RECEPTIVE).append("</td></tr>");

		if(npc.getTraitStat().trait_sword != 1)
			dialog.append("<tr><td>trait_sword: </td><td>").append(npc.getTraitStat().trait_sword*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_blunt != 1)
			dialog.append("<tr><td>trait_blunt: </td><td>").append(npc.getTraitStat().trait_blunt*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_dagger != 1)
			dialog.append("<tr><td>trait_dagger: </td><td>").append(npc.getTraitStat().trait_dagger*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_bow != 1)
			dialog.append("<tr><td>trait_bow: </td><td>").append(npc.getTraitStat().trait_bow*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_pole != 1)
			dialog.append("<tr><td>trait_pole: </td><td>").append(npc.getTraitStat().trait_pole*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_fist != 1)
			dialog.append("<tr><td>trait_fist: </td><td>").append(npc.getTraitStat().trait_fist*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_dual != 1)
			dialog.append("<tr><td>trait_dual: </td><td>").append(npc.getTraitStat().trait_dual*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_dualfist != 1)
			dialog.append("<tr><td>trait_dualfist: </td><td>").append(npc.getTraitStat().trait_dualfist*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_rapier != 1)
			dialog.append("<tr><td>trait_rapier: </td><td>").append(npc.getTraitStat().trait_rapier*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_crossbow != 1)
			dialog.append("<tr><td>trait_crossbow: </td><td>").append(npc.getTraitStat().trait_crossbow*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_ancientsword != 1)
			dialog.append("<tr><td>trait_ancientsword: </td><td>").append(npc.getTraitStat().trait_ancientsword*-1).append("</td></tr>");
		if(npc.getTraitStat().trait_dualdagger != 1)
			dialog.append("<tr><td>trait_dualdagger: </td><td>").append(npc.getTraitStat().trait_dualdagger*-1).append("</td></tr>");

		if(npc.getTraitStat().trait_bleed != 0)
			dialog.append("<tr><td>trait_bleed: </td><td>").append(npc.getTraitStat().trait_bleed).append("</td></tr>");
		if(npc.getTraitStat().trait_poison != 0)
			dialog.append("<tr><td>trait_poison: </td><td>").append(npc.getTraitStat().trait_poison).append("</td></tr>");
		if(npc.getTraitStat().trait_shock != 0)
			dialog.append("<tr><td>trait_shock: </td><td>").append(npc.getTraitStat().trait_shock).append("</td></tr>");
		if(npc.getTraitStat().trait_hold != 0)
			dialog.append("<tr><td>trait_hold: </td><td>").append(npc.getTraitStat().trait_hold).append("</td></tr>");
		if(npc.getTraitStat().trait_sleep != 0)
			dialog.append("<tr><td>trait_sleep: </td><td>").append(npc.getTraitStat().trait_sleep).append("</td></tr>");
		if(npc.getTraitStat().trait_paralyze != 0)
			dialog.append("<tr><td>trait_paralyze: </td><td>").append(npc.getTraitStat().trait_paralyze).append("</td></tr>");
		if(npc.getTraitStat().trait_derangement != 0)
			dialog.append("<tr><td>trait_derangement: </td><td>").append(npc.getTraitStat().trait_derangement).append("</td></tr>");
			
		if(npc.getTraitStat().trait_bleed_power != 0)
			dialog.append("<tr><td>trait_bleed_power: </td><td>").append(npc.getTraitStat().trait_bleed_power).append("</td></tr>");
		if(npc.getTraitStat().trait_poison_power != 0)
			dialog.append("<tr><td>trait_poison_power: </td><td>").append(npc.getTraitStat().trait_poison_power).append("</td></tr>");
		if(npc.getTraitStat().trait_shock_power != 0)
			dialog.append("<tr><td>trait_shock_power: </td><td>").append(npc.getTraitStat().trait_shock_power).append("</td></tr>");
		if(npc.getTraitStat().trait_hold_power != 0)
			dialog.append("<tr><td>trait_hold_power: </td><td>").append(npc.getTraitStat().trait_hold_power).append("</td></tr>");
		if(npc.getTraitStat().trait_sleep_power != 0)
			dialog.append("<tr><td>trait_sleep_power: </td><td>").append(npc.getTraitStat().trait_sleep_power).append("</td></tr>");
		if(npc.getTraitStat().trait_paralyze_power != 0)
			dialog.append("<tr><td>trait_paralyze_power: </td><td>").append(npc.getTraitStat().trait_paralyze_power).append("</td></tr>");
		if(npc.getTraitStat().trait_derangement_power != 0)
			dialog.append("<tr><td>trait_derangement_power: </td><td>").append(npc.getTraitStat().trait_derangement_power).append("</td></tr>");

		int DEBUFF_RECEPTIVE = (int) npc.calcStat(Stats.DEBUFF_RECEPTIVE, null, null);
		if(DEBUFF_RECEPTIVE != 0)
			dialog.append("<tr><td>Debuff</td><td>").append(DEBUFF_RECEPTIVE).append("</td></tr>");

		int CANCEL_RECEPTIVE = (int) npc.calcStat(Stats.CANCEL_RECEPTIVE, null, null);
		if(CANCEL_RECEPTIVE != 0)
			dialog.append("<tr><td>Cancel</td><td>").append(CANCEL_RECEPTIVE).append("</td></tr>");

		int CRIT_CHANCE_RECEPTIVE = 100 - (int) npc.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, null, null);
		if(CRIT_CHANCE_RECEPTIVE != 0)
			dialog.append("<tr><td>Crit get chance</td><td>").append(CRIT_CHANCE_RECEPTIVE).append("%</td></tr>");

		int CRIT_DAMAGE_RECEPTIVE = 100 - (int) npc.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, null, null);
		if(CRIT_DAMAGE_RECEPTIVE != 0)
			dialog.append("<tr><td>Crit get damage</td><td>").append(CRIT_DAMAGE_RECEPTIVE).append("%</td></tr>");

		int ElemAtkPower = 0;
		switch(((L2NpcTemplate)npc.getTemplate()).atkElement)
		{
			case 0:
				ElemAtkPower = (int) npc.calcStat(Stats.ATTACK_ELEMENT_FIRE, 0, null, null);
				break;
			case 1:
				ElemAtkPower = (int) npc.calcStat(Stats.ATTACK_ELEMENT_WATER, 0, null, null);
				break;
			case 2:
				ElemAtkPower = (int) npc.calcStat(Stats.ATTACK_ELEMENT_WIND, 0, null, null);
				break;
			case 3:
				ElemAtkPower = (int) npc.calcStat(Stats.ATTACK_ELEMENT_EARTH, 0, null, null);
				break;
			case 4:
				ElemAtkPower = (int) npc.calcStat(Stats.ATTACK_ELEMENT_SACRED, 0, null, null);
				break;
			case 5:
				ElemAtkPower = (int) npc.calcStat(Stats.ATTACK_ELEMENT_UNHOLY, 0, null, null);
				break;
			default:
				ElemAtkPower = 0;
				break;
		}

		dialog.append("<tr><td>Attack Element: "+getElementNameById(((L2NpcTemplate)npc.getTemplate()).atkElement)+"</td><td>").append(ElemAtkPower).append("</td></tr>");

		/*if(FIRE_RECEPTIVE == 0 && WIND_RECEPTIVE == 0 && WATER_RECEPTIVE == 0 && EARTH_RECEPTIVE == 0 && UNHOLY_RECEPTIVE == 0 && SACRED_RECEPTIVE // primary elements
		== 0 && trait_bleed == 0 && trait_shock // phys debuff
		== 0 && trait_poison == 0 && trait_hold == 0 && trait_sleep == 0 && trait_paralyze == 0 && trait_derangement == 0 && DEBUFF_RECEPTIVE == 0 && CANCEL_RECEPTIVE // mag debuff
		== 0 && SWORD_WPN_RECEPTIVE == 0 && DUAL_WPN_RECEPTIVE == 0 && BLUNT_WPN_RECEPTIVE == 0 && DAGGER_WPN_RECEPTIVE == 0 && BOW_WPN_RECEPTIVE == 0 && POLE_WPN_RECEPTIVE == 0 && FIST_WPN_RECEPTIVE == 0// weapons
		)
			dialog.append("</table>No resists</body></html>");
		else*/
			dialog.append("</table></body></html>");
		show(dialog.toString(), player, null);
	}

	public void aggro()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		StringBuilder dialog = new StringBuilder("<html><body><table width=\"80%\"><tr><td>Attacker</td><td>Damage</td><td>Hate</td></tr>");

		// Сортировка аггролиста по хейту
		TreeSet<AggroInfo> aggroList = new TreeSet<AggroInfo>(new Comparator<AggroInfo>(){
			@Override
			public int compare(AggroInfo o1, AggroInfo o2)
			{
				int hateDiff = o1.hate - o2.hate;
				if(hateDiff != 0)
					return hateDiff;
				return o1.damage - o2.damage;
			}
		});
		aggroList.addAll(npc.getAggroList());

		// Вывод результата
		for(AggroInfo aggroInfo : aggroList.descendingSet())
			if(aggroInfo.attacker != null && (aggroInfo.attacker.isPlayer() || aggroInfo.attacker.isSummon() || aggroInfo.attacker.isPet()))
				dialog.append("<tr><td>" + aggroInfo.attacker.getName() + "</td><td>" + aggroInfo.damage + "</td><td>" + aggroInfo.hate + "</td></tr>");

		dialog.append("</table><br><center><button value=\"");
		dialog.append(player.isLangRus() ? "Обновить" : "Refresh");
		dialog.append("\" action=\"bypass -h scripts_actions.OnActionShift:aggro\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center></body></html>");
		show(dialog.toString(), player, npc);
	}

	public boolean OnActionShift_L2DoorInstance(L2Player player, L2Object object)
	{
		if(player == null || object == null || !player.getPlayerAccess().Door || !object.isDoor())
			return false;

		String dialog;
		L2DoorInstance door = (L2DoorInstance) object;
		dialog = Files.read("data/scripts/actions/admin.L2DoorInstance.onActionShift.htm", player);
		dialog = dialog.replaceFirst("%CurrentHp%", String.valueOf((int) door.getCurrentHp()));
		dialog = dialog.replaceFirst("%MaxHp%", String.valueOf(door.getMaxHp()));
		dialog = dialog.replaceFirst("%ObjectId%", String.valueOf(door.getObjectId()));
		dialog = dialog.replaceFirst("%doorId%", String.valueOf(door.getDoorId()));
		dialog = dialog.replaceFirst("%pdef%", String.valueOf(door.getPDef(null)));
		dialog = dialog.replaceFirst("%mdef%", String.valueOf(door.getMDef(null, null)));
		dialog = dialog.replaceFirst("%siege%", door.isSiegeWeaponOnlyAttackable() ? "Siege weapon only attackable." : "");
		dialog = dialog.replaceFirst("%isOpen%", door.isOpen() ? "Open" : "Close");
		dialog = dialog.replaceFirst("%geoOpen%", door.geoOpen ? "Geo Open" : "Geo Close");
		

		L2Object target = player.getTarget();
		if(target == null || !target.isDoor())
		{
			dialog = dialog.replaceFirst("bypass -h admin_open", "bypass -h admin_open " + door.getDoorId());
			dialog = dialog.replaceFirst("bypass -h admin_close", "bypass -h admin_close " + door.getDoorId());
		}

		show(dialog, player);
		player.sendActionFailed();
		return true;
	}

	public boolean OnActionShift_L2Player(L2Player player, L2Object object)
	{
		if(player == null || object == null)
			return false;
		if(object.isPlayer())
		{
			if(player.getPlayerAccess().CanViewChar)
			{
				AdminEditChar.showCharacterList(player, (L2Player) object);
				player.sendActionFailed();
				return true;
			}
			else if(ConfigValue.EnableBotReport && !((L2Player) object).isDead())
			{
				// bot_report_already
				// bot_report_ok
				// bot_report
				String dialog = Files.read("data/scripts/actions/bot_report.htm", player);
				dialog = dialog.replace("<?name?>", String.valueOf(object.getName()));
				dialog = dialog.replace("<?target_id?>", String.valueOf(object.getObjectId())+" "+object.getName());
				show(dialog, player);
				player.sendActionFailed();
			}
		}
		return false;
	}

	public void spam_report(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || param == null)
			return;

		String dialog;

		if(!report_check(player.getHWIDs(), Integer.parseInt(param[0]), "SPAMER"))
			dialog = Files.read("data/scripts/actions/bot_report_already.htm", player);
		else
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			try
			{
				// type,char_name,char_id,char_hwid,char_r_id,char_r_name,char_r_hwid,read
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO bot_report VALUES (?,?,?,?,?,?,?,0)");
				statement.setString(1, "SPAMER");
				statement.setString(2, param[2]);
				statement.setInt(3, Integer.parseInt(param[0]));
				statement.setString(4, param[1]);
				statement.setInt(5, player.getObjectId());
				statement.setString(6, player.getName());
				statement.setString(7, player.getHWIDs());
				statement.execute();
			}
			catch(Exception e)
			{
				_log.warning("error spam_report " + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
			dialog = Files.read("data/scripts/actions/bot_report_ok.htm", player);
		}

		dialog = dialog.replace("<?name?>", param[2]);
		show(dialog, player);
	}

	public void bot_report(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || param == null)
			return;

		String dialog;

		if(!report_check(player.getHWIDs(), Integer.parseInt(param[0]), "BOT"))
			dialog = Files.read("data/scripts/actions/bot_report_already.htm", player);
		else
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			try
			{
				// type,char_name,char_id,char_hwid,char_r_id,char_r_name,char_r_hwid,read
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO bot_report VALUES (?,?,?,?,?,?,?,0)");
				statement.setString(1, "BOT");
				statement.setString(2, param[2]);
				statement.setInt(3, Integer.parseInt(param[0]));
				statement.setString(4, param[1]);
				statement.setInt(5, player.getObjectId());
				statement.setString(6, player.getName());
				statement.setString(7, player.getHWIDs());
				statement.execute();
			}
			catch(Exception e)
			{
				_log.warning("error spam_report " + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
			dialog = Files.read("data/scripts/actions/bot_report_ok.htm", player);
		}

		dialog = dialog.replace("<?name?>", param[2]);
		show(dialog, player);
	}

	private boolean report_check(String hwid, int object_id, String type)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_id FROM bot_report WHERE char_r_hwid=? AND char_id=? AND type=? AND `read`=0");
			statement.setString(1, hwid);
			statement.setInt(2, object_id);
			statement.setString(3, type);
			rset = statement.executeQuery();

			if(rset.next())
				return false;
		}
		catch(Exception e)
		{
			_log.warning("data error on report_check: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return true;
	}

	public boolean OnActionShift_L2SummonInstance(L2Player player, L2Object object)
	{
		if(player == null || object == null || !player.getPlayerAccess().CanViewChar)
			return false;
		if(object.isSummon())
		{
			String dialog;
			L2SummonInstance summon = (L2SummonInstance) object;
			dialog = Files.read("data/scripts/actions/admin.L2SummonInstance.onActionShift.htm");
			dialog = dialog.replaceFirst("%name%", String.valueOf(summon.getName()));
			dialog = dialog.replaceFirst("%level%", String.valueOf(summon.getLevel()));
			dialog = dialog.replaceFirst("%class%", String.valueOf(summon.getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
			dialog = dialog.replaceFirst("%xyz%", summon.getLoc().x + " " + summon.getLoc().y + " " + summon.getLoc().z);
			dialog = dialog.replaceFirst("%heading%", String.valueOf(summon.getLoc().h));

			dialog = dialog.replaceFirst("%owner%", String.valueOf(summon.getPlayer().getName()));
			dialog = dialog.replaceFirst("%ownerId%", String.valueOf(summon.getPlayer().getObjectId()));

			dialog = dialog.replaceFirst("%npcId%", String.valueOf(summon.getNpcId()));
			dialog = dialog.replaceFirst("%expPenalty%", String.valueOf(summon.getExpPenalty()));

			dialog = dialog.replaceFirst("%maxHp%", String.valueOf(summon.getMaxHp()));
			dialog = dialog.replaceFirst("%maxMp%", String.valueOf(summon.getMaxMp()));
			dialog = dialog.replaceFirst("%currHp%", String.valueOf((int) summon.getCurrentHp()));
			dialog = dialog.replaceFirst("%currMp%", String.valueOf((int) summon.getCurrentMp()));

			dialog = dialog.replaceFirst("%pDef%", String.valueOf(summon.getPDef(null)));
			dialog = dialog.replaceFirst("%mDef%", String.valueOf(summon.getMDef(null, null)));
			dialog = dialog.replaceFirst("%pAtk%", String.valueOf(summon.getPAtk(null)));
			dialog = dialog.replaceFirst("%mAtk%", String.valueOf(summon.getMAtk(null, null)));
			dialog = dialog.replaceFirst("%accuracy%", String.valueOf(summon.getAccuracy()));
			dialog = dialog.replaceFirst("%evasionRate%", String.valueOf(summon.getEvasionRate(null)));
			dialog = dialog.replaceFirst("%crt%", String.valueOf(summon.getCriticalHit(null, null)));
			dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(summon.getRunSpeed()));
			dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(summon.getWalkSpeed()));
			dialog = dialog.replaceFirst("%pAtkSpd%", String.valueOf(summon.getPAtkSpd()));
			dialog = dialog.replaceFirst("%mAtkSpd%", String.valueOf(summon.getMAtkSpd()));
			dialog = dialog.replaceFirst("%dist%", String.valueOf((int) summon.getRealDistance(player)));

			dialog = dialog.replaceFirst("%STR%", String.valueOf(summon.getSTR()));
			dialog = dialog.replaceFirst("%DEX%", String.valueOf(summon.getDEX()));
			dialog = dialog.replaceFirst("%CON%", String.valueOf(summon.getCON()));
			dialog = dialog.replaceFirst("%INT%", String.valueOf(summon.getINT()));
			dialog = dialog.replaceFirst("%WIT%", String.valueOf(summon.getWIT()));
			dialog = dialog.replaceFirst("%MEN%", String.valueOf(summon.getMEN()));

			show(dialog, player);
		}
		player.sendActionFailed();
		return false;
	}

	public boolean OnActionShift_L2PetInstance(L2Player player, L2Object object)
	{
		if(player == null || object == null || !player.getPlayerAccess().CanViewChar)
			return false;
		if(object.isPet())
		{
			L2PetInstance pet = (L2PetInstance) object;
			
			String dialog;
			
			dialog = Files.read("data/scripts/actions/admin.L2PetInstance.onActionShift.htm");
			dialog = dialog.replaceFirst("%name%", String.valueOf(pet.getName()));
			dialog = dialog.replaceFirst("%level%", String.valueOf(pet.getLevel()));
			dialog = dialog.replaceFirst("%class%", String.valueOf(pet.getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
			dialog = dialog.replaceFirst("%xyz%", pet.getLoc().x + " " + pet.getLoc().y + " " + pet.getLoc().z);
			dialog = dialog.replaceFirst("%heading%", String.valueOf(pet.getLoc().h));

			dialog = dialog.replaceFirst("%owner%", String.valueOf(pet.getPlayer().getName()));
			dialog = dialog.replaceFirst("%ownerId%", String.valueOf(pet.getPlayer().getObjectId()));
			dialog = dialog.replaceFirst("%npcId%", String.valueOf(pet.getNpcId()));
			dialog = dialog.replaceFirst("%controlItemId%", String.valueOf(pet.getControlItem().getItemId()));

			dialog = dialog.replaceFirst("%exp%", String.valueOf(pet.getExp()));
			dialog = dialog.replaceFirst("%sp%", String.valueOf(pet.getSp()));

			dialog = dialog.replaceFirst("%maxHp%", String.valueOf(pet.getMaxHp()));
			dialog = dialog.replaceFirst("%maxMp%", String.valueOf(pet.getMaxMp()));
			dialog = dialog.replaceFirst("%currHp%", String.valueOf((int) pet.getCurrentHp()));
			dialog = dialog.replaceFirst("%currMp%", String.valueOf((int) pet.getCurrentMp()));

			dialog = dialog.replaceFirst("%pDef%", String.valueOf(pet.getPDef(null)));
			dialog = dialog.replaceFirst("%mDef%", String.valueOf(pet.getMDef(null, null)));
			dialog = dialog.replaceFirst("%pAtk%", String.valueOf(pet.getPAtk(null)));
			dialog = dialog.replaceFirst("%mAtk%", String.valueOf(pet.getMAtk(null, null)));
			dialog = dialog.replaceFirst("%accuracy%", String.valueOf(pet.getAccuracy()));
			dialog = dialog.replaceFirst("%evasionRate%", String.valueOf(pet.getEvasionRate(null)));
			dialog = dialog.replaceFirst("%crt%", String.valueOf(pet.getCriticalHit(null, null)));
			dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(pet.getRunSpeed()));
			dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(pet.getWalkSpeed()));
			dialog = dialog.replaceFirst("%pAtkSpd%", String.valueOf(pet.getPAtkSpd()));
			dialog = dialog.replaceFirst("%mAtkSpd%", String.valueOf(pet.getMAtkSpd()));
			dialog = dialog.replaceFirst("%dist%", String.valueOf((int) pet.getRealDistance(player)));

			dialog = dialog.replaceFirst("%STR%", String.valueOf(pet.getSTR()));
			dialog = dialog.replaceFirst("%DEX%", String.valueOf(pet.getDEX()));
			dialog = dialog.replaceFirst("%CON%", String.valueOf(pet.getCON()));
			dialog = dialog.replaceFirst("%INT%", String.valueOf(pet.getINT()));
			dialog = dialog.replaceFirst("%WIT%", String.valueOf(pet.getWIT()));
			dialog = dialog.replaceFirst("%MEN%", String.valueOf(pet.getMEN()));

			show(dialog, player);
		}
		player.sendActionFailed();
		return false;
	}

	public boolean OnActionShift_L2ItemInstance(L2Player player, L2Object object)
	{
		if(player == null || object == null || !player.getPlayerAccess().CanViewChar)
			return false;
			
			String dialog;
			L2ItemInstance item = (L2ItemInstance) object;
			dialog = Files.read("data/scripts/actions/admin.L2ItemInstance.onActionShift.htm");
			dialog = dialog.replaceFirst("%name%", String.valueOf(item.getItem().getName()));
			dialog = dialog.replaceFirst("%objId%", String.valueOf(item.getObjectId()));
			dialog = dialog.replaceFirst("%itemId%", String.valueOf(item.getItemId()));
			dialog = dialog.replaceFirst("%grade%", String.valueOf(item.getCrystalType()));
			dialog = dialog.replaceFirst("%count%", String.valueOf(item.getCount()));

			dialog = dialog.replaceFirst("%owner%", item.getPlayer() != null ? String.valueOf(item.getPlayer()) : "none");
			dialog = dialog.replaceFirst("%ownerId%", item.getPlayer() != null ? String.valueOf(item.getPlayer().getObjectId()) : "-1");

            dialog = dialog.replaceFirst("%attrElement%", String.valueOf(item.getAttackElement()));
            dialog = dialog.replaceFirst("%attrValue%", String.valueOf(item.getAttackElementAndValue()));
			dialog = dialog.replaceFirst("%enchLevel%", String.valueOf(item.getEnchantLevel()));
			dialog = dialog.replaceFirst("%type%", String.valueOf(item.getItemType()));

			dialog = dialog.replaceFirst("%dropTime%", String.valueOf((int) item.getDropTimeOwner()));

			dialog = dialog.replaceFirst("%dropOwner%", item.getItemDropOwner() != null ? String.valueOf(item.getItemDropOwner()) : "none");
			dialog = dialog.replaceFirst("%dropOwnerId%", item.getItemDropOwner() != null ? String.valueOf(item.getItemDropOwner().getObjectId()) : "-1");

			dialog = dialog.replaceFirst("%loc%", String.valueOf(item.getLoc()));

			show(dialog, player);
		player.sendActionFailed();
		return false;
	}

	public String getElementNameById(int id)
	{
		switch(id)
		{
			case 0:
				return "Fire";
			case 1:
				return "Water";
			case 2:
				return "Wind";
			case 3:
				return "Earth";
			case 4:
				return "Sacred";
			case 5:
				return "Unholy";
			default:
				return "Not defined";
		}
	}
}