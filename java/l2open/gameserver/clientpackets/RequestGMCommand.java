package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.serverpackets.*;

public class RequestGMCommand extends L2GameClientPacket
{
	// format: cSdd
	private String _targetName;
	private int _command;
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	public void readImpl()
	{
		_targetName = readS();
		_command = readD();
		_unknown = readD(); //always 0
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = L2World.getPlayer(_targetName);
		if(activeChar == null || getClient().getActiveChar() == null || !getClient().getActiveChar().getPlayerAccess().CanViewChar || getClient().getActiveChar().is_block)
			return;

		switch(_command)
		{
			case 1:
				sendPacket(new GMViewCharacterInfo(activeChar));
				sendPacket(new GMHennaInfo(activeChar));
				break;
			case 2:
				if(activeChar.getClan() != null)
					sendPacket(new GMViewPledgeInfo(activeChar.getClan(), activeChar));
				break;
			case 3:
				sendPacket(new GMViewSkillInfo(activeChar));
				break;
			case 4:
				sendPacket(new GMViewQuestInfo(activeChar));
				break;
			case 5:
				sendPacket(new GMViewItemList(activeChar));
				break;
			case 6:
				sendPacket(new GMViewWarehouseWithdrawList(activeChar));
				break;
			default:
				_log.info("Request Unknown GMCommand :: " + _command);
		}
	}
}