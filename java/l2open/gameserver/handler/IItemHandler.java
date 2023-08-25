package l2open.gameserver.handler;

import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.items.L2ItemInstance;

/**
 * Mother class of all itemHandlers.<BR><BR>
 * an IItemHandler implementation has to be stateless
 */
public interface IItemHandler
{
	/**
	 * Launch task associated to the item.
	 * @param item : L2ItemInstance designating the item to use
	 * @param ctrl TODO
	 * @param activeChar : L2PlayableInstance designating the player
	 */
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl);

	/**
	 * Returns the list of item IDs corresponding to the type of item.<BR><BR>
	 * <B><I>Use :</I></U><BR>
	 * This method is called at initialization to register all the item IDs automatically
	 * @return int[] designating all itemIds for a type of item.
	 */
	public int[] getItemIds();
}
