package commands.voiced;

import java.text.NumberFormat;
import java.util.Locale;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.RadarControl;
import l2open.gameserver.skills.Calculator;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.tables.FakePlayersTable;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.util.Files;
import l2open.util.Util;

/**
 * @Author: Abaddon
 */
public class Help extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "help", "whoami", "whoiam", "whoapet", "heading", "whofake", "sweep",
			"pflag", "cflag", "exp", "stats" };

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		command = command.intern();
		if(command.equalsIgnoreCase("help"))
			return help(command, activeChar, args);
		if(command.equalsIgnoreCase("whoami") || command.equalsIgnoreCase("whoiam"))
			return whoami(command, activeChar, args);
		if(command.equalsIgnoreCase("whoapet"))
			return whoapet(command, activeChar, args);
		if(command.equalsIgnoreCase("stats"))
			return stats(activeChar);
		if(command.equalsIgnoreCase("heading"))
		{
			activeChar.sendMessage(String.valueOf(activeChar.getHeading()));
			return true;
		}
		if(command.equalsIgnoreCase("whofake"))
			return whofake(command, activeChar, args);
		if(command.equalsIgnoreCase("sweep"))
			return sweep(command, activeChar, args);
		if(command.equalsIgnoreCase("pflag"))
			return pflag(command, activeChar, args);
		if(command.equalsIgnoreCase("cflag"))
			return cflag(command, activeChar, args);
		if(command.equalsIgnoreCase("exp"))
			return exp(command, activeChar, args);

		return false;
	}

	private static NumberFormat df = NumberFormat.getNumberInstance();
	static
	{
		df.setMaximumFractionDigits(2);
	}

	private boolean exp(String command, L2Player activeChar, String args)
	{
		if(activeChar.getLevel() >= (activeChar.isSubClassActive() ? Experience.getMaxSubLevel() : Experience.getMaxLevel()))
			show("Maximum level!", activeChar);
		else
		{
			long exp = Experience.LEVEL[activeChar.getLevel() + 1] - activeChar.getExp();
			double count = 0;
			String ret = "Exp left: " + exp;
			if(count > 0)
				ret += "<br>Monsters left: " + df.format(count);
			show(ret, activeChar);
		}
		return true;
	}

	private boolean pflag(String command, L2Player activeChar, String args)
	{
		if(!activeChar.isInParty())
			return false;
		RadarControl rc = new RadarControl(0, 1, activeChar.getLoc());
		for(L2Player p : activeChar.getParty().getPartyMembers())
			if(p != activeChar)
				p.sendPacket(rc);
		return true;
	}

	private boolean cflag(String command, L2Player activeChar, String args)
	{
		if(activeChar.getClan() == null)
			return false;
		RadarControl rc = new RadarControl(0, 1, activeChar.getLoc());
		for(L2Player p : activeChar.getClan().getOnlineMembers(activeChar.getObjectId()))
			p.sendPacket(rc);
		return true;
	}

	private boolean help(String command, L2Player activeChar, String args)
	{
		String dialog = Files.read("data/scripts/commands/voiced/help.htm", activeChar);
		show(dialog, activeChar);
		return true;
	}

	private boolean whoapet(String command, L2Player activeChar, String args)
	{
		if(activeChar == null)
			return false;
		showInfo(activeChar, activeChar.getPet());
		return true;
	}

	private boolean whoami(String command, L2Player activeChar, String args)
	{
		showInfo(activeChar, activeChar);
		return true;
	}

	private boolean whofake(String command, L2Player activeChar, String args)
	{
		if(!activeChar.isGM())
			return false;
		StringBuilder sb = new StringBuilder("");
		for(String p : FakePlayersTable.getActiveFakePlayers())
			sb.append(p).append(" ");
		show(sb.toString(), activeChar);
		return true;
	}

	private boolean sweep(String command, L2Player activeChar, String args)
	{
		if(activeChar.getSkillLevel(42) > 0)
			for(L2Character target : activeChar.getAroundCharacters(300, 200))
				if(target.isMonster() && target.isDead() && ((L2MonsterInstance) target).isSweepActive())
				{
					activeChar.getAI().Cast(activeChar.getKnownSkill(42), target);
					return true;
				}
		return false;
	}

	public static void showInfo(L2Player caller, L2Character cha)
	{
		if(caller == null || cha == null)
			return;
		StringBuilder dialog = new StringBuilder("<html><body>");

		NumberFormat df = NumberFormat.getNumberInstance(Locale.ENGLISH);
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(0);

		/*
		dialog.append("<center><font color=\"LEVEL\">Basic info</font></center><br><table width=\"100%\"><tr>");

		dialog.append("<td>Name</td><td>").append(cha.getName()).append("</td>");
		dialog.append("<td>Login</td><td>").append(cha.getAccountName()).append("</td>");
		dialog.append("</tr><tr>");
		dialog.append("<td>Class</td><td>").append(cha.getClassId().name()).append("</td>");
		dialog.append("<td>IP</td><td>").append(cha.getNetConnection().getIpAddr()).append("</td>");
		dialog.append("</tr><tr>");
		dialog.append("<td>Level</td><td>").append(cha.getLevel()).append("</td>");
		dialog.append("<td>ObjId</td><td>").append(cha.getObjectId()).append("</td>");
		dialog.append("</tr></table><br>");
		*/

		dialog.append("<center><font color=\"LEVEL\">Stats</font></center><br><table width=\"100%\"><tr>");

		dialog.append("<td>HP regen</td><td>").append(df.format(Formulas.calcHpRegen(cha))).append("</td>");
		dialog.append("<td>CP regen</td><td>").append(df.format(Formulas.calcCpRegen(cha))).append("</td>");
		dialog.append("</tr><tr>");
		dialog.append("<td>MP regen</td><td>").append(df.format(Formulas.calcMpRegen(cha))).append("</td>");
		dialog.append("<td>HP drain</td><td>").append(df.format(cha.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0, null, null))).append("%</td>");
		dialog.append("</tr><tr>");
		dialog.append("<td>HP gain</td><td>").append(df.format(cha.calcStat(Stats.HEAL_EFFECTIVNESS, 100, null, null))).append("%</td>");
		dialog.append("<td>MP gain</td><td>").append(df.format(cha.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100, null, null))).append("%</td>");
		dialog.append("</tr><tr>");
		//dialog.append("<td>Crit damage</td><td>").append(df.format(cha.calcStat(Stats.CRITICAL_DAMAGE, null, null))).append("% + ").append((int) cha.calcStat(Stats.CRITICAL_DAMAGE_STATIC, null, null)).append("</td>");
		dialog.append("<td>Crit damage</td><td>").append(df.format(2 * cha.calcStat(Stats.CRITICAL_DAMAGE, null, null))).append("% + ").append((int) cha.calcStat(Stats.CRITICAL_DAMAGE_STATIC, null, null)).append("</td>");
		dialog.append("<td>Magic crit</td><td>").append(df.format(cha.getMagicCriticalRate(null, null))).append("%</td>");
		dialog.append("</tr><tr>");
		dialog.append("<td>Blow rate</td><td>x").append(df.format(cha.calcStat(Stats.FATALBLOW_RATE, null, null))).append("</td>");
		dialog.append("<td>MCrit damage</td><td>x").append(df.format(cha.calcStat(Stats.MCRITICAL_DAMAGE, 2.5, null, null))).append("</td>");

		L2ItemInstance shld = cha.getSecondaryWeaponInstance();
		if(shld != null && shld.getItemType() == WeaponType.NONE)
		{
			dialog.append("</tr><tr>");
			dialog.append("<td>Shield def</td><td>").append(cha.getShldDef()).append("</td>");
			dialog.append("<td>Shield rate</td><td>").append(df.format(cha.calcStat(Stats.SHIELD_RATE, null, null))).append("</td>");
		}
		dialog.append("</tr><tr>");
		L2Weapon weaponItem = cha.getActiveWeaponItem();
		if(weaponItem != null && (weaponItem.getItemType() == WeaponType.BOW || weaponItem.getItemType() == WeaponType.CROSSBOW))
			dialog.append("<td>Attack dist</td><td>").append(cha.getPhysicalAttackRange()).append("</td>");

		dialog.append("<td>Bonus</td><td>");
		if(caller.getBonus().RATE_XP*caller.getHwidBonus() == 1)
			dialog.append("<font color=FF0000>x").append(caller.getBonus().RATE_XP*caller.getHwidBonus());
		else
			dialog.append("<font color=00FF00>x").append(caller.getBonus().RATE_XP*caller.getHwidBonus());
		dialog.append("</font></td>");
		

		dialog.append("</tr></table><br><center><font color=\"LEVEL\">Resists</font></center><br><table width=\"70%\">");

		int FIRE_RECEPTIVE = (int) cha.calcStat(Stats.FIRE_RECEPTIVE, 0, null, null);
		if(FIRE_RECEPTIVE != 0)
			dialog.append("<tr><td>Fire</td><td>").append(-FIRE_RECEPTIVE).append("</td></tr>");

		int WIND_RECEPTIVE = (int) cha.calcStat(Stats.WIND_RECEPTIVE, 0, null, null);
		if(WIND_RECEPTIVE != 0)
			dialog.append("<tr><td>Wind</td><td>").append(-WIND_RECEPTIVE).append("</td></tr>");

		int WATER_RECEPTIVE = (int) cha.calcStat(Stats.WATER_RECEPTIVE, 0, null, null);
		if(WATER_RECEPTIVE != 0)
			dialog.append("<tr><td>Water</td><td>").append(-WATER_RECEPTIVE).append("</td></tr>");

		int EARTH_RECEPTIVE = (int) cha.calcStat(Stats.EARTH_RECEPTIVE, 0, null, null);
		if(EARTH_RECEPTIVE != 0)
			dialog.append("<tr><td>Earth</td><td>").append(-EARTH_RECEPTIVE).append("</td></tr>");

		int SACRED_RECEPTIVE = (int) cha.calcStat(Stats.SACRED_RECEPTIVE, 0, null, null);
		if(SACRED_RECEPTIVE != 0)
			dialog.append("<tr><td>Light</td><td>").append(-SACRED_RECEPTIVE).append("</td></tr>");

		int UNHOLY_RECEPTIVE = (int) cha.calcStat(Stats.UNHOLY_RECEPTIVE, 0, null, null);
		if(UNHOLY_RECEPTIVE != 0)
			dialog.append("<tr><td>Darkness</td><td>").append(-UNHOLY_RECEPTIVE).append("</td></tr>");

		if(cha.getTraitStat().trait_sword != 1)
			dialog.append("<tr><td>trait_sword: </td><td>").append(cha.getTraitStat().trait_sword*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_blunt != 1)
			dialog.append("<tr><td>trait_blunt: </td><td>").append(cha.getTraitStat().trait_blunt*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_dagger != 1)
			dialog.append("<tr><td>trait_dagger: </td><td>").append(cha.getTraitStat().trait_dagger*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_bow != 1)
			dialog.append("<tr><td>trait_bow: </td><td>").append(cha.getTraitStat().trait_bow*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_pole != 1)
			dialog.append("<tr><td>trait_pole: </td><td>").append(cha.getTraitStat().trait_pole*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_fist != 1)
			dialog.append("<tr><td>trait_fist: </td><td>").append(cha.getTraitStat().trait_fist*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_dual != 1)
			dialog.append("<tr><td>trait_dual: </td><td>").append(cha.getTraitStat().trait_dual*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_dualfist != 1)
			dialog.append("<tr><td>trait_dualfist: </td><td>").append(cha.getTraitStat().trait_dualfist*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_rapier != 1)
			dialog.append("<tr><td>trait_rapier: </td><td>").append(cha.getTraitStat().trait_rapier*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_crossbow != 1)
			dialog.append("<tr><td>trait_crossbow: </td><td>").append(cha.getTraitStat().trait_crossbow*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_ancientsword != 1)
			dialog.append("<tr><td>trait_ancientsword: </td><td>").append(cha.getTraitStat().trait_ancientsword*-1).append("</td></tr>");
		if(cha.getTraitStat().trait_dualdagger != 1)
			dialog.append("<tr><td>trait_dualdagger: </td><td>").append(cha.getTraitStat().trait_dualdagger*-1).append("</td></tr>");

		if(cha.getTraitStat().trait_bleed != 0)
			dialog.append("<tr><td>trait_bleed: </td><td>").append(cha.getTraitStat().trait_bleed).append("</td></tr>");
		if(cha.getTraitStat().trait_poison != 0)
			dialog.append("<tr><td>trait_poison: </td><td>").append(cha.getTraitStat().trait_poison).append("</td></tr>");
		if(cha.getTraitStat().trait_shock != 0)
			dialog.append("<tr><td>trait_shock: </td><td>").append(cha.getTraitStat().trait_shock).append("</td></tr>");
		if(cha.getTraitStat().trait_hold != 0)
			dialog.append("<tr><td>trait_hold: </td><td>").append(cha.getTraitStat().trait_hold).append("</td></tr>");
		if(cha.getTraitStat().trait_sleep != 0)
			dialog.append("<tr><td>trait_sleep: </td><td>").append(cha.getTraitStat().trait_sleep).append("</td></tr>");
		if(cha.getTraitStat().trait_paralyze != 0)
			dialog.append("<tr><td>trait_paralyze: </td><td>").append(cha.getTraitStat().trait_paralyze).append("</td></tr>");
		if(cha.getTraitStat().trait_derangement != 0)
			dialog.append("<tr><td>trait_derangement: </td><td>").append(cha.getTraitStat().trait_derangement).append("</td></tr>");
			
		if(cha.getTraitStat().trait_bleed_power != 0)
			dialog.append("<tr><td>trait_bleed_power: </td><td>").append(cha.getTraitStat().trait_bleed_power).append("</td></tr>");
		if(cha.getTraitStat().trait_poison_power != 0)
			dialog.append("<tr><td>trait_poison_power: </td><td>").append(cha.getTraitStat().trait_poison_power).append("</td></tr>");
		if(cha.getTraitStat().trait_shock_power != 0)
			dialog.append("<tr><td>trait_shock_power: </td><td>").append(cha.getTraitStat().trait_shock_power).append("</td></tr>");
		if(cha.getTraitStat().trait_hold_power != 0)
			dialog.append("<tr><td>trait_hold_power: </td><td>").append(cha.getTraitStat().trait_hold_power).append("</td></tr>");
		if(cha.getTraitStat().trait_sleep_power != 0)
			dialog.append("<tr><td>trait_sleep_power: </td><td>").append(cha.getTraitStat().trait_sleep_power).append("</td></tr>");
		if(cha.getTraitStat().trait_paralyze_power != 0)
			dialog.append("<tr><td>trait_paralyze_power: </td><td>").append(cha.getTraitStat().trait_paralyze_power).append("</td></tr>");
		if(cha.getTraitStat().trait_derangement_power != 0)
			dialog.append("<tr><td>trait_derangement_power: </td><td>").append(cha.getTraitStat().trait_derangement_power).append("</td></tr>");

		int DEBUFF_RECEPTIVE = (int) cha.calcStat(Stats.DEBUFF_RECEPTIVE, null, null);
		if(DEBUFF_RECEPTIVE != 0)
			dialog.append("<tr><td>Debuff</td><td>").append(DEBUFF_RECEPTIVE).append("</td></tr>");

		int CANCEL_RECEPTIVE = (int) cha.calcStat(Stats.CANCEL_RECEPTIVE, null, null);
		if(CANCEL_RECEPTIVE != 0)
			dialog.append("<tr><td>Cancel</td><td>").append(CANCEL_RECEPTIVE).append("</td></tr>");

		int CRIT_CHANCE_RECEPTIVE = 100 - (int) cha.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, null, null);
		if(CRIT_CHANCE_RECEPTIVE != 0)
			dialog.append("<tr><td>Crit get chance</td><td>").append(CRIT_CHANCE_RECEPTIVE).append("%</td></tr>");

		int CRIT_DAMAGE_RECEPTIVE = 100 - (int) cha.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, null, null);
		if(CRIT_DAMAGE_RECEPTIVE != 0)
			dialog.append("<tr><td>Crit get damage</td><td>").append(CRIT_DAMAGE_RECEPTIVE).append("%</td></tr>");

		/*if(FIRE_RECEPTIVE == 0 && WIND_RECEPTIVE == 0 && WATER_RECEPTIVE == 0 && EARTH_RECEPTIVE == 0 && UNHOLY_RECEPTIVE == 0 && SACRED_RECEPTIVE // primary elements
		== 0 && trait_bleed == 0 && trait_shock // phys debuff
		== 0 && trait_poison == 0 && trait_hold == 0 && trait_sleep == 0 && trait_paralyze == 0 && trait_derangement == 0 && DEBUFF_RECEPTIVE == 0 && CANCEL_RECEPTIVE // mag debuff
		== 0 && SWORD_WPN_RECEPTIVE == 0 && DUAL_WPN_RECEPTIVE == 0 && BLUNT_WPN_RECEPTIVE == 0 && DAGGER_WPN_RECEPTIVE == 0 && BOW_WPN_RECEPTIVE == 0 && POLE_WPN_RECEPTIVE == 0 && FIST_WPN_RECEPTIVE // weapons
		== 0 && CRIT_CHANCE_RECEPTIVE == 0 && CRIT_DAMAGE_RECEPTIVE == 0 // other
		)
			dialog.append("</table>No resists</body></html>");
		else*/
			dialog.append("</table></body></html>");
		show(dialog.toString(), caller, null);
	}

	private static boolean stats(L2Player player)
	{
		if(player == null)
			return false;

		StringBuilder dialog = new StringBuilder("<html><body>");

		dialog.append("<center><font color=\"LEVEL\">All Stats</font></center><br><br>");

		Calculator[] calculators = player.getCalculators();
		if(calculators == null || calculators.length == 0)
			dialog.append("None");
		else
			for(Calculator c : calculators)
				dialog.append("[scripts_commands.voiced.Help:showfuncs ").append(c._stat.getValue()).append("|").append(c._stat.getValue()).append("]<br1>");

		dialog.append("</body></html>");

		show(dialog.toString(), player, null);
		return true;
	}

	public void showstats()
	{
		stats((L2Player) getSelf());
	}

	public void showfuncs(String[] var)
	{
		L2Player player = (L2Player) getSelf();
		if(var.length != 1)
		{
			show("Некорректные данные", player);
			return;
		}

		String value = var[0];
		Stats stat = Stats.valueOfXml(value);

		StringBuilder dialog = new StringBuilder("<html><body>");

		dialog.append("<table><tr>");
		dialog.append("<td><button value=\"Back\" action=\"bypass -h scripts_commands.voiced.Help:showstats\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		dialog.append("<td><button value=\"Refresh\" action=\"bypass -h scripts_commands.voiced.Help:showfuncs ").append(value).append("\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		dialog.append("</tr></table>");

		dialog.append("<br><center><font color=\"LEVEL\">Stat: ").append(stat).append("</font></center><br><br>");

		Calculator[] calculators = player.getCalculators();
		if(calculators == null || calculators.length == 0 || stat == null)
			dialog.append("None");
		else
		{
			Calculator c = calculators[stat.ordinal()];
			Func[] funcs = c.getFunctions();
			for(int i = 0; i < funcs.length; i++)
				if(funcs[i]._funcOwner != null)
					dialog.append(funcs[i]._funcOwner.toString()).append(" [").append(Integer.toHexString(funcs[i]._order)).append("]<br1>");
				else
					dialog.append(funcs[i].getClass().getSimpleName()).append(" [").append(Integer.toHexString(funcs[i]._order)).append("]<br1>");
		}

		dialog.append("</body></html>");

		show(dialog.toString(), player);
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}