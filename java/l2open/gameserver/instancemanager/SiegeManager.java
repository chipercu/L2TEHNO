package l2open.gameserver.instancemanager;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.tables.SkillTable;

public abstract class SiegeManager
{
	public static void addSiegeSkills(L2Player character)
	{
		character.addSkill(SkillTable.getInstance().getInfo(246, 1), false);
		character.addSkill(SkillTable.getInstance().getInfo(247, 1), false);
		if(character.isNoble())
			character.addSkill(SkillTable.getInstance().getInfo(326, 1), false);

		if(character.getClan() != null && character.getClan().getHasCastle() > 0)
		{
			character.addSkill(SkillTable.getInstance().getInfo(844, 1), false);
			character.addSkill(SkillTable.getInstance().getInfo(845, 1), false);
		}
	}

	public static void removeSiegeSkills(L2Player character)
	{
		character.removeSkill(SkillTable.getInstance().getInfo(246, 1), false, true);
		character.removeSkill(SkillTable.getInstance().getInfo(247, 1), false, true);
		character.removeSkill(SkillTable.getInstance().getInfo(326, 1), false, true);

		if(character.getClan() != null && character.getClan().getHasCastle() > 0)
		{
			character.removeSkill(SkillTable.getInstance().getInfo(844, 1), false, true);
			character.removeSkill(SkillTable.getInstance().getInfo(845, 1), false, true);
		}
	}

	public static boolean getCanRide()
	{
		if(TerritorySiege.isInProgress())
			return false;
		for(Castle castle : CastleManager.getInstance().getCastles().values())
			if(castle != null && castle.getSiege().isInProgress())
				return false;
		for(Fortress fortress : FortressManager.getInstance().getFortresses().values())
			if(fortress != null && fortress.getSiege().isInProgress())
				return false;
		for(ClanHall clanhall : ClanHallManager.getInstance().getClanHalls().values())
			if(clanhall != null && clanhall.getSiege() != null && clanhall.getSiege().isInProgress())
				return false;
		return true;
	}

	public static Residence getSiegeUnitByObject(L2Object activeObject)
	{
		return getSiegeUnitByCoord(activeObject.getX(), activeObject.getY());
	}

	public static Residence getSiegeUnitByCoord(int x, int y)
	{
		for(Castle castle : CastleManager.getInstance().getCastles().values())
			if(castle.checkIfInZone(x, y))
				return castle;
		for(Fortress fortress : FortressManager.getInstance().getFortresses().values())
			if(fortress.checkIfInZone(x, y))
				return fortress;
		return null;
	}

	public static Siege getSiege(L2Object activeObject, boolean onlyActive)
	{
		return getSiege(activeObject.getX(), activeObject.getY(), onlyActive);
	}

	public static Siege getSiege(int x, int y, boolean onlyActive)
	{
		for(Castle castle : CastleManager.getInstance().getCastles().values())
			if(castle.getSiege().checkIfInZone(x, y, onlyActive))
				return castle.getSiege();
		for(Fortress fortress : FortressManager.getInstance().getFortresses().values())
			if(fortress.getSiege().checkIfInZone(x, y, onlyActive))
				return fortress.getSiege();
		for(ClanHall clanhall : ClanHallManager.getInstance().getClanHalls().values())
			if(clanhall.getSiege() != null && clanhall.getSiege().checkIfInZone(x, y, onlyActive))
				return clanhall.getSiege();
		return null;
	}

	public static void clearCastleRegistrations(L2Clan clan)
	{
		for(Castle castle : CastleManager.getInstance().getCastles().values())
			if(castle != null)
				castle.getSiege().clearSiegeClan(clan, true);
	}

	public static void clearFortressRegistrations(L2Clan clan)
	{
		for(Fortress fortress : FortressManager.getInstance().getFortresses().values())
			if(fortress != null)
				fortress.getSiege().clearSiegeClan(clan, true);
	}
}