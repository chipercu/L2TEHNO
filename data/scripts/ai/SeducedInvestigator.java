package ai;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import org.apache.commons.lang3.ArrayUtils;

/**
 * AI Seduced Investigator для Rim Pailaka
 */

public class SeducedInvestigator extends Fighter
{
	private int[] _allowedTargets = {25653,25654,25655,25657,25658,25659,25659,25660,25661,25662,25663,25664 };

	public SeducedInvestigator(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
		AI_TASK_ACTIVE_DELAY = 500;
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new BuffTask(actor), 120000, 120000);
	}

	private class BuffTask extends l2open.common.RunnableImpl
	{
		private L2NpcInstance actor;

		public BuffTask(L2NpcInstance _npc)
		{
			actor = _npc;
		}

		@Override
		public void runImpl()
		{
			GArray<L2Player> players = L2World.getAroundPlayers(actor, 75, 75);
			if(players == null || players.size() < 1)
				return;
			for(L2Player player : players)
				if(player.getReflectionId() == actor.getReflectionId())
				{
					int[] buffs = { 5970, 5971, 5972, 5973 };
					L2Skill skill;
					if(actor.getNpcId() == 36562)
						skill = SkillTable.getInstance().getInfo(buffs[0], 1);
					else if(actor.getNpcId() == 36563)
						skill = SkillTable.getInstance().getInfo(buffs[1],1);
					else if(actor.getNpcId() == 36564)
						skill = SkillTable.getInstance().getInfo(buffs[2],1);
					else
						skill = SkillTable.getInstance().getInfo(buffs[3],1);
					if(skill != null)
					{
						actor.broadcastSkill(new MagicSkillUse(actor, player, skill.getId(), 1, skill.getHitTime(), 0));
						skill.getEffects(actor, player, false, false);
					}
				}
		}
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor.isDead())
			return false;

		GArray<L2NpcInstance> around = actor.getAroundNpc(1000, 300);
		if(around != null && !around.isEmpty())
			for(L2NpcInstance npc : around)
				if(ArrayUtils.contains(_allowedTargets, npc.getNpcId()))
				{
					npc.addDamageHate(actor, 0, 100); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc);
				}
		return true;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		Reflection r = actor.getReflection();
		for(L2Player p : r.getPlayers())
			p.sendPacket(new ExShowScreenMessage("Иследователь был убит. Задание провалено", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));

		r.startCollapseTimer(5 * 1000L);

		super.MY_DYING(killer);
	}


	@Override
	public void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		L2Character target = prepareTarget();
		if(target != null && target.isPlayable())
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}
		super.thinkAttack();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(attacker == null)
			return;

		if(attacker.isPlayable())
		{
			return;
		}

		if(attacker.getNpcId() == 25659 || attacker.getNpcId() == 25660 || attacker.getNpcId() == 25661 || attacker.getNpcId() == 25653 || attacker.getNpcId() == 25654 || attacker.getNpcId() == 25655)
			actor.addDamageHate((L2NpcInstance)attacker, 0, 20);

		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		if(target.isPlayer() || target.isPet() || target.isSummon())
			return;

		super.onEvtAggression(target, aggro);
	}

	@Override
	public void checkAggression(L2Character target)
	{
		if(target.isPlayable())
			return;

		super.checkAggression(target);
	}
}