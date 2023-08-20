package communityboard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.RadarControl;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SpawnTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;

import java.util.logging.Logger;

//import actions.RewardListInfo;

/**
 * Community Board page containing Drop Calculator
 */
public class CommunityDropCalculator extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	private static final Logger _log = Logger.getLogger(CommunityDropCalculator.class.getName());

	private static enum Commands
	{
		_dropcalc,
		_dropitemsbyname,
		_dropmonstersbyitem,
		_dropmonstersbyname,
		_dropmonsterdetailsbyitem,
		_dropmonsterdetailsbyname,
		_bbsobservemonster,
		_bbsobserveitem
	}

	// да через жопу...
	private static enum Commands2
	{
		_friendlist_0_,
		_dropcalc,
		_dropitemsbyname,
		_dropmonstersbyitem,
		_dropmonstersbyname,
		_dropmonsterdetailsbyitem,
		_dropmonsterdetailsbyname,
		_bbsobservemonster,
		_bbsobserveitem
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();

		switch (cmd)
		{
			case "dropcalc":
			case "friendlist":
				showMainPage(player);
				break;
			case "dropitemsbyname":
				if (!st.hasMoreTokens())
				{
					showMainPage(player);
					return;
				}
				String itemName = "";
				while (st.countTokens() > 1)
					itemName += " " + st.nextToken();

				int itemsPage = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
				showdropitemsbynamePage(player, itemName.trim(), itemsPage);
				break;
			case "dropmonstersbyitem":
				int itemId = Integer.parseInt(st.nextToken());
				int monstersPage = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
				showDropMonstersByItem(player, itemId, monstersPage);
				break;
			case "dropmonsterdetailsbyitem":
				int monsterId = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
					manageButton(player, Integer.parseInt(st.nextToken()), monsterId);
				showdropMonsterDetailsByItem(player, monsterId);
				break;
			case "dropmonstersbyname":
				if (!st.hasMoreTokens())
				{
					showMainPage(player);
					return;
				}
				String monsterName = "";
				while (st.countTokens() > 1)
					monsterName += " " + st.nextToken();

				int monsterPage = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
				showDropMonstersByName(player, monsterName.trim(), monsterPage);
				break;
			case "dropmonsterdetailsbyname":
				int chosenMobId = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
					manageButton(player, Integer.parseInt(st.nextToken()), chosenMobId);
				showDropMonsterDetailsByName(player, chosenMobId);
				break;
			case "bbsobservemonster":
				int monsterIdO = Integer.parseInt(st.nextToken());
				L2NpcInstance npc = L2ObjectsStorage.getByNpcId(monsterIdO);
				if(npc != null && npc.getSpawn() != null && npc.getSpawnedLoc() != null && !npc.isAlikeDead() && npc.getLoc() != null)
					observer(player, npc);
				else
				{
					player.sendMessage(player.isLangRus() ? "Монстр мертв!" : "Monster's dead!");
					showDropMonsterDetailsByName(player, monsterIdO);
				}
				break;
			case "bbsobserveitem":
				int monsterIdI = Integer.parseInt(st.nextToken());
				L2NpcInstance npcI = L2ObjectsStorage.getByNpcId(monsterIdI);
				if(npcI != null && npcI.getSpawn() != null && npcI.getSpawnedLoc() != null && !npcI.isAlikeDead() && npcI.getLoc() != null)
					observer(player, npcI);
				else
				{
					player.sendMessage(player.isLangRus() ? "Монстр мертв!" : "Monster's dead!");
					showdropMonsterDetailsByItem(player, monsterIdI);
				}
				break;
			default:
				break;
		}
	}

	private static void observer(L2Player player, L2Object target)
	{
		if(player == null || target == null)
			return;
		player.enterObserverMode(target.getLoc(), null);
	}

	private static void showMainPage(L2Player player)
	{
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "drop/CalcMain.htm", player);
		ShowBoard.separateAndSend(html, player);
	}

	private static void showdropitemsbynamePage(L2Player player, String itemName, int page)
	{
		player.addQuickVar("DCItemName", itemName);
		player.addQuickVar("DCItemsPage", String.valueOf(page));
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "drop/ItemsByName.htm", player);
		html = replaceItemsByNamePage(player, html, itemName, page);
		ShowBoard.separateAndSend(html, player);
	}

	private static String replaceItemsByNamePage(L2Player player, String html, String itemName, int page)
	{
		String newHtml = html;

		long start_time = System.currentTimeMillis();
		
		List<L2Item> itemsByName = ItemTemplates.getInstance().getItemsByNameContainingString(itemName, true);

		long time1 = System.currentTimeMillis()-start_time;
		if(time1 > 50)
			_log.info("replaceItemsByNamePage: 1st["+itemsByName.size()+"]="+time1);
		itemsByName.sort(new ItemComparator(itemName));
		long time2 = System.currentTimeMillis()-start_time;
		if(time2 > 50)
			_log.info("replaceItemsByNamePage: 2st="+time2);

		int itemIndex = 0;

		for (int i = 0; i < 8; i++)
		{
			itemIndex = i + (page - 1) * 8;
			L2Item item = itemsByName.size() > itemIndex ? itemsByName.get(itemIndex) : null;

			newHtml = newHtml.replace("%itemIcon" + i + '%', item != null ? getItemIcon(item) : "<br>");
			newHtml = newHtml.replace("%itemName" + i + '%', item != null ? getName(item.getName(), false) : "<br>");
			newHtml = newHtml.replace("%itemGrade" + i + '%', item != null ? getItemGradeIcon(item) : "<br>");
			newHtml = newHtml.replace("%dropLists" + i + '%', item != null ? String.valueOf(CalculateRewardChances.getDroplistsCountByItemId(item.getItemId(), true)) : "<br>");
			newHtml = newHtml.replace("%spoilLists" + i + '%', item != null ? String.valueOf(CalculateRewardChances.getDroplistsCountByItemId(item.getItemId(), false)) : "<br>");
			newHtml = newHtml.replace("%showMonsters" + i + '%', item != null ? "<button value=\""+(player.isLangRus() ? "Список монстров" : "Show Monsters")+"\" action=\"bypass -h _dropmonstersbyitem_%itemChosenId" + i + "%\" width=120 height=32 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">" : "<br>");
			newHtml = newHtml.replace("%itemChosenId" + i + '%', item != null ? String.valueOf(item.getItemId()) : "<br>");
		}

		newHtml = newHtml.replace("%previousButton%", page > 1 ? "<button value=\"<<<\" action=\"bypass -h _dropitemsbyname_" + itemName + "_" + (page - 1) + "\" width=100 height=22 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">" : "<br>");
		newHtml = newHtml.replace("%nextButton%", itemsByName.size() > itemIndex + 1 ? "<button value=\">>>\" action=\"bypass -h _dropitemsbyname_" + itemName + "_" + (page + 1) + "\" width=100 height=22 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">" : "<br>");

		newHtml = newHtml.replace("%searchItem%", itemName);
		newHtml = newHtml.replace("%page%", String.valueOf(page));

		return newHtml;
	}
	private static void showDropMonstersByItem(L2Player player, int itemId, int page)
	{
		player.addQuickVar("DCItemId", String.valueOf(itemId));
		player.addQuickVar("DCMonstersPage", String.valueOf(page));
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "drop/MonstersByItem.htm", player);
		html = replaceMonstersByItemPage(player, html, itemId, page);
		ShowBoard.separateAndSend(html, player);
	}

	private static String replaceMonstersByItemPage(L2Player player, String html, int itemId, int page)
	{
		String newHtml = html;

		List<CalculateRewardChances.NpcTemplateDrops> templates = CalculateRewardChances.getNpcsByDropOrSpoil(itemId);
		templates.sort(new ItemChanceComparator(player, itemId));

		int npcIndex = 0;

		for (int i = 0; i < 10; i++)
		{
			npcIndex = i + (page - 1) * 10;
			CalculateRewardChances.NpcTemplateDrops drops = templates.size() > npcIndex ? templates.get(npcIndex) : null;
			L2NpcTemplate npc = templates.size() > npcIndex ? templates.get(npcIndex).template : null;

			newHtml = newHtml.replace("%monsterName" + i + '%', npc != null ? getName(npc.name, npc.isRaid || npc.isEpicRaid || npc.isBoss || npc.isRefRaid) : "<br>");
			newHtml = newHtml.replace("%monsterLevel" + i + '%', npc != null ? String.valueOf(npc.level) : "<br>");
			newHtml = newHtml.replace("%monsterAggro" + i + '%', npc != null ? Util.boolToString(npc.aggroRange > 0) : "<br>");
			newHtml = newHtml.replace("%monsterType" + i + '%', npc != null ? drops.dropNoSpoil ? "Drop" : "Spoil" : "<br>");
			newHtml = newHtml.replace("%monsterCount" + i + '%', npc != null ? String.valueOf(getDropCount(player, npc, itemId, drops.dropNoSpoil)) : "<br>");
			newHtml = newHtml.replace("%monsterChance" + i + '%', npc != null ? String.valueOf(getDropChance(player, npc, itemId, drops.dropNoSpoil)) : "<br>");
			newHtml = newHtml.replace("%showDetails" + i + '%', npc != null ? "<button value=\""+(player.isLangRus() ? "Детально" : "Show Details")+"\" action=\"bypass -h _dropmonsterdetailsbyitem_%monsterId" + i + "%\" width=120 height=32 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">" : "<br>");
			newHtml = newHtml.replace("%monsterId" + i + '%', npc != null ? String.valueOf(npc.getNpcId()) : "<br>");
		}

		newHtml = newHtml.replace("%previousButton%", page > 1 ? "<button value=\"<<<\" action=\"bypass -h _dropmonstersbyitem_%itemChosenId%_" + (page - 1) + "\" width=100 height=22 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">" : "<br>");
		newHtml = newHtml.replace("%nextButton%", templates.size() > npcIndex + 1 ? "<button value=\">>>\" action=\"bypass -h _dropmonstersbyitem_%itemChosenId%_" + (page + 1) + "\" width=100 height=22 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">" : "<br>");

		newHtml = newHtml.replace("%searchItem%", player.getVar("DCItemName"));
		newHtml = newHtml.replace("%searchItemPage%", String.valueOf(player.getVarInt("DCItemsPage")));
		newHtml = newHtml.replace("%itemChosenId%", String.valueOf(itemId));
		newHtml = newHtml.replace("%monsterPage%", String.valueOf(page));
		return newHtml;
	}

	private static void showdropMonsterDetailsByItem(L2Player player, int monsterId)
	{
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "drop/MonsterDetailsByItem.htm", player);
		html = replaceMonsterDetails(player, html, monsterId);

		if (!canTeleToMonster(player, monsterId, false))
			html = html.replace("%goToNpc%", "&nbsp;");
		else
			html = html.replace("%goToNpc%", "<button value=\"Go to\" action=\"bypass -h _dropmonsterdetailsbyitem_"+monsterId+"_3\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Fight1None_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Fight1None\">");

		ShowBoard.separateAndSend(html, player);
	}

	private static String replaceMonsterDetails(L2Player player, String html, int monsterId)
	{
		String newHtml = html;

		int itemId = player.getVarInt("DCItemId");
		L2NpcTemplate template = NpcTable.getTemplate(monsterId);
		if (template == null)
			return newHtml;

		newHtml = newHtml.replace("%searchName%", String.valueOf(player.getVar("DCMonsterName")));
		newHtml = newHtml.replace("%itemChosenId%", String.valueOf(player.getVarInt("DCItemId")));
		newHtml = newHtml.replace("%monsterPage%", String.valueOf(player.getVarInt("DCMonstersPage")));
		newHtml = newHtml.replace("%monsterId%", String.valueOf(monsterId));
		newHtml = newHtml.replace("%monsterName%", getName(template.name, template.isRaid || template.isEpicRaid || template.isBoss || template.isRefRaid));
		newHtml = newHtml.replace("%monsterLevel%", String.valueOf(template.level));
		newHtml = newHtml.replace("%monsterAggro%", Util.boolToString(template.aggroRange > 0));
		if (itemId > 0)
		{
			newHtml = newHtml.replace("%monsterDropSpecific%", String.valueOf(getDropChance(player, template, itemId, true)));
			newHtml = newHtml.replace("%monsterSpoilSpecific%", String.valueOf(getDropChance(player, template, itemId, false)));
		}
		newHtml = newHtml.replace("%monsterDropAll%", String.valueOf(CalculateRewardChances.getDrops(template, true, false).size()));
		newHtml = newHtml.replace("%monsterSpoilAll%", String.valueOf(CalculateRewardChances.getDrops(template, false, true).size()));
		newHtml = newHtml.replace("%spawnCount%", String.valueOf(L2ObjectsStorage.getAllByNpcId(monsterId, false).size()));

		int minions = 0;
		for(L2MinionData minion : template.getMinionData())
			minions+=minion.getAmount();

		newHtml = newHtml.replace("%minions%", String.valueOf(minions));
		newHtml = newHtml.replace("%expReward%", String.valueOf(template.revardExp));
		newHtml = newHtml.replace("%maxHp%", String.valueOf(template.baseHpMax));
		newHtml = newHtml.replace("%maxMP%", String.valueOf(template.baseMpMax));
		newHtml = newHtml.replace("%pAtk%", String.valueOf(template.basePAtk));
		newHtml = newHtml.replace("%mAtk%", String.valueOf(template.baseMAtk));
		newHtml = newHtml.replace("%pDef%", String.valueOf(template.basePDef));
		newHtml = newHtml.replace("%mDef%", String.valueOf(template.baseMDef));
		newHtml = newHtml.replace("%atkSpd%", String.valueOf(template.basePAtkSpd));
		newHtml = newHtml.replace("%castSpd%", String.valueOf(template.baseMAtkSpd));
		newHtml = newHtml.replace("%runSpd%", String.valueOf(template.baseRunSpd));

		return newHtml;
	}

	private static void showDropMonstersByName(L2Player player, String monsterName, int page)
	{
		player.addQuickVar("DCMonsterName", monsterName);
		player.addQuickVar("DCMonstersPage", String.valueOf(page));
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "drop/MonstersByName.htm", player);
		html = replaceMonstersByName(player, html, monsterName, page);
		ShowBoard.separateAndSend(html, player);
	}

	private static String replaceMonstersByName(L2Player player, String html, String monsterName, int page)
	{
		String newHtml = html;
		List<L2NpcTemplate> npcTemplates = CalculateRewardChances.getNpcsContainingString(monsterName);
		npcTemplates = sortMonsters(npcTemplates, monsterName);

		int npcIndex = 0;

		for (int i = 0; i < 10; i++)
		{
			npcIndex = i + (page - 1) * 10;
			L2NpcTemplate npc = npcTemplates.size() > npcIndex ? npcTemplates.get(npcIndex) : null;

			newHtml = newHtml.replace("%monsterName" + i + '%', npc != null ? getName(npc.name, npc.isRaid || npc.isEpicRaid || npc.isBoss || npc.isRefRaid) : "<br>");
			newHtml = newHtml.replace("%monsterLevel" + i + '%', npc != null ? String.valueOf(npc.level) : "<br>");
			newHtml = newHtml.replace("%monsterAggro" + i + '%', npc != null ? Util.boolToString(npc.aggroRange > 0) : "<br>");
			newHtml = newHtml.replace("%monsterDrops" + i + '%', npc != null ? String.valueOf(CalculateRewardChances.getDrops(npc, true, false).size()) : "<br>");
			newHtml = newHtml.replace("%monsterSpoils" + i + '%', npc != null ? String.valueOf(CalculateRewardChances.getDrops(npc, false, true).size()) : "<br>");
			newHtml = newHtml.replace("%showDetails" + i + '%', npc != null ? "< button value =\""+(player.isLangRus() ? "Детально" : "Show Details")+"\" action=\"bypass -h _dropmonsterdetailsbyname_" + npc.getNpcId() + "\" width=120 height=32 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">" : "<br>");
		}

		newHtml = newHtml.replace("%previousButton%", page > 1 ? "<button value=\"<<<\" action=\"bypass -h _dropmonstersbyname_%searchName%_" + (page - 1) + "\" width=100 height=22 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">" : "<br>");
		newHtml = newHtml.replace("%nextButton%", npcTemplates.size() > npcIndex + 1 ? "<button value=\">>>\" action=\"bypass -h _dropmonstersbyname_%searchName%_" + (page + 1) + "\" width=100 height=22 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">" : "<br>");

		newHtml = newHtml.replace("%searchName%", monsterName);
		newHtml = newHtml.replace("%page%", String.valueOf(page));
		return newHtml;
	}

	private static void showDropMonsterDetailsByName(L2Player player, int monsterId)
	{
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "drop/MonsterDetailsByName.htm", player);
		html = replaceMonsterDetails(player, html, monsterId);
		if (!canTeleToMonster(player, monsterId, false))
			html = html.replace("%goToNpc%", "&nbsp;");
		else
			html = html.replace("%goToNpc%", "<button value=\"Go to\" action=\"bypass -h _dropmonsterdetailsbyname_" + monsterId + "_3\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Fight1None_Down\" fore=\"L2UI_ct1" + ".OlympiadWnd_DF_Fight1None\">");

		ShowBoard.separateAndSend(html, player);
	}

	private static void manageButton(L2Player player, int buttonId, int monsterId)
	{
		switch (buttonId)
		{
			case 1:// Show Monster on Map
			
				List<Location> locs = new ArrayList<>();
				for(L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
					if(spawn.getNpcId() == monsterId && spawn.getLastSpawn() != null)
						locs.add(spawn.getLastSpawn().getSpawnedLoc());

				if (locs == null || locs.isEmpty())
					return;

				player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
				player.sendPacket(new Say2(player.getObjectId(), Say2C.COMMANDCHANNEL_ALL, "", player.isLangRus() ? "Откройте карту, чтобы увидеть местоположение." : "Open Map to see Locations"));

				for (Location loc : locs)
					player.sendPacket(new RadarControl(0, 1, loc));
				break;
			case 2:// Show Drops
				/**RewardListInfo.showInfo(player, NpcTable.getTemplate(monsterId), false, false, 1.0);**/
					droplist(player, monsterId);
				break;
			case 3:// Teleport To Monster
				if (!canTeleToMonster(player, monsterId, true))
					return;
				List<L2NpcInstance> aliveInstance = L2ObjectsStorage.getAllByNpcId(monsterId, false);
				try
				{
					for(L2NpcInstance npc : aliveInstance)
						if(npc != null && npc.getLoc() != null)
						{
							player.teleToLocation(npc.getLoc());
							break;
						}
					player.sendMessage("Monster isn't alive!");
				}
				catch(Exception e)
				{
					player.sendMessage("Monster isn't alive!");
				}
				break;
			default:
				break;
		}
	}

	private static boolean canTeleToMonster(L2Player player, int monsterId, boolean sendMessage)
	{
		if(player.isGM())
			return true;
		return false;
		/*if (!player.isInZonePeace())
		{
			if (sendMessage)
				player.sendMessage("You can do it only in safe zone!");
			return false;
		}

		if (Olympiad.isRegistered(player) || player.isInOlympiadMode())
		{
			if (sendMessage)
				player.sendMessage("You cannot do it while being registered in Olympiad Battle!");
			return false;
		}

		if (Arrays.binarySearch(ConfigValue.DROP_CALCULATOR_DISABLED_TELEPORT, monsterId) >= 0)
		{
			if (sendMessage)
				player.sendMessage("You cannot teleport to this Npc!");
			return false;
		}

		return true;*/
	}

//if (!player.reduceAdena(1000000, true, "TeleportToMonster"))	
  //      {
    //        if(sendMessage)	
      //          player.sendMessage("You do not have enough adena!");	
        //    return false;	
       // }

	private static CharSequence getItemIcon(L2Item template)
	{
		return "<img src=\""+template.getIcon()+"\" width=32 height=32>";
	}

	private static CharSequence getItemGradeIcon(L2Item template)
	{
		if (template.getCrystalType() == L2Item.Grade.NONE)
			return "";
		return ConfigValue.DropCalculator_GradeIcon[template.getCrystalType().ordinal()];
		//return "<img src=\"L2UI_CT1.Icon_DF_ItemGrade_"+template.getCrystalType()+"\" width=16 height=16>";
	}

	private static CharSequence getName(String name, boolean isRb)
	{
		if (isRb)
			return "</font><font color=c47e0f>"+name;
		return name;
	}

	private static String getDropCount(L2Player player, L2NpcTemplate monster, int itemId, boolean drop)
	{
		long[] counts = CalculateRewardChances.getDropCounts(player, monster, drop, itemId);
		String formattedCounts = "[" + counts[0] + "..." + counts[1] + ']';
		if (formattedCounts.length() > 20)
			formattedCounts = "</font><font color=c47e0f>" + formattedCounts;
		return formattedCounts;
	}

	private static String getDropChance(L2Player player, L2NpcTemplate monster, int itemId, boolean drop)
	{
		String chance = CalculateRewardChances.getDropChance(player, monster, drop, itemId);
		return formatDropChance(chance);
	}

	public static String formatDropChance(String chance)
	{
		String realChance = chance;
		if (realChance.length() - realChance.indexOf('.') > 6)
			realChance = realChance.substring(0, realChance.indexOf('.')+7);

		if (realChance.endsWith(".0"))
			realChance = realChance.substring(0, realChance.length()-2);

		return realChance+'%';
	}

	public static class ItemComparator implements Comparator<L2Item>, Serializable
	{
		private static final long serialVersionUID = -6389059445439769861L;
		private final String search;

		private ItemComparator(String search)
		{
			this.search = search;
		}

		@Override
		public int compare(L2Item o1, L2Item o2)
		{
			if (o1.equals(o2))
				return 0;
			if (o1.getName().equalsIgnoreCase(search))
				return -1;
			if (o2.getName().equalsIgnoreCase(search))
				return 1;

			//return Integer.compare(CalculateRewardChances.getDroplistsCountByItemId(o2.getItemId(), true), CalculateRewardChances.getDroplistsCountByItemId(o1.getItemId(), true));
			return Integer.compare(o2.getItemId(), o1.getItemId());
		}
	}

	private static class ItemChanceComparator implements Comparator<CalculateRewardChances.NpcTemplateDrops>,
			Serializable
	{
		private static final long serialVersionUID = 6323413829869254438L;
		private final int itemId;
		private final L2Player player;

		private ItemChanceComparator(L2Player player, int itemId)
		{
			this.itemId = itemId;
			this.player = player;
		}

		@Override
		public int compare(CalculateRewardChances.NpcTemplateDrops o1, CalculateRewardChances.NpcTemplateDrops o2)
		{
			BigDecimal maxDrop1 = BigDecimal.valueOf(CalculateRewardChances.getDropCounts(player, o1.template, o1.dropNoSpoil, itemId)[1]);
			BigDecimal maxDrop2 = BigDecimal.valueOf(CalculateRewardChances.getDropCounts(player, o2.template, o2.dropNoSpoil, itemId)[1]);
			BigDecimal chance1 = new BigDecimal(CalculateRewardChances.getDropChance(player, o1.template, o1.dropNoSpoil, itemId));
			BigDecimal chance2 = new BigDecimal(CalculateRewardChances.getDropChance(player, o2.template, o2.dropNoSpoil, itemId));

			int compare = chance2.multiply(maxDrop2).compareTo(chance1.multiply(maxDrop1));
			if (compare == 0)
				return o2.template.name.compareTo(o1.template.name);
			return compare;
		}
	}

	private static List<L2NpcTemplate> sortMonsters(List<L2NpcTemplate> npcTemplates, String monsterName)
	{
		Collections.sort(npcTemplates, new MonsterComparator(monsterName));
		return npcTemplates;
	}

	private static class MonsterComparator implements Comparator<L2NpcTemplate>, Serializable
	{
		private static final long serialVersionUID = 2116090903265145828L;
		private final String search;

		private MonsterComparator(String search)
		{
			this.search = search;
		}

		@Override
		public int compare(L2NpcTemplate o1, L2NpcTemplate o2)
		{
			if (o1.equals(o2))
				return 0;
			if (o1.name.equalsIgnoreCase(search))
				return 1;
			if (o2.name.equalsIgnoreCase(search))
				return -1;

			return o2.name.compareTo(o2.name);
		}
	}

	public static void droplist(L2Player player, int monsterId)
	{
		L2NpcTemplate template = NpcTable.getTemplate(monsterId);
		if(player == null || template == null)
			return;

		int diff = CalculateRewardChances.calculateLevelDiffForDrop(template.level, player.isInParty() ? player.getParty().getLevel() : player.getLevel(), false);
		double mult = 1;
		if(diff > 0)
			mult = Experience.penaltyModifier(diff, 9);

		L2MonsterInstance npc = null;
		List<L2NpcInstance> aliveInstance = L2ObjectsStorage.getAllByNpcId(monsterId, false);
		for(L2NpcInstance npcs : aliveInstance)
			if(npcs != null && npcs.isMonster())
				{
					npc = (L2MonsterInstance)npcs;
					break;
				}

		mult = npc == null ? mult : npc.calcStat(Stats.DROP, mult, player, null);

		double mod_adena = npc == null ? 1 : npc.calcStat(Stats.ADENA, 1., player, null);

		Functions.show(DropList.generateDroplist(template, npc, mult, mod_adena, player), player, null);
	}

	@Override
	public void parsewrite(String arg1, String arg2, String arg3, String arg4, String arg5, L2Player player)
	{}

	@SuppressWarnings("rawtypes")
	public Enum[] getCommunityCommandEnum()
	{
		return ConfigValue.DropCalculatorFriend ? Commands2.values() : Commands.values();
	}

	@Override
	public void onLoad()
	{
		if(ConfigValue.EnableDropCalculator)
		{
			_log.info("CommunityBoard: Drop Calculator service loaded.");
			CommunityHandler.getInstance().registerCommunityHandler(this);
		}
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}