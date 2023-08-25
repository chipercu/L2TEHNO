package l2open.gameserver.communitybbs.Manager;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

public class SmsBBSManager extends BaseBBSManager
{
	public static SmsBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}

	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "303.htm", player);
		content = content.replace("%name%", player.getName());
		separateAndSend(content, player);
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{
	}

	private static class SingletonHolder
	{
		protected static final SmsBBSManager _instance = new SmsBBSManager();
	}
}