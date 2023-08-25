package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2TrapInstance;
import l2open.gameserver.serverpackets.NpcInfo;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class DetectTrap extends L2Skill
{
	public DetectTrap(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null && target.isTrap() && !((L2TrapInstance) target).isDetected())
			{
				L2TrapInstance trap = (L2TrapInstance) target;
				if(trap.getLevel() <= getPower())
				{
					trap.setDetected(true);
					for(L2Player player : L2World.getAroundPlayers(trap))
						if(player != null)
							player.sendPacket(new NpcInfo(trap, player));
				}
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}