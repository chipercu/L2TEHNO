package ai.dragonvalley;

import l2open.config.ConfigValue;
import l2open.common.*;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.NpcUtils;
import quests._456_DontKnowDontCare._456_DontKnowDontCare;

public class DrakeBosses extends Fighter
{
	public DrakeBosses(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance npc = null;
		L2Player player = killer.getPlayer();
		if(player != null && player.getParty() != null && player.getParty().getCommandChannel() != null)
		{
			for(L2Player p : player.getParty().getCommandChannel().getMembers())
			{
				if(p.getQuestState("_456_DontKnowDontCare") != null)
					if(player.isInRange(p, ConfigValue.AltPartyDistributionRange))
						p.getQuestState("_456_DontKnowDontCare").set("killDone", "1");
			}
		}
		else if(player != null && player.getParty() != null)
		{
			for(L2Player p : player.getParty().getPartyMembers())
			{
				if(p.getQuestState("_456_DontKnowDontCare") != null)
					if(player.isInRange(p, ConfigValue.AltPartyDistributionRange))
						p.getQuestState("_456_DontKnowDontCare").set("killDone", "1");
			}
		}
		else if(player != null && player.getQuestState("_456_DontKnowDontCare") != null)
			player.getQuestState("_456_DontKnowDontCare").set("killDone", "1");
		switch(getActor().getNpcId())
		{
			case 25725:
				npc = NpcUtils.spawnSingle(32884, getActor().getLoc(), 0);
				break;
			case 25726:
				npc = NpcUtils.spawnSingle(32885, getActor().getLoc(), 0);
				break;
			case 25727:
				npc = NpcUtils.spawnSingle(32886, getActor().getLoc(), 0);
				break;
		}
		final L2NpcInstance npc2 = npc;
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				if(npc2 != null)
				{
					npc2.deleteMe();
				}
			}
		}, 120 * 1000);
		super.MY_DYING(killer);
		getActor().endDecayTask();
	}
}