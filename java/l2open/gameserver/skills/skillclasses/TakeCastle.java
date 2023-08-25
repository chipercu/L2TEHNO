package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class TakeCastle extends L2Skill
{
	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;
		else if(activeChar == null || !activeChar.isPlayer() || activeChar.isOutOfControl() || activeChar.getDuel() != null || activeChar.getTeam() > 0)
			return false;

		L2Player player = (L2Player) activeChar;
		if(player.getClan() == null || !player.isClanLeader() && player.getEventMaster() == null)
			return false;

		Siege siege = SiegeManager.getSiege(activeChar, true);

		if(activeChar.getEventMaster() == null || activeChar.getEventMaster()._ref == null || activeChar.getEventMaster()._ref.getId() != activeChar.getReflectionId())
		{
			if(siege == null || siege.getSiegeUnit().getType() != ResidenceType.Castle || siege.getAttackerClan(player.getClan()) == null)
				return false;
		}
		else if(activeChar.getEventMaster()._defender_clan != null && activeChar.getEventMaster()._defender_clan.getClanId() == activeChar.getClanId())
			return false;

		if(player.isMounted())
			return false;
		int diff = player.getZ()-target.getZ();

		// Если цель не в радиусе 150, если цель на 100 выше или ниже, не даем возможность кастануть...
		if(!player.isInRange(target, 150) || diff+100 < 0 || diff-100 > 0)
		{
			player.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
			return false;
		}

		if(first)
		{
			if(activeChar.getEventMaster() == null)
				for(SiegeClan sc : siege.getDefenderClans().values())
				{
					L2Clan clan = sc.getClan();
					if(clan != null)
						clan.broadcastToOnlineMembers(Msg.THE_OPPONENT_CLAN_HAS_BEGUN_TO_ENGRAVE_THE_RULER);
				}
			else if(activeChar.getEventMaster()._defender_clan != null)
			{
				L2Clan clan = activeChar.getEventMaster()._defender_clan.getClan();
				if(clan != null)
					clan.broadcastToOnlineMembers(Msg.THE_OPPONENT_CLAN_HAS_BEGUN_TO_ENGRAVE_THE_RULER);
			}
		}

		return true;
	}

	public TakeCastle(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				if(!target.isArtefact())
					continue;
				L2Player player = (L2Player) activeChar;
				if(activeChar.getEventMaster() == null)
				{
					Siege siege = SiegeManager.getSiege(activeChar, true);
					if(siege != null)
						if(siege.Engrave(player.getClan(), target.getObjectId()))
							siege.announceToPlayer(new SystemMessage(SystemMessage.CLAN_S1_HAS_SUCCEEDED_IN_ENGRAVING_THE_RULER).addString(player.getClan().getName()), false, true);
				}
				else
					activeChar.getEventMaster().Engrave(player, target.getObjectId());
			}
	}
}