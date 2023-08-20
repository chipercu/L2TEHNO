package quests.JiniasHideout;

import javolution.util.FastMap;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Diagod
 * 18.15.2011
 **/

public class _99910_JiniasHideout extends Quest implements ScriptFile
{
	public _99910_JiniasHideout()
	{
		super(false);
	}
	private static L2NpcInstance spawn(int npcId, Location loc, int refl)
	{
		try
		{
			L2NpcInstance npc = NpcTable.getTemplate(npcId).getNewInstance();
			if (npc != null)
			{
				npc.setReflection(refl);
				npc.setSpawnedLoc(loc);
				npc.onSpawn();
				npc.spawnMe(npc.getSpawnedLoc());
				return npc;
			}
		}
		catch (Exception e)
		{
		}
		return null;
	}
	private class World
	{
		public int questId; 
		public World()
		{
		}
	}
	public static FastMap<Integer, World> worlds = new FastMap<Integer, World>();
	public static void addWorld(int id, World world)
	{
		worlds.put(id, world);
	}
	public static World getWorld(int id)
	{
		World world = worlds.get(id);
		if(world != null)
			return world;
		return null;
	}

	private static final ReentrantLock lock = new ReentrantLock();
	private static final int RAFFORTY = 32020;
	private static final int JINIA = 32760;
	private static final int SIRRA = 32762;

	private boolean checkConditions(L2Player player)
	{
		SystemMessage sm;
		if(player.getLevel() < 82 || player.getLevel() > 85)
		{
			sm = new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player);
			player.sendPacket(sm);
			return false;
		}
		else if(player.isCursedWeaponEquipped() || player.isInFlyingTransform() || player.isDead())
		{
			sm = new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player);
			player.sendPacket(sm);
			return false;
		}
		return true; 
	}

	protected int enterInstance(L2Player player, int questId)
	{
		int templateId = 0;
		switch(questId)
		{
			case 10284:
				templateId = 140;
				break;
			case 10285:
				templateId = 141;
				break;
			case 10286:
				templateId = 145;
				break;
			case 10287:
				templateId = 146;
				break;
		}
		if(!checkConditions(player))
			return 0;
		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(templateId);
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return 0;
		}
		InstancedZone iz = izs.get(0);
		if(iz == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return 0;
		}

		String name = iz.getName();
		int time = iz.getTimelimit();
		Reflection ref = new Reflection(name);
		ref.setInstancedZoneId(templateId);
		for(InstancedZone i : izs.values())
		{
			if (ref.getReturnLoc() == null)
				ref.setReturnLoc(i.getReturnCoords());
			if (ref.getTeleportLoc() == null)
				ref.setTeleportLoc(i.getTeleportCoords());
			ref.FillSpawns(i.getSpawnsInfo());
		}
		World world = new World();
		world.questId = questId;
		lock.lock();
		try
		{
			addWorld(ref.getId(), world);
		}
		finally
		{
			lock.unlock();
		}
		player.setReflection(ref);
		player.teleToLocation(iz.getTeleportCoords());
		player.setVar("backCoords", player.getLoc().toXYZString());
		ref.startCollapseTimer(time * 60000);

		/*String msg = null;
		switch(templateId)
		{
			case 141:
				msg = "There's nothing you can't say. I can't listen to you anymore!";
				break;
			case 145:
				msg = "You advanced bravely but got such a tiny result. Hohoho.";
				break;
		}
		if(msg != null)
			jinia.broadcastPacket(new NpcSay(jinia, Say2C.NPC_ALL, msg));*/

		return templateId;
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = null;
		if(event.startsWith("enterInstance_") && npc.getNpcId() == RAFFORTY)
		{
			int questId = -1;
			QuestState hostQuest = null;
			try
			{
				questId = Integer.parseInt(event.substring(14));
				switch(questId)
				{
					case 10284:
						hostQuest = st.getPlayer().getQuestState("_10284_AcquisitionOfDivineSword");
						htmltext = "10284_failed.htm";
						break;
					case 10285:
						hostQuest = st.getPlayer().getQuestState("_10285_MeetingSirra");
						htmltext = "10285_failed.htm";
						break;
					case 10286:
						hostQuest = st.getPlayer().getQuestState("_10286_ReunionWithSirra");
						htmltext = "10286_failed.htm";
						break;
					case 10287:
						hostQuest = st.getPlayer().getQuestState("_10287_StoryOfThoseLeft");
						htmltext = "10287_failed.htm";
						break;
				}	
				if(hostQuest != null && hostQuest.getInt("cond") == 1)
				{
					hostQuest.playSound("ItemSound.quest_middle");
					hostQuest.set("cond", "2");
				}
				if(enterInstance(st.getPlayer(), questId) > 0)
					htmltext = null;
			}
			catch(Exception e)
			{
			
			}
		}
		else if(event.equalsIgnoreCase("leaveInstance") && npc.getNpcId() == JINIA)
		{
			QuestState hostQuest = null;

			World world = getWorld(st.getPlayer().getReflectionId());
			if(world == null)
				return null;
			switch(world.questId)
			{
				case 10285:
					hostQuest = st.getPlayer().getQuestState("_10285_MeetingSirra");
					break;
				case 10286:
					hostQuest = st.getPlayer().getQuestState("_10286_ReunionWithSirra");
					break;
				case 10287:
					hostQuest = st.getPlayer().getQuestState("_10287_StoryOfThoseLeft");
					break;
			}
			if(hostQuest != null && hostQuest.getState() == STARTED && hostQuest.getInt("progress") == 2)
			{
				switch(world.questId)
				{
					case 10285:
						break;
					case 10286:
						hostQuest.playSound("ItemSound.quest_middle");
						hostQuest.set("cond", "5");
						break;
					case 10287:
						hostQuest.playSound("ItemSound.quest_middle");
						hostQuest.set("cond", "5");
				}
				htmltext = "";
			}
			st.getPlayer().getReflection().startCollapseTimer(1000);
		}
		return htmltext;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}