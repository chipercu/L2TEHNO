package commands.voiced;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2TradeList;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.entity.olympiad.Olympiad;

public class Offline extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "offline", "ghost", "offtrade" };

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		if(activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUFF)
			return setOfflineBuffer(activeChar);
		else if(!ConfigValue.AllowOfflineTrade)
		{
			activeChar.sendMessage("Не верная команда.");
			return false;
		}
		else if(activeChar.getOlympiadObserveId() != -1 || activeChar.getOlympiadGame() != null || Olympiad.isRegisteredInComp(activeChar) || activeChar.getKarma() > 0)
		{
			activeChar.sendActionFailed();
			return false;
		}
		else if(activeChar.getLevel() < ConfigValue.OfflineMinLevel)
		{
			show(new CustomMessage("scripts.commands.user.offline.LowLevel", activeChar).addNumber(ConfigValue.OfflineMinLevel), activeChar);
			return false;
		}
		else if(!activeChar.isInStoreMode())
		{
			show(new CustomMessage("scripts.commands.user.offline.IncorrectUse", activeChar), activeChar);
			return false;
		}
		else if(activeChar.getNoChannelRemained() > 0)
		{
			show(new CustomMessage("scripts.commands.user.offline.BanChat", activeChar), activeChar);
			return false;
		}
		else if(activeChar.isActionBlocked(L2Zone.BLOCKED_ACTION_PRIVATE_STORE) && activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_BUFF)
		{
			activeChar.sendMessage(new CustomMessage("trade.OfflineNoTradeZone", activeChar));
			return false;
		}
		else if(activeChar.isInZone(L2Zone.ZoneType.Siege) || activeChar.isCursedWeaponEquipped() || activeChar.isCombatFlagEquipped() || activeChar.isTerritoryFlagEquipped())
		{
			activeChar.sendActionFailed();
			return false;
		}
		else if(activeChar.getVar("jailed") != null)
			return false;
		else if(ConfigValue.OfflineTradePrice > 0 && ConfigValue.OfflineTradePriceItem > 0)
		{
			if(getItemCount(activeChar, ConfigValue.OfflineTradePriceItem) < ConfigValue.OfflineTradePrice)
			{
				show(new CustomMessage("scripts.commands.user.offline.NotEnough", activeChar).addItemName(ConfigValue.OfflineTradePriceItem).addNumber(ConfigValue.OfflineTradePrice), activeChar);
				return false;
			}
			removeItem(activeChar, ConfigValue.OfflineTradePriceItem, ConfigValue.OfflineTradePrice);
		}

		L2TradeList.validateList(activeChar);

		if(activeChar.getPet() != null)
			activeChar.getPet().unSummon();

		activeChar.offline();
		return true;
	}

	private boolean setOfflineBuffer(L2Player activeChar)
	{
		if(!ConfigValue.AllowOfflineBuffStore)
		{
			activeChar.sendMessage("Не верная команда.");
			return false;
		}
		else if(activeChar.getOlympiadObserveId() != -1 || activeChar.getOlympiadGame() != null || Olympiad.isRegisteredInComp(activeChar) || activeChar.getKarma() > 0)
		{
			activeChar.sendActionFailed();
			return false;
		}
		else if(activeChar.getLevel() < ConfigValue.BuffStoreMinLevel)
		{
			show(new CustomMessage("scripts.commands.user.offline.LowLevel", activeChar).addNumber(ConfigValue.BuffStoreMinLevel), activeChar);
			return false;
		}
		else if(!activeChar.isInStoreMode())
		{
			show(new CustomMessage("scripts.commands.user.offline.IncorrectUse", activeChar), activeChar);
			return false;
		}
		else if(activeChar.getNoChannelRemained() > 0)
		{
			show(new CustomMessage("scripts.commands.user.offline.BanChat", activeChar), activeChar);
			return false;
		}
		else if(activeChar.isInZone(L2Zone.ZoneType.Siege) || activeChar.isCursedWeaponEquipped() || activeChar.isCombatFlagEquipped() || activeChar.isTerritoryFlagEquipped())
		{
			activeChar.sendActionFailed();
			return false;
		}
		else if(activeChar.getVar("jailed") != null)
			return false;
		else if(ConfigValue.BuffStorePrice > 0 && ConfigValue.BuffStorePriceItem > 0)
		{
			if(getItemCount(activeChar, ConfigValue.BuffStorePriceItem) < ConfigValue.BuffStorePrice)
			{
				show(new CustomMessage("scripts.commands.user.offline.NotEnough", activeChar).addItemName(ConfigValue.BuffStorePriceItem).addNumber(ConfigValue.BuffStorePrice), activeChar);
				return false;
			}
			removeItem(activeChar, ConfigValue.BuffStorePriceItem, ConfigValue.BuffStorePrice);
		}

		L2TradeList.validateList(activeChar);

		if(activeChar.getPet() != null)
			activeChar.getPet().unSummon();

		activeChar.offline();
		return true;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}