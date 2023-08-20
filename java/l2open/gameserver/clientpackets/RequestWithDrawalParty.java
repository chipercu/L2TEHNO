package l2open.gameserver.clientpackets;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.DimensionalRift;
import l2open.gameserver.model.entity.olympiad.Olympiad;

public class RequestWithDrawalParty extends L2GameClientPacket
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
		if(activeChar.isInParty())
		{
			if(Olympiad.isRegistered(activeChar) || activeChar.getOlympiadGame() != null || activeChar.isInOlympiadMode())
			{
				activeChar.sendPacket(Msg.FAILED_TO_WITHDRAW_FROM_THE_PARTY);
				return;
			}
			Reflection r = activeChar.getParty().getReflection();
			if(r != null && r instanceof DimensionalRift && activeChar.getReflection().equals(r))
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestWithDrawalParty.Rift", activeChar));
			else if(r != null && (activeChar.isInCombat() || !activeChar.can_create_party))
				activeChar.sendPacket(Msg.FAILED_TO_WITHDRAW_FROM_THE_PARTY);
			else
				activeChar.getParty().oustPartyMember(activeChar);
		}
	}
}