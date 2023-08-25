package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.model.entity.Duel;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 *  format  chddd
 */

public class RequestDuelAnswerStart extends L2GameClientPacket
{
	private int _response;
	private int _duelType;
	@SuppressWarnings("unused")
	private int _unk1;

	@Override
	public void readImpl()
	{
		_duelType = readD();
		_unk1 = readD();
		_response = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Transaction transaction = player.getTransaction();

		if(transaction == null)
			return;

		if(!transaction.isValid() || !transaction.isTypeOf(TransactionType.DUEL))
		{
			transaction.cancel();
			player.sendPacket(Msg.TIME_EXPIRED, Msg.ActionFail);
			return;
		}

		L2Player requestor = transaction.getOtherPlayer(player);

		transaction.cancel();

		if(_response == 1)
		{
			SystemMessage msg1, msg2;
			if(_duelType == 1)
			{
				msg1 = new SystemMessage(SystemMessage.YOU_HAVE_ACCEPTED_S1S_CHALLENGE_TO_A_PARTY_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
				msg2 = new SystemMessage(SystemMessage.S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
			}
			else
			{
				msg1 = new SystemMessage(SystemMessage.YOU_HAVE_ACCEPTED_S1S_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
				msg2 = new SystemMessage(SystemMessage.S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
			}

			player.sendPacket(msg1.addString(requestor.getName()));
			requestor.sendPacket(msg2.addString(player.getName()));

			Duel.createDuel(requestor, player, _duelType);
		}
		else if(_duelType == 1)
			requestor.sendPacket(Msg.THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
		else
			requestor.sendPacket(new SystemMessage(SystemMessage.S1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL).addString(player.getName()));
	}
}