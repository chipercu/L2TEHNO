package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExReceiveShowPostFriend;

public class RequestExShowPostFriendListForPostBox extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		player.sendPacket(new ExReceiveShowPostFriend(player));
	}
}