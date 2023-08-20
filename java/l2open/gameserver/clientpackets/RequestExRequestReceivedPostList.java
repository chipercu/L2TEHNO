package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExNoticePostArrived;
import l2open.gameserver.serverpackets.ExShowReceivedPostList;

/**
 * Отсылается при нажатии на кнопку "почта", "received mail" или уведомление от {@link ExNoticePostArrived}, запрос входящих писем.
 * В ответ шлется {@link ExShowReceivedPostList}
 */
public class RequestExRequestReceivedPostList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
	//just a trigger
	}

	@Override
	public void runImpl()
	{
		L2Player cha = getClient().getActiveChar();
		if(cha != null && !cha.is_block && cha.isInEvent() == 0)
			cha.sendPacket(new ExShowReceivedPostList(cha));
	}
}