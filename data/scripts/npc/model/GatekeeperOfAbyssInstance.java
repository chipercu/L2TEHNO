package npc.model;

import l2open.gameserver.instancemanager.SeedOfInfinityManager;
import bosses.EkimusManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public class GatekeeperOfAbyssInstance extends L2NpcInstance
{
	public GatekeeperOfAbyssInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public void onBypassFeedback(L2Player player, String command)
	{
		if (!canBypassCheck(player, this))
			return;
		int stage = SeedOfInfinityManager.getCurrentCycle();
		if (command.equalsIgnoreCase("enter"))
		{
			if (stage == 1)
				showChatWindow(player, 3);
			else if (stage == 2 || stage == 5)
				showChatWindow(player, 2);
			else
				showChatWindow(player, 1);
		}
		else if (command.equalsIgnoreCase("toSeed"))
		{
			if (stage == 3 || stage == 4)
				SeedOfInfinityManager.enterToGathering(player);
			else
				showChatWindow(player, 3);
		}
		else if (command.equalsIgnoreCase("toInfinity"))
		{
			if (stage == 2)
				EkimusManager.enterInstance(player);
			else if (stage == 5)
				showQuestWindow(player, ("_698_BlocktheLordsEscape"));
			else
				showChatWindow(player, 3);
		}
		else
			super.onBypassFeedback(player, command);
	}
}