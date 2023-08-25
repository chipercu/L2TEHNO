import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExChangeNicknameNColor;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.ShowXMasSeal;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.RateService;
import l2open.util.Rnd;
import l2open.util.Util;
import org.apache.commons.lang3.ArrayUtils;

public class ItemHandlers extends Functions
{
	public static void addItem(L2Playable playable, int item_id, long count, int enchant)
	{
		if(playable == null || count < 1)
			return;

		L2Playable player;
		if(playable.isSummon())
			player = playable.getPlayer();
		else
			player = playable;

		L2ItemInstance item = ItemTemplates.getInstance().createItem(item_id);
		if(item.isStackable())
		{
			item.setCount(count);
			player.getInventory().addItem(item);
		}
		else
		{
			item.setEnchantLevel(enchant);
			player.getInventory().addItem(item);
			for(int i = 1; i < count; i++)
			{
				item = ItemTemplates.getInstance().createItem(item_id);
				item.setEnchantLevel(enchant);
				player.getInventory().addItem(item);
			}
		}

		player.sendPacket(SystemMessage.obtainItems(item_id, count, enchant));
	}

	// Newspaper
	public void ItemHandler_19999(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		show("data/html/newspaper/00000000.htm", player);
	}
	
	public void ItemHandler_22187(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(22187, player))
			return;
		removeItem(player, 22187, 1);
		addItem(player, 22188, 3);
		addItem(player, 21595, 1);
		addItem(player, 21594, 1);
	}
	
	//Rune of EXP 30% 5-hour
	public void ItemHandler_20548(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20548, player))
			return;
		removeItem(player, 20548, 1);
		addItem(player, 20335, 1);
	}
	
	//Rune of EXP 50% 5-hour
	public void ItemHandler_20549(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20549, player))
			return;
		removeItem(player, 20549, 1);
		addItem(player, 20336, 1);
	}
	
	//Rune of EXP 30% 10-hour
	public void ItemHandler_20550(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20550, player))
			return;
		removeItem(player, 20550, 1);
		addItem(player, 20337, 1);
	}
	
	//Rune of EXP 50% 10-hour
	public void ItemHandler_20551(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20551, player))
			return;
		removeItem(player, 20551, 1);
		addItem(player, 20338, 1);
	}
	
	//Rune of EXP 30% 7-day
	public void ItemHandler_20552(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20552, player))
			return;
		removeItem(player, 20552, 1);
		addItem(player, 20339, 1);
	}
	
	//Rune of EXP 50% 7-day
	public void ItemHandler_20553(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20553, player))
			return;
		removeItem(player, 20553, 1);
		addItem(player, 20340, 1);
	}
	
	//Rune of SP 30% 5-hour
	public void ItemHandler_20554(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20554, player))
			return;
		removeItem(player, 20554, 1);
		addItem(player, 20341, 1);
	}
	
	//Rune of SP 50% 5-hour
	public void ItemHandler_20555(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20555, player))
			return;
		removeItem(player, 20555, 1);
		addItem(player, 20342, 1);
	}
	
	//Rune of SP 30% 10-hour
	public void ItemHandler_20556(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20556, player))
			return;
		removeItem(player, 20556, 1);
		addItem(player, 20343, 1);
	}
	
	//Rune of SP 50% 10-hour
	public void ItemHandler_20557(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20557, player))
			return;
		removeItem(player, 20557, 1);
		addItem(player, 20344, 1);
	}
	
	//Rune of SP 30% 7-day
	public void ItemHandler_20558(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20558, player))
			return;
		removeItem(player, 20558, 1);
		addItem(player, 20345, 1);
	}
	
	//Rune of SP 50% 7-day
	public void ItemHandler_20559(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20559, player))
			return;
		removeItem(player, 20559, 1);
		addItem(player, 20346, 1);
	}
	
	//Rune of Crystal Level 3 - 5 hour
	public void ItemHandler_20560(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20560, player))
			return;
		removeItem(player, 20560, 1);
		addItem(player, 20347, 1);
	}
	
	//Rune of Crystal Level 5 - 5 hour
	public void ItemHandler_20561(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20561, player))
			return;
		removeItem(player, 20561, 1);
		addItem(player, 20348, 1);
	}
	
	//Rune of Crystal Level 3 - 10 hour
	public void ItemHandler_20562(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20562, player))
			return;
		removeItem(player, 20562, 1);
		addItem(player, 20349, 1);
	}
	
	//Rune of Crystal Level 5 - 10 hour
	public void ItemHandler_20563(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20563, player))
			return;
		removeItem(player, 20563, 1);
		addItem(player, 20350, 1);
	}
	
	//Rune of Crystal Level 3 - 7-day
	public void ItemHandler_20564(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20564, player))
			return;
		removeItem(player, 20564, 1);
		addItem(player, 20351, 1);
	}
	
	//Rune of Crystal Level 5 - 7-day
	public void ItemHandler_20565(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20565, player))
			return;
		removeItem(player, 20565, 1);
		addItem(player, 20352, 1);
	}
	
	//Rune of Feather - 24 hour
	public void ItemHandler_20566(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20566, player))
			return;
		removeItem(player, 20566, 1);
		addItem(player, 22066, 1);
	}
	
	//Rune of Eva - 5 hour
	public void ItemHandler_20579(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20579, player))
			return;
		removeItem(player, 20579, 1);
		addItem(player, 20570, 1);
	}
	
	//Rune of Eva - 3 day
	public void ItemHandler_20580(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20580, player))
			return;
		removeItem(player, 20580, 1);
		addItem(player, 20571, 1);
	}
	
	//Rune of EXP 30% - 1 hour
	public void ItemHandler_21085(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(21085, player))
			return;
		removeItem(player, 21085, 1);
		addItem(player, 21084, 1);
	}
	
	//Rune of SP 30% - 1 hour
	public void ItemHandler_21087(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(21087, player))
			return;
		removeItem(player, 21087, 1);
		addItem(player, 21086, 1);
	}
	
	public void ItemHandler_20993(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20993, player))
			return;
		int[] list = new int[] { 6575, 6573, 6571, 6569, 6577 };
		int[] counts = new int[] { 2, 1, 1, 1, 1 };
		int[] chances = new int[] { 45, 30, 15, 9, 1 };
		removeItem(player, 20993, 1);
		extract_item_r(list, counts, chances, player);
	}	
	
	public void ItemHandler_15537(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		show("data/scripts/quests/_464_Oath/item.htm", player);
	}

	public static void extract_item(int[] list, int[] counts, L2Player player)
	{
		if(player == null)
			return;
		int index = Rnd.get(list.length);
		int id = list[index];
		int count = counts[index];
		addItem(player, id, count);
	}

	public static GArray<int[]> mass_extract_item(long source_count, int[] list, int[] counts, L2Player player)
	{
		if(player == null)
			return new GArray<int[]>(0);

		GArray<int[]> result = new GArray<int[]>((int) Math.min(list.length, source_count));

		for(int n = 1; n <= source_count; n++)
		{
			int index = Rnd.get(list.length);
			int item = list[index];
			int count = counts[index];

			int[] old = null;
			for(int[] res : result)
				if(res[0] == item)
					old = res;

			if(old == null)
				result.add(new int[] { item, count });
			else
				old[1] += count;
		}

		return result;
	}

	public static void extract_item_r(int[] list, int[] count_min, int[] count_max, int[] chances, L2Player player)
	{
		int[] counts = count_min;
		for(int i = 0; i < count_min.length; i++)
			counts[i] = Rnd.get(count_min[i], count_max[i]);
		extract_item_r(list, counts, chances, player);
	}

	public static void extract_item_r(int[] list, int[] counts, int[] chances, L2Player player)
	{
		if(list.length != chances.length || counts != null && list.length != counts.length)
			throw new IllegalArgumentException("list: "+list.length+" chances: "+chances.length+" counts: "+(counts != null ? counts.length : 0));

		if(player == null)
			return;

		int sumChance = 0;
		for(int i = 0; i < list.length; i++)
			sumChance += chances[i];

		int roll = Rnd.get(sumChance);

		for(int i = 0; i < list.length; i++)
		{
			if(roll >= chances[i])
			{
				roll -= chances[i];
				continue;
			}

			int item = list[i];
			int count = counts != null ? counts[i] : 1;

			if(item > 0)
				addItem(player, item, count);
			else
				player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
			return;
		}
	}

	public static void extract_item_r(int[] list, int[] counts, double[] chances, L2Player player)
	{
		if(player == null)
			return;

		int sum = 0;

		for(int i = 0; i < list.length; i++)
			sum += chances[i];

		int[] table = new int[sum];
		int k = 0;

		for(int i = 0; i < list.length; i++)
			for(int j = 0; j < chances[i]; j++)
			{
				table[k] = i;
				k++;
			}

		int i = table[Rnd.get(table.length)];
		int item = list[i];
		int count = counts[i];

		addItem(player, item, count);
	}

	public static GArray<int[]> mass_extract_item_r(long source_count, int[] list, int[] count_min, int[] count_max, int[] chances, L2Player player)
	{
		int[] counts = count_min;
		for(int i = 0; i < count_min.length; i++)
			counts[i] = Rnd.get(count_min[i], count_max[i]);
		return mass_extract_item_r(source_count, list, counts, chances, player);
	}

	public static GArray<int[]> mass_extract_item_r(long source_count, int[] list, int[] counts, int[] chances, L2Player player)
	{
		if(player == null)
			return new GArray<int[]>(0);

		GArray<int[]> result = new GArray<int[]>((int) Math.min(list.length, source_count));

		int sum = 0;
		for(int i = 0; i < list.length; i++)
			sum += chances[i];

		int[] table = new int[sum];
		int k = 0;

		for(int i = 0; i < list.length; i++)
			for(int j = 0; j < chances[i]; j++)
			{
				table[k] = i;
				k++;
			}

		for(int n = 1; n <= source_count; n++)
		{
			int i = table[Rnd.get(table.length)];
			int item = list[i];
			int count = counts[i];

			int[] old = null;
			for(int[] res : result)
				if(res[0] == item)
					old = res;

			if(old == null)
				result.add(new int[] { item, count });
			else
				old[1] += count;
		}

		return result;
	}

	public static boolean canBeExtracted(int itemId, L2Player player)
	{
		if(player == null)
			return false;
		if(player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10)
		{
			player.sendPacket(Msg.YOUR_INVENTORY_IS_FULL, new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return false;
		}
		return true;
	}
	
	private static boolean extractRandomOneItem(L2Player player, int[][] items, double[] chances)
	{
		if(items.length != chances.length)
			return false;

		double extractChance = 0;
		for(double c : chances)
			extractChance += c;

		if(Rnd.chance(extractChance))
		{
			int[] successfulItems = new int[0];
			while(successfulItems.length == 0)
				for(int i = 0; i < items.length; i++)
					if(Rnd.chance(chances[i]))
						successfulItems = ArrayUtils.add(successfulItems, i);
			int[] item = items[successfulItems[Rnd.get(successfulItems.length)]];
			if(item.length < 2)
				return false;

			Functions.addItem(player, item[0], item[1]);
		}
		return true;
	}

	public static long rollDrop(long min, long max, double chance, boolean paramBoolean, L2Player player)
	{
		if(chance <= 0 || min <= 0 || max <= 0)
			return 0;
		int i = 1;
		if(paramBoolean)
			chance *= RateService.getRateDropItems(player);
		if(chance > 1000000)
			if (chance % 1000000 == 0)
			{
				i = (int)(chance / 1000000);
			}
			else
			{
				i = (int)Math.ceil(chance / 1000000);
				chance /= i;
			}
		return (Rnd.chance(chance / 10000) ? Rnd.get(min * i, max * i) : 0);
	}

	public void ItemHandler_5555(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		player.sendPacket(new ShowXMasSeal(5555));
	}

	public void ItemHandler_8547(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(8547, player))
			return;
		removeItem(player, 8547, 1);
		if(Rnd.chance(85))
			addItem(player, 8034, 3);
	}
	
	public void ItemHandler_8058(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(8058, player))
			return;
		removeItem(player, 8058, 1);
		addItem(player, 8059, 1);
	}

	// ------ Adventurer's Boxes ------

	// Adventurer's Box: C-Grade Accessory (Low Grade)
	public void ItemHandler_8534(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(8534, player))
			return;
		int[] list = new int[] { 853, 916, 884 };
		int[] chances = new int[] { 17, 17, 17 };
		int[] counts = new int[] { 1, 1, 1 };
		removeItem(player, 8534, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Adventurer's Box: C-Grade Accessory (Medium Grade)
	public void ItemHandler_8535(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(8535, player))
			return;
		int[] list = new int[] { 854, 917, 885 };
		int[] chances = new int[] { 17, 17, 17 };
		int[] counts = new int[] { 1, 1, 1 };
		removeItem(player, 8535, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Adventurer's Box: C-Grade Accessory (High Grade)
	public void ItemHandler_8536(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(8536, player))
			return;
		int[] list = new int[] { 855, 119, 886 };
		int[] chances = new int[] { 17, 17, 17 };
		int[] counts = new int[] { 1, 1, 1 };
		removeItem(player, 8536, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Adventurer's Box: B-Grade Accessory (Low Grade)
	public void ItemHandler_8537(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(8537, player))
			return;
		int[] list = new int[] { 856, 918, 887 };
		int[] chances = new int[] { 17, 17, 17 };
		int[] counts = new int[] { 1, 1, 1 };
		removeItem(player, 8537, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Adventurer's Box: B-Grade Accessory (High Grade)
	public void ItemHandler_8538(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(8538, player))
			return;
		int[] list = new int[] { 864, 926, 895 };
		int[] chances = new int[] { 17, 17, 17 };
		int[] counts = new int[] { 1, 1, 1 };
		removeItem(player, 8538, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Adventurer's Box: Hair Accessory
	public void ItemHandler_8539(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(8539, player))
			return;
		int[] list = new int[] { 8179, 8178, 8177 };
		int[] chances = new int[] { 10, 20, 30 };
		int[] counts = new int[] { 1, 1, 1 };
		removeItem(player, 8539, 1);
		extract_item_r(list, counts, chances, player);
	}
	
	public void ItemHandler_15716(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(15716, player))
			return;
		int[] list = new int[] { 8619, 8620, 15715 };
		int[] chances = new int[] { 40, 40, 20 };
		int[] counts = new int[] { 1, 1, 1 };
		removeItem(player, 15716, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Adventurer's Box: Cradle of Creation
	public void ItemHandler_8540(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(8540, player))
			return;
		removeItem(player, 8540, 1);
		if(Rnd.chance(30))
			addItem(player, 8175, 1);
	}

	// Quest 370: A Wiseman Sows Seeds
	public void ItemHandler_5916(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(5916, player))
			return;
		int[] list = new int[] { 5917, 5918, 5919, 5920, 736 };
		int[] counts = new int[] { 1, 1, 1, 1, 1 };
		removeItem(player, 5916, 1);
		extract_item(list, counts, player);
	}

	// Quest 376: Giants Cave Exploration, Part 1
	public void ItemHandler_5944(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(5944, player))
			return;
		int[] list = { 5922, 5923, 5924, 5925, 5926, 5927, 5928, 5929, 5930, 5931, 5932, 5933, 5934, 5935, 5936, 5937,
				5938, 5939, 5940, 5941, 5942, 5943 };
		int[] counts = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

		if(ctrl)
		{
			long item_count = removeItem(player, 5944, Long.MAX_VALUE);
			for(int[] res : mass_extract_item(item_count, list, counts, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 5944, 1);
			extract_item(list, counts, player);
		}
	}

	// Quest 376: Giants Cave Exploration, Part 1
	public void ItemHandler_14841(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14841, player))
			return;

		int[] list = { 14836, 14837, 14838, 14839, 14840 };
		int[] counts = { 1, 1, 1, 1, 1 };

		if(ctrl)
		{
			long item_count = removeItem(player, 14841, Long.MAX_VALUE);
			for(int[] res : mass_extract_item(item_count, list, counts, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 14841, 1);
			extract_item(list, counts, player);
		}
	}

	// Quest 377: Giants Cave Exploration, Part 2, old
	public void ItemHandler_5955(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(5955, player))
			return;
		int[] list = { 5942, 5943, 5945, 5946, 5947, 5948, 5949, 5950, 5951, 5952, 5953, 5954 };
		int[] counts = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

		if(ctrl)
		{
			long item_count = removeItem(player, 5955, Long.MAX_VALUE);
			for(int[] res : mass_extract_item(item_count, list, counts, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 5955, 1);
			extract_item(list, counts, player);
		}
	}

	// Quest 377: Giants Cave Exploration, Part 2, new
	public void ItemHandler_14847(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14847, player))
			return;
		int[] list = { 14842, 14843, 14844, 14845, 14846 };
		int[] counts = { 1, 1, 1, 1, 1 };

		if(ctrl)
		{
			long item_count = removeItem(player, 14847, Long.MAX_VALUE);
			for(int[] res : mass_extract_item(item_count, list, counts, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 14847, 1);
			extract_item(list, counts, player);
		}
	}

	// Quest 372: Legacy of Insolence
	public void ItemHandler_5966(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(5966, player))
			return;
		int[] list = new int[] { 5970, 5971, 5977, 5978, 5979, 5986, 5993, 5994, 5995, 5997, 5983, 6001 };
		int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		removeItem(player, 5966, 1);
		extract_item(list, counts, player);
	}

	// Quest 372: Legacy of Insolence
	public void ItemHandler_5967(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(5967, player))
			return;
		int[] list = new int[] { 5970, 5971, 5975, 5976, 5980, 5985, 5993, 5994, 5995, 5997, 5983, 6001 };
		int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		removeItem(player, 5967, 1);
		extract_item(list, counts, player);
	}

	// Quest 372: Legacy of Insolence
	public void ItemHandler_5968(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(5968, player))
			return;
		int[] list = new int[] { 5973, 5974, 5981, 5984, 5989, 5990, 5991, 5992, 5996, 5998, 5999, 6000, 5988, 5983, 6001 };
		int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		removeItem(player, 5968, 1);
		extract_item(list, counts, player);
	}

	// Quest 372: Legacy of Insolence
	public void ItemHandler_5969(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(5969, player))
			return;
		int[] list = new int[] { 5970, 5971, 5982, 5987, 5989, 5990, 5991, 5992, 5996, 5998, 5999, 6000, 5972, 6001 };
		int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		removeItem(player, 5969, 1);
		extract_item(list, counts, player);
	}

	/**
	 * Quest 373: Supplier of Reagents, from Hallate's Maid, Reagent Pouch (Gray)
	 * 2x Quicksilver (6019) 30%
	 * 2x Moonstone Shard (6013) 30%
	 * 1x Rotten Bone Piece (6014) 20%
	 * 1x Infernium Ore (6016) 20%
	 */
	public void ItemHandler_6007(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(6007, player))
			return;

		int[] list = new int[] { 6019, 6013, 6014, 6016 };
		int[] counts = new int[] { 2, 2, 1, 1 };
		int[] chances = new int[] { 30, 30, 20, 20 };

		if(ctrl)
		{
			long item_count = player.getInventory().getCountOf(6007);
			removeItem(player, 6007, item_count);
			for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 6007, 1);
			extract_item_r(list, counts, chances, player);
		}
	}

	/**
	 * Quest 373: Supplier of Reagents, from Platinum Tribe Shaman, Reagent Pouch (Yellow)
	 * 2x Blood Root (6017) 10%
	 * 2x Sulfur (6020) 20%
	 * 1x Rotten Bone Piece (6014) 35%
	 * 1x Infernium Ore (6016) 35%
	 */
	public void ItemHandler_6008(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(6008, player))
			return;

		int[] list = new int[] { 6017, 6020, 6014, 6016 };
		int[] counts = new int[] { 2, 2, 1, 1 };
		int[] chances = new int[] { 10, 20, 35, 35 };

		if(ctrl)
		{
			long item_count = player.getInventory().getCountOf(6008);
			removeItem(player, 6008, item_count);
			for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 6008, 1);
			extract_item_r(list, counts, chances, player);
		}
	}

	/**
	 * Quest 373: Supplier of Reagents, from Hames Orc Shaman, Reagent Pouch (Brown)
	 * 1x Lava Stone (6012) 20%
	 * 2x Volcanic Ash (6018) 20%
	 * 2x Quicksilver (6019) 20%
	 * 1x Moonstone Shard (6013) 40%
	 */
	public void ItemHandler_6009(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(6009, player))
			return;

		int[] list = new int[] { 6012, 6018, 6019, 6013 };
		int[] counts = new int[] { 1, 2, 2, 1 };
		int[] chances = new int[] { 20, 20, 20, 40 };

		if(ctrl)
		{
			long item_count = player.getInventory().getCountOf(6009);
			removeItem(player, 6009, item_count);
			for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 6009, 1);
			extract_item_r(list, counts, chances, player);
		}
	}

	/**
	 * Quest 373: Supplier of Reagents, from Platinum Guardian Shaman, Reagent Box
	 * 2x Blood Root (6017) 20%
	 * 2x Sulfur (6020) 20%
	 * 1x Infernium Ore (6016) 35%
	 * 2x Demon's Blood (6015) 25%
	 */
	public void ItemHandler_6010(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(6010, player))
			return;

		int[] list = new int[] { 6017, 6020, 6016, 6015 };
		int[] counts = new int[] { 2, 2, 1, 2 };
		int[] chances = new int[] { 20, 20, 35, 25 };

		if(ctrl)
		{
			long item_count = player.getInventory().getCountOf(6010);
			removeItem(player, 6010, item_count);
			for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 6010, 1);
			extract_item_r(list, counts, chances, player);
		}
	}

	// Quest 628: Hunt of Golden Ram
	public void ItemHandler_7725(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(7725, player))
			return;

		int[] list = new int[] { 6035, 1060, 735, 1540, 1061, 1539 };
		int[] counts = new int[] { 1, 1, 1, 1, 1, 1 };
		int[] chances = new int[] { 7, 39, 7, 3, 12, 32 };

		if(ctrl)
		{
			long item_count = player.getInventory().getCountOf(7725);
			removeItem(player, 7725, item_count);
			for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 7725, 1);
			extract_item_r(list, counts, chances, player);
		}
	}

	// Quest 628: Hunt of Golden Ram
	public void ItemHandler_7637(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(7637, player))
			return;

		int[] list = new int[] { 4039, 4041, 4043, 4044, 4042, 4040 };
		int[] counts = new int[] { 4, 1, 4, 4, 2, 2 };
		int[] chances = new int[] { 20, 10, 20, 20, 15, 15 };

		if(ctrl)
		{
			long item_count = player.getInventory().getCountOf(7637);
			removeItem(player, 7637, item_count);
			for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 7637, 1);
			extract_item_r(list, counts, chances, player);
		}
	}

	// Quest 628: Hunt of Golden Ram
	public void ItemHandler_7636(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(7636, player))
			return;

		int[] list = new int[] { 1875, 1882, 1880, 1874, 1877, 1881, 1879, 1876 };
		int[] counts = new int[] { 3, 3, 4, 1, 3, 1, 3, 6 };
		int[] chances = new int[] { 10, 20, 10, 10, 10, 12, 12, 16 };

		if(ctrl)
		{
			long item_count = player.getInventory().getCountOf(7636);
			removeItem(player, 7636, item_count);
			for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 7636, 1);
			extract_item_r(list, counts, chances, player);
		}
	}

	// Looted Goods - White Cargo box
	public void ItemHandler_7629(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(7629, player))
			return;
		int[] list = new int[] { 6688, 6689, 6690, 6691, 6693, 6694, 6695, 6696, 6697, 7579, 57 };
		int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 330000 };
		int[] chances = new int[] { 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10 };
		removeItem(player, 7629, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Looted Goods - Blue Cargo box #All chances of 8 should be 8.5, must be fixed if possible!!
	public void ItemHandler_7630(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(7630, player))
			return;
		int[] list = new int[] { 6703, 6704, 6705, 6706, 6708, 6709, 6710, 6712, 6713, 6714, 57 };
		int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 292000 };
		int[] chances = new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 20 };
		removeItem(player, 7630, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Looted Goods - Yellow Cargo box
	public void ItemHandler_7631(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(7631, player))
			return;
		int[] list = new int[] { 6701, 6702, 6707, 6711, 57 };
		int[] counts = new int[] { 1, 1, 1, 1, 930000 };
		int[] chances = new int[] { 20, 20, 20, 20, 20 };
		removeItem(player, 7631, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Looted Goods - Red Filing Cabinet
	public void ItemHandler_7632(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(7632, player))
			return;

		int[] list;
		if(ConfigValue.Alt100PercentRecipesS)
			list = new int[] { 6858, 6860, 6862, 6864, 6868, 6870, 6872, 6876, 6878, 6880, 13101, 57 };
		else
			list = new int[] { 6857, 6859, 6861, 6863, 6867, 6869, 6871, 6875, 6877, 6879, 13100, 57 };
		int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 340000 };
		int[] chances = new int[] { 8, 9, 8, 9, 8, 9, 8, 9, 8, 9, 8, 7 };
		removeItem(player, 7632, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Looted Goods - Purple Filing Cabinet
	public void ItemHandler_7633(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(7633, player))
			return;
		int[] list;
		if(ConfigValue.Alt100PercentRecipesS)
			list = new int[] { 6854, 6856, 6866, 6874, 57 };
		else
			list = new int[] { 6853, 6855, 6865, 6873, 57 };
		int[] counts = new int[] { 1, 1, 1, 1, 850000 };
		int[] chances = new int[] { 20, 20, 20, 20, 20 };
		removeItem(player, 7633, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Looted Goods - Brown Pouch
	public void ItemHandler_7634(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(7634, player))
			return;
		int[] list = new int[] { 1874, 1875, 1876, 1877, 1879, 1880, 1881, 1882, 57 };
		int[] counts = new int[] { 20, 20, 20, 20, 20, 20, 20, 20, 150000 };
		int[] chances = new int[] { 10, 10, 16, 11, 10, 5, 10, 18, 10 };
		removeItem(player, 7634, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Looted Goods - Gray Pouch
	public void ItemHandler_7635(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(7635, player))
			return;
		int[] list = new int[] { 4039, 4040, 4041, 4042, 4043, 4044, 57 };
		int[] counts = new int[] { 4, 4, 4, 4, 4, 4, 160000 };
		int[] chances = new int[] { 20, 10, 10, 10, 20, 20, 10 };
		removeItem(player, 7635, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Old Agathion
	public void ItemHandler_10408(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(10408, player))
			return;
		removeItem(player, 10408, 1);
		addItem(player, 6471, 20);
		addItem(player, 5094, 40);
		addItem(player, 9814, 3);
		addItem(player, 9816, 4);
		addItem(player, 9817, 4);
		addItem(player, 9815, 2);
		addItem(player, 57, 6000000);
	}

	// Magic Armor Set
	public void ItemHandler_10473(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(10473, player))
			return;
		removeItem(player, 10473, 1);
		addItem(player, 10470, 2); // Shadow Item - Red Crescent
		addItem(player, 10471, 2); // Shadow Item - Ring of Devotion
		addItem(player, 10472, 1); // Shadow Item - Necklace of Devotion
	}

	private final int[] sweet_list = {
	// Sweet Fruit Cocktail 
			2404, // Might
			2405, // Shield
			2406, // Wind Walk
			2407, // Focus
			2408, // Death Whisper
			2409, // Guidance
			2410, // Bless Shield
			2411, // Bless Body
			2412, // Haste
			2413, // Vampiric Rage
	};

	// Sweet Fruit Cocktail 
	public void ItemHandler_10178(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(player.isInZone(ZoneType.OlympiadStadia))
			return;
		removeItem(player, 10178, 1);
		for(int skill : sweet_list)
		{
			player.broadcastSkill(new MagicSkillUse(player, player, skill, 1, 0, 0), true);
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
		}
	}

	private final int[] fresh_list = {
	// Fresh Fruit Cocktail 
			2414, // Berserker Spirit
			2411, // Bless Body
			2415, // Magic Barrier
			2405, // Shield
			2406, // Wind Walk
			2416, // Bless Soul
			2417, // Empower
			2418, // Acumen
			2419, // Clarity
	};

	// Fresh Fruit Cocktail 
	public void ItemHandler_10179(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(player.isInZone(ZoneType.OlympiadStadia))
			return;
		removeItem(player, 10179, 1);
		for(int skill : fresh_list)
		{
			player.broadcastSkill(new MagicSkillUse(player, player, skill, 1, 0, 0), true);
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
		}
	}

	// Battleground Spell - Shield Master
	public void ItemHandler_10143(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!player.isInZone(ZoneType.Siege))
			return;
		removeItem(player, 10143, 1);
		for(int skill : new int[] { 2379, 2380, 2381, 2382, 2383 })
		{
			player.broadcastSkill(new MagicSkillUse(player, player, skill, 1, 0, 0), true);
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
		}
	}

	// Battleground Spell - Wizard
	public void ItemHandler_10144(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!player.isInZone(ZoneType.Siege))
			return;
		removeItem(player, 10144, 1);
		for(int skill : new int[] { 2379, 2380, 2381, 2384, 2385 })
		{
			player.broadcastSkill(new MagicSkillUse(player, player, skill, 1, 0, 0), true);
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
		}
	}

	// Battleground Spell - Healer
	public void ItemHandler_10145(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!player.isInZone(ZoneType.Siege))
			return;
		removeItem(player, 10145, 1);
		for(int skill : new int[] { 2379, 2380, 2381, 2384, 2386 })
		{
			player.broadcastSkill(new MagicSkillUse(player, player, skill, 1, 0, 0), true);
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
		}
	}

	// Battleground Spell - Dagger Master
	public void ItemHandler_10146(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!player.isInZone(ZoneType.Siege))
			return;
		removeItem(player, 10146, 1);
		for(int skill : new int[] { 2379, 2380, 2381, 2388, 2383 })
		{
			player.broadcastSkill(new MagicSkillUse(player, player, skill, 1, 0, 0), true);
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
		}
	}

	// Battleground Spell - Bow Master
	public void ItemHandler_10147(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!player.isInZone(ZoneType.Siege))
			return;
		removeItem(player, 10147, 1);
		for(int skill : new int[] { 2379, 2380, 2381, 2389, 2383 })
		{
			player.broadcastSkill(new MagicSkillUse(player, player, skill, 1, 0, 0), true);
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
		}
	}

	// Battleground Spell - Berserker
	public void ItemHandler_10148(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!player.isInZone(ZoneType.Siege))
			return;
		removeItem(player, 10148, 1);
		for(int skill : new int[] { 2390, 2391 })
		{
			player.broadcastSkill(new MagicSkillUse(player, player, skill, 1, 0, 0), true);
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
		}
	}

	// Wondrous Cubic
	public void ItemHandler_10632(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(10632, player))
			return;

		long lastuse = 0;
		try
		{
			String var = player.getVar("WondrousCubic");
			if(var != null && !var.equals("null"))
				lastuse = Long.parseLong(var);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}

		long timeleft = Util.calcFixedReuse(6, 30, lastuse);

		if(timeleft > 0)
		{
			timeleft /= 1000;
			long hours = timeleft / 3600;
			long minutes = (timeleft - hours * 3600) / 60;
			long seconds = timeleft - hours * 3600 - minutes * 60;
			if(hours > 0)
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addItemName(10632).addNumber(hours).addNumber(minutes).addNumber(seconds));
			else if(minutes > 0)
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addItemName(10632).addNumber(minutes).addNumber(seconds));
			else
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addItemName(10632).addNumber(seconds));
			return;
		}

		player.setVar("WondrousCubic", String.valueOf(System.currentTimeMillis()));

		rollCubic(player);
	}

	//Wondrous Cubic - 1 time use
	public void ItemHandler_21106(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21106, player))
			return;

		removeItem(player, 21106, 1);
		rollCubic(player);
	}

	private final int[] list_cubic = {
			//
			10633, // Cube Fragment Armor - D
			10642, // Cube Fragment Weapon - D
			10634, // Cube Fragment Armor - C
			10643, // Cube Fragment Weapon - C
			10635, // Cube Fragment Armor - B
			10644, // Cube Fragment Weapon - B
			10636, // Cube Fragment Armor - A
			10645, // Cube Fragment Weapon - A
			10637, // Cube Fragment Armor - S
			10646, // Cube Fragment Weapon - S
			21096, // Shiny Cube Fragment Armor - D
			21101, // Shiny Cube Fragment Weapon - D
			21097, // Shiny Cube Fragment Armor - C
			21102, // Shiny Cube Fragment Weapon - C
			21098, // Shiny Cube Fragment Armor - B
			21103, // Shiny Cube Fragment Weapon - B
			21099, // Shiny Cube Fragment Armor - A
			21104, // Shiny Cube Fragment Weapon - A
			21100, // Shiny Cube Fragment Armor - S
			21105, // Shiny Cube Fragment Weapon - S
			21593, // Ziggo's Jewel
	};
	private final int[] chances_cubic = {
			3500,
			1500,
			2000,
			1000,
			400,
			300,
			120,
			80,
			60,
			40,
			350,
			150,
			200,
			100,
			40,
			30,
			12,
			8,
			6,
			4,
			1, };

	private void rollCubic(L2Player player)
	{
		extract_item_r(list_cubic, null, chances_cubic, player);

		if(Rnd.chance(3))
			addItem(player, 21106, 1); // Wondrous Cubic - 1 time use
	}

	private final int[] list_21107 = {
			//
			10633, // Cube Fragment Armor - D
			8728, // Life Stone -  Level 61
			8729, // Life Stone -  Level 64
			8730, // Life Stone -  Level 67
			8731, // Life Stone -  Level 70
			8732, // Life Stone -  Level 76
			8738, // Mid-Grade Life Stone -  Level 61
			8739, // Mid-Grade Life Stone -  Level 64
			8740, // Mid-Grade Life Stone -  Level 67
			8741, // Mid-Grade Life Stone -  Level 70
			8742, // Mid-Grade Life Stone -  Level 76
			8748, // High-Grade Life Stone -  Level 61
			8749, // High-Grade Life Stone -  Level 64
			8750, // High-Grade Life Stone -  Level 67
			8751, // High-Grade Life Stone -  Level 70
			8752, // High-Grade Life Stone -  Level 76
			8758, // Top-Grade Life Stone -  Level 61
			8759, // Top-Grade Life Stone -  Level 64
			8760, // Top-Grade Life Stone -  Level 67
			8761, // Top-Grade Life Stone -  Level 70
			8762, // Top-Grade Life Stone -  Level 76
			9573, // Life Stone -  Level 80
			9574, // Mid-Grade Life Stone -  Level 80
			9575, // High-Grade Life Stone -  Level 80
			9576, // Top-Grade Life Stone -  Level 80
			10483, // Life Stone -  Level 82
			10484, // Mid-Grade Life Stone -  Level 82
			10485, // High-Grade Life Stone -  Level 82
			10486, // Top-Grade Life Stone -  Level 82
			14166, // Life Stone -  Level 84
			14167, // Mid-Grade Life Stone -  Level 84
			14168, // High-Grade Life Stone -  Level 84
			14169, // Top-Grade Life Stone -  Level 84
	};

	//Refined Cube - 1 time use
	public void ItemHandler_21107(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21107, player))
			return;

		removeItem(player, 21107, 1);

		addItem(player, list_21107[Rnd.get(list_21107.length)], 1);
	}

	// Ancient Tome of the Demon
	public void ItemHandler_9599(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(9599, player))
			return;

		int[] list = new int[] { 9600, 9601, 9602 };
		int[] count_min = new int[] { 1, 1, 1 };
		int[] count_max = new int[] { 2, 2, 1 };
		int[] chances = new int[] { 4, 10, 1 };

		if(ctrl)
		{
			long item_count = player.getInventory().getCountOf(9599);
			removeItem(player, 9599, item_count);
			for(int[] res : mass_extract_item_r(item_count, list, count_min, count_max, chances, player))
				addItem(player, res[0], res[1]);
		}
		else
		{
			removeItem(player, 9599, 1);
			extract_item_r(list, count_min, count_max, chances, player);
		}
	}

	public void ItemHandler_13010(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(InstancedZoneManager.getInstance().getTimeToNextEnterInstance("Kamaloka, Hall of the Abyss", player) > 0)
		{
			removeItem(player, 13010, 1);
			player.unsetVar("Kamaloka, Hall of the Abyss");
		}
		else
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(13010));
	}

	public void ItemHandler_13011(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(InstancedZoneManager.getInstance().getTimeToNextEnterInstance("Kamaloka, Hall of the Nightmares", player) > 0)
		{
			removeItem(player, 13011, 1);
			player.unsetVar("Kamaloka, Hall of the Nightmares");
		}
		else
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(13011));
	}

	public void ItemHandler_13012(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(InstancedZoneManager.getInstance().getTimeToNextEnterInstance("Kamaloka, Labyrinth of the Abyss", player) > 0)
		{
			removeItem(player, 13012, 1);
			player.unsetVar("Kamaloka, Labyrinth of the Abyss");
		}
		else
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(13012));
	}

	// Baby Panda Agathion Pack
	public void ItemHandler_20069(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20069, player))
			return;
		removeItem(player, 20069, 1);
		addItem(player, 20063, 1);
	}

	// Bamboo Panda Agathion Pack
	public void ItemHandler_20070(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(20070, player))
			return;
		removeItem(player, 20070, 1);
		addItem(player, 20064, 1);
	}

	// Sexy Panda Agathion Pack
	public void ItemHandler_20071(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20071, player))
			return;
		removeItem(player, 20071, 1);
		addItem(player, 20065, 1);
	}

	// Agathion of Baby Panda 15 Day Pack
	public void ItemHandler_20072(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20072, player))
			return;
		removeItem(player, 20072, 1);
		addItem(player, 20066, 1);
	}

	// Bamboo Panda Agathion 15 Day Pack
	public void ItemHandler_20073(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20073, player))
			return;
		removeItem(player, 20073, 1);
		addItem(player, 20067, 1);
	}

	// Agathion of Sexy Panda 15 Day Pack
	public void ItemHandler_20074(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20074, player))
			return;
		removeItem(player, 20074, 1);
		addItem(player, 20068, 1);
	}

	// Charming Valentine Gift Set
	public void ItemHandler_20210(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20210, player))
			return;
		removeItem(player, 20210, 1);
		addItem(player, 20212, 1);
	}

	// Naughty Valentine Gift Set
	public void ItemHandler_20211(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20211, player))
			return;
		removeItem(player, 20211, 1);
		addItem(player, 20213, 1);
	}

	// White Maneki Neko Agathion Pack
	public void ItemHandler_20215(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20215, player))
			return;
		removeItem(player, 20215, 1);
		addItem(player, 20221, 1);
	}

	// Black Maneki Neko Agathion Pack
	public void ItemHandler_20216(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20216, player))
			return;
		removeItem(player, 20216, 1);
		addItem(player, 20222, 1);
	}

	// Brown Maneki Neko Agathion Pack
	public void ItemHandler_20217(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20217, player))
			return;
		removeItem(player, 20217, 1);
		addItem(player, 20223, 1);
	}

	// White Maneki Neko Agathion 7-Day Pack
	public void ItemHandler_20218(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20218, player))
			return;
		removeItem(player, 20218, 1);
		addItem(player, 20224, 1);
	}

	// Black Maneki Neko Agathion 7-Day Pack
	public void ItemHandler_20219(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20219, player))
			return;
		removeItem(player, 20219, 1);
		addItem(player, 20225, 1);
	}

	// Brown Maneki Neko Agathion 7-Day Pack
	public void ItemHandler_20220(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20220, player))
			return;
		removeItem(player, 20220, 1);
		addItem(player, 20226, 1);
	}

	// One-Eyed Bat Drove Agathion Pack
	public void ItemHandler_20227(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20227, player))
			return;
		removeItem(player, 20227, 1);
		addItem(player, 20230, 1);
	}

	// One-Eyed Bat Drove Agathion 7-Day Pack
	public void ItemHandler_20228(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20228, player))
			return;
		removeItem(player, 20228, 1);
		addItem(player, 20231, 1);
	}

	// One-Eyed Bat Drove Agathion 7-Day Pack
	public void ItemHandler_20229(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20229, player))
			return;
		removeItem(player, 20229, 1);
		addItem(player, 20232, 1);
	}

	// Pegasus Agathion Pack
	public void ItemHandler_20233(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20233, player))
			return;
		removeItem(player, 20233, 1);
		addItem(player, 20236, 1);
	}

	// Pegasus Agathion 7-Day Pack
	public void ItemHandler_20234(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20234, player))
			return;
		removeItem(player, 20234, 1);
		addItem(player, 20237, 1);
	}

	// Pegasus Agathion 7-Day Pack
	public void ItemHandler_20235(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20235, player))
			return;
		removeItem(player, 20235, 1);
		addItem(player, 20238, 1);
	}

	// Yellow-Robed Tojigong Pack
	public void ItemHandler_20239(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20239, player))
			return;
		removeItem(player, 20239, 1);
		addItem(player, 20245, 1);
	}

	// Blue-Robed Tojigong Pack
	public void ItemHandler_20240(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20240, player))
			return;
		removeItem(player, 20240, 1);
		addItem(player, 20246, 1);
	}

	// Green-Robed Tojigong Pack
	public void ItemHandler_20241(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20241, player))
			return;
		removeItem(player, 20241, 1);
		addItem(player, 20247, 1);
	}

	// Yellow-Robed Tojigong 7-Day Pack
	public void ItemHandler_20242(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20242, player))
			return;
		removeItem(player, 20242, 1);
		addItem(player, 20248, 1);
	}

	// Blue-Robed Tojigong 7-Day Pack
	public void ItemHandler_20243(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20243, player))
			return;
		removeItem(player, 20243, 1);
		addItem(player, 20249, 1);
	}

	// Green-Robed Tojigong 7-Day Pack
	public void ItemHandler_20244(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20244, player))
			return;
		removeItem(player, 20244, 1);
		addItem(player, 20250, 1);
	}

	// Bugbear Agathion Pack
	public void ItemHandler_20251(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20251, player))
			return;
		removeItem(player, 20251, 1);
		addItem(player, 20252, 1);
	}

	// Agathion of Love Pack (Event)
	public void ItemHandler_20254(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20254, player))
			return;
		removeItem(player, 20254, 1);
		addItem(player, 20253, 1);
	}

	// Gold Afro Hair Pack
	public void ItemHandler_20278(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20278, player))
			return;
		removeItem(player, 20278, 1);
		addItem(player, 20275, 1);
	}

	// Pink Afro Hair Pack
	public void ItemHandler_20279(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20279, player))
			return;
		removeItem(player, 20279, 1);
		addItem(player, 20276, 1);
	}

	// Plaipitak Agathion Pack
	public void ItemHandler_20041(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20041, player))
			return;
		removeItem(player, 20041, 1);
		addItem(player, 20012, 1);
	}

	// Plaipitak Agathion 30-Day Pack
	public void ItemHandler_20042(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20042, player))
			return;
		removeItem(player, 20042, 1);
		addItem(player, 20013, 1);
	}

	// Plaipitak Agathion 30-Day Pack
	public void ItemHandler_20043(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20043, player))
			return;
		removeItem(player, 20043, 1);
		addItem(player, 20014, 1);
	}

	// Plaipitak Agathion 30-Day Pack
	public void ItemHandler_20044(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20044, player))
			return;
		removeItem(player, 20044, 1);
		addItem(player, 20015, 1);
	}

	// Majo Agathion Pack
	public void ItemHandler_20035(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20035, player))
			return;
		removeItem(player, 20035, 1);
		addItem(player, 20006, 1);
	}

	// Gold Crown Majo Agathion Pack
	public void ItemHandler_20036(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20036, player))
			return;
		removeItem(player, 20036, 1);
		addItem(player, 20007, 1);
	}

	// Black Crown Majo Agathion Pack
	public void ItemHandler_20037(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20037, player))
			return;
		removeItem(player, 20037, 1);
		addItem(player, 20008, 1);
	}

	// Majo Agathion 30-Day Pack
	public void ItemHandler_20038(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20038, player))
			return;
		removeItem(player, 20038, 1);
		addItem(player, 20009, 1);
	}

	// Gold Crown Majo Agathion 30-Day Pack
	public void ItemHandler_20039(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20039, player))
			return;
		removeItem(player, 20039, 1);
		addItem(player, 20010, 1);
	}

	// Black Crown Majo Agathion 30-Day Pack
	public void ItemHandler_20040(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20040, player))
			return;
		removeItem(player, 20040, 1);
		addItem(player, 20011, 1);
	}

	// Kat the Cat Hat Pack
	public void ItemHandler_20060(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20060, player))
			return;
		removeItem(player, 20060, 1);
		addItem(player, 20031, 1);
	}

	// Skull Hat Pack
	public void ItemHandler_20061(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20061, player))
			return;
		removeItem(player, 20061, 1);
		addItem(player, 20032, 1);
	}

	// ****** Start Item Mall ******
	// Small fortuna box
	public void ItemHandler_22000(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(22000, player))
			return;
		int[] list = new int[] { 22006, 22007, 22022, 22023, 22024, 22025 };
		int[] counts = new int[] { 2, 2, 6, 8, 10, 12 }; //TODO: correct count
		int[] chances = new int[] { 10, 15, 20, 25, 30, 35 }; //TODO: correct chance
		removeItem(player, 22000, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Middle fortuna box
	public void ItemHandler_22001(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(22001, player))
			return;
		int[] list = new int[] { 22006, 22007, 22008, 22014, 22022, 22023, 22024, 22025 };
		int[] counts = new int[] { 2, 2, 2, 2, 6, 8, 10, 12 }; //TODO: correct count
		int[] chances = new int[] { 15, 10, 5, 3, 20, 25, 30, 35 }; //TODO: correct chance
		removeItem(player, 22001, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Large fortuna box
	public void ItemHandler_22002(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(22002, player))
			return;
		int[] list = new int[] { 22008, 22009, 22014, 22015, 22018, 22019, 22022, 22023, 22024, 22025 };
		int[] counts = new int[] { 2, 2, 2, 2, 2, 2, 6, 8, 10, 12 }; //TODO: correct count
		int[] chances = new int[] { 10, 5, 4, 3, 2, 2, 20, 25, 30, 35 }; //TODO: correct chance
		removeItem(player, 22002, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Small fortuna cube
	public void ItemHandler_22003(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(22003, player))
			return;
		int[] list = new int[] { 22010, 22011, 22012, 22022, 22023, 22024, 22025 };
		int[] counts = new int[] { 2, 2, 2, 6, 8, 10, 12 }; //TODO: correct count
		int[] chances = new int[] { 10, 5, 4, 20, 25, 30, 35 }; //TODO: correct chance
		removeItem(player, 22003, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Middle fortuna cube
	public void ItemHandler_22004(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(22004, player))
			return;
		int[] list = new int[] { 22011, 22012, 22013, 22016, 22022, 22023, 22024, 22025 };
		int[] counts = new int[] { 2, 2, 2, 2, 6, 8, 10, 12 }; //TODO: correct count
		int[] chances = new int[] { 10, 5, 4, 2, 20, 25, 30, 35 }; //TODO: correct chance
		removeItem(player, 22004, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Large fortuna cube
	public void ItemHandler_22005(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(22005, player))
			return;
		int[] list = new int[] { 22012, 22013, 22016, 22017, 22020, 22021, 22022, 22023, 22024, 22025 };
		int[] counts = new int[] { 2, 2, 2, 2, 2, 2, 6, 8, 10, 12 }; //TODO: correct count
		int[] chances = new int[] { 10, 5, 4, 3, 2, 2, 20, 25, 30, 35 }; //TODO: correct chance
		removeItem(player, 22005, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Beast Soulshot Pack
	public void ItemHandler_20326(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20326, player))
			return;
		removeItem(player, 20326, 1);
		addItem(player, 20332, 5000);
	}

	// Beast Spiritshot Pack
	public void ItemHandler_20327(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20327, player))
			return;
		removeItem(player, 20327, 1);
		addItem(player, 20333, 5000);
	}

	// Beast Soulshot Large Pack
	public void ItemHandler_20329(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20329, player))
			return;
		removeItem(player, 20329, 1);
		addItem(player, 20332, 10000);
	}

	// Beast Spiritshot Large Pack
	public void ItemHandler_20330(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20330, player))
			return;
		removeItem(player, 20330, 1);
		addItem(player, 20333, 10000);
	}

	// Light Purple Maned Horse Bracelet 30-Day Pack
	public void ItemHandler_20059(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20059, player))
			return;
		removeItem(player, 20059, 1);
		addItem(player, 20030, 1);
	}

	// Steam Beatle Mounting Bracelet 7 Day Pack
	public void ItemHandler_20494(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20494, player))
			return;
		removeItem(player, 20494, 1);
		addItem(player, 20449, 1);
	}

	// Light Purple Maned Horse Mounting Bracelet 7 Day Pack
	public void ItemHandler_20493(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20493, player))
			return;
		removeItem(player, 20493, 1);
		addItem(player, 20448, 1);
	}

	// Steam Beatle Mounting Bracelet Pack
	public void ItemHandler_20395(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20395, player))
			return;
		removeItem(player, 20395, 1);
		addItem(player, 20396, 1);
	}

	// Pumpkin Transformation Stick 7 Day Pack
	public void ItemHandler_13281(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13281, player))
			return;
		removeItem(player, 13281, 1);
		addItem(player, 13253, 1);
	}

	// Kat the Cat Hat 7-Day Pack
	public void ItemHandler_13282(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13282, player))
			return;
		removeItem(player, 13282, 1);
		addItem(player, 13239, 1);
	}

	// Feline Queen Hat 7-Day Pack
	public void ItemHandler_13283(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13283, player))
			return;
		removeItem(player, 13283, 1);
		addItem(player, 13240, 1);
	}

	// Monster Eye Hat 7-Day Pack
	public void ItemHandler_13284(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13284, player))
			return;
		removeItem(player, 13284, 1);
		addItem(player, 13241, 1);
	}

	// Brown Bear Hat 7-Day Pack
	public void ItemHandler_13285(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13285, player))
			return;
		removeItem(player, 13285, 1);
		addItem(player, 13242, 1);
	}

	// Fungus Hat 7-Day Pack
	public void ItemHandler_13286(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13286, player))
			return;
		removeItem(player, 13286, 1);
		addItem(player, 13243, 1);
	}

	// Skull Hat 7-Day Pack
	public void ItemHandler_13287(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13287, player))
			return;
		removeItem(player, 13287, 1);
		addItem(player, 13244, 1);
	}

	// Ornithomimus Hat 7-Day Pack
	public void ItemHandler_13288(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13288, player))
			return;
		removeItem(player, 13288, 1);
		addItem(player, 13245, 1);
	}

	// Feline King Hat 7-Day Pack
	public void ItemHandler_13289(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13289, player))
			return;
		removeItem(player, 13289, 1);
		addItem(player, 13246, 1);
	}

	// Kai the Cat Hat 7-Day Pack
	public void ItemHandler_13290(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13290, player))
			return;
		removeItem(player, 13290, 1);
		addItem(player, 13247, 1);
	}

	// Sudden Agathion 7 Day Pack
	public void ItemHandler_14267(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14267, player))
			return;
		removeItem(player, 14267, 1);
		addItem(player, 14093, 1);
	}

	// Shiny Agathion 7 Day Pack
	public void ItemHandler_14268(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14268, player))
			return;
		removeItem(player, 14268, 1);
		addItem(player, 14094, 1);
	}

	// Sobbing Agathion 7 Day Pack
	public void ItemHandler_14269(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14269, player))
			return;
		removeItem(player, 14269, 1);
		addItem(player, 14095, 1);
	}

	// Agathion of Love 7-Day Pack
	public void ItemHandler_13280(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13280, player))
			return;
		removeItem(player, 13280, 1);
		addItem(player, 20201, 1);
	}

	// A Scroll Bundle of Fighter
	public void ItemHandler_22087(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(22087, player))
			return;
		removeItem(player, 22087, 1);
		addItem(player, 22039, 1);
		addItem(player, 22040, 1);
		addItem(player, 22041, 1);
		addItem(player, 22042, 1);
		addItem(player, 22043, 1);
		addItem(player, 22044, 1);
		addItem(player, 22047, 1);
		addItem(player, 22048, 1);
	}

	// A Scroll Bundle of Mage
	public void ItemHandler_22088(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(22088, player))
			return;
		removeItem(player, 22088, 1);
		addItem(player, 22045, 1);
		addItem(player, 22046, 1);
		addItem(player, 22048, 1);
		addItem(player, 22049, 1);
		addItem(player, 22050, 1);
		addItem(player, 22051, 1);
		addItem(player, 22052, 1);
		addItem(player, 22053, 1);
	}

	// ****** End Item Mall ******

	// Pathfinder's Reward - D-Grade
	public void ItemHandler_13003(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13003, player))
			return;
		removeItem(player, 13003, 1);
		if(Rnd.chance(3.2))
			addItem(player, 947, 1); // Scroll: Enchant Weapon B
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Pathfinder's Reward - C-Grade
	public void ItemHandler_13004(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13004, player))
			return;
		removeItem(player, 13004, 1);
		if(Rnd.chance(1.6111))
			addItem(player, 729, 1); // Scroll: Enchant Weapon A
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Pathfinder's Reward - B-Grade
	public void ItemHandler_13005(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13005, player))
			return;
		removeItem(player, 13005, 1);
		if(Rnd.chance(1.14))
			addItem(player, 959, 1); // Scroll: Enchant Weapon S
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Pathfinder's Reward - A-Grade
	public void ItemHandler_13006(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13006, player))
			return;
		int[] items = new int[]{9546, 9548, 9550, 959, 9442, 9443, 9444, 9445, 9446, 9447, 9448, 9449, 9450, 10252, 10253, 15645, 15646, 15647};
		int[] chances = new int[]{198, 198, 198, 198, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 10};
		int[] counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		removeItem(player, 13006, 1);
		extract_item_r(items, counts, chances, player);
	}

	// Pathfinder's Reward - S-Grade
	public void ItemHandler_13007(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13007, player))
			return;
		int[] items = new int[]{9546, 9548, 9550, 959, 10215, 10216, 10217, 10218, 10219, 10220, 10221, 10222, 10223};
		int[] chances = new int[]{264, 264, 264, 384, 13, 13, 13, 13, 13, 13, 13, 13, 13};
		int[] counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		removeItem(player, 13007, 1);
		extract_item_r(items, counts, chances, player);
	}

	// Pathfinder's Reward - AU Karm
	public void ItemHandler_13270(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13270, player))
			return;
		removeItem(player, 13270, 1);
		if(Rnd.chance(50))
			addItem(player, 13236, 1);
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Pathfinder's Reward - AR Karm
	public void ItemHandler_13271(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13271, player))
			return;
		removeItem(player, 13271, 1);
		if(Rnd.chance(50))
			addItem(player, 13237, 1);
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Pathfinder's Reward - AE Karm
	public void ItemHandler_13272(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13272, player))
			return;
		removeItem(player, 13272, 1);
		if(Rnd.chance(50))
			addItem(player, 13238, 1);
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// ****** Belts ******
	// Gludio Supply Box - Belt: Grade B, C
	public void ItemHandler_13713(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13713, player))
			return;
		removeItem(player, 13713, 1);
		if(Rnd.chance(50))
			addItem(player, 13894, 1); // Cloth Belt
		if(Rnd.chance(50))
			addItem(player, 13895, 1); // Leather Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Dion Supply Box - Belt: Grade B, C
	public void ItemHandler_13714(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13714, player))
			return;
		removeItem(player, 13714, 1);
		if(Rnd.chance(50))
			addItem(player, 13894, 1); // Cloth Belt
		if(Rnd.chance(50))
			addItem(player, 13895, 1); // Leather Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Giran Supply Box - Belt: Grade B, C
	public void ItemHandler_13715(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13715, player))
			return;
		removeItem(player, 13715, 1);
		if(Rnd.chance(50))
			addItem(player, 13894, 1); // Cloth Belt
		if(Rnd.chance(50))
			addItem(player, 13895, 1); // Leather Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Oren Supply Box - Belt: Grade B, C
	public void ItemHandler_13716(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13716, player))
			return;
		removeItem(player, 13716, 1);
		if(Rnd.chance(50))
			addItem(player, 13894, 1); // Cloth Belt
		if(Rnd.chance(50))
			addItem(player, 13895, 1); // Leather Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Aden Supply Box - Belt: Grade B, C
	public void ItemHandler_13717(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13717, player))
			return;
		removeItem(player, 13717, 1);
		if(Rnd.chance(50))
			addItem(player, 13894, 1); // Cloth Belt
		if(Rnd.chance(50))
			addItem(player, 13895, 1); // Leather Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Innadril Supply Box - Belt: Grade B, C
	public void ItemHandler_13718(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13718, player))
			return;
		removeItem(player, 13718, 1);
		if(Rnd.chance(50))
			addItem(player, 13894, 1); // Cloth Belt
		if(Rnd.chance(50))
			addItem(player, 13895, 1); // Leather Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Goddard Supply Box - Belt: Grade B, C
	public void ItemHandler_13719(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13719, player))
			return;
		removeItem(player, 13719, 1);
		if(Rnd.chance(50))
			addItem(player, 13894, 1); // Cloth Belt
		if(Rnd.chance(50))
			addItem(player, 13895, 1); // Leather Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Rune Supply Box - Belt: Grade B, C
	public void ItemHandler_13720(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13720, player))
			return;
		removeItem(player, 13720, 1);
		if(Rnd.chance(50))
			addItem(player, 13894, 1); // Cloth Belt
		if(Rnd.chance(50))
			addItem(player, 13895, 1); // Leather Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Schuttgart Supply Box - Belt: Grade B, C
	public void ItemHandler_13721(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13721, player))
			return;
		removeItem(player, 13721, 1);
		if(Rnd.chance(50))
			addItem(player, 13894, 1); // Cloth Belt
		if(Rnd.chance(50))
			addItem(player, 13895, 1); // Leather Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Gludio Supply Box - Belt: Grade S, A
	public void ItemHandler_14549(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14549, player))
			return;
		removeItem(player, 14549, 1);
		if(Rnd.chance(50))
			addItem(player, 13896, 1); // Iron Belt
		if(Rnd.chance(50))
			addItem(player, 13897, 1); // Mithril Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Dion Supply Box - Belt: Grade S, A
	public void ItemHandler_14550(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14550, player))
			return;
		removeItem(player, 14550, 1);
		if(Rnd.chance(50))
			addItem(player, 13896, 1); // Iron Belt
		if(Rnd.chance(50))
			addItem(player, 13897, 1); // Mithril Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Giran Supply Box - Belt: Grade S, A
	public void ItemHandler_14551(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14551, player))
			return;
		removeItem(player, 14551, 1);
		if(Rnd.chance(50))
			addItem(player, 13896, 1); // Iron Belt
		if(Rnd.chance(50))
			addItem(player, 13897, 1); // Mithril Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Oren Supply Box - Belt: Grade S, A
	public void ItemHandler_14552(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14552, player))
			return;
		removeItem(player, 14552, 1);
		if(Rnd.chance(50))
			addItem(player, 13896, 1); // Iron Belt
		if(Rnd.chance(50))
			addItem(player, 13897, 1); // Mithril Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Aden Supply Box - Belt: Grade S, A
	public void ItemHandler_14553(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14553, player))
			return;
		removeItem(player, 14553, 1);
		if(Rnd.chance(50))
			addItem(player, 13896, 1); // Iron Belt
		if(Rnd.chance(50))
			addItem(player, 13897, 1); // Mithril Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Innadril Supply Box - Belt: Grade S, A
	public void ItemHandler_14554(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14554, player))
			return;
		removeItem(player, 14554, 1);
		if(Rnd.chance(50))
			addItem(player, 13896, 1); // Iron Belt
		if(Rnd.chance(50))
			addItem(player, 13897, 1); // Mithril Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Goddard Supply Box - Belt: Grade S, A
	public void ItemHandler_14555(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14555, player))
			return;
		removeItem(player, 14555, 1);
		if(Rnd.chance(50))
			addItem(player, 13896, 1); // Iron Belt
		if(Rnd.chance(50))
			addItem(player, 13897, 1); // Mithril Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Rune Supply Box - Belt: Grade S, A
	public void ItemHandler_14556(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14556, player))
			return;
		removeItem(player, 14556, 1);
		if(Rnd.chance(50))
			addItem(player, 13896, 1); // Iron Belt
		if(Rnd.chance(50))
			addItem(player, 13897, 1); // Mithril Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Schuttgart Supply Box - Belt: Grade S, A
	public void ItemHandler_14557(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14557, player))
			return;
		removeItem(player, 14557, 1);
		if(Rnd.chance(50))
			addItem(player, 13896, 1); // Iron Belt
		if(Rnd.chance(50))
			addItem(player, 13897, 1); // Mithril Belt
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// ****** Magic Pins ******
	// Gludio Supply Box - Magic Pin: Grade B, C
	public void ItemHandler_13695(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13695, player))
			return;
		removeItem(player, 13695, 1);
		if(Rnd.chance(50))
			addItem(player, 13898, 1); // Sealed Magic Pin (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13899, 1); // Sealed Magic Pin (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Dion Supply Box - Magic Pin: Grade B, C
	public void ItemHandler_13696(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13696, player))
			return;
		removeItem(player, 13696, 1);
		if(Rnd.chance(50))
			addItem(player, 13898, 1); // Sealed Magic Pin (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13899, 1); // Sealed Magic Pin (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Giran Supply Box - Magic Pin: Grade B, C
	public void ItemHandler_13697(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13697, player))
			return;
		removeItem(player, 13697, 1);
		if(Rnd.chance(50))
			addItem(player, 13898, 1); // Sealed Magic Pin (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13899, 1); // Sealed Magic Pin (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Oren Supply Box - Magic Pin: Grade B, C
	public void ItemHandler_13698(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13698, player))
			return;
		removeItem(player, 13698, 1);
		if(Rnd.chance(50))
			addItem(player, 13898, 1); // Sealed Magic Pin (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13899, 1); // Sealed Magic Pin (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Aden Supply Box - Magic Pin: Grade B, C
	public void ItemHandler_13699(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13699, player))
			return;
		removeItem(player, 13699, 1);
		if(Rnd.chance(50))
			addItem(player, 13898, 1); // Sealed Magic Pin (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13899, 1); // Sealed Magic Pin (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Innadril Supply Box - Magic Pin: Grade B, C
	public void ItemHandler_13700(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13700, player))
			return;
		removeItem(player, 13700, 1);
		if(Rnd.chance(50))
			addItem(player, 13898, 1); // Sealed Magic Pin (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13899, 1); // Sealed Magic Pin (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Goddard Supply Box - Magic Pin: Grade B, C
	public void ItemHandler_13701(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13701, player))
			return;
		removeItem(player, 13701, 1);
		if(Rnd.chance(50))
			addItem(player, 13898, 1); // Sealed Magic Pin (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13899, 1); // Sealed Magic Pin (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Rune Supply Box - Magic Pin: Grade B, C
	public void ItemHandler_13702(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13702, player))
			return;
		removeItem(player, 13702, 1);
		if(Rnd.chance(50))
			addItem(player, 13898, 1); // Sealed Magic Pin (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13899, 1); // Sealed Magic Pin (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Schuttgart Supply Box - Magic Pin: Grade B, C
	public void ItemHandler_13703(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13703, player))
			return;
		removeItem(player, 13703, 1);
		if(Rnd.chance(50))
			addItem(player, 13898, 1); // Sealed Magic Pin (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13899, 1); // Sealed Magic Pin (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Gludio Supply Box - Magic Pin: Grade S, A
	public void ItemHandler_14531(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14531, player))
			return;
		removeItem(player, 14531, 1);
		if(Rnd.chance(50))
			addItem(player, 13900, 1); // Sealed Magic Pin (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13901, 1); // Sealed Magic Pin (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Dion Supply Box - Magic Pin: Grade S, A
	public void ItemHandler_14532(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14532, player))
			return;
		removeItem(player, 14532, 1);
		if(Rnd.chance(50))
			addItem(player, 13900, 1); // Sealed Magic Pin (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13901, 1); // Sealed Magic Pin (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Giran Supply Box - Magic Pin: Grade S, A
	public void ItemHandler_14533(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14533, player))
			return;
		removeItem(player, 14533, 1);
		if(Rnd.chance(50))
			addItem(player, 13900, 1); // Sealed Magic Pin (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13901, 1); // Sealed Magic Pin (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Oren Supply Box - Magic Pin: Grade S, A
	public void ItemHandler_14534(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14534, player))
			return;
		removeItem(player, 14534, 1);
		if(Rnd.chance(50))
			addItem(player, 13900, 1); // Sealed Magic Pin (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13901, 1); // Sealed Magic Pin (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Aden Supply Box - Magic Pin: Grade S, A
	public void ItemHandler_14535(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14535, player))
			return;
		removeItem(player, 14535, 1);
		if(Rnd.chance(50))
			addItem(player, 13900, 1); // Sealed Magic Pin (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13901, 1); // Sealed Magic Pin (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Innadril Supply Box - Magic Pin: Grade S, A
	public void ItemHandler_14536(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14536, player))
			return;
		removeItem(player, 14536, 1);
		if(Rnd.chance(50))
			addItem(player, 13900, 1); // Sealed Magic Pin (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13901, 1); // Sealed Magic Pin (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Goddard Supply Box - Magic Pin: Grade S, A
	public void ItemHandler_14537(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14537, player))
			return;
		removeItem(player, 14537, 1);
		if(Rnd.chance(50))
			addItem(player, 13900, 1); // Sealed Magic Pin (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13901, 1); // Sealed Magic Pin (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Rune Supply Box - Magic Pin: Grade S, A
	public void ItemHandler_14538(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14538, player))
			return;
		removeItem(player, 14538, 1);
		if(Rnd.chance(50))
			addItem(player, 13900, 1); // Sealed Magic Pin (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13901, 1); // Sealed Magic Pin (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Schuttgart Supply Box - Magic Pin: Grade S, A
	public void ItemHandler_14539(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14539, player))
			return;
		removeItem(player, 14539, 1);
		if(Rnd.chance(50))
			addItem(player, 13900, 1); // Sealed Magic Pin (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13901, 1); // Sealed Magic Pin (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// ****** Magic Pouchs ******
	// Gludio Supply Box - Magic Pouch: Grade B, C
	public void ItemHandler_13704(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13704, player))
			return;
		removeItem(player, 13704, 1);
		if(Rnd.chance(50))
			addItem(player, 13918, 1); // Sealed Magic Pouch (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13919, 1); // Sealed Magic Pouch (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Dion Supply Box - Magic Pouch: Grade B, C
	public void ItemHandler_13705(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13705, player))
			return;
		removeItem(player, 13705, 1);
		if(Rnd.chance(50))
			addItem(player, 13918, 1); // Sealed Magic Pouch (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13919, 1); // Sealed Magic Pouch (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Giran Supply Box - Magic Pouch: Grade B, C
	public void ItemHandler_13706(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13706, player))
			return;
		removeItem(player, 13706, 1);
		if(Rnd.chance(50))
			addItem(player, 13918, 1); // Sealed Magic Pouch (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13919, 1); // Sealed Magic Pouch (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Oren Supply Box - Magic Pouch: Grade B, C
	public void ItemHandler_13707(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13707, player))
			return;
		removeItem(player, 13707, 1);
		if(Rnd.chance(50))
			addItem(player, 13918, 1); // Sealed Magic Pouch (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13919, 1); // Sealed Magic Pouch (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Aden Supply Box - Magic Pouch: Grade B, C
	public void ItemHandler_13708(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13708, player))
			return;
		removeItem(player, 13708, 1);
		if(Rnd.chance(50))
			addItem(player, 13918, 1); // Sealed Magic Pouch (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13919, 1); // Sealed Magic Pouch (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Innadril Supply Box - Magic Pouch: Grade B, C
	public void ItemHandler_13709(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13709, player))
			return;
		removeItem(player, 13709, 1);
		if(Rnd.chance(50))
			addItem(player, 13918, 1); // Sealed Magic Pouch (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13919, 1); // Sealed Magic Pouch (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Goddard Supply Box - Magic Pouch: Grade B, C
	public void ItemHandler_13710(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13710, player))
			return;
		removeItem(player, 13710, 1);
		if(Rnd.chance(50))
			addItem(player, 13918, 1); // Sealed Magic Pouch (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13919, 1); // Sealed Magic Pouch (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Rune Supply Box - Magic Pouch: Grade B, C
	public void ItemHandler_13711(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13711, player))
			return;
		removeItem(player, 13711, 1);
		if(Rnd.chance(50))
			addItem(player, 13918, 1); // Sealed Magic Pouch (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13919, 1); // Sealed Magic Pouch (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Schuttgart Supply Box - Magic Pouch: Grade B, C
	public void ItemHandler_13712(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13712, player))
			return;
		removeItem(player, 13712, 1);
		if(Rnd.chance(50))
			addItem(player, 13918, 1); // Sealed Magic Pouch (C-Grade)
		if(Rnd.chance(50))
			addItem(player, 13919, 1); // Sealed Magic Pouch (B-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Gludio Supply Box - Magic Pouch: Grade S, A
	public void ItemHandler_14540(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14540, player))
			return;
		removeItem(player, 14540, 1);
		if(Rnd.chance(50))
			addItem(player, 13920, 1); // Sealed Magic Pouch (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13921, 1); // Sealed Magic Pouch (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Dion Supply Box - Magic Pouch: Grade S, A
	public void ItemHandler_14541(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14541, player))
			return;
		removeItem(player, 14541, 1);
		if(Rnd.chance(50))
			addItem(player, 13920, 1); // Sealed Magic Pouch (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13921, 1); // Sealed Magic Pouch (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Giran Supply Box - Magic Pouch: Grade S, A
	public void ItemHandler_14542(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14542, player))
			return;
		removeItem(player, 14542, 1);
		if(Rnd.chance(50))
			addItem(player, 13920, 1); // Sealed Magic Pouch (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13921, 1); // Sealed Magic Pouch (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Oren Supply Box - Magic Pouch: Grade S, A
	public void ItemHandler_14543(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14543, player))
			return;
		removeItem(player, 14543, 1);
		if(Rnd.chance(50))
			addItem(player, 13920, 1); // Sealed Magic Pouch (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13921, 1); // Sealed Magic Pouch (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Aden Supply Box - Magic Pouch: Grade S, A
	public void ItemHandler_14544(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14544, player))
			return;
		removeItem(player, 14544, 1);
		if(Rnd.chance(50))
			addItem(player, 13920, 1); // Sealed Magic Pouch (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13921, 1); // Sealed Magic Pouch (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Innadril Supply Box - Magic Pouch: Grade S, A
	public void ItemHandler_14545(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14545, player))
			return;
		removeItem(player, 14545, 1);
		if(Rnd.chance(50))
			addItem(player, 13920, 1); // Sealed Magic Pouch (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13921, 1); // Sealed Magic Pouch (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Goddard Supply Box - Magic Pouch: Grade S, A
	public void ItemHandler_14546(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14546, player))
			return;
		removeItem(player, 14546, 1);
		if(Rnd.chance(50))
			addItem(player, 13920, 1); // Sealed Magic Pouch (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13921, 1); // Sealed Magic Pouch (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Rune Supply Box - Magic Pouch: Grade S, A
	public void ItemHandler_14547(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14547, player))
			return;
		removeItem(player, 14547, 1);
		if(Rnd.chance(50))
			addItem(player, 13920, 1); // Sealed Magic Pouch (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13921, 1); // Sealed Magic Pouch (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Schuttgart Supply Box - Magic Pouch: Grade S, A
	public void ItemHandler_14548(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14548, player))
			return;
		removeItem(player, 14548, 1);
		if(Rnd.chance(50))
			addItem(player, 13920, 1); // Sealed Magic Pouch (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 13921, 1); // Sealed Magic Pouch (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// ****** Magic Rune Clip ******
	// Gludio Supply Box - Magic Rune Clip: Grade S, A
	public void ItemHandler_14884(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14884, player))
			return;
		removeItem(player, 14884, 1);
		if(Rnd.chance(50))
			addItem(player, 14902, 1); // Sealed Magic Rune Clip (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 14903, 1); // Sealed Magic Rune Clip (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Dion Supply Box - Magic Rune Clip: Grade S, A
	public void ItemHandler_14885(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14885, player))
			return;
		removeItem(player, 14885, 1);
		if(Rnd.chance(50))
			addItem(player, 14902, 1); // Sealed Magic Rune Clip (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 14903, 1); // Sealed Magic Rune Clip (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Giran Supply Box - Magic Rune Clip: Grade S, A
	public void ItemHandler_14886(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14886, player))
			return;
		removeItem(player, 14886, 1);
		if(Rnd.chance(50))
			addItem(player, 14902, 1); // Sealed Magic Rune Clip (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 14903, 1); // Sealed Magic Rune Clip (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Oren Supply Box - Magic Rune Clip: Grade S, A
	public void ItemHandler_14887(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14887, player))
			return;
		removeItem(player, 14887, 1);
		if(Rnd.chance(50))
			addItem(player, 14902, 1); // Sealed Magic Rune Clip (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 14903, 1); // Sealed Magic Rune Clip (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Aden Supply Box - Magic Rune Clip: Grade S, A
	public void ItemHandler_14888(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14888, player))
			return;
		removeItem(player, 14888, 1);
		if(Rnd.chance(50))
			addItem(player, 14902, 1); // Sealed Magic Rune Clip (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 14903, 1); // Sealed Magic Rune Clip (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Innadril Supply Box - Magic Rune Clip: Grade S, A
	public void ItemHandler_14889(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14889, player))
			return;
		removeItem(player, 14889, 1);
		if(Rnd.chance(50))
			addItem(player, 14902, 1); // Sealed Magic Rune Clip (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 14903, 1); // Sealed Magic Rune Clip (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Goddard Supply Box - Magic Rune Clip: Grade S, A
	public void ItemHandler_14890(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14890, player))
			return;
		removeItem(player, 14890, 1);
		if(Rnd.chance(50))
			addItem(player, 14902, 1); // Sealed Magic Rune Clip (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 14903, 1); // Sealed Magic Rune Clip (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Rune Supply Box - Magic Rune Clip: Grade S, A
	public void ItemHandler_14891(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14891, player))
			return;
		removeItem(player, 14891, 1);
		if(Rnd.chance(50))
			addItem(player, 14902, 1); // Sealed Magic Rune Clip (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 14903, 1); // Sealed Magic Rune Clip (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Schuttgart Supply Box - Magic Rune Clip: Grade S, A
	public void ItemHandler_14892(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14892, player))
			return;
		removeItem(player, 14892, 1);
		if(Rnd.chance(50))
			addItem(player, 14902, 1); // Sealed Magic Rune Clip (A-Grade)
		if(Rnd.chance(50))
			addItem(player, 14903, 1); // Sealed Magic Rune Clip (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// ****** Magic Ornament ******
	// Gludio Supply Box - Magic Ornament: Grade S, A
	public void ItemHandler_14893(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14893, player))
			return;
		removeItem(player, 14893, 1);
		if(Rnd.chance(20))
			addItem(player, 14904, 1); // Sealed Magic Ornament (A-Grade)
		if(Rnd.chance(20))
			addItem(player, 14905, 1); // Sealed Magic Ornament (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Dion Supply Box - Magic Ornament: Grade S, A
	public void ItemHandler_14894(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14894, player))
			return;
		removeItem(player, 14894, 1);
		if(Rnd.chance(20))
			addItem(player, 14904, 1); // Sealed Magic Ornament (A-Grade)
		if(Rnd.chance(20))
			addItem(player, 14905, 1); // Sealed Magic Ornament (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Giran Supply Box - Magic Ornament: Grade S, A
	public void ItemHandler_14895(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14895, player))
			return;
		removeItem(player, 14895, 1);
		if(Rnd.chance(20))
			addItem(player, 14904, 1); // Sealed Magic Ornament (A-Grade)
		if(Rnd.chance(20))
			addItem(player, 14905, 1); // Sealed Magic Ornament (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Oren Supply Box - Magic Ornament: Grade S, A
	public void ItemHandler_14896(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14896, player))
			return;
		removeItem(player, 14896, 1);
		if(Rnd.chance(20))
			addItem(player, 14904, 1); // Sealed Magic Ornament (A-Grade)
		if(Rnd.chance(20))
			addItem(player, 14905, 1); // Sealed Magic Ornament (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Aden Supply Box - Magic Ornament: Grade S, A
	public void ItemHandler_14897(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14897, player))
			return;
		removeItem(player, 14897, 1);
		if(Rnd.chance(20))
			addItem(player, 14904, 1); // Sealed Magic Ornament (A-Grade)
		if(Rnd.chance(20))
			addItem(player, 14905, 1); // Sealed Magic Ornament (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Innadril Supply Box - Magic Ornament: Grade S, A
	public void ItemHandler_14898(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14898, player))
			return;
		removeItem(player, 14898, 1);
		if(Rnd.chance(20))
			addItem(player, 14904, 1); // Sealed Magic Ornament (A-Grade)
		if(Rnd.chance(20))
			addItem(player, 14905, 1); // Sealed Magic Ornament (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Goddard Supply Box - Magic Ornament: Grade S, A
	public void ItemHandler_14899(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14899, player))
			return;
		removeItem(player, 14899, 1);
		if(Rnd.chance(20))
			addItem(player, 14904, 1); // Sealed Magic Ornament (A-Grade)
		if(Rnd.chance(20))
			addItem(player, 14905, 1); // Sealed Magic Ornament (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Rune Supply Box - Magic Ornament: Grade S, A
	public void ItemHandler_14900(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14900, player))
			return;
		removeItem(player, 14900, 1);
		if(Rnd.chance(20))
			addItem(player, 14904, 1); // Sealed Magic Ornament (A-Grade)
		if(Rnd.chance(20))
			addItem(player, 14905, 1); // Sealed Magic Ornament (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Schuttgart Supply Box - Magic Ornament: Grade S, A
	public void ItemHandler_14901(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14901, player))
			return;
		removeItem(player, 14901, 1);
		if(Rnd.chance(20))
			addItem(player, 14904, 1); // Sealed Magic Ornament (A-Grade)
		if(Rnd.chance(20))
			addItem(player, 14905, 1); // Sealed Magic Ornament (S-Grade)
		else
			player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
	}

	// Gift from Santa Claus
	public void ItemHandler_14616(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14616, player))
			return;
		removeItem(player, 14616, 1);

		// Santa Claus' Weapon Exchange Ticket - 12 Hour Expiration Period
		addItem(player, 20107, 1);

		// Christmas Red Sock
		addItem(player, 14612, 1);

		// Special Christmas Tree
		if(Rnd.chance(30))
			addItem(player, 5561, 1);

		// Christmas Tree
		if(Rnd.chance(50))
			addItem(player, 5560, 1);

		// Agathion Seal Bracelet - Rudolph (постоянный предмет)
		if(getItemCount(player, 10606) == 0 && Rnd.chance(5))
			addItem(player, 10606, 1);

		// Agathion Seal Bracelet: Rudolph - 30 дней со скилом на виталити
		if(getItemCount(player, 20094) == 0 && Rnd.chance(3))
			addItem(player, 20094, 1);

		// Chest of Experience (Event)
		if(Rnd.chance(30))
			addItem(player, 20575, 1);
	}

	// Christmas Red Sock
	public void ItemHandler_14612(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(player.isInZone(ZoneType.OlympiadStadia))
			return;
		removeItem(player, 14612, 1);
		player.broadcastSkill(new MagicSkillUse(player, player, 23017, 1, 0, 0), true);
		player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(23017, 1));
	}

	// Chest of Experience (Event)
	public void ItemHandler_20575(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20575, player))
			return;
		removeItem(player, 20575, 1);
		addItem(player, 20335, 1); // Rune of Experience: 30% - 5 hour limited time
		addItem(player, 20341, 1); // Rune of SP 30% - 5 Hour Expiration Period
	}

	// Nepal Snow Agathion Pack
	public void ItemHandler_20804(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20804, player))
			return;
		removeItem(player, 20804, 1);
		addItem(player, 20782, 1);
	}

	// Nepal Snow Agathion 7-Day Pack - Snow's Haste
	public void ItemHandler_20807(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20807, player))
			return;
		removeItem(player, 20807, 1);
		addItem(player, 20785, 1);
	}

	// Round Ball Snow Agathion Pack
	public void ItemHandler_20805(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20805, player))
			return;
		removeItem(player, 20805, 1);
		addItem(player, 20783, 1);
	}

	// Round Ball Snow Agathion 7-Day Pack - Snow's Acumen
	public void ItemHandler_20808(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20808, player))
			return;
		removeItem(player, 20808, 1);
		addItem(player, 20786, 1);
	}

	// Ladder Snow Agathion Pack
	public void ItemHandler_20806(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20806, player))
			return;
		removeItem(player, 20806, 1);
		addItem(player, 20784, 1);
	}

	// Ladder Snow Agathion 7-Day Pack - Snow's Wind Walk
	public void ItemHandler_20809(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20809, player))
			return;
		removeItem(player, 20809, 1);
		addItem(player, 20787, 1);
	}

	// Iken Agathion Pack
	public void ItemHandler_20842(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20842, player))
			return;
		removeItem(player, 20842, 1);
		addItem(player, 20818, 1);
	}

	// Iken Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20843(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20843, player))
			return;
		removeItem(player, 20843, 1);
		addItem(player, 20819, 1);
	}

	// Lana Agathion Pack
	public void ItemHandler_20844(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20844, player))
			return;
		removeItem(player, 20844, 1);
		addItem(player, 20820, 1);
	}

	// Lana Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20845(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20845, player))
			return;
		removeItem(player, 20845, 1);
		addItem(player, 20821, 1);
	}

	// Gnocian Agathion Pack
	public void ItemHandler_20846(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20846, player))
			return;
		removeItem(player, 20846, 1);
		addItem(player, 20822, 1);
	}

	// Gnocian Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20847(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20847, player))
			return;
		removeItem(player, 20847, 1);
		addItem(player, 20823, 1);
	}

	// Orodriel Agathion Pack
	public void ItemHandler_20848(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20848, player))
			return;
		removeItem(player, 20848, 1);
		addItem(player, 20824, 1);
	}

	// Orodriel Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20849(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20849, player))
			return;
		removeItem(player, 20849, 1);
		addItem(player, 20825, 1);
	}

	// Lakinos Agathion Pack
	public void ItemHandler_20850(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20850, player))
			return;
		removeItem(player, 20850, 1);
		addItem(player, 20826, 1);
	}

	// Lakinos Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20851(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20851, player))
			return;
		removeItem(player, 20851, 1);
		addItem(player, 20827, 1);
	}

	// Mortia Agathion Pack
	public void ItemHandler_20852(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20852, player))
			return;
		removeItem(player, 20852, 1);
		addItem(player, 20828, 1);
	}

	// Mortia Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20853(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20853, player))
			return;
		removeItem(player, 20853, 1);
		addItem(player, 20829, 1);
	}

	// Hayance Agathion Pack
	public void ItemHandler_20854(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20854, player))
			return;
		removeItem(player, 20854, 1);
		addItem(player, 20830, 1);
	}

	// Hayance Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20855(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20855, player))
			return;
		removeItem(player, 20855, 1);
		addItem(player, 20831, 1);
	}

	// Meruril Agathion Pack
	public void ItemHandler_20856(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20856, player))
			return;
		removeItem(player, 20856, 1);
		addItem(player, 20832, 1);
	}

	// Meruril Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20857(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20857, player))
			return;
		removeItem(player, 20857, 1);
		addItem(player, 20833, 1);
	}

	// Taman ze Lapatui Agathion Pack
	public void ItemHandler_20858(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20858, player))
			return;
		removeItem(player, 20858, 1);
		addItem(player, 20834, 1);
	}

	// Taman ze Lapatui Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20859(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20859, player))
			return;
		removeItem(player, 20859, 1);
		addItem(player, 20835, 1);
	}

	// Kaurin Agathion Pack
	public void ItemHandler_20860(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20860, player))
			return;
		removeItem(player, 20860, 1);
		addItem(player, 20836, 1);
	}

	// Kaurin Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20861(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20861, player))
			return;
		removeItem(player, 20861, 1);
		addItem(player, 20837, 1);
	}

	// Ahertbein Agathion Pack
	public void ItemHandler_20862(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20862, player))
			return;
		removeItem(player, 20862, 1);
		addItem(player, 20838, 1);
	}

	// Ahertbein Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20863(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20863, player))
			return;
		removeItem(player, 20863, 1);
		addItem(player, 20839, 1);
	}

	// Naonin Agathion Pack
	public void ItemHandler_20864(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20864, player))
			return;
		removeItem(player, 20864, 1);
		addItem(player, 20840, 1);
	}

	// Rocket Gun Hat Pack Continuous Fireworks
	public void ItemHandler_20811(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20811, player))
			return;
		removeItem(player, 20811, 1);
		addItem(player, 20789, 1);
	}

	// Yellow Paper Hat 7-Day Pack Bless the Body
	public void ItemHandler_20812(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20812, player))
			return;
		removeItem(player, 20812, 1);
		addItem(player, 20790, 1);
	}

	// Pink Paper Mask Set 7-Day Pack Bless the Soul
	public void ItemHandler_20813(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20813, player))
			return;
		removeItem(player, 20813, 1);
		addItem(player, 20791, 1);
	}

	// Flavorful Cheese Hat Pack
	public void ItemHandler_20814(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20814, player))
			return;
		removeItem(player, 20814, 1);
		addItem(player, 20792, 1);
	}

	// Sweet Cheese Hat Pack
	public void ItemHandler_20815(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20815, player))
			return;
		removeItem(player, 20815, 1);
		addItem(player, 20793, 1);
	}

	// Flavorful Cheese Hat 7-Day Pack Scent of Flavorful Cheese
	public void ItemHandler_20816(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20816, player))
			return;
		removeItem(player, 20816, 1);
		addItem(player, 20794, 1);
	}

	// Sweet Cheese Hat 7-Day Pack Scent of Sweet Cheese
	public void ItemHandler_20817(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20817, player))
			return;
		removeItem(player, 20817, 1);
		addItem(player, 20795, 1);
	}

	// Flame Box Pack
	public void ItemHandler_20810(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20810, player))
			return;
		removeItem(player, 20810, 1);
		addItem(player, 20725, 1);
	}

	// Naonin Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	public void ItemHandler_20865(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20865, player))
			return;
		removeItem(player, 20865, 1);
		addItem(player, 20841, 1);
	}

	// Shiny Mask of Giant Hercules 7 day Pack
	public void ItemHandler_20748(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20748, player))
			return;
		removeItem(player, 20748, 1);
		addItem(player, 20743, 1);
	}

	// Shiny Mask of Silent Scream 7 day Pack
	public void ItemHandler_20749(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20749, player))
			return;
		removeItem(player, 20749, 1);
		addItem(player, 20744, 1);
	}

	// Shiny Spirit of Wrath Mask 7 day Pack
	public void ItemHandler_20750(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20750, player))
			return;
		removeItem(player, 20750, 1);
		addItem(player, 20745, 1);
	}

	// Shiny Undecaying Corpse Mask 7 Day Pack
	public void ItemHandler_20751(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20751, player))
			return;
		removeItem(player, 20751, 1);
		addItem(player, 20746, 1);
	}

	// Shiny Planet X235 Alien Mask 7 day Pack
	public void ItemHandler_20752(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20752, player))
			return;
		removeItem(player, 20752, 1);
		addItem(player, 20747, 1);
	}

	// Simple Valentine Cake
	public void ItemHandler_20195(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20195, player))
			return;
		removeItem(player, 20195, 1);

		// Velvety Valentine Cake
		if(Rnd.chance(20))
			addItem(player, 20196, 1);
		else
		{
			// Dragon Bomber Transformation Scroll
			if(Rnd.chance(5))
				addItem(player, 20371, 1);

			// Unicorn Transformation Scroll
			if(Rnd.chance(5))
				addItem(player, 20367, 1);

			// Quick Healing Potion
			if(Rnd.chance(10))
				addItem(player, 1540, 1);

			// Greater Healing Potion
			if(Rnd.chance(15))
				addItem(player, 1539, 1);
		}
	}
	
	public void ItemHandler_15278(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(15278, player))
			return;
		removeItem(player, 15278, 1);

		addItem(player, 17020, 1);

		if(Rnd.chance(5))
			addItem(player, 20583, 1);
		if(Rnd.chance(1))
			addItem(player, 14705, 1);
		if(Rnd.chance(1))
			addItem(player, 14706, 1);
	}
	
	public void ItemHandler_17020(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(17020, player))
			return;
		removeItem(player, 17020, 1);

		addItem(player, 14739, 1);
	}

	// Velvety Valentine Cake
	public void ItemHandler_20196(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20196, player))
			return;
		removeItem(player, 20196, 1);

		// Delectable Valentine Cake
		if(Rnd.chance(15))
			addItem(player, 20197, 1);
		else
		{
			// Scroll: Enchant Armor (C)
			if(Rnd.chance(10))
				addItem(player, 952, 1);

			// Scroll: Enchant Armor (B)
			if(Rnd.chance(5))
				addItem(player, 948, 1);

			// Blessed Scroll of Escape
			if(Rnd.chance(10))
				addItem(player, 1538, 1);

			// Blessed Scroll of Resurrection
			if(Rnd.chance(5))
				addItem(player, 3936, 1);

			// Agathion of Love - 3 Day Expiration Period
			if(Rnd.chance(10))
				addItem(player, 20200, 1);
		}
	}

	// Delectable Valentine Cake
	public void ItemHandler_20197(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20197, player))
			return;
		removeItem(player, 20197, 1);

		// Decadent Valentine Cake
		if(Rnd.chance(10))
			addItem(player, 20198, 1);
		else
		{
			// Scroll: Enchant Weapon (C)
			if(Rnd.chance(10))
				addItem(player, 951, 1);

			// Scroll: Enchant Weapon (B)
			if(Rnd.chance(5))
				addItem(player, 947, 1);

			// Agathion of Love - 7 Day Expiration Period
			if(Rnd.chance(5))
				addItem(player, 20201, 1);
		}
	}

	// Decadent Valentine Cake
	public void ItemHandler_20198(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20198, player))
			return;
		removeItem(player, 20198, 1);

		// Scroll: Enchant Weapon (S)
		if(Rnd.chance(5))
			addItem(player, 959, 1);

		// Scroll: Enchant Weapon (A)
		if(Rnd.chance(10))
			addItem(player, 729, 1);

		// Agathion of Love - 15 Day Expiration Period
		if(Rnd.chance(10))
			addItem(player, 20202, 1);

		// Agathion of Love - 30 Day Expiration Period
		if(Rnd.chance(5))
			addItem(player, 20203, 1);
	}

	private static final int[] SOI_books = { 14209, // Forgotten Scroll - Hide
			14212, // Forgotten Scroll - Enlightenment - Wizard
			14213, // Forgotten Scroll - Enlightenment - Healer
			10554, //Forgotten Scroll - Anti-Magic Armor
			14208, // Forgotten Scroll - Final Secret
			10577 // Forgotten Scroll - Excessive Loyalty
	};

	// Jewel Ornamented Duel Supplies
	public void ItemHandler_13777(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13777, player))
			return;
		removeItem(player, 13777, 1);

		int rnd = Rnd.get(100);
		if(rnd <= 65)
		{
			addItem(player, 9630, 3); // 3 Orichalcum
			addItem(player, 9629, 3); // 3 Adamantine
			addItem(player, 9628, 4); // 4 Leonard
			addItem(player, 8639, 6); // 6 Elixir of CP (S-Grade)
			addItem(player, 8627, 6); // 6 Elixir of Life (S-Grade)
			addItem(player, 8633, 6); // 6 Elixir of Mental Strength (S-Grade)
		}
		else if(rnd <= 95)
			addItem(player, SOI_books[Rnd.get(SOI_books.length)], 1);
		else
			addItem(player, 14027, 1); // Collection Agathion Summon Bracelet
	}

	// Mother-of-Pearl Ornamented Duel Supplies
	public void ItemHandler_13778(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13778, player))
			return;
		removeItem(player, 13778, 1);

		int rnd = Rnd.get(100);
		if(rnd <= 65)
		{
			addItem(player, 9630, 2); // 3 Orichalcum
			addItem(player, 9629, 2); // 3 Adamantine
			addItem(player, 9628, 3); // 4 Leonard
			addItem(player, 8639, 5); // 5 Elixir of CP (S-Grade)
			addItem(player, 8627, 5); // 5 Elixir of Life (S-Grade)
			addItem(player, 8633, 5); // 5 Elixir of Mental Strength (S-Grade)
		}
		else if(rnd <= 95)
			addItem(player, SOI_books[Rnd.get(SOI_books.length)], 1);
		else
			addItem(player, 14027, 1); // Collection Agathion Summon Bracelet
	}

	// Gold-Ornamented Duel Supplies
	public void ItemHandler_13779(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13779, player))
			return;
		removeItem(player, 13779, 1);

		int rnd = Rnd.get(100);
		if(rnd <= 65)
		{
			addItem(player, 9630, 1); // 1 Orichalcum
			addItem(player, 9629, 1); // 1 Adamantine
			addItem(player, 9628, 2); // 2 Leonard
			addItem(player, 8639, 4); // 4 Elixir of CP (S-Grade)
			addItem(player, 8627, 4); // 4 Elixir of Life (S-Grade)
			addItem(player, 8633, 4); // 4 Elixir of Mental Strength (S-Grade)
		}
		else if(rnd <= 95)
			addItem(player, SOI_books[Rnd.get(SOI_books.length)], 1);
		else
			addItem(player, 14027, 1); // Collection Agathion Summon Bracelet
	}

	// Silver-Ornamented Duel Supplies
	public void ItemHandler_13780(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13780, player))
			return;
		removeItem(player, 13780, 1);

		addItem(player, 8639, 4); // 4 Elixir of CP (S-Grade)
		addItem(player, 8627, 4); // 4 Elixir of Life (S-Grade)
		addItem(player, 8633, 4); // 4 Elixir of Mental Strength (S-Grade)
	}

	// Bronze-Ornamented Duel Supplies
	public void ItemHandler_13781(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13781, player))
			return;
		removeItem(player, 13781, 1);

		addItem(player, 8639, 4); // 4 Elixir of CP (S-Grade)
		addItem(player, 8627, 4); // 4 Elixir of Life (S-Grade)
		addItem(player, 8633, 4); // 4 Elixir of Mental Strength (S-Grade)
	}

	// Non-Ornamented Duel Supplies
	public void ItemHandler_13782(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13782, player))
			return;
		removeItem(player, 13782, 1);

		addItem(player, 8639, 3); // 3 Elixir of CP (S-Grade)
		addItem(player, 8627, 3); // 3 Elixir of Life (S-Grade)
		addItem(player, 8633, 3); // 3 Elixir of Mental Strength (S-Grade)
	}

	// Weak-Looking Duel Supplies
	public void ItemHandler_13783(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13783, player))
			return;
		removeItem(player, 13783, 1);

		addItem(player, 8639, 3); // 3 Elixir of CP (S-Grade)
		addItem(player, 8627, 3); // 3 Elixir of Life (S-Grade)
		addItem(player, 8633, 3); // 3 Elixir of Mental Strength (S-Grade)
	}

	// Sad-Looking Duel Supplies
	public void ItemHandler_13784(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13784, player))
			return;
		removeItem(player, 13784, 1);

		addItem(player, 8639, 3); // 3 Elixir of CP (S-Grade)
		addItem(player, 8627, 3); // 3 Elixir of Life (S-Grade)
		addItem(player, 8633, 3); // 3 Elixir of Mental Strength (S-Grade)
	}

	// Poor-Looking Duel Supplies
	public void ItemHandler_13785(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13785, player))
			return;
		removeItem(player, 13785, 1);

		addItem(player, 8639, 2); // 2 Elixir of CP (S-Grade)
		addItem(player, 8627, 2); // 2 Elixir of Life (S-Grade)
		addItem(player, 8633, 2); // 2 Elixir of Mental Strength (S-Grade)
	}

	// Worthless Duel Supplies
	public void ItemHandler_13786(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13786, player))
			return;
		removeItem(player, 13786, 1);

		addItem(player, 8639, 1); // 1 Elixir of CP (S-Grade)
		addItem(player, 8627, 1); // 1 Elixir of Life (S-Grade)
		addItem(player, 8633, 1); // 1 Elixir of Mental Strength (S-Grade)
	}

	// S-Grade Accessory Chest(MasterOfEnchanting Event)
	private static final int[] SAccessoryChest = { 6724, 6725, 6726 };

	public void ItemHandler_13992(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13992, player))
			return;
		removeItem(player, 13992, 1);
		addItem(player, SAccessoryChest[Rnd.get(SAccessoryChest.length)], 1);
	}

	// S-Grade Armor Chest(MasterOfEnchanting Event)
	private static final int[] SArmorChest = { 6674, 6675, 6679, 6683, 6687, 6678, 6677, 6682, 6686, 6676, 6681, 6685,
			9582, 10500, 10501, 10502, 6680, 6684 };

	public void ItemHandler_13991(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13991, player))
			return;
		removeItem(player, 13991, 1);
		addItem(player, SArmorChest[Rnd.get(SArmorChest.length)], 1);
	}

	// S-Grade Weapon Chest(MasterOfEnchanting Event)
	private static final int[] SWeaponChest = { 6364, 6372, 6365, 6579, 6369, 6367, 6370, 6371, 7575, 6580 };

	public void ItemHandler_13990(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13990, player))
			return;
		removeItem(player, 13990, 1);
		addItem(player, SWeaponChest[Rnd.get(SWeaponChest.length)], 1);
	}

	// S80-Grade Armor Chest(MasterOfEnchanting Event)
	private static final int[] S80ArmorChest = { 9514, 9519, 9515, 9520, 9525, 9516, 9521, 9526, 9529, 9518, 9523, 9528,
			9517, 9522, 9527 };

	public void ItemHandler_13989(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13989, player))
			return;
		removeItem(player, 13989, 1);
		addItem(player, S80ArmorChest[Rnd.get(S80ArmorChest.length)], 1);
	}

	// S80-Grade Weapon Chest(MasterOfEnchanting Event)
	private static final int[] S80WeaponChest = { 9444, 9442, 9449, 9448, 9446, 9447, 9450, 9445, 10004 };

	public void ItemHandler_13988(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(13988, player))
			return;
		removeItem(player, 13988, 1);
		addItem(player, S80WeaponChest[Rnd.get(S80WeaponChest.length)], 1);
	}
	
	public void ItemHandler_13021(L2Player player, Boolean ctrl) // Name Color CHange
	{
		player.sendPacket(new ExChangeNicknameNColor());
	}
	
	public void ItemHandler_14833(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14833, player))
			return;
		removeItem(player, 14833, 1);

		if(Rnd.chance(10))
			addItem(player, 14209, 1);

		if(Rnd.chance(10))
			addItem(player, 14208, 1);

		if(Rnd.chance(10))
			addItem(player, 14212, 1);

		if(Rnd.chance(10))
			addItem(player, 10577, 1);
				
		if(Rnd.chance(10))
			addItem(player, 959, 1);
		
		if(Rnd.chance(15))
			addItem(player, 960, 2);
			
		if(Rnd.chance(25))
			addItem(player, 9573, 1);
			
		if(Rnd.chance(25))
			addItem(player, 10483, 1);
			
		if(Rnd.chance(5))
		{
			int[] list = new int[] { 10373, 10374, 10375, 10376, 10377, 10378, 10379, 10380, 10381 };
			int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			int[] chances = new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8 };
			
			if(ctrl)
			{
				long item_count = player.getInventory().getCountOf(14833);
				removeItem(player, 14833, item_count);
				for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
					addItem(player, res[0], res[1]);
			}
			else
			{
				removeItem(player, 14833, 1);
				extract_item_r(list, counts, chances, player);	
			}
		}
	}
	
	public void ItemHandler_14834(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14834, player))
			return;
		removeItem(player, 14834, 1);

		if(Rnd.chance(15))
			addItem(player, 14209, 1);

		if(Rnd.chance(15))
			addItem(player, 14208, 1);

		if(Rnd.chance(15))
			addItem(player, 14212, 1);

		if(Rnd.chance(15))
			addItem(player, 10577, 1);
				
		if(Rnd.chance(15))
			addItem(player, 959, 1);
		
		if(Rnd.chance(20))
			addItem(player, 960, 2);
			
		if(Rnd.chance(25))
			addItem(player, 9573, 1);
			
		if(Rnd.chance(25))
			addItem(player, 10483, 1);
			
		if(Rnd.chance(10))
		{
			int[] list = new int[] { 10373, 10374, 10375, 10376, 10377, 10378, 10379, 10380, 10381 };
			int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			int[] chances = new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8 };
			
			if(ctrl)
			{
				long item_count = player.getInventory().getCountOf(14834);
				removeItem(player, 14834, item_count);
				for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
					addItem(player, res[0], res[1]);
			}
			else
			{
				removeItem(player, 14834, 1);
				extract_item_r(list, counts, chances, player);	
			}
		}		
	}
	
	public void ItemHandler_14849(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14849, player))
			return;
		removeItem(player, 14849, 1);

		if(Rnd.chance(15))
			addItem(player, 14209, 1);

		if(Rnd.chance(15))
			addItem(player, 14208, 1);

		if(Rnd.chance(15))
			addItem(player, 14212, 1);

		if(Rnd.chance(15))
			addItem(player, 10577, 1);
				
		if(Rnd.chance(15))
			addItem(player, 959, 1);
		
		if(Rnd.chance(20))
			addItem(player, 960, 2);
			
		if(Rnd.chance(25))
			addItem(player, 9573, 1);
			
		if(Rnd.chance(25))
			addItem(player, 10483, 1);
			
		if(Rnd.chance(10))
			addItem(player, 9625, 1);
		
		if(Rnd.chance(10))
			addItem(player, 9626, 1);
			
		if(Rnd.chance(9))
		{
			int[] list = new int[] { 10373, 10374, 10375, 10376, 10377, 10378, 10379, 10380, 10381 };
			int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			int[] chances = new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8 };
			
			if(ctrl)
			{
				long item_count = player.getInventory().getCountOf(14849);
				removeItem(player, 14849, item_count);
				for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
					addItem(player, res[0], res[1]);
			}
			else
			{
				removeItem(player, 14849, 1);
				extract_item_r(list, counts, chances, player);	
			}
		}		
	}
	
	public void ItemHandler_14850(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(14850, player))
			return;
		removeItem(player, 14850, 1);

		if(Rnd.chance(15))
			addItem(player, 14209, 1);

		if(Rnd.chance(15))
			addItem(player, 14208, 1);

		if(Rnd.chance(15))
			addItem(player, 14212, 1);

		if(Rnd.chance(15))
			addItem(player, 10577, 1);

		if(Rnd.chance(15))
			addItem(player, 959, 1);

		if(Rnd.chance(20))
			addItem(player, 960, 2);

		if(Rnd.chance(25))
			addItem(player, 9573, 1);

		if(Rnd.chance(25))
			addItem(player, 10483, 1);

		if(Rnd.chance(10))
			addItem(player, 9625, 1);

		if(Rnd.chance(10))
			addItem(player, 9626, 1);

		if(Rnd.chance(9))
		{
			int[] list = new int[] { 10373, 10374, 10375, 10376, 10377, 10378, 10379, 10380, 10381 };
			int[] counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			int[] chances = new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8 };

			if(ctrl)
			{
				long item_count = player.getInventory().getCountOf(14850);
				removeItem(player, 14850, item_count);
				for(int[] res : mass_extract_item_r(item_count, list, counts, chances, player))
					addItem(player, res[0], res[1]);
			}
			else
			{
				removeItem(player, 14850, 1);
				extract_item_r(list, counts, chances, player);	
			}
		}		
	}	
	
	public void ItemHandler_15482(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(15482, player))
			return;
		removeItem(player, 15482, 1);
		addItem(player, 15474, 50);
		if(Rnd.chance(5))
		{
			addItem(player, 15474, 40);
			addItem(player, 15476, 5);
		}
		if(Rnd.chance(5))
		{
			addItem(player, 15474, 40);
			addItem(player, 15478, 5);
		}
	}
	
	public static void ItemHandler_15483(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(15483, player))
			return;
		removeItem(player, 15483, 1);
		addItem(player, 15475, 50);
		if(Rnd.chance(5))
		{
			addItem(player, 15475, 40);
			addItem(player, 15477, 5);
		}
		if(Rnd.chance(5))
		{
			addItem(player, 15475, 40);
			addItem(player, 15479, 5);
		}
	}

	// Golden Jack O'Lantern Mask 7 Day Pack
	public static void ItemHandler_20734(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20734, player))
			return;
		removeItem(player, 20734, 1);
		addItem(player, 20724, 1);
	}

	// Treasure Sack of the Ancient Giants
	public static void ItemHandler_13799(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(13799, player))
			return;
		removeItem(player, 13799, 1);

		addItem(player, 9628, rollDrop(1, 1, 35, true, player)); // Leonard
		addItem(player, 9629, rollDrop(1, 1, 30, true, player)); // Adamantine
		addItem(player, 9630, rollDrop(1, 1, 35, true, player)); // Orichalcum
	}

	// Olympiad Treasure Chest
	public static void ItemHandler_17169(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(17169, player))
			return;
		removeItem(player, 17169, 1);

		int[][] items = new int[][]{{13750, 1}, {13751, 1}, {13754, 1}, {13753, 1}, {13752, 1}, {6622, 1}, {8621, 1}};
		double[] chances = new double[]{34.7, 12.3, 2.65, 1.2, 1.98, 46.5, 5.4};
		if(Rnd.chance(60))
			extractRandomOneItem(player, items, chances);
		int[] counts = {100, 150, 200, 250, 300, 350};
		addItem(player, 13722, counts[Rnd.get(counts.length)]);
	}

	// Nevit's Voice
	public static void ItemHandler_17094(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(17094, player))
			return;
		removeItem(player, 17094, 1);

		player.getRecommendation().addRecomHave(10);
		player.getRecommendation().updateVoteInfo();
	}

	// Birthday Present Pack
	public static void ItemHandler_21169(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21169, player))
			return;
		removeItem(player, 21169, 1);

		addItem(player, 21170, 3);
		addItem(player, 21595, 1);
		addItem(player, 13488, 1);
	}
	// Beginner Adventurer's Treasure Sack
	public static void ItemHandler_21747(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21747, player))
			return;
		removeItem(player, 21747, 1);

		int group = Rnd.get(7);
		int[] items = new int[0];
		if(group < 4) //Low D-Grade rewards
			items = new int[]{312, 167, 220, 258, 178, 221, 123, 156, 291, 166, 274};
		else if(group >= 4) //Mid D-Grade rewards
			items = new int[]{261, 224, 318, 93, 129, 294, 88, 90, 158, 172, 279, 169};

		addItem(player, items[Rnd.get(items.length)], 1);
	}

	// Experienced Adventurer's Treasure Sack
	public static void ItemHandler_21748(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21748, player))
			return;
		removeItem(player, 21748, 1);

		int group = Rnd.get(10);
		int[] items = new int[0];
		if(group < 4) //Low C-Grade rewards
			items = new int[]{160, 298, 72, 193, 192, 281, 7887, 226, 2524, 191, 71, 263};
		else if(group >= 4 && group < 7) //Low B-Grade rewards
			items = new int[]{78, 2571, 300, 284, 142, 267, 229, 148, 243, 92, 7892, 91};
		else if(group >= 7 && group < 9) //Low A-Grade rewards
			items = new int[]{98, 5233, 80, 235, 269, 288, 7884, 2504, 150, 7899, 212};
		else if(group == 9) //Low S-Grade rewards
			items = new int[]{6365, 6371, 6364, 6366, 6580, 7575, 6579, 6372, 6370, 6369, 6367};

		addItem(player, items[Rnd.get(items.length)], 1);
	}

	// Great Adventurer's Treasure Sack
	public static void ItemHandler_21749(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21749, player))
			return;
		removeItem(player, 21749, 1);

		int group = Rnd.get(9);
		int[] items = new int[0];
		if(group < 5) //Top S-Grade rewards
			items = new int[]{9447, 9384, 9449, 9380, 9448, 9443, 9450, 10253, 9445, 9442, 9446, 10004, 10252, 9376, 9444};
		else if(group >= 5 && group < 8) //S80-Grade rewards
			items = new int[]{10226, 10217, 10224, 10215, 10225, 10223, 10220, 10415, 10216, 10221, 10219, 10218, 10222};
		else if(group == 8) //Low S84-Grade rewards
			items = new int[]{13467, 13462, 13464, 13461, 13465, 13468, 13463, 13470, 13460, 52, 13466, 13459, 13457, 13469, 13458};

		addItem(player, items[Rnd.get(items.length)], 1);
	}

	// Pablo's Box
	public static void ItemHandler_21753(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21753, player))
			return;
		removeItem(player, 21753, 1);

		int category = Rnd.get(7);
		switch(category)
		{
			case 0:
				addItem(player, 21122, 1);
				break;
			case 1:
				addItem(player, 21118, 1);
				break;
			case 2:
				addItem(player, 21116, 1);
				break;
			case 3:
				addItem(player, 21114, 1);
				break;
			case 4:
				addItem(player, 21112, 1);
				break;
			case 5:
				addItem(player, 21120, 1);
				break;
			case 6:
				addItem(player, 21126, 1);
				break;
		}
	}

	// Rune Jewelry Box - Talisman
	public static void ItemHandler_21752(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21752, player))
			return;
		removeItem(player, 21752, 1);

		final GArray<Integer> talismans = new GArray<Integer>();

		//9914-9965
		for(int i = 9914; i <= 9965; i++)
			if(i != 9923)
				talismans.add(i);
		//10416-10424
		for(int i = 10416; i <= 10424; i++)
			talismans.add(i);
		//10518-10519
		for(int i = 10518; i <= 10519; i++)
			talismans.add(i);
		//10533-10543
		for(int i = 10533; i <= 10543; i++)
			talismans.add(i);

		addItem(player, talismans.get(Rnd.get(talismans.size())), 1);
	}

	// 1st Place Treasure Sack
	public void ItemHandler_10254(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(10254, player))
			return;
		removeItem(player, 10254, 1);

		int _chanche = Rnd.get(100);
		if(_chanche < 25)
			addItem(player, 10295, 1); // Transformation Sealbook - Zaken
		else if(_chanche < 50)
			addItem(player, 10296, 1); // Transformation Sealbook - Anakim
		else if(_chanche < 75)
		{
			addItem(player, 17263, 1); // Blessed Weapon Enchant Scroll - S-Grade
			addItem(player, 10295, 2); // Transformation Sealbook - Zaken
			addItem(player, 10296, 2); // Transformation Sealbook - Anakim
		}
		else
		{
			addItem(player, 17264, 1); // Blessed Armor Enchant Scroll - S-Grade
			addItem(player, 10295, 2); // Transformation Sealbook - Zaken
			addItem(player, 10296, 2); // Transformation Sealbook - Anakim
		}
	}

	// 2st Place Treasure Sack
	public void ItemHandler_10255(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(10255, player))
			return;
		removeItem(player, 10255, 1);

		int _chanche = Rnd.get(100);
		if(_chanche < 15)
			addItem(player, 10297, 1); // Transformation Sealbook - Venom
		else if(_chanche < 30)
			addItem(player, 10300, 1); // Transformation Sealbook - Kechi
		else if(_chanche < 45)
			addItem(player, 10301, 1); // Transformation Sealbook - Demon Prince
		else if(_chanche < 58)
		{
			addItem(player, 17263, 1); // Blessed Weapon Enchant Scroll - S-Grade
			addItem(player, 10295, 2); // Transformation Sealbook - Zaken
			addItem(player, 10296, 2); // Transformation Sealbook - Anakim
		}
		else if(_chanche < 72)
		{
			addItem(player, 17264, 1); // Blessed Armor Enchant Scroll - S-Grade
			addItem(player, 10295, 2); // Transformation Sealbook - Zaken
			addItem(player, 10296, 2); // Transformation Sealbook - Anakim
		}
		else if(_chanche < 87)
		{
			addItem(player, 17263, 1); // Blessed Weapon Enchant Scroll - S-Grade
			addItem(player, 10295, 1); // Transformation Sealbook - Zaken
			addItem(player, 10296, 1); // Transformation Sealbook - Anakim
		}
		else
		{
			addItem(player, 17264, 1); // Blessed Armor Enchant Scroll - S-Grade
			addItem(player, 10295, 1); // Transformation Sealbook - Zaken
			addItem(player, 10296, 1); // Transformation Sealbook - Anakim
		}
	}

	// 3st Place Treasure Sack
	public void ItemHandler_10256(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(10256, player))
			return;
		removeItem(player, 10256, 1);

		int _chanche = Rnd.get(150);
		if(_chanche < 10)
			addItem(player, 10298, 1); // Transformation Sealbook - Gordon
		else if(_chanche < 20)
			addItem(player, 10299, 1); // Transformation Sealbook - Ranku
		else if(_chanche < 30)
			addItem(player, 10303, 1); // Transformation Sealbook - Veil Master
		else if(_chanche < 40)
			addItem(player, 10302, 1); // Transformation Sealbook - Heretic
		else if(_chanche < 50)
			addItem(player, 10304, 1); // Transformation Sealbook - Saber Tooth Tiger
		else if(_chanche < 60)
			addItem(player, 10305, 1); // Transformation Sealbook - Ol Mahum
		else if(_chanche < 70)
			addItem(player, 10306, 1); // Transformation Sealbook - Doll Blader
		else if(_chanche < 80)
			addItem(player, 17255, 1); // Blessed Weapon Enchant Scroll - A-Grade
		else if(_chanche < 90)
			addItem(player, 17258, 1); // Blessed Armor Enchant Scroll - A-Grade
		else if(_chanche < 100)
			addItem(player, 9552, 1); // Fire Crystal
		else if(_chanche < 110)
			addItem(player, 9553, 1); // Water Crystal
		else if(_chanche < 120)
			addItem(player, 9554, 1); // Earth Crystal
		else if(_chanche < 130)
			addItem(player, 9555, 1); // Wind Crystal
		else if(_chanche < 140)
			addItem(player, 9556, 1); // Dark Crystal
		else
			addItem(player, 9557, 1); // Holy Crystal
	}

	// 4st Place Treasure Sack
	public void ItemHandler_10257(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(10257, player))
			return;
		removeItem(player, 10257, 1);

		int _chanche = Rnd.get(110);
		if(_chanche < 10)
		{
			addItem(player, 21187, 3); // Transformation Scroll - Venom
			addItem(player, 21188, 3); // Transformation Scroll - Gordon
			addItem(player, 21189, 3); // Transformation Scroll - Ranku
			addItem(player, 21190, 3); // Transformation Scroll - Kechi
			addItem(player, 21191, 3); // Transformation Scroll - Demon Prince
		}
		else if(_chanche < 20)
		{
			addItem(player, 21174, 3); // Transformation Scroll - Grail Apostle
			addItem(player, 21175, 3); // Transformation Scroll - Unicorn
			addItem(player, 21176, 3); // Transformation Scroll - Lilim Knight
		}
		else if(_chanche < 30)
		{
			addItem(player, 21177, 3); // Transformation Scroll - Golem Guardian
			addItem(player, 21178, 3); // Transformation Scroll - Inferno Drake
			addItem(player, 21179, 3); // Transformation Scroll - Dragon Bomber
		}
		else if(_chanche < 40)
			addItem(player, 17257, 1); // Blessed Weapon Enchant Scroll - B-Grade
		else if(_chanche < 50)
			addItem(player, 17258, 1); // Blessed Armor Enchant Scroll - B-Grade
		else if(_chanche < 60)
			addItem(player, 9552, 1); // Fire Crystal
		else if(_chanche < 70)
			addItem(player, 9553, 1); // Water Crystal
		else if(_chanche < 80)
			addItem(player, 9554, 1); // Earth Crystal
		else if(_chanche < 90)
			addItem(player, 9555, 1); // Wind Crystal
		else if(_chanche < 100)
			addItem(player, 9556, 1); // Dark Crystal
		else
			addItem(player, 9557, 1); // Holy Crystal
	}

	// 5st Place Treasure Sack
	public void ItemHandler_10258(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(10258, player))
			return;
		removeItem(player, 10258, 1);

		int _chanche = Rnd.get(110);
		if(_chanche < 10)
		{
			addItem(player, 21187, 2); // Transformation Scroll - Venom
			addItem(player, 21190, 2); // Transformation Scroll - Kechi
			addItem(player, 21191, 2); // Transformation Scroll - Demon Prince
		}
		else if(_chanche < 20)
		{
			addItem(player, 21174, 2); // Transformation Scroll - Grail Apostle
			addItem(player, 21175, 2); // Transformation Scroll - Unicorn
			addItem(player, 21176, 2); // Transformation Scroll - Lilim Knight
		}
		else if(_chanche < 30)
		{
			addItem(player, 21177, 2); // Transformation Scroll - Golem Guardian
			addItem(player, 21178, 2); // Transformation Scroll - Inferno Drake
			addItem(player, 21179, 2); // Transformation Scroll - Dragon Bomber
		}
		else if(_chanche < 40)
			addItem(player, 17259, 1); // Blessed Weapon Enchant Scroll - C-Grade
		else if(_chanche < 50)
			addItem(player, 17260, 1); // Blessed Armor Enchant Scroll - C-Grade
		else if(_chanche < 60)
			addItem(player, 9552, 1); // Fire Crystal
		else if(_chanche < 70)
			addItem(player, 9553, 1); // Water Crystal
		else if(_chanche < 80)
			addItem(player, 9554, 1); // Earth Crystal
		else if(_chanche < 90)
			addItem(player, 9555, 1); // Wind Crystal
		else if(_chanche < 100)
			addItem(player, 9556, 1); // Dark Crystal
		else
			addItem(player, 9557, 1); // Holy Crystal
	}

	// 6st Place Treasure Sack
	public void ItemHandler_10259(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(10259, player))
			return;
		removeItem(player, 10259, 1);

		int _chanche = Rnd.get(100);
		if(_chanche < 10)
		{
			addItem(player, 21174, 1); // Transformation Scroll - Grail Apostle
			addItem(player, 21175, 1); // Transformation Scroll - Unicorn
			addItem(player, 21176, 1); // Transformation Scroll - Lilim Knight
			addItem(player, 21172, 1); // Transformation Scroll - Onyx Beast
		}
		else if(_chanche < 20)
		{
			addItem(player, 21177, 1); // Transformation Scroll - Golem Guardian
			addItem(player, 21178, 1); // Transformation Scroll - Inferno Drake
			addItem(player, 21179, 1); // Transformation Scroll - Dragon Bomber
			addItem(player, 21173, 1); // Transformation Scroll - Death Blader
		}
		else if(_chanche < 30)
			addItem(player, 17261, 1); // Blessed Weapon Enchant Scroll - D-Grade
		else if(_chanche < 40)
			addItem(player, 17262, 1); // Blessed Armor Enchant Scroll - D-Grade
		else if(_chanche < 50)
			addItem(player, 9552, 1); // Fire Crystal
		else if(_chanche < 60)
			addItem(player, 9553, 1); // Water Crystal
		else if(_chanche < 70)
			addItem(player, 9554, 1); // Earth Crystal
		else if(_chanche < 80)
			addItem(player, 9555, 1); // Wind Crystal
		else if(_chanche < 90)
			addItem(player, 9556, 1); // Dark Crystal
		else
			addItem(player, 9557, 1); // Holy Crystal
	}
	
	// Huge fortuna box
	public void ItemHandler_20515(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20515, player))
			return;
		int[] list = new int[] { 22009, 20517, 22024, 8751, 20546, 8752, 9575, 20547, 20545, 8761, 8762, 9576, 22015, 20519, 22019, 20521};
		int[] counts = new int[] { 2, 1, 15, 1, 10, 1, 1, 10, 8, 1, 1, 1, 1, 1, 1, 1};
		int[] chances = new int[] { 24, 14, 12, 10, 8, 8, 7, 4, 4, 2, 2, 1, 1, 1, 1, 1};
		removeItem(player, 20515, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Huge fortuna cube
	public void ItemHandler_20516(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		if(!canBeExtracted(20516, player))
			return;
		int[] list = new int[] { 22013, 20518, 22017, 20520, 22021, 20522, 20545, 20546, 20547, 8751, 8752, 9575, 8761, 8762, 9576, 22024};
		int[] counts = new int[] { 2, 1, 1, 1, 1, 1, 8, 10, 10, 1, 1, 1, 1, 1, 1, 15};
		int[] chances = new int[] { 25, 14, 1, 1, 1, 1, 3, 7, 4, 10, 8, 7, 2, 2, 1, 13};
		removeItem(player, 20516, 1);
		extract_item_r(list, counts, chances, player);
	}

	// Soul Crystal - Stage 17 Box
	public static void ItemHandler_22206(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(22206, player))
			return;
		removeItem(player, 22206, 1);

		final GArray<Integer> talismans = new GArray<Integer>();

		talismans.add(15541);
		talismans.add(15542);
		talismans.add(15543);

		addItem(player, talismans.get(Rnd.get(talismans.size())), 1);
	}

	//Greater Elixir Gift Box (No-Grade)
	public void ItemHandler_14713(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(14713, player))
			return;
		removeItem(player, 14713, 1);
		addItem(player, 14682, 50); // Greater Elixir of Life (No-Grade)
		addItem(player, 14688, 50); // Greater Elixir of Mental Strength (No-Grade)
		addItem(player, 14694, 50); // Greater Elixir of CP (No Grade)
	}

	//Greater Elixir Gift Box (D-Grade)
	public void ItemHandler_14714(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(14714, player))
			return;
		removeItem(player, 14714, 1);
		addItem(player, 14683, 50); // Greater Elixir of Life (D-Grade)
		addItem(player, 14689, 50); // Greater Elixir of Mental Strength (D-Grade)
		addItem(player, 14695, 50); // Greater Elixir of CP (D Grade)
	}

	//Greater Elixir Gift Box (C-Grade)
	public void ItemHandler_14715(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(14715, player))
			return;
		removeItem(player, 14715, 1);
		addItem(player, 14684, 50); // Greater Elixir of Life (C-Grade)
		addItem(player, 14690, 50); // Greater Elixir of Mental Strength (C-Grade)
		addItem(player, 14696, 50); // Greater Elixir of CP (C Grade)
	}

	//Greater Elixir Gift Box (B-Grade)
	public void ItemHandler_14716(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(14716, player))
			return;
		removeItem(player, 14716, 1);
		addItem(player, 14685, 50); // Greater Elixir of Life (B-Grade)
		addItem(player, 14691, 50); // Greater Elixir of Mental Strength (B-Grade)
		addItem(player, 14697, 50); // Greater Elixir of CP (B Grade)
	}

	//Greater Elixir Gift Box (A-Grade)
	public void ItemHandler_14717(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(14717, player))
			return;
		removeItem(player, 14717, 1);
		addItem(player, 14686, 50); // Greater Elixir of Life (A-Grade)
		addItem(player, 14692, 50); // Greater Elixir of Mental Strength (A-Grade)
		addItem(player, 14698, 50); // Greater Elixir of CP (A Grade)
	}

	//Greater Elixir Gift Box (S-Grade)
	public void ItemHandler_14718(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(14718, player))
			return;
		removeItem(player, 14718, 1);
		addItem(player, 14687, 50); // Greater Elixir of Life (S-Grade)
		addItem(player, 14693, 50); // Greater Elixir of Mental Strength (S-Grade)
		addItem(player, 14699, 50); // Greater Elixir of CP (S Grade)
	}

	public void ItemHandler_15627(L2Player player, Boolean ctrl)
	{
		int count = 1000;
		if(!canBeExtracted(15627, player))
			return;
		if(player.getClan() == null)
			return;
		removeItem(player, 15627, 1);
		player.getClan().incReputation(count, false, "ItemHandler_15627");
		player.sendPacket(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_ADDED_1S_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber(count));
	}
	
	public void ItemHandler_13015(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(13015, player))
			return;
		int count = player.bookmarks.getCapacity();
		if(count < 9)
		{
			removeItem(player, 13015, 1);
			player.bookmarks.setCapacity(count+3);
		}
	}

	public void ItemHandler_13301(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(13301, player))
			return;
		int count = player.bookmarks.getCapacity();
		if(count < 9)
		{
			removeItem(player, 13301, 1);
			player.bookmarks.setCapacity(count+3);
		}
	}

	public void ItemHandler_9122(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(9122, player))
			return;
		removeItem(player, 9122, 1);

		int[] items = {9034, 9035, 9037, 9038, 9036, 9039};
		int[] counts = {1,1,1,1,1,1};

		for(int index=0;index<items.length;index++)
			addItem(player, items[index], counts[index]);
	}

	public void ItemHandler_9123(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(9123, player))
			return;
		removeItem(player, 9123, 1);

		int[] items = {9049, 9050, 9051, 9052, 9053, 9044};
		int[] counts = {1,1,1,1,1,1};
		for(int index=0;index<items.length;index++)
			addItem(player, items[index], counts[index]);
	}

	public void ItemHandler_9116(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(9116, player))
			return;
		removeItem(player, 9116, 1);

		int[] items = {9032, 9033, 9037, 9038, 9039};
		int[] counts = {1,1,1,1,1};
		for(int index=0;index<items.length;index++)
			addItem(player, items[index], counts[index]);
	}

	public void ItemHandler_9117(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(9117, player))
			return;
		removeItem(player, 9117, 1);

		int[] items = {9045, 9046, 9047, 9048, 9053};
		int[] counts = {1,1,1,1,1};
		for(int index=0;index<items.length;index++)
			addItem(player, items[index], counts[index]);
	}

	public void ItemHandler_9110(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(9110, player))
			return;
		removeItem(player, 9110, 1);

		int[] items = {9030, 9031, 9038, 9039, 9037, 9036};
		int[] counts = {1,1,1,1,1,1};
		for(int index=0;index<items.length;index++)
			addItem(player, items[index], counts[index]);
	}

	public void ItemHandler_9111(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(9111, player))
			return;
		removeItem(player, 9111, 1);

		int[] items = {9040, 9041, 9042, 9043, 9044, 9053};
		int[] counts = {1,1,1,1,1,1};
		for(int index=0;index<items.length;index++)
			addItem(player, items[index], counts[index]);
	}

	public void ItemHandler_20081(L2Player player, Boolean ctrl)
	{
		if(!canBeExtracted(20081, player))
			return;
		removeItem(player, 20081, 1);

		int[] items = {20091, 20088, 20085, 20090, 20087, 20084, 20089, 20086, 20083};
		int rnd = Rnd.get(10000);
		int index = 0;
		if(rnd < 333)
			index = 0;
		else if(rnd > 333 && rnd < 666)
			index = 1;
		else if(rnd > 666 && rnd < 999)
			index = 2;
		else if(rnd > 999 && rnd < 1999)
			index = 3;
		else if(rnd > 1999 && rnd < 2999)
			index = 4;
		else if(rnd > 2999 && rnd < 3999)
			index = 5;
		else if(rnd > 3999 && rnd < 5999)
			index = 6;
		else if(rnd > 5999 && rnd < 7999)
			index = 7;
		else
			index = 8;

		addItem(player, items[index], 1);
	}
	
	//No Grade Beginner's Adventurer Support Pack
	public static void ItemHandler_20635(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(20635, player))
			return;
		removeItem(player, 20635, 1);

		addItem(player, 8973, 1);
		addItem(player, 8977, 1);
		addItem(player, 9030, 1);
		addItem(player, 9031, 1);
		addItem(player, 9032, 1);
		addItem(player, 9033, 1);
		addItem(player, 9034, 1);
		addItem(player, 9035, 1);
		addItem(player, 21093, 3);
		addItem(player, 21094, 3);
	}
	
	//D-Grade Fighter Support Pack
	public static void ItemHandler_20636(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(20636, player))
			return;
		removeItem(player, 20636, 1);

		addItem(player, 20639, 1);
		addItem(player, 20640, 1);
		addItem(player, 20641, 1);
		addItem(player, 20642, 1);
		addItem(player, 20643, 1);
		addItem(player, 20644, 1);
		addItem(player, 20645, 1);
		addItem(player, 20646, 1);
		addItem(player, 20647, 1);
		addItem(player, 20648, 1);
	}
	
	//D-Grade Mage Support Pack
	public static void ItemHandler_20637(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(20637, player))
			return;
		removeItem(player, 20637, 1);
		
		addItem(player, 20649, 1);
		addItem(player, 20650, 1);
		addItem(player, 20651, 1);
		addItem(player, 20652, 1);
		addItem(player, 20653, 1);
		addItem(player, 20645, 1);
	}
	
	//Beginner's Adventurer Reinforcement Pack
	public static void ItemHandler_20638(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(20638, player))
			return;
		removeItem(player, 20638, 1);

		addItem(player, 20415, 1);
		addItem(player, 21091, 1);
		addItem(player, 21092, 1);
	}
	
	//Rune of Experience Points 50% 7-Day Pack
	public static void ItemHandler_21091(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21091, player))
			return;
		removeItem(player, 21091, 1);

		addItem(player, 20340, 1);
	}
	
	//Rune of SP 50% 7-Day Pack
	public static void ItemHandler_21092(L2Player player, Boolean ctrl)
	{
		if(player == null || !canBeExtracted(21092, player))
			return;
		removeItem(player, 21092, 1);

		addItem(player, 20346, 1);
	}

	/*private static L2Skill[] st_1 = {SkillTable.getInstance().getInfo(23019, 1)};
	private static L2Skill[] st_2 = {SkillTable.getInstance().getInfo(23019, 1)};
	private static L2Skill[] st_3 = {SkillTable.getInstance().getInfo(23019, 1)};
	private static L2Skill[] st_4 = {SkillTable.getInstance().getInfo(23019, 1)};
	private static L2Skill[] st_5 = {SkillTable.getInstance().getInfo(23019, 1)};
	private static L2Skill[] st_6 = {SkillTable.getInstance().getInfo(23019, 1)};

	public void ItemHandler_10254(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		int count = player.getVarInt("skill_count_1", 0)
		if(!canBeExtracted(10254, player) || count < 5)
			return;
		removeItem(player, 10254, 1);

		for(int i=0;i<st_1.length;i++)
		{
			if(count >= 5)
				break;
			if(Rnd.chance(100/st_1.length))
			{
				player.addSkill(st_1[i], true);
				count++;
				player.setVar("skill_count_1", count);
			}
		}
	}

	public void ItemHandler_10255(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		int count = player.getVarInt("skill_count_2", 0)
		if(!canBeExtracted(10255, player) || count < 5)
			return;
		removeItem(player, 10255, 1);

		for(int i=0;i<st_2.length;i++)
		{
			if(count >= 5)
				break;
			if(Rnd.chance(100/st_2.length))
			{
				player.addSkill(st_2[i], true);
				count++;
				player.setVar("skill_count_2", count);
			}
		}
	}

	public void ItemHandler_10256(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		int count = player.getVarInt("skill_count_3", 0)
		if(!canBeExtracted(10256, player) || count < 5)
			return;
		removeItem(player, 10256, 1);

		for(int i=0;i<st_3.length;i++)
		{
			if(count >= 5)
				break;
			if(Rnd.chance(100/st_3.length))
			{
				player.addSkill(st_3[i], true);
				count++;
				player.setVar("skill_count_3", count);
			}
		}
	}

	public void ItemHandler_10257(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		int count = player.getVarInt("skill_count_4", 0)
		if(!canBeExtracted(10257, player) || count < 5)
			return;
		removeItem(player, 10257, 1);

		for(int i=0;i<st_4.length;i++)
		{
			if(count >= 5)
				break;
			if(Rnd.chance(100/st_4.length))
			{
				player.addSkill(st_4[i], true);
				count++;
				player.setVar("skill_count_4", count);
			}
		}
	}

	public void ItemHandler_10258(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		int count = player.getVarInt("skill_count_5", 0)
		if(!canBeExtracted(10258, player) || count < 5)
			return;
		removeItem(player, 10258, 1);

		for(int i=0;i<st_5.length;i++)
		{
			if(count >= 5)
				break;
			if(Rnd.chance(100/st_5.length))
			{
				player.addSkill(st_5[i], true);
				count++;
				player.setVar("skill_count_5", count);
			}
		}
	}

	public void ItemHandler_10259(L2Player player, Boolean ctrl)
	{
		if(player == null)
			return;
		int count = player.getVarInt("skill_count_6", 0)
		if(!canBeExtracted(10259, player) || count < 5)
			return;
		removeItem(player, 10259, 1);

		for(int i=0;i<st_6.length;i++)
		{
			if(count >= 5)
				break;
			if(Rnd.chance(100/st_6.length))
			{
				player.addSkill(st_6[i], true);
				count++;
				player.setVar("skill_count_6", count);
			}
		}
	}*/
}