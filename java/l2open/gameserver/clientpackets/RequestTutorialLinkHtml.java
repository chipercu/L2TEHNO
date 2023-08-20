package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.quest.Quest;

public class RequestTutorialLinkHtml extends L2GameClientPacket
{
	// format: cS

	String _bypass;

	@Override
	public void readImpl()
	{
		_bypass = readS();
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		if(player.getEventMaster() != null && player.getEventMaster().tutorialLinkHtml(player, _bypass))
			return;

		Quest q = QuestManager.getQuest(255);
		if(q != null)
			player.processQuestEvent(q.getName(), _bypass, null);
	}
}