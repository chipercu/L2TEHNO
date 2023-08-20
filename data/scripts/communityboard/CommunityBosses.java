package services.community;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import l2open.config.*;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.RadarControl;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.*;
import l2open.util.reference.HardReference;
import l2open.util.reference.HardReferences;

//import actions.RewardListInfo;

public class CommunityBosses extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	public static HardReference<L2Player> self = HardReferences.emptyRef();

	private static final int BOSSES_PER_PAGE = 10;
	private static final int[] BOSSES_TO_NOT_SHOW = { 29006,//Core
			29001,//Queen Ant
			29014,//Orfen
			25692,//Aenkinel
			25423,//Fairy Queen Timiniel
			25010,//Furious Thieles
			25532,//Kechi
			25119,//Messenger of Fairy Queen Berun
			25159,//Paniel the Unicorn
			25163,//Roaring Skylancer
			25070,//Enchanted Forest Watcher Ruell
			25603,//Darion
			25544,//Tully
			36600,//Baltazar
			38908,//Balok
			50014,//Valakas Mini
			50015,//Antharas Mini
			50016,//Baium Mini
			29019,//Antharas
			29066,//Antharas
			29067,//Antharas
			29068,//Antharas
			29028,//Valakas
			29020,//Baium
			50009 //Gorinich Maroder
			
	};

	private static enum Commands
	{
		_bbsmemo,
		_bbsbosslist,
		_bbsboss,
		_bbsobserveboss
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		//player.setSessionVar("add_fav", null);

		if(bypass.startsWith("_bbsmemo") || bypass.startsWith("_bbsbosslist"))//_bbsbosslist_sort_page_search
		{
			int sort = Integer.parseInt(st.hasMoreTokens() ? st.nextToken().trim() : "1");
			int page = Integer.parseInt(st.hasMoreTokens() ? st.nextToken().trim() : "0");
			String search = st.hasMoreTokens() ? st.nextToken().trim() : "";

			sendBossListPage(player, getSortByIndex(sort), page, search);
		}
		else if(bypass.startsWith("_bbsboss"))//_bbsboss_sort_page_search_rbId_btn
		{
			int sort = Integer.parseInt(st.hasMoreTokens() ? st.nextToken().trim() : "3");
			int page = Integer.parseInt(st.hasMoreTokens() ? st.nextToken().trim() : "0");
			String search = st.hasMoreTokens() ? st.nextToken().trim() : "";
			int bossId = Integer.parseInt(st.hasMoreTokens() ? st.nextToken().trim() : "25044");
			int buttonClick = Integer.parseInt(st.hasMoreTokens() ? st.nextToken().trim() : "0");

			manageButtons(player, buttonClick, bossId);

			sendBossDetails(player, getSortByIndex(sort), page, search, bossId);
		}
	}

	/**
	 * Showing list of bosses in Community Board with their Name, Level, Status and Show Details button
	 * @param player guy that will receive list
	 * @param sort index of the sorting type
	 * @param page number of the page(Starting from 0)
	 * @param search word in Name of the boss
	 */
	
	private void sendBossListPage(L2Player player, SortType sort, int page, String search)
	{
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "bbs_boss_list.htm", player);

		Map<Integer, StatsSet> allBosses = getSearchedBosses(sort, search);
		Map<Integer, StatsSet> bossesToShow = getBossesToShow(allBosses, page);
		boolean isThereNextPage = allBosses.size() > bossesToShow.size();

		html = getBossListReplacements(player, html, page, bossesToShow, isThereNextPage);

		html = getNormalReplacements(html, page, sort, search, -1);
		separateAndSend(html, player);
	}

	/**
	 * Replacing %x% words in bbs_bbslink_list.htm file
	 * @param html existing file
	 * @param page number of the page(Starting from 0)
	 * @param allBosses Map<BossId, BossStatsSet> of bosses that will be shown
	 * @param nextPage Is the next page?
	 * @return ready HTML
	 */
	private static String getBossListReplacements(L2Player player, String html, int page, Map<Integer, StatsSet> allBosses, boolean nextPage)
	{
		String newHtml = html;

		int i = 0;

		for(Entry<Integer, StatsSet> entry : allBosses.entrySet())
		{
			StatsSet boss = entry.getValue();
			L2NpcTemplate temp = NpcTable.getTemplate(entry.getKey().intValue());

			boolean isAlive = isBossAlive(boss);

			newHtml = newHtml.replace("<?name_" + i + "?>", temp.name);
			newHtml = newHtml.replace("<?level_" + i + "?>", String.valueOf(temp.level));
			newHtml = newHtml.replace("<?status_" + i + "?>", isAlive ? "Alive" : getRespawnTime(boss));
			newHtml = newHtml.replace("<?status_color_" + i + "?>", getTextColor(isAlive));
			if(player.isLangRus())
			{
				newHtml = newHtml.replace("<?bp_" + i + "?>", "<button value=\"Показать\" action=\"bypass -h _bbsboss_<?sort?>_" + page + "_ <?search?> _" + entry.getKey() + "\" width=55 height=12 back=\"L2UI_CT1.ListCTRL_DF_Title_Down\" fore=\"L2UI_CT1.ListCTRL_DF_Title\">");
			}
			else
			{
				newHtml = newHtml.replace("<?bp_" + i + "?>", "<button value=\"Show\" action=\"bypass -h _bbsboss_<?sort?>_" + page + "_ <?search?> _" + entry.getKey() + "\" width=55 height=12 back=\"L2UI_CT1.ListCTRL_DF_Title_Down\" fore=\"L2UI_CT1.ListCTRL_DF_Title\">");
			}
			i++;
		}

		for(int j = i; j < BOSSES_PER_PAGE; j++)
		{
			newHtml = newHtml.replace("<?name_" + j + "?>", "...");
			newHtml = newHtml.replace("<?level_" + j + "?>", "...");
			newHtml = newHtml.replace("<?status_" + j + "?>", "...");
			newHtml = newHtml.replace("<?status_color_" + j + "?>", "FFFFFF");
			newHtml = newHtml.replace("<?bp_" + j + "?>", "...");

		}

		newHtml = newHtml.replace("<?previous?>", page > 0 ? "<button action=\"bypass -h _bbsbosslist_<?sort?> " + (page - 1) + " <?search?>\" width=16 height=16 back=\"L2UI_CH3.shortcut_prev_down\" fore=\"L2UI_CH3.shortcut_prev\">" : "<br>");
		newHtml = newHtml.replace("<?next?>", nextPage && i == BOSSES_PER_PAGE ? "<button action=\"bypass -h _bbsbosslist_<?sort?> " + (page + 1) + " <?search?>\" width=16 height=16 back=\"L2UI_CH3.shortcut_next_down\" fore=\"L2UI_CH3.shortcut_next\">" : "<br>");
		newHtml = newHtml.replace("<?pages?>", String.valueOf(page + 1));

		return newHtml;
	}

	/**
	 * Getting all bosses to show(checking only page)
	 * @param page number of the page(Starting from 0)
	 * @return Bosses
	 */
	private static Map<Integer, StatsSet> getBossesToShow(Map<Integer, StatsSet> allBosses, int page)
	{
		Map<Integer, StatsSet> bossesToShow = new LinkedHashMap<Integer, StatsSet>();
		int i = 0;
		for(Entry<Integer, StatsSet> entry : allBosses.entrySet())
		{
			if(i < page * BOSSES_PER_PAGE)
			{
				i++;
			}
			else
			{
				StatsSet boss = entry.getValue();
				L2NpcTemplate temp = NpcTable.getTemplate(entry.getKey().intValue());
				if(boss != null && temp != null)
				{
					i++;
					bossesToShow.put(entry.getKey(), entry.getValue());
					if(i > (page * BOSSES_PER_PAGE + BOSSES_PER_PAGE - 1)){ return bossesToShow; }
				}
			}
		}
		return bossesToShow;
	}

	/**
	 * Showing detailed info about Boss in Community Board. Including name, level, status, stats, image
	 * @param player guy that will receive details
	 * @param sort index of the sorting type
	 * @param page number of the page(Starting from 0)
	 * @param search word in Name of the boss
	 * @param bossId Id of the boss to show
	 */
	private void sendBossDetails(L2Player player, SortType sort, int page, CharSequence search, int bossId)
	{
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "bbs_boss_details.htm", player);
		StatsSet bossSet = RaidBossSpawnManager.getInstance().getAllBosses().get(bossId);

		if(bossSet == null)
		{
			separateAndSend(html, player);
			return;
		}

		L2NpcTemplate bossTemplate = NpcTable.getTemplate(bossId);
		L2NpcInstance bossInstance = getAliveBoss(bossId);

		html = getDetailedBossReplacements(player, html, bossSet, bossTemplate, bossInstance);
		html = getNormalReplacements(html, page, sort, search, bossId);

		//if(!AutoImageSenderManager.isImageAutoSendable(bossId))
		//	ImagesCache.getInstance().sendImageToPlayer(player, bossId);

		separateAndSend(html, player);
	}

	/**
	 * Managing buttons that were clicking in Boss Details page
	 * @param player that clicked button
	 * @param buttonIndex 1: Showing Location of the boss. 2: Showing Drops
	 * @param bossId Id of the boss that player was looking into
	 */
	private static void manageButtons(L2Player player, int buttonIndex, int bossId)
	{
		switch(buttonIndex)
		{
			case 1://Show Location
				RaidBossSpawnManager.showBossLocation(player, bossId);
				break;
			case 2://Show Drops
				//if(Config.ALLOW_DROP_CALCULATOR)
					//RewardListInfo.showInfo(player, NpcTable.getTemplate(bossId), true, false, 1.0);
				break;
			case 3://Go to Boss
				if(!player.isInZonePeace() || Olympiad.isRegistered(player))
				{
					player.sendMessage(player.isLangRus() ? "Доступно только в мирной зоне!" : "Available only in peace zone!");
					return;
				}
				L2NpcInstance aliveInstance = getAliveBoss(bossId);
				if(aliveInstance != null)
					player.teleToLocation(aliveInstance.getLoc());
				else
					player.sendMessage(player.isLangRus() ? "Босс мертв!" : "Boss is dead!");
				break;
			case 4://Show Location
				player.sendPacket(new RadarControl(1, 2, 0, 0, 0));
				break;
			case 5:
				L2NpcInstance aliveInstance1 = getAliveBoss(bossId);
				if(aliveInstance1 != null)
					player.enterObserverMode(aliveInstance1.getLoc(), null);
				else
					player.sendMessage(player.isLangRus() ? "Босс мертв!" : "Boss is dead!");
				break;
			default:
				break;
				
		}
	}

	/**
	 * Replacing all %a% words by real Values in Detailed Boss Page
	 * @param html current Html
	 * @param bossSet StatsSet of the boss
	 * @param bossTemplate L2NpcTemplate of the boss
	 * @param bossInstance any Instance of the boss(can be null)
	 * @return filled HTML
	 */
	private static String getDetailedBossReplacements(L2Player player, String html, StatsSet bossSet, L2NpcTemplate bossTemplate, L2NpcInstance bossInstance)
	{
		String newHtml = html;

		boolean isAlive = isBossAlive(bossSet);

		newHtml = newHtml.replace("<?name?>", bossTemplate.name);
		newHtml = newHtml.replace("<?level?>", String.valueOf(bossTemplate.level));
		newHtml = newHtml.replace("<?status?>", isAlive ? (player.isLangRus() ? "Жив" : "Alive") : getRespawnTime(bossSet));
		newHtml = newHtml.replace("<?status_color?>", getTextColor(isAlive));
		newHtml = newHtml.replace("<?minions?>", String.valueOf(getMinionsCount(bossTemplate)));

		newHtml = newHtml.replace("<?currentHp?>", Util.formatAdena((int) (bossInstance != null ? (int) bossInstance.getCurrentHp() : 0)));
		newHtml = newHtml.replace("<?maxHp?>", Util.formatAdena((int) bossTemplate.baseHpMax));
		newHtml = newHtml.replace("<?minions?>", String.valueOf(getMinionsCount(bossTemplate)));

		return newHtml;
	}

	/**
	 * Replacing page, sorts, bossId, search
	 * @param html to fill
	 * @param page number
	 * @param sort type
	 * @param search word
	 * @param bossId If of the boss, set -1 if doesn't matter
	 * @return new Html page
	 */
	private static String getNormalReplacements(String html, int page, SortType sort, CharSequence search, int bossId)
	{
		String newHtml = html;
		newHtml = newHtml.replace("<?page?>", String.valueOf(page));
		newHtml = newHtml.replace("<?sort?>", String.valueOf(sort.index));
		newHtml = newHtml.replace("<?bossId?>", String.valueOf(bossId));
		newHtml = newHtml.replace("<?search?>", search);

		for(int i = 1; i <= 6; i++)
		{
			if(Math.abs(sort.index) == i)
				newHtml = newHtml.replace("<?sort" + i + "?>", String.valueOf(-sort.index));
			else
				newHtml = newHtml.replace("<?sort" + i + "?>", String.valueOf(i));
		}

		return newHtml;
	}

	private static boolean isBossAlive(StatsSet set)
	{
		return (long) set.getInteger("respawn_delay", 0) < System.currentTimeMillis() / TimeUnit.SECONDS.toMillis(1L);
	}

	private static String getRespawnTime(StatsSet set)
	{
		if(set.getInteger("respawn_delay", 0) < System.currentTimeMillis() / TimeUnit.SECONDS.toMillis(1L))
			return "isAlive";
		
		long delay = set.getInteger("respawn_delay", 0)-(System.currentTimeMillis() / TimeUnit.SECONDS.toMillis(1L));

		//System.out.println(delay);
		int hours = (int) (delay / 60 / 60);
		int mins = (int) ((delay - (hours * 60 * 60)) / 60);
		int secs = (int) ((delay - ((hours * 60 * 60) + (mins * 60))));
		
		String Strhours = hours < 10 ? "0"+hours : ""+hours;
		String Strmins = mins < 10 ? "0"+mins : ""+mins;
		String Strsecs = secs < 10 ? "0"+secs : ""+secs;

		return "<font color=\"b02e31\">"+Strhours+":"+Strmins+":"+Strsecs+"</font>";
	}	
	
	/**
	 * Getting alive and visible instance of the bossId
	 * @param bossId Id of the boss
	 * @return Instance of the boss
	 */
	private static L2NpcInstance getAliveBoss(int bossId)
	{
		List<L2NpcInstance> instances = L2ObjectsStorage.getAllByNpcId(bossId, true, true);
		return instances.isEmpty() ? null : instances.get(0);
	}

	private static int getMinionsCount(L2NpcTemplate template)
	{
		int minionsCount = 0;
		for(L2MinionData minion : template.getMinionData())
			minionsCount += minion.getAmount();
		return minionsCount;
	}

	private static String getTextColor(boolean alive)
	{
		if(alive)
			return "259a30";//"327b39";
		else
			return "b02e31";//"8f3d3f";
	}

	/**
	 * Getting List of Bosses that player is looking for(including sort and search)
	 * @param sort Type of sorting he want to use
	 * @param search word that he is looking for
	 * @return Map of Bosses
	 */
	private static Map<Integer, StatsSet> getSearchedBosses(SortType sort, String search)
	{
		Map<Integer, StatsSet> result = getBossesMapBySearch(search);

		for(int id : BOSSES_TO_NOT_SHOW)
			result.remove(id);

		result = sortResults(result, sort);

		return result;
	}

	/**
	 * Getting List of Bosses that player is looking for(including search)
	 * @param search String that boss Name needs to contains(can be Empty)
	 * @return MapMap of Bosses
	 */
	private static Map<Integer, StatsSet> getBossesMapBySearch(String search)
	{
		Map<Integer, StatsSet> finalResult = new HashMap<Integer, StatsSet>();
		if(search.isEmpty())
		{
			finalResult = RaidBossSpawnManager.getInstance().getAllBosses();
		}
		else
		{
			for(Entry<Integer, StatsSet> entry : RaidBossSpawnManager.getInstance().getAllBosses().entrySet())
			{
				L2NpcTemplate temp = NpcTable.getTemplate(entry.getKey().intValue());
				try
				{
					if(StringUtils.containsIgnoreCase(temp.name, search))
						finalResult.put(entry.getKey(), entry.getValue());
				}
				catch(Exception e)
				{
					_log.info("CommunityBosses: ERROR->getBossesMapBySearch: temp["+entry.getKey().intValue()+"]=null");
				}
			}
		}
		return finalResult;
	}

	/**
	 * Sorting results by sort type
	 * @param result map to sort
	 * @param sort type
	 * @return sorted Map
	 */
	private static Map<Integer, StatsSet> sortResults(Map<Integer, StatsSet> result, SortType sort)
	{
		ValueComparator bvc = new ValueComparator(result, sort);
		Map<Integer, StatsSet> sortedMap = new TreeMap<Integer, StatsSet>(bvc);
		sortedMap.putAll(result);
		return sortedMap;
	}

	/**
	 * Comparator of Bosses
	 */
	private static class ValueComparator implements Comparator<Integer>, Serializable
	{
		private static final long serialVersionUID = 4782405190873267622L;
		private final Map<Integer, StatsSet> base;
		private final SortType sortType;

		private ValueComparator(Map<Integer, StatsSet> base, SortType sortType)
		{
			this.base = base;
			this.sortType = sortType;
		}

		@Override
		public int compare(Integer o1, Integer o2)
		{
			int sortResult = sortById(o1, o2, sortType);
			if(sortResult == 0 && !o1.equals(o2) && Math.abs(sortType.index) != 1)
				sortResult = sortById(o1, o2, SortType.NAME_ASC);
			return sortResult;
		}

		/**
		 * Comparing a and b but sorting
		 * @param a first variable
		 * @param b second variable
		 * @param sorting type of sorting
		 * @return result of comparing
		 */
		private int sortById(Integer a, Integer b, SortType sorting)
		{
			L2NpcTemplate temp1 = NpcTable.getTemplate(a.intValue());
			L2NpcTemplate temp2 = NpcTable.getTemplate(b.intValue());
			StatsSet set1 = base.get(a);
			StatsSet set2 = base.get(b);
			if(temp1 == null || temp2 == null)
				return 0;
			switch(sorting)
			{
				case NAME_ASC:
					return temp1.name.compareTo(temp2.name);
				case NAME_DESC:
					return temp2.name.compareTo(temp1.name);
				case LEVEL_ASC:
					return Integer.compare(temp1.level, temp2.level);
				case LEVEL_DESC:
					return Integer.compare(temp2.level, temp1.level);
				case STATUS_ASC:
					return Integer.compare(set1.getInteger("respawn_delay", 0), set2.getInteger("respawn_delay", 0));
				case STATUS_DESC:
					return Integer.compare(set2.getInteger("respawn_delay", 0), set1.getInteger("respawn_delay", 0));
			}
			return 0;
		}
	}

	private enum SortType
	{
		NAME_ASC(1),
		NAME_DESC(-1),
		LEVEL_ASC(2),
		LEVEL_DESC(-2),
		STATUS_ASC(3),
		STATUS_DESC(-3);

		public final int index;

		SortType(int index)
		{
			this.index = index;
		}
	}
	
	public static L2Player getSelf()
	{
		return self.get();
	}

	/**
	 * Getting SortType by index
	 * @param i index
	 * @return SortType
	 */
	private static SortType getSortByIndex(int i)
	{
		for(SortType type : SortType.values())
			if(type.index == i)
				return type;
		return SortType.NAME_ASC;
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{}

	@Override
	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}
}