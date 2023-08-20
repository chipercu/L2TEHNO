package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.BlockList;

import java.util.Collection;
import java.util.logging.Logger;

public class RequestBlock extends L2GameClientPacket
{
	// format: cd(S)
	private static Logger _log = Logger.getLogger(L2Player.class.getName());

	private final static int BLOCK = 0;
	private final static int UNBLOCK = 1;
	private final static int BLOCKLIST = 2;
	private final static int ALLBLOCK = 3;
	private final static int ALLUNBLOCK = 4;

	private Integer _type;
	private String targetName = null;

	@Override
	public void readImpl()
	{
		_type = readD(); //0x00 - block, 0x01 - unblock, 0x03 - allblock, 0x04 - allunblock

		if(_type == BLOCK || _type == UNBLOCK)
			targetName = readS(ConfigValue.cNameMaxLen);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		switch(_type)
		{
			case BLOCK:
				activeChar.addToBlockList(targetName);
				break;
			case UNBLOCK:
				activeChar.removeFromBlockList(targetName);
				break;
			case BLOCKLIST:
				if(getClient().isLindvior())
					activeChar.sendPacket(new BlockList(activeChar));
				else
				{
					Collection<String> blockList = activeChar.getBlockList();

					if(blockList != null)
					{
						activeChar.sendPacket(Msg._IGNORE_LIST_);

						for(String name : blockList)
							activeChar.sendMessage(name);

						activeChar.sendPacket(Msg.__EQUALS__);
					}
				}
				break;
			case ALLBLOCK:
				activeChar.setBlockAll(true);
				activeChar.sendPacket(Msg.YOU_ARE_NOW_BLOCKING_EVERYTHING);
				break;
			case ALLUNBLOCK:
				activeChar.setBlockAll(false);
				activeChar.sendPacket(Msg.YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING);
				break;
			default:
				_log.info("Unknown 0x0a block type: " + _type);
		}
	}
}