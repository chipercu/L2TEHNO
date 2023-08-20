package ai;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;

import motion.MonasteryOfSilenceGame;

/**
 * Заебашил: Diagod
 * open-team.ru
 ********************************************************************************************
 * Не нравится мой код, иди на 8====> "в лес".
 ********************************************************************************************
 * AI для мини-игры в МоС.
 **/
public class MonasteryBurnerMiniGame extends Fighter
{
	private MonasteryOfSilenceGame mos = null;

	public int POT_NUMBER = 0;

	public MonasteryBurnerMiniGame(L2Character actor)
	{
		super(actor);
		mos = new MonasteryOfSilenceGame();
		actor.setIsInvul(true);
		actor.setParalyzed(true);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance npc = getActor();
		if(npc == null || skill == null)
			return;
		if(skill.getId() == 9059)
		{
			mos.setTurn(npc, (L2Player)caster);
			// Сбиваем таргет...
			caster.setTarget(null);
		}
		super.onEvtSeeSpell(skill, caster);
	}
}