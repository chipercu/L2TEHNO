package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

public class Thomas extends Fighter
{
	private long _lastSay;

	private static final String[] _stay = 
	{
		"Ха...Ха... Вы пришли спасти снеговика?", "Так просто я вам его не отдам!",
		"Чтобы спасти вашего снеговика, вам придется убить меня!", "Ха...Ха... Вы думаете это так просто?"
	};

	private static final String[] _attacked =
	{
		"Вы должны все умереть!", "Снеговик мой и не будет у вас Нового Года!",
		"Я вас всех убью!", "Что так слабо бьете? Мало каши ели? Ха... Ха...", "И это называется герои?",
		"Не видать вам снеговика!", "Только древнее оружие способно победить меня!"
	};

	public Thomas(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		// Ругаемся не чаще, чем раз в 10 секунд
		if(!actor.isInCombat() && System.currentTimeMillis() - _lastSay > 10000)
		{
			Functions.npcSay(actor, _stay[Rnd.get(_stay.length)]);
			_lastSay = System.currentTimeMillis();
		}
		return super.thinkActive();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || attacker == null || attacker.getPlayer() == null)
			return;

		// Ругаемся не чаще, чем раз в 5 секунд
		if(System.currentTimeMillis() - _lastSay > 5000)
		{
			Functions.npcSay(actor, _attacked[Rnd.get(_attacked.length)]);
			_lastSay = System.currentTimeMillis();
		}
		super.ATTACKED(attacker, damage, skill);
	}
}