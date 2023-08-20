package l2open.gameserver.model;

import l2open.config.ConfigValue;
import l2open.config.ConfigSystem;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.instances.L2ChestInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.RateService;
import l2open.util.Rnd;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.logging.Logger;

/**
 * Created by Michał on 04.12.13.
 */
public class CalculateRewardChances
{
	private static final Logger _log = Logger.getLogger(CalculateRewardChances.class.getName());

	public static final double CORRECT_CHANCE_TRIES = 10000.0;
	private static final Map<Integer, Integer[]> droplistsCountCache = new HashMap<>();
	private static final Map<String, String> correctedChances = new HashMap<>();

	public static List<L2NpcTemplate> getNpcsContainingString(CharSequence name)
	{
		List<L2NpcTemplate> templates = new ArrayList<>();

		for (L2NpcTemplate template : NpcTable.getAll())
			if (templateExists(template) && StringUtils.containsIgnoreCase(template.name, name))
				if (isDroppingAnything(template))
					templates.add(template);

		return templates;
	}

	public static int getDroplistsCountByItemId(int itemId, boolean drop)
	{
		if (droplistsCountCache.containsKey(itemId))
			if (drop)
				return droplistsCountCache.get(itemId)[0].intValue();
			else
				return droplistsCountCache.get(itemId)[1].intValue();

		int dropCount = 0;
		int spoilCount = 0;
		for (L2NpcTemplate template : NpcTable.getAll())
			if (templateExists(template) && template.getDropData() != null)
			{
				if(template.getDropData().getNormal() != null)
					for(L2DropGroup dg : template.getDropData().getNormal())
						for(L2DropData dd : dg.getDropItems(false))
							if(dd.getItemId() == itemId)
								dropCount++;

				if(template.getDropData().getSpoil() != null)
					for(L2DropGroup dg : template.getDropData().getSpoil())
						for(L2DropData dd : dg.getDropItems(false))
							if(dd.getItemId() == itemId)
								spoilCount++;
			}

		droplistsCountCache.put(itemId, new Integer[] {dropCount, spoilCount});

		if (drop)
			return dropCount;
		else
			return spoilCount;
	}
	
	private static boolean templateExists(L2NpcTemplate template)
	{
		if (template == null)
			return false;
		//if (L2ObjectsStorage.getAllByNpcId(template.getNpcId(), false).size() == 0)
		//	return false;
		return true;
	}

	public static boolean isItemDroppable(int itemId)
	{
		if (!droplistsCountCache.containsKey(itemId))
			getDroplistsCountByItemId(itemId, true);

		return droplistsCountCache.get(itemId)[0].intValue() > 0 || droplistsCountCache.get(itemId)[1].intValue() > 0;
	}

	public static List<L2Item> getDroppableItems()
	{
		List<L2Item> items = new ArrayList<>();
		for (L2NpcTemplate template : NpcTable.getAll())
			if (templateExists(template) && template.getDropData() != null)
			{
				if(template.getDropData().getNormal() != null)
					for(L2DropGroup dg : template.getDropData().getNormal())
						for(L2DropData dd : dg.getDropItems(false))
							if(!items.contains(dd.getItem()))
								items.add(dd.getItem());

				if(template.getDropData().getSpoil() != null)
					for(L2DropGroup dg : template.getDropData().getSpoil())
						for(L2DropData dd : dg.getDropItems(false))
							if(!items.contains(dd.getItem()))
								items.add(dd.getItem());
			}
		return items;
	}

	/**
	 * Key: 0 - Drop, 1 - Spoil
	 * @param itemId
	 * @return
	 */
	public static List<NpcTemplateDrops> getNpcsByDropOrSpoil(int itemId)
	{
		List<NpcTemplateDrops> templates = new ArrayList<>();
		for (L2NpcTemplate template : NpcTable.getAll())
		{
			if (template == null)
				continue;
			//if (L2ObjectsStorage.getAllByNpcId(template.getNpcId(), false).size() == 0)
			//	continue;
			
			boolean[] dropSpoil = templateContainsItemId(template, itemId);

			if (dropSpoil[0])
				templates.add(new NpcTemplateDrops(template, true));
			if (dropSpoil[1])
				templates.add(new NpcTemplateDrops(template, false));
		}
		return templates;
	}

	public static class NpcTemplateDrops
	{
		public L2NpcTemplate template;
		public boolean dropNoSpoil;
		private NpcTemplateDrops(L2NpcTemplate template, boolean dropNoSpoil)
		{
			this.template = template;
			this.dropNoSpoil = dropNoSpoil;
		}
	}

	private static boolean[] templateContainsItemId(L2NpcTemplate template, int itemId)
	{
		boolean[] dropSpoil = {false, false};

		if(template.getDropData() != null)
		{
			if(template.getDropData().getNormal() != null)
				for(L2DropGroup dg : template.getDropData().getNormal())
					for(L2DropData dd : dg.getDropItems(false))
						if(dd.getItemId() == itemId)
						{
							dropSpoil[0] = true;
							break;
						}

			if(template.getDropData().getSpoil() != null)
				for(L2DropGroup dg : template.getDropData().getSpoil())
					for(L2DropData dd : dg.getDropItems(false))
						if(dd.getItemId() == itemId)
						{
							dropSpoil[1] = true;
							break;
						}
		}
		return dropSpoil;
	}

	private static boolean isDroppingAnything(L2NpcTemplate template)
	{
		if(template.getDropData() != null)
		{
			if(template.getDropData().getNormal() != null && template.getDropData().getNormal().size() > 0)
				return true;
			if(template.getDropData().getSpoil() != null && template.getDropData().getSpoil().size() > 0)
				return true;
		}
		return false;
	}
	
	public static List<L2DropData> getDrops(L2NpcTemplate template, boolean drop, boolean spoil)
	{
		List<L2DropData> allRewards = new ArrayList<>();
		if (template == null)
			return allRewards;

		if(drop && template.getDropData().getNormal() != null)
			for(L2DropGroup dg : template.getDropData().getNormal())
				for(L2DropData dd : dg.getDropItems(false))
					allRewards.add(dd);

		if(spoil && template.getDropData().getSpoil() != null)
			for(L2DropGroup dg : template.getDropData().getSpoil())
				for(L2DropData dd : dg.getDropItems(false))
					allRewards.add(dd);

		return allRewards;
	}

	public static String getDropChance(L2Player player, L2NpcTemplate npc, boolean dropNoSpoil, int itemId)
	{
		TypeGroupData info = getGroupAndData(npc, dropNoSpoil, itemId);

		if (info == null)
			return "0";

		double mod = Experience.penaltyModifier((long) calculateLevelDiffForDrop(npc.level, player.getLevel(), false), 9.0);
		double baseRate = 1.0;
		double playerRate = 1.0;
		if (info.type == RewardType.SWEEP)
		{
			baseRate = ConfigValue.RateDropSpoil;
			playerRate = player.getRateSpoil();
		}
		else if (info.type == RewardType.RATED_GROUPED)
		{
			if (info.group.isAdena())
				return getAdenaChance(info, mod);
			if (npc.isRaid)
				return getItemChance(info, mod, ConfigValue.RateRaidBoss, 1.0);

			baseRate = ConfigValue.RateDropItems;
			playerRate = player.getRateItems();
		}

		return getItemChance(info, mod, baseRate, playerRate);
	}

	private static String getAdenaChance(TypeGroupData info, double mod)
	{
		if (mod <= 0)
			return "0";

		double groupChance = info.group.getChance();
		if (mod > 10)
		{
			groupChance = (double) L2Drop.MAX_CHANCE;
		}

		double itemChance = info.data.getChance();

		groupChance /= (double) L2Drop.MAX_CHANCE;
		itemChance /= (double) L2Drop.MAX_CHANCE;
		double finalChance = groupChance*itemChance;
		return String.valueOf(finalChance*100);
	}

	private static String getItemChance(TypeGroupData info, double mod, double baseRate, double playerRate)
	{
		if (mod <= 0.0)
			return "0";

		double rate;
		if (info.group.notRate())
			rate = Math.min(mod, 1.0);
		else
			rate = baseRate * playerRate * mod;

		double mult = Math.ceil(rate);

		BigDecimal totalChance = BigDecimal.valueOf(0.0);
		for (double n = 0.0; n < mult; n++)
		{
			BigDecimal groupChance = BigDecimal.valueOf(info.group.getChance() * Math.min(rate - n, 1.0));
			BigDecimal itemChance = BigDecimal.valueOf(info.data.getChance());
			groupChance = groupChance.divide(BigDecimal.valueOf((long) L2Drop.MAX_CHANCE));
			itemChance = itemChance.divide(BigDecimal.valueOf((long) L2Drop.MAX_CHANCE));
			totalChance = totalChance.add(groupChance.multiply(itemChance));
		}
		String totalChanceString = totalChance.multiply(BigDecimal.valueOf(100.0)).toString();
		
		return getCorrectedChance(totalChanceString, info.group.getChance()/10000.0, info.data.getChance()/10000.0, mult);
	}
	
	private static String getCorrectedChance(String totalChanceString, double groupChance, double itemChance, 
	                                         double mult)
	{
		Comparable<BigDecimal> totalChance = new BigDecimal(totalChanceString);
		if (totalChance.compareTo(BigDecimal.valueOf(5.0)) < 0)
			return totalChance.toString();
		
		if (correctedChances.containsKey(totalChanceString))
			return correctedChances.get(totalChanceString);
		
		double totalPassed = 0.0;
		double x;
		for (double i = 0.0;i<CORRECT_CHANCE_TRIES;i++)
		{
			for (x = 0.0; x <mult;x++)
			{
				if (Rnd.chance(groupChance))
					if (Rnd.chance(itemChance))
					{
						totalPassed++;
						break;
					}
			}
		}
		String finalValue = String.valueOf(totalPassed / (CORRECT_CHANCE_TRIES / 100.0));
		correctedChances.put(totalChanceString, finalValue);
		return finalValue;
	}

	private static class TypeGroupData
	{
		private final RewardType type;
		private final L2DropGroup group;
		private final L2DropData data;
		private TypeGroupData(RewardType type, L2DropGroup group, L2DropData data)
		{
			this.type = type;
			this.group = group;
			this.data = data;
		}
	}

	private static long[] drop_null = new long[]{0L,0L,0L};
	public static long[] getDropCounts(L2Player player, L2NpcTemplate template, boolean dropNoSpoil, int itemId)
	{
		TypeGroupData info = getGroupAndData(template, dropNoSpoil, itemId);

		if (info == null)
			return drop_null;

		int diff = CalculateRewardChances.calculateLevelDiffForDrop(template.level, player.isInParty() ? player.getParty().getLevel() : player.getLevel(), false);
		double mod = 1;
		if(diff > 0)
			mod = Experience.penaltyModifier(diff, 9);

		double GCHANCE = 0; // шанс группы, по сути сумма шансов вещей в ней, в расчетах используется только для фиксированного количества
		double dropmult = 1; // множитель количества дропа, только для адены или фиксированного количества (canIterate)
		double chancemult; // множитель шанса дропа, только для фиксированного количества (canIterate)
		// ------------------------------------------------------------------------------------------------------------------------
		L2MonsterInstance monster = null;
		/*List<L2NpcInstance> aliveInstance = L2ObjectsStorage.getAllByNpcId(template.getNpcId(), false);
		for(L2NpcInstance npcs : aliveInstance)
			if(npcs != null && npcs.isMonster())
				{
					monster = (L2MonsterInstance)npcs;
					if(monster.getChampion() <= 0)
						break;
				}*/
		mod = monster == null ? mod : monster.calcStat(Stats.DROP, mod, player, null);

		if (info.type == RewardType.SWEEP)
		{
			double rateSpoil = mod * RateService.getRateDropSpoil(player) * (player != null ? player.getRateSpoil() : 1);
			double rateChest = mod * ConfigValue.RateDropChest * (player != null ? player.getRateChest() : 1);
			if(rateSpoil > 0 || (rateChest > 0 && isChest(template.getNpcId(), monster)))
			{
				Entry<Double, Integer> e = L2DropGroup.balanceChanceAndMult(info.data.getChance() * (isChest(template.getNpcId(), monster) ? rateChest : rateSpoil));
				GCHANCE = e.getKey() / 1000000;
				dropmult = e.getValue();

				if(ConfigValue.SpoilMinRate)
					return new long[] {Math.round(info.data.getMinDrop()), Math.round(info.data.getMaxDrop() * dropmult * ConfigValue.RateCountDropSpoil)};
				else
					return new long[] {Math.round(info.data.getMinDrop() * dropmult * ConfigValue.RateCountDropSpoil), Math.round(info.data.getMaxDrop() * dropmult * ConfigValue.RateCountDropSpoil)};
			}
			return drop_null;
		}
		else
		{
			double mod_adena = monster == null ? 1 : monster.calcStat(Stats.ADENA, 1., player, null);
			// ------------------------------------------------------------------------------------------------------------------------
			if(info.group.isAdena())
			{
				dropmult = mod_adena * ConfigSystem.getRateAdena(player);
				if(ConfigValue.NoRaitDropRb && (monster != null && (monster.isEpicRaid() || monster.isRefRaid()) || template.isRaid))
					dropmult = mod_adena*ConfigValue.RateDropAdena*ConfigValue.RateDropAdenaMultMod + ConfigValue.RateDropAdenaStaticMod;

				if(dropmult == 0)
					return drop_null;
				else if(info.group.notRate()) // фактически только эпики
				{
					/*mod = Math.min(1, mod); // модификатор не может быть положительным, шанс может только уменьшиться
					GCHANCE = info.group.getChance() * mod; // на шанс влияет только модификатор уровня
					chancemult = mod;*/
					dropmult = 1;
				}
				/*else
				{
					GCHANCE = info.group.getChance(); // шанс жестко фиксирован
					chancemult = 1;
				}*/
			}
			else
			{
				double rateDrop = mod;
				if(monster != null && monster.isEpicRaid())
					rateDrop *= (info.group._isRate ? ConfigValue.RateEpicBoss : 1.) * (player != null && !ConfigValue.NoRaitDropRb && info.group._isPremium ? player.getRateItems() : 1);
				else if(template.isRaid || monster != null && monster.isRefRaid())
					rateDrop *= (info.group._isRate ? ConfigValue.RateRaidBoss : 1.) * (player != null && !ConfigValue.NoRaitDropRb && info.group._isPremium ? player.getRateItems() : 1);
				else
					rateDrop *= (info.group._isRate ? RateService.getRateDropItems(player) : 1.) * (player != null && info.group._isPremium ? player.getRateItems() : 1);

				if(!info.group.isAdena() && !info.group.isEpaulette() && rateDrop == 0)
					return drop_null;

				if(template.isRaid || info.group.fixedQty() || info.group.notRate() || !info.group._isRate && info.group._isPremium) // моб чамп/рейд или дроп экипировки/кеев
				{
					if(info.group.notRate()) // фактически только эпики
					{
						/*mod = Math.min(1, mod); // модификатор не может быть положительным, шанс может только уменьшиться
						GCHANCE = info.group.getChance() * mod; // на шанс влияет только модификатор уровня
						chancemult = mod;*/
						dropmult = 1;
					}
					else
					{
						if(info.group.notRate())
							rateDrop = Math.min(mod, 1);
						else if(monster != null && monster.isEpicRaid())
							rateDrop = info.group._isRate ? ConfigValue.RateEpicBoss * mod : Math.min(mod, 1);
						else if(monster != null && (monster.isRaid() || monster.isRefRaid()))
							rateDrop = info.group._isRate ? ConfigValue.RateRaidBoss * mod : Math.min(mod, 1);
						else
							rateDrop = (info.group._isRate ? RateService.getRateDropItems(player) : 1.) * (info.group._isPremium ? player.getRateItems() : 1.) * mod;

						GCHANCE = info.group.getChance() * rateDrop; // в шансе группы берем рейт дропа
						Entry<Double, Integer> balanced = L2DropGroup.balanceChanceAndMult(GCHANCE); // балансируем шанс и количество так чтобы не зашкаливать за 100% 
						chancemult = balanced.getKey() / info.group.getChance();
						GCHANCE = balanced.getKey();
						dropmult = !ConfigValue.DropFixedQty/* && _fixedQty*/ ? 1 : balanced.getValue();
					}
				}
				else if(info.group.isEpaulette())
				{
					double rateEpaulette = mod * ConfigValue.RateDropEpaulette * (player != null ? player.getRateEpaulette() : 1);
					if(rateEpaulette == 0 || (monster != null && player != null && !monster.can_drop_epaulette(player)))
						return drop_null;

					if(info.group.notRate()) // фактически только эпики
					{
						/*mod = Math.min(1, mod); // модификатор не может быть положительным, шанс может только уменьшиться
						GCHANCE = info.group.getChance() * mod; // на шанс влияет только модификатор уровня
						chancemult = mod;*/
						dropmult = 1;
					}
					else if(rateEpaulette > ConfigValue.RateBreakpoint)
					{
						dropmult = Math.min(Math.ceil(rateEpaulette / ConfigValue.RateBreakpoint), ConfigValue.RateMaxIterations);
						rateEpaulette /= dropmult;
					}
					else
					{
						dropmult = 1; // уже учтено в механизме
					}
					/*items = info.group.getRatedItems(rateEpaulette, 1, player, true); // стандартный балансирующий механизм обработки рейтов для дропа
					chancemult = 1; // уже учтено в механизме
					GCHANCE = 0;
					for(L2DropData i : items)
						GCHANCE += i.getChance(); // шанс группы пересчитываем*/
				}
				else
				// все остальные случаи - моб обычный, дроп всякой фигни
				{
					if(info.group.notRate()) // фактически только эпики
					{
						/*mod = Math.min(1, mod); // модификатор не может быть положительным, шанс может только уменьшиться
						GCHANCE = info.group.getChance() * mod; // на шанс влияет только модификатор уровня
						chancemult = mod;*/
						dropmult = 1;
					}
					else
					{
						double new_rateDrop=rateDrop;
						if(new_rateDrop > ConfigValue.RateBreakpoint)
						{
							dropmult = Math.min(Math.ceil(new_rateDrop / ConfigValue.RateBreakpoint), ConfigValue.RateMaxIterations);
							new_rateDrop /= dropmult;
						}
						else
						{
							dropmult = 1; // уже учтено в механизме
						}
						/*items = info.group.getRatedItems(new_rateDrop, 1, player, false); // стандартный балансирующий механизм обработки рейтов для дропа
						chancemult = 1; // уже учтено в механизме
						GCHANCE = 0;
						for(L2DropData i : items)
							GCHANCE += i.getChance(); // шанс группы пересчитываем*/
					}
				}
			}
		}
		return new long[] {Math.round(info.data.getMinDrop() * dropmult), Math.round(info.data.getMaxDrop() * dropmult)};
	}

	private static TypeGroupData getGroupAndData(L2NpcTemplate npc, boolean dropNoSpoil, int itemId)
	{
		if(dropNoSpoil && npc.getDropData().getNormal() != null)
			for(L2DropGroup dg : npc.getDropData().getNormal())
				for(L2DropData dd : dg.getDropItems(false))
					if (dd.getItemId() == itemId)
						return new TypeGroupData(RewardType.RATED_GROUPED, dg, dd);

		if(!dropNoSpoil && npc.getDropData().getSpoil() != null)
			for(L2DropGroup dg : npc.getDropData().getSpoil())
				for(L2DropData dd : dg.getDropItems(false))
					if (dd.getItemId() == itemId)
						return new TypeGroupData(RewardType.SWEEP, dg, dd);
		return null;
	}

	public static enum RewardType
	{
		RATED_GROUPED,         //additional_make_multi_list
		SWEEP;       // corpse_make_list
	}

	public static int calculateLevelDiffForDrop(int mobLevel, int charLevel, boolean boss)
	{
		if(!ConfigValue.UseDeepBlueDropRules)
			return 0;
		// According to official data (Prima), deep blue mobs are 9 or more levels below players
		int deepblue_maxdiff = boss ? ConfigValue.DeepBlueDropRaidMaxDiff : ConfigValue.DeepBlueDropMaxDiff;

		return Math.max(charLevel - mobLevel - deepblue_maxdiff, 0);
	}

	public static boolean isChest(int npcId, L2NpcInstance npc)
	{
		if(npc != null && (npc instanceof L2ChestInstance))
			return true;
		if(npcId >= 18265 && npcId <= 18298 || npcId >= 18534 && npcId <= 18538 || npcId >= 21801 && npcId <= 21822 || npcId == 35593 || npcId == 21671 || npcId == 21694 || npcId == 21717 || npcId == 21740 || npcId == 21763 || npcId == 21786)
			return true;
		return false;
	}
}
