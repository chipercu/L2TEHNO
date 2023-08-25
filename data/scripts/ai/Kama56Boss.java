package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2ReflectionBossInstance;
import l2open.util.GArray;
import l2open.util.MinionList;
import l2open.util.Rnd;
import l2open.util.reference.*;

public class Kama56Boss extends Fighter
{
	private long _nextOrderTime = 0;
	private HardReference<L2Player> _lastMinionsTarget = HardReferences.emptyRef();

	public Kama56Boss(L2Character actor)
	{
		super(actor);
	}

	private void sendOrderToMinions(L2NpcInstance actor)
	{
		if(!actor.isInCombat())
		{
			_lastMinionsTarget = HardReferences.emptyRef();
			return;
		}

		MinionList ml = ((L2ReflectionBossInstance) actor).getMinionList();
		if(ml == null || !ml.hasMinions())
		{
			_lastMinionsTarget = HardReferences.emptyRef();
			return;
		}

		long now = System.currentTimeMillis();
		if(_nextOrderTime > now)
		{
			L2Player old_target = _lastMinionsTarget.get();
			if(old_target != null && !old_target.isAlikeDead())
			{
				for(L2MinionInstance m : ml.getSpawnedMinions())
					if(m.getAI().getAttackTarget() != old_target)
					{
						m.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, old_target, 10000000);
						m.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, old_target);
					}
				return;
			}
		}

		_nextOrderTime = now + 30000;

		GArray<L2Player> pl = L2World.getAroundPlayers(actor);
		if(pl.isEmpty())
		{
			_lastMinionsTarget = HardReferences.emptyRef();
			return;
		}

		GArray<L2Player> alive = new GArray<L2Player>(6);
		for(L2Player p : pl)
			if(!p.isAlikeDead())
				alive.add(p);
		if(alive.isEmpty())
		{
			_lastMinionsTarget = HardReferences.emptyRef();
			return;
		}

		L2Player target = alive.get(Rnd.get(alive.size()));
		_lastMinionsTarget = target.getRef();

		Functions.npcSayCustomMessage(actor, "Kama56Boss.attack", target.getName());
		for(L2MinionInstance m : ml.getSpawnedMinions())
		{
			m.clearAggroList(false);
			m.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 10000000);
			m.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
	}

	@Override
	protected void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		sendOrderToMinions(actor);
		super.thinkAttack();
	}
}