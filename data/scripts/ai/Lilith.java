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
 * AI для Lilith ID: 27385
 * @author DarkShadow74 
 * Сцена перепалки с Анаким.
 * Ругается в шаут некультурным англ. матом. :)
 * Не реагирует на атаки со стороны чаров(Неуязвим)
 * Делетит себя если у персонажа в сумке 4 или больше итемов по квесту.
 * Рейт использования физ атаки 0(Использует только 1 скилл и то магичиский...)
 * Нет рандомного передвижения.
 */
public class Lilith extends Mystic
{
	private long _lastSay;
	private L2NpcInstance anakim;
	private static final String[] say = {
			"You, such a fool! The victory over this war belongs to Shilien!!!",
			"How dare you try to contend against me in strength? Ridiculous.",
			"Anakim! In the name of Great Shilien, I will cut your throat!",
			"You cannot be the match of Lilith. I'll teach you a lesson!" };
			
	public Lilith(L2Character actor)
	{
		super(actor);
		AI_TASK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}
	
	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return true;
		
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
		{
		    if(anakim == null)
		    	for(L2NpcInstance npc : L2World.getAroundNpc(actor, 1000, 200))
			    	if(npc.getNpcId() == 32718)
					{
						npc.addDamageHate(actor, 0, 100);
				    	anakim = npc;
					}
						
			if(anakim != null)
		    	setIntention(CtrlIntention.AI_INTENTION_ATTACK, anakim);
		}
				
		return true;
	}

	@Override
	protected void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		Reflection r = actor.getReflection();
		
		// Ругаемся не чаще, чем раз в 10 секунд
		if(System.currentTimeMillis() - _lastSay > 10000)
		{
			Functions.npcShout(actor, say[Rnd.get(say.length)]);
			_lastSay = System.currentTimeMillis();
		}
		for(L2Player pl : r.getPlayers())
			if(pl != null && Functions.getItemCount(pl, 13846) >= 4)
    			actor.deleteMe();
				
		super.thinkAttack();
	}
}