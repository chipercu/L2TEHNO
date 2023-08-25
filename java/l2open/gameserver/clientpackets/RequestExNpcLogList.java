package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExQuestNpcLogList;

/**
 * Format: d
 * @author Drizzy
 */

public class RequestExNpcLogList extends L2GameClientPacket
{
	private int _questId;

	@Override
	protected void readImpl()
	{
		_questId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		QuestState st = getClient().getActiveChar().getQuestState(QuestManager.getQuest(_questId).getName());
		getClient().sendPacket(new ExQuestNpcLogList(st));
	}
}