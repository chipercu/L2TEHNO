package l2open.gameserver.instancemanager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javolution.util.FastList;
import javolution.util.FastMap;

import l2open.database.*;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Player;

/**
 * @author sednka
 */
public class PlayerLevelRewardManager
{
	private final String SQL_INSERT = "INSERT INTO level_rewards VALUES(?,?,?)";
	private final String SQL_SELECT_CLS_REWARDS = "SELECT classLevel FROM level_rewards WHERE objectId=? AND classId=?";
	private final String SQL_SELECT_CLS_REWARDS2 = "SELECT classLevel FROM level_rewards WHERE objectId=?";
	
	private FastMap<Integer, FastList<RewardItem>> rewards;
	private FastMap<L2Player, FastMap<Integer, FastList<Integer>>> issuedRewards;
	private FastMap<Integer, FastMap<Integer, FastList<Integer>>> reward_compl;
	
	private static PlayerLevelRewardManager _instance;
	private static final Logger _log = Logger.getLogger(PlayerLevelRewardManager.class.getName());
	
	private PlayerLevelRewardManager()
	{
		rewards = new FastMap<Integer, FastList<RewardItem>>();
		issuedRewards = new FastMap<L2Player, FastMap<Integer, FastList<Integer>>>();
		reward_compl = new FastMap<Integer, FastMap<Integer, FastList<Integer>>>();
		parseData(ConfigValue.PLRM_Rewards);
		_log.info("LevelRewardsManager: Loaded for " + rewards.keySet().size() + " levels.");
	}
	
	private void parseData(String text)
	{
		StringTokenizer st = new StringTokenizer(text, ",;");
		if(st.countTokens() % 3 != 0 && (!ConfigValue.PLRM_NewFormatRewards || st.countTokens() % 4 != 0))
		{
			_log.warning("LevelRewardsManager: Wrong rewards data.");
			return;
		}
		while(st.hasMoreTokens())
		{
			int level = Integer.parseInt(st.nextToken().trim());
			FastList<RewardItem> rewardsList = rewards.get(level);
			if(rewardsList == null) 
			{
				rewardsList = new FastList<RewardItem>();
				rewards.put(level, rewardsList);
			}
			rewardsList.add(new RewardItem(Integer.parseInt(st.nextToken().trim()), Integer.parseInt(st.nextToken().trim()), ConfigValue.PLRM_NewFormatRewards ? Integer.parseInt(st.nextToken().trim()) : 0));
		}
	}

	public void rewardPlayer(L2Player player)
	{
		int playerLevel = player.getLevel();
		for(int level: rewards.keySet())
			if(playerLevel >= level && isRewardReq(player, level))
				addReward(player, level);
	}

	private boolean isRewardReq(L2Player player, int playerLevel)
	{
		int playerObjectId = player.getObjectId();
		int playerCls = player.getActiveClassId();

		if(ConfigValue.PLRM_RewardsTypeNew)
		{
			if(reward_compl.containsKey(playerObjectId))
			{
				FastList<Integer> class_for_level = reward_compl.get(playerObjectId).get(playerLevel);
				if(class_for_level == null)
					class_for_level = fillClassIdList(playerObjectId, playerLevel);
				if(class_for_level.size() > ConfigValue.PLRM_RewardsSubCount || class_for_level.contains(playerCls))
					return false;
				class_for_level.add(playerCls); // Добавляем класс, в список уровней...

				FastMap<Integer, FastList<Integer>> clsLevelRewards = new FastMap<Integer, FastList<Integer>>();
				clsLevelRewards.put(playerLevel, class_for_level);
				reward_compl.put(playerObjectId, clsLevelRewards);
				addToBase(playerObjectId, playerCls, playerLevel);
				return true;
			}
			else
			{
				FastMap<Integer, FastList<Integer>> clsLevelRewards = new FastMap<Integer, FastList<Integer>>();
				FastList<Integer> class_for_level = fillClassIdList(playerObjectId, playerLevel);
				clsLevelRewards.put(playerLevel, class_for_level);
				reward_compl.put(playerObjectId, clsLevelRewards);
				if(class_for_level.size() <= ConfigValue.PLRM_RewardsSubCount && !class_for_level.contains(playerCls))
				{
					class_for_level.add(playerCls);
					addToBase(playerObjectId, playerCls, playerLevel);
					return true;
				}
			}
		}
		else
		{
			playerCls = -1;
			if(issuedRewards.containsKey(player))
			{
				FastList<Integer> levels = issuedRewards.get(player).get(playerCls);
				if(levels == null)
					levels = fillLevelsList(playerObjectId, playerCls, true); // fillClassIdList(playerObjectId, playerLevel);
				if(levels.contains(playerLevel))
					return false;
				levels.add(playerLevel);
				addToBase(playerObjectId, playerCls, playerLevel);
				return true;
			}
			else 
			{
				FastMap<Integer, FastList<Integer>> clsLevelRewards = new FastMap<Integer, FastList<Integer>>();
				FastList<Integer> levels = fillLevelsList(playerObjectId, playerCls, true); // fillClassIdList(playerObjectId, playerLevel);
				clsLevelRewards.put(playerCls, levels);
				issuedRewards.put(player, clsLevelRewards);
				if(!levels.contains(playerLevel))
				{
					levels.add(playerLevel);
					addToBase(playerObjectId, playerCls, playerLevel);
					return true;
				}
			}
		}
		return false;
	}
	private void addReward(L2Player player, int level)
	{
		FastList<RewardItem> items = rewards.get(level);
		for(RewardItem item: items)
		{
			Functions.addItem(player, item.id, item.count, item.enchant);
			if(ConfigValue.PLRM_PrintInfo)
				_log.info("LevelRewardsManager: Player " + player.getName() + " [" + player.getObjectId() + "] (classId: " + player.getActiveClass() + ")" + " received item: ID " + item.id + "[+"+item.enchant+"] count " + item.count + " (" + level + " Lvl).");
		}
		if(ConfigValue.PLRM_SendMessageToChar)
			player.sendMessage(ConfigValue.PLRM_MessageToChar.replace("%level%", String.valueOf(level)));
	}
	public void changeClass(final L2Player player, final int oldclass, final int newclass)
	{
		FastMap<Integer, FastList<Integer>> _inf = reward_compl.get(player.getObjectId());
		if(_inf != null)
			for(FastList<Integer> class_list : _inf.values())
				for(int i=0;i<class_list.size();i++)
					if(class_list.get(i) == oldclass)
					{
						class_list.set(i, newclass);
						break;
					}
	}
	private FastList<Integer> fillClassIdList(int objectId, int classLevel)
	{
		FastList<Integer> classId = new FastList<Integer>();
		ThreadConnection con = null;
		FiltredPreparedStatement stat = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			stat = con.prepareStatement("SELECT classId FROM level_rewards WHERE objectId=? AND classLevel=?");
			stat.setInt(1, objectId);
			stat.setInt(2, classLevel);
			rset = stat.executeQuery();
			while(rset.next())
				classId.add(rset.getInt("classId"));
		}
		catch(SQLException e)
		{
			_log.warning("LevelRewardsManager: ERROR " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, stat, rset);
		}
		return classId;
	}
	private FastList<Integer> fillLevelsList(int objectId, int classId, boolean is)
	{
		FastList<Integer> levels = new FastList<Integer>();
		ThreadConnection con = null;
		FiltredPreparedStatement stat = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			stat = con.prepareStatement(is ? SQL_SELECT_CLS_REWARDS : SQL_SELECT_CLS_REWARDS2);
			stat.setInt(1, objectId);
			if(is)
				stat.setInt(2, classId);
			rset = stat.executeQuery();
			while(rset.next())
				levels.add(rset.getInt("classLevel"));
		}
		catch(SQLException e)
		{
			_log.warning("LevelRewardsManager: ERROR " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, stat, rset);
		}
		return levels;
	}
	private void addToBase(int objectId, int classId, int level)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement stat = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			stat = con.prepareStatement(SQL_INSERT);
			stat.setInt(1, objectId);
			stat.setInt(2, classId);
			stat.setInt(3, level);
			stat.executeUpdate();
		}
		catch(SQLException e)
		{
			_log.warning("LevelRewardsManager: ERROR " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, stat);
		}
	}
	
	public class RewardItem
	{
		public final int id;
		public final int count;
		public final int enchant;
		
		public RewardItem(int itemId, int itemCount, int enchant_)
		{
			id = itemId;
			count = itemCount;
			enchant = enchant_;
		}
	}
	
	public static PlayerLevelRewardManager getInstance()
	{
		if(_instance == null)
			_instance = new PlayerLevelRewardManager();
		return _instance;
	}
}