package communityboard;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import l2open.config.*;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Files;
import l2open.util.RateService;
import l2open.util.ValueSortMap;
import l2open.util.Util;

import bosses.EpicBossState;

public class CommunityBoardStats extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	static final Logger _log = Logger.getLogger(CommunityBoardFullStats.class.getName());

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}

	private static enum Commands
	{
		_bbsstat
	}

	private static Map<Integer, StatsSet> _top_pts;

	public static class Manager
	{
		public String[] TopPvPName = new String[5];
		public int[] TopPvPPvP = new int[5];

		public String[] TopPkName = new String[5];
		public int[] TopPk = new int[5];

	}

	public static class RaidBoss
	{
		public String[] RaidBossName = new String[6];
		public byte[] RaidBossStatus = new byte[6];
	}

	static Manager ManagerStats = new Manager();
	static RaidBoss RaidBossStats = new RaidBoss();

	public long updateTopPK = 0;

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;

		if(bypass.equals("_bbsstat"))
		{
			if(updateTopPK + ConfigValue.StatisticUpdateTopPK * 60 < System.currentTimeMillis() / 1000)
			{
				reloadAll();

				updateTopPK = System.currentTimeMillis() / 1000;
			}
			show(player);
		}
	}

	private void show(L2Player player)
	{
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/index.htm", player);
		CustomMessage No = new CustomMessage("common.result.no", player);
		CustomMessage Yes = new CustomMessage("common.result.yes", player);

		for(int i=0;i<5;i++)
		{
			if(ManagerStats.TopPkName[i] != null)
			{
				html = html.replace("<?pk_name_" + i + "?>", ManagerStats.TopPkName[i]);
				html = html.replace("<?pk_count_" + i + "?>", Integer.toString(ManagerStats.TopPk[i]));
			}
			else
			{
				html = html.replace("<?pk_name_" + i + "?>", "&nbsp;");
				html = html.replace("<?pk_count_" + i + "?>", "&nbsp;");
			}

			if(ManagerStats.TopPkName[i] != null)
			{
				html = html.replace("<?pvp_name_" + i + "?>", ManagerStats.TopPvPName[i]);
				html = html.replace("<?pvp_count_" + i + "?>", Integer.toString(ManagerStats.TopPvPPvP[i]));
			}
			else
			{
				html = html.replace("<?pvp_name_" + i + "?>", "&nbsp;");
				html = html.replace("<?pvp_count_" + i + "?>", "&nbsp;");
			}
		}

		for(int i=0;i<6;i++)
		{
			if(RaidBossStats.RaidBossName[i] != null)
			{
				html = html.replace("<?rb_name_" + i + "?>", RaidBossStats.RaidBossName[i]);
				html = html.replace("<?rb_status_" + i + "?>", RaidBossStats.RaidBossStatus[i] == 0 ? "<font color=\"f41100\">Мертв</font>" : "<font color=\"ffedec\">Жив</font>");
			}
			else
			{
				html = html.replace("<?rb_name_" + i + "?>", "&nbsp;");
				html = html.replace("<?rb_status_" + i + "?>", "&nbsp;");
			}
		}

		int index=0;
		for(Entry<Integer, StatsSet> entry : _top_pts.entrySet())
		{
			StatsSet set = entry.getValue();

			html = html.replace("<?olymp_name_" + index + "?>", set.getString(Olympiad.CHAR_NAME, ""));
			html = html.replace("<?olymp_count_" + index + "?>", String.valueOf(set.getInteger(Olympiad.POINTS)));
			index++;
		}
		for(int i=index;i<10;i++)
		{
				html = html.replace("<?olymp_name_" + i + "?>", "&nbsp;");
				html = html.replace("<?olymp_count_" + i + "?>", "&nbsp;");
		}
		html = html.replace("<?olymp_win_rate?>", String.valueOf(Olympiad.getCompetitionDone(player.getObjectId()) > 0 ? Olympiad.getCompetitionWin(player.getObjectId())/Olympiad.getCompetitionDone(player.getObjectId())*100 : 0));

		separateAndSend(addCustomReplace(html), player);
	}

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	public static String date(long time)
	{
		return DATE_FORMAT.format(new Date(time * 1000));
	}

	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final DateFormat TIME_FORMAT2 = new SimpleDateFormat("HH:00");

	public static String time(long time)
	{
		if(ConfigValue.StatisticRaidShowMin)
			return TIME_FORMAT.format(new Date(time * 1000));
		return TIME_FORMAT2.format(new Date(time * 1000));
	}

	public void selectTopPVP()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		int number = 0;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name, obj_Id, pvpkills FROM characters ORDER BY pvpkills DESC LIMIT 10");
			rset = statement.executeQuery();

			while(rset.next())
			{
				if(!rset.getString("char_name").isEmpty())
				{
					int obj_Id = rset.getInt("obj_Id");
					if(ConfigSystem.gmlist.containsKey(obj_Id) && ConfigSystem.gmlist.get(obj_Id).IsGM)
						continue;
					ManagerStats.TopPvPName[number] = rset.getString("char_name");
					ManagerStats.TopPvPPvP[number] = rset.getInt("pvpkills");
				}
				else
				{
					ManagerStats.TopPvPName[number] = null;
					ManagerStats.TopPvPPvP[number] = 0;
				}
				if(++number >= 5)
					break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return;
	}

	public void selectTopPK()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		int number = 0;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name, obj_Id, pkkills FROM characters ORDER BY pkkills DESC LIMIT 10");
			rset = statement.executeQuery();

			while(rset.next())
			{
				if(!rset.getString("char_name").isEmpty())
				{
					int obj_Id = rset.getInt("obj_Id");
					if(ConfigSystem.gmlist.containsKey(obj_Id) && ConfigSystem.gmlist.get(obj_Id).IsGM)
						continue;
					ManagerStats.TopPkName[number] = rset.getString("char_name");
					ManagerStats.TopPk[number] = rset.getInt("pkkills");
				}
				else
				{
					ManagerStats.TopPkName[number] = null;
					ManagerStats.TopPk[number] = 0;
				}
				if(++number >= 5)
					break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return;
	}

	public void selectOlyStat()
	{
		_top_pts = Collections.synchronizedMap(Olympiad._nobles);
		_top_pts = (LinkedHashMap)ValueSortMap.sortMapByValue(_top_pts, comparator_pts);
	}

	public void selectEpicBoss()
	{
		int number = 0;

		for(int i = 0; i < 6; i++)
		{
			int npc_id = ConfigValue.StatisticRaid[i];
			if(npc_id != 29118)
			{
				long time = RaidBossSpawnManager.getInstance().getRespawnTime(npc_id)+System.currentTimeMillis();

				_log.info("npc_id="+npc_id+" time="+RaidBossSpawnManager.getInstance().getRespawnTime(npc_id));
				L2NpcTemplate template = NpcTable.getTemplate(npc_id);
				RaidBossStats.RaidBossName[number] = template.name;

				if(time > System.currentTimeMillis())
					RaidBossStats.RaidBossStatus[number] = 0;
				else if(CommunityBoardFullStats._epic_state.containsKey(npc_id))
				{
					EpicBossState state = CommunityBoardFullStats._epic_state.get(npc_id);
					
					_log.info("npc_id="+npc_id+" state="+state.getRespawnDate());
					if(state.getRespawnDate() > System.currentTimeMillis())
						RaidBossStats.RaidBossStatus[number] = 0;
					else
						RaidBossStats.RaidBossStatus[number] = 1;
				}
				else
					RaidBossStats.RaidBossStatus[number] = 1;
				number++;
			}
		}
		if(6 > number && Util.contains(ConfigValue.StatisticRaid, 29118))
		{
			L2NpcTemplate template = NpcTable.getTemplate(29118);
			RaidBossStats.RaidBossName[number] = template.name;

			long res = ServerVariables.getLong("BelethKillTime", 0);
			if(res > System.currentTimeMillis())
				RaidBossStats.RaidBossStatus[number] = 0;
			else
				RaidBossStats.RaidBossStatus[number] = 1;
		}
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}

	static Comparator comparator_pts = new Comparator<StatsSet>()
	{
		@Override
		public int compare(StatsSet o1, StatsSet o2)
		{
			return (int) (o2.getInteger(Olympiad.POINTS) - o1.getInteger(Olympiad.POINTS));
		}
	};

	@Override
	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);

		// К - костыли
		reloadAll();
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			public void run()
			{
				reloadAll();
			}
		}, 15000);
		_log.info("Full statistics in the commynity board has been updated.");
	}

	private void reloadAll()
	{
		selectOlyStat();
		selectTopPK();
		selectTopPVP();
		selectEpicBoss();
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}