package l2open.gameserver.serverpackets;

import l2open.gameserver.clientpackets.RequestExDeleteSentPost;
import l2open.gameserver.clientpackets.RequestExRequestSentPost;
import l2open.gameserver.clientpackets.RequestExRequestSentPostList;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.model.items.MailParcelController.Letter;
import l2open.util.GArray;

/**
 * Появляется при нажатии на кнопку "sent mail", исходящие письма
 * Ответ на {@link RequestExRequestSentPostList}
 * При нажатии на письмо в списке шлется {@link RequestExRequestSentPost}, а в ответ {@link ExReplySentPost}.
 * При нажатии на "delete" шлется {@link RequestExDeleteSentPost}.
 * @see ExShowReceivedPostList аналогичный список принятой почты
 */
public class ExShowSentPostList extends L2GameServerPacket
{
	private static final GArray<Letter> EMPTY_LETTERS = new GArray<Letter>(0);
	private GArray<Letter> letters;

	public ExShowSentPostList(L2Player cha)
	{
		letters = MailParcelController.getInstance().getSent(cha.getObjectId());

		if(letters == null)
			letters = EMPTY_LETTERS;
	}

	// d dx[dSSddddd]
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xAC);

		writeD(1); // ?

		writeD(letters.size()); // количество писем
		for(Letter letter : letters)
		{
			writeD(letter.id); // уникальный id письма
			writeS(letter.topic); // топик
			writeS(letter.receiverName); // кому
			writeD(letter.price > 0 ? 1 : 0); // если тут 1 то письмо требует оплаты
			writeD(letter.validtime - (int) (System.currentTimeMillis() / 1000)); // время действительности письма в секундах
			writeD(letter.unread);
			writeD(0);
			writeD(Math.min(letter.attachments, 1)); // 1 - письмо с приложением, 0 - просто письмо
			if(getClient().isLindvior())
				writeD(0x00);
		}
	}
}