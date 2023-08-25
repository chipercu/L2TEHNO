package services;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2TerritoryManagerInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.util.Files;

public class NoblessSell extends Functions implements ScriptFile
{
	public void get()
	{
		L2Player player = (L2Player) getSelf();

		if(!checkCondition(player))
			return;
		else if(DifferentMethods.getPay(player, ConfigValue.NoblessSellItem, ConfigValue.NoblessSellPrice, true))
		{
			makeSubQuests();
			becomeNoble();
		}
	}

	public void getTW()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = this.getNpc();
		if(npc == null || !(npc instanceof L2TerritoryManagerInstance))
			return;

		int terr = npc.getNpcId() - 36489;
		if(terr > 9 || terr < 1)
			return;

		int territoryBadgeId = 13756 + terr;

		if(player.isNoble())
		{
			player.sendMessage(new CustomMessage("services.NoblessSell.isNooble", player));
			return;
		}
		else if(player.getLevel() < ConfigValue.NoblessSellSubLevel)
		{
			player.sendMessage(new CustomMessage("services.NoblessSell.SubLevel.low", player));
			return;
		}
		else if(DifferentMethods.getPay(player, territoryBadgeId, 100, true))
		{
			makeSubQuests();
			becomeNoble();
		}
	}

	public void makeSubQuests()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		Quest q = QuestManager.getQuest("_234_FatesWhisper");
		QuestState qs = player.getQuestState(q.getName());
		if(qs != null)
			qs.exitCurrentQuest(true);
		q.newQuestState(player, Quest.COMPLETED);

		if(player.getRace() == Race.kamael)
		{
			q = QuestManager.getQuest("_236_SeedsOfChaos");
			qs = player.getQuestState(q.getName());
			if(qs != null)
				qs.exitCurrentQuest(true);
			q.newQuestState(player, Quest.COMPLETED);
		}
		else
		{
			q = QuestManager.getQuest("_235_MimirsElixir");
			qs = player.getQuestState(q.getName());
			if(qs != null)
				qs.exitCurrentQuest(true);
			q.newQuestState(player, Quest.COMPLETED);
		}
	}

	public void becomeNoble()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || player.isNoble())
			return;

		Olympiad.addNoble(player);
		player.setNoble(true);
		player.updatePledgeClass();
		player.updateNobleSkills();
		player.sendPacket(new SkillList(player));
		player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.VICTORY));
		player.broadcastUserInfo(true);
	}

	private boolean checkCondition(L2Player player)
	{
		if(player == null)
			return false;
		else if(player.isNoble())
		{
			player.sendMessage(new CustomMessage("services.NoblessSell.isNooble", player));
			return false;
		}
		else if(player.getSubLevel() < ConfigValue.NoblessSellSubLevel)
		{
			player.sendMessage(new CustomMessage("services.NoblessSell.SubLevel.low", player));
			return false;
		}

		return true;
	}

	public void dialogTW()
	{
		L2Player player = (L2Player) getSelf();
		if(ConfigValue.NoblessTWEnabled)
			show(Files.read("data/html/TerritoryManager/TerritoryManager-2.htm", player), player);
		else
			show(new CustomMessage("common.Disabled", player), player);
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Nobless sell");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}