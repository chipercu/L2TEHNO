package l2open.gameserver.model.entity.soi;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExSendUIEvent;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

public class SeedOfInfinity extends Quest
{
	private static HashMap<Integer, World> worlds = new HashMap<Integer, World>();
	private final ReentrantLock lock = new ReentrantLock();
	protected static final int BOSS_LIMIT_COUNT = 20;
	protected static final int time_limit = 300000;
	protected static final int max_npc_count = 10;
	protected static final int GATEKEEPER = 32539;
	protected static final int LIFE_SEED = 32541;
	protected static final int SOUL_DEVOURER = 22523;
	protected static final int MARK_OF_KEUCEREUS_STAGE_2 = 13692;
	protected static final int TEPIOS_REWARD = 32530;
	protected static final int TIME_TUMOR_RESPAWN = 180;
	protected static final int MOUTH_OF_EKIMUS = 32537;
	protected static final int YEHAN_KlANIKUS = 25666;
	protected static final int TIME_BOSS_DELAY_MAX = 60;
	protected static final int MARK_OF_KEUCEREUS_STAGE_1 = 13691;
	protected static final int DESTROYED_TUMOR2 = 32535;
	protected static final int spc_corpse_a = 18718;
	protected static final int TIME_BOSS_DELAY_MIN = 20;
	protected static final int TEPIOS = 32603;
	protected static final int RAVENOUS_SOUL_DEVOURER = 25636;
	protected static final int TIME_BOSS_DELAY_DEFAULT = 40;
	protected static final int spc_corpse_b = 18719;
	protected static final int TUMOR_OF_DEATH2 = 18708;
	protected static final int CENTER_TUMOR = 32547;
	protected static final int DESTROYED_TUMOR = 18705;
	protected static final int COHEMENES = 25634;
	protected static final int SYMBOL_OF_COHEMENES = 18780;
	protected static final int VESPER_STONE = 14052;
	protected static final int TUMOR_OF_DEATH = 18704;
	protected static final int YEHAN_KLODEKUS = 25665;
	protected static final int SOE = 736;
	protected static final int FERAL_HOUND = 29152;
	protected static final int TIME_LIMIT = 1500;
	protected static final int[][][] waves_spawn = new int[][][] { { { 22509, 2 }, { 22510, 3 } }, { { 22509, 1 }, { 22510, 3 }, { 22511, 3 } }, { { 22509, 2 }, { 22510, 2 }, { 22511, 2 }, { 22512, 3 } }, { { 22513, 2 }, { 22511, 2 }, { 22512, 2 }, { 22514, 3 } }, { { 22513, 2 }, { 22512, 3 }, { 22514, 3 }, { 22515, 2 } } };
	protected static final int[] MOBS = new int[] { 22509, 22510, 22511, 22512, 22513, 22514, 22515 };
	protected static final int[] SUPPLIES = new int[] { 0, 13777, 13778, 13779, 13780, 13781, 13782, 13783, 13784, 13785, 13786 };
	protected static final int[] monsters = new int[] { 22516, 22517, 22518, 22519, 22520, 22521, 22522, 22524, 22526, 22528, 22530, 22532, 22534 };
	protected static final int[] waves_time = new int[] { 1000, 10000, 15000, 25000, 30000, 35000 };
	protected static final Location center_tumor_loction = new Location(-179538, 211313, -15488, 16384);
	protected static final Location boss_spawn_location = new Location(-179528, 206728, -15488);
	protected static final Location[] mark_cohemenes_loactions = new Location[] { new Location(-178418, 211653, -12029, 49151), new Location(-178417, 206558, -12032, 16384), new Location(-180911, 206551, -12028, 16384), new Location(-180911, 211652, -12028, 49151) };
	protected static final Location[] tumor_death_locations = new Location[] { new Location(-176036, 210002, -11948, 36863), new Location(-176039, 208203, -11949, 28672), new Location(-183288, 208205, -11939, 4096), new Location(-183290, 210004, -11939, 61439) };
	protected static final Location[] tumors_locations = new Location[] { new Location(-179779, 212540, -15520, 49151), new Location(-177028, 211135, -15520, 36863), new Location(-176355, 208043, -15520, 28672), new Location(-179284, 205990, -15520, 16384), new Location(-182268, 208218, -15520, 4096), new Location(-182069, 211140, -15520, 61439) };
	protected static final String COHEMENES_START = "C'mon, c'mon! Show your face, you little rats! Let me see what the doomed weaklings are scheming!";
	protected static final String COHEMENES_DIE = "Keu... I will leave for now... But don't think this is over... The Seed of Infinity can never die...";
	protected static final L2GameServerPacket FAILED_HEART_OF_INFINITY_DEFEND = new ExShowScreenMessage("You have failed at Heart of Infinity Defend... The instance will shortly expire.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket TUMOR_DIE = new ExShowScreenMessage("The tumor inside Heart of Infinity has been destroyed! \n The speed that Ekimus calls out his prey has slowed down!", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket SECOND_FERAL = new ExShowScreenMessage("The second Feral Hound of the Netherworld has awakened!", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket PREPARE_HALL_OF_EROSION_ATTACK = new ExShowScreenMessage("You will participate in Hall of Erosion Attack shortly. Be prepared for anything.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket SUCCEEDED_HALL_OF_EROSION_ATTACK = new ExShowScreenMessage("Congratulations! You have succeeded at Hall of Erosion Attack! The instance will shortly expire.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket FAILED_HEART_OF_INFINITY_ATTACK = new ExShowScreenMessage("You have failed at Heart of Infinity Attack... The instance will shortly expire.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket DESTROY_MORE_TRUMOR = new ExShowScreenMessage("The tumor inside Hall of Erosion has been destroyed! \n In order to draw out the cowardly Cohemenes, you must destroy all the tumors!", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket TUMOR_RESPAWN = new ExShowScreenMessage("The tumor inside Heart of Infinity has completely revived. \n Ekimus started to regain his energy and is desperately looking for his prey...", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket PREPARE_HALL_OF_EROSION_DEFEND = new ExShowScreenMessage("You will participate in Hall of Erosion Defend shortly. Be prepared for anything.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket START_HEART_OF_INFINITY_ATTACK = new ExShowScreenMessage("You will participate in Heart of Infinity Attack shortly. Be prepared for anything.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket FAILED_HALL_OF_EROSION_ATTACK = new ExShowScreenMessage("You have failed at Hall of Erosion Attack... The instance will shortly expire.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket PREPARE_HEART_OF_INFINITY_DEFEND = new ExShowScreenMessage("You will participate in Heart of Infinity Defend shortly. Be prepared for anything.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket SUCCEEDED_HEART_OF_INFINITY_DEFEND = new ExShowScreenMessage("Congratulations! You have succeeded at Heart of Infinity Defend! The instance will shortly expire.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket SUCCEEDED_HALL_OF_EROSION_DEFEND = new ExShowScreenMessage("Congratulations! You have succeeded at Hall of Erosion Defend! The instance will shortly expire.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket START_HALL_OF_EROSION_ATTACK = new ExShowScreenMessage("You can hear the undead of Ekimus rushing toward you. Hall of Erosion Attack, it has now begun!", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket FAILED_HALL_OF_EROSION_DEFEND = new ExShowScreenMessage("You have failed at Hall of Erosion Defend... The instance will shortly expire.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket REVIVED_ALL_TRUMOR = new ExShowScreenMessage("The tumor inside Hall of Erosion has completely revived. \n The restrengthened Cohemenes has fled deeper inside the seed...", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket DESTROY_ALL_TRUMOR = new ExShowScreenMessage("All the tumors inside Hall of Erosion have been destroyed! \n Driven into a corner, Cohemenes appears close by!", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket START_HALL_OF_EROSION_DEFEND = new ExShowScreenMessage("You can hear the undead of Ekimus rushing toward you. Hall of Erosion Defend, it has now begun!", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket FIRST_FERAL = new ExShowScreenMessage("The first Feral Hound of the Netherworld has awakened!", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
	protected static final L2GameServerPacket START_HEART_OF_INFINITY_DEFEND = new ExShowScreenMessage("You can hear the undead of Ekimus rushing toward you. Hall of Erosion Defend, it has now begun!", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);

	public SeedOfInfinity(int id, int party)
    {
		super(0, id);
		addStartNpc(32603);
    }

	public void initialInstance(L2Player player)
	{
	}

	protected static void showMessageToParty(L2Party party, String meseg)
	{
		party.broadcastToPartyMembers(new ExShowScreenMessage(meseg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
	}

	protected static L2Party getParty(int id)
	{
		return getParty(ReflectionTable.getInstance().get(id));
	}

	public HallofErosionWorld getHallofErosionWorld(int id)
	{
		return (HallofErosionWorld)setWorld(id);
	}

	protected HeartOfInfinityWorld getHeartOfInfinityWorld(int id)
	{
		return (HeartOfInfinityWorld)setWorld(id);
	}

	private World setWorld(int id)
	{
		World world = null;
		lock.lock();
		try
		{
			world = worlds.get(id);
		}
		finally
		{
			lock.unlock();
		}
		return world;
	}

	protected boolean checkQuest(QuestState state)
	{
		L2Player player = state.getPlayer();
		return player != null && player.getVar("SeedOfInfinityQuest").equalsIgnoreCase(state.getQuest().getName());
	}

	protected static void showMessageToCommandChannel(L2CommandChannel channel, L2GameServerPacket packet)
	{
		channel.broadcastToChannelMembers(packet);
	}

	protected static L2CommandChannel getCommandChannel(int id)
	{
		return getCommandChannel(ReflectionTable.getInstance().get(id));
	}

	protected static QuestState getPlayerQuestState(L2Player player, String text)
	{
		return player.getQuestState(text);
	}

	protected static L2Party getParty(L2Player player)
	{
		return player.getParty();
	}

	public Reflection enterCommandChannelInstance(L2Player player, int id, World world)
	{
		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(id);
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return null;
		}
		InstancedZone iz = izs.get(0);
		//assert iz != null;

		String name = iz.getName();
		int time = iz.getTimelimit();

		Reflection ref = new Reflection(name);
		ref.setInstancedZoneId(id);

		for(InstancedZone i : izs.values())
        {
            if(ref.getReturnLoc() == null)
                ref.setReturnLoc(i.getReturnCoords());
            if(ref.getTeleportLoc() == null)
                ref.setTeleportLoc(i.getTeleportCoords());
            ref.FillSpawns(i.getSpawnsInfo());
            ref.FillDoors(i.getDoors());
        }

		world.instanceId = ref.getId();
		world.status = 0;
		world.createNpcList();
		world.timer = (System.currentTimeMillis() / 1000);
		lock.lock();

		try
		{
			worlds.put(ref.getId(), world);
		}
		finally
		{
			lock.unlock();
		}

		L2CommandChannel cc = player.getParty().getCommandChannel();
		for(L2Player member : cc.getMembers())
        {
            member.setReflection(ref);
            member.teleToLocation(iz.getTeleportCoords());
            member.setVar("backCoords", ref.getReturnLoc().toXYZString());
            member.setVarInst(name, String.valueOf(System.currentTimeMillis()));
        }
		cc.setReflection(ref);
		ref.setCommandChannel(cc);
		ref.startCollapseTimer(time * 60000);
		return ref;
	}

	protected boolean checkCondition(L2Player player, int cond)
	{
		return InstancedZoneManager.checkCondition(cond, player, true, player.getName(), "_10268_ToTheSeedOfInfinity") == null;
	}

	public static boolean checkCondition(L2Player player, int cond, String quest)
	{
		return InstancedZoneManager.checkCondition(cond, player, true, player.getName(), quest) == null;
	}

	protected boolean checkCondition(L2Player player, int cond, boolean isNidChannel)
	{
		return InstancedZoneManager.checkCondition(cond, player, isNidChannel, player.getName(), "_10268_ToTheSeedOfInfinity") == null;
	}

	protected HallofSufferingWorld getHallofSufferingWorld(int id)
	{
		return (HallofSufferingWorld)setWorld(id);
	}

	protected static L2Party getParty(Reflection ref)
	{
		return ref.getParty();
	}

	protected static void showMessageToParty(L2Party party, L2GameServerPacket packet)
	{
		party.broadcastToPartyMembers(packet);
	}

	public Reflection enterPartyInstance(L2Player player, int id, World world)
	{
		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(id);
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return null;
		}
		InstancedZone iz = izs.get(0);
		//assert iz != null;

		String name = iz.getName();
		int time = iz.getTimelimit();

		Reflection ref = new Reflection(name);
		ref.setInstancedZoneId(id);

        for(InstancedZone i : izs.values())
        {
            if (ref.getReturnLoc() == null)
                ref.setReturnLoc(i.getReturnCoords());
            if (ref.getTeleportLoc() == null)
                ref.setTeleportLoc(i.getTeleportCoords());
            ref.FillSpawns(i.getSpawnsInfo());
            ref.FillDoors(i.getDoors());
        }
		
		world.instanceId = ref.getId();
		world.status = 0;
		world.createNpcList();
		world.timer = (System.currentTimeMillis() / 1000);
		lock.lock();
		try
		{
			worlds.put(ref.getId(), world);
		}
		finally
		{
			lock.unlock();
		}
		
		for(L2Player member : player.getParty().getPartyMembers())
        {
            member.setReflection(ref);
            member.teleToLocation(iz.getTeleportCoords());
			if(iz.isDispellBuffs())
			{
			 	dispellBuffs(member);
				if(member.getPet() != null)
					dispellBuffs(member.getPet());
			}
            member.setVar("backCoords", ref.getReturnLoc().toXYZString());
            member.setVarInst(name, String.valueOf(System.currentTimeMillis()));
			member.sendPacket(new ExSendUIEvent(member, false, true, 0, time * 60, ""));
        }

		player.getParty().setReflection(ref);
		ref.setParty(player.getParty());
		ref.startCollapseTimer(time * 60000);
		return ref;
	}

	private void dispellBuffs(L2Playable playable)
	{
		if(playable != null)
		{
			for(L2Effect effect : playable.getEffectList().getAllEffects())
			{
				if(effect.getEffectType() == EffectType.Vitality)
					continue;
				if(!effect.getSkill().isOffensive() && !effect.getSkill().getName().startsWith("Adventurer's "))
					effect.exit(false, false);
				playable.updateEffectIcons();
			}
		}
	}

	protected static L2CommandChannel getCommandChannel(L2Player player)
	{
		return player.getParty() != null ? player.getParty().getCommandChannel() : null;
	}

	protected boolean checkKillProgress(L2NpcInstance npc, World world)
	{
		Map<L2NpcInstance, Boolean> npcList = world.getNpcList();
		if(!npcList.isEmpty())
		{
			if(npcList.containsKey(npc))
				npcList.put(npc, true);
			for(Boolean bool : npcList.values())
				if(!bool)
					return false;
		}
		return true;
	}

	protected static void showMessageToCommandChannel(L2CommandChannel chennal, String text)
	{
		chennal.broadcastToChannelMembers(new ExShowScreenMessage(text, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
	}

	protected static L2CommandChannel getCommandChannel(Reflection ref)
	{
		if(ref == null)
			return null;
		L2Party player = ref.getParty();
		return player == null ? ref.getCommandChannel() : player.getCommandChannel();
	}

	protected static void endInstance(L2CommandChannel channal, int time)
	{
		if(channal.getReflection() != null)
			channal.getReflection().startCollapseTimer(time * 1000);
	}

	protected static void endInstance(L2Party party, int time)
	{
		if(party.getReflection() != null)
			party.getReflection().startCollapseTimer(time * 1000);
		if(time/60 >= 1)
			party.broadcastToPartyMembers(new SystemMessage(2106).addNumber(time/60));
	}

	protected static void endInstance(L2Player player)
	{
		player.getReflection().startCollapseTimer(300000);
		if(player.isInParty())
			player.getParty().broadcastToPartyMembers(new SystemMessage(2106).addNumber(5));
	}

	protected static void endInstance(L2Player player, int time)
	{
		player.getReflection().startCollapseTimer(time * 1000);
		if(player.isInParty() && time/60 >= 1)
			player.getParty().broadcastToPartyMembers(new SystemMessage(2106).addNumber(time/60));
	}

	protected class BroadcastOnScreenMsgStr extends l2open.common.RunnableImpl
	{
		private L2GameServerPacket _packet;
		private L2CommandChannel _channel;

		public BroadcastOnScreenMsgStr(L2CommandChannel channel, L2GameServerPacket packet)
		{
			_channel = channel;
			_packet = packet;
		}

		@Override
		public void runImpl()
		{
			if(_channel != null)
				_channel.broadcastToChannelMembers(_packet);
		}
	}

	@Deprecated
    protected class BuffMobs extends l2open.common.RunnableImpl
    {
        L2NpcInstance actor;
        int radius;
        int recTime;

        public BuffMobs(L2NpcInstance _act, int _radius, int _recTime)
        {
            actor = _act;
            radius = _radius;
            recTime = _recTime;
        }

        @Override
        public void runImpl()
        {
            if(actor != null && !actor.isDead())
            {
                for(L2NpcInstance npc : actor.getAroundNpc(1000, 200))
                {
                    L2Skill skill = SkillTable.getInstance().getInfo(23076, 3);
                    skill.getEffects(npc, npc, false, false);
                }
                ThreadPoolManager.getInstance().schedule(new BuffMobs(actor, radius, recTime), recTime);
            }
        }
    }

	protected class TimeLimitCheck extends l2open.common.RunnableImpl
	{
		private HeartOfInfinityWorld _hiw;
		private L2CommandChannel _channel;
		private String _messeg;

		public TimeLimitCheck(HeartOfInfinityWorld hiw, L2CommandChannel channel, String messeg)
		{
			_hiw = hiw;
			_channel = channel;
			_messeg = messeg;
		}

		@Override
		public void runImpl()
		{
			if(!_hiw.successfully)
			{
				_hiw.timer -= 5;
				if(_hiw.timer <= 0)
				{
					if(_hiw.raidbossCount < 20)
					{
						_channel.broadcastToChannelMembers(SUCCEEDED_HEART_OF_INFINITY_DEFEND);
						for(L2Player member : _channel.getMembers())
						{
							QuestState state = getPlayerQuestState(member, getName());
							if(state != null)
								state.setCond(2);
						}
						_hiw.successfully = true;
						if(_hiw.bossSpawnTask != null)
							_hiw.bossSpawnTask.cancel(false);
						_hiw.bossSpawnTask = null;
					}
					if(_hiw.remainingTimeTask != null)
					{
						_hiw.remainingTimeTask.cancel(false);
						_hiw.remainingTimeTask = null;
					}
				}
				else
				{
					for(L2Player member : _channel.getMembers())
					{
						QuestState state = getPlayerQuestState(member, getName());
						if(state != null)
							state.exitCurrentQuest(true);
					}
					if(_hiw.bossSpawnTask != null)
					{
						_hiw.bossSpawnTask.cancel(false);
						_hiw.bossSpawnTask = null;
					}
					if(_hiw.remainingTimeTask != null)
					{
						_hiw.remainingTimeTask.cancel(false);
						_hiw.remainingTimeTask = null;
					}
				}
			}
			else
			{
				_channel.broadcastToChannelMembers(new ExShowScreenMessage(_messeg.replace("%time%", String.valueOf(_hiw.timer)), 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
				_hiw.remainingTimeTask = ThreadPoolManager.getInstance().schedule(new TimeLimitCheck(_hiw, _channel, _messeg), 300000);
			}
		}
    }

	protected class TimeRemaining extends l2open.common.RunnableImpl
	{
		private L2CommandChannel _channel;
		private HallofErosionWorld _hew;
		private String _messeg;

		public TimeRemaining(HallofErosionWorld hew, L2CommandChannel channel, String messeg)
		{
			_hew = hew;
			_channel = channel;
			_messeg = messeg;
		}

		@Override
		public void runImpl()
		{
			if(!_hew.raidboss_spawned)
			{
				_hew.timer -= 5;
				if(_hew.timer == 0)
				{
					_channel.broadcastToChannelMembers(getQuestIntId() == 696 ? FAILED_HALL_OF_EROSION_ATTACK : FAILED_HALL_OF_EROSION_DEFEND);
					endInstance(_channel, 15);
				}
				else if(_messeg != null)
				{
					_channel.broadcastToChannelMembers(new ExShowScreenMessage(_messeg.replace("%time%", String.valueOf(_hew.timer)), 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
					_hew.remainingTimeTask = ThreadPoolManager.getInstance().schedule(new TimeRemaining(_hew, _channel, _messeg), 300000);
				}
			}
		}
	}
}