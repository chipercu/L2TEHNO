package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExReplyPostItemList;
import l2open.gameserver.serverpackets.ExShowReceivedPostList;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 *  Нажатие на кнопку "send mail" в списке из {@link ExShowReceivedPostList}, запрос создания нового письма
 *  В ответ шлется {@link ExReplyPostItemList}
 */
public class RequestExPostItemList extends L2GameClientPacket
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
		if(cha != null && (cha.getVar("jailed") != null || cha.is_block || cha.isInEvent() != 0 || !cha.canItemAction()))
			cha.sendPacket(new SystemMessage(SystemMessage.XYOU_DO_NOT_HAVE_XWRITEX_PERMISSION));
		else if(cha != null && cha.getLevel() >= ConfigValue.SendMailLevel)
			cha.sendPacket(new ExReplyPostItemList(cha));
		else if(cha != null)
			cha.sendMessage("Функции отправки почты, доступны с "+ConfigValue.SendMailLevel+" уровня.");
	}
}