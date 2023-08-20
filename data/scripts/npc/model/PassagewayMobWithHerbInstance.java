package npc.model;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * Моб при смерти дропает херб "Fiery Demon Blood"
 * @author SYS
 */
public final class PassagewayMobWithHerbInstance extends L2MonsterInstance
{
	public PassagewayMobWithHerbInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public static final int FieryDemonBloodHerb = 9849;

	@Override
	public void calculateRewards(L2Character lastAttacker)
	{
		if(lastAttacker == null)
			return;

		super.calculateRewards(lastAttacker);

		if(lastAttacker.isPlayable())
			dropItem(lastAttacker.getPlayer(), FieryDemonBloodHerb, 1);
	}
}