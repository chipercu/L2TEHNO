package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
* AI для Stakato Cheif (рб из кокона)
* Выдаёт итемы всей пати.
* @author Drizzy
* @date 01.07.2011 0:55
*/

public class StakatoCheif extends Fighter
{
	private static final int reward[] = { 14833, 14834 };

	public StakatoCheif(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		L2Player player = killer.getPlayer();
		if(player == null)
			return;

		L2Party party = player.getParty();
		if(party == null)
		{
			Functions.addItem(player,  reward[Rnd.get(reward.length)], 1);
		}
		else
		{
			for(L2Player p : party.getPartyMembers())
			{
				Functions.addItem(p, reward[Rnd.get(reward.length)], 1);
			}
		}
		super.MY_DYING(killer);
	}
}