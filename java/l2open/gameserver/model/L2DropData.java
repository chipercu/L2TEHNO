package l2open.gameserver.model;

import l2open.config.*;
import l2open.gameserver.model.base.ItemToDrop;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.RateService;
import l2open.util.Rnd;

public class L2DropData implements Cloneable
{
	private L2Item _item;
	private long _mindrop;
	private long _maxdrop;
	private boolean _sweep;
	private boolean _is_rate=true;
	private boolean _is_premium=true;
	private double _chance;
	private double _chanceInGroup;
	private int _groupId;
	private int _minLevel;
	private int _maxLevel;

	public L2DropData()
	{}

	public L2DropData(int id, long min, long max, double chance)
	{
		this(id, min, max, chance, 0, 0);
	}

	public L2DropData(int id, long min, long max, double chance, int minLevel)
	{
		this(id, min, max, chance, minLevel, 0);
	}

	public L2DropData(int id, long min, long max, double chance, int minLevel, int maxLevel)
	{
		_item = ItemTemplates.getInstance().getTemplate(id);
		_mindrop = min;
		_maxdrop = max;
		_chance = chance;
		_minLevel = minLevel;
		_maxLevel = maxLevel;
	}

	@Override
	public L2DropData clone()
	{
		return new L2DropData(getItemId(), getMinDrop(), getMaxDrop(), getChance(), getMinLevel(), getMaxLevel());
	}

	public int getItemId()
	{
		return _item.getItemId();
	}

	public L2Item getItem()
	{
		return _item;
	}

	public String getName()
	{
		return getItem().getName();
	}

	public void setItemId(int itemId)
	{
		_item = ItemTemplates.getInstance().getTemplate(itemId);
	}

	public void setGroupId(int gId)
	{
		_groupId = gId;
	}

	public int getGroupId()
	{
		return _groupId;
	}

	public long getMinDrop()
	{
		return _mindrop;
	}

	public long getMaxDrop()
	{
		return _maxdrop;
	}

	public boolean isSweep()
	{
		return _sweep;
	}

	public boolean isRate()
	{
		return _is_rate;
	}

	public boolean isPremium()
	{
		return _is_premium;
	}

	public double getChance()
	{
		return _chance;
	}

	public void setMinDrop(long mindrop)
	{
		_mindrop = mindrop;
	}

	public void setMaxDrop(long maxdrop)
	{
		_maxdrop = maxdrop;
	}

	public void setSweep(boolean sweep)
	{
		_sweep = sweep;
	}

	public void setIsRate(boolean value)
	{
		_is_rate = value;
	}

	public void setIsPremium(boolean value)
	{
		_is_premium = value;
	}

	public void setChance(double chance)
	{
		_chance = chance;
	}

	public void setChanceInGroup(double chance)
	{
		_chanceInGroup = chance;
	}

	public double getChanceInGroup()
	{
		return _chanceInGroup;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	@Override
	public String toString()
	{
		return "ItemID: " + getItem() + " Min: " + getMinDrop() + " Max: " + getMaxDrop() + " Chance: " + getChance() / 10000.0 + "%";
	}

	public String toString(double mult)
	{
		return "ItemID: " + getItem() + " Min: " + getMinDrop()*mult + " Max: " + getMaxDrop()*mult + " Chance: " + getChance()*mult / 10000.0 + "%";
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof L2DropData)
		{
			L2DropData drop = (L2DropData) o;
			return drop.getItemId() == getItemId();
		}
		return false;
	}

	/**
	 * Подсчет шанса выпадения этой конкретной вещи
	 * Используется в эвентах и некоторых специальных механизмах
	 * @param player игрок (его бонус влияет на шанс)
	 * @param mod (просто множитель шанса)
	 * @return информация о выпавшей вещи
	 */
	 // Используется только в ивенте TheFallHarvest
	public GArray<ItemToDrop> roll(L2Player player, double mod, boolean isRaid)
	{
		double rate = (isRaid ? ConfigValue.RateRaidBoss : RateService.getRateDropItems(player)) * (player != null ? player.getRateItems() : 1);
		double adenarate = isRaid ? ConfigValue.RateRaidBoss * (player != null ? player.getRateAdena() : 1) : ConfigSystem.getRateAdena(player);
		float rateEpaulette = ConfigValue.RateDropEpaulette * (player != null ? player.getRateEpaulette() : 1);


		// calc group chance
		double calcChance = mod * _chance * (_item.isAdena() ? 1d : _item.isEpaulette() ? rateEpaulette : rate);

		int dropmult = 1;
		// Если шанс оказался больше 100%
		if(calcChance > L2Drop.MAX_CHANCE)
			if(calcChance % L2Drop.MAX_CHANCE == 0) // если кратен 100% то тупо умножаем количество
				dropmult = (int) (calcChance / L2Drop.MAX_CHANCE);
			else
			{ // иначе балансируем
				dropmult = (int) Math.ceil(calcChance / L2Drop.MAX_CHANCE); // множитель равен шанс / 100% округление вверх
				calcChance = calcChance / dropmult; // шанс равен шанс / множитель
				// в результате получаем увеличение количества и уменьшение шанса, при этом шанс не падает ниже 50%
			}

		GArray<ItemToDrop> ret = new GArray<ItemToDrop>();

		for(int i = 1; i <= dropmult; i++)
		{
			if(Rnd.get(L2Drop.MAX_CHANCE) > calcChance)
				continue;

			ItemToDrop t = new ItemToDrop(_item.getItemId());
			t.isStackable = _item.isStackable();

			// если это адена то умножаем на рейт адены, иначе на множитель перебора шанса
			double mult = _item.isAdena() ? adenarate : 1;

			if(getMinDrop() >= getMaxDrop())
				t.count = (long) (getMinDrop() * mult);
			else
				t.count = Rnd.get((long) (getMinDrop() * mult), (long) (getMaxDrop() * mult));

			ret.add(t);
		}
		return ret;
	}

	@Override
	public int hashCode()
	{
		return _item.getItemId();
	}
}