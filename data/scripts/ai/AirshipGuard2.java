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

public class AirshipGuard2 extends Guard
{
	static final Location[] points2 = {
			new Location(-149469, 254124, -184),
			new Location(-150054, 254373, -184),
			new Location(-150431, 254837, -184),
			new Location(-150473, 255335, -184) };

	static final Location[] points4 = {
			new Location(-149499, 254101, -184),
			new Location(-150085, 254350, -184),
			new Location(-150462, 254813, -184),
			new Location(-150504, 255312, -184) };

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public AirshipGuard2(L2Character actor)
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

			if(ROUTE == 2)
			{
				if(current_point >= points2.length)
					current_point = 0;
				addTaskMove(points2[current_point].rnd(0, 100, false), true);
			}
			else if(ROUTE == 4)
			{
				if(current_point >= points4.length)
					current_point = 0;
				addTaskMove(points4[current_point].rnd(0, 100, false), true);
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