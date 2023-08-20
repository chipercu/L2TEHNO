package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * @author Diamond
 */
public class EvasGiftBox extends Fighter
{
	private static final int[] KISS_OF_EVA = new int[] { 1073, 3141, 3252 };

	private static final int Red_Coral = 9692;
	private static final int Crystal_Fragment = 9693;

	public EvasGiftBox(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor != null && killer != null)
		{
			L2Player player = killer.getPlayer();
			if(player != null && player.getEffectList().containEffectFromSkills(KISS_OF_EVA))
				actor.dropItem(player, Rnd.chance(50) ? Red_Coral : Crystal_Fragment, 1);
		}
		super.MY_DYING(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}