package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Ride extends L2Skill
{
	public Ride(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;

		L2Player player = (L2Player) activeChar;
		if(getNpcId() != 0)
		{
			if(player.isInOlympiadMode())
			{
				player.sendPacket(Msg.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
				return false;
			}
			if(player.getDuel() != null || player.isSitting() || player.isInCombat() || player.isFishing() || player.isCursedWeaponEquipped() || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped() || player.getTransformation() != 0 || player.getPet() != null || player.isMounted() || player.isInVehicle())
			{
				player.sendPacket(Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
				return false;
			}
		}
		else if(getNpcId() == 0 && !player.isMounted())
			return false;

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character caster, GArray<L2Character> targets)
	{
		if(!caster.isPlayer())
			return;

		L2Player activeChar = (L2Player) caster;
		activeChar.setMount(getNpcId(), 0, 0);
	}
}