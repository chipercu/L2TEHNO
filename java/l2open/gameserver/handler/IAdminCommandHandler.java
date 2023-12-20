package l2open.gameserver.handler;

import l2open.gameserver.model.L2Player;
import l2open.database.exceptions.ResourceProvideException;

public interface IAdminCommandHandler
{
	/**
	 * this is the worker method that is called when someone uses an admin command.
	 * @param fullString TODO
	 * @param activeChar
	 * @param comm
	 * @return command success
	 */
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar) throws ResourceProvideException;

	/**
	 * this method is called at initialization to register all the item ids automatically
	 * @return all known itemIds
	 */
	public Enum[] getAdminCommandEnum();
}