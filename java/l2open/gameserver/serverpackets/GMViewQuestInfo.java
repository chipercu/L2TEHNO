package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class GMViewQuestInfo extends L2GameServerPacket
{
	private final L2Player _cha;

	public GMViewQuestInfo(L2Player cha)
	{
		_cha = cha;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x99);
		writeS(_cha.getName());

		Quest[] quests = _cha.getAllActiveQuests();

		if(quests.length == 0)
		{
			writeH(0);
			writeH(0);
			return;
		}

		writeH(quests.length);
		for(Quest q : quests)
		{
			writeD(q.getQuestIntId());
			QuestState qs = _cha.getQuestState(q.getName());
			writeD(qs == null ? 0 : qs.getInt("cond"));
		}

		writeH(0); //количество элементов типа: ddQd , как-то связано с предметами
	}
}