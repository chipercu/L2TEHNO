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
 * AI для Anakim ID: 27388
 * @author DarkShadow74 
 * Сцена перепалки с Лилит.
 * Ругается в шаут некультурным англ. матом. :)
 * Ругается в ПМ игроку который в комнате.
 * Не реагирует на атаки со стороны чаров(Неуязвим)
 * Делетит себя если у персонажа в сумке 4 или больше итемов по квесту.
 * Рейт использования физ атаки 0(Использует только 1 скилл и то магичиский...)
 * Нет рандомного передвижения.
 */
public class Anakim extends Mystic
{
	private long _lastSay;
	private L2NpcInstance lilith;
	private static final String[] say = {
			"For the eternity of Einhasad!!!",
			"I'll show you the real power of Einhasad!",
			"Dear Military Force of Light! Go destroy the offspring of Shilien!!!",
			"Dear Shilien's offspring! You are not capable of confronting us!" };
	private static final String[] sayToPlayer = {
			"My power's weakening. Hurry and turn on the sealing device!!!",
			"Lilith's attack is getting stronger! Go ahead and turn it on!",
			"All 4 sealing devices must be turned on!!!" };
			
	public Anakim(L2Character actor)
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
		    if(lilith == null)
		    	for(L2NpcInstance npc : L2World.getAroundNpc(actor, 1000, 200))
			    	if(npc.getNpcId() == 32715)
					{
						npc.addDamageHate(actor, 0, 100);
				    	lilith = npc;
					}
						
			if(lilith != null)
		    	setIntention(CtrlIntention.AI_INTENTION_ATTACK, lilith);
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
			
			for(L2Player pl : r.getPlayers())
				if(pl != null && Rnd.chance(40)) //С шансом в 40% посылает сообщение игроку в ПМ.
				{
				    if(Rnd.chance(20))//с шансорм 20% посылает игроку это сообщение если нет то рандомно одно из остальных 3-х.
    					Functions.npcSayToPlayer(actor, pl,"Dear " + pl.getName() + ", give me more strength.");
					else
    					Functions.npcSayToPlayer(actor, pl, sayToPlayer[Rnd.get(sayToPlayer.length)]);
				}
		}
		for(L2Player pl : r.getPlayers())
			if(pl != null && Functions.getItemCount(pl, 13846) >= 4)
    			actor.deleteMe();
				
		super.thinkAttack();
	}
}