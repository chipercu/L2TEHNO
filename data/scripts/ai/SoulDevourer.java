package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.reference.*;

public class SoulDevourer extends Fighter
{
	private boolean isArrived = true;
	private static final Location[][] location_list =	{
															{
																new Location(-179551, 207088, -15488),
																new Location(-179550, 207375, -15496),
																new Location(-180162, 207441, -15496),
																new Location(-180717, 207687, -15496),
																new Location(-181138, 208053, -15496),
																new Location(-181565, 208512, -15496),
																new Location(-181726, 209028, -15496),
																new Location(-181794, 209631, -15496),
																new Location(-181714, 210148, -15496),
																new Location(-181520, 210667, -15496),
																new Location(-181140, 211155, -15496),
																new Location(-180684, 211526, -15496),
																new Location(-180233, 211774, -15496),
																new Location(-179550, 211888, -15496),
																new Location(-179547, 211251, -15472) 
															},
															{
																new Location(-179550, 207088, -15488),
																new Location(-179557, 207387, -15496),
																new Location(-178982, 207412, -15496),
																new Location(-178433, 207653, -15496),
																new Location(-177962, 208020, -15496),
																new Location(-177596, 208520, -15496),
																new Location(-177366, 209054, -15496),
																new Location(-177315, 209603, -15496),
																new Location(-177373, 210173, -15496),
																new Location(-177614, 210708, -15496),
																new Location(-177968, 211173, -15496),
																new Location(-178424, 211562, -15496),
																new Location(-178898, 211747, -15496),
																new Location(-179549, 211884, -15496),
																new Location(-179550, 211248, -15472)
															}
														};
	private int current_point = -1;
	private int point = -1;
	private HardReference<? extends L2NpcInstance> _npc_ref = HardReferences.emptyRef();

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance npc;
		if ((npc = getActor()) == null || npc.isDead())
			return true;
		if (_def_think)
		{
			doTask();
			return true;
		}
		if (isArrived)
		{
			isArrived = false;
			current_point++;
			if (current_point >= location_list[point].length)
			{
				stopAITask();
				L2NpcInstance npc2 = _npc_ref.get();
				if(npc2 != null)
					npc.doDie(npc2);
				npc.deleteMe();
				return false;
			}
			addTaskMove(location_list[point][current_point], true);
			doTask();
			return true;
		}
		return false;
	}

	@Override
	protected void onEvtArrived()
	{
		L2NpcInstance npc;
		if ((npc = getActor()) == null || npc.isDead())
			return;
		isArrived = true;
		super.thinkActive();
	}

	public SoulDevourer(L2Character actor, L2NpcInstance npc, int size)
	{
		super(actor);
		point = size;
		_npc_ref = npc.getRef();
		AI_TASK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}

	@Override
	protected void onEvtClanAttacked(L2Character actor, L2Character attacker, int count)
	{
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void onEvtAggression(L2Character actor, int count)
	{
	}

	@Override
	public void checkAggression(L2Character actor)
	{
	}

	@Override
	protected void onIntentionAttack(L2Character actor)
	{
	}

	@Override
	protected void ATTACKED(L2Character actor, int count, L2Skill skill)
	{
	}
}