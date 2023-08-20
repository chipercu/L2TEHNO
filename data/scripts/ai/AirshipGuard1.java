package ai;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Guard;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class AirshipGuard1 extends Guard
{
	static final Location[] points1 = {
			new Location(-149268, 254124, -184),
			new Location(-148700, 254399, -184),
			new Location(-148313, 254841, -184),
			new Location(-148256, 255338, -184) };

	static final Location[] points3 = {
			new Location(-149239, 254100, -184),
			new Location(-148671, 254375, -184),
			new Location(-148284, 254817, -184),
			new Location(-148227, 255314, -184) };

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public AirshipGuard1(L2Character actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		for(L2Character cha : L2World.getAroundCharacters(getActor(), 1200, 300))
			if(cha.getNpcId() == 18782)
			{
				cha.addDamageHate(getActor(), 0, Rnd.get(50, 150)); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
				getActor().setRunning();
				getActor().getAI().setAttackTarget(cha); // На всякий случай, не обязательно делать
				getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, cha, null); // Переводим в состояние атаки
				getActor().getAI().addTaskAttack(cha); // Добавляем отложенное задание атаки, сработает в самом конце движения
			}

		if(_def_think)
		{
			doTask();
			return true;
		}

		if(System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5)))
		{
			if(!wait)
				switch(current_point)
				{
					case 0:
						wait_timeout = System.currentTimeMillis() + Rnd.get(0, 30000);
						wait = true;
						return true;
					case 8:
						wait_timeout = System.currentTimeMillis() + Rnd.get(0, 30000);
						wait = true;
						return true;
				}

			wait_timeout = 0;
			wait = false;
			current_point++;
			if(ROUTE == 1)
			{
				if(current_point >= points1.length)
					current_point = 0;
				addTaskMove(points1[current_point].rnd(0, 100, false), true);
			}
			else if(ROUTE == 3)
			{
				if(current_point >= points3.length)
					current_point = 0;
				addTaskMove(points3[current_point].rnd(0, 100, false), true);
			}
			doTask();
			return true;
		}

		if(randomAnimation())
			return true;

		return false;
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 1000)
		{
			L2Character c0 = L2ObjectsStorage.getNpc(script_event_arg2);
			if( IsNullCreature(c0) == 0 )
			{
				c0.addDamageHate(getActor(), 0, Rnd.get(50, 150)); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
				getActor().setRunning();
				getActor().getAI().setAttackTarget(c0); // На всякий случай, не обязательно делать
				getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, c0, null); // Переводим в состояние атаки
				getActor().getAI().addTaskAttack(c0); // Добавляем отложенное задание атаки, сработает в самом конце движения
			}
		}
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}