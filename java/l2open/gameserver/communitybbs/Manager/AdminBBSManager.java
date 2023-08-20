package l2open.gameserver.communitybbs.Manager;

import l2open.gameserver.model.L2Player;

public class AdminBBSManager extends BaseBBSManager
{
	private static AdminBBSManager _Instance = null;

	public static AdminBBSManager getInstance()
	{
		if(_Instance == null)
			_Instance = new AdminBBSManager();
		return _Instance;
	}

	@Override
	public void parsecmd(String command, L2Player activeChar)
	{
		if(activeChar.getPlayerAccess().IsGM)
			return;
		if(command.startsWith("admin_bbs"))
			separateAndSend("<html><body><br><br><center>This Page is only an exemple :)<br><br>command=" + command + "</center></body></html>", activeChar);
		else
			separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", activeChar);
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{
		if(activeChar.getPlayerAccess().IsGM)
			return;
	}
}