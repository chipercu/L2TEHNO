package l2open.gameserver.clientpackets;


import l2open.gameserver.instancemanager.HandysBlockCheckerManager;
import l2open.gameserver.model.L2Player;
import java.util.logging.Logger;

/**
 * Format: chddd
 * 
 * d: Arena
 * d: Answer
 */
public final class RequestExCubeGameReadyAnswer extends L2GameClientPacket
{
	private static final Logger _log = Logger.getLogger(RequestExCubeGameChangeTeam.class.getName());

	int _arena;
	int _answer;

	@Override
	protected void readImpl()
	{
		_arena = readD() + 1;
		_answer = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		switch(_answer)
		{
			case 0:
				// Cancel
				break;
			case 1:
				// OK or Time Over
				HandysBlockCheckerManager.getInstance().increaseArenaVotes(_arena);
				break;
			default:
				_log.warning("Unknown Cube Game Answer ID: " + _answer);
				break;
		}
	}
}
