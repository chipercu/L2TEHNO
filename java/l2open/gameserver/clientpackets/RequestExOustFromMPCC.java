package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.serverpackets.SystemMessage;

public class RequestExOustFromMPCC extends L2GameClientPacket
{
	private String _name;

	/**
	 * format: chS
	 */
	@Override
	public void readImpl()
	{
		_name = readS(ConfigValue.cNameMaxLen);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || !activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
			return;

		L2Player target = L2World.getPlayer(_name);

		// Чар с таким имененм не найден в мире
		if(target == null)
		{
			activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
			return;
		}

		// Сам себя нельзя
		if(activeChar == target)
			return;

		// Указанный чар не в пати, не в СС, в чужом СС
		if(!target.isInParty() || !target.getParty().isInCommandChannel() || activeChar.getParty().getCommandChannel() != target.getParty().getCommandChannel())
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return;
		}

		// Это может делать только лидер СС
		if(activeChar.getParty().getCommandChannel().getChannelLeader() != activeChar)
		{
			activeChar.sendPacket(Msg.ONLY_THE_CREATOR_OF_A_CHANNEL_CAN_ISSUE_A_GLOBAL_COMMAND);
			return;
		}

		target.getParty().getCommandChannel().getChannelLeader().sendPacket(new SystemMessage(SystemMessage.S1_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL).addString(target.getName()));
		target.getParty().getCommandChannel().removeParty(target.getParty());
		target.getParty().broadcastToPartyMembers(Msg.YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL);
	}
}