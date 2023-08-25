package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

 /**
 * AI для Lilim Magus & Lilim Great Magus. ID: 27372/27378
 * @author DarkShadow74 
 * При агре ругаются матом.
 * При смерти включают Соц Экшан "Негодование" :D
 * Используется так же для Миниона лилит визарда.
 * Выбирает рандомно одного из минионов Анакима для атаки.
 */
public class EpicQuestMage extends Mystic
{
	private L2NpcInstance anakimMinion;
	private static final int[] minions = { 32719, 32720, 32721 };
	
	public EpicQuestMage(L2Character actor)
	{
		super(actor);
		AI_TASK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}

	@Override
	public void onIntentionAttack(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
			return;
		
		switch (getActor().getNpcId())
		{
			case 27372:
			case 27378:
	        	Functions.npcSay(actor, "Who dares enter this place?");
				break;
		}
		super.onIntentionAttack(target);
	}
	
	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		
		switch (getActor().getNpcId())
		{
			case 27372:
			case 27378:
				Functions.npcSay(actor, "Lord Shilen... some day... you will accomplish... this mission...");
				break;
		}
		
		super.MY_DYING(killer);
	}
	
	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return true;
		switch (getActor().getNpcId())
		{
			case 32716:
				if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
				{
		    		if(anakimMinion == null)
			    		for(L2NpcInstance npc : L2World.getAroundNpc(actor, 1000, 200))
			    			if(npc.getNpcId() == minions[Rnd.get(minions.length)])
							{
					    		npc.addDamageHate(actor, 0, 100);
								anakimMinion = npc;
							}
					if(anakimMinion != null)
		    			setIntention(CtrlIntention.AI_INTENTION_ATTACK, anakimMinion);
				}
				break;
		}
		return super.thinkActive();
	}

	@Override
	protected void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
			
		Reflection r = actor.getReflection();
		
		for(L2Player pl : r.getPlayers())
			if(pl != null && Functions.getItemCount(pl, 13846) >= 4)
    			actor.deleteMe();
				
		super.thinkAttack();
	}
}