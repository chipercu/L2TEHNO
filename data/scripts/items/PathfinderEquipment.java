package items;

import java.util.HashMap;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2PathfinderInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item.Grade;
import l2open.util.GArray;
import l2open.util.Rnd;

public class PathfinderEquipment implements IItemHandler, ScriptFile
{
	private static int[] _itemIds = null;
	private static HashMap<Integer, Grade[]> rew_table = null;

	private static final int[][] enchants = {
	//
			{
			// NG
			}, {
			// D
					955, // Scroll: Enchant Weapon
					956, // Scroll: Enchant Armor
			}, {
			// C
					951, // Scroll: Enchant Weapon
					952, // Scroll: Enchant Armor
			}, {
			// B
					947, // Scroll: Enchant Weapon
					948, // Scroll: Enchant Armor
			}, {
			// A
					729, // Scroll: Enchant Weapon
					730, // Scroll: Enchant Armor
			}, {
			// S
					959, // Scroll: Enchant Weapon
					960, // Scroll: Enchant Armor
			},

	};

	static final int[][][] rewards = {
	//
			{
			// NG
			}, {
			// D
					{ 1463, 300 }, // SS
					{ 1539, 4 }, // Greater Healing Potion 
					{ 2510, 300 }, // SPS
					{ 8623, 3 }, // Elixir of Life
					{ 8629, 3 }, // Elixir of Mental Strength
			}, {
			// C
					{ 1464, 300 }, // SS
					{ 1539, 4 }, // Greater Healing Potion
					{ 2511, 300 }, // SPS
					{ 8624, 3 }, // Elixir of Life
					{ 8630, 3 }, // Elixir of Mental Strength
			}, {
			// B
					{ 1465, 100 }, // SS
					{ 1539, 4 }, // Greater Healing Potion
					{ 2512, 100 }, // SPS
					{ 8625, 3 }, // Elixir of Life
					{ 8631, 3 }, // Elixir of Mental Strength
			}, {
			// A
					{ 1466, 100 }, // SS
					{ 1539, 4 }, // Greater Healing Potion
					{ 2513, 100 }, // SPS
					{ 8626, 3 }, // Elixir of Life
					{ 8632, 3 }, // Elixir of Mental Strength
			}, {
			// S
					{ 1467, 100 }, // SS
					{ 1539, 4 }, // Greater Healing Potion
					{ 2514, 100 }, // SPS
					{ 8627, 3 }, // Elixir of Life
					{ 8633, 3 }, // Elixir of Mental Strength
			}, };

	public PathfinderEquipment()
	{
		rew_table = new HashMap<Integer, Grade[]>();
		for(int i = 0; i < L2PathfinderInstance.boxes.length; i++)
			for(int j = 0; j < L2PathfinderInstance.boxes[i].length; j++)
				if(L2PathfinderInstance.boxes[i][j] > 0)
				{
					Grade[] asc = { Grade.values()[i], Grade.values()[j] };
					rew_table.put(L2PathfinderInstance.boxes[i][j], asc);
				}

		GArray<Integer> temp = new GArray<Integer>();
		for(int[] l1 : L2PathfinderInstance.boxes)
			for(int l2 : l1)
				if(l2 > 0)
					temp.add(l2);
		_itemIds = new int[temp.size()];
		for(int i = 0; i < temp.size(); i++)
			_itemIds[i] = temp.get(i);
	}

	public synchronized void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		Grade[] grade = rew_table.get(item.getItemId());
		player.getInventory().destroyItem(item, 1, true);

		double mult = 1;
		switch(grade[1])
		{
			case S:
				mult = 3;
				break;
			case A:
				mult = 2.5;
				break;
			case B:
				mult = 2;
				break;
			case C:
				mult = 1.5;
				break;
		}

		if(Rnd.chance(40 * mult))
			Functions.addItem(player, enchants[grade[0].externalOrdinal][Rnd.chance(5 * mult) ? 0 : 1], 1);
		for(int[] ent : rewards[grade[0].externalOrdinal])
			Functions.addItem(player, ent[0], (int) (mult * ent[1]));

	}

	public int[] getItemIds()
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