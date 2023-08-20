package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestBR_MinigameInsertScore extends L2GameClientPacket
{
	private int _score;

	@Override
	protected void readImpl() throws Exception
	{
		_score = readD();
	}

	@Override
	protected void runImpl() throws Exception
	{
		L2Player player = getClient().getActiveChar();
		//if(player == null || !Config.EX_JAPAN_MINIGAME)
		//	return;
		//MiniGameScoreManager.getInstance().insertScore(player, _score);
	}
}