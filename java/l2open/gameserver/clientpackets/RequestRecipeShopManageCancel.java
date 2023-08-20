package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.RecipeShopSellList;

// Deprecated?
public class RequestRecipeShopManageCancel extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getTarget() == null)
			return;

		if(activeChar.getDuel() != null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isAlikeDead())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getTarget().isPlayer())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2Player target = (L2Player) activeChar.getTarget();
		activeChar.sendPacket(new RecipeShopSellList(activeChar, target));
	}
}