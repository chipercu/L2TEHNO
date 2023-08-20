package l2open.util;

import l2open.config.*;
import l2open.gameserver.model.L2Drop;
import l2open.gameserver.model.L2DropData;
import l2open.gameserver.model.L2DropGroup;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.skills.Stats;
import l2open.util.RateService;

import java.text.NumberFormat;
import java.util.Map.Entry;

public abstract class DropList
{
	private static NumberFormat df = NumberFormat.getPercentInstance();
	static
	{
		df.setMaximumFractionDigits(4);
	}

	public static String generateDroplist(L2NpcTemplate template, L2MonsterInstance monster, double mod, double mod_adena, L2Player pl)
	{
		StringBuffer tmp = new StringBuffer();
		tmp.append("<html><body><center><font color=\"LEVEL\">").append(template.name).append(", Id: ").append(template.getNpcId()).append("</font>");

		//if(pl.isGM())
		//	tmp.append("<button value=\"Edit\" action=\"bypass -h admin_edit_drop info "+template.getNpcId()+" 0 -1 true\" width=100 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");

		if(template.isDropHerbs)
			tmp.append("<br1><font color=\"00FF00\">herbs</font>");
		tmp.append("</center><table><tr><td></td></tr>");
		boolean emptylist = false;
		boolean overlevel = true;
		boolean icons = pl != null ? pl.getVarB("DroplistIcons", true) : false;

		if(template.getDropData() != null)
		{
			if(template.getDropData().getNormal() != null)
				for(L2DropGroup g : template.getDropData().getNormal())
				{
					double rateAdena = mod_adena * ConfigSystem.getRateAdena(pl);
					double rateEpaulette = mod * ConfigValue.RateDropEpaulette * (pl != null ? pl.getRateEpaulette() : 1);
					double rateDrop = mod;

					// ----------------------------------------------------------------
					if(ConfigValue.NoRaitDropRb && (monster != null && (monster.isEpicRaid() || monster.isRefRaid()) || template.isRaid))
						rateAdena = mod_adena*ConfigValue.RateDropAdena*ConfigValue.RateDropAdenaMultMod + ConfigValue.RateDropAdenaStaticMod;
					// ----------------------------------------------------------------
					if(monster != null && monster.isEpicRaid())
						rateDrop *= (g._isRate ? ConfigValue.RateEpicBoss : 1.) * (pl != null && !ConfigValue.NoRaitDropRb && g._isPremium ? pl.getRateItems() : 1);
					else if(template.isRaid || monster != null && monster.isRefRaid())
						rateDrop *= (g._isRate ? ConfigValue.RateRaidBoss : 1.) * (pl != null && !ConfigValue.NoRaitDropRb && g._isPremium ? pl.getRateItems() : 1);
					else
						rateDrop *= (g._isRate ? RateService.getRateDropItems(pl) : 1.) * (pl != null && g._isPremium ? pl.getRateItems() : 1);

					if(g.isAdena() && rateAdena == 0)
						continue;
					else if(!g.isAdena() && !g.isEpaulette() && rateDrop == 0)
						continue;
					else if(g.isEpaulette() && rateEpaulette == 0)
						continue;
					else if(g.isEpaulette() && monster != null && pl != null)
					{
						
						if(!monster.can_drop_epaulette(pl))
							continue;
					}
					
					overlevel = false;

					GArray<L2DropData> items; // список вещей группы
					double GCHANCE; // шанс группы, по сути сумма шансов вещей в ней, в расчетах используется только для фиксированного количества
					double dropmult; // множитель количества дропа, только для адены или фиксированного количества (canIterate)
					double chancemult; // множитель шанса дропа, только для фиксированного количества (canIterate)
					boolean canIterate; // если true то dropmult - изменение количества проходов, иначе умножать на него количество

					if(g.notRate()) // фактически только эпики
					{
						mod = Math.min(1, mod); // модификатор не может быть положительным, шанс может только уменьшиться
						GCHANCE = g.getChance() * mod; // на шанс влияет только модификатор уровня
						chancemult = mod;
						dropmult = 1; // количество жестко фиксировано
						items = g.getDropItems(false); // список стандартный
						canIterate = false; // только один раз...
					}
					else if(g.isAdena())
					{
						if(mod < 10)
						{ // обычный моб
							GCHANCE = g.getChance(); // шанс жестко фиксирован
							chancemult = 1;
							dropmult = rateAdena; // количество меняется по рейту адены
						}
						else
						{ // чамп
							chancemult = L2Drop.MAX_CHANCE / g.getChance();
							dropmult = rateAdena * g.getChance() / L2Drop.MAX_CHANCE;
							GCHANCE = L2Drop.MAX_CHANCE;
						}
						items = g.getDropItems(false); // список стандартный
						canIterate = false;
					}
					else if(template.isRaid || g.fixedQty() || g.notRate() || !g._isRate && g._isPremium) // моб чамп/рейд или дроп экипировки/кеев
					{
						if(g.notRate())
							rateDrop = Math.min(mod, 1);
						else if(monster != null && monster.isEpicRaid())
							rateDrop = g._isRate ? ConfigValue.RateEpicBoss * mod : Math.min(mod, 1);
						else if(monster != null && (monster.isRaid() || monster.isRefRaid()))
							rateDrop = g._isRate ? ConfigValue.RateRaidBoss * mod : Math.min(mod, 1);
						else
							rateDrop = (g._isRate ? RateService.getRateDropItems(pl) : 1.) * (g._isPremium ? pl.getRateItems() : 1.) * mod;

						GCHANCE = g.getChance() * rateDrop; // в шансе группы берем рейт дропа
						Entry<Double, Integer> balanced = L2DropGroup.balanceChanceAndMult(GCHANCE); // балансируем шанс и количество так чтобы не зашкаливать за 100% 
						chancemult = balanced.getKey() / g.getChance();
						GCHANCE = balanced.getKey();
						dropmult = !ConfigValue.DropFixedQty/* && _fixedQty*/ ? 1 : balanced.getValue();
						items = g.getDropItems(false); // список стандартный
						canIterate = true;
					}
					else if(g.isEpaulette())
					{
						if(rateEpaulette > ConfigValue.RateBreakpoint)
						{
							canIterate = true;
							dropmult = Math.min(Math.ceil(rateEpaulette / ConfigValue.RateBreakpoint), ConfigValue.RateMaxIterations);
							rateEpaulette /= dropmult;
						}
						else
						{
							dropmult = 1; // уже учтено в механизме
							canIterate = false;
						}
						items = g.getRatedItems(rateEpaulette, 1, pl, true); // стандартный балансирующий механизм обработки рейтов для дропа
						chancemult = 1; // уже учтено в механизме
						GCHANCE = 0;
						for(L2DropData i : items)
							GCHANCE += i.getChance(); // шанс группы пересчитываем
					}
					else
					// все остальные случаи - моб обычный, дроп всякой фигни
					{
						double new_rateDrop=rateDrop;
						if(new_rateDrop > ConfigValue.RateBreakpoint)
						{
							canIterate = true;
							dropmult = Math.min(Math.ceil(new_rateDrop / ConfigValue.RateBreakpoint), ConfigValue.RateMaxIterations);
							new_rateDrop /= dropmult;
						}
						else
						{
							dropmult = 1; // уже учтено в механизме
							canIterate = false;
						}
						items = g.getRatedItems(new_rateDrop, 1, pl, false); // стандартный балансирующий механизм обработки рейтов для дропа
						chancemult = 1; // уже учтено в механизме
						GCHANCE = 0;
						for(L2DropData i : items)
							GCHANCE += i.getChance(); // шанс группы пересчитываем
					}

					tmp.append("</table><br><center>Group chance: ").append(df.format(GCHANCE / L2Drop.MAX_CHANCE));
					if(dropmult > 1 && canIterate) // если количество фиксировано то используется увеличение числа проходов
					{
						tmp.append(" x").append((int) dropmult);
						dropmult = 1;
					}
					tmp.append("</center><table width=100%>");

					for(L2DropData d : items)
					{
						String chance = df.format(d.getChance() * chancemult / L2Drop.MAX_CHANCE);
						if(icons && !d.getItem().isHerb())
						{
							tmp.append("<tr><td width=32><img src=").append(d.getItem().getIcon()).append(" width=32 height=32></td><td width=200>").append(compact(d.getName())).append("<br1>[");
							tmp.append(Math.round(d.getMinDrop() * dropmult)).append("-").append(Math.round(d.getMaxDrop() * dropmult)).append("]    ");
							tmp.append(chance).append("</td></tr>");
						}
						else
						{
							tmp.append("<tr><td width=80%>").append(compact(d.getName())).append("</td><td width=10%>");
							tmp.append(Math.min(Math.round((d.getMinDrop() + d.getMaxDrop()) * dropmult / 2f), 9999999)).append("</td><td width=10%>");
							tmp.append(chance).append("</td></tr>");
						}
					}
				}

			if(template.getDropData().getSpoil() != null)
				if(template.getDropData().getSpoil().size() > 0)
				{
					double rateSpoil = mod * RateService.getRateDropSpoil(pl) * (pl != null ? pl.getRateSpoil() : 1);
					double rateChest = mod * ConfigValue.RateDropChest * (pl != null ? pl.getRateChest() : 1);

					if(rateSpoil > 0 || (rateChest > 0 && isChest(template.getNpcId(), monster)))
					{
						if(monster != null)
							rateSpoil*=monster.calcStat(Stats.SPOIL, 1., pl, null);
						overlevel = false;
						float spoil_count = ConfigValue.RateCountDropSpoil;
						if(isChest(template.getNpcId(), monster))
						{
							spoil_count = 1;
							tmp.append("</table><center>Drop chest:</center><table width=100%>");
						}
						else
							tmp.append("</table><center>Spoil:</center><table width=100%>");
						for(L2DropGroup g : template.getDropData().getSpoil())
							for(L2DropData d : g.getDropItems(false))
							{
								Entry<Double, Integer> e = L2DropGroup.balanceChanceAndMult(d.getChance() * (isChest(template.getNpcId(), monster) ? rateChest : rateSpoil));
								double GCHANCE = e.getKey() / 1000000;
								int dropmult = e.getValue();
								if(icons)
								{
									String icon = d.getItem().getIcon();
									if(icon == null || icon.equals(""))
										icon = "icon.etc_question_mark_i00";
									tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=200>").append(compact(d.getName())).append("<br1>[");
									if(ConfigValue.SpoilMinRate)
										tmp.append(d.getMinDrop()).append("-").append(d.getMaxDrop() * dropmult * spoil_count).append("]    ");
									else
										tmp.append(d.getMinDrop() * dropmult * spoil_count).append("-").append(d.getMaxDrop() * dropmult * spoil_count).append("]    ");
									tmp.append(df.format(GCHANCE)).append("</td></tr>");
								}
								else
								{
									float qty = (d.getMinDrop() + d.getMaxDrop()) * dropmult * spoil_count / 2f;
									tmp.append("<tr><td width=80%>").append(compact(d.getName())).append("</td><td width=10%>");
									tmp.append(Math.round(qty)).append("</td><td width=10%>");
									tmp.append(df.format(GCHANCE)).append("</td></tr>");
								}
							}
					}
				}
		}
		else
			emptylist = true;

		tmp.append("</table>");
		if(emptylist)
			tmp.append("<center>Droplist is empty</center>");
		else if(overlevel)
			tmp.append("<center>This monster is too weak for you!</center>");
		tmp.append("</body></html>");
		return tmp.toString();
	}

	public static boolean isChest(int npcId, L2NpcInstance npc)
	{
		if(npc != null && (npc instanceof L2ChestInstance))
			return true;
		if(npcId >= 18265 && npcId <= 18298 || npcId >= 18534 && npcId <= 18538 || npcId >= 21801 && npcId <= 21822 || npcId == 35593 || npcId == 21671 || npcId == 21694 || npcId == 21717 || npcId == 21740 || npcId == 21763 || npcId == 21786)
			return true;
		return false;
	}

	public static String compact(String s)
	{
		return s.replaceFirst("Recipe:", "R:").replaceFirst("Common Item - ", "Common ").replaceFirst("Scroll: Enchant", "Enchant").replaceFirst("Compressed Package", "CP");
	}
}