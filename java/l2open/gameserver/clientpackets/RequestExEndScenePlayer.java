package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestExEndScenePlayer extends L2GameClientPacket
{
	private int _movieId;

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(!activeChar.isInMovie() || activeChar.getMovieId() != _movieId)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setIsInMovie(false);
		activeChar.setMovieId(0);
		activeChar.decayMe();
		activeChar.spawnMe();
	}
	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		_movieId = readD();
	}
}