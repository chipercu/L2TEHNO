package l2open.gameserver.model;

import l2open.gameserver.model.base.ItemToDrop;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.util.GArray;

import java.util.Collection;

public class L2Drop
{
	public static final int MAX_CHANCE = 1000000;
	private GArray<L2DropGroup> _drop;
	private GArray<L2DropGroup> _spoil;

	public void addData(L2DropData d)
	{
		if(d.isSweep())
			addSpoil(d);
		else
			addDrop(d);
	}

	public void addDrop(L2DropData d)
	{
		if(_drop == null)
			_drop = new GArray<L2DropGroup>();
		if(_drop.size() != 0)
			for(L2DropGroup g : _drop)
				if(g.getId() == d.getGroupId())
				{
					g.addDropItem(d);
					return;
				}
		L2DropGroup temp = new L2DropGroup(d.getGroupId());
		temp.addDropItem(d);
		_drop.add(temp);
	}

	public void addSpoil(L2DropData s)
	{
		if(_spoil == null)
			_spoil = new GArray<L2DropGroup>();
		L2DropGroup temp = new L2DropGroup(0);
		temp.addDropItem(s);
		_spoil.add(temp);
	}

	public GArray<ItemToDrop> rollDrop(int diff, L2MonsterInstance monster, L2Player player, double mod, double mod_adena)
	{
		GArray<ItemToDrop> temp = new GArray<ItemToDrop>();
		if(_drop != null)
			for(L2DropGroup g : _drop)
			{
				if(g.isEpaulette() && monster != null && player != null && !monster.can_drop_epaulette(player))
					continue;
				Collection<ItemToDrop> tdl = g.roll(diff, false, monster, player, mod, mod_adena);
				if(tdl != null)
					for(ItemToDrop itd : tdl)
					{
						boolean add=true;
						if(true && itd.isStackable)
						{
							
							for(ItemToDrop itd_temp : temp)
								if(itd_temp.itemId == itd.itemId)
								{
									add=false;
									itd_temp.count+=itd.count;
								}
						}
						if(add)
							temp.add(itd);
					}
			}
		return temp;
	}

	public GArray<ItemToDrop> rollSpoil(int diff, L2MonsterInstance monster, L2Player player, double mod)
	{
		GArray<ItemToDrop> temp = new GArray<ItemToDrop>();
		if(_spoil != null)
			for(L2DropGroup g : _spoil)
			{
				Collection<ItemToDrop> tdl = g.roll(diff, true, monster, player, mod, mod);
				if(tdl != null)
					for(ItemToDrop itd : tdl)
						temp.add(itd);
			}
		return temp;
	}

	public GArray<ItemToDrop> rollChest(int diff, L2MonsterInstance monster, L2Player player, double mod)
	{
		GArray<ItemToDrop> temp = new GArray<ItemToDrop>();
		if(_spoil != null)
			for(L2DropGroup g : _spoil)
			{
				Collection<ItemToDrop> tdl = g.rollChest(diff, player, mod);
				if(tdl != null)
					for(ItemToDrop itd : tdl)
						temp.add(itd);
			}
		return temp;
	}

	public GArray<L2DropGroup> getSpoil()
	{
		return _spoil;
	}

	public GArray<L2DropGroup> getNormal()
	{
		return _drop;
	}

	public boolean validate()
	{
		if(_drop == null)
			return true; // Что бы не мусолило глаза...
		for(L2DropGroup g : _drop)
		{
			int sum_chance = 0; // сумма шансов группы
			for(L2DropData d : g.getDropItems(false))
				sum_chance += d.getChance();
			if(sum_chance <= MAX_CHANCE) // всё в порядке?
				return true;
			double mod = MAX_CHANCE / sum_chance;
			for(L2DropData d : g.getDropItems(false))
			{
				double group_chance = d.getChance() * mod; // коррекция шанса группы
				d.setChance(group_chance);
				g.setChance(MAX_CHANCE);
			}
		}
		return false;
	}
}