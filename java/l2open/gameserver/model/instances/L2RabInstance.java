package l2open.gameserver.model.instances;

import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2DropData;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.RateService;
import l2open.util.Util;

import java.util.concurrent.ScheduledFuture;

public class L2RabInstance extends L2MonsterInstance
{
	public ScheduledFuture<?> FallowTask;
	private static L2Player spasitel;
	private static final L2DropData[] DROPS = { new L2DropData(1876, 1, 2, 1000000, 1), new L2DropData(1885, 3, 4, 1000000, 1), new L2DropData(9628, 5, 10, 1000000, 1) };

	public L2RabInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public void doDie(L2Character killer)
	{
		HellboundManager.getInstance().addPoints(-10);
		Location loc = getSpawn() == null ? null : getSpawn().getLoc();
		ThreadPoolManager.getInstance().schedule(new Spawn(loc), 180000);
		deleteMe();
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		L2Player player = attacker.getPlayer();
		if(player == null)
			return false;
		if(player.isPlayable())
			return false;
		return true;
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		int hLevel = HellboundManager.getInstance().getLevel();
		String filename = "";
		if (hLevel < 5)
			filename = "/data/html/hellbound/rab/" + getNpcId() + "-no.htm";
		else if (hLevel >= 5)
			filename = "/data/html/hellbound/rab/" + getNpcId() + ".htm";
		super.showChatWindow(player, filename);
	}

	public void onBypassFeedback(L2Player p, String command)
	{
		if (command.equalsIgnoreCase("fallowme"))
		{
			setSpasitel(p);
			startFallowTask();
		}
		super.onBypassFeedback(p, command);
	}

	public void startFallowTask()
	{
		if (FallowTask != null)
			stopFallowTask();
		if (getSpasitel() != null)
			FallowTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Fallow(), 1000, 1000);
	}

	public void stopFallowTask()
	{
		if (FallowTask != null)
			FallowTask.cancel(true);
		FallowTask = null;
	}
	
	public ScheduledFuture getFollowTask()
	{
		return FallowTask;
	}

	public void setSpasitel(L2Player p)
	{
		spasitel = p;
	}

	public L2Player getSpasitel()
	{
		return spasitel;
	}

	public void setIsSpasen()
	{
		stopFallowTask();
		stopMove();

		if(getSpasitel() != null)
		{
			double chancemod = Experience.penaltyModifier(calculateLevelDiffForDrop(getSpasitel().getLevel(), false), 9);

			L2DropData d = DROPS[l2open.util.Rnd.get(0, DROPS.length)];
			if(d != null)
				dropItem(getSpasitel(), d.getItemId(), Util.rollDrop(d.getMinDrop(), d.getMaxDrop(), d.getChance() * chancemod * RateService.getRateDropItems(getSpasitel()) * getSpasitel().getRateItems(), true, getSpasitel()));
		}
		Functions.npcSay(this, "Thanks for safe me. This is small gift for you.");
		HellboundManager.getInstance().addPoints(10);
		changeRescued(1);
		checklvlup();
		Location loc = getSpawn() == null ? null : getSpawn().getLoc();
		ThreadPoolManager.getInstance().schedule(new Spawn(loc), 180000);
		deleteMe();
	}

	public class Spawn extends RunnableImpl
	{
		Location _loc;

		public Spawn(Location loc)
		{
			_loc = loc;
		}

		@Override
		public void runImpl() throws Exception
		{
			if(_loc != null)
				Functions.spawn(_loc, 32299, 180);
		}
	}

	public static void changeRescued(int mod)
	{
		int curr = getRescued();
		int n = Math.max(0, mod + curr);
		if(curr != n)
			ServerVariables.set("HellboundRabInstance", n);
	}

	private static int getRescued()
	{
		return ServerVariables.getInt("HellboundRabInstance", 0);
	}
	
	
	public void checklvlup()
	{	
		int curr = getRescued();
		if(curr >= ConfigValue.HellboundRescued)
			if(HellboundManager.getInstance().getLevel() == 5) // На всякий случай.
				HellboundManager.getInstance().changeLevel(6);
	}

	private void checkInRadius(int id)
	{
		L2NpcInstance Pillar = L2ObjectsStorage.getByNpcId(id);
		if (getRealDistance3D(Pillar) <= 300)
			setIsSpasen();
	}

	private class Fallow extends l2open.common.RunnableImpl
	{
		private Fallow()
		{
		}

		public void runImpl()
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getSpasitel(), 300);
			checkInRadius(32307);
		}
	}

	// Не отображаем значки клана на рабах ХБ.
	@Override
	public boolean isCrestEnable()
	{
		return false;
	}
}