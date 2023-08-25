package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;

import motion.MonasteryOfSilence;

public class MonasteryBurner extends Fighter
{
	public MonasteryBurner(L2Character actor)
	{
		super(actor);
		actor.setIsInvul(true);
		actor.setParalyzed(true);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		// Зажигаем тот котел, который ударили, если горит другой то его тушим...
		getActor().setNpcState(1);
		for(L2NpcInstance npc : L2World.getAroundNpc(getActor(), 400, 30))
			if(npc.getNpcId() == 18914)
				npc.setNpcState(2);

		MonasteryOfSilence.getInstance().setSpawn(getActor());
	}
}