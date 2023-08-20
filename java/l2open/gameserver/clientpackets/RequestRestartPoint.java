package l2open.gameserver.clientpackets;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.residence.ResidenceFunction;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.Die;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.MapRegion;
import l2open.util.Location;

public class RequestRestartPoint extends L2GameClientPacket
{
	protected int _requestedPointType;
	protected boolean _continuation;

	private static final int TO_VILLAGE = 0;
	private static final int TO_CLANHALL = 1;
	private static final int TO_CASTLE = 2;
	private static final int TO_FORTRESS = 3;
	private static final int TO_SIEGEHQ = 4;
	private static final int FIXED = 5;
	private static final int AGATHION = 6;
	private static final int EVENT_MASTER = 7;

	/**
	 * packet type id 0x7D
	 * format:    cd
	 */
	@Override
	public void readImpl()
	{
		_requestedPointType = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		if(activeChar.isFakeDeath())
		{
			activeChar.breakFakeDeath();
			return;
		}

		if(!activeChar.isDead() && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		if((activeChar.getTeam() > 0 || activeChar.isInEvent() > 0) && activeChar.isRestartPoint() > 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		// Запрещаем воскрешение во время эвента CtF
		try
		{
			if(activeChar.getTeam() > 0 && ZoneManager.getInstance().checkIfInZoneAndIndex(ZoneType.battle_zone, 4, activeChar) && (Boolean) Functions.callScripts("events.CtF.CtF", "isRunned", new Object[] {}))
			{
				activeChar.sendActionFailed();
				return;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		int _requested_point_type = _requestedPointType;
		try
		{
			if(activeChar.isFestivalParticipant())
			{
				activeChar.doRevive();
				activeChar.teleToLocation(activeChar.getLoc());
				return;
			}
			else if(activeChar.getEventMaster() != null)
				_requestedPointType = EVENT_MASTER;

			Location loc = null;
			int ref = 0;

			boolean isInDefense = false;
			L2Clan clan = activeChar.getClan();
			Siege siege = SiegeManager.getSiege(activeChar, true);

			switch(_requestedPointType)
			{
				case TO_CLANHALL:
					if(clan == null || clan.getHasHideout() == 0)
						loc = MapRegion.getTeleToClosestTown(activeChar);
					else
					{
						ClanHall clanHall = activeChar.getClanHall();
						loc = MapRegion.getTeleToClanHall(activeChar);
						if(clanHall.getFunction(ResidenceFunction.RESTORE_EXP) != null)
							activeChar.restoreExp(clanHall.getFunction(ResidenceFunction.RESTORE_EXP).getLevel());
					}
					break;
				case TO_CASTLE:
					isInDefense = false;
					if(siege != null && siege.checkIsDefender(clan))
						isInDefense = true;
					if((clan == null || clan.getHasCastle() == 0) && !isInDefense)
					{
						sendPacket(Msg.ActionFail, new Die(activeChar));
						return;
					}
					Castle castle = activeChar.getCastle();
					loc = MapRegion.getTeleToCastle(activeChar);
					if(castle.getFunction(ResidenceFunction.RESTORE_EXP) != null)
						activeChar.restoreExp(castle.getFunction(ResidenceFunction.RESTORE_EXP).getLevel());
					break;
				case TO_FORTRESS:
					isInDefense = false;
					if(siege != null && siege.checkIsDefender(clan))
						isInDefense = true;
					if((clan == null || clan.getHasFortress() == 0) && !isInDefense)
					{
						sendPacket(Msg.ActionFail, new Die(activeChar));
						return;
					}
					Fortress fort = activeChar.getFortress();
					loc = MapRegion.getTeleToFortress(activeChar);
					if(fort.getFunction(ResidenceFunction.RESTORE_EXP) != null)
						activeChar.restoreExp(fort.getFunction(ResidenceFunction.RESTORE_EXP).getLevel());
					break;
				case TO_SIEGEHQ:
					SiegeClan siegeClan = null;
					if(siege != null)
						siegeClan = siege.getAttackerClan(clan);
					else if(TerritorySiege.checkIfInZone(activeChar))
						siegeClan = TerritorySiege.getSiegeClan(clan);
					if(siegeClan == null || siegeClan.getHeadquarter() == null)
					{
						sendPacket(Msg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE, new Die(activeChar));
						return;
					}
					loc = MapRegion.getTeleToHeadquarter(activeChar);
					break;
				case AGATHION:
					if(activeChar.isAgathionResAvailable())
						activeChar.doRevive(100);
					else
					{
						activeChar.sendActionFailed();
						activeChar.sendPacket(new Die(activeChar));
					}
					break;
				case FIXED:
					if(activeChar.getPlayerAccess().ResurectFixed)
						activeChar.doRevive(100);
					else if(Functions.removeItem(activeChar, 13300, 1) == 1)
					{
						activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT));
						activeChar.doRevive(100);
					}
					else if(Functions.removeItem(activeChar, 10649, 1) == 1)
					{
						activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT));
						activeChar.doRevive(100);
					}
					else
					{
						activeChar.sendActionFailed(); 
						return;
					}
					break;
				case EVENT_MASTER:
					loc = activeChar.getEventMaster().doReviveLoc(activeChar, _requested_point_type);
					ref = loc.id;
					break;
				case TO_VILLAGE:
				default:
					loc = MapRegion.getTeleToClosestTown(activeChar);
					break;
			}

			// зачем здесь это?
			//if(activeChar.getTransformation() > 0 && !activeChar.isBlessedByNoblesse())
			//	activeChar.setTransformation(0);
			if(_requestedPointType != FIXED && _requestedPointType != AGATHION)
				activeChar.doRevive();
			if(loc != null)
				activeChar.teleToLocation(loc, ref);
		}
		catch(Throwable e)
		{}
	}
}