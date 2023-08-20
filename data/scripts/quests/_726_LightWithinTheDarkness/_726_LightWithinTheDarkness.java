package quests._726_LightWithinTheDarkness;

import javolution.util.FastMap;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.SeducedInvestigatorInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Location;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class _726_LightWithinTheDarkness extends Quest implements ScriptFile
{
	private final static int DungeonLeaderMark = 9797;
	private final static int KnightsEpaulette = 9912;

	private static final FastMap<Integer, Prison> _prisons = new FastMap<Integer, Prison>().setShared(true);

	private static final int SeducedKnight = 36562;
	private static final int SeducedRanger = 36563;
	private static final int SeducedMage = 36564;
	private static final int SeducedWarrior = 36565;
	private static final int KanadisGuide1 = 25659;
	private static final int KanadisGuide2 = 25660;
	private static final int KanadisGuide3 = 25661;
	private static final int KanadisFollower1 = 25662;
	private static final int KanadisFollower2 = 25663;
	private static final int KanadisFollower3 = 25664;
	private static final long initdelay = 30 * 1000L;
	private static final long firstwavedelay = 120 * 1000L;
	private static final long secondwavedelay = 480 * 1000L; // 8 минут после первой волны
	private static final long thirdwavedelay = 480 * 1000L; // 16 минут после первой волны

	private ScheduledFuture<?> initTask;
	private ScheduledFuture<?> firstwaveTask;
	private ScheduledFuture<?> secondWaveTask;
	private ScheduledFuture<?> thirdWaveTask;

	public _726_LightWithinTheDarkness()
	{
		super(0, 726);

		// Detention Camp Wardens
		addStartNpc(35666, 35698, 35735, 35767, 35804, 35835, 35867, 35904, 35936, 35974, 36011, 36043, 36081, 36118, 36149, 36181, 36219, 36257, 36294, 36326, 36364);
		addQuestItem(DungeonLeaderMark);
		addKillId(KanadisGuide1, KanadisGuide2, KanadisGuide3, SeducedKnight, SeducedRanger, SeducedMage, SeducedWarrior);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("fort_a_campkeeper_q0726_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("exit") && !npc.isBusy())
		{
			for(Prison prison : _prisons.values())
				if(prison.getReflectionId() == npc.getReflection().getId())
				{
					Reflection r = ReflectionTable.getInstance().get(prison.getReflectionId());
					if(r != null)
						r.collapse();
				}
			return null;
		}
		else if(event.equalsIgnoreCase("enter"))
			if(st.getState() == CREATED || !check(st.getPlayer()))
				return "fort_a_campkeeper_q0726_01a.htm";
			else
				return enterPrison(st.getPlayer());
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		if(!check(st.getPlayer()))
			return "fort_a_campkeeper_q0726_01a.htm";
		if(st.getPlayer().getLevel() < 70)
			return "dcw_q726_0.htm";
		if(st.getState() == CREATED)
			return "fort_a_campkeeper_q0726_01.htm";
		if(st.getQuestItemsCount(DungeonLeaderMark) > 0)
		{
			st.giveItems(KnightsEpaulette, st.getQuestItemsCount(DungeonLeaderMark));
			st.takeItems(DungeonLeaderMark, -1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return "fort_a_campkeeper_q0726_06.htm";
		}
		return "dcw_q726_5.htm";
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		for(Prison prison : _prisons.values())
			if(prison.getReflectionId() == npc.getReflection().getId())
			{
				switch(npc.getNpcId())
				{
					case KanadisGuide1:
					case KanadisGuide2:
					case KanadisGuide3:
						Reflection r = ReflectionTable.getInstance().get(prison.getReflectionId());
						if(r != null)
						{
							if (npc.getNpcId() == KanadisGuide3)
							{
								for (L2NpcInstance m: r.getNpcs())
									if (m.getNpcId() == KanadisGuide3 && !m.isDead())
										return null;

								L2Party party = st.getPlayer().getParty();
								if(party != null)
									for(L2Player member : party.getPartyMembers())
									{
										QuestState qs = member.getQuestState(getName());
										if(qs != null && qs.isStarted())
										{
											qs.giveItems(DungeonLeaderMark, 159);
											qs.playSound(SOUND_ITEMGET);
											qs.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
											r.clearReflection(5, true);
										}
									}
								else
								{
									st.giveItems(DungeonLeaderMark, 159);
									st.playSound(SOUND_ITEMGET);
									st.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
									r.clearReflection(5, true);
								}

								for (L2NpcInstance m: r.getNpcs())
									if (m instanceof SeducedInvestigatorInstance)
										m.setBusy(false);
							}
						}
						break;
				}
				break;
			}

		return null;
	}

	private boolean check(L2Player player)
	{
		Fortress fort = FortressManager.getInstance().getFortressByObject(player);
		if(fort == null)
			return false;
		L2Clan clan = player.getClan();
		if(clan == null)
			return false;
		if(clan.getClanId() != fort.getOwnerId())
			return false;
		return true;
	}

	private String enterPrison(L2Player player)
	{
		Fortress fort = FortressManager.getInstance().getFortressByObject(player);
		if(fort == null || fort.getOwner() != player.getClan())
			return "fort_a_campkeeper_q0726_01a.htm";

		// Крепость должна быть независимой
		if(fort.getFortState() != 1)
			return "fort_a_campkeeper_q0726_08.htm";

		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZoneManager.InstancedZone> ils = ilm.getById(90);

		if(ils == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return null;
		}

		InstancedZoneManager.InstancedZone il = ils.get(0);

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
			return null;
		}

		if(player.isInParty())
		{
			if(player.getParty().isInReflection())
			{
				player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
				return null;
			}

            if(ServerVariables.getLong("_q726"+player.getClanId(), 0) > System.currentTimeMillis())
            {
                player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addString(player.getClan().getName()));
                return null;
            }

			if(!player.getParty().isLeader(player))
			{
				player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
				return null;
			}

			if(player.getParty().getMemberCount() > maxParty)
			{
				player.sendPacket(Msg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
				return null;
			}

			for(L2Player member : player.getParty().getPartyMembers())
			{
				if(member.getLevel() < min_level || member.getLevel() > max_level)
				{
					SystemMessage sm = new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
					member.sendPacket(sm);
					player.sendPacket(sm);
					return null;
				}
				if(member.getClan() != player.getClan())
					return "fort_a_campkeeper_q0726_01a.htm";
				if(!player.isInRange(member, 500))
				{
					member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					return null;
				}
			}
		}

		Prison prison = null;
		if(!_prisons.isEmpty())
		{
			prison = _prisons.get(fort.getId());
			if(prison != null && prison.isLocked())
			{
				player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
				return null;
			}
		}

		prison = new Prison(fort.getId(), ils);
		_prisons.put(prison.getFortId(), prison);

		Reflection r = ReflectionTable.getInstance().get(prison.getReflectionId());

		r.setReturnLoc(player.getLoc());

		for(L2Player member : player.getParty().getPartyMembers())
		{
			if(member.getQuestState(getName()) == null)
				newQuestState(member, STARTED);
			else if(member.getQuestState(getName()).getState() != STARTED)
			{
				member.getQuestState(getName()).setState(STARTED);
				member.getQuestState(getName()).setCond(1);
			}
			member.setReflection(r);
			member.teleToLocation(il.getTeleportCoords());
			member.setVar("backCoords", r.getReturnLoc().toXYZString());
		}

		ServerVariables.set("_q726"+player.getClanId(), System.currentTimeMillis() + 1000 * 60 * 60 * 4);
		player.getParty().setReflection(r);
		r.setParty(player.getParty());
		r.startCollapseTimer(timelimit * 60 * 1000L);
		player.getParty().broadcastToPartyMembers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));
		initTask = ThreadPoolManager.getInstance().schedule(new InvestigatorsSpawn(player.getParty().getReflection()), initdelay);
		firstwaveTask = ThreadPoolManager.getInstance().schedule(new FirstWave(player.getParty().getReflection()), firstwavedelay);
		return null;
	}
	
	public class InvestigatorsSpawn extends RunnableImpl
	{
		Reflection _reflection;

		public InvestigatorsSpawn(Reflection ref)
		{
			_reflection = ref;
		}

		@Override
		public void runImpl() throws Exception
		{
			Location ranger = new Location(49192, -12232, -9384, 0);
			Location mage = new Location(49192, -12456, -9392, 0);
			Location warrior = new Location(49192, -11992, -9392, 0);
			Location knight = new Location(49384, -12232, -9384, 0);
			addSpawnToInstance(SeducedKnight, knight, 0, _reflection.getId());
			addSpawnToInstance(SeducedRanger, ranger, 0, _reflection.getId());
			addSpawnToInstance(SeducedMage, mage, 0, _reflection.getId());
			addSpawnToInstance(SeducedWarrior, warrior, 0, _reflection.getId());
		}
	}

	public class FirstWave extends RunnableImpl
	{
		Reflection _reflection;

		public FirstWave(Reflection ref)
		{
			_reflection = ref;
		}

		@Override
		public void runImpl() throws Exception
		{
			List<L2Player> who = _reflection.getPlayers();
			if(who != null && !who.isEmpty())
				for(L2Player player : who)
					player.sendPacket(new ExShowScreenMessage("Начало 1-го этапа!", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

			Location bossnminions = new Location(50536, -12232, -9384, 32768);
			addSpawnToInstance(KanadisGuide1, bossnminions, 0, _reflection.getId());
			for(int i = 0; i < 10; i++)
				addSpawnToInstance(KanadisFollower1, bossnminions, 400, _reflection.getId());
			secondWaveTask = ThreadPoolManager.getInstance().schedule(new SecondWave(_reflection), secondwavedelay);
		}
	}

	public class SecondWave extends RunnableImpl
	{
		Reflection _reflection;

		public SecondWave(Reflection ref)
		{
			_reflection = ref;
		}

		@Override
		public void runImpl() throws Exception
		{
			List<L2Player> who = _reflection.getPlayers();
			if(who != null && !who.isEmpty())
				for(L2Player player : who)
					player.sendPacket(new ExShowScreenMessage("Начало 2-го этапа!", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

			Location bossnminions = new Location(50536, -12232, -9384, 32768);
			addSpawnToInstance(KanadisGuide2, bossnminions, 0, _reflection.getId());
			for(int i = 0; i < 10; i++)
				addSpawnToInstance(KanadisFollower2, bossnminions, 400, _reflection.getId());
			thirdWaveTask = ThreadPoolManager.getInstance().schedule(new ThirdWave(_reflection), thirdwavedelay);
		}
	}

	public class ThirdWave extends RunnableImpl
	{
		Reflection _reflection;

		public ThirdWave(Reflection ref)
		{
			_reflection = ref;
		}

		@Override
		public void runImpl() throws Exception
		{
			List<L2Player> who = _reflection.getPlayers();
			if(who != null && !who.isEmpty())
				for(L2Player player : who)
					player.sendPacket(new ExShowScreenMessage("Начало 3-го этапа!", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

			Location bossnminions = new Location(50536, -12232, -9384, 32768);
			addSpawnToInstance(KanadisGuide3, bossnminions, 100, _reflection.getId());
			addSpawnToInstance(KanadisGuide3, bossnminions, 100, _reflection.getId());
			for(int i = 0; i < 10; i++)
				addSpawnToInstance(KanadisFollower3, bossnminions, 400, _reflection.getId());
		}
	}

	public static void OnDie(L2Character killed, L2Character killer)
	{
		if(killed == null || (killed.getNpcId() != 36562 && killed.getNpcId() != 36563 && killed.getNpcId() != 36564 && killed.getNpcId() != 36565 && killed.getNpcId() != 36566 && killed.getNpcId() != 36567 && killed.getNpcId() != 36568 && killed.getNpcId() != 36569))
			return;

		for(Prison prison : _prisons.values())
			if(prison.getReflectionId() == killed.getReflection().getId())
			{
				switch(killed.getNpcId())
				{
					case SeducedKnight:
					case SeducedRanger:
					case SeducedMage:
					case SeducedWarrior:
						Reflection ref = ReflectionTable.getInstance().get(prison.getReflectionId());
						if(ref != null)
						{
							ref.collapse();
						}
						break;
				}
			}
	}

	private class Prison
	{
		private int _fortId;
		private int _reflectionId;
		private long _lastEnter;
		public Prison(int id, FastMap<Integer, InstancedZoneManager.InstancedZone> ils)
		{
			try
			{
				Reflection r = new Reflection(ils.get(0).getName());
				r.setInstancedZoneId(90);
				for(InstancedZoneManager.InstancedZone i : ils.values())
				{
					if(r.getTeleportLoc() == null)
						r.setTeleportLoc(i.getTeleportCoords());
					r.FillSpawns(i.getSpawnsInfo());
					r.FillDoors(i.getDoors());
				}

				_reflectionId = r.getId();
				_fortId = id;
				_lastEnter = System.currentTimeMillis();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		public int getReflectionId()
		{
			return _reflectionId;
		}

		public int getFortId()
		{
			return _fortId;
		}

		public boolean isLocked()
		{
			return System.currentTimeMillis() - _lastEnter < 4 * 60 * 60 * 1000L;
		}
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}