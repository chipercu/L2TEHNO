package ai;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2ObjectTasks.NotifyFactionTask;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;
import l2open.util.reference.*;

/**
 * AI для ищущих помощи при HP < 50%
 *
 * @author Diamond
 */
public class WatchmanMonster extends Fighter
{
	private long _lastSearch = 0;
	private boolean isSearching = false;
	static final String[] flood = { "I'll be back", "You are stronger than expected" };
	static final String[] flood2 = { "Help me!", "Alarm! We are under attack!" };
	private HardReference<? extends L2Character> _attacker = HardReferences.emptyRef();

	public WatchmanMonster(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(final L2Character attacker, int damage, L2Skill skill)
	{
		final L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(attacker != null && !actor.getFactionId().isEmpty() && actor.getCurrentHpPercents() < 50 && _lastSearch < System.currentTimeMillis() - 15000)
		{
			_lastSearch = System.currentTimeMillis();
			_attacker = attacker.getRef();

			if(findHelp())
				return;
			Functions.npcSay(actor, "Anyone, help me!");
		}
		super.ATTACKED(attacker, damage, skill);
	}

	private boolean findHelp()
	{
		isSearching = false;
		final L2NpcInstance actor = getActor();
		L2Character attacker = _attacker.get();
		if(actor == null || attacker == null)
			return false;

		for(final L2NpcInstance npc : actor.getAroundNpc(1000, 150))
			if(!actor.isDead() && npc.getFactionId().equals(actor.getFactionId()) && !npc.isInCombat() && actor.buildPathTo(npc.getX(), npc.getY(), npc.getZ(), 20, true, false, false, null))
			{
				clearTasks();
				isSearching = true;
				addTaskMove(npc.getLoc(), true);
				Functions.npcSay(actor, flood[Rnd.get(flood.length)]);
				return true;
			}
		return false;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_lastSearch = 0;
		_attacker = HardReferences.emptyRef();
		isSearching = false;
		super.MY_DYING(killer);
	}

	@Override
	protected void onEvtArrived()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(isSearching)
		{
			L2Character attacker = _attacker.get();
			if(attacker != null)
			{
				Functions.npcSay(actor, flood2[Rnd.get(flood2.length)]);
				actor.callFriends(attacker, 100);
				ThreadPoolManager.getInstance().execute(new NotifyFactionTask(actor, attacker, 100, false));
			}
			isSearching = false;
			notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
		}
		else
			super.onEvtArrived();
	}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		if(!isSearching)
			super.onEvtAggression(target, aggro);
	}
}