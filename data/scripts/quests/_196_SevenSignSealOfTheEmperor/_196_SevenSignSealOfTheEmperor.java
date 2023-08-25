package quests._196_SevenSignSealOfTheEmperor;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;
import quests._195_SevenSignsSecretRitualPriests._195_SevenSignsSecretRitualPriests;

import java.util.HashMap;

public class _196_SevenSignSealOfTheEmperor extends Quest implements ScriptFile
{
    /** Npc **/
	private static final int IasonHeine = 30969;
	private static final int MerchantofMammon = 32584;
	private static final int PromiseofMammon = 32585;
	private static final int Shunaiman = 32586;
	private static final int Leon = 32587;
	private static final int CourtMagician = 32598;
	private static final int DisciplesGatekeeper = 32657;
	private static final int Wood = 32593;
	
	/** Mob's **/
	private static final int SealDevice = 27384;
	private static final int SealDeviceDestroy = 18833;
	private static final int[] MOBS = { 27371, 27372, 27373, 27374, 27375, 27377, 27378, 27379 };
	
    /** Item's **/
	private static final int SacredSwordofEinhasad = 15310; //This is Weapon... ;)
	private static final int ElmoredenHolyWater = 13808; //This is Key
	private static final int CourtMagiciansMagicStaff = 13809; //This is Key
	private static final int SealofBinding = 13846;
	
	/** Options **/
	private static final int roomSpawnOffset = 380; // разброс спауна от центра комнаты
	private static final int roomSpawnOffset2 = 500; // разброс спауна от центра комнаты (для комнат по больше)

	/** SystemMessages **/
	public static final SystemMessage By_using_the_skill_of_Einhasads_holy_sword = new SystemMessage(3031);
	public static final SystemMessage In_order_to_help_Anakim = new SystemMessage(3032);
	public static final SystemMessage By_using_the_holy_water_of_Einhasad = new SystemMessage(3039);
	public static final SystemMessage By_using_the_Court_Magician_Magic_Staff = new SystemMessage(3040);
	public static final SystemMessage The_sealing_device_glitters_and_moves = new SystemMessage(3060);

	private static FastMap<Integer, Integer> _instances = new FastMap<Integer, Integer>();
	private static HashMap<Integer, World> worlds = new HashMap<Integer, World>();
	
	public class World
	{
		public int instanceId;
		public int status;
		public GArray<Room> rooms;
	}
	
	public class Room
	{
		public Room()
		{
			npclist = new HashMap<L2NpcInstance, Boolean>();
		}

		public HashMap<L2NpcInstance, Boolean> npclist;
		public Location center;
	}

	public _196_SevenSignSealOfTheEmperor()
	{
		super(false);
		
		addStartNpc(IasonHeine);
		addTalkId(MerchantofMammon, PromiseofMammon, Shunaiman, Leon, CourtMagician, DisciplesGatekeeper, Wood);
		addKillId(SealDevice);
		addKillId(MOBS);
		addQuestItem(SealofBinding, CourtMagiciansMagicStaff, ElmoredenHolyWater, SacredSwordofEinhasad);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		L2Player player = st.getPlayer();
		
		if(event.equalsIgnoreCase("30969-05.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30969-06.htm"))
		{
			if(!L2ObjectsStorage.getAllByNpcId(MerchantofMammon, false).isEmpty())
				return "30969-06a.htm"; //Если мамон есть то открываем это...
			L2NpcInstance mammon = st.addSpawn(MerchantofMammon,109743,219975,-3512,180000);
		    Functions.npcSay(mammon, "Who dares summon the Merchant of Mammon?!");
		}
		else if(event.equalsIgnoreCase("32584-05.htm") && npc.getNpcId() == 32584)
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
			npc.decayMe();
		}
		else if(event.equalsIgnoreCase("32586-06.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_MIDDLE);
            player.broadcastPacket(new ExShowScreenMessage("", 4000, ScreenMessageAlign.TOP_CENTER, true, 0, 3031, false));
            player.sendPacket(By_using_the_skill_of_Einhasads_holy_sword);
            player.sendPacket(By_using_the_holy_water_of_Einhasad);
			st.giveItems(ElmoredenHolyWater, 1);
			st.giveItems(SacredSwordofEinhasad, 1);
		}
		else if(event.equalsIgnoreCase("32586-11.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_MIDDLE);
			st.takeItems(SacredSwordofEinhasad, -1);
			st.takeItems(ElmoredenHolyWater, -1);
			st.takeItems(CourtMagiciansMagicStaff, -1);
			st.takeItems(SealofBinding, -1);
		}
		else if(event.equalsIgnoreCase("32598-02.htm"))
			st.giveItems(CourtMagiciansMagicStaff, 1);
		else if(event.equalsIgnoreCase("30969-10.htm"))
		{
			st.set("cond", "6");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32593-02.htm"))
		{
			if(!player.isSubClassActive())
			{
				st.addExpAndSp(52518015, 5817677);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
			else
				htmltext = "<html><body>Only characters who are <font color=\"LEVEL\">main class</font>.</body></html>";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();
		L2Player player = st.getPlayer();
		final World world = worlds.get(player.getReflection().getId());
		Reflection r = player.getReflection();
		
		if(npcId == IasonHeine)
		{
			if(id == CREATED)
			{
				if(player.getLevel() < 79 && player.isSubClassActive())
				{
					st.exitCurrentQuest(true);
					return "30969-00.htm";
				}
				QuestState qs = player.getQuestState(_195_SevenSignsSecretRitualPriests.class);
				if(qs == null || !qs.isCompleted())
				{
					st.exitCurrentQuest(true);
					return "noquest";
				}
				return "30969-01.htm";
			}
			if(cond == 1)
				return "30969-05.htm";
			else if(cond == 2)
			{
			    st.set("cond", "3");
			    st.playSound(SOUND_MIDDLE);
				return "30969-07.htm";
			}
			else if(cond == 3)
				return "30969-07a.htm";
			else if(cond == 5)
				return "30969-08.htm";
			else if(cond == 6)
				return "30969-11.htm";
		}
		if(npcId == MerchantofMammon)
			if(cond == 1)
				return "32584-01.htm";
		if(npcId == PromiseofMammon)
		{
			if(cond >= 3 && cond <= 5)
			{
				enterInstance(player);
    			return null;
			}
		}
		if(npcId == Leon)
		{   //Вроде надо переделать...
		    if(cond >= 3)
			{
		    	if (r.getReturnLoc() != null)
		    		player.teleToLocation(r.getReturnLoc(), 0);
		    	else
		    		player.setReflection(0);
				st.takeItems(SacredSwordofEinhasad, -1);
		    	player.unsetVar("backCoords");
				return "32587-00.htm";
			}
		}
		if(npcId == Shunaiman)
		{
			if(cond == 3)
				return "32586-01.htm";
			else if(cond == 4)
			{
				if(st.getQuestItemsCount(SealofBinding) >= 4)
					return "32586-07.htm";
				else if(st.getQuestItemsCount(SacredSwordofEinhasad) >= 1)
					return "32586-06a.htm";
				else
				{
				    player.broadcastPacket(new ExShowScreenMessage("", 4000, ScreenMessageAlign.TOP_CENTER, true, 0, 3031, false));
				    player.sendPacket(By_using_the_skill_of_Einhasads_holy_sword);
				    st.giveItems(SacredSwordofEinhasad, 1);
					return "32586-06b.htm";
				}
			}
			else if(cond == 5)
				return "32586-11b.htm";
		}
		if(npcId == CourtMagician && cond == 4)
		{
			if(st.getQuestItemsCount(CourtMagiciansMagicStaff) >= 1)
			{
			    player.sendPacket(By_using_the_Court_Magician_Magic_Staff);
				return "32598-02a.htm";
			}
			else
			{
			    player.sendPacket(By_using_the_Court_Magician_Magic_Staff);
				return "32598-01.htm";
	    	}
		}
		if(npcId == DisciplesGatekeeper)
		{
		    if(cond == 4)
			{
    			ReflectionTable.getInstance().get(world.instanceId).openDoor(17240111);
    			player.sendPacket(new ExStartScenePlayer(ExStartScenePlayer.SCENE_SSQ_SEALING_EMPEROR_1ST));
    			player.sendPacket(In_order_to_help_Anakim);
				//Таймер для спауна Анаким и Лилит (Нужен для того чтобы не показывало их во время ролика...)
				ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl(){
        			@Override
        			public void runImpl()
        			{
        				L2NpcInstance lilith = addSpawnToInstance(32715, new Location(-83176,217048,-7488, 0), 0, world.instanceId);
						// Высталяем миньёнов лилит с разбросом в 200
        				addSpawnToInstance(32716, lilith.getLoc(), Rnd.get(100, 300), world.instanceId); 
        				addSpawnToInstance(32717, lilith.getLoc(), Rnd.get(1, 300), world.instanceId);
						L2NpcInstance anakim = addSpawnToInstance(32718, new Location(-83176,216472,-7488, 0), 0, world.instanceId);
						// Высталяем миньёнов анаким с разбросом в 200
						addSpawnToInstance(32719, anakim.getLoc(), Rnd.get(1, 300), world.instanceId);
						addSpawnToInstance(32720, anakim.getLoc(), Rnd.get(1, 300), world.instanceId);
						addSpawnToInstance(32721, anakim.getLoc(), Rnd.get(1, 300), world.instanceId);
        			}
        		}, 17000);
    			return null;
			}
		}
		if(npcId == Wood)
		    if(cond == 6)
				return "32593-01.htm";
		return "noquest";
	}
	
	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		final L2Player player = st.getPlayer();
		World world = worlds.get(npc.getReflection().getId());
		if(world == null)
			return null;

		if(npc.getNpcId() == SealDevice)
		{
			st.giveItems(SealofBinding, 1);
			npc.decayMe();//Удоляется сразу чтобы небыло видно подмены...
			L2NpcInstance seal = addSpawnToInstance(SealDeviceDestroy, npc.getLoc(), 0, player.getReflectionId());//Делаем подмену печати... И Всё же я думаю что это не правильно... (нужен лог пакетов для разбора полётов ;))
			seal.setIsInvul(true); //Делаем бессмертным ибо L2Monster
		    //Если у персонажа 4 или больше предмета то запускаем ролик и таймер для телепортации
			if(st.getQuestItemsCount(SealofBinding) < 4)
			    st.playSound(SOUND_ITEMGET);
			else
			{
			    st.playSound(SOUND_MIDDLE);
				player.sendPacket(The_sealing_device_glitters_and_moves);
				player.sendPacket(new ExStartScenePlayer(ExStartScenePlayer.SCENE_SSQ_SEALING_EMPEROR_2ND));
				// Телепортирует персонажа через 28 секунд с начала ролика... (ролик идёт 26 секунд)
				ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl(){
        			@Override
        			public void runImpl()
        			{
        				player.teleToLocation(new Location(-89559, 216030, -7488));
        			}
        		}, 28000);
			}
		}
			
		if(checkKillProgress(npc, world.rooms.get(world.status)))
		{
			world.status++;
			runTheSanctum(world);
		}

		return null;
	}
	
	private void enterInstance(L2Player player)
	{
		int instancedZoneId = 112;
		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> ils = ilm.getById(instancedZoneId);
		
		if(ils == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone il = ils.get(0);

		assert il != null;

		if(player.isInParty())
		{
			player.sendPacket(Msg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
			return;
		}

		if(player.isCursedWeaponEquipped())
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
			return;
		}

		Integer old = _instances.get(player.getObjectId());
		if(old != null)
		{
			Reflection old_r = ReflectionTable.getInstance().get(old);
			if(old_r != null)
			{
				player.setReflection(old_r);
				player.teleToLocation(il.getTeleportCoords());
				player.setVar("backCoords", old_r.getReturnLoc().toXYZString());
				return;
			}
		}

		Reflection r = new Reflection(il.getName());
		r.setInstancedZoneId(instancedZoneId);
		for(InstancedZone i : ils.values())
		{
			if(r.getReturnLoc() == null)
				r.setReturnLoc(i.getReturnCoords());
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
			r.FillDoors(i.getDoors());
		}
		
		// init
		World world = new World();
		world.rooms = new GArray<Room>();
		world.instanceId = r.getId();
		world.status = 0;
		worlds.put(r.getId(), world);
		runTheSanctum(world);

		int timelimit = il.getTimelimit();

		player.setReflection(r);
		player.teleToLocation(il.getTeleportCoords());
		player.setVar("backCoords", r.getReturnLoc().toXYZString());

		r.setNotCollapseWithoutPlayers(true);
		r.startCollapseTimer(timelimit * 60 * 1000L);

		_instances.put(player.getObjectId(), r.getId());
	}

	private void runTheSanctum(World world)
	{
		Room room = new Room();
		Reflection r = ReflectionTable.getInstance().get(world.instanceId);
		switch(world.status)
		{
			case 0: // Комната 1
			    /******************************
				* - Lilim Butcher = 1
				* - Lilim Magus = 1
				* - Lilim Slave Knight = 1
				* - Shilen's Evil Thoughts = 1
				******************************/
				room.center = new Location(-89240, 217928, -7517, 0);
				room.npclist.put(addSpawnToInstance(27371, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27372, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27373, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27374, room.center, roomSpawnOffset, world.instanceId), false);
				world.rooms.add(room);
				break;
			case 1: // Комната 2
			    /******************************
				* - Lilim Butcher = 2
				* - Lilim Magus = 1
				* - Lilim Slave Knight = 2
				* - Shilen's Evil Thoughts = 1
				******************************/
				room.center = new Location(-88600, 220264, -7517, 0);
				room.npclist.put(addSpawnToInstance(27371, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27371, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27372, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27373, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27373, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27374, room.center, roomSpawnOffset, world.instanceId), false);
				r.openDoor(17240102);
				world.rooms.add(room);
				break;
			case 2: // Комната 3
			    /******************************
				* - Lilim Butcher = 2
				* - Lilim Magus = 2
				* - Lilim Slave Knight = 2
				* - Shilen's Evil Thoughts = 2
				******************************/
				room.center = new Location(-87032, 220632, -7517, 0);
				room.npclist.put(addSpawnToInstance(27371, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27371, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27372, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27372, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27373, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27373, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27374, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27374, room.center, roomSpawnOffset, world.instanceId), false);
				r.openDoor(17240104);
				world.rooms.add(room);
				break;
			case 3: // Комната 4
			    /******************************
				* - Lilim Assassin = 1
				* - Lilim Guard Knight = 1
				* - Lilim Butcher = 1
				* - Lilim Magus = 1
				* - Lilim Great Magus = 1
				* - Lilim Slave Knight = 2
				* - Shilen's Evil Thoughts = 2
				* - Shilen's Evil Thoughts = 1(мелкий)
				******************************/
				room.center = new Location(-85352, 219224, -7517, 0);
				room.npclist.put(addSpawnToInstance(27371, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27372, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27373, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27373, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27374, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27374, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27375, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27377, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27378, room.center, roomSpawnOffset, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27379, room.center, roomSpawnOffset, world.instanceId), false);
				r.openDoor(17240106);
				world.rooms.add(room);
				break;
			case 4: // Комната 5
			    /******************************
				* - Lilim Assassin = 2
				* - Lilim Guard Knight = 2
				* - Lilim Butcher = 1
				* - Lilim Magus = 1
				* - Lilim Great Magus = 2
				* - Lilim Slave Knight = 1
				* - Shilen's Evil Thoughts = 1
				* - Shilen's Evil Thoughts = 2(мелкий)
				******************************/
				room.center = new Location(-87448, 217608, -7517, 0);
				room.npclist.put(addSpawnToInstance(27371, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27372, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27373, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27374, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27375, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27375, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27377, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27377, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27378, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27378, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27379, room.center, roomSpawnOffset2, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27379, room.center, roomSpawnOffset2, world.instanceId), false);
				r.openDoor(17240108);
				world.rooms.add(room);
				break;
			case 5: // Комната 6 (Комната с Лилит и Анаким)
			    /******************************
				* - Seal Device = 4
				******************************/
				//Seals
				room.npclist.put(addSpawnToInstance(27384, new Location(-83177, 216137, -7517, 0), 0, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27384, new Location(-83177, 217353, -7517, 0), 0, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27384, new Location(-82588, 216754, -7517, 0), 0, world.instanceId), false);
				room.npclist.put(addSpawnToInstance(27384, new Location(-83804, 216754, -7517, 0), 0, world.instanceId), false);
				world.rooms.add(room);
				r.openDoor(17240110);
				break;
		}
	}
	
	private boolean checkKillProgress(L2NpcInstance npc, Room room)
	{
		if(room.npclist.containsKey(npc))
			room.npclist.put(npc, true);
		for(boolean value : room.npclist.values())
			if(!value)
				return false;
		return true;
	}
	
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

}