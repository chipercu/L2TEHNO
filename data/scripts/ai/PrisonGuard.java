package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;

/**
 * AI мобов Prison Guard на Isle of Prayer.<br>
 * - Не используют функцию Random Walk<br>
 * - Ругаются на атаковавших чаров без эффекта Event Timer<br>
 * - Ставят в петрификацию атаковавших чаров без эффекта Event Timer<br>
 * - Не могут быть убиты чарами без эффекта Event Timer<br>
 * - Не проявляют агресии к чарам без эффекта Event Timer<br>
 * ID: 18367, 18368
 *
 * @author SYS
 */
public class PrisonGuard extends Fighter
{
	private static final int RACE_STAMP = 10013;

	public PrisonGuard(L2Character actor)
	{
		super(actor);
	}

	@Override
	public void checkAggression(L2Character target)
	{
		if(target.getEffectList().getEffectsBySkillId(L2Skill.SKILL_EVENT_TIMER) == null)
			return;

		// 18367 не агрятся
		L2NpcInstance actor = getActor();
		if(actor == null || actor.getNpcId() == 18367)
			return;

		super.checkAggression(target);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(attacker.isSummon() || attacker.isPet())
			attacker = attacker.getPlayer();
		if(attacker.getEffectList().getEffectsBySkillId(L2Skill.SKILL_EVENT_TIMER) == null)
		{
			if(actor.getNpcId() == 18367)
				Functions.npcSay(actor, "It's not easy to obtain.");
			else if(actor.getNpcId() == 18368)
				Functions.npcSay(actor, "You're out of mind comming here...");

			L2Skill petrification = SkillTable.getInstance().getInfo(4578, 1); // Petrification
			actor.doCast(petrification, attacker, true);
			if(attacker.getPet() != null)
				actor.doCast(petrification, attacker.getPet(), true);

			return;
		}

		// 18367 не отвечают на атаку, но зовут друзей
		if(actor.getNpcId() == 18367)
		{
			actor.callFriends(attacker, damage);
			return;
		}

		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(actor.getNpcId() == 18367 && killer.getPlayer().getEffectList().getEffectsBySkillId(L2Skill.SKILL_EVENT_TIMER) != null)
			Functions.addItem(killer.getPlayer(), RACE_STAMP, 1);

		super.MY_DYING(killer);
	}
}