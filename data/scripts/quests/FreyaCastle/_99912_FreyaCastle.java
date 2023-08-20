package quests.FreyaCastle;

import javolution.util.FastMap;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Diagod
 * 18.15.2011
 **/

public class _99912_FreyaCastle extends Quest implements ScriptFile
{
	public _99912_FreyaCastle()
	{
		super(false);
	}

	private class World
	{
		public int instanceId = 0;
		public boolean showIsInProgress = false; 
		public L2NpcInstance spawnedFreya = null;
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

	private static final int JINIA2 = 32781;
	private static final int FREYA = 18847;
	private static final int[] CROWD = { 18848, 18849, 18926, 22767};

	private static final L2Skill _showBlizzard = SkillTable.getInstance().getInfo(6276, 1);

	private ZoneListener _zoneListener = new ZoneListener();

	private L2Zone _zone;

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

	protected int enterInstance(L2Player player)
	{
		int templateId = 137;
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
			ref.FillDoors(i.getDoors());
		}
		World world = new World();
		world.instanceId = ref.getId();

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
		world.spawnedFreya = addSpawnToInstance(FREYA, new Location(114720,-117085,-11088,15956), 0, world.instanceId);
		world.spawnedFreya.setRunning();
		world.spawnedFreya.getAI().addTaskMove(new Location(114730, -114805, -11200, 0), true);
		// Запрещаем всем НПС двигатся пока чар не войдет в зал...
		for(L2NpcInstance npc : ref.getMonsters())
			if(npc.getNpcId() != FREYA)
				npc.p_block_move(true, null);
		return templateId;
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		World world = getWorld(npc.getReflectionId());
		if(npc.getNpcId() == FREYA)
		{
			if(event.equalsIgnoreCase("blizzard"))
			{
				Functions.npcSayCustomMessage(npc, "FreyaCastle1");
				npc.stopMove();
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				npc.setIsInvul(true);
				npc.setTarget(npc);
				if(_showBlizzard != null)
					npc.doCast(_showBlizzard, npc, true);
				startQuestTimer("movie", 12000, npc, st.getPlayer());
				return null;
			}
			else if(event.equalsIgnoreCase("movie") && st.getPlayer() != null)
			{
				for(L2NpcInstance npcs : st.getPlayer().getReflection().getMonsters())
					npcs.deleteMe();
				st.getPlayer().showQuestMovie(21);
				startQuestTimer("movie_end", 22000, npc, st.getPlayer());
			}
			else if(event.equalsIgnoreCase("movie_end") && st.getPlayer() != null)
			{
				QuestState hostQuest = st.getPlayer().getQuestState("_10285_MeetingSirra");
				if (hostQuest != null && hostQuest.getState() == STARTED && hostQuest.getInt("progress") == 2)
				{
					hostQuest.set("cond", "10");
					hostQuest.playSound("ItemSound.quest_middle");
					hostQuest.set("progress", "3");
				}
				st.getPlayer().leaveMovieMode();
				st.getPlayer().unsetVar("backCoords");
				try
				{
					if(st.getPlayer().getReflection().getId() > 0)
						st.getPlayer().getReflection().startCollapseTimer(10);
				}
				catch(Exception e)
				{}
			}
		}
		else if(npc.getNpcId() == JINIA2)
		{
			if(event.equalsIgnoreCase("toEnter"))
			{
				QuestState hostQuest = st.getPlayer().getQuestState("_10285_MeetingSirra");
				if(hostQuest != null && hostQuest.getState() == STARTED && hostQuest.getInt("progress") == 2)
				{
					hostQuest.set("cond", "9");
					hostQuest.playSound("ItemSound.quest_middle");
				}
				if(enterInstance(st.getPlayer()) <= 0)
					return "32781-10.htm";
			}
		}
		return null;
	}

	public class ZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			boolean isShat = false;
			if(object != null && object.isPlayer() && !object.inObserverMode())
			{
				L2Player player = (L2Player)object;
				World world = getWorld(player.getReflectionId());
				if(world != null)
				{
					if(!world.showIsInProgress)
					{
						startQuestTimer("blizzard", 100000, world.spawnedFreya, player);
						for(L2NpcInstance npc : player.getReflection().getMonsters())
						{
							if(Arrays.binarySearch(CROWD, npc.getNpcId()) >= 0)
							{
								if(npc.getNpcId() == 18848 && !isShat)
								{
									NpcSay cs = new NpcSay(npc, Say2C.NPC_ALL, 1801096, player.getName());
									npc.broadcastPacket(cs);
									isShat = true;
								}
								npc.p_block_move(false, null); // Разрешаем всем НПС двигатся...
							}
						}
						world.showIsInProgress = true;
					}
				}
			}
		}
		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{}
	}

	public void onLoad()
	{
		_zone = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702122, false);
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
	}

	public void onReload()
	{
		_zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
	}

	public void onShutdown()
	{}
}