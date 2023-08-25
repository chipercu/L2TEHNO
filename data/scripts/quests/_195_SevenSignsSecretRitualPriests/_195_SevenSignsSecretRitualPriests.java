package quests._195_SevenSignsSecretRitualPriests;

import ai.GuardOfTheDawn;
import quests._194_SevenSignContractOfMammon._194_SevenSignContractOfMammon;
import javolution.util.FastMap;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Location;

/** @author DarkShadow74 **/
public class _195_SevenSignsSecretRitualPriests extends Quest implements ScriptFile
{
	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;
	//Npc
	private static final int ClaudiaAthebaldt = 31001;
	private static final int CaptainoftheDawnJohn = 32576;
	private static final int HighPriestRaymond = 30289;
	private static final int LightofDawn = 32575;
	private static final int DarknessofDawn = 32579;
	private static final int IasonHeine = 30969;
	private static final int PasswordEntryDevice = 32577;
	private static final int IdentityConfirmDevice = 32578;
	private static final int Bookcase = 32580;

	private static final int[] DOORS = { 17240001, 17240002 };
	private static final int[] DOOR = { 17240003, 17240004 };
	private static final int[] libdoors = { 17240005, 17240006 };

	//Items
	private static final int GuardsoftheDawnIdentityCard = 13822;
	private static final int EmperorShunaimanContract = 13823;

	//Transform Skill/Id
	private static final int TransformedConditionGuardsOfTheDawn = 6204;
	private static final int GuardsOfTheDawn = 113;

	private static L2Zone _zone = ZoneManager.getInstance().getZoneById(ZoneType.dummy, 705011, false);

	private static FastMap<Integer, Integer> _instances = new FastMap<Integer, Integer>();

	public _195_SevenSignsSecretRitualPriests()
	{
		super(false);

		addStartNpc(ClaudiaAthebaldt);
		addTalkId(CaptainoftheDawnJohn, HighPriestRaymond, LightofDawn, IdentityConfirmDevice, PasswordEntryDevice, Bookcase, IasonHeine);
		addQuestItem(GuardsoftheDawnIdentityCard, EmperorShunaimanContract);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		L2Player player = st.getPlayer();
		Reflection r = player.getReflection();

		if(event.equals("31001-05.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("32576-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
			st.giveItems(GuardsoftheDawnIdentityCard, 1);
		}
		else if(event.equals("30289-go.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_MIDDLE);
			if(player.getTransformation() != 0)
			{
				player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			SkillTable.getInstance().getInfo(TransformedConditionGuardsOfTheDawn, 1).getEffects(player, player, false, false);
		}
		else if(event.equals("30289-remove.htm"))
			player.setTransformation(0);
		else if(event.equals("30289-retrans.htm"))
		{
			if(player.getTransformation() != 0)
			{
				player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			SkillTable.getInstance().getInfo(TransformedConditionGuardsOfTheDawn, 1).getEffects(player, player, false, false);
		}
		else if(event.equals("30289-07.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_MIDDLE);
			st.takeItems(GuardsoftheDawnIdentityCard, 1);
			player.setTransformation(0);
		}
		else if(event.equals("32580-03.htm"))
			returnToAden(player);
		else if(event.equalsIgnoreCase("exit"))
		{
			htmltext = "32579ex.htm";
			returnToAden(player);
		}
		else if(event.equalsIgnoreCase("code"))
		{
			for(int doorId : libdoors)
				ReflectionTable.getInstance().get(r.getId()).openDoor(doorId);
			htmltext = "32577open.htm";
		}
		else if(event.equals("30969-03.htm"))
		{
			if(!player.isSubClassActive())
			{
				st.takeItems(EmperorShunaimanContract, 1);
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
		Reflection r = player.getReflection();

		if(npcId == ClaudiaAthebaldt)
		{
			if(id == CREATED)
			{
				if(player.getLevel() < 79 && player.isSubClassActive())
				{
					st.exitCurrentQuest(true);
					return "31001-00.htm";
				}
				QuestState qs = player.getQuestState(_194_SevenSignContractOfMammon.class);
				if(qs == null || !qs.isCompleted())
				{
					st.exitCurrentQuest(true);
					return "noquest";
				}
				return "31001-01.htm";
			}
			else if(cond == 1)
				return "31001-05a.htm";
		}
		if(npcId == CaptainoftheDawnJohn)
		{
			if(cond == 1)
				return "32576-01.htm";
			else if(cond == 2)
				return "32576-02a.htm";
		}
		if(npcId == HighPriestRaymond)
		{
			if(cond == 2)
				return "30289-01.htm";
			else if(cond == 3)
			{
				if(st.getQuestItemsCount(EmperorShunaimanContract) != 0)
					return "30289-05.htm";
				else
					return "30289-04.htm";
			}
			else if(cond == 4)
				return "30289-07a.htm";
		}
		if(npcId == LightofDawn)
		{
			if(cond == 3)
				if(player.getTransformation() == GuardsOfTheDawn && st.getQuestItemsCount(GuardsoftheDawnIdentityCard) != 0)
				{
					enterInstance(player);
					return "32575-confirm.htm";
				}
				else
					return "32575-no.htm";
		}
		if(npcId == PasswordEntryDevice)
		{
			if(cond == 3)
			{
				player.teleToLocation(-78240, 205858, -7856);
				return "32577fq.htm";
			}
		}
		if(npcId == Bookcase)
		{
			if(cond == 3)
			{
				if(st.getQuestItemsCount(EmperorShunaimanContract) == 0)
				{
					st.giveItems(EmperorShunaimanContract, 1);
				}
				return "32580-01.htm";
			}
		}
		if(npcId == IasonHeine)
			if(cond == 4)
				return "30969-01.htm";
		if(npcId == IdentityConfirmDevice)
		{
			if(player.getTransformation() == GuardsOfTheDawn && st.getQuestItemsCount(GuardsoftheDawnIdentityCard) != 0)
			{
				if(_zone.checkIfInZone(npc.getX(), npc.getY()))
				{
					for(int doorId : DOORS)
						ReflectionTable.getInstance().get(r.getId()).openDoor(doorId);
					player.broadcastPacket(new ExShowScreenMessage("", 4000, ScreenMessageAlign.TOP_CENTER, true, 0, 3033, false));
					player.sendPacket(new SystemMessage(3033));
					player.sendPacket(new SystemMessage(3037));
					player.sendPacket(new SystemMessage(3038));
					return "32578-yes.htm";
				}
				else
				{
					for(int doorId : DOOR)
						//Functions.openDoor(doorId, player.getReflection().getId());
						ReflectionTable.getInstance().get(r.getId()).openDoor(doorId);
					player.sendPacket(new ExStartScenePlayer(11));
					player.sendPacket(new SystemMessage(3034));
					return "32578-yes.htm";
				}
			}
			else
				return "32578-no.htm"; //нет ХТМлки...
		}
		return "noquest";
	}

	private void enterInstance(L2Player player)
	{
		int instancedZoneId = 111;
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

		int timelimit = il.getTimelimit();
		SpawnNpc(r.getId());

		player.setReflection(r);
		player.teleToLocation(il.getTeleportCoords());
		player.setVar("backCoords", r.getReturnLoc().toXYZString());

		r.setNotCollapseWithoutPlayers(true);
		r.startCollapseTimer(timelimit * 60 * 1000L);

		_instances.put(player.getObjectId(), r.getId());
	}

	private void returnToAden(L2Player player)
	{
		Reflection r = player.getReflection();
		if(r.getReturnLoc() != null)
			player.teleToLocation(r.getReturnLoc(), 0);
		else
			player.setReflection(0);
		player.unsetVar("backCoords");
	}

	private void SpawnNpc(int refId)
	{
		//Чекпоинт 1
		//Первая комната
		InstSpawnMethod(18834, new Location(-74934, 213446, -7216, 33334), null, new Location(-75784, 213416, -7120), refId);
		InstSpawnMethod(18835, new Location(-74532, 212108, -7312), new Location(-74255, 212108, -7312), new Location(-75784, 213416, -7120), refId);
		InstSpawnMethod(18835, new Location(-75046, 212107, -7312), new Location(-74854, 212107, -7312), new Location(-75784, 213416, -7120), refId);
		InstSpawnMethod(18835, new Location(-75200, 211178, -7312), new Location(-75200, 211465, -7312), new Location(-75784, 213416, -7120), refId);
		InstSpawnMethod(18835, new Location(-75650, 212107, -7312), new Location(-75373, 212107, -7312), new Location(-75784, 213416, -7120), refId);
		InstSpawnMethod(18835, new Location(-74701, 211128, -7312), new Location(-74701, 211460, -7312), new Location(-75784, 213416, -7120), refId);
		InstSpawnMethod(18835, new Location(-74951, 211629, -7312, 16384), null, new Location(-75784, 213416, -7120), refId);
		//Вторая комната
		InstSpawnMethod(18834, new Location(-74750, 209820, -7408), new Location(-75168, 209820, -7408), new Location(-75784, 213416, -7120), refId);
		InstSpawnMethod(18834, new Location(-74750, 210174, -7408), new Location(-75168, 210174, -7408), new Location(-75784, 213416, -7120), refId);
		InstSpawnMethod(27351, new Location(-75301, 209980, -7408, 1722), null, new Location(-75784, 213416, -7120), refId);
		InstSpawnMethod(27351, new Location(-74619, 209981, -7408, 30212), null, new Location(-75784, 213416, -7120), refId);
		//Чекпоинт 2
		//Третья комната
		InstSpawnMethod(18834, new Location(-75428, 208115, -7504, 32768), null, new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-75654, 208112, -7504, 2718), null, new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-74955, 207611, -7504), null, new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(27351, new Location(-74282, 208784, -7504, 40959), null, new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(27351, new Location(-75454, 206740, -7504, 34645), null, new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(27351, new Location(-74558, 206625, -7504, 65102), null, new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-74956, 206312, -7504), new Location(-74956, 206680, -7504), new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-75402, 206939, -7504), new Location(-75704, 206939, -7504), new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-74206, 207064, -7504), new Location(-74520, 207064, -7504), new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-74200, 208290, -7504), new Location(-74508, 208290, -7504), new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-75559, 208712, -7504), new Location(-75559, 207860, -7504), new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-74216, 206515, -7504), new Location(-75668, 206515, -7504), new Location(-74952, 209192, -7488), refId);
		//Коридор
		InstSpawnMethod(18834, new Location(-76392, 207852, -7600), new Location(-76628, 207852, -7600), new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-76632, 208186, -7600), new Location(-76376, 208186, -7600), new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-76376, 208848, -7600), new Location(-76632, 208848, -7600), new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-76928, 209446, -7600), new Location(-76928, 209192, -7600), new Location(-74952, 209192, -7488), refId);
		InstSpawnMethod(18834, new Location(-77183, 209448, -7600), new Location(-77183, 209188, -7600), new Location(-74952, 209192, -7488), refId);
		//Чекпоинт 3
		//Комната 4
		InstSpawnMethod(18835, new Location(-76881, 208037, -7696), new Location(-77340, 208036, -7696), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-77702, 208184, -7696), new Location(-77702, 207414, -7696), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-77013, 207105, -7696), new Location(-77336, 207428, -7696), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-78054, 208464, -7696), new Location(-77361, 208464, -7696), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-78335, 207793, -7696), new Location(-77060, 207793, -7696), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-78520, 208036, -7696), new Location(-78065, 208036, -7696), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-78346, 207146, -7696, 8680), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-78113, 207384, -7696, 41575), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-77159, 207642, -7696, 32460), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-77558, 207138, -7696, 17906), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18835, new Location(-77558, 207138, -7696, 17906), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(27351, new Location(-77703, 207275, -7696, 49151), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(27351, new Location(-77703, 208320, -7696, 16384), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(27351, new Location(-76962, 207802, -7696, 35928), null, new Location(-77704, 208856, -7664), refId);
		//Круглая комната
		InstSpawnMethod(27351, new Location(-78891, 206272, -7888, 59013), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(27351, new Location(-79813, 205426, -7888, 9231), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(27351, new Location(-79814, 206277, -7888, 59013), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(27351, new Location(-78926, 205432, -7888, 23278), null, new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(27351, new Location(-78926, 205432, -7888, 23278), null, new Location(-77704, 208856, -7664), refId);

		InstSpawnMethod(18834, new Location(-79656, 206264, -7888), new Location(-79849, 206446, -7888), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18834, new Location(-79064, 205432, -7888), new Location(-78870, 205253, -7888), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18834, new Location(-79672, 205432, -7888), new Location(-79849, 205260, -7888), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18834, new Location(-79032, 206264, -7888), new Location(-78855, 206443, -7888), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18834, new Location(-79781, 205602, -7888), new Location(-79992, 205400, -7888), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18834, new Location(-79364, 205384, -7888), new Location(-79364, 204964, -7888), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18834, new Location(-78744, 205400, -7888), new Location(-78928, 205585, -7888), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18834, new Location(-79368, 206344, -7888), new Location(-79357, 206713, -7888), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18834, new Location(-78920, 206104, -7888), new Location(-78721, 206302, -7888), new Location(-77704, 208856, -7664), refId);
		InstSpawnMethod(18834, new Location(-80008, 206280, -7888), new Location(-79800, 206088, -7888), new Location(-77704, 208856, -7664), refId);
		//Чекпоинт 4 :D ласт(библиотека...)
		InstSpawnMethod(27351, new Location(-81535, 205503, -7984, 16384), null, new Location(-79992, 205864, -7888), refId);
		InstSpawnMethod(27351, new Location(-81536, 206223, -7984, 49151), null, new Location(-79992, 205864, -7888), refId);
		InstSpawnMethod(27351, new Location(-81144, 205856, -7984), new Location(-81938, 205856, -7984), new Location(-79992, 205864, -7888), refId);
	}

	private void InstSpawnMethod(int npcId, Location point1, Location point2, Location teleport, int refId)
	{
		L2NpcInstance npc = addSpawnToInstance(npcId, point1, 0, refId);
		GuardOfTheDawn ai = new GuardOfTheDawn(npc);
		if(point2 != null)
			ai.points = new Location[] { point1, point2 };
		ai.teleport = teleport;
		npc.setAI(ai);
		npc.getAI().startAITask();
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}
