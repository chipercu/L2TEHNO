package ai.adept;

import l2open.config.ConfigValue;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.cache.FStringCache;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.util.Location;
import l2open.util.Rnd;

public class Adept extends DefaultAI
{
	protected Location[] _points;
	private int _lastPoint = 0;
	private boolean lastPoint = false;

	public Adept(L2NpcInstance actor)
	{
		super(actor);
		AI_TASK_ACTIVE_DELAY = 5000;
		AI_TASK_DELAY = 5000;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		if(super.thinkActive())
			return true;

		if(!getActor().isMoving)
			startMoveTask();

		return true;
	}

	@Override
	protected void onEvtArrived()
	{
		startMoveTask();
		if(Rnd.chance(5))
			sayRndMsg();
		super.onEvtArrived();
	}

	private void startMoveTask()
	{
		if(!lastPoint)
			_lastPoint++;

		if(_lastPoint >= _points.length)
			lastPoint = true;

		if(lastPoint)
			_lastPoint--;

		if(_lastPoint < 0)
			_lastPoint = 0;

		if(_lastPoint == 0 && lastPoint)
			lastPoint = false;
			
		addTaskMove(_points[_lastPoint], true);
		doTask();
		clearTasks();
	}

	private void sayRndMsg()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		NpcSay ns;
		if(!ConfigValue.AdeptSayText)
		{
			switch(Rnd.get(7))
			{
				case 1:
					ns = new NpcSay(actor, Say2C.NPC_ALL, 1010222);
					break;
				case 2:
					ns = new NpcSay(actor, Say2C.NPC_ALL, 1010223);
					break;
				case 3:
					ns = new NpcSay(actor, Say2C.NPC_ALL, 1010224);
					break;
				case 4:
					ns = new NpcSay(actor, Say2C.NPC_ALL, 1010225);
					break;
				case 5:
					ns = new NpcSay(actor, Say2C.NPC_ALL, 1010226);
					break;
				case 6:
					ns = new NpcSay(actor, Say2C.NPC_ALL, 1010227);
					break;
				default:
					ns = new NpcSay(actor, Say2C.NPC_ALL, 1010221);
					break;
			}
		}
		else
			ns = new NpcSay(actor, Say2C.NPC_ALL, FStringCache.getString(0));
		actor.broadcastPacket(ns);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
	}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
	}
}