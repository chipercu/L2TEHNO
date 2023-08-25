package ai;

import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Rnd;

/**
 * Author: VISTALL
 * Date:  9:03/17.11.2010
 * npc Id : 18601
 */
public class KrateisCubeWatcherRed extends DefaultAI
{
	private static final int[][] SKILLS = { { 1064, 14 }, { 1160, 15 }, { 1164, 19 }, { 1167, 6 }, { 1168, 7 } };
	private static final int SKILL_CHANCE = 25;

	public KrateisCubeWatcherRed(L2NpcInstance actor)
	{
		super(actor);
		AI_TASK_ACTIVE_DELAY = 3000;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtThink()
	{
		L2NpcInstance actor = getActor();
		GArray<L2Character> around = L2World.getAroundCharacters(actor, 600, 300);
		if(around.isEmpty())
			return;

		for(L2Character cha : around)
			if(cha.isPlayer() && !cha.isDead() && Rnd.chance(SKILL_CHANCE))
			{
				int rnd = Rnd.get(SKILLS.length);
				L2Skill skill = SkillTable.getInstance().getInfo(SKILLS[rnd][0], SKILLS[rnd][1]);
				if(skill != null)
					skill.getEffects(cha, cha, false, false);
			}
	}

	@Override
	public void MY_DYING(L2Character killer)
	{
		final L2NpcInstance actor = getActor();
		super.MY_DYING(killer);

		actor.deleteMe();
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				L2NpcTemplate template = NpcTable.getTemplate(18602);
				if(template != null)
				{
					L2NpcInstance a = template.getNewInstance();
					a.setCurrentHpMp(a.getMaxHp(), a.getMaxMp());
					a.spawnMe(actor.getLoc());
				}
			}
		}, 10000L);
	}
}
