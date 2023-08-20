package npc.model;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2AquilaniInstance extends L2NpcInstance
{
	public L2AquilaniInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		QuestState _prev = player.getQuestState("_10288_SecretMission");
		if(_prev != null && _prev.getState() == 3 && player.getLevel() >= 82)
			showHtmlFile(player, "falsepriest_aquilani002.htm");
		else
			showHtmlFile(player, "falsepriest_aquilani001.htm");
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		if(command.equalsIgnoreCase("teleport"))
		{
			player.teleToLocation(118833, -80589, -2688);
		}
		else
			super.onBypassFeedback(player, command);
	}
}