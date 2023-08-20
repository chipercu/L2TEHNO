package events.CofferofShadows;

import java.util.HashMap;
import java.util.Map.Entry;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Drop;
import l2open.gameserver.model.L2DropData;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Rnd;

public class Coffer implements IItemHandler, ScriptFile
{
	// Дроп для эвентого сундука Coffer of Shadows
	private static final int[] _itemIds = { 8659 };

	protected static final L2DropData[] _dropmats = new L2DropData[] {
	//                                     Item                      Chance
			// Материалы
			new L2DropData(4041, 1, 1, 250, 1), // Mold Hardener         0.025%
			new L2DropData(4042, 1, 1, 450, 1), // Enria                 0.045%
			new L2DropData(4040, 1, 1, 500, 1), // Mold Lubricant        0.05%
			new L2DropData(1890, 1, 3, 833, 1), // Mithril Alloy         0.0833%
			new L2DropData(5550, 1, 3, 833, 1), // Durable Metal Plate   0.0833%
			new L2DropData(4039, 1, 1, 833, 1), // Mold Glue             0.0833%
			new L2DropData(4043, 1, 1, 833, 1), // Asofe                 0.0833%
			new L2DropData(4044, 1, 1, 833, 1), // Thons                 0.0833%
			new L2DropData(1888, 1, 3, 1000, 1), // Synthetic Cokes      0.1%
			new L2DropData(1877, 1, 3, 1000, 1), // Adamantite Nugget    0.1%
			new L2DropData(1894, 1, 3, 3000, 1), // Crafted Leather      0.3%
			new L2DropData(1874, 1, 5, 3000, 1), // Oriharukon Ore       0.3%
			new L2DropData(1875, 1, 5, 3000, 1), // Stone of Purity      0.3%
			new L2DropData(1887, 1, 3, 3000, 1), // Varnish of Purity    0.3%
			new L2DropData(1866, 1, 10, 16666, 1), // Suede              1.6666%
			new L2DropData(1882, 1, 10, 16666, 1), // Leather            1.6666%
			new L2DropData(1881, 1, 10, 10000, 1), // Coarse Bone Powder 1%
			new L2DropData(1873, 1, 10, 10000, 1), // Silver Nugget      1%
			new L2DropData(1879, 1, 5, 10000, 1), // Cokes               1%
			new L2DropData(1880, 1, 5, 10000, 1), // Steel               1%
			new L2DropData(1876, 1, 5, 10000, 1), // Mithril Ore         1%
			new L2DropData(1864, 1, 20, 25000, 1), // Stem               2.5%
			new L2DropData(1865, 1, 20, 25000, 1), // Varnish            2.5%
			new L2DropData(1868, 1, 15, 25000, 1), // Thread             2.5%
			new L2DropData(1869, 1, 15, 25000, 1), // Iron Ore           2.5%
			new L2DropData(1870, 1, 15, 25000, 1), // Coal               2.5%
			new L2DropData(1871, 1, 15, 25000, 1), // Charcoal           2.5%
			new L2DropData(1872, 1, 20, 30000, 1), // Animal Bone        3%
			new L2DropData(1867, 1, 20, 33333, 1), // Animal Skin        3.3333%
	};

	protected static final L2DropData[] _dropacc = new L2DropData[] {
	// Аксессуары и сувениры
			new L2DropData(8660, 1, 1, 1000, 1), // Demon Horns        0.1%
			new L2DropData(8661, 1, 1, 1000, 1), // Mask of Spirits    0.1%
			new L2DropData(4393, 1, 1, 300, 1), // Calculator          0.03%
			new L2DropData(5590, 1, 1, 200, 1), // Squeaking Shoes     0.02%
			new L2DropData(7058, 1, 1, 50, 1), // Chrono Darbuka       0.005%
			new L2DropData(8350, 1, 1, 50, 1), // Chrono Maracas       0.005%
			new L2DropData(5133, 1, 1, 50, 1), // Chrono Unitus        0.005%
			new L2DropData(5817, 1, 1, 50, 1), // Chrono Campana       0.005%
			new L2DropData(9140, 1, 1, 30, 1), // Salvation Bow        0.003%
			// Призрачные аксессуары - шанс 0.01%
			new L2DropData(9177, 1, 1, 100, 1), // Teddy Bear Hat - Blessed Resurrection Effect
			new L2DropData(9178, 1, 1, 100, 1), // Piggy Hat - Blessed Resurrection Effect
			new L2DropData(9179, 1, 1, 100, 1), // Jester Hat - Blessed Resurrection Effect
			new L2DropData(9180, 1, 1, 100, 1), // Wizard's Hat - Blessed Resurrection Effect
			new L2DropData(9181, 1, 1, 100, 1), // Dapper Cap - Blessed Resurrection Effect
			new L2DropData(9182, 1, 1, 100, 1), // Romantic Chapeau - Blessed Resurrection Effect
			new L2DropData(9183, 1, 1, 100, 1), // Iron Circlet - Blessed Resurrection Effect
			new L2DropData(9184, 1, 1, 100, 1), // Teddy Bear Hat - Blessed Escape Effect
			new L2DropData(9185, 1, 1, 100, 1), // Piggy Hat - Blessed Escape Effect
			new L2DropData(9186, 1, 1, 100, 1), // Jester Hat - Blessed Escape Effect
			new L2DropData(9187, 1, 1, 100, 1), // Wizard's Hat - Blessed Escape Effect
			new L2DropData(9188, 1, 1, 100, 1), // Dapper Cap - Blessed Escape Effect
			new L2DropData(9189, 1, 1, 100, 1), // Romantic Chapeau - Blessed Escape Effect
			new L2DropData(9190, 1, 1, 100, 1), // Iron Circlet - Blessed Escape Effect
			new L2DropData(9191, 1, 1, 100, 1), // Teddy Bear Hat - Big Head
			new L2DropData(9192, 1, 1, 100, 1), // Piggy Hat - Big Head
			new L2DropData(9193, 1, 1, 100, 1), // Jester Hat - Big Head
			new L2DropData(9194, 1, 1, 100, 1), // Wizard Hat - Big Head
			new L2DropData(9195, 1, 1, 100, 1), // Dapper Hat - Big Head
			new L2DropData(9196, 1, 1, 100, 1), // Romantic Chapeau - Big Head
			new L2DropData(9197, 1, 1, 100, 1), // Iron Circlet - Big Head
			new L2DropData(9198, 1, 1, 100, 1), // Teddy Bear Hat - Firework
			new L2DropData(9199, 1, 1, 100, 1), // Piggy Hat - Firework
			new L2DropData(9200, 1, 1, 100, 1), // Jester Hat - Firework
			new L2DropData(9201, 1, 1, 100, 1), // Wizard's Hat - Firework
			new L2DropData(9202, 1, 1, 100, 1), // Dapper Hat - Firework
			new L2DropData(9203, 1, 1, 100, 1), // Romantic Chapeau - Firework
			new L2DropData(9204, 1, 1, 100, 1) // Iron Circlet - Firework
	};

	protected static final L2DropData[] _dropevents = new L2DropData[] {
	// Эвентовые скролы
			new L2DropData(9146, 1, 1, 3000, 1), // Scroll of Guidance        0.3%
			new L2DropData(9147, 1, 1, 3000, 1), // Scroll of Death Whisper   0.3%
			new L2DropData(9148, 1, 1, 3000, 1), // Scroll of Focus           0.3%
			new L2DropData(9149, 1, 1, 3000, 1), // Scroll of Acumen          0.3%
			new L2DropData(9150, 1, 1, 3000, 1), // Scroll of Haste           0.3%
			new L2DropData(9151, 1, 1, 3000, 1), // Scroll of Agility         0.3%
			new L2DropData(9152, 1, 1, 3000, 1), // Scroll of Empower         0.3%
			new L2DropData(9153, 1, 1, 3000, 1), // Scroll of Might           0.3%
			new L2DropData(9154, 1, 1, 3000, 1), // Scroll of Wind Walk       0.3%
			new L2DropData(9155, 1, 1, 3000, 1), // Scroll of Shield          0.3%
			new L2DropData(9156, 1, 1, 2000, 1), // BSoE                      0.2%
			new L2DropData(9157, 1, 1, 1000, 1), // BRES                      0.1%

			// Хлам
			new L2DropData(5234, 1, 5, 25000, 1), // Mystery Potion           2.5%
			new L2DropData(7609, 50, 100, 24000, 1), // Proof of Catching a Fish 1.2%
			new L2DropData(7562, 2, 4, 10000, 1), // Dimensional Diamond       0.1%
			new L2DropData(6415, 1, 3, 20000, 1), // Ugly Green Fish :)        0.1%
			new L2DropData(1461, 1, 3, 15000, 1), // Crystal: A-Grade          0.5%
			new L2DropData(6406, 1, 3, 20000, 1), // Firework                 1%
			new L2DropData(6407, 1, 1, 20000, 1), // Large Firework           1%
			new L2DropData(6403, 1, 5, 20000, 1), // Star Shard               1%
			new L2DropData(6036, 1, 5, 30000, 1), // GMHP                     1%
			new L2DropData(5595, 1, 1, 15000, 1), // SP Scroll: High Grade    1%
			new L2DropData(9898, 1, 1, 6000, 1), // SP Scroll: Highest Grade 0.3%
			new L2DropData(1374, 1, 5, 20000, 1), // GHP                      1%
			new L2DropData(1375, 1, 5, 20000, 1), // GSAP                     1%
			new L2DropData(1540, 1, 3, 20000, 1), // Quick Healing Potion     1%
			new L2DropData(5126, 1, 1, 1000, 1) // Dualsword Craft Stamp      0.1%
	};

	protected static final L2DropData[] _dropench = new L2DropData[] {
	// Заточки
			new L2DropData(955, 1, 1, 400, 1), // EWD          0.04%
			new L2DropData(956, 1, 1, 2000, 1), // EAD         0.2%
			new L2DropData(951, 1, 1, 300, 1), // EWC          0.03%
			new L2DropData(952, 1, 1, 1500, 1), // EAC         0.15%
			new L2DropData(947, 1, 1, 200, 1), // EWB          0.02%
			new L2DropData(948, 1, 1, 1000, 1), // EAB         0.1%
			new L2DropData(729, 1, 1, 100, 1), // EWA          0.01%
			new L2DropData(730, 1, 1, 500, 1), // EAA          0.05%
			new L2DropData(959, 1, 1, 50, 1), // EWS           0.005%
			new L2DropData(960, 1, 1, 300, 1), // EAS          0.03%

			// Soul Cry 11-14 lvl
			new L2DropData(5577, 1, 1, 90, 1), // Red 11       0.009%
			new L2DropData(5578, 1, 1, 90, 1), // Green 11     0.009%
			new L2DropData(5579, 1, 1, 90, 1), // Blue 11      0.009%
			new L2DropData(5580, 1, 1, 70, 1), // Red 12       0.007%
			new L2DropData(5581, 1, 1, 70, 1), // Green 12     0.007%
			new L2DropData(5582, 1, 1, 70, 1), // Blue 12      0.007%
			new L2DropData(5908, 1, 1, 50, 1), // Red 13       0.005%
			new L2DropData(5911, 1, 1, 50, 1), // Green 13     0.005%
			new L2DropData(5914, 1, 1, 50, 1), // Blue 13      0.005%
			new L2DropData(9570, 1, 1, 30, 1), // Red 14       0.003%
			new L2DropData(9571, 1, 1, 30, 1), // Green 14     0.003%
			new L2DropData(9572, 1, 1, 30, 1), // Blue 14      0.003%
	};

	public synchronized void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(!playable.isPlayer())
			return;
		L2Player activeChar = playable.getPlayer();

		if(!activeChar.isQuestContinuationPossible(true))
			return;

		HashMap<Integer, Long> items = new HashMap<Integer, Long>();
		long count = 0;
		do
		{
			count++;
			activeChar.getInventory().destroyItem(item, 1, true);
			getGroupItem(activeChar, _dropmats, items);
			getGroupItem(activeChar, _dropacc, items);
			getGroupItem(activeChar, _dropevents, items);
			getGroupItem(activeChar, _dropench, items);
		} while(ctrl && item.getCount() > count && activeChar.isQuestContinuationPossible(false));

		activeChar.sendPacket(SystemMessage.removeItems(item.getItemId(), count));
		for(Entry<Integer, Long> e : items.entrySet())
			activeChar.sendPacket(SystemMessage.obtainItems(e.getKey(), e.getValue(), 0));
	}

	/*
	* Выбирает 1 предмет из группы
	*/
	public void getGroupItem(L2Player activeChar, L2DropData[] dropData, HashMap<Integer, Long> report)
	{
		L2ItemInstance item;
		long count = 0;
		for(L2DropData d : dropData)
			if(Rnd.get(1, L2Drop.MAX_CHANCE) <= d.getChance() * ConfigValue.CofferOfShadowsRewardRate)
			{
				count = Rnd.get(d.getMinDrop(), d.getMaxDrop());
				item = ItemTemplates.getInstance().createItem(d.getItemId());
				item.setCount(count);
				activeChar.getInventory().addItem(item);
				Long old = report.get(d.getItemId());
				report.put(d.getItemId(), old != null ? old + count : count);
			}
	}

	public final int[] getItemIds()
	{
		return _itemIds;
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