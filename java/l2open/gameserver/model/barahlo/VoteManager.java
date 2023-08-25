package l2open.gameserver.model.barahlo;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.ConfirmDlg;
import l2open.gameserver.serverpackets.SystemMessage;

public class VoteManager
{
	private static VoteManager _instance = new VoteManager();
	
	public int vote_yes = 0;
	public int vote_no = 0;
	public int vote_time = 0;
	public boolean vote_start = false;

	public VoteManager()
	{}

	public void addVote(L2Player activeChar, int result)
	{
		if(vote_start)
		{
			if(result == 1)
				vote_yes++;
			else
				vote_no++;
		}
		else
			activeChar.sendMessage("Голосование окончено.");
	}

	public void endVote()
	{
		vote_start = false;
		int all=(vote_yes+vote_no);
		int percent_yes = (int) (vote_yes/(all/100f));
		int percent_no = 100-percent_yes;

		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null)
			{
				player.sendMessage("Голосование окончено.");
				player.sendMessage("Результат: За "+percent_yes+"%, Против "+percent_no+"%.");
			}
	}

	public void voteAnsver(String text)
	{
		vote_start = true;
		ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
		{
			public void runImpl()
			{
				endVote();
			}
		}, 60000);
		ConfirmDlg cd = new ConfirmDlg(SystemMessage.S1, 60000, 5).addString(text);
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null)
				player.sendPacket(cd);
	}

	public static VoteManager getInstance()
	{
		return _instance;
	}
}