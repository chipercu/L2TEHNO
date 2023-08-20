package npc.model;

import bosses.EkimusManager;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public class DestroyedTumorEntranceInstance extends L2NpcInstance
{
	public DestroyedTumorEntranceInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public String getHtmlPath(int npcId, int val)
	{
		if(val == 0)
			return "data/html/instance/SeedOfInfinity/TumorEntrance.htm";
		return "data/html/instance/SeedOfInfinity/TumorEntrance-" + val + ".htm";
	}

	public void onBypassFeedback(L2Player player, String command)
	{
		if (!canBypassCheck(player, this))
			return;
		if (command.equalsIgnoreCase("view"))
			showChatWindow(player, getHtmlPath(getNpcId(), 1));
		else if(command.equalsIgnoreCase("enter"))
			if (player.getParty() == null || !player.getParty().isLeader(player))
			{
				showChatWindow(player, getHtmlPath(getNpcId(), 2));
			}
			else
			{
				if(EkimusManager.teleToHeart(player, (L2NpcInstance)player.getTarget()))
					return;
				showChatWindow(player, getHtmlPath(getNpcId(), 3));
			}
		else
			super.onBypassFeedback(player, command);
	}
}