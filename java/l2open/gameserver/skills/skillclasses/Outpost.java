package l2open.gameserver.skills.skillclasses;

import l2open.config.ConfigValue;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2SiegeHeadquarterInstance;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.StatsSet;
import l2open.util.*;

import java.util.logging.Logger;

// 844(build), 845
public class Outpost extends L2Skill
{
	protected static Logger _log = Logger.getLogger(Outpost.class.getName());
	private final boolean _build;

	public Outpost(StatsSet set)
	{
		super(set);
		_build = set.getBool("build", true);
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(activeChar == null || !activeChar.isPlayer() || activeChar.isOutOfControl() || activeChar.getDuel() != null)
			return false;
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		L2Player player = (L2Player) activeChar;
		L2Clan clan = player.getClan();
		if(clan == null || !player.isClanLeader())
		{
			//_log.warning(player.toFullString() + " has " + toString() + ", but he isn't in a clan leader.");
			return false;
		}

		if(player.isInZone(ZoneType.siege_residense) || player.isInZone(ZoneType.RESIDENCE) || player.isInZone(ZoneType.block_outpost) || !TerritorySiege.checkIfInZone(player))
		{
			activeChar.sendMessage("Outpost can't be placed here.");
			return false;
		}

		SiegeClan siegeClan = TerritorySiege.getSiegeClan(clan);
		if(player.getTerritorySiege() == -1 || siegeClan == null)
		{
			activeChar.sendMessage("You must be registered to place a Outpost.");
			return false;
		}

		if(_build && siegeClan.getHeadquarter() != null)
		{
			activeChar.sendMessage("You already has a Outpost.");
			return false;
		}

		if(!_build && (siegeClan.getHeadquarter() == null || player.getDistance(siegeClan.getHeadquarter()) > 500))
			return false;

		int x = (int) (activeChar.getX() + 250 * Math.cos(activeChar.headingToRadians(activeChar.getHeading() - 32768)));
		int y = (int) (activeChar.getY() + 250 * Math.sin(activeChar.headingToRadians(activeChar.getHeading() - 32768)));
		int x1 = (int) (activeChar.getX() + 130 * Math.cos(activeChar.headingToRadians(activeChar.getHeading() - 32768)));
		int y1 = (int) (activeChar.getY() + 130 * Math.sin(activeChar.headingToRadians(activeChar.getHeading() - 32768)));

		boolean isWater = activeChar.isInWater() || L2World.isWater(x, y, GeoEngine.getNextZ(x1, y1));

		//activeChar.sendMessage("Z="+activeChar.getZ()+" getNextZ="+GeoEngine.getNextZ(x, y)+" Z2="+(activeChar.getZ() + 32)+" getNextZ2="+(GeoEngine.getNextZ(x, y)+32)+" true="+(GeoEngine.canSeeCoord(activeChar.getX(), activeChar.getY(), activeChar.getZ(), x, y, GeoEngine.getNextZ(x, y), false, 0)) + " result: "+(activeChar.getZ() - GeoEngine.getNextZ(x, y)) + " result2: "+(GeoEngine.getNextZ(x, y) - activeChar.getZ()));
		if(_build && (isWater || !GeoEngine.canSeeCoord(activeChar.getX(), activeChar.getY(), activeChar.getZ() + 32, x, y, GeoEngine.getNextZ(x, y)+32, false, 0) || !(activeChar.getZ() - GeoEngine.getNextZ(x1, y1) < 42 && activeChar.getZ() - GeoEngine.getNextZ(x1, y1) > -42)))
		{
			activeChar.sendMessage("Простите но здесь нельзя установить палатку.");
			return false;
		}
		else if(ConfigValue.EnableCustomZoneToOutpost && !ZoneManager.getInstance().checkIfInZone(ZoneType.allow_outpost, x1, y1, -1))
		{
			activeChar.sendMessage("Простите но здесь нельзя установить палатку.");
			return false;
		}
		return true;
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		L2Player player = (L2Player) activeChar;

		L2Clan clan = player.getClan();
		if(clan == null || !player.isClanLeader())
			return;

		if(!TerritorySiege.checkIfInZone(player) || clan.getTerritorySiege() == -1 || player.isInZone(ZoneType.siege_residense) || player.isInZone(ZoneType.RESIDENCE))
			return;

		SiegeClan siegeClan = TerritorySiege.getSiegeClan(clan);
		if(siegeClan == null)
			return;

		if(_build)
		{
			// Ставим аутпост перед чаром
			int x = (int) (player.getX() + 130 * Math.cos(player.headingToRadians(player.getHeading() - 32768)));
			int y = (int) (player.getY() + 130 * Math.sin(player.headingToRadians(player.getHeading() - 32768)));

			L2SiegeHeadquarterInstance outpost = new L2SiegeHeadquarterInstance(player, IdFactory.getInstance().getNextId(), NpcTable.getTemplate(36590));
			outpost.setCurrentHpMp(outpost.getMaxHp(), outpost.getMaxMp(), true);
			outpost.setHeading(player.getHeading()+32768); // Разворачиваем входом к роже чара.
			outpost.setName(clan.getName());
			outpost.setInvul(true);				
			outpost.spawnMe(new Location(x, y, GeoEngine.getNextZ(x, y)));

			siegeClan.setHeadquarter(outpost);
		}
		else
		{
			L2SiegeHeadquarterInstance outpost = siegeClan.getHeadquarter();
			if(outpost == null)
				return;

			outpost.deleteMe();
			siegeClan.setHeadquarter(null);
		}
	}
}