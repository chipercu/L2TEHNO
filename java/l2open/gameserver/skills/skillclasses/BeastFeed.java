package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2FeedableBeastInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class BeastFeed extends L2Skill
{
	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!(target instanceof L2FeedableBeastInstance))
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}
		if(target.isDead())
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	public BeastFeed(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		_log.fine("Beast Feed casting succeded.");

		for(L2Character target : targets)
			if(target != null)
				if(target.isNpc())
					((L2FeedableBeastInstance) target).onSkillUse((L2NpcInstance) target, (L2Player) activeChar, _id);
	}
}
