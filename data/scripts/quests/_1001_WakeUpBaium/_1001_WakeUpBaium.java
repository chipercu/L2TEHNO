package quests._1001_WakeUpBaium;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Location;
import bosses.BaiumManager;

public class _1001_WakeUpBaium extends Quest implements ScriptFile
{
	private static final int Baium = 29020;
	private static final int BaiumNpc = 29025;
	private static final int AngelicVortex = 31862;
	private static final int BloodedFabric = 4295;
	//private static final Location BAIUM_SPAWN_POSITION = new Location(116127, 17368, 10107, 35431);
	private static final Location TELEPORT_POSITION = new Location(113100, 14500, 10077);

	private static long first_tp_time = 0;

	public void onLoad()
	{
		ScriptFile._log.info("Loaded Quest: 1001: Wake Up Baium");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _1001_WakeUpBaium()
	{
		super("Wake Up Baium", true);

		addStartNpc(BaiumNpc);
		addStartNpc(AngelicVortex);
	}

	@Override
	public synchronized String onTalk(final L2NpcInstance npc, final QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == BaiumNpc)
		{
			if(st.getInt("ok") != 1)
			{
				st.exitCurrentQuest(true);
				return "Conditions are not right to wake up Baium!";
			}
			if(first_tp_time > System.currentTimeMillis())
				return "Вы не можете сейчас разбудить Баюма, попробуйте через "+((first_tp_time - System.currentTimeMillis())/1000)+" секунд.";
			if(npc.isBusy())
				return "Baium is busy!";
			npc.setBusy(true);
			npc.setBusyMessage("Attending another player's request");
			Functions.npcSay(npc, "You call my name! Now you gonna die!");

			ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
			{
				public void runImpl()
				{
					npc.deleteMe();
					BaiumManager.spawnBaium(st.getPlayer());
				}
			}, ConfigValue.WakeUpBaiumTimer*1000);

			first_tp_time = 0;
			return "You call my name! Now you gonna die!";
		}
		else if(npcId == AngelicVortex)
		{
			if(st.getQuestItemsCount(BloodedFabric) > 0)
			{
				L2NpcInstance baiumBoss = L2ObjectsStorage.getByNpcId(Baium);
				if(baiumBoss != null)
					return "<html><head><body>Angelic Vortex:<br>Baium is already woken up! You can't enter!</body></html>";
				L2NpcInstance isbaiumNpc = L2ObjectsStorage.getByNpcId(BaiumNpc);
				if(isbaiumNpc == null)
					return "<html><head><body>Angelic Vortex:<br>Baium now here is not present!</body></html>";
				st.takeItems(BloodedFabric, 1);
				st.getPlayer().teleToLocation(TELEPORT_POSITION);
				st.set("ok", "1");
				if(first_tp_time == 0)
					first_tp_time = System.currentTimeMillis()+ConfigValue.WakeUpBaiumTime*1000;
				return "";
			}
			return "<html><head><body>Angelic Vortex:<br>You do not have enough items!</body></html>";
		}
		return null;
	}
}