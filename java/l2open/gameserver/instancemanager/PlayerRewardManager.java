package l2open.gameserver.instancemanager;

import java.util.logging.Logger;


import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.player.PlayerData;

/**
 * @author Diagod
 */
/**
1. Первые 34 профессии 85 лвл.
2. Первый нубл на сервере ( один ).
3. Первый кто сделает 10 пвп.
4. Первый кто сделает 10 пк.
**/
public class PlayerRewardManager
{
	private static final Logger _log = Logger.getLogger(PlayerRewardManager.class.getName());
	private static PlayerRewardManager _instance;

	private static boolean[] inc_level = new boolean[48];
	private static int set_noble;
	private static int inc_pvp;
	private static int inc_pc;

	private PlayerRewardManager()
	{
		for(int i=0;i<48;i++)
			inc_level[i] = ServerVariables.getBool("pr_inc_level_"+i, false);
		set_noble = ServerVariables.getInt("pr_set_noble", 0);
		inc_pvp = ServerVariables.getInt("pr_inc_pvp", 0);
		inc_pc = ServerVariables.getInt("pr_inc_pc", 0);
		_log.info("PlayerRewardManager: set_noble["+set_noble+"] inc_pvp["+inc_pvp+"] inc_pc["+inc_pc+"].");
	}

	public static PlayerRewardManager getInstance()
	{
		if(_instance == null)
			_instance = new PlayerRewardManager();
		return _instance;
	}

	public void inc_level(L2Player player)
	{
		if(ConfigValue.PLRM_Enable)
			PlayerLevelRewardManager.getInstance().rewardPlayer(player);
		// ----
		int class_id = player.class_id()-88;
		if(ConfigValue.PlayerRewardManager_Enable && player.getLevel() >= ConfigValue.PlayerRewardManager_LevelForReward && !inc_level[class_id] && !player.getVarB("preward_level", false))
		{
			inc_level[class_id] = true;
			ServerVariables.set("pr_inc_level_"+class_id, String.valueOf(true));
			player.setVar("preward_level", String.valueOf(true));
			for(int i=0; i < ConfigValue.PlayerRewardManager_LevelReward.length; i+=2)
				Functions.addItem(player, (int)ConfigValue.PlayerRewardManager_LevelReward[i], ConfigValue.PlayerRewardManager_LevelReward[i+1]);
			player.sendMessage("Вы стали одним из победителей акции Достижения на сервере.");
		}
	}

	public void set_noble(L2Player player)
	{
		PlayerData.getInstance().checkReferralBonus(player, 3);
		// ----
		if(ConfigValue.PlayerRewardManager_Enable && set_noble < ConfigValue.PlayerRewardManager_NobleCountReward && !player.getVarB("preward_noble", false))
		{
			set_noble++;
			ServerVariables.set("pr_set_noble", String.valueOf(set_noble));
			player.setVar("preward_noble", String.valueOf(true));
			for(int i=0; i < ConfigValue.PlayerRewardManager_NobleReward.length; i+=2)
				Functions.addItem(player, (int)ConfigValue.PlayerRewardManager_NobleReward[i], ConfigValue.PlayerRewardManager_NobleReward[i+1]);
			player.sendMessage("Вы стали одним из победителей акции Достижения на сервере.");
		}
	}

	public void inc_pvp(L2Player killer, L2Player player)
	{
		if(killer.getAttainment() != null)
			killer.getAttainment().incPvp(player);
		// ----
		if(ConfigValue.PlayerRewardManager_Enable && killer.getPvpKills() >= ConfigValue.PlayerRewardManager_PvpForReward && inc_pvp < ConfigValue.PlayerRewardManager_PvpCountReward && !killer.getVarB("preward_pvp", false))
		{
			inc_pvp++;
			ServerVariables.set("pr_inc_pvp", String.valueOf(inc_pvp));
			killer.setVar("preward_pvp", String.valueOf(true));
			for(int i=0; i < ConfigValue.PlayerRewardManager_PvpReward.length; i+=2)
				Functions.addItem(killer, (int)ConfigValue.PlayerRewardManager_PvpReward[i], ConfigValue.PlayerRewardManager_PvpReward[i+1]);
			killer.sendMessage("Вы стали одним из победителей акции Достижения на сервере.");
		}
	}

	public void inc_pc(L2Player killer, L2Player player)
	{
		if(killer.getAttainment() != null)
			killer.getAttainment().incPk(player);
		// ----
		if(ConfigValue.PlayerRewardManager_Enable && killer.getPkKills() >= ConfigValue.PlayerRewardManager_PcForReward && inc_pc < ConfigValue.PlayerRewardManager_PcCountReward && !killer.getVarB("preward_pc", false))
		{
			inc_pc++;
			ServerVariables.set("pr_inc_pc", String.valueOf(inc_pc));
			killer.setVar("preward_pc", String.valueOf(true));
			for(int i=0; i < ConfigValue.PlayerRewardManager_PcReward.length; i+=2)
				Functions.addItem(killer, (int)ConfigValue.PlayerRewardManager_PcReward[i], ConfigValue.PlayerRewardManager_PcReward[i+1]);
			killer.sendMessage("Вы стали одним из победителей акции Достижения на сервере.");
		}
	}
}