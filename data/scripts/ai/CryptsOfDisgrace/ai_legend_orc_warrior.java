package ai.CryptsOfDisgrace;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * @date: 21.08.2012
 */

public class ai_legend_orc_warrior extends Fighter
{
	private L2Character myself = null;
	public ai_legend_orc_warrior(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(Rnd.get(100) < 2)
		{
			switch(Rnd.get(2))
			{
				case 0:
					CreateOnePrivateEx(22707,"CryptsOfDisgrace.ai_legend_orc_ev_vice","L2Monster", "ID", killer.getObjectId(),getActor().getX(),getActor().getY(),getActor().getZ(), 0);
					break;
				case 1:
					if(Rnd.get(2) < 1)
					{
						CreateOnePrivateEx(18815,"CryptsOfDisgrace.ai_legend_orc_ev_leader","L2Monster", "ID", killer.getObjectId(),getActor().getX(),getActor().getY(),getActor().getZ(), 0);
					}
					break;
			}
		}
		super.MY_DYING(killer);
	}
}
