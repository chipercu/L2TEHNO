package ai.custom;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * @author pchayka
 */
public class GvGBoss extends Fighter
{
	boolean phrase1 = false;
	boolean phrase2 = false;
	boolean phrase3 = false;

	public GvGBoss(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();

		if(actor.getCurrentHpPercents() < 50 && phrase1 == false)
		{
			phrase1 = true;
			Functions.npcSay(actor, "Вам не удастся похитить сокровища Геральда!");
		}
		else if(actor.getCurrentHpPercents() < 30 && phrase2 == false)
		{
			phrase2 = true;
			Functions.npcSay(actor, "Я тебе череп проломлю!");
		}
		else if(actor.getCurrentHpPercents() < 5 && phrase3 == false)
		{
			phrase3 = true;
			Functions.npcSay(actor, "Вы все погибнете в страшных муках! Уничтожу!");
		}

		super.ATTACKED(attacker, damage, skill);
	}
}