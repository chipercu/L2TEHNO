package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.model.entity.Duel;
import l2open.gameserver.serverpackets.ExDuelAskStart;
import l2open.gameserver.serverpackets.SystemMessage;

import java.util.logging.Logger;

public class RequestDuelStart extends L2GameClientPacket
{
	// format: (ch)Sd
	private static Logger _log = Logger.getLogger(RequestDuelStart.class.getName());
	private String _name;
	private int _duelType;

	@Override
	public void readImpl()
	{
		_name = readS(ConfigValue.cNameMaxLen);
		_duelType = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		L2Player targetChar = L2World.getPlayer(_name);
		if(activeChar == null || activeChar.is_block)
			return;

		else if(targetChar == null || targetChar == activeChar)
		{
			activeChar.sendPacket(Msg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
			return;
		}

		// Check if duel is possible
		else if(!Duel.checkIfCanDuel(activeChar, activeChar, true) || !Duel.checkIfCanDuel(activeChar, targetChar, true))
			return;

		// Duel is a party duel
		else if(_duelType == 1)
		{
			/* Заглушка, нам не известны координаты стадионов для парти,
			 * а сваливать всех в одно место - бред.
			 * Собственно говоря нужно найти координаты, сделать обработку стадионов, убрать заглушку.
			 */
			if(Boolean.TRUE)
			{
				activeChar.sendMessage("Sorry, but party duels are currently disabled. If you know coords of duel stadium pleace contact developers.");
				return;
			}

			L2Party activeCharParty = activeChar.getParty();

			// Player must be in a party & the party leader
			if(activeCharParty == null || activeCharParty.isLeader(activeChar))
			{
				activeChar.sendMessage("You have to be the leader of a party in order to request a party duel.");
				return;
			}
			// Target must be in a party
			else if(!targetChar.isInParty())
			{
				activeChar.sendPacket(Msg.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
				return;
			}
			// Target may not be of the same party
			else if(activeCharParty.getPartyMembers().contains(targetChar))
			{
				activeChar.sendMessage("This player is a member of your own party.");
				return;
			}

			// Check if every player is ready for a duel
			for(L2Player temp : activeCharParty.getPartyMembers())
			{
				if(!Duel.checkIfCanDuel(activeChar, temp, false))
				{
					activeChar.sendMessage("Not all the members of your party are ready for a duel.");
					return;
				}
				if(temp.getTransformation() != 0)
				{
					activeChar.sendPacket(Msg.PARTY_DUEL_CANNOT_BE_INITIATED_DUEL_TO_A_POLYMORPHED_PARTY_MEMBER);
					return;
				}
			}
			L2Player targetLeader = null; // snatch party leader of target's party
			for(L2Player temp : targetChar.getParty().getPartyMembers())
			{
				if(targetChar.getParty().isLeader(temp))
					targetLeader = temp;

				if(!Duel.checkIfCanDuel(activeChar, temp, false))
				{
					activeChar.sendPacket(Msg.THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL);
					return;
				}
				if(temp.getTransformation() != 0)
				{
					activeChar.sendPacket(Msg.PARTY_DUEL_CANNOT_BE_INITIATED_DUEL_TO_A_POLYMORPHED_PARTY_MEMBER);
					return;
				}
			}

			//Никогда не должно случатся, если случилось то кто-то сломал L2Party
			if(targetLeader == null)
			{
				_log.warning("Some asshole has broken L2Party. Can't get party leader.");
				return;
			}

			// Send request to target's party leader
			if(!targetLeader.isInTransaction())
			{
				new Transaction(TransactionType.DUEL, activeChar, targetLeader, 10000);
				targetLeader.sendPacket(new ExDuelAskStart(activeChar.getName(), _duelType));

				SystemMessage msg = new SystemMessage(SystemMessage.S1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL);
				msg.addString(targetLeader.getName());
				activeChar.sendPacket(msg);

				msg = new SystemMessage(SystemMessage.S1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL);
				msg.addString(activeChar.getName());
				targetChar.sendPacket(msg);
			}
			else
				activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(targetLeader.getName()));
		}
		else if(!targetChar.isInTransaction())
		{
			Transaction transaction = new Transaction(TransactionType.DUEL, activeChar, targetChar, 10000);
			activeChar.setTransaction(transaction);
			targetChar.setTransaction(transaction);

			SystemMessage msg = new SystemMessage(SystemMessage.S1_HAS_BEEN_CHALLENGED_TO_A_DUEL);
			msg.addString(targetChar.getName());
			activeChar.sendPacket(msg);

			msg = new SystemMessage(SystemMessage.S1_HAS_CHALLENGED_YOU_TO_A_DUEL);
			msg.addString(activeChar.getName());
			targetChar.sendPacket(new ExDuelAskStart(activeChar.getName(), _duelType), msg);
		}
		else
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(targetChar.getName()));
	}
}