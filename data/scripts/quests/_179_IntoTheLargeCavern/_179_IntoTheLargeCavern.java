package quests._179_IntoTheLargeCavern;

import java.util.HashMap;

import javolution.util.FastMap;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Location;
import quests._178_IconicTrinity._178_IconicTrinity;

public class _179_IntoTheLargeCavern extends Quest implements ScriptFile
{
	public class World
	{
		public int instanceId;
		public int status;
	}

	private static HashMap<Integer, World> worlds = new HashMap<Integer, World>();

	private final static int KEKROPUS = 32138;
	private final static int GardenGuard = 25529;

	private final static int GardenGuard1 = 18347;
	private final static int GardenGuard2 = 18348;
	private final static int GardenGuard3 = 18349;

	private final static int Kamael_Guard = 18352;
	private final static int Guardian_of_Records = 18353;
	private final static int Guardian_of_Observation = 18354;
	private final static int Spiculas_Guard = 18355;
	private final static int Harkilgameds_Gatekeeper = 18356;
	private final static int Rodenpiculas_Gatekeeper = 18357;
	private final static int Guardian_of_Secrets = 18358;
	private final static int Guardian_of_Arviterre = 18359;
	private final static int Katenars_Gatekeeper = 18360;
	private final static int Guardian_of_Prediction = 18361;

	private final static int Gate_Key_Kamael = 9703;
	private final static int Gate_Key_Archives = 9704;
	private final static int Gate_Key_Observation = 9705;
	private final static int Gate_Key_Spicula = 9706;
	private final static int Gate_Key_Harkilgamed = 9707;
	private final static int Gate_Key_Rodenpicula = 9708;
	private final static int Gate_Key_Arviterre = 9709;
	private final static int Gate_Key_Katenar = 9710;
	private final static int Gate_Key_Prediction = 9711;
	private final static int Gate_Key_Massive_Cavern = 9712;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _179_IntoTheLargeCavern()
	{
		super(1, 179);

		addStartNpc(KEKROPUS);
		addTalkId(GardenGuard);
		addAttackId(GardenGuard1);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("32138-06.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("EnterNornilsGarden"))
		{
			if(st.getCond() != 1 || st.getPlayer().getRace() != Race.kamael)
				return "noquest";
			enterInstance(npc, st.getPlayer());
			return null;
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		String htmltext = "noquest";
		if(st.getCond() == 0)
		{
			htmltext = "32138-01.htm";
			if(player.getLevel() < 17)
			{
				htmltext = "32138-02.htm";
				st.exitCurrentQuest(true);
			}
			else if(player.getLevel() > 20 || player.getClassId().getLevel() > 1)
			{
				htmltext = "32138-02a.htm";
				st.exitCurrentQuest(true);
			}
			else if(!player.isQuestCompleted(_178_IconicTrinity.class))
			{
				htmltext = "32138-03.htm";
				st.exitCurrentQuest(true);
			}
			else if(player.getRace() != Race.kamael)
			{
				htmltext = "32138-04.htm";
				st.exitCurrentQuest(true);
			}
		}
		else
			htmltext = "32138-07.htm";
		return htmltext;
	}

	@Override
	public String onAttack(L2NpcInstance npc, QuestState st)
	{
		World world = worlds.get(npc.getReflection().getId());
		if(world != null && world.status == 0)
		{
			world.status = 1;
			addSpawnToInstance(GardenGuard3, new Location(-110016, 74512, -12533, 0), 0, world.instanceId);
			addSpawnToInstance(GardenGuard2, new Location(-109729, 74913, -12533, 0), 0, world.instanceId);
			addSpawnToInstance(GardenGuard2, new Location(-109981, 74899, -12533, 0), 0, world.instanceId);
		}
		return null;
	}

	public void OnDie(L2Character cha, L2Character killer)
	{
		if(cha == null || !cha.isNpc())
			return;
		switch(cha.getNpcId())
		{
			case Kamael_Guard:
				dropItem(cha, Gate_Key_Kamael, 1);
				break;
			case Guardian_of_Records:
				dropItem(cha, Gate_Key_Archives, 1);
				break;
			case Guardian_of_Observation:
				dropItem(cha, Gate_Key_Observation, 1);
				break;
			case Spiculas_Guard:
				dropItem(cha, Gate_Key_Spicula, 1);
				break;
			case Harkilgameds_Gatekeeper:
				dropItem(cha, Gate_Key_Harkilgamed, 1);
				break;
			case Rodenpiculas_Gatekeeper:
				dropItem(cha, Gate_Key_Rodenpicula, 1);
				break;
			case Guardian_of_Arviterre:
				dropItem(cha, Gate_Key_Arviterre, 1);
				break;
			case Katenars_Gatekeeper:
				dropItem(cha, Gate_Key_Katenar, 1);
				break;
			case Guardian_of_Prediction:
				dropItem(cha, Gate_Key_Prediction, 1);
				break;
			case Guardian_of_Secrets:
				dropItem(cha, Gate_Key_Massive_Cavern, 1);
				break;
		}
	}

	private void enterInstance(L2NpcInstance npc, L2Player player)
	{
		int instancedZoneId = 11;
		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> ils = ilm.getById(11);
		if(ils == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone il = ils.get(0);

		assert il != null;

		String name = il.getName();
		int timelimit = il.getTimelimit();
		int min_level = il.getMinLevel();
		int max_level = il.getMaxLevel();
		int minParty = il.getMinParty();
		int maxParty = il.getMaxParty();

		if(minParty > 1 && !player.isInParty())
		{
			player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
			return;
		}

		if(player.isInParty())
		{
			if(player.getParty().isInReflection())
			{
				player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
				return;
			}

			for(L2Player member : player.getParty().getPartyMembers())
				if(ilm.getTimeToNextEnterInstance(name, member) > 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
					return;
				}

			if(!player.getParty().isLeader(player))
			{
				player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
				return;
			}

			if(player.getParty().getMemberCount() > maxParty)
			{
				player.sendPacket(Msg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
				return;
			}

			for(L2Player member : player.getParty().getPartyMembers())
			{
				if(member.getLevel() < min_level || member.getLevel() > max_level)
				{
					SystemMessage sm = new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
					member.sendPacket(sm);
					player.sendPacket(sm);
					return;
				}
				if(member.getClassId().getLevel() > 1 || member.isCursedWeaponEquipped())
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
					return;
				}
				if(!player.isInRange(member, 500))
				{
					member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					return;
				}
			}
		}

		Reflection r = new Reflection(name);
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

		World world = new World();
		world.instanceId = r.getId();
		worlds.put(r.getId(), world);

		for(L2Player member : player.getParty().getPartyMembers())
		{
			npc.makeSupportMagic(player);
			if(member != player)
				newQuestState(member, STARTED);
			member.setReflection(r);
			member.teleToLocation(il.getTeleportCoords());
			member.setVar("backCoords", r.getReturnLoc().toXYZString());
			member.setVarInst(name, String.valueOf(System.currentTimeMillis()));
		}

		player.getParty().setReflection(r);
		r.setParty(player.getParty());
		r.startCollapseTimer(timelimit * 60 * 1000L);
		player.getParty().broadcastToPartyMembers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));
	}

	private void dropItem(L2Character npc, int itemId, int count)
	{
		L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);
		item.setCount(count);
		item.dropMe(npc, npc.getLoc());
	}
}