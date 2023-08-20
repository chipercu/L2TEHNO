package ai.PavelRuins;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * @date: 16.08.2012
 */

public class ai_golem_giga_p extends Fighter
{
	public ai_golem_giga_p(L2Character actor)
	{
		super(actor);
	}
	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(Rnd.get(100) < 70)
		{
			if(IsNullCreature(killer) == 0)
			{
				L2NpcInstance monster = CreateOnePrivateEx(22802,"Fighter","L2Monster",getActor().getX() + Rnd.get(100),getActor().getY() + Rnd.get(100),getActor().getZ(), 0);
				monster.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 100);
				L2NpcInstance monster1 = CreateOnePrivateEx(22803,"Fighter","L2Monster",getActor().getX() + Rnd.get(100),getActor().getY() + Rnd.get(100),getActor().getZ(), 0);
				monster1.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 100);
			}
		}
		super.MY_DYING(killer);
	}
}
