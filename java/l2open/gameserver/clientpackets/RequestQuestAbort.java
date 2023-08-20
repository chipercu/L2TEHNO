package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.quest.*;
import l2open.gameserver.serverpackets.SystemMessage;

public class RequestQuestAbort extends L2GameClientPacket
{
	private int _QuestID;

	/**
	 * packet type id 0x63
	 * format: cd
	 */
	@Override
	public void readImpl()
	{
		_QuestID = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		Quest quest = QuestManager.getQuest(_QuestID);
		if(activeChar == null || quest == null)
			return;
		if(!quest.canAbortByPacket())
			return;
		QuestState qs = activeChar.getQuestState(QuestManager.getQuest(_QuestID).getName());
		if(qs != null)
		{
			qs.abortQuest();
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_ABORTED).addString(QuestManager.getQuest(_QuestID).getDescr(activeChar)));
		}
	}
}