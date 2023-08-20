package events.bountyhunters;

import l2open.config.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2ChestInstance;
import l2open.gameserver.model.instances.L2DeadManInstance;
import l2open.gameserver.model.instances.L2FestivalMonsterInstance;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2PenaltyMonsterInstance;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.model.instances.L2TamedBeastInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Rnd;
import npc.model.KanabionInstance;

public class HuntersGuild extends Functions implements ScriptFile, IVoicedCommandHandler
{
	private static final String[] _commandList = new String[] { "gettask", "declinetask" };

	public void onLoad()
	{
		if(!ConfigValue.BountyHuntersEnabled)
			return;
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		_log.info("Loaded Event: Bounty Hunters Guild");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static boolean checkTarget(L2NpcTemplate npc)
	{
		if(!npc.isInstanceOf(L2MonsterInstance.class))
			return false;
		if(npc.revardExp == 0)
			return false;
		if(npc.isInstanceOf(L2RaidBossInstance.class))
			return false;
		if(npc.isInstanceOf(npc.model.SquashInstance.class))
			return false;
		if(npc.isInstanceOf(L2PenaltyMonsterInstance.class))
			return false;
		if(npc.isInstanceOf(L2MinionInstance.class))
			return false;
		if(npc.isInstanceOf(L2FestivalMonsterInstance.class))
			return false;
		if(npc.isInstanceOf(L2TamedBeastInstance.class))
			return false;
		if(npc.isInstanceOf(L2DeadManInstance.class))
			return false;
		if(npc.isInstanceOf(L2ChestInstance.class))
			return false;
		if(npc.isInstanceOf(KanabionInstance.class))
			return false;
		if(npc.title.contains("Quest Monster"))
			return false;
		if(L2ObjectsStorage.getByNpcId(npc.getNpcId()) == null)
			return false;
		return true;
	}

	public void getTask(L2Player player, int id)
	{
		if(!ConfigValue.BountyHuntersEnabled)
			return;
		L2NpcTemplate target;
		double mod = 1.;
		if(id == 0)
		{
			GArray<L2NpcTemplate> monsters = NpcTable.getAllOfLevel(player.getLevel());
			if(monsters == null || monsters.isEmpty())
			{
				show(new CustomMessage("scripts.events.bountyhunters.NoTargets", player), player);
				return;
			}
			GArray<L2NpcTemplate> targets = new GArray<L2NpcTemplate>();
			for(L2NpcTemplate npc : monsters)
				if(checkTarget(npc))
					targets.add(npc);
			if(targets.isEmpty())
			{
				show(new CustomMessage("scripts.events.bountyhunters.NoTargets", player), player);
				return;
			}
			target = targets.get(Rnd.get(targets.size()));
		}
		else
		{
			target = NpcTable.getTemplate(id);
			if(target == null || !checkTarget(target))
			{
				show(new CustomMessage("scripts.events.bountyhunters.WrongTarget", player), player);
				return;
			}
			if(player.getLevel() - target.level > 5)
			{
				show(new CustomMessage("scripts.events.bountyhunters.TooEasy", player), player);
				return;
			}
			mod = 0.5 * (10 + target.level - player.getLevel()) / 10.;
		}

		int mobcount = target.level + Rnd.get(25, 50);
		player.setVar("bhMonstersId", String.valueOf(target.getNpcId()));
		player.setVar("bhMonstersNeeded", String.valueOf(mobcount));
		player.setVar("bhMonstersKilled", "0");

		int fails = player.getVar("bhfails") == null ? 0 : Integer.parseInt(player.getVar("bhfails")) * 5;
		int success = player.getVar("bhsuccess") == null ? 0 : Integer.parseInt(player.getVar("bhsuccess")) * 5;

		double reputation = Math.min(Math.max((100 + success - fails) / 100., .25), 2.) * mod;

		long adenarewardvalue = Math.round((target.level * Math.max(Math.log(target.level), 1) * 10 + Math.max((target.level - 60) * 33, 0) + Math.max((target.level - 65) * 50, 0)) * target.expRate * mobcount * ConfigSystem.getRateAdena(player) * reputation * .15);
		if(Rnd.chance(30)) // Адена, 30% случаев
		{
			player.setVar("bhRewardId", "57");
			player.setVar("bhRewardCount", String.valueOf(adenarewardvalue));
		}
		else
		{ // Кристаллы, 70% случаев
			int crystal = 0;
			if(target.level <= 39)
				crystal = 1458; // D
			else if(target.level <= 51)
				crystal = 1459; // C
			else if(target.level <= 60)
				crystal = 1460; // B
			else if(target.level <= 75)
				crystal = 1461; // A
			else
				crystal = 1462; // S
			player.setVar("bhRewardId", String.valueOf(crystal));
			player.setVar("bhRewardCount", String.valueOf(adenarewardvalue / ItemTemplates.getInstance().getTemplate(crystal).getReferencePrice()));
		}
		show(new CustomMessage("scripts.events.bountyhunters.TaskGiven", player).addNumber(mobcount).addString(target.name), player);
	}

	public static void OnDie(L2Character cha, L2Character killer)
	{
		if(!ConfigValue.BountyHuntersEnabled)
			return;
		if(cha.isMonster() && !cha.isRaid() && killer != null && killer.getPlayer() != null && killer.getPlayer().getVar("bhMonstersId") != null && Integer.parseInt(killer.getPlayer().getVar("bhMonstersId")) == cha.getNpcId())
		{
			int count = Integer.parseInt(killer.getPlayer().getVar("bhMonstersKilled")) + 1;
			killer.getPlayer().setVar("bhMonstersKilled", String.valueOf(count));
			int needed = Integer.parseInt(killer.getPlayer().getVar("bhMonstersNeeded"));
			if(count >= needed)
				doReward(killer.getPlayer());
			else
				sendMessage(new CustomMessage("scripts.events.bountyhunters.NotifyKill", killer.getPlayer()).addNumber(needed - count), killer.getPlayer());
		}
	}

	private static void doReward(L2Player player)
	{
		if(!ConfigValue.BountyHuntersEnabled)
			return;
		int rewardid = Integer.parseInt(player.getVar("bhRewardId"));
		long rewardcount = Long.parseLong(player.getVar("bhRewardCount"));
		player.unsetVar("bhMonstersId");
		player.unsetVar("bhMonstersNeeded");
		player.unsetVar("bhMonstersKilled");
		player.unsetVar("bhRewardId");
		player.unsetVar("bhRewardCount");
		if(player.getVar("bhsuccess") != null)
			player.setVar("bhsuccess", String.valueOf(Integer.parseInt(player.getVar("bhsuccess")) + 1));
		else
			player.setVar("bhsuccess", "1");
		addItem(player, rewardid, rewardcount);
		show(new CustomMessage("scripts.events.bountyhunters.TaskCompleted", player).addNumber(rewardcount).addItemName(rewardid), player);
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	public boolean useVoicedCommand(String command, L2Player activeChar, String target)
	{
		if(activeChar == null || !ConfigValue.BountyHuntersEnabled)
			return false;
		if(activeChar.getLevel() < 20)
		{
			sendMessage(new CustomMessage("scripts.events.bountyhunters.TooLowLevel", activeChar), activeChar);
			return true;
		}
		if(command.equalsIgnoreCase("gettask"))
		{
			if(activeChar.getVar("bhMonstersId") != null)
			{
				int mobid = Integer.parseInt(activeChar.getVar("bhMonstersId"));
				int mobcount = Integer.parseInt(activeChar.getVar("bhMonstersNeeded")) - Integer.parseInt(activeChar.getVar("bhMonstersKilled"));
				show(new CustomMessage("scripts.events.bountyhunters.TaskGiven", activeChar).addNumber(mobcount).addString(NpcTable.getTemplate(mobid).name), activeChar);
				return true;
			}
			int id = 0;
			//if(target != null && target.trim().matches("[\\d]{1,9}"))
			//	id = Integer.parseInt(target);
			getTask(activeChar, id);
			return true;
		}
		if(command.equalsIgnoreCase("declinetask"))
		{
			if(activeChar.getVar("bhMonstersId") == null)
			{
				sendMessage(new CustomMessage("scripts.events.bountyhunters.NoTask", activeChar), activeChar);
				return true;
			}
			activeChar.unsetVar("bhMonstersId");
			activeChar.unsetVar("bhMonstersNeeded");
			activeChar.unsetVar("bhMonstersKilled");
			activeChar.unsetVar("bhRewardId");
			activeChar.unsetVar("bhRewardCount");
			if(activeChar.getVar("bhfails") != null)
				activeChar.setVar("bhfails", String.valueOf(Integer.parseInt(activeChar.getVar("bhfails")) + 1));
			else
				activeChar.setVar("bhfails", "1");
			show(new CustomMessage("scripts.events.bountyhunters.TaskCanceled", activeChar), activeChar);
			return true;
		}
		return false;
	}
}