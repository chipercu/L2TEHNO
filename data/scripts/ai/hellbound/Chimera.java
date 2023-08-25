package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

public class Chimera extends Fighter
{
	public Chimera(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		if(skill.getId() != 2359)
			return;
		L2NpcInstance actor = getActor();
		if(!actor.isDead() && actor.getCurrentHpPercents() > 10) // 10% ХП для использования бутылки
			return;
		switch(actor.getNpcId())
		{
			case 22353: // Celtus
				actor.dropItem(caster.getPlayer(), 9682, 1);
				break;
			case 22349: // Chimeras
			case 22350:
			case 22351:
			case 22352:
				if(Rnd.chance(70))
				{
					if(Rnd.chance(30))
						actor.dropItem(caster.getPlayer(), 9681, 1);
					else
						actor.dropItem(caster.getPlayer(), 9680, 1);
				}
				break;
		}
		actor.doDie(null);
		actor.endDecayTask();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(HellboundManager.getInstance().getLevel() < 7)
		{
			attacker.teleToLocation(-11272, 236464, -3248);
			return;
		}
		super.ATTACKED(attacker, damage, skill);
	}
}