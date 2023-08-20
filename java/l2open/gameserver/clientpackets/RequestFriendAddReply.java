package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.tables.FriendsTable;

// format: cd
public class RequestFriendAddReply extends L2GameClientPacket
{
	private int _response;

	@Override
	public void readImpl()
	{
		_response = _buf.hasRemaining() ? readD() : 0;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Transaction transaction = activeChar.getTransaction();

		if(transaction == null)
			return;

		if(!transaction.isValid() || !transaction.isTypeOf(TransactionType.FRIEND))
		{
			transaction.cancel();
			activeChar.sendPacket(Msg.TIME_EXPIRED, Msg.ActionFail);
			return;
		}

		L2Player requestor = transaction.getOtherPlayer(activeChar);

		transaction.cancel();

		if(_response == 1 && !FriendsTable.getInstance().checkIsFriends(requestor.getObjectId(), activeChar.getObjectId()))
			FriendsTable.getInstance().addFriend(requestor, activeChar);
		else
			requestor.sendPacket(Msg.YOU_HAVE_FAILED_TO_INVITE_A_FRIEND);
	}
}