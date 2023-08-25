package ai;

import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Rnd;

/**
 * Author: VISTALL
 * Date:  9:03/17.11.2010
 * npc Id : 18602
 */
public class KrateisCubeWatcherBlue extends DefaultAI
{
	private static final int RESTORE_CHANCE = 60;

	public KrateisCubeWatcherBlue(L2NpcInstance actor)
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
			if(cha.isPlayer() && !cha.isDead() && Rnd.chance(RESTORE_CHANCE))
			{
				double valCP = cha.getMaxCp() - cha.getCurrentCp();
				if(valCP > 0)
				{
					cha.setCurrentCp(valCP + cha.getCurrentCp());
					cha.sendPacket(new SystemMessage(SystemMessage.S1_CPS_WILL_BE_RESTORED).addNumber(Math.round(valCP)));
				}

				double valHP = cha.getMaxHp() - cha.getCurrentHp();
				if(valHP > 0)
				{
					cha.setCurrentHp(valHP + cha.getCurrentHp(), false);
					cha.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(valHP)));
				}

				double valMP = cha.getMaxMp() - cha.getCurrentMp();
				if(valMP > 0)
				{
					cha.setCurrentMp(valMP + cha.getCurrentMp());
					cha.sendPacket(new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED).addNumber(Math.round(valMP)));
				}
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
				L2NpcTemplate template = NpcTable.getTemplate(18601);
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
