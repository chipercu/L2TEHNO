package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Alliance;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.Log;

/**
 *  format  c(d)
 */
public class RequestAnswerJoinAlly extends L2GameClientPacket
{
	private int _response;

	@Override
	public void readImpl()
	{
		_response = _buf.remaining() >= 4 ? readD() : 0;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar != null)
		{
			Transaction transaction = activeChar.getTransaction();

			if(transaction == null)
				return;

			if(!transaction.isValid() || !transaction.isTypeOf(TransactionType.ALLY))
			{
				transaction.cancel();
				activeChar.sendPacket(Msg.TIME_EXPIRED, Msg.ActionFail);
				return;
			}

			L2Player requestor = transaction.getOtherPlayer(activeChar);

			transaction.cancel();

			if(requestor.getAlliance() == null)
				return;

			if(_response == 1)
			{
				L2Alliance ally = requestor.getAlliance();
				activeChar.sendPacket(Msg.YOU_HAVE_ACCEPTED_THE_ALLIANCE);
				activeChar.getClan().setAllyId(requestor.getAllyId());
				PlayerData.getInstance().updateClanInDB(activeChar.getClan());
				ally.addAllyMember(activeChar.getClan(), true);
				ally.broadcastAllyStatus(true);
				Log.add("JOIN_ALLY: ally_name="+ally.getAllyName()+" clan="+activeChar.getClan().getName()+" requestor="+requestor.getName(), "alli_info");
			}
			else
				requestor.sendPacket(Msg.YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE);
		}
	}
}