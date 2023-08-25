package ai;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

import java.util.concurrent.ScheduledFuture;

public class FortuneBug extends DefaultAI
{
	private static final int MAX_RADIUS = 500;
	private static final int ItemName_A = 57;
	private static final int ItemName_B_1 = 1881;
	private static final int ItemName_B_2 = 1890;
	private static final int ItemName_B_3 = 1880;
	private static final int ItemName_B_4 = 729;
	private static final L2Skill s_display_bug_of_fortune1 = SkillTable.getInstance().getInfo(6045, 1);
	private static final L2Skill s_display_jackpot_firework = SkillTable.getInstance().getInfo(5778, 1);

	private ScheduledFuture<?> _timer;

	private long _nextEat;
	private int i_ai0, i_ai1, i_ai2;

	public FortuneBug(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_timer = ThreadPoolManager.getInstance().schedule(new onEvtTimer(), 1000);
		i_ai0 = i_ai1 = i_ai2 = 0;
	}

	@Override
	protected void onEvtArrived()
	{
		super.onEvtArrived();
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		L2ItemInstance closestItem = null;
		if(_nextEat < System.currentTimeMillis())
		{
			for(L2Object obj : L2World.getAroundObjects(actor, 20, 200))
				if(obj.isItem() && ((L2ItemInstance) obj).isStackable())
					closestItem = (L2ItemInstance) obj;

			if(closestItem != null)
			{
				closestItem.deleteMe();
				actor.altUseSkill(s_display_bug_of_fortune1, actor);
				Functions.npcSayInRange(actor, 600, 1800291); // Text: Yum-yum, yum-yum

				i_ai0++;
				if(i_ai0 > 1 && i_ai0 <= 10)
					i_ai1 = 1;
				else if(i_ai0 > 10 && i_ai0 <= 100)
					i_ai1 = 2;
				else if(i_ai0 > 100 && i_ai0 <= 500)
					i_ai1 = 3;
				else if(i_ai0 > 500 && i_ai0 <= 1000)
					i_ai1 = 4;
				if(i_ai0 > 1000)
					i_ai1 = 5;

				switch(i_ai1)
				{
					case 0:
						i_ai2 = 0;
						break;
					case 1:
						if(Rnd.get(100) < 10)
							i_ai2 = 2;
						else if(Rnd.get(100) < 15)
							i_ai2 = 3;
						else
							i_ai2 = 1;
						break;
					case 2:
						if(Rnd.get(100) < 10)
							i_ai2 = 3;
						else if(Rnd.get(100) < 15)
							i_ai2 = 4;
						else
							i_ai2 = 2;
						break;
					case 3:
						if(Rnd.get(100) < 10)
							i_ai2 = 4;
						else
							i_ai2 = 3;
						break;
					case 4:
						if(Rnd.get(100) < 10)
							i_ai2 = 3;
						else
							i_ai2 = 4;
						break;
				}

				_nextEat = System.currentTimeMillis() + 10000;
			}
		}
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		if(!actor.isMoving && _nextEat < System.currentTimeMillis())
		{
			L2ItemInstance closestItem = null;
			for(L2Object obj : L2World.getAroundObjects(actor, MAX_RADIUS, 200))
				if(obj.isItem() && ((L2ItemInstance) obj).isStackable())
					closestItem = (L2ItemInstance) obj;

			if(closestItem != null)
				actor.moveToLocation(closestItem.getLoc(), 0, true);
		}

		return false;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		super.MY_DYING(killer);
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(killer != null)
		{
			if(i_ai2 == 0)
				Functions.npcSayInRange(actor, 600, 1800290); // Text: I haven't eaten anything, I'm so weak~
			else
				actor.broadcastSkill(new MagicSkillUse(actor, s_display_jackpot_firework.getId(), 1, s_display_jackpot_firework.getHitTime(), 0));
			switch(i_ai2)
			{
				case 1:
					actor.dropItem(killer.getPlayer(), ItemName_A, 695 + Rnd.get(1550));
					break;
				case 2:
					actor.dropItem(killer.getPlayer(), ItemName_A, 3200 + Rnd.get(5200));
					break;
				case 3:
					actor.dropItem(killer.getPlayer(), ItemName_B_1, 7 + Rnd.get(10));
					actor.dropItem(killer.getPlayer(), ItemName_B_2, 1);
					actor.dropItem(killer.getPlayer(), ItemName_B_3, 7 + Rnd.get(10));
					break;
				case 4:
					actor.dropItem(killer.getPlayer(), ItemName_B_1, 15 + Rnd.get(30));
					actor.dropItem(killer.getPlayer(), ItemName_B_2, 10 + Rnd.get(10));
					actor.dropItem(killer.getPlayer(), ItemName_B_3, 15 + Rnd.get(30));
					if(Rnd.get(100) < 10)
						actor.dropItem(killer.getPlayer(), ItemName_B_4, 1);
					break;
			}
		}
	}

	public class onEvtTimer extends l2open.common.RunnableImpl
	{
		public onEvtTimer()
		{
		}

		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;
			switch(i_ai0)
			{
				case 0:
					Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? 1800279 : 1800280);
					break;
				case 1:
					Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? 1800281 : 1800282);
					break;
				case 2:
					Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? 1800283 : 1800284);
					break;
				case 3:
					Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? 1800285 : 1800286);
					break;
				case 4:
					Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? 1800287 : 1800288);
					break;
				case 5:
					Functions.npcSayInRange(actor, 600, 1800289);
					break;
			}
			if(_timer != null)
			{
				_timer.cancel(true);
				_timer = null;
			}
			_timer = ThreadPoolManager.getInstance().schedule(new onEvtTimer(), 10000 + Rnd.get(10) * 1000);
		}
	}
	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}
