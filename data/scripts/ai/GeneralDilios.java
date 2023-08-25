package ai;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.util.GArray;
import l2open.util.Rnd;

/**
 * AI General Dilios at Gracia, Keucereus Alliance Base
 */
public class GeneralDilios extends DefaultAI
{
	private static final int GUARD_ID = 32619;
	private GArray<L2NpcInstance> _guards;
	private long wait_shout_timeout = 0;
	private long wait_anime_timeout = 0;

	private static final long SHOUT_DELAY = 10 * 60 * 1000L; // 10 min
	private static final long ANIMATION_DELAY = 1 * 60 * 1000L; // 1 min

	private static final String[] TEXT = {
			"Messenger, inform the patrons of the Keucereus Alliance Base! The Seed of Infinity is currently secured under the flag of the Keucereus Alliance!",
			"Messenger, inform the patrons of the Keucereus Alliance Base! We're gathering brave adventurers to attack Tiat's Mounted Troop that's rooted in the Seed of Destruction.",
			"Messenger, inform the brothers in Kucereu's clan outpost! Brave adventurers are currently eradicating Undead that are widespread in Seed of Immortality's Hall of Suffering and Hall of Erosion!" };

	public GeneralDilios(L2Character actor)
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
			return false;

		if(System.currentTimeMillis() > wait_shout_timeout)
		{
			Functions.npcShout(actor, TEXT[Rnd.get(TEXT.length)]);
			wait_shout_timeout = System.currentTimeMillis() + SHOUT_DELAY;
		}

		if(System.currentTimeMillis() > wait_anime_timeout)
		{
			if(_guards == null || _guards.isEmpty())
			{
				if(_guards == null)
					_guards = new GArray<L2NpcInstance>();
				for(L2NpcInstance npc : L2World.getAroundNpc(actor))
					if(npc.getNpcId() == GUARD_ID)
						_guards.add(npc);
			}
			else
			{
				Functions.npcSay(actor, "Stabbing three times!");
				ThreadPoolManager.getInstance().schedule(new RunAnimation(), 2200L, false);
				ThreadPoolManager.getInstance().schedule(new RunAnimation(), 4400L, false);
				ThreadPoolManager.getInstance().schedule(new RunAnimation(), 6600L, false);
			}

			wait_anime_timeout = System.currentTimeMillis() + ANIMATION_DELAY;
		}

		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}

	public class RunAnimation extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			for(L2NpcInstance guard : _guards)
				guard.broadcastPacketToOthers2(new SocialAction(guard.getObjectId(), SocialAction.ADVANCE));
		}
	}
}