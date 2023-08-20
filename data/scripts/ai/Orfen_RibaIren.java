package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

public class Orfen_RibaIren extends Fighter
{
	private static final int Orfen_id = 29014;

	public Orfen_RibaIren(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean createNewTask()
	{
		return defaultNewTask();
	}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{
		super.onEvtClanAttacked(attacked_member, attacker, damage);
		L2NpcInstance actor = getActor();
		if(actor == null || attacked_member == null)
			return;
		if(!actor.isInRange(attacked_member, actor.getFactionRange()) || _heal.length == 0)
			return;
		if(attacked_member.isDead() || actor.isDead() || attacked_member.getCurrentHpPercents() > 50)
			return;

		int heal_chance = 0;
		if(attacked_member.getNpcId() == actor.getNpcId())
			heal_chance = attacked_member.getObjectId() == actor.getObjectId() ? 100 : 0;
		else
			heal_chance = attacked_member.getNpcId() == Orfen_id ? 90 : 10;

		if(Rnd.chance(heal_chance) && canUseSkill(_heal[0], attacked_member, -1))
			AddUseSkillDesire(attacked_member, _heal[0], 1000000);
	}
}