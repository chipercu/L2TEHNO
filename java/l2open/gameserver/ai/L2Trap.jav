package l2open.gameserver.ai;

import l2open.config.ConfigValue;
import l2open.common.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.taskmanager.AiTaskManager;
import l2open.util.*;

import java.util.concurrent.ScheduledFuture;
import java.lang.ref.WeakReference;

public class L2Trap extends L2CharacterAI
{
	protected ScheduledFuture<?> _aiTask;
	protected GCArray<L2Playable> _see_creature_list = new GCArray<L2Playable>();
	protected boolean _thinking = false;

	public L2Trap(L2Character actor)
	{
		super(actor);
	}

	@Override
	public void startAITask()
	{
		if(_aiTask == null)
		{
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0, 500);
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
		_see_creature_list.clear();
	}

	@Override
	public void stopAITask()
	{
		try
		{
			if(_aiTask != null)
			{
				setIntention(CtrlIntention.AI_INTENTION_IDLE);
				_aiTask.cancel(false);
				_aiTask = null;
			}
		}
		catch(Exception e)
		{}
		_see_creature_list.clear();
	}

	@Override
	public void runImpl() throws Exception
	{
		// _log.info("L2TrapAI run Start");
		if(_aiTask == null)
			return;
		else if(getActor() == null)
		{
			stopAITask();
			return;
		}
		onEvtThink();
	}

	/**
	 * Если при спауне, цель в радиусе 100 - ловушка срабатывает.
	 * Если при спауне, ловушка на дистанции больше 100, но меньше 200, срабатывает она только в радиусе 15.
	 * Если при спауне, цель на дистанции 200+, то при подходе на дистанцю 70, ловушка срабатывает.
	 **/
	@Override
	protected void onEvtThink()
	{
		//setLog("onEvtThink Start");
		L2TrapInstance actor = getActor();
		if(_thinking || actor == null || actor.isDead())
		{
			return;
		}

		_thinking = true;
		try
		{
			for(L2Playable obj : L2World.getAroundPlayables(actor, 100, 150)) // 15-35 ренж
				if(obj != null && !obj.isAlikeDead() && obj.isVisible() && !_see_creature_list.contains(obj))
				{
					SEE_CREATURE(obj);
					if(actor.getDistance(obj) <= actor.getAgroRange())
						_see_creature_list.add(obj);
				}
			if(actor == null)
				_see_creature_list.clear();
			for(L2Playable obj : _see_creature_list)
			{
				try
				{
					if(obj == null || obj.isDead() || actor.getDistance(obj) > 100)
						_see_creature_list.remove(obj);
				}
				catch(Exception e)
				{}
			}
			if(_see_creature_list.size() == 0)
				_see_creature_list.clear();
		}
		catch(Exception e)
		{
			_see_creature_list.clear();
			e.printStackTrace();
		}
		finally
		{
			//setLog("onEvtThink Finish");
			_thinking = false;
		}
	}

	@Override
	protected void onEvtSpawn()
	{
		L2TrapInstance actor = getActor();
		L2Character detonate=null;
		if(actor.getOwner().isInZoneBattle() || actor.getOwner().isInZone(L2Zone.ZoneType.Siege))
		{
			actor.setDetected(true);
			for(L2Player player : L2World.getAroundPlayers(actor))
				if(player != null)
					player.sendPacket(new NpcInfo(actor, player));
		}
		else
			for(L2Character cha : L2World.getAroundCharacters(actor, 100, 100))
			{
				_log.info("L2TrapInstance: isAutoAttackable["+actor.getOwner().isAutoAttackable(cha)+"]["+cha.isAutoAttackable(actor.getOwner())+"]");
				if(actor.can_detonate(cha))
				{
					detonate=cha;
					break;
				}
			}
		if(detonate != null)
		{
			actor.detonate(detonate);
		}
	}

	@Override
	public void SEE_CREATURE(L2Character target)
	{
		if(getActor().can_detonate(target))
			_log.info("L2Trap SEE_CREATURE: "+target);
	}

	@Override
	public L2TrapInstance getActor()
	{
		return (L2TrapInstance) super.getActor();
	}
}