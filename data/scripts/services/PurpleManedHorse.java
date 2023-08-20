package services;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Multisell.MultiSellListContainer;
import l2open.gameserver.model.base.MultiSellEntry;
import l2open.util.PrintfFormat;

public class PurpleManedHorse extends Functions implements ScriptFile
{
	private static boolean Enabled = false;
	private static boolean MultiSellLoaded = false;
	private static final int MultiSellID = -1001;
	private static final PrintfFormat dlg = new PrintfFormat("<br>[npc_%%objectId%%_Multisell %d|%s]");
	private static MultiSellListContainer list;

	public void onLoad()
	{
		if(ConfigValue.SellPets.isEmpty())
			return;
		String[] SELLPETS = ConfigValue.SellPets.split(";");
		if(SELLPETS.length == 0)
			return;

		list = new MultiSellListContainer();
		list.setNoTax(true);
		list.setShowAll(true);
		list.setKeepEnchant(false);
		list.setNoKey(true);
		int entId = 1;
		for(String SELLPET : SELLPETS)
		{
			MultiSellEntry e = L2Multisell.parseEntryFromStr(SELLPET);
			if(e != null)
			{
				e.setEntryId(entId++);
				list.addEntry(e);
			}
		}
		if(list.getEntries().size() == 0)
			return;

		Enabled = true;
		loadMultiSell();
		_log.info("Loaded Service: Purple-Maned Horses");
	}

	private static void loadMultiSell()
	{
		if(MultiSellLoaded)
			return;
		L2Multisell.getInstance().addMultiSellListContainer(MultiSellID, list);
		MultiSellLoaded = true;
	}

	public String PetManagersDialogAppend(Integer val)
	{
		if(val == 0 && Enabled)
			return dlg.sprintf(new Object[] { MultiSellID, isRus() ? "Приобрести новых питомцев" : "Buy New Pets" });
		return "";
	}

	public String DialogAppend_30731(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_30827(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_30828(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_30829(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_30830(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_30831(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_30869(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_31067(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_31265(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_31309(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	public String DialogAppend_31954(Integer val)
	{
		return PetManagersDialogAppend(val);
	}

	private static boolean isRus(L2Player player)
	{
		return player.isLangRus();
	}

	private boolean isRus()
	{
		return isRus((L2Player) getSelf());
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public static void OnReloadMultiSell()
	{
		MultiSellLoaded = false;
		if(Enabled)
			loadMultiSell();
	}
}