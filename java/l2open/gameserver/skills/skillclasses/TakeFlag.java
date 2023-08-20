package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2SiegeHeadquarterInstance;
import l2open.gameserver.model.instances.L2TerritoryFlagInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class TakeFlag extends L2Skill
{
	private final int _event_type;

	public TakeFlag(StatsSet set)
	{
		super(set);
		_event_type = set.getInteger("event_type", 0);
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;
		else if(activeChar == null || !activeChar.isPlayer() || activeChar.isOutOfControl() || activeChar.getDuel() != null)
			return false;

		L2Player player = (L2Player) activeChar;

		if(player.getClan() == null && _event_type == 0)
			return false;

		int siegeEvent1 = player.getTerritorySiege();
		if(siegeEvent1 == -1 && _event_type == 0)
			return false;
		else if(player.isMounted())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_id, _level));
			return false;
		}
		else if(_event_type == 0 && (target == null || !(target instanceof L2SiegeHeadquarterInstance) || target.getNpcId() != 36590 || ((L2SiegeHeadquarterInstance) target).getClan() != player.getClan()) || _event_type == 1 && (target == null || target.getNpcId() != activeChar.getEventMaster().flag1id && target.getNpcId() != activeChar.getEventMaster().flag2id))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_id, _level));
			return false;
		}
		else if(!player.isTerritoryFlagEquipped() && (_event_type == 0 || (_event_type == 2 && !player.isCombatFlagEquipped())))
			return false;

		int diff = player.getZ()-target.getZ();
		// Если цель не в радиусе 120, если цель на 50 выше или ниже, не даем возможность кастануть...
		if(!player.isInRange(target, 150) || diff+100 < 0 || diff-100 > 0)
		{
			player.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
			return false;
		}

		if(_event_type == 0 && (player.isInZone(ZoneType.Siege) || player.isInZone(ZoneType.Castle)))
			return true;
		else if(_event_type == 1 && activeChar.getEventMaster() != null ||  _event_type == 2 && player.isInZone(ZoneType.event) && activeChar.getEventMaster() != null)
			return true;
		else if(_event_type == 2)
			player.sendMessage("Вы находитесь не в зоне кастовальни.");
		/*int siegeEvent2 = target.getPlayer().getTerritorySiege();
		if(siegeEvent2 == -1 || siegeEvent1 != siegeEvent2)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_id, _level));
			return false;
		}*/
		/* TODO сообщение
		if(first)
			for(SiegeClan sc : siege.getDefenderClans().values())
			{
				L2Clan clan = sc.getClan();
				if(clan != null)
					clan.broadcastToOnlineMembers(Msg.THE_OPPONENT_CLAN_HAS_BEGUN_TO_ENGRAVE_THE_RULER);
			}
			*/
		return false;
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null && _event_type == 0)
			{
				L2Player player = (L2Player) activeChar;
				if(!(target instanceof L2SiegeHeadquarterInstance) || target.getNpcId() != 36590 || ((L2SiegeHeadquarterInstance) target).getClan() != player.getClan())
					continue;
				if(player.getTerritorySiege() > -1 && player.isTerritoryFlagEquipped())
				{
					L2ItemInstance flag = player.getActiveWeaponInstance();
					if(flag != null && flag.getCustomType1() != 77) // 77 это эвентовый флаг
					{
						L2TerritoryFlagInstance flagNpc = TerritorySiege.getNpcFlagByItemId(flag.getItemId());
						flagNpc.engrave(player);
					}
				}
			}
			else if(target != null && activeChar.getEventMaster() != null)
			{
				if(!activeChar.getPlayer().isTerritoryFlagEquipped() && !activeChar.getPlayer().isCombatFlagEquipped() && _event_type == 1)
					activeChar.getEventMaster().captureFlag(activeChar.getPlayer(), target);
				else if((activeChar.getPlayer().isTerritoryFlagEquipped() || activeChar.getPlayer().isCombatFlagEquipped()) && _event_type == 2)
					activeChar.getEventMaster().mountingFlag(activeChar.getPlayer());
				break;
			}
	}
}