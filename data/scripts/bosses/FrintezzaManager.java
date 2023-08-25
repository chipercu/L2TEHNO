package bosses;

import l2open.common.*;
import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.extensions.listeners.CurrentHpChangeListener;
import l2open.extensions.listeners.PropertyCollection;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Rnd;

import java.util.ArrayList;
import java.util.List;

import ai.Scarlet;

/**
 * @author: unkown
 * @reauthor: Drizzy
 * @edit-date: 17.11.2011
 * @ Manager for Epic-Boss Frintezza. Rework spawn on instance and AI.
**/

public class FrintezzaManager extends Functions implements ScriptFile
{
	private static Location frintezzaSpawn = new Location(-87776, -155085, -9086, 16048, 29045);
	private static Location scarletSpawnWeak = new Location(-87785, -153300, -9176, 16384, 29046);

	private static Location[] portraitSpawns = {
			new Location(-86185, -152456, -9168, 35048, 29048),
			new Location(-86137, -153976, -9168, 28205, 29049),
			new Location(-89417, -153976, -9168, 64817, 29048),
			new Location(-89385, -152456, -9168, 57730, 29049) };

	private static Location[] demonSpawns = {
			new Location(-86185, -152456, -9168, 35048, 29050),
			new Location(-86137, -153976, -9168, 28205, 29051),
			new Location(-89417, -153976, -9168, 64817, 29051),
			new Location(-89385, -152456, -9168, 57730, 29050) };

	private static int _intervalOfFrintezzaSongs = 60000;
	private static CurrentHpListener _currentHpListener = new CurrentHpListener();
	private static final int FWF_INTERVALOFNEXTMONSTER = 20000;
	private static final int FWF_ACTIVITYTIMEOFFRINTEZZA = 120 * 60000;
	private static final int _strongScarletId = 29047;
	private static final int _frintezzasSwordId = 7903;

	public static class CurrentHpListener extends CurrentHpChangeListener
	{
		@Override
		public void onCurrentHpChange(L2Character actor, double oldHp, double newHp)
		{
			if(actor == null || actor.getNpcId() != 29046 || actor.isDead())
				return;
			LastImperialTombManager.World world = LastImperialTombManager.getWorld(actor.getReflectionId());
			if(world == null)
				return;
			double maxHp = actor.getMaxHp();

			switch(world._scarletMorph)
			{
				case 1:
					if(newHp < 0.6 * maxHp)
					{
						world._scarletMorph = 2;
						ThreadPoolManager.getInstance().schedule(new SecondMorph(1, world.reflection.getId()), 1100);
						world.weakScarlet.i_ai4 = 1;
					}
					break;
				case 2:
					if(newHp < 0.2 * maxHp)
					{
						world._scarletMorph = 3;
						ThreadPoolManager.getInstance().schedule(new ThirdMorph(1, world.reflection.getId(), null), 2000);
					}
					break;
			}
		}
	}

	private static L2NpcInstance spawn_fr(Location loc, int world_id)
	{
		L2NpcTemplate template = NpcTable.getTemplate(loc.id);
		L2NpcInstance npc = template.getNewInstance();
		npc.setSpawnedLoc(loc);
        npc.setReflection(world_id);
		npc.onSpawn();
		npc.setHeading(loc.h);
		npc.setXYZInvisible(loc);
		npc.spawnMe();
		return npc;
	}

	public static void setScarletSpawnTask(boolean forced, int world_id)
	{
		LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
		if(world != null && forced || world != null && world._monsterSpawnTask == null)
		{
			if(forced && world._monsterSpawnTask != null)
				world._monsterSpawnTask.cancel(true);
			world._monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new Spawn(1, world_id), forced ?  1000 : Rnd.get(300000, 600000));
		}
	}

	protected static void broadCastPacket(int world_id, L2GameServerPacket packet)
	{
		LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
		for(L2Player pc : world.reflection.getPlayers())
		{
			if(pc != null && pc.isOnline() && pc.getReflection().getId() == world.reflection.getId())
				pc.sendPacket(packet);
		}
	}

	private static void sendPacketX(int world_id, L2GameServerPacket packet1, L2GameServerPacket packet2, int x)
	{
		LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
		for(L2Player pc : world.reflection.getPlayers())
		{
			if(pc != null && pc.isOnline() && pc.getReflection().getId() == world.reflection.getId())
			{
				if(pc.getX() < x)
					pc.sendPacket(packet1);
				else
					pc.sendPacket(packet2);
			}
		}
	}

	private static class Spawn extends RunnableImpl
	{
		private int _taskId;
		private int world_id;

		public Spawn(int taskId, int _world_id)
		{
			_taskId = taskId;
			world_id = _world_id;
		}

		public void runImpl()
		{
			try
			{
				LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
				switch(_taskId)
				{
					case 1: // spawn.
						world._frintezzaDummy = spawn_fr(new Location(-87784, -155083, -9087, 16048, 29052), world_id);
						world._frintezzaDummy.setIsInvul(true);
						world._frintezzaDummy.p_block_move(true, null);

						world.overheadDummy = spawn_fr(new Location(-87784, -153298, -9175, 16384, 29052), world_id);
						world.overheadDummy.setIsInvul(true);
						world.overheadDummy.p_block_move(true, null);
						world.overheadDummy.setCollisionHeight(600);
						broadCastPacket(world_id, new NpcInfo(world.overheadDummy, null));

						world.portraitDummy1 = spawn_fr(new Location(-89566, -153168, -9165, 16048, 29052), world_id);
						world.portraitDummy1.p_block_move(true, null);
						world.portraitDummy1.setIsInvul(true);

						world.portraitDummy3 = spawn_fr(new Location(-86004, -153168, -9165, 16048, 29052), world_id);
						world.portraitDummy3.p_block_move(true, null);
						world.portraitDummy3.setIsInvul(true);

						world.scarletDummy = spawn_fr(new Location(-87784, -153298, -9175, 16384, 29053), world_id);
						world.scarletDummy.setIsInvul(true);
						world.scarletDummy.p_block_move(true, null);
						ThreadPoolManager.getInstance().schedule(new Spawn(2,world_id), 1000);
						break;
					case 2:
						broadCastPacket(world_id, new SpecialCamera(world.overheadDummy, 0, 75, -89, 0, 100, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SpecialCamera(world.overheadDummy, 0, 75, -89, 0, 100, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SpecialCamera(world.overheadDummy, 300, 90, -10, 6500, 7000, 0, 0, 1, 0, 0));
						//ReflectionTable.getInstance().get(world_id).closeDoor(17130046);
						LastImperialTombManager.getWorld(world_id).reflection.closeDoor(17130046);
						world.frintezza = spawn_fr(frintezzaSpawn, world_id);
						for(int i = 0; i < 4; i++)
						{
							world.portraits[i] = spawn_fr(portraitSpawns[i], world_id);
							world.portraits[i].p_block_move(true, null);
							world.demons[i] = spawn_fr(demonSpawns[i], world_id);
						}
						blockAll(true, world_id);
						ThreadPoolManager.getInstance().schedule(new Spawn(3,world_id), 6500);
						break;
					case 3:
						broadCastPacket(world_id, new SpecialCamera(world._frintezzaDummy, 1800, 90, 8, 6500, 7000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(4,world_id), 900);
						break;
					case 4:
						broadCastPacket(world_id, new SpecialCamera(world._frintezzaDummy, 140, 90, 10, 2500, 4500, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(5,world_id), 4000);
						break;
					case 5:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 40, 75, -10, 0, 1000, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 40, 75, -10, 0, 12000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(6,world_id), 1350);
						break;
					case 6:
						world.frintezza.broadcastPacket(new SocialAction(world.frintezza.getObjectId(), 2));
						ThreadPoolManager.getInstance().schedule(new Spawn(7,world_id), 7000);
						break;
					case 7:
						world._frintezzaDummy.deleteMe();
						world._frintezzaDummy = null;
						world._zonefrintezza = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702120, false);
						ThreadPoolManager.getInstance().schedule(new Spawn(8,world_id), 1000);
						break;
					case 8:
						broadCastPacket(world_id, new SocialAction(world.demons[1].getObjectId(), 1));
						broadCastPacket(world_id, new SocialAction(world.demons[2].getObjectId(), 1));
						ThreadPoolManager.getInstance().schedule(new Spawn(9,world_id), 400);
						break;
					case 9:
						broadCastPacket(world_id, new SocialAction(world.demons[0].getObjectId(), 1));
						broadCastPacket(world_id, new SocialAction(world.demons[3].getObjectId(), 1));
						sendPacketX(world_id, new SpecialCamera(world.portraitDummy1, 1000, 118, 0, 0, 1000, 0, 0, 1, 0, 0), new SpecialCamera(world.portraitDummy3, 1000, 62, 0, 0, 1000, 0, 0, 1, 0, 0), -87784);
						sendPacketX(world_id, new SpecialCamera(world.portraitDummy1, 1000, 118, 0, 0, 10000, 0, 0, 1, 0, 0), new SpecialCamera(world.portraitDummy3, 1000, 62, 0, 0, 10000, 0, 0, 1, 0, 0), -87784);
						ThreadPoolManager.getInstance().schedule(new Spawn(10,world_id), 2000);
						break;
					case 10:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 240, 90, 0, 0, 1000, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 240, 90, 25, 5500, 10000, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SocialAction(world.frintezza.getObjectId(), 3));
						world.portraitDummy1.deleteMe();
						world.portraitDummy3.deleteMe();
						world.portraitDummy1 = null;
						world.portraitDummy3 = null;
						ThreadPoolManager.getInstance().schedule(new Spawn(11,world_id), 4500);
						break;
					case 11:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 100, 195, 35, 0, 10000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(12,world_id), 700);
						break;
					case 12:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 100, 195, 35, 0, 10000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(13,world_id), 1300);
						break;
					case 13:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 120, 180, 45, 1500, 10000, 0, 0, 1, 0, 0));
						world.frintezza.broadcastSkill(new MagicSkillUse(world.frintezza, world.frintezza, 5006, 1, 34000, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(14,world_id), 1500);
						break;
					case 14:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 520, 135, 45, 8000, 10000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(15,world_id), 7500);
						break;
					case 15:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 1500, 110, 25, 10000, 13000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(16,world_id), 9500);
						break;
					case 16:
						broadCastPacket(world_id, new SpecialCamera(world.overheadDummy, 930, 160, -20, 0, 1000, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SpecialCamera(world.overheadDummy, 600, 180, -25, 0, 10000, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new MagicSkillUse(world.scarletDummy, world.overheadDummy, 5004, 1, 5800, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(17,world_id), 5000);
						break;
					case 17:
						world.weakScarlet = spawn_fr(scarletSpawnWeak, world_id);
						world.weakScarlet.setRHandId(8204);
						block(world.weakScarlet, true);
						world.weakScarlet.getListenerEngine().addPropertyChangeListener(PropertyCollection.HitPoints, _currentHpListener);
						Earthquake eq = new Earthquake(world.weakScarlet.getLoc(), 50, 6);
						for(L2Player pc : world.reflection.getPlayers())
							pc.broadcastPacket(eq);
						broadCastPacket(world_id, new SocialAction(world.weakScarlet.getObjectId(), 3));
						broadCastPacket(world_id, new SpecialCamera(world.scarletDummy, 800, 180, 10, 1000, 10000, 0, 0, 1, 0, 0));

						ThreadPoolManager.getInstance().schedule(new Spawn(18,world_id), 2100);
						break;
					case 18:
						broadCastPacket(world_id, new SpecialCamera(world.weakScarlet, 300, 60, 8, 0, 10000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(19,world_id), 2000);
						break;
					case 19:
						broadCastPacket(world_id, new SpecialCamera(world.weakScarlet, 500, 90, 10, 3000, 5000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new Spawn(20,world_id), 3000);
						break;
					case 20:
						for(L2Player pc : world.reflection.getPlayers())
							pc.leaveMovieMode();
						world.overheadDummy.deleteMe();
						world.scarletDummy.deleteMe();
						world.overheadDummy = null;
						world.scarletDummy = null;
						ThreadPoolManager.getInstance().schedule(new Spawn(21,world_id), 2000);
						break;
					case 21:
						blockAll(false, world_id);
						world._scarletMorph = 1;
						world._activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(world_id), FWF_ACTIVITYTIMEOFFRINTEZZA);
						for(int i = 0; i < 4; i++)
							ThreadPoolManager.getInstance().schedule(new doSkill(world.demons[i], _intervalOfFrintezzaSongs, 1000), 4000);
						ThreadPoolManager.getInstance().schedule(new respawnDemons(world_id), FWF_INTERVALOFNEXTMONSTER);
						ThreadPoolManager.getInstance().schedule(new Music(world_id), Rnd.get(_intervalOfFrintezzaSongs));
						spawn_fr(new Location(-87904, -141296, -9168, 0, 29061), world_id);
						LastImperialTombManager.getWorld(world_id).reflection.closeDoor(17130042);
						// ReflectionTable.getInstance().get(world_id).closeDoor(17130042);
						L2Character target = getRandomPlayer(world_id);
						if(target != null)
						{
							world.weakScarlet.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 10000);
						}
						break;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class Music extends RunnableImpl
	{
		private int world_id;
		public Music(int _world_id)
		{
			world_id = _world_id;
		}

		public void runImpl()
		{
			LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
			if(world == null)
				return;
			int song = Math.max(1, Math.min(5, getSong()));
			//Если вернулись к ид 5, то обрываем каст песен. И запускаем треадпул на запуск следующей песни.
			if(song == 5)
			{
				ThreadPoolManager.getInstance().schedule(new Music(world_id), _intervalOfFrintezzaSongs + Rnd.get(10000));
				return;
			}
			int song_name = 0;
			switch(song)
			{
				case 1:
					song_name = 1000522;
					break;
				case 2:
					song_name = 1000524;
					break;
				case 3:
					song_name = 1000523;
					break;
				case 4:
					song_name = 1000526;
					break;
			}
			if(world.frintezza != null && !world.frintezza.isBlocked())
			{
				world.frintezza.broadcastPacket(new ExShowScreenMessage(song_name, 3000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				world.frintezza.broadcastSkill(new MagicSkillUse(world.frintezza, world.frintezza, 5007, song, _intervalOfFrintezzaSongs, 0));
				ThreadPoolManager.getInstance().schedule(new SongEffectLaunched(getSongTargets(song, world_id), song, world_id, 10000), 10000);
			}
			ThreadPoolManager.getInstance().schedule(new Music(world_id), _intervalOfFrintezzaSongs + Rnd.get(10000));
		}

		/** Depending on the song, returns the song's targets (either mobs or players) */
		private static GArray<L2Character> getSongTargets(int songId, int world_id)
		{
			LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
			GArray<L2Character> targets = new GArray<L2Character>();
			if(songId < 4)
			{
				if(world.weakScarlet != null && !world.weakScarlet.isDead())
					targets.add(world.weakScarlet);
				if(world.strongScarlet != null && !world.strongScarlet.isDead())
					targets.add(world.strongScarlet);
				for(int i = 0; i < 4; i++)
				{
					if(world.portraits[i] != null && !world.portraits[i].isDead())
						targets.add(world.portraits[i]);
					if(world.demons[i] != null && !world.demons[i].isDead())
						targets.add(world.demons[i]);
				}
			}
			else
				for(L2Player pc : world.reflection.getPlayers())
					if(!pc.isDead())
						targets.add(pc);
			return targets;
		}

		/**
		 * returns the chosen symphony for Frintezza to play
		 * If the minions are injured he has 40% to play a healing song
		 * If they are all dead, he will only play harmful player symphonies
		 */
		private int getSong()
		{
			if(Rnd.chance(20))
				return 1;
			if(Rnd.chance(20))
				return 2;
			if(Rnd.chance(20))
				return 3;
			if(Rnd.chance(20))
				return 4;
			return 5;
		}
	}

	/** The song was played, this class checks it's affects (if any) */
	private static class SongEffectLaunched extends RunnableImpl
	{
		private GArray<L2Character> _targets;

		private int _song, _currentTime;

		private int world_id;

		/**
		 * @param targets - song's targets
		 * @param song - song id 1-5
		 * @param currentTimeOfSong - skills during music play are consecutive, repeating
		 * @param _p - player
		 */
		public SongEffectLaunched(GArray<L2Character> targets, int song, int _world_id, int currentTimeOfSong)
		{
			_targets = targets;
			_song = song;
			world_id = _world_id;
			_currentTime = currentTimeOfSong;
		}

		public void runImpl()
		{
			LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
			if(world == null || world.frintezza == null)
				return;
			// If the song time is over stop this loop
			if(_currentTime > _intervalOfFrintezzaSongs)
				return;
			// Skills are consecutive, so call them again
			SongEffectLaunched songLaunched = new SongEffectLaunched(_targets, _song, world_id, _currentTime + _intervalOfFrintezzaSongs / 10);
			ThreadPoolManager.getInstance().schedule(songLaunched, _intervalOfFrintezzaSongs / 10);
			world.frintezza.callSkill(SkillTable.getInstance().getInfo(5008, _song), _targets, false);
			if(world.weakScarlet != null)
				((Scarlet)world.weakScarlet.getAI()).callDash(_song);
			else if(world.strongScarlet != null)
				((Scarlet)world.strongScarlet.getAI()).callDash(_song);
		}
	}

	/**
	 * If the dead boss is a Portrait, we delete it from the world, and it's demon as well.
	 * If the dead boss is Scarlet or Frintezza, we do a bossesAreDead() check to see if both Frintezza and Scarlet are dead.
	 */
	public static void OnDie(L2Character self, L2Character killer)
	{
		if(self == null)
		{
			Log.add("Frintezza died, self==null killer="+killer.getName(), "frintezza");
			return;
		}
		else if(self.getNpcId() == _strongScarletId)
		{
			int world_id = self.getReflectionId();
			Reflection r = self.getReflection();
			if(!ConfigValue.DEBUG_FRINTEZZA)
			{
				if(LastImperialTombManager.getMembersCC().containsKey(world_id))
				{
					L2Player pl = killer.getPlayer();
					if(pl != null && pl.getAttainment() != null)
					{
						List<String> hwid = new ArrayList<String>();
						L2Party party = pl.getParty();
						if(party != null)
						{
							L2CommandChannel cc = party.getCommandChannel();
							if(cc != null)
							{
								for(L2Player member : cc.getMembers())
									if(member.getReflection() == r && member.getLevel() >= 85 && !hwid.contains(member.getHWIDs()))
									{
										member.getAttainment().setKillFrinteza();
										hwid.add(member.getHWIDs());
									}
							}
							else
							{
								for(L2Player member : party.getPartyMembers())
									if(member.getReflection() == r && member.getLevel() >= 85 && !hwid.contains(member.getHWIDs()))
									{
										member.getAttainment().setKillFrinteza();
										hwid.add(member.getHWIDs());
									}
							}
							hwid.clear();
						}
						else
							pl.getAttainment().setKillFrinteza();
					}
					try
					{
						String time = String.valueOf(System.currentTimeMillis());
						Log.add("Frintezza died0, set("+world_id+") reuse.--------------------- "+world_id, "frintezza");
						for(Integer objId : LastImperialTombManager.getMembersCC().get(world_id))
						{
							Log.add("Frintezza died1, set("+world_id+") reuse: "+objId, "frintezza");
							L2Player player = L2ObjectsStorage.getPlayer(objId);
							if(player != null)
							{
								player.setVarInst(r.getName(), time);
								if(QuestManager.getQuest(99995) != null)
								{
									String qn = QuestManager.getQuest(99995).getName();
									if(qn != null)
									{
										QuestState qs = player.getQuestState(qn);
										if(qs != null)
											qs.exitCurrentQuest(true);
									}
								}
							}
							else
								mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,-1)", objId, r.getName(), time);
						}
					}
					finally
					{
						LastImperialTombManager.getMembersCC().remove(world_id);
						r.clearReflection(10, false);
						if(killer != null)
							Log.add("Frintezza died2, self("+world_id+")="+self+" killer("+killer.getReflectionId()+")="+killer.getName(), "frintezza");
					}
				}
				else if(killer != null)
					Log.add("Frintezza died3, self("+world_id+")="+self+" killer("+killer.getReflectionId()+")="+killer.getName(), "frintezza");
			}
			LastImperialTombManager.World world = LastImperialTombManager.getWorld(self.getReflectionId());
			if(world == null)
				return;
			if(self == world.strongScarlet && world._dieTask == null)
				world._dieTask = ThreadPoolManager.getInstance().schedule(new Die(1, world_id), 50);
		}
	}

	/** Class<?>  respawns a demon if it's portrait is not dead. */
	private static class respawnDemons extends RunnableImpl
	{
		private int world_id;

		public respawnDemons(int _world_id)
		{
			world_id = _world_id;
		}
		public void runImpl()
		{
			LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
			boolean isAllDead = true;
			for(int i = 0; i < 4; i++)
				if(world.portraits[i] != null && !world.portraits[i].isDead())
				{
					isAllDead = false;
					if(world.demons[i] == null || world.demons[i].isDead())
					{
						world.demons[i] = spawn_fr(demonSpawns[i],world_id);
						L2Character target = getRandomPlayer(world_id);
						if(target != null)
							world.demons[i].getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 10000);
					}
				}
			if(!isAllDead)
				ThreadPoolManager.getInstance().schedule(new respawnDemons(world_id), FWF_INTERVALOFNEXTMONSTER);
		}
	}

	private static class ThirdMorph extends RunnableImpl
	{
		private int _taskId;
		private Location loc;
		private int world_id;

		public ThirdMorph(int taskId, int _world_id, Location loca)
		{
			world_id = _world_id;
			_taskId = taskId;
			loc = loca;
		}

		public void runImpl()
		{
			try
			{
				LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
				if(world == null)
					return;
				switch(_taskId)
				{
					case 1:
						blockAll(true, world_id);
						world.frintezza.broadcastSkill(new MagicSkillCanceled(world.frintezza.getObjectId()));
						world.frintezza.broadcastPacket(new SocialAction(world.frintezza.getObjectId(), 4));
						ThreadPoolManager.getInstance().schedule(new ThirdMorph(2,world_id, null), 100);
						break;
					case 2:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 250, 120, 15, 0, 1000, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 250, 120, 15, 0, 10000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new ThirdMorph(3,world_id, null), 7000);
						break;
					case 3:
						broadCastPacket(world_id, new MagicSkillUse(world.frintezza, world.frintezza, 5006, 1, 34000, 0));
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 500, 70, 15, 3000, 10000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new ThirdMorph(4,world_id, null), 3000);
						break;
					case 4:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 2500, 90, 12, 6000, 10000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new ThirdMorph(5,world_id, null), 3000);
						break;
					case 5:
						world.scarlet_x = world.weakScarlet.getX();
						world.scarlet_y = world.weakScarlet.getY();
						world.scarlet_z = world.weakScarlet.getZ();
						world.scarlet_h = world.weakScarlet.getHeading();
						if (world.scarlet_h < 32768)
						{
							world.scarlet_a = Math.abs(180 - (int) (world.scarlet_h / 182.044444444));
						}
						else
						{
							world.scarlet_a = Math.abs(540 - (int) (world.scarlet_h / 182.044444444));
						}
						broadCastPacket(world_id, new SpecialCamera(world.weakScarlet, 250, world.scarlet_a, 12, 0, 1000, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SpecialCamera(world.weakScarlet, 250, world.scarlet_a, 12, 0, 10000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new ThirdMorph(6,world_id, null), 500);
						break;
					case 6:
						loc = world.weakScarlet.getLoc();
						loc.setId(_strongScarletId);
						broadCastPacket(world_id, new SpecialCamera(world.weakScarlet, 450, world.scarlet_a, 14, 8000, 8000, 0, 0, 1, 0, 0));
						world.weakScarlet.doDie(world.weakScarlet);
						ThreadPoolManager.getInstance().schedule(new ThirdMorph(7,world_id, null), 6250);
						ThreadPoolManager.getInstance().schedule(new ThirdMorph(8,world_id, loc), 7200);
						break;
					case 7:
						world.weakScarlet.deleteMe();
						world.weakScarlet = null;
						break;
					case 8:
						world.strongScarlet = spawn_fr(loc, world_id);
						block(world.strongScarlet, true);
						broadCastPacket(world_id, new SpecialCamera(world.strongScarlet, 450, world.scarlet_a, 12, 500, 14000, 0, 0, 1, 0, 0));
						ThreadPoolManager.getInstance().schedule(new ThirdMorph(9,world_id, null), 8100);
						break;
					case 9:
						blockAll(false, world_id);
						for(L2Player pc : world.reflection.getPlayers())
							pc.leaveMovieMode();
						broadCastPacket(world_id, new SocialAction(world.strongScarlet.getObjectId(), 2));
						world.strongScarlet.MPCC_SetMasterPartyRouting(world.channel,1);
						L2Character target = getRandomPlayer(world_id);
						if(target != null)
						{
							world.strongScarlet.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 10000);
						}
						break;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class SecondMorph extends RunnableImpl
	{
		private int _taskId;
		private int world_id;

		public SecondMorph(int taskId, int _world_id)
		{
			_taskId = taskId;
			world_id = _world_id;
		}

		public void runImpl()
		{
			try
			{
				LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
				if(world == null)
					return;
				switch(_taskId)
				{
					case 1:
						blockAll(true, world_id);
						world.weakScarlet.broadcastPacket(new SocialAction(world.weakScarlet.getObjectId(), 1));
						world.weakScarlet.setCurrentHp(world.weakScarlet.getMaxHp() * 3 / 4, false);
						world.weakScarlet.setRHandId(_frintezzasSwordId);
						for(L2Player pc : world.reflection.getPlayers())
							pc.sendPacket(new NpcInfo(world.weakScarlet, pc));
						blockAll(false, world_id);
						L2Skill skill = SkillTable.getInstance().getInfo(5017, 1);
						world.weakScarlet.doCast(skill,world.weakScarlet,true);
						world.weakScarlet.i_ai4 = 0;
						break;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class doSkill extends RunnableImpl
	{
		private final L2Character _caster;
		private final int _interval, _range;

		public doSkill(L2Character caster, int interval, int range)
		{
			_caster = caster;
			_interval = interval;
			_range = range;
		}

		public void runImpl()
		{
			if(_caster == null || _caster.isDead())
				return;
			LastImperialTombManager.World world = LastImperialTombManager.getWorld(_caster.getReflectionId());
			try
			{
				L2Object tempTarget = _caster.getTarget();
				if(tempTarget == null || !(tempTarget instanceof L2Character))
					tempTarget = _caster;

				int x = tempTarget.getX() + Rnd.get(_range) - _range / 2, y = tempTarget.getY() + Rnd.get(_range) - _range / 2, z = tempTarget.getZ();
				if(_caster.getDistance(x, y) > _range && world._zonefrintezza.checkIfInZone(tempTarget))
				{
					_caster.broadcastSkill(new MagicSkillUse(_caster, (L2Character) tempTarget, 1086, 1, 0, 0));
					_caster.decayMe();
					_caster.setXYZ(x, y, z);
					_caster.spawnMe();
					_caster.setTarget(tempTarget);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			ThreadPoolManager.getInstance().schedule(new doSkill(_caster, _interval, _range), _interval + Rnd.get(500));
		}
	}

	private static class Die extends RunnableImpl
	{
		private int _taskId;
		private int world_id;

		public Die(int taskId, int _world_id)
		{
			_taskId = taskId;
			world_id = _world_id;
		}

		public void runImpl()
		{
			try
			{
				LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
				switch(_taskId)
				{
					case 1:
						blockAll(true, world_id);
						deletePortrait(world_id);
						broadCastPacket(world_id, new SpecialCamera(world.strongScarlet, 300, world.scarlet_a - 180, 5, 0, 7000, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SpecialCamera(world.strongScarlet, 200, world.scarlet_a, 85, 4000, 10000, 0, 0, 1, 0, 0));
						world._dieTask = ThreadPoolManager.getInstance().schedule(new Die(2,world_id), 7500);
						break;
					case 2:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 100, 120, 5, 0, 7000, 0, 0, 1, 0, 0));
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 100, 90, 5, 5000, 15000, 0, 0, 1, 0, 0));
						world._dieTask = ThreadPoolManager.getInstance().schedule(new Die(3,world_id), 6000);
						break;
					case 3:
						broadCastPacket(world_id, new SpecialCamera(world.frintezza, 900, 90, 25, 7000, 10000, 0, 0, 1, 0, 0));
						world.frintezza.doDie(world.frintezza);
						world.frintezza = null;
						world._dieTask = ThreadPoolManager.getInstance().schedule(new Die(4,world_id), 7000);
						break;
					case 4:
						for(L2Player pc : world.reflection.getPlayers())
							pc.leaveMovieMode();
						world._dieTask = ThreadPoolManager.getInstance().schedule(new Die(5,world_id), 600000);
						break;
					case 5:
						setUnspawn(world_id);
						break;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/** Class<?>  ends the activity of the Bosses after a interval of time Exits the battle field in any way... */
	private static class ActivityTimeEnd extends RunnableImpl
	{
		private int world_id;
		public ActivityTimeEnd(int _world_id)
		{
			world_id = _world_id;
		}

		public void runImpl()
		{
			setUnspawn(world_id);
		}
	}

	/** Clean Frintezza's lair. */
	public static void setUnspawn(int world_id)
	{
		LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
		banishForeigners(world_id);

		Log.add("Frintezza died", "bosses");

		if(world != null)
		{
			if(world.frintezza != null)
				world.frintezza.deleteMe();
			if(world.weakScarlet != null)
				world.weakScarlet.deleteMe();
			if(world.strongScarlet != null)
			{
				world.strongScarlet.MPCC_SetMasterPartyRouting(null, 0);
				world.channel = null;
				world.strongScarlet.deleteMe();
			}
			if(world.cube != null)
				world.cube.deleteMe();
			world.frintezza = null;
			world.weakScarlet = null;
			world.strongScarlet = null;
			world.cube = null;
		}

		deletePortrait(world_id);

		if(world != null)
		{
			if(world._monsterSpawnTask != null)
			{
				world._monsterSpawnTask.cancel(true);
				world._monsterSpawnTask = null;
			}
			if(world._intervalEndTask != null)
			{
				world._intervalEndTask.cancel(true);
				world._intervalEndTask = null;
			}
			if(world._activityTimeEndTask != null)
			{
				world._activityTimeEndTask.cancel(true);
				world._activityTimeEndTask = null;
			}
			if(world._dieTask != null)
			{
				world._dieTask.cancel(false);
				world._dieTask = null;
			}
		}
	}

	private static void deletePortrait(int world_id)
	{
		LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
		if(world != null)
			for(int i = 0; i < 4; i++)
			{
				if(world.portraits[i] != null)
				{
					world.portraits[i].deleteMe();
					world.portraits[i] = null;
				}
				if(world.demons[i] != null)
				{
					world.demons[i].deleteMe();
					world.demons[i] = null;
				}
			}
	}

	private static void blockAll(boolean flag, int world_id)
	{
		LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
		block(world.frintezza, flag);
		block(world.weakScarlet, flag);
		block(world.strongScarlet, flag);
		for(int i = 0; i < 4; i++)
		{
			block(world.portraits[i], flag);
			block(world.demons[i], flag);
		}
	}

	private static void block(L2NpcInstance npc, boolean flag)
	{
		if(npc == null || npc.isDead())
			return;
		if(flag)
		{
			npc.abortAttack(true, false);
			npc.abortCast(true);
			npc.setTarget(null);
			if(npc.isMoving)
				npc.stopMove();
			npc.block();
		}
		else
			npc.unblock();
		npc.setIsInvul(flag);
	}

	private static void banishForeigners(int world_id)
	{
		LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
		if(world != null)
			for(L2Player player : world.reflection.getPlayers())
				if(player != null && !player.isGM())
					player.teleToClosestTown();
	}

	private static L2Player getRandomPlayer(int world_id)
	{
		LastImperialTombManager.World world = LastImperialTombManager.getWorld(world_id);
		List<L2Player> list = world.reflection.getPlayers();
		if(list.isEmpty())
			return null;
		return list.get(Rnd.get(list.size()));
	}

	public void onLoad()
	{}

	public void onReload()
	{
		for(LastImperialTombManager.World world	: LastImperialTombManager.getAllWorld())
		{
			if(world.weakScarlet != null)
				world.weakScarlet.getListenerEngine().removePropertyChangeListener(_currentHpListener);
		}
	}

	public void onShutdown()
	{}
}