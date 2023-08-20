package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class TeleportNpc extends L2Skill
{
	public TeleportNpc(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null && !target.isDead())
			{
				getEffects(activeChar, target, getActivateRate() > 0, false);
				target.abortAttack(true, true);
				target.abortCast(true);
				target.stopMove();
				int x = activeChar.getX();
				int y = activeChar.getY();
				int z = activeChar.getZ();
				int h = activeChar.getHeading();
				int range = (int) (activeChar.getColRadius() + target.getColRadius());
				int hyp = (int) Math.sqrt(range * range / 2);
				if(h < 16384)
				{
					x += hyp;
					y += hyp;
				}
				else if(h > 16384 && h <= 32768)
				{
					x -= hyp;
					y += hyp;
				}
				else if(h < 32768 && h <= 49152)
				{
					x -= hyp;
					y -= hyp;
				}
				else if(h > 49152)
				{
					x += hyp;
					y -= hyp;
				}
				target.setXYZ(x, y, z);
				target.validateLocation(1);
			}
	}
}