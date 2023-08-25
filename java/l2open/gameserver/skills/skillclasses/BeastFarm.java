package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2TamedBeastInstance;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class BeastFarm extends L2Skill
{
	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(_id == 8362)
			if(!(target instanceof L2TamedBeastInstance))
			{
				activeChar.sendPacket(Msg.INVALID_TARGET());
				return false;
			}
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}
	
	public BeastFarm(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(getId() == 8362)
		{
			for(L2Character target : targets)
				if(target != null && target instanceof L2TamedBeastInstance)
					((L2TamedBeastInstance) target).doDespawn();
		}
		if(getId()  == 8363)
		{
			if(((L2Player) activeChar).getTrainedBeast() != null)
			{
				for(L2TamedBeastInstance beast : ((L2Player) activeChar).getTrainedBeast())
				{
					beast.startFollowTask();
					beast.setFollow(true);
				}
			}
		}
		if(getId() == 8364)
		{
			if(((L2Player) activeChar).getTrainedBeast() != null)
			{
				for(L2TamedBeastInstance beast : ((L2Player) activeChar).getTrainedBeast())
					beast.castBeastSkills();
			}
		}
		if(getId() == 8378)
		{
			if(((L2Player) activeChar).getTrainedBeast() != null)
			{
			  	for(L2TamedBeastInstance beast : ((L2Player) activeChar).getTrainedBeast())
			   		beast.doDespawn();
			}
		}
	}
}