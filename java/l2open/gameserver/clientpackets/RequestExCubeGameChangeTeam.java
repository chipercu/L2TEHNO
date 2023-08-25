package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.HandysBlockCheckerManager;
import l2open.gameserver.model.L2Player;

import java.util.logging.Logger;

/**
 * Format: chdd
 * d: team
 */
public final class RequestExCubeGameChangeTeam extends L2GameClientPacket
{
	private static final Logger _log = Logger.getLogger(RequestExCubeGameChangeTeam.class.getName());

	int _team, _arena;

	@Override
	protected void readImpl()
	{
		_arena = readD() + 1;
		_team = readD();
	}

	@Override
	protected void runImpl()
	{
		if(HandysBlockCheckerManager.getInstance().arenaIsBeingUsed(_arena))
			return;
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.isDead())
			return;

		switch(_team)
		{
			case 0:
			case 1:
				// Change Player Team
				HandysBlockCheckerManager.getInstance().changePlayerToTeam(activeChar, _arena, _team);
				break;
			case -1:
			{
				int team = HandysBlockCheckerManager.getInstance().getHolder(_arena).getPlayerTeam(activeChar);
				// client sends two times this packet if click on exit
				// client did not send this packet on restart
				if(team > -1)
					HandysBlockCheckerManager.getInstance().removePlayer(activeChar, _arena, team);
				break;
			}
			default:
				_log.warning("Wrong Team ID: " + _team);
				break;
		}
	}
}

