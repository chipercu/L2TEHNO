package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;
import l2open.util.reference.*;

public class EdwinFollower extends DefaultAI
{
	private static final int EDWIN_ID = 32072;
	private static final int DRIFT_DISTANCE = 350;
	private long _wait_timeout = 0;
	private HardReference<? extends L2Character> _edwin = HardReferences.emptyRef();

	public EdwinFollower(L2Character actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return false;
		L2Character edwin = _edwin.get();
		if(edwin == null)
		{
			// Ищем преследуемого не чаще, чем раз в 15 секунд, если по каким-то причинам его нету
			if(System.currentTimeMillis() > _wait_timeout)
			{
				_wait_timeout = System.currentTimeMillis() + 15000;
				for(L2NpcInstance npc : L2World.getAroundNpc(actor))
					if(npc.getNpcId() == EDWIN_ID)
					{
						_edwin = npc.getRef();
						return true;
					}
			}
		}
		else if(!actor.isMoving)
		{
			int x = edwin.getX() + Rnd.get(2 * DRIFT_DISTANCE) - DRIFT_DISTANCE;
			int y = edwin.getY() + Rnd.get(2 * DRIFT_DISTANCE) - DRIFT_DISTANCE;
			int z = edwin.getZ();

			actor.setRunning(); // всегда бегают
			actor.moveToLocation(x, y, z, 0, true);
			return true;
		}
		return false;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}