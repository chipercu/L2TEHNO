package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2HennaInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.player.PlayerData;

public class RequestHennaUnequip extends L2GameClientPacket
{
	private int _symbolId;

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if (player == null)
			return;

		for (int i = 1; i <= 3; i ++)
		{
			L2HennaInstance henna = player.getHenna(i);
			if (henna == null)
				continue;

			if (henna.getSymbolId() == _symbolId)
			{
				if(player.getInventory().getCountOf(ConfigValue.HennaRemoveItemId) >= ConfigValue.HennaRemoveItemCount)
				{
					if(ConfigValue.HennaRemoveItemCount > 0)
						player.getInventory().destroyItemByItemId(ConfigValue.HennaRemoveItemId, ConfigValue.HennaRemoveItemCount, true);
					PlayerData.getInstance().removeHenna(player, i);
					player.sendPacket(new SystemMessage(SystemMessage.THE_SYMBOL_HAS_BEEN_DELETED));
				}
				else
				{
					if(ConfigValue.HennaRemoveItemId == 57)
						player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
					else
						player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				}
				
				break;
			}
		}
	}

	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		_symbolId = readD();
	}
}