package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;

/**
 * format: (ch)d
 */
public class AnswerJoinPartyRoom extends L2GameClientPacket
{
	private int _response;

	@Override
	public void readImpl()
	{
		if(_buf.hasRemaining())
			_response = readD();
		else
			_response = 0;
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

		if(!transaction.isValid() || !transaction.isTypeOf(TransactionType.PARTY_ROOM))
		{
			transaction.cancel();
			activeChar.sendPacket(Msg.TIME_EXPIRED, Msg.ActionFail);
			return;
		}

		L2Player requestor = transaction.getOtherPlayer(activeChar);

		transaction.cancel();

		if(_response == 1)
		{
			if(requestor.getPartyRoom() <= 0)
			{
				activeChar.sendActionFailed();
				return;
			}
			if(activeChar.getPartyRoom() > 0)
			{
				activeChar.sendActionFailed();
				return;
			}
			PartyRoomManager.getInstance().joinPartyRoom(activeChar, requestor.getPartyRoom());
		}
		else
			requestor.sendPacket(Msg.THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY);

		//TODO проверить на наличие пакета ДОБАВЛЕНИЯ в список, в другом случае отсылать весь список всем мемберам
	}
}