package npc.model;

import java.util.HashMap;
import java.util.concurrent.Future;

import bosses.FourSepulchersManager;
import bosses.FourSepulchersSpawn;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2SepulcherRaidInstance extends L2RaidBossInstance
{
	public L2SepulcherRaidInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public int mysteriousBoxId = 0;
	protected Future<?> _onDeadEventTask = null;

	@Override
	public void doDie(L2Character killer)
	{
		super.doDie(killer);

		L2Player player = killer.getPlayer();
		if(player != null)
			giveCup(player);
		if(_onDeadEventTask != null)
			_onDeadEventTask.cancel(true);
		_onDeadEventTask = ThreadPoolManager.getInstance().schedule(new OnDeadEvent(this), 8500);
	}

	@Override
	public void deleteMe()
	{
		if(_onDeadEventTask != null)
		{
			_onDeadEventTask.cancel(true);
			_onDeadEventTask = null;
		}

		super.deleteMe();
	}

	private void giveCup(L2Player player)
	{
		String questId = FourSepulchersManager.QUEST_ID;
		int cupId = 0;
		int oldBrooch = 7262;

		switch(getNpcId())
		{
			case 25339:
				cupId = 7256;
				break;
			case 25342:
				cupId = 7257;
				break;
			case 25346:
				cupId = 7258;
				break;
			case 25349:
				cupId = 7259;
				break;
		}

		if(ConfigValue.FourGobletsSetItemInAllAggroList)
		{
			HashMap<L2Playable, AggroInfo> aggroList = getAggroMap();
			if(aggroList != null && !aggroList.isEmpty())
				for(L2Playable pl : aggroList.keySet())
					if(pl.isPlayer() && pl.getReflectionId() == getReflectionId() && (pl.isInRange(this, ConfigValue.AltPartyDistributionRange) || pl.isInRange(player, ConfigValue.AltPartyDistributionRange)) && Math.abs(pl.getZ() - getZ()) < 400)
					{
						QuestState qs = pl.getPlayer().getQuestState(questId);
						if(qs != null && (qs.isStarted() || qs.isCompleted()) && pl.getInventory().getItemByItemId(oldBrooch) == null)
							Functions.addItem(pl, cupId, 1);
					}
		}
		else
		{
			if(player.getParty() != null)
				for(L2Player mem : player.getParty().getPartyMembers())
				{
					QuestState qs = mem.getQuestState(questId);
					if(qs != null && (qs.isStarted() || qs.isCompleted()) && mem.getInventory().getItemByItemId(oldBrooch) == null)
						Functions.addItem(mem, cupId, 1);
				}
			else
			{
				QuestState qs = player.getQuestState(questId);
				if(qs != null && (qs.isStarted() || qs.isCompleted()) && player.getInventory().getItemByItemId(oldBrooch) == null)
					Functions.addItem(player, cupId, 1);
			}
		}
	}

	private class OnDeadEvent extends l2open.common.RunnableImpl
	{
		L2SepulcherRaidInstance _activeChar;

		public OnDeadEvent(L2SepulcherRaidInstance activeChar)
		{
			_activeChar = activeChar;
		}

		public void runImpl()
		{
			FourSepulchersSpawn.spawnEmperorsGraveNpc(_activeChar.mysteriousBoxId);
		}
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}