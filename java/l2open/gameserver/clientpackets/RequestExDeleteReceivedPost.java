package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.serverpackets.ExShowReceivedPostList;

/**
 * Запрос на удаление полученных сообщений. Удалить можно только письмо без вложения. Отсылается при нажатии на "delete" в списке полученных писем.
 * @see ExShowReceivedPostList
 * @see RequestExDeleteSentPost
 */
public class RequestExDeleteReceivedPost extends L2GameClientPacket
{
	private int _count;
	private int[] _list;

	/**
	 * format: dx[d]
	 */
	@Override
	public void readImpl()
	{
		_count = readD();
		if(_count * 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}
		_list = new int[_count]; // количество элементов для удаления
		for(int i=0;i<_count;i++)
			_list[i] = readD(); // уникальный номер письма
	}

	@Override
	public void runImpl()
	{
		if(_count == 0)
			return;

		MailParcelController.getInstance().deleteLetter(_list);

		L2Player cha = getClient().getActiveChar();
		if(cha != null)
			cha.sendPacket(new ExShowReceivedPostList(cha));
	}
}