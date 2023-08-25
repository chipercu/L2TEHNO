package l2open.gameserver.model;

import l2open.config.*;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.base.ItemToDrop;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2ReflectionBossInstance;
import l2open.util.GArray;
import l2open.util.RateService;
import l2open.util.Rnd;
import l2open.util.Util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map.Entry;

public class L2DropGroup implements Cloneable
{
	private int _id;
	private double _chance;
	private boolean _isEpaulette = false;
	private boolean _isAdena = false; // Шанс фиксирован, растет только количество
	private boolean _fixedQty = false; // Вместо увеличения количества используется увеличение количества роллов группы
	private boolean _notRate = false; // Рейты вообще не применяются

	public boolean _isRate = true;
	public boolean _isPremium = true;

	private GArray<L2DropData> _items = new GArray<L2DropData>();

	public L2DropGroup(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public boolean fixedQty()
	{
		return _fixedQty;
	}

	public boolean notRate()
	{
		return _notRate;
	}

	public void addDropItem(L2DropData item)
	{
		_isRate = item.isRate();
		_isPremium = item.isPremium();

		if(item.getItem().isAdena())
			_isAdena = true;
		if(item.getItem().isRaidAccessory() || item.getItem().isArrow() || item.getItem().isHerb() || Util.contains(ConfigValue.NoRateDropList, item.getItemId()))
			_notRate = true;
		if(item.getItem().isEquipment() || item.getItem().isKeyMatherial() || Util.contains(ConfigValue.FixedRateDropList, item.getItemId()))
			_fixedQty = true;
			//_notRate = true;
				/*data.setNotRate((data.getItem().isArrow() // стрелы не рейтуются
				|| (Config.NO_RATE_EQUIPMENT && data.getItem().isEquipment()) // отключаемая рейтовка эквипа
				|| (Config.NO_RATE_KEY_MATERIAL && data.getItem().isKeyMatherial()) // отключаемая рейтовка ключевых материалов
				|| (Config.NO_RATE_RECIPES && data.getItem().isRecipe())), // отключаемая рейтовка рецептов
				ArrayUtils.contains(Config.NO_RATE_ITEMS, itemId)); // индивидуальная отключаемая однопроходная рейтовка для списка предметов
*/
		
		// 22839
		if(item.getItem().isEpaulette())
			_isEpaulette = true;
		item.setChanceInGroup(_chance);
		_chance += item.getChance();
		_items.add(item);
	}

	/**
	 * Возвращает список вещей или копию списка
	 */
	public GArray<L2DropData> getDropItems(boolean copy)
	{
		if(!copy)
			return _items;
		GArray<L2DropData> temp = new GArray<L2DropData>();
		temp.addAll(_items);
		return temp;
	}

	/**
	 * Возвращает полностью независимую копию группы
	 */
	@Override
	public L2DropGroup clone()
	{
		L2DropGroup ret = new L2DropGroup(_id);
		for(L2DropData i : _items)
			ret.addDropItem(i.clone());
		return ret;
	}

	/**
	 * Возвращает оригинальный список вещей если рейты не нужны или клон с примененными рейтами
	 */
	public GArray<L2DropData> getRatedItems(double rate, double mod, L2Player player, boolean epaulette)
	{
		if((mod*rate == 1 || _notRate) && !epaulette)
			return _items;
		GArray<L2DropData> ret = new GArray<L2DropData>();

		double calcChance=0;
		for(L2DropData i : _items)
			calcChance += i.getChance();
		for(L2DropData i : _items)
			ret.add(i.clone()); // создаем копию группы

		double perItemChance = 1000000. / ret.size();
		double gChance = 0;
		for(L2DropData i : ret)
		{
			if(i.getItem().isEpaulette() && player != null)
				rate = ConfigValue.RateDropEpaulette * player.getRateEpaulette();
			double avgQty = (i.getMinDrop() + i.getMaxDrop()) / 2.; // среднее количество дропа
			double newChance = rate * mod * i.getChance() * avgQty; // новый шанс группы плюс количество дропа, например 1-4 с шансом 43% при рейте 3х дадут ((1+4)/2)*0.43*3=3.225
			long avgCount = (long) Math.ceil(newChance / perItemChance); // новое количество дропа, допустим при количестве групп 3 пример выше даст 3.225/(1/3)=ceil(9.675)=10

			long min = avgCount, max = avgCount; // создаем некоторый разброс количества
			long shift = Math.min(Math.round(avgCount * 1. / 3.), avgCount - 1);
			if(shift > 0)
			{
				min -= shift;
				max += shift;
			}
			i.setMinDrop(min);
			i.setMaxDrop(max);

			i.setChance(newChance / avgCount); // новый шанс считается как полный новый шанс деленный на среднее количество, для примера выше 32.25%
			i.setChanceInGroup(gChance);
			gChance += i.getChance();
		}

		return ret;
	}

	/**
	 * Эта функция выбирает одну вещь из группы
	 * Используется в основном механизме расчета дропа
	 */
	public Collection<ItemToDrop> roll(int diff, boolean isSpoil, L2MonsterInstance monster, L2Player player, double mod, double mod_adena)
	{
		if(_isAdena)
			return rollAdena(diff, player, monster, mod_adena);
		if(isSpoil)
			return rollSpoil(diff, player, mod);
		if(monster.isRaid() || monster.isRefRaid() || _notRate || _fixedQty || !_isRate && _isPremium)
			return rollFixedQty(diff, monster, player, mod);
		if(_isEpaulette)
			return rollNormal(diff, monster, player, mod, true);
		// если множитель дропа большой разбивать дроп на кучки
		// количество итераций не более L2Drop.MAX_DROP_ITERATIONS

		double cmod = mod;
		if(monster.isEpicRaid())
			cmod *= (_isRate ? ConfigValue.RateEpicBoss : 1.) * (!ConfigValue.NoRaitDropRb && _isPremium ? player.getRateItems() : 1.);
		else if(monster.isRaid() || monster.isRefRaid())
			cmod *= (_isRate ? ConfigValue.RateRaidBoss : 1.) * (!ConfigValue.NoRaitDropRb && _isPremium ? player.getRateItems() : 1.);
		else
			cmod *= (_isRate ? RateService.getRateDropItems(player) : 1.) * (_isPremium ? player.getRateItems() : 1.);

		if(cmod > ConfigValue.RateBreakpoint)
		{
			long iters = Math.min((long) Math.ceil(cmod / ConfigValue.RateBreakpoint), ConfigValue.RateMaxIterations);
			GArray<ItemToDrop> ret = new GArray<ItemToDrop>();
			for(int i = 0; i < iters; i++)
				ret.addAll(rollNormal(diff, monster, player, mod / iters));
			return ret;
		}

		return rollNormal(diff, monster, player, mod);
	}

	public Collection<ItemToDrop> rollNormal(int diff, L2MonsterInstance monster, L2Player player, double mod)
	{
		return rollNormal(diff, monster, player, mod, false);
	}

	public Collection<ItemToDrop> rollNormal(int diff, L2MonsterInstance monster, L2Player player, double mod, boolean epaulette)
	{
		// Поправка на глубоко синих мобов
		if(ConfigValue.UseDeepBlueDropRules && diff > 0)
			mod *= Experience.penaltyModifier(diff, 9);
		if(mod <= 0)
			return null;

		double rate;
		if(monster.isEpicRaid())
			rate = (_isRate ? ConfigValue.RateEpicBoss : 1.) * (!ConfigValue.NoRaitDropRb && _isPremium ? player.getRateItems() : 1.);
		else if(monster.isRaid() || monster.isRefRaid())
			rate = (_isRate ? ConfigValue.RateRaidBoss : 1.) * (!ConfigValue.NoRaitDropRb && _isPremium ? player.getRateItems() : 1.);
		else
			rate = (_isRate ? RateService.getRateDropItems(player) : 1.) * (_isPremium ? player.getRateItems() : 1.);

		double calcChance = 0;
		double rollChance = 0;
		GArray<L2DropData> items;
		double mult = 1;

		items = getRatedItems(rate, mod, player, epaulette);

		calcChance = 0;
		// Считаем шанс группы
		for(L2DropData i : items)
			calcChance += i.getChance();
		rollChance = calcChance;
		if(!ConfigValue.DropV2)
			if(Rnd.get(1, L2Drop.MAX_CHANCE) > calcChance)
				return null;

		GArray<ItemToDrop> ret = new GArray<ItemToDrop>();
		rollFinal(items, ret, mult, rollChance, 4);
		return ret;
	}

	public Collection<ItemToDrop> rollFixedQty(int diff, L2MonsterInstance monster, L2Player player, double mod)
	{
		// Поправка на глубоко синих мобов
		if(ConfigValue.UseDeepBlueDropRules && diff > 0)
			mod *= Experience.penaltyModifier(diff, 9);
		if(mod <= 0)
			return null;
		double rate;
		if(_notRate)
			rate = Math.min(mod, 1);
		else if(monster.isEpicRaid())
			rate = _isRate ? ConfigValue.RateEpicBoss * mod : Math.min(mod, 1);
		else if(monster.isRaid() || monster.isRefRaid())
			rate = _isRate ? ConfigValue.RateRaidBoss * mod : Math.min(mod, 1);
		else
			rate = (_isRate ? RateService.getRateDropItems(player) : 1.) * (_isPremium ? player.getRateItems() : 1.) * mod;

		// Считаем шанс группы
		double calcChance = _chance * rate;
		Entry<Double, Integer> e = balanceChanceAndMult(calcChance);
		calcChance = e.getKey();
		int dropmult = !ConfigValue.DropFixedQty/* && _fixedQty*/ ? 1 : e.getValue();

		GArray<ItemToDrop> ret = new GArray<ItemToDrop>();
		for(int n = 0; n < dropmult; n++)
			if(Rnd.get(1, L2Drop.MAX_CHANCE) < calcChance)
			{
				rollFinal(_items, ret, 1, _chance, 3);
				if(ConfigValue.D1)
					return ret;
			}
		return ret;
	}

	private Collection<ItemToDrop> rollSpoil(int diff, L2Player player, double mod)
	{
		double rate = RateService.getRateDropSpoil(player) * player.getRateSpoil();
		// Поправка на глубоко синих мобов
		if(ConfigValue.UseDeepBlueDropRules && diff > 0)
			mod *= Experience.penaltyModifier(diff, 9);
		if(mod <= 0)
			return null;

		// Считаем шанс группы
		double calcChance = _chance * rate * mod;
		Entry<Double, Integer> e = balanceChanceAndMult(calcChance);
		calcChance = e.getKey();
		int dropmult = e.getValue();
		if(!ConfigValue.DropV2)
			if(Rnd.get(1, L2Drop.MAX_CHANCE) > calcChance)
				return null;

		GArray<ItemToDrop> ret = new GArray<ItemToDrop>(1);
		rollFinal(_items, ret, dropmult*ConfigValue.RateCountDropSpoil, _chance, 2);
		return ret;
	}

	public Collection<ItemToDrop> rollChest(int diff, L2Player player, double mod)
	{
		float rate = ConfigValue.RateDropChest * player.getRateChest();
		// Поправка на глубоко синих мобов
		if(ConfigValue.UseDeepBlueDropRules && diff > 0)
			mod *= Experience.penaltyModifier(diff, 9);
		if(mod <= 0)
			return null;

		// Считаем шанс группы
		double calcChance = _chance * rate * mod;
		Entry<Double, Integer> e = balanceChanceAndMult(calcChance);
		calcChance = e.getKey();
		int dropmult = e.getValue();
		if(!ConfigValue.DropV2)
			if(Rnd.get(1, L2Drop.MAX_CHANCE) > calcChance)
				return null;

		GArray<ItemToDrop> ret = new GArray<ItemToDrop>(1);
		rollFinal(_items, ret, dropmult, _chance, 1);
		return ret;
	}

	private Collection<ItemToDrop> rollAdena(int diff, L2Player player, L2MonsterInstance monster, double mod_adena)
	{
		if(ConfigValue.UseDeepBlueDropRules && diff > 0)
			mod_adena *= Experience.penaltyModifier(diff, 9);
		double chance = _chance;
		if(mod_adena > 10)
		{
			mod_adena *= _chance / L2Drop.MAX_CHANCE;
			chance = L2Drop.MAX_CHANCE;
		}
		if(!ConfigValue.DropV2)
			if(mod_adena <= 0 || Rnd.get(1, L2Drop.MAX_CHANCE) > chance)
				return null;
		double mult = mod_adena * ConfigSystem.getRateAdena(player);
		if(ConfigValue.NoRaitDropRb && (monster.isEpicRaid() || monster.isRaid() || monster.isRefRaid()))
			mult = mod_adena*ConfigValue.RateDropAdena*ConfigValue.RateDropAdenaMultMod + ConfigValue.RateDropAdenaStaticMod;

		GArray<ItemToDrop> ret = new GArray<ItemToDrop>(1);
		rollFinal(_items, ret, mult, _chance, 0);
		for(ItemToDrop i : ret)
			i.isAdena = true;
		return ret;
	}

	public static Entry<Double, Integer> balanceChanceAndMult(Double calcChance)
	{
		Integer dropmult = 1;
		if(calcChance > L2Drop.MAX_CHANCE)
		{
			if(calcChance % L2Drop.MAX_CHANCE == 0) // если кратен 100% то тупо умножаем количество
				dropmult = (int) (calcChance / L2Drop.MAX_CHANCE);
			else
				// иначе балансируем
				dropmult = (int) Math.ceil(calcChance / L2Drop.MAX_CHANCE); // множитель равен шанс / 100% округление вверх
			calcChance = calcChance / dropmult; // шанс равен шанс / множитель
			// в результате получаем увеличение количества и уменьшение шанса, при этом шанс не падает ниже 50%
		}
		return new SimpleEntry<Double, Integer>(calcChance, dropmult);
	}

	private void rollFinal2(GArray<L2DropData> items, GArray<ItemToDrop> ret, double mult, double chanceSum, int type)
	{
		// перебираем все вещи в группе и проверяем шанс
		for(L2DropData i : items)
		{
			if(Rnd.get(0, L2Drop.MAX_CHANCE) < (i.getChance()*(type == 2 ? ConfigValue.SpoilChanceMod : ConfigValue.DropChanceMod)))
			{
				ItemToDrop t = new ItemToDrop(i.getItemId());
				t.isStackable = i.getItem().isStackable();

				if(type == 2 && ConfigValue.SpoilMinRate)
				{
					t.count = Rnd.get(Math.round(i.getMinDrop()), Math.round(i.getMaxDrop() * mult));
				}
				else
				{
					if(i.getMinDrop() >= i.getMaxDrop())
						t.count = Math.round(i.getMinDrop() * mult);
					else
						t.count = Rnd.get(Math.round(i.getMinDrop() * mult), Math.round(i.getMaxDrop() * mult));
				}

				ret.add(t);
			}
		}
	}
	private void rollFinal(GArray<L2DropData> items, GArray<ItemToDrop> ret, double mult, double chanceSum, int type)
	{
		if(ConfigValue.DropV2)
		{
			rollFinal2(items, ret, mult, chanceSum, type);
			return;
		}
		// перебираем все вещи в группе и проверяем шанс
		int chance = Rnd.get(0, (int) chanceSum);
		for(L2DropData i : items)
		{
			if(chance < i.getChanceInGroup())
				continue;
			boolean notlast = false;
			for(L2DropData t : items)
			{
				if(t.getChanceInGroup() > i.getChanceInGroup() && chance > t.getChanceInGroup())
				{
					notlast = true;
					break;
				}
			}
			if(notlast)
				continue;

			ItemToDrop t = new ItemToDrop(i.getItemId());
			t.isStackable = i.getItem().isStackable();

			if(type == 2 && ConfigValue.SpoilMinRate)
			{
				t.count = Rnd.get(Math.round(i.getMinDrop()), Math.round(i.getMaxDrop() * mult));
			}
			else
			{
				if(i.getMinDrop() >= i.getMaxDrop())
					t.count = Math.round(i.getMinDrop() * mult);
				else
					t.count = Rnd.get(Math.round(i.getMinDrop() * mult), Math.round(i.getMaxDrop() * mult));
			}

			ret.add(t);
			break;
		}
	}

	public double getChance()
	{
		return _chance;
	}

	public void setChance(double chance)
	{
		_chance = chance;
	}

	public boolean isAdena()
	{
		return _isAdena;
	}

	public boolean isEpaulette()
	{
		return _isEpaulette;
	}
}