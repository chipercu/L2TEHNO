package npc.model;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * Author: Drizzy
 * Date: 13.11.11
 * Time: 21:32
 * This instance for exchange freya necklace.
 */
public class L2RaffortyInstance extends L2NpcInstance
{
	public L2RaffortyInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		QuestState hostQuest = player.getQuestState("_10286_ReunionWithSirra");
		if(hostQuest != null && hostQuest.getState() == 3 && player.getLevel() >= 82)
			showHtmlFile(player, "repre003.htm");
		else
			showHtmlFile(player, "repre0003.htm");
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(command.equalsIgnoreCase("check_necklace"))
		{
			if (Functions.getItemCount(player, 16025) >= 1)
				showHtmlFile(player, "repre004.htm");
			else
				showHtmlFile(player, "repre006.htm");
		}
		else if(command.equalsIgnoreCase("check_bottle"))
		{
			if (Functions.getItemCount(player, 16027) >= 1)
				showHtmlFile(player, "repre008.htm");
			else
				showHtmlFile(player, "repre007.htm");
		}
		else if(command.equalsIgnoreCase("exchange"))
		{
			if (Functions.getItemCount(player, 16027) >= 1 && Functions.getItemCount(player, 16025) >= 1)
			{
				Functions.removeItem(player,16025,1);
				Functions.removeItem(player,16027,1);
				Functions.addItem(player,16026,1);
				showHtmlFile(player, "repre009.htm");
			}
			else
				showHtmlFile(player, "repre011.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}
}
