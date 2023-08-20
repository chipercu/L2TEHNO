package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class ClanGate extends L2Skill
{
	public ClanGate(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(final L2Character activeChar, final L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;

		L2Player player = activeChar.getPlayer();

		if(!player.isClanLeader())
		{
			player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return false;
		}

		if (activeChar.isAlikeDead() || activeChar.isInOlympiadMode() || activeChar.inObserverMode() || activeChar.isFlying() || activeChar.getPlayer().isFestivalParticipant())
		{
			player.sendPacket(Msg.NOTHING_HAPPENED);
			return false;
		}

		if(activeChar.isInZoneBattle() || activeChar.isInZone(L2Zone.ZoneType.Siege) || activeChar.isInZone(L2Zone.ZoneType.no_restart) || activeChar.isInZone(L2Zone.ZoneType.no_summon) || activeChar.isInVehicle() || activeChar.getReflection().getId() != 0)
		{
			player.sendPacket(Msg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
			return false;
		}

		if(activeChar.getPlayer().isInStoreMode())
		{
			player.sendPacket(Msg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS);
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}
	
	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!activeChar.isPlayer())
			return;

		L2Player player = (L2Player) activeChar;
		L2Clan clan = player.getClan();
		clan.broadcastToOtherOnlineMembers(new SystemMessage(SystemMessage.COURT_MAGICIAN__THE_PORTAL_HAS_BEEN_CREATED), player);


		getEffects(activeChar, activeChar, getActivateRate() > 0, true);
	}

}
