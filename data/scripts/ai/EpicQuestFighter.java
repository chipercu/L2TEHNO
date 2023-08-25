package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * AI для Lilim Fighter's. ID: 27373,27377,27371,27379
 * @author DarkShadow74 
 * При агре ругаются матом.
 * При смерти включают Соц Экшан "Негодование" :D
 * Так же используется для Минионов Анакима и Лилит.
 * Минионы анакима берёт цель на миниона файтера лилит.
 * Минион файтел лилит выбирает рандомно одного из минионов анакима для атаки.
 */
public class EpicQuestFighter extends Fighter
{
	private L2NpcInstance lilithMinion;
	private L2NpcInstance anakimMinion;
	private static final int[] minions = { 32719, 32720, 32721 };
	
	public EpicQuestFighter(L2Character actor)
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
			case 27371:
			case 27379:
				Functions.npcSay(actor, "This place once belonged to Lord Shilen.");
				break;
			case 27373:
				Functions.npcSay(actor, "Those who are afraid should get away and those who are brave should fight!");
				break;
			case 27377:
				Functions.npcSay(actor, "Leave now!");
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
			case 27373:
			case 27379:
				Functions.npcSay(actor, "Why are you getting in our way?");
				break;
			case 27377:
				Functions.npcSay(actor, "For Shilen!");
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
			case 32719:
			case 32720:
			case 32721:
				if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
				{
		    		if(lilithMinion == null)
			    		for(L2NpcInstance npc : L2World.getAroundNpc(actor, 1000, 200))
			    			if(npc.getNpcId() == 32717)
							{
					    		npc.addDamageHate(actor, 0, 100);
								lilithMinion = npc;
							}
					if(lilithMinion != null)
		    			setIntention(CtrlIntention.AI_INTENTION_ATTACK, lilithMinion);
				}
				break;
			case 32717:
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