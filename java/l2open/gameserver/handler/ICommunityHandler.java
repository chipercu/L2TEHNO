package l2open.gameserver.handler;

import l2open.gameserver.model.L2Player;

public interface ICommunityHandler
{
	public void parsecmd(String command, L2Player activeChar);
	//public int getCommunityName();
	public Enum[] getCommunityCommandEnum();
}