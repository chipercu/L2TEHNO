package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.serverpackets.ExReplyReceivedPost;
import l2open.gameserver.serverpackets.ExShowReceivedPostList;

/**
 * Шлется клиентом при согласии принять письмо в {@link ExReplyReceivedPost}. Если письмо с оплатой то создателю письма шлется запрошенная сумма.
 */
public class RequestExReceivePost extends L2GameClientPacket
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
		if(cha == null || cha.is_block)
			return;

		MailParcelController.getInstance().receivePost(postId, cha);

		cha.sendPacket(new ExShowReceivedPostList(cha));
	}
}