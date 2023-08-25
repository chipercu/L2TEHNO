package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.serverpackets.ExShowSentPostList;

/**
 * Запрос на удаление отправленных сообщений. Удалить можно только письмо без вложения. Отсылается при нажатии на "delete" в списке отправленных писем.
 * @see ExShowSentPostList
 * @see RequestExDeleteReceivedPost
 */
public class RequestExDeleteSentPost extends L2GameClientPacket
{
	private int _count;
	private int[] _list;

	/**
	 * format: dx[d]
	 */
	@Override
	public void readImpl()
	{
		_count = readD(); // количество элементов для удаления
		if(_count * 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}
		_list = new int[_count];
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
			cha.sendPacket(new ExShowSentPostList(cha));
	}
}