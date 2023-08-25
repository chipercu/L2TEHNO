package events.GvG;

import events.GvG.GvG;
import gnu.trove.TIntObjectHashMap;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.mutable.MutableInt;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;
import l2open.util.reference.*;

/**
 * Инстанс для GvG турнира
 *
 * @author pchayka
 */
public class GvGInstance extends Reflection
{
	private final static int BOX_ID = 18822; // <set name="ai_type" value="custom.MutantChest" />
	private final static int BOSS_ID = 25655; // <set name="ai_type" value="custom.GvGBoss" />

	private final static int SCORE_BOX = 20;
	private final static int SCORE_BOSS = 100;
	private final static int SCORE_KILL = 5;
	private final static int SCORE_DEATH = 3;

	private int eventTime = 600;
	private long bossSpawnTime = 10 * 60 * 1000L;

	private boolean active = false;

	private L2Party team1;
	private L2Party team2;
	private List<HardReference<L2Player>> bothTeams = new CopyOnWriteArrayList<HardReference<L2Player>>();

	private TIntObjectHashMap<MutableInt> score = new TIntObjectHashMap<MutableInt>();
	private int team1Score = 0;
	private int team2Score = 0;

	private long startTime;

	private ScheduledFuture<?> _bossSpawnTask;
	private ScheduledFuture<?> _countDownTask;
	private ScheduledFuture<?> _battleEndTask;

	private L2Zone zonebattle;
	private L2Zone zonepvp;

	private L2Zone zonepeace1;
	private L2Zone peace1;

	private L2Zone zonepeace2;
	private L2Zone peace2;

	public void setTeam1(L2Party party1)
	{
		team1 = party1;
	}

	public void setTeam2(L2Party party2)
	{
		team2 = party2;
	}

	public GvGInstance(InstancedZone iz)
	{
		super(iz);
	}

	/**
	 * General instance initialization and assigning global variables
	 */
	public void start()
	{
		zonepvp = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.battle_zone, 21740001);//getZone("[gvg_battle_zone]");
		peace1 = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.peace_zone, 520001);//getZone("[gvg_1_peace]");
		peace2 = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.peace_zone, 520002);//getZone("[gvg_2_peace]");
		//Box spawns
		Location boxes[] = {
				new Location(142696, 139704, -15264, 0),
				new Location(142696, 145944, -15264, 0),
				new Location(145784, 142824, -15264, 0),
				new Location(145768, 139704, -15264, 0),
				new Location(145768, 145944, -15264, 0),
				new Location(141752, 142760, -15624, 0),
				new Location(145720, 142008, -15880, 0),
				new Location(145720, 143640, -15880, 0),
				new Location(139592, 142824, -15264, 0)
		};

		for(int i = 0; i < boxes.length; i++)
			CreateOnePrivateEx(BOX_ID, boxes[i], "custom.MutantChest", "L2Monster");

		CreateOnePrivateEx(35423, new Location(139640, 139736, -15264), "npc", "L2Npc"); //Red team flag
		CreateOnePrivateEx(35426, new Location(139672, 145896, -15264), "npc", "L2Npc"); //Blue team flag

		_bossSpawnTask = ThreadPoolManager.getInstance().schedule(new BossSpawn(), bossSpawnTime);
		_countDownTask = ThreadPoolManager.getInstance().schedule(new CountingDown(), (eventTime - 1) * 1000L);
		_battleEndTask = ThreadPoolManager.getInstance().schedule(new BattleEnd(), (eventTime - 6) * 1000L); // -6 is about to prevent built-in BlockChecker countdown task

		//Assigning players to teams
		for(L2Player member : team1.getPartyMembers())
			bothTeams.add(member.getRef());

		for(L2Player member : team2.getPartyMembers())
			bothTeams.add(member.getRef());

		startTime = System.currentTimeMillis() + eventTime * 1000L; //Used in packet broadcasting

		//Forming packets to send everybody
		final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(eventTime, team1Score, team2Score);
		final ExCubeGameCloseUI cui = new ExCubeGameCloseUI();
		ExCubeGameExtendedChangePoints clientSetUp;

		for(L2Player tm : HardReferences.unwrap(bothTeams))
		{
			score.put(tm.getObjectId(), new MutableInt());

			tm.setCurrentCp(tm.getMaxCp());
			tm.setCurrentHp(tm.getMaxHp(), false);
			tm.setCurrentMp(tm.getMaxMp());
			clientSetUp = new ExCubeGameExtendedChangePoints(eventTime, team1Score, team2Score, isRedTeam(tm), tm, 0);
			tm.sendPacket(clientSetUp);
			tm.sendActionFailed(); //useless? copy&past from BlockChecker
			tm.sendPacket(initialPoints);
			tm.sendPacket(cui); //useless? copy&past from BlockChecker
			broadCastPacketToBothTeams(new ExCubeGameAddPlayer(tm, isRedTeam(tm)));
		}

		active = true;
	}

	/**
	 * @param packet Broadcasting packet to every member of instance
	 */
	private void broadCastPacketToBothTeams(L2GameServerPacket packet)
	{
		for(L2Player tm : HardReferences.unwrap(bothTeams))
			tm.sendPacket(packet);
	}

	/**
	 * @return Whether event is active. active starts with instance dungeon and ends with team victory
	 */
	private boolean isActive()
	{
		return active;
	}

	/**
	 * @param player
	 * @return Whether player belongs to Red Team (team2)
	 */
	private boolean isRedTeam(L2Player player)
	{
		if(team2.containsMember(player))
			return true;
		return false;
	}

	private void deleteArena(long time)
	{
		GvGInstance[] inst = { this };
		GvG.executeTask("events.GvG.GvG", "deleteArena", inst, time);
	}

	/**
	 * Handles the end of event
	 */
	private void end()
	{
		if(active)
		{
			if(_battleEndTask != null)
			{
				_battleEndTask.cancel(false);
				_battleEndTask = null;
			}

			deleteArena(60000);
			active = false;

			startCollapseTimer(60 * 1000L);

			paralyzePlayers();
			ThreadPoolManager.getInstance().schedule(new Finish(), 55 * 1000L);

			if(_bossSpawnTask != null)
			{
				_bossSpawnTask.cancel(false);
				_bossSpawnTask = null;
			}
			if(_countDownTask != null)
			{
				_countDownTask.cancel(false);
				_countDownTask = null;
			}

			boolean isRedWinner = false;

			isRedWinner = getRedScore() >= getBlueScore();

			final ExCubeGameEnd end = new ExCubeGameEnd(isRedWinner);
			broadCastPacketToBothTeams(end);

			reward(isRedWinner ? team2 : team1);
			GvG.updateWinner(isRedWinner ? team2.getPartyLeader() : team1.getPartyLeader());

			//Удаление созданных зон из мира
			zonepvp.setActive(false);
			peace1.setActive(false);
			peace2.setActive(false);

			GvG.startTimerTask();
		}
	}

	private void reward(L2Party party)
	{
		for(L2Player member : party.getPartyMembers())
		{
			member.sendMessage("Ваша группа выиграла GvG турнир, лидер группы добавлен в рейтинг победителей.");
			if(ConfigValue.GvG_AddFame > 0)
				member.setFame(member.getFame() + ConfigValue.GvG_AddFame, "GvG"); // fame
			Functions.addItem(member, ConfigValue.GvG_ItemReward, ConfigValue.GvG_ItemRewardCount); // Fantasy Isle Coin
		}
	}

	public void OnDie(L2Character self, L2Character killer)
	{
		if(!isActive())
			return;

		//Убийство произошло в инстанте
		if(self.getReflection() != killer.getReflection() || self.getReflection() != this)
		{
			System.out.println("OnDie ololosh... self.getReflection() != this:"+(self.getReflection() != this)+"  self.getReflection() != killer.getReflection():"+(self.getReflection() != killer.getReflection()));
			return;
		}

		if(self.isPlayer() && killer.isPlayable()) //if PvP kill
		{
			if(team1.containsMember(self.getPlayer()) && team2.containsMember(killer.getPlayer()))
			{
				addPlayerScore(killer.getPlayer());
				changeScore(1, SCORE_KILL, SCORE_DEATH, true, true, killer.getPlayer());
			}
			else if(team2.containsMember(self.getPlayer()) && team1.containsMember(killer.getPlayer()))
			{
				addPlayerScore(killer.getPlayer());
				changeScore(2, SCORE_KILL, SCORE_DEATH, true, true, killer.getPlayer());
			}
			resurrectAtBase(self.getPlayer());
		}
		else if(self.isPlayer() && !killer.isPlayable()) //if not-PvP kill
			resurrectAtBase(self.getPlayer());
		else if(self.isNpc() && killer.isPlayable()) //onKill - mob death
		{
			if(self.getNpcId() == BOX_ID)
			{
				if(team1.containsMember(killer.getPlayer()))
					changeScore(1, SCORE_BOX, 0, false, false, killer.getPlayer());
				else if(team2.containsMember(killer.getPlayer()))
					changeScore(2, SCORE_BOX, 0, false, false, killer.getPlayer());
			}
			else if(self.getNpcId() == BOSS_ID)
			{
				if(team1.containsMember(killer.getPlayer()))
					changeScore(1, SCORE_BOSS, 0, false, false, killer.getPlayer());
				else if(team2.containsMember(killer.getPlayer()))
					changeScore(2, SCORE_BOSS, 0, false, false, killer.getPlayer());

				broadCastPacketToBothTeams(new ExShowScreenMessage("Охранник Сокровищ Геральда погиб от руки " + killer.getName(), 5000, ScreenMessageAlign.MIDDLE_CENTER, true));
				end();
			}
		}
	}

	/**
	 * @param teamId
	 * @param toAdd			 - how much points to add
	 * @param toSub			 - how much points to remove
	 * @param subbing		   - whether change is reducing points
	 * @param affectAnotherTeam - change can affect only teamId or both
	 * @param player			Any score change are handled here.
	 */
	private synchronized void changeScore(int teamId, int toAdd, int toSub, boolean subbing, boolean affectAnotherTeam, L2Player player)
	{
		int timeLeft = (int) ((startTime - System.currentTimeMillis()) / 1000);
		if(teamId == 1)
		{
			if(subbing)
			{
				team1Score -= toSub;
				if(team1Score < 0)
					team1Score = 0;
				if(affectAnotherTeam)
				{
					team2Score += toAdd;
					broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
				}
				broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
			}
			else
			{
				team1Score += toAdd;
				if(affectAnotherTeam)
				{
					team2Score -= toSub;
					if(team2Score < 0)
						team2Score = 0;
					broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
				}
				broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
			}
		}
		else if(teamId == 2)
			if(subbing)
			{
				team2Score -= toSub;
				if(team2Score < 0)
					team2Score = 0;
				if(affectAnotherTeam)
				{
					team1Score += toAdd;
					broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
				}
				broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
			}
			else
			{
				team2Score += toAdd;
				if(affectAnotherTeam)
				{
					team1Score -= toSub;
					if(team1Score < 0)
						team1Score = 0;
					broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, false, player, getPlayerScore(player)));
				}
				broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, team1Score, team2Score, true, player, getPlayerScore(player)));
			}
	}

	/**
	 * @param player Handles the increase of personal player points
	 */
	private void addPlayerScore(L2Player player)
	{
		MutableInt points = score.get(player.getObjectId());
		points.increment();
	}

	/**
	 * @param player
	 * @return Returns personal player score
	 */
	public int getPlayerScore(L2Player player)
	{
		MutableInt points = score.get(player.getObjectId());
		return points.intValue();
	}

	/**
	 * Paralyzes everybody in instance to prevent any actions while event is !isActive
	 */
	public void paralyzePlayers()
	{
		for(L2Player tm : HardReferences.unwrap(bothTeams))
		{
			if(tm.isDead())
			{
				tm.setCurrentHp(tm.getMaxHp(), true);
				tm.broadcastPacket(new Revive(tm));
			}
			else
				tm.setCurrentHp(tm.getMaxHp(), false);

			tm.setCurrentMp(tm.getMaxMp());
			tm.setCurrentCp(tm.getMaxCp());

			tm.getEffectList().stopEffect(L2Skill.SKILL_MYSTIC_IMMUNITY);
			tm.getEffectList().stopEffect(1540);
			tm.getEffectList().stopEffect(396);
			tm.getEffectList().stopEffect(914);
			tm.block();
		}
	}

	/**
	 * Romoves paralization
	 */
	public void unParalyzePlayers()
	{
		for(L2Player tm : HardReferences.unwrap(bothTeams))
		{
			tm.unblock();
			tm.leaveParty();
			removePlayer(tm, true);
			tm.unsetVar("reflection");
			tm.unsetVar("backCoords");
		}
	}

	/**
	 * Cleans up every list and task
	 */
	private void cleanUp()
	{
		team1 = null;
		team2 = null;
		bothTeams.clear();
		team1Score = 0;
		team2Score = 0;
		score.clear();
	}

	/**
	 * @param player
	 * @param refId  Called by onDeath. Handles the resurrection at the proper base.
	 */
	public void resurrectAtBase(L2Player player)
	{
		ThreadPoolManager.getInstance().schedule(new ResurectPlayer(player.getRef()), 10000);
		/*if(player.isDead())
		{
			//player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(0.7 * player.getMaxHp(), true);
			//player.setCurrentMp(player.getMaxMp());
			player.broadcastPacket(new Revive(player));
		}
		player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(5660, 2)); // Battlefield Death Syndrome

		Location pos;
		if(team1.containsMember(player))
			pos = Location.findPointToStay(GvG.TEAM1_LOC, 0, 150, getGeoIndex());
		else
			pos = Location.findPointToStay(GvG.TEAM2_LOC, 0, 150, getGeoIndex());

		player.setReflection(this);
		player.teleToLocation(pos);*/
	}

	/**
	 * @param player
	 * @param legalQuit - whether quit was called by event or by player escape
	 *                  Removes player from every list or instance, teleports him and stops the event timer
	 */
	private void removePlayer(L2Player player, boolean legalQuit)
	{
		bothTeams.remove(player.getRef());

		broadCastPacketToBothTeams(new ExCubeGameRemovePlayer(player, isRedTeam(player)));
		if(!legalQuit)
			player.sendPacket(new ExCubeGameEnd(false));
		player.teleToLocation(Location.findPointToStay(GvG.RETURN_LOC, 0, 150, 0), 0);
		player.setTeam(0, true);
		player.setRestartPoint(true);
	}

	/**
	 * @param isRed Handles the team withdraw from the area of event. Can only be called when !isActive
	 */
	private void teamWithdraw(L2Party party)
	{
		if(party == team1)
		{
			for(L2Player player : team1.getPartyMembers())
				removePlayer(player, false);

			L2Player player = team2.getPartyLeader();
			changeScore(2, 200, 0, false, false, player); //adding 200 to the team score for enemy team withdrawal. player - leader of the team who's left in the instance
		}
		else
		{
			for(L2Player player : team2.getPartyMembers())
				removePlayer(player, false);

			L2Player player = team1.getPartyLeader();
			changeScore(1, 200, 0, false, false, player); //adding 200 to the team score for enemy team withdrawal. player - leader of the team who's left in the instance
		}

		broadCastPacketToBothTeams(new ExShowScreenMessage("Команда соперника покинула поле боя в полном составе. Конец сражения.", 4000, ScreenMessageAlign.MIDDLE_CENTER, true));
		end();
	}

	private int getBlueScore()
	{
		return team1Score;
	}

	private int getRedScore()
	{
		return team2Score;
	}

	public class BossSpawn extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			broadCastPacketToBothTeams(new ExShowScreenMessage("Появился Охранник Сокровищ Геральда", 5000, ScreenMessageAlign.MIDDLE_CENTER, true));
			CreateOnePrivateEx(BOSS_ID, new Location(147304, 142824, -15864, 32768), "custom.GvGBoss", "GvGBoss");
			openDoor(24220042);
		}
	}

	public class CountingDown extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			broadCastPacketToBothTeams(new ExShowScreenMessage("До конца сражения осталась 1 минута", 4000, ScreenMessageAlign.MIDDLE_CENTER, true));
		}
	}

	public class BattleEnd extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			broadCastPacketToBothTeams(new ExShowScreenMessage("Время битвы истекло. Телепортация через 1 минуту.", 4000, ScreenMessageAlign.BOTTOM_RIGHT, true));
			end();
		}
	}

	public class Finish extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			unParalyzePlayers();
			cleanUp();
		}
	}

	/**
	 * Handles any Teleport action of any player inside
	 **/
	public void onPlayerTeleport(L2Character player, Location loc)
	{
		if(!isActive())
			return;
		if(zonepvp.checkIfInZone(loc.x, loc.y, loc.z) || peace1.checkIfInZone(loc.x, loc.y, loc.z) || peace2.checkIfInZone(loc.x, loc.y, loc.z))
			return;
		player.getPlayer().leaveParty();
		removePlayer(player.getPlayer(), false);
		player.sendMessage("Вы досрочно покинули зону битвы и были дисквалифицированы.");
	}

	@Override
	public void dissolveParty(L2Party party)
	{
		//teamWithdraw(party);
	}

	@Override
	public void oustPartyMember(L2Party party, L2Player player)
	{
		if(!isActive())
			return;

		if(party.getMemberCount() >= 2)
		{
			removePlayer(player, false);
			return;
		}

		teamWithdraw(party);
	}

	private static class ResurectPlayer implements Runnable
	{
		private HardReference<L2Player> _player;

		public ResurectPlayer(HardReference<L2Player> p)
		{
			_player = p;
		}

		@Override
		public void run()
		{
			L2Player player = _player.get();
			if(player == null)
				return;

			if(player.isDead())
			{
				//player.setCurrentCp(player.getMaxCp());
				player.setCurrentHp(0.7 * player.getMaxHp(), true);
				//player.setCurrentMp(player.getMaxMp());
				player.broadcastPacket(new Revive(player));
			}
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(5660, 2)); // Battlefield Death Syndrome

			Location pos;
			if(player.getTeam() == 1)
				pos = Location.findPointToStay(GvG.TEAM1_LOC, 0, 150, player.getGeoIndex());
			else
				pos = Location.findPointToStay(GvG.TEAM2_LOC, 0, 150, player.getGeoIndex());

			player.teleToLocation(pos);
		}
	}
}