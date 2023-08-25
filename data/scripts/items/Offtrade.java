package items;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2TradeList;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.items.L2ItemInstance;

public class Offtrade extends Functions implements IItemHandler, ScriptFile
{
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player activeChar = (L2Player) playable;

		if(activeChar.getOlympiadObserveId() != -1 || activeChar.getOlympiadGame() != null || Olympiad.isRegisteredInComp(activeChar) || activeChar.getKarma() > 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getLevel() < ConfigValue.OfflineMinLevel)
		{
			show(new CustomMessage("scripts.commands.user.offline.LowLevel", activeChar).addNumber(ConfigValue.OfflineMinLevel), activeChar);
			return;
		}

		if(!activeChar.isInStoreMode())
		{
			show(new CustomMessage("scripts.commands.user.offline.IncorrectUse", activeChar), activeChar);
			return;
		}

		if(activeChar.getNoChannelRemained() > 0)
		{
			show(new CustomMessage("scripts.commands.user.offline.BanChat", activeChar), activeChar);
			return;
		}

		if(activeChar.isActionBlocked(L2Zone.BLOCKED_ACTION_PRIVATE_STORE))
		{
			activeChar.sendMessage(new CustomMessage("trade.OfflineNoTradeZone", activeChar));
			return;
		}

		if(activeChar.isInZone(L2Zone.ZoneType.Siege) || activeChar.isCursedWeaponEquipped() || activeChar.isCombatFlagEquipped() || activeChar.isTerritoryFlagEquipped())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getVar("jailed") != null)
			return;

		if(ConfigValue.OfflineTradePrice > 0 && ConfigValue.OfflineTradePriceItem > 0)
		{
			if(getItemCount(activeChar, ConfigValue.OfflineTradePriceItem) < ConfigValue.OfflineTradePrice)
			{
				show(new CustomMessage("scripts.commands.user.offline.NotEnough", activeChar).addItemName(ConfigValue.OfflineTradePriceItem).addNumber(ConfigValue.OfflineTradePrice), activeChar);
				return;
			}
			removeItem(activeChar, ConfigValue.OfflineTradePriceItem, ConfigValue.OfflineTradePrice);
		}

		activeChar.getInventory().destroyItem(item, 1, false);
		L2TradeList.validateList(activeChar);

		if(activeChar.getPet() != null)
			activeChar.getPet().unSummon();

		activeChar.offline();
	}

	public final int[] getItemIds()
	{
		return ConfigValue.OfftradeItem;
	}

	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}