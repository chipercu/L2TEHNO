package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.serverpackets.ExReplySentPost;
import l2open.gameserver.serverpackets.ExShowSentPostList;

/**
 * Запрос на удаление письма с приложениями. Возвращает приложения отправителю на личный склад и удаляет письмо. Ответ на кнопку Cancel в {@link ExReplySentPost}.
 */
public class RequestExCancelSentPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		postId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player cha = getClient().getActiveChar();
		if(cha == null)
			return;

		MailParcelController.getInstance().returnLetter(postId, false);

		cha.sendPacket(new ExShowSentPostList(cha));
	}
}