package events.GvG;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

public class MutantChest extends Fighter
{

	public MutantChest(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(Rnd.chance(30))
			Functions.npcSay(actor, "Враги! Всюду враги! Все сюда, враги здесь!");

		actor.deleteMe();
	}
}