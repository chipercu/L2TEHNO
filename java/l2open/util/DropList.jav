package l2open.util;

import l2open.gameserver.model.L2Drop;
import l2open.gameserver.model.L2DropData;
import l2open.gameserver.model.L2DropGroup;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2ReflectionBossInstance;
import l2open.gameserver.templates.L2NpcTemplate;

import java.text.NumberFormat;
import java.util.Map.Entry;

public abstract class DropList
{
	private static NumberFormat df = NumberFormat.getPercentInstance();
	static
	{
		df.setMaximumFractionDigits(6);
	}

	public static String generateDroplist(L2NpcTemplate template, L2MonsterInstance monster, double mod, L2Player pl)
	{
		StringBuffer tmp = new StringBuffer();
		tmp.append("<html><body><center><font color=\"LEVEL\">").append(template.name).append(", Id: ").append(template.getNpcId()).append("</font>");
		if(template.isDropHerbs)
			tmp.append("<br1><font color=\"00FF00\">herbs</font>");
		tmp.append("</center><table><tr><td></td></tr>");
		boolean emptylist = false;
		boolean overlevel = true;
		boolean icons = pl != null ? pl.getVarB("DroplistIcons") : false;
		double rateAdena = mod * Config.getRateAdena(pl);
		double rateDrop = mod * ((template.isRaid || monster instanceof L2ReflectionBossInstance) ? ConfigValue.RateRaidBoss : ConfigValue.RateDropItems * (pl != null ? pl.getRateItems() : 1));
		double rateSpoil = mod * ConfigValue.RateDropSpoil * (pl != null ? pl.getRateSpoil() : 1);
		if(template.getDropData() != null)
		{
			if(template.getDropData().getNormal() != null)
				for(L2DropGroup g : template.getDropData().getNormal())
				{
					if(g.isAdena() && rateAdena == 0)
						continue;
					else if(!g.isAdena() && rateDrop == 0)
						continue;
					overlevel = false;

					GArray<L2DropData> items; // список вещей группы
					double GCHANCE; // шанс группы, по сути сумма шансов вещей в ней, в расчетах используется только для фиксированного количества
					double dropmult; // множитель количества дропа, только для адены или фиксированного количества (canIterate)
					double chancemult; // множитель шанса дропа, только для фиксированного количества (canIterate)
					boolean canIterate; // если true то dropmult - изменение количества проходов, иначе умножать на него количество

					if(g.notRate()) // фактически только эпики
					{
						mod = Math.min(1, mod); // модификатор не может быть положительным, шанс может только уменьшиться
						GCHANCE = g.getChance(); // на шанс влияет только модификатор уровня
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
					else if(template.isRaid || g.fixedQty() || g.notRate()) // моб чамп/рейд или дроп экипировки/кеев
					{
						GCHANCE = g.getChance(); // в шансе группы берем рейт дропа
						Entry<Double, Integer> balanced = L2DropGroup.balanceChanceAndMult(GCHANCE); // балансируем шанс и количество так чтобы не зашкаливать за 100% 
						chancemult = balanced.getKey() / g.getChance();
						GCHANCE = balanced.getKey();
						dropmult = balanced.getValue();
						items = g.getDropItems(false); // список стандартный
						canIterate = true;
					}
					else
					// все остальные случаи - моб обычный, дроп всякой фигни
					{
						if(rateDrop > ConfigValue.RateBreakpoint)
						{
							canIterate = true;
							dropmult = Math.min(Math.ceil(rateDrop / ConfigValue.RateBreakpoint), ConfigValue.RateMaxIterations);
							rateDrop /= dropmult;
						}
						else
						{
							dropmult = 1; // уже учтено в механизме
							canIterate = false;
						}
						items = g.getRatedItems(rateDrop); // стандартный балансирующий механизм обработки рейтов для дропа
						chancemult = 1; // уже учтено в механизме
						GCHANCE = g.getChance();
					}
					tmp.append("</table><br><center>Group chance: ").append(df.format(GCHANCE / L2Drop.MAX_CHANCE));
					if(dropmult > 1 && canIterate) // если количество фиксировано то используется увеличение числа проходов
					{
						tmp.append(" x").append((int) dropmult);
						dropmult = 1;
					}
					tmp.append("</center><br><center>Group Id: ").append(g.getId());
					tmp.append("</center><table width=100%>");
					for(L2DropData d : items)
					{
						String chance = df.format(d.getChance() / L2Drop.MAX_CHANCE);
						if(icons)
						{
							tmp.append("<tr><td width=32><img src=icon.").append(d.getItem().getIcon()).append(" width=32 height=32></td><td width=200>").append(compact(d.getName())).append("<br1>[");
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
					if(rateSpoil > 0)
					{
						overlevel = false;
						tmp.append("</table><center>Spoil:</center><table width=100%>");
						for(L2DropGroup g : template.getDropData().getSpoil())
							for(L2DropData d : g.getDropItems(false))
							{
								Entry<Double, Integer> e = L2DropGroup.balanceChanceAndMult(d.getChance() * rateSpoil);
								double GCHANCE = e.getKey() / 1000000;
								int dropmult = e.getValue();
								if(icons)
								{
									tmp.append("<tr><td width=32><img src=icon.").append(d.getItem().getIcon()).append(" width=32 height=32></td><td width=200>").append(compact(d.getName())).append("<br1>[");
									tmp.append(d.getMinDrop() * dropmult).append("-").append(d.getMaxDrop() * dropmult).append("]    ");
									tmp.append(df.format(GCHANCE)).append("</td></tr>");
								}
								else
								{
									float qty = (d.getMinDrop() + d.getMaxDrop()) * dropmult / 2f;
									tmp.append("<tr><td width=80%>").append(compact(d.getName())).append("</td><td width=10%>");
									tmp.append(Math.round(qty)).append("</td><td width=10%>");
									tmp.append(df.format(GCHANCE)).append("</td></tr>");
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

	public static String compact(String s)
	{
		return s.replaceFirst("Recipe:", "R:").replaceFirst("Common Item - ", "Common ").replaceFirst("Scroll: Enchant", "Enchant").replaceFirst("Compressed Package", "CP");
	}
}