package l2open.gameserver.clientpackets;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;

public class Logout extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		// Dont allow leaving if player is fighting
		if(activeChar.isInCombat())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_EXIT_WHILE_IN_COMBAT, Msg.ActionFail);
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING, Msg.ActionFail);
			return;
		}

		if(activeChar.isBlocked() && !activeChar.isFlying()) // Разрешаем выходить из игры если используется сервис HireWyvern. Вернет в начальную точку.
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.Logout.OutOfControl", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		// Prevent player from logging out if they are a festival participant
		// and it is in progress, otherwise notify party members that the player
		// is not longer a participant.
		if(activeChar.isFestivalParticipant())
		{
			if(SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				activeChar.sendMessage("You cannot log out while you are a participant in a festival.");
				activeChar.sendActionFailed();
				return;
			}
			L2Party playerParty = activeChar.getParty();
			if(playerParty != null)
				playerParty.broadcastMessageToPartyMembers(activeChar.getName() + " has been removed from the upcoming festival.");
		}

		if(activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.Logout.Olympiad", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.inObserverMode())
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.Logout.Observer", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		activeChar.logout(false, false, false, false);
	}
}