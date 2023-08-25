package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.TradeStart;

public class AnswerTradeRequest extends L2GameClientPacket
{
	// Format: cd
	private int _response;

	@Override
	public void readImpl()
	{
		_response = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Transaction transaction = activeChar.getTransaction();

		if(transaction == null || activeChar.is_block)
			return;

		if(!transaction.isValid() || !transaction.isTypeOf(TransactionType.TRADE_REQUEST))
		{
			transaction.cancel();
			if(_response == 1)
				activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_ONLINE, Msg.ActionFail);
			else
				activeChar.sendPacket(Msg.TIME_EXPIRED, Msg.ActionFail);
			return;
		}

		L2Player requestor = transaction.getOtherPlayer(activeChar);

		if(activeChar.getTeam() != 0 || requestor.getTeam() != 0 || activeChar.isInEvent() > 0 || requestor.isInEvent() > 0)
		{
			requestor.sendActionFailed();
			return;
		}
		else if(_response != 1 || activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			requestor.sendPacket(new SystemMessage(SystemMessage.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE).addString(activeChar.getName()), Msg.ActionFail);
			transaction.cancel();
			if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
				activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		transaction.cancel();

		new Transaction(TransactionType.TRADE, activeChar, requestor);

		requestor.sendPacket(new SystemMessage(SystemMessage.YOU_BEGIN_TRADING_WITH_C1).addString(activeChar.getName()), new TradeStart(requestor, activeChar));
		activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_BEGIN_TRADING_WITH_C1).addString(requestor.getName()), new TradeStart(activeChar, requestor));
	}
}