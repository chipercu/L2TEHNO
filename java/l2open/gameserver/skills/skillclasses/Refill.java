package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Refill extends L2Skill
{
	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(target == null || !target.isPlayer() || !target.isInVehicle() || !target.getPlayer().getVehicle().isAirShip())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_id, _level));
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	public Refill(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead() || !target.isPlayer() || !target.isInVehicle() || !target.getPlayer().getVehicle().isAirShip())
					continue;
				L2AirShip airship = (L2AirShip) target.getPlayer().getVehicle();
				airship.setFuel(airship.getFuel() + (int) _power);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}