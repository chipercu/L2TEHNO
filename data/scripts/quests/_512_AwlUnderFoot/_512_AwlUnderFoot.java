package quests._512_AwlUnderFoot;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Location;
import l2open.util.Rnd;

public class _512_AwlUnderFoot extends Quest implements ScriptFile
{
	private final static int FragmentOfTheDungeonLeaderMark = 9798;
	private final static int RewardMarksCount = 1500;
	private final static int KnightsEpaulette = 9912;

	private static final FastMap<Integer, Prison> _prisons = new FastMap<Integer, Prison>().setShared(true);

	private static final int RhiannaTheTraitor = 25546;
	private static final int TeslaTheDeceiver = 25549;
	private static final int SoulHunterChakundel = 25552;

	private static final int DurangoTheCrusher = 25553;
	private static final int BrutusTheObstinate = 25554;
	private static final int RangerKarankawa = 25557;
	private static final int SargonTheMad = 25560;

	private static final int BeautifulAtrielle = 25563;
	private static final int NagenTheTomboy = 25566;
	private static final int JaxTheDestroyer = 25569;

	private static final int[] type1 = new int[] { RhiannaTheTraitor, TeslaTheDeceiver, SoulHunterChakundel };
	private static final int[] type2 = new int[] { DurangoTheCrusher, BrutusTheObstinate, RangerKarankawa, SargonTheMad };
	private static final int[] type3 = new int[] { BeautifulAtrielle, NagenTheTomboy, JaxTheDestroyer };

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public _512_AwlUnderFoot()
	{
		super(false);

		// Wardens
		addStartNpc(36403, 36404, 36405, 36406, 36407, 36408, 36409, 36410, 36411);
		addQuestItem(FragmentOfTheDungeonLeaderMark);
		addKillId(RhiannaTheTraitor, TeslaTheDeceiver, SoulHunterChakundel, DurangoTheCrusher, BrutusTheObstinate, RangerKarankawa, SargonTheMad, BeautifulAtrielle, NagenTheTomboy, JaxTheDestroyer);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("gludio_prison_keeper_q0512_03.htm") || event.equalsIgnoreCase("gludio_prison_keeper_q0512_05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("exit"))
		{
			st.exitCurrentQuest(true);
			return null;
		}
		else if(event.equalsIgnoreCase("enter"))
			if(st.getState() == CREATED || !check(st.getPlayer()))
				return "gludio_prison_keeper_q0512_01a.htm";
			else
				return enterPrison(st.getPlayer());
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		if(!check(st.getPlayer()))
			return "gludio_prison_keeper_q0512_01a.htm";
		if(st.getState() == CREATED)
			return "gludio_prison_keeper_q0512_01.htm";
		if(st.getQuestItemsCount(FragmentOfTheDungeonLeaderMark) > 0)
		{
			st.giveItems(KnightsEpaulette, st.getQuestItemsCount(FragmentOfTheDungeonLeaderMark));
			st.takeItems(FragmentOfTheDungeonLeaderMark, -1);
			st.playSound(SOUND_FINISH);
			return "gludio_prison_keeper_q0512_08.htm";
		}
		return "gludio_prison_keeper_q0512_09.htm";
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		for(Prison prison : _prisons.values())
			if(prison.getReflectionId() == npc.getReflection().getId())
			{
				switch(npc.getNpcId())
				{
					case RhiannaTheTraitor:
					case TeslaTheDeceiver:
					case SoulHunterChakundel:
						prison.initSpawn(type2[Rnd.get(type2.length)], false);
						break;
					case DurangoTheCrusher:
					case BrutusTheObstinate:
					case RangerKarankawa:
					case SargonTheMad:
						prison.initSpawn(type3[Rnd.get(type3.length)], false);
						break;
					case BeautifulAtrielle:
					case NagenTheTomboy:
					case JaxTheDestroyer:
						L2Party party = st.getPlayer().getParty();
						if(party != null)
							for(L2Player member : party.getPartyMembers())
							{
								QuestState qs = member.getQuestState(getClass());
								if(qs != null && qs.isStarted())
								{
									qs.giveItems(FragmentOfTheDungeonLeaderMark, RewardMarksCount / party.getMemberCount());
									qs.playSound(SOUND_ITEMGET);
									qs.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
								}
							}
						else
						{
							st.giveItems(FragmentOfTheDungeonLeaderMark, RewardMarksCount);
							st.playSound(SOUND_ITEMGET);
							st.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
						}
						Reflection r = ReflectionTable.getInstance().get(prison.getReflectionId());
						if(r != null)
							r.startCollapseTimer(300000); // Всех боссов убили, запускаем коллапс через 5 минут
						break;
				}
				break;
			}

		return null;
	}

	private boolean check(L2Player player)
	{
		Castle castle = CastleManager.getInstance().getCastleByObject(player);
		if(castle == null)
			return false;
		L2Clan clan = player.getClan();
		if(clan == null)
			return false;
		if(clan.getClanId() != castle.getOwnerId())
			return false;
		return true;
	}

	private String enterPrison(L2Player player)
	{
		Castle castle = CastleManager.getInstance().getCastleByObject(player);
		if(castle == null || castle.getOwner() != player.getClan())
			return "gludio_prison_keeper_q0512_01a.htm";

		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> ils = ilm.getById(13);

		if(ils == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return null;
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
			return null;
		}

		if(player.isInParty())
		{
			if(player.getParty().isInReflection())
			{
				player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
				return null;
			}

			for(L2Player member : player.getParty().getPartyMembers())
				if(ilm.getTimeToNextEnterInstance(name, member) > 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
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
					return "gludio_prison_keeper_q0512_01a.htm";
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
			prison = _prisons.get(castle.getId());
			if(prison != null && prison.isLocked())
			{
				// TODO правильное сообщение
				player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
				return null;
			}
		}

		prison = new Prison(castle.getId(), ils);
		_prisons.put(prison.getCastleId(), prison);

		Reflection r = ReflectionTable.getInstance().get(prison.getReflectionId());

		r.setReturnLoc(player.getLoc());

		for(L2Player member : player.getParty().getPartyMembers())
		{
			if(member != player)
				newQuestState(member, STARTED);
			member.setReflection(r);
			member.setVar("backCoords", r.getReturnLoc().toXYZString());
			member.teleToLocation(il.getTeleportCoords());
			member.setVarInst(name, String.valueOf(System.currentTimeMillis()));
		}

		player.getParty().setReflection(r);
		r.setParty(player.getParty());
		r.startCollapseTimer(timelimit * 60 * 1000L);
		player.getParty().broadcastToPartyMembers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));

		prison.initSpawn(type1[Rnd.get(type1.length)], true);

		return null;
	}

	private class Prison
	{
		private int _castleId;
		private int _reflectionId;
		private long _lastEnter;

		private class PrisonSpawnTask extends l2open.common.RunnableImpl
		{
			int _npcId;

			public PrisonSpawnTask(int npcId)
			{
				_npcId = npcId;
			}

			public void runImpl()
			{
				addSpawnToInstance(_npcId, new Location(12152, -49272, -3008, 25958), 0, _reflectionId);
			}
		}

		public Prison(int id, FastMap<Integer, InstancedZone> ils)
		{
			try
			{
				Reflection r = new Reflection(ils.get(0).getName());
				r.setInstancedZoneId(13);
				for(InstancedZone i : ils.values())
				{
					if(r.getTeleportLoc() == null)
						r.setTeleportLoc(i.getTeleportCoords());
					if(i.getDoors() != null)
						for(L2DoorInstance d : i.getDoors())
						{
							L2DoorInstance door = d.clone();
							r.addDoor(door);
							door.setReflection(r);
							door.spawnMe();
						}
				}

				_reflectionId = r.getId();
				_castleId = id;
				_lastEnter = System.currentTimeMillis();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		public void initSpawn(int npcId, boolean first)
		{
			ThreadPoolManager.getInstance().schedule(new PrisonSpawnTask(npcId), first ? 60000 : 180000);
		}

		public int getReflectionId()
		{
			return _reflectionId;
		}

		public int getCastleId()
		{
			return _castleId;
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