package l2open.gameserver.model.entity.olympiad;

import javolution.util.FastMap;
import l2open.config.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.instancemanager.OlympiadHistoryManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2OlympiadManagerInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.EffectsComparator;
import l2open.util.GArray;
import l2open.util.GCSArray;
import l2open.util.Log;

import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class OlympiadGame
{
	private static final Logger _log = Logger.getLogger(OlympiadGame.class .getName());
	public static final int MAX_POINTS_LOOSE = ConfigValue.MaxPointLoose;
	public boolean validated = false;

	public int _winner = -1;
	private int _state = 0;
	private int _OllyID;
	private int _id;
	private Reflection _inst;
	private CompType _type;
	private OlympiadTeam _team1;
	private OlympiadTeam _team2;
	private GCSArray<L2Player> _spectators = new GCSArray<L2Player>();
	private GArray<L2Spawn> _buffers;
	OlympiadGameTask _task;
	ScheduledFuture<?> _shedule;
	private long _startTime;

	public OlympiadGame(int id, int OllyID, CompType type, GCSArray<Integer> opponents)
	{
		_type = type;
		_id = id;
		_OllyID = OllyID;

		int id_s = ConfigValue.OlympiadStadiums[OllyID];

		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(id_s+146);
		if(izs == null)
			return;

		_inst = new Reflection(izs.get(0).getName());

		_inst.setInstancedZoneId(id_s+146);

		for(InstancedZone i : izs.values())
			_inst.FillDoors(i.getDoors());

		_team1 = new OlympiadTeam(this, 1);
		_team2 = new OlympiadTeam(this, 2);

		for (int i = 0; i < opponents.size() / 2; i++)
			_team1.addMember(opponents.get(i));
		for (int i = opponents.size() / 2; i < opponents.size(); i++)
			_team2.addMember(opponents.get(i));
		Log.add("Olympiad System: Game - " + id + ": " + _team1.getName() + " Vs " + _team2.getName(), "olympiad");
	}

	public void addBuffers()
	{
		if(!_type.hasBuffer())
			return;

		L2Zone zone = Olympiad.STADIUMS[_OllyID].getZone();
		if(zone == null || zone.getSpawns() == null || zone.getSpawns().size() == 0)
		{
			_log.warning("Olympiad zone or spawns is null!!!");
			return;
		}
		
		

		_buffers = new GArray<L2Spawn>();

		for(int i = 0;i<2;i++)
			try
			{
				int[] loc = zone.getSpawns().get(i);
				L2NpcTemplate template = NpcTable.getTemplate(36402); // Olympiad Host
				//TODO исправить координаты и heading
				L2Spawn buffer = new L2Spawn(template);
				buffer.setLocx(loc[0]);
				buffer.setLocy(loc[1]);
				buffer.setReflection(_inst.getId());
				_inst.addSpawn(buffer);
				buffer.setLocz(loc[2]);
				buffer.setRespawnDelay(10);
				buffer.spawnOne();
				_buffers.add(buffer);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	}

	public void deleteBuffers()
	{
		if(_buffers == null)
			return;
		for(L2Spawn spawn : _buffers)
			spawn.despawnAll();
		_buffers.clear();
		_buffers = null;
	}

	public void managerShout()
	{
		try
		{
			for(L2OlympiadManagerInstance npc : Olympiad.getNpcs())
			{
				if(_type == CompType.CLASSED)
					Functions.npcShout(npc, Say2C.NPC_SHOUT, 1300167, String.valueOf(_id + 1));
				else if(_type == CompType.TEAM || _type == CompType.TEAM_RANDOM)
					Functions.npcShout(npc, Say2C.NPC_SHOUT, 1300132, String.valueOf(_id + 1));
				else
					Functions.npcShout(npc, Say2C.NPC_SHOUT, 1300166, String.valueOf(_id + 1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void portPlayersToArena()
	{
		_team1.portPlayersToArena();
		_team2.portPlayersToArena();
	}

	public void sendRelation()
	{
		_team1.sendRelation();
		_team2.sendRelation();
	}

	public void preparePlayers()
	{
		_team1.preparePlayers();
		_team2.preparePlayers();
	}

	public void regenPlayers()
	{
		_team1.regenPlayers();
		_team2.regenPlayers();
	}

	public void portPlayersBack()
	{
		_team1.portPlayersBack();
		_team2.portPlayersBack();
		_inst.collapse();
	}

	public void validateWinner(boolean aborted, L2Player player) throws Exception
	{
		int state = _state;
		_state = 0;

		broadcastPacket(new ExOlympiadMatchEnd(), true, false);
		if(validated)
		{
			Log.add("Olympiad Result: " + _team1.getName() + " vs " + _team2.getName() + " ... double validate check!!!", "olympiad");
			l2open.util.Util.test("Olympiad Result: " + _team1.getName() + " vs " + _team2.getName() + " ... double validate check!!!: ", "olympiad", "olympiad");
			return;
		}
		validated = true;

		if(state < (1 + ConfigValue.OlympiadGivePointToCrash) && aborted)
		{
			if(player != null)
			{
				if(_team1.contains(player.getObjectId()))
					_team1.takePointsForCrash(true);
				else
					_team2.takePointsForCrash(true);
			}
			/*else
			{
				_team1.takePointsForCrash(false);
				_team2.takePointsForCrash(false);
			}*/
			broadcastPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME, true, false);
			return;
		}

		boolean teamOneCheck = _team1.checkPlayers();
		boolean teamTwoCheck = _team2.checkPlayers();

		if(_winner <= 0)
		{
			if(!teamOneCheck && !teamTwoCheck)
				_winner = 0;
			else if(!teamTwoCheck)
				_winner = 1;
			else if(!teamOneCheck)
				_winner = 2;
			else if(_team1.getAllDamage() < _team2.getAllDamage())
				_winner = 1;
			else if(_team1.getAllDamage() > _team2.getAllDamage())
				_winner = 2;
		}
		if(aborted && player != null)
		{
			if(player.getOlympiadSide() == 2)
				_winner = 1;
			else if(player.getOlympiadSide() == 1)
				_winner = 2;
			broadcastPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME, _winner);
		}
		if(_winner == 1)
			winGame(_team1, _team2);
		else if(_winner == 2)
			winGame(_team2, _team1);
		else 
			tie();
		try
		{
			if(_winner == 1)
			{
				notifyForQuest(_team1, _team2, true);
				_team1.winGame(_team2);
			}
			else if(_winner == 2)
			{
				notifyForQuest(_team2, _team1, true);
				_team2.winGame(_team1);
			}
			else
			{
				if(ConfigValue.EnableOlyTotalizator)
					Functions.callScripts("communityboard.manager.OlyTotalizator", "endBattle", new Object[] {null, null});
				notifyForQuest(_team1, _team2, false);
				_team1.tie(_team2);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		_team1.saveNobleData();
		_team2.saveNobleData();
		_team1.stopEffect();
		_team2.stopEffect();
		broadcastPacket(new SystemMessage(SystemMessage.YOU_WILL_GO_BACK_TO_THE_VILLAGE_IN_S1_SECOND_S).addNumber(20), true, true);
	}

	public void openDoors() throws Exception
	{
		if(_inst == null)
		{
			_log.warning("Olympiad Instance is Null");
			return;
		}

		if(_inst.getDoors() == null)
		{
			_log.warning("Olympiad Instance Doors is Null");
			return;
		}

		for(L2DoorInstance door : _inst.getDoors())
			if(door != null)
				door.openMe();
			else
				_log.warning("Olympiad door is Null");
	}

	public int getId()
	{
		return _id;
	}

	public int getOllyId()
	{
		return _OllyID;
	}

	public String getTitle()
	{
		return _team1.getName() + " vs " + _team2.getName();
	}

	public String getTeam1Title()
	{
		String title = "";
		for(TeamMember member : _team1.getMembers())
			title += (title.isEmpty() ? "" : ", ") + member.getName();
		return "<font color=\"00FF00\">" + title + "</font>";
	}

	public String getTeam2Title()
	{
		String title = "";
		for(TeamMember member : _team2.getMembers())
			title += (title.isEmpty() ? "" : ", ") + member.getName();
		return "<font color=\"FF0000\">" + title + "</font>";
	}

	public boolean isRegistered(int objId)
	{
		return _team1.contains(objId) || _team2.contains(objId);
	}

	public GCSArray<L2Player> getSpectators()
	{
		return _spectators;
	}

	public boolean containsSpectator(L2Player player)
	{
		for(L2Player spectator : _spectators)
			if(player.equals(spectator))
				return true;
		return false;
	}

	public void addSpectator(L2Player spec)
	{
		_spectators.add(spec);
	}

	public void removeSpectator(L2Player spec)
	{
		_spectators.remove(spec);
	}

	public void clearSpectators()
	{
		for(L2Player pc : _spectators)
			if(pc != null && pc.inObserverMode())
				pc.leaveObserverMode(this);
		_spectators.clear();
	}

	public void broadcastInfo(L2Player sender, L2Player receiver, boolean onlyToSpectators)
	{
		if(sender != null)
		{
			if(receiver != null)
				receiver.sendPacket(new ExOlympiadUserInfo(sender, sender.getOlympiadSide()));
			else
				broadcastPacket(new ExOlympiadUserInfo(sender, sender.getOlympiadSide()), !onlyToSpectators, true);
		}
		else
		{
			for(L2Player player : _team1.getPlayers())
				if(receiver != null)
					receiver.sendPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()));
				else
					broadcastPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()), !onlyToSpectators, true);
			for(L2Player player : _team2.getPlayers())
				if(receiver != null)
					receiver.sendPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()));
				else
					broadcastPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()), !onlyToSpectators, true);
		}
	}

	public void broadcastPacket(L2GameServerPacket packet, int toTeams)
	{
		if(toTeams == 1)
			_team1.broadcast(packet);
		else
			_team2.broadcast(packet);
	}

	public void broadcastPacket(L2GameServerPacket packet, boolean toTeams, boolean toSpectators)
	{
		if(toTeams)
		{
			_team1.broadcast(packet);
			_team2.broadcast(packet);
		}

		if(toSpectators && _spectators != null)
			for(L2Player spec : _spectators)
				if(spec != null)
					spec.sendPacket(packet);
	}

	public void updateEffectIcons()
	{
		for(L2Player member1 : _team1.getPlayers())
		{
			L2Effect[] effects = member1.getEffectList().getAllFirstEffects();
			Arrays.sort(effects, EffectsComparator.getInstance());

			ExOlympiadSpelledInfo OlympiadSpelledInfo = new ExOlympiadSpelledInfo();

			for(L2Effect effect : effects)
				if(effect != null && effect.isInUse())
					effect.addOlympiadSpelledIcon(member1, OlympiadSpelledInfo);

			for(L2Player member : getSpectators())
				member.sendPacket(OlympiadSpelledInfo);
		}

		for(L2Player member2 : _team2.getPlayers())
		{
			L2Effect[] effects = member2.getEffectList().getAllFirstEffects();
			Arrays.sort(effects, EffectsComparator.getInstance());

			ExOlympiadSpelledInfo OlympiadSpelledInfo = new ExOlympiadSpelledInfo();

			for(L2Effect effect : effects)
				if(effect != null && effect.isInUse())
					effect.addOlympiadSpelledIcon(member2, OlympiadSpelledInfo);

			for(L2Player member : getSpectators())
				member.sendPacket(OlympiadSpelledInfo);
		}
	}

	public void setWinner(int val)
	{
		_winner = val;
	}

	public void setState(int val)
	{
		_state = val;
		if(_state == 1)
			_startTime = System.currentTimeMillis();
	}

	public int getState()
	{
		return _state;
	}

	public GArray<L2Player> getTeamMembers(L2Player player)
	{
		return player.getOlympiadSide() == 1 ? _team1.getPlayers() : _team2.getPlayers();
	}

	public boolean doDie(L2Player player)
	{
		return player.getOlympiadSide() == 1 ? _team1.doDie(player) : _team2.doDie(player);
	}

	// возвращать бракованого игрока...
	public boolean checkPlayersOnline()
	{
		return _team1.checkPlayers() && _team2.checkPlayers();
	}

	public synchronized void sheduleTask(OlympiadGameTask task)
	{
		if(_shedule != null)
			_shedule.cancel(false);
		_task = task;
		_shedule = task.shedule();
	}

	public OlympiadGameTask getTask()
	{
		return _task;
	}

	public BattleStatus getStatus()
	{
		if(_task != null)
			return _task.getStatus();
		return BattleStatus.Begining;
	}

	public void endGame(long time, boolean aborted, L2Player player)
	{
		try
		{
			validateWinner(aborted, player);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		sheduleTask(new OlympiadGameTask(this, BattleStatus.Ending, 0, time));
	}

	public OlympiadTeam getTeam(int Team)
	{
		return Team == 1 ? _team1 : _team2;
	}

	public CompType getType()
	{
		return _type;
	}

	public Reflection getReflect()
	{
		return _inst;
	}

	public void notifyForQuest(OlympiadTeam win, OlympiadTeam loss, boolean haveWin)
	{
		sendNotifyQuest(win.getPlayers(), win, loss, haveWin);
		sendNotifyQuest(loss.getPlayers(), win, loss, haveWin);
	}

	public void sendNotifyQuest(GArray<L2Player> players, OlympiadTeam win, OlympiadTeam loss, boolean haveWin)
	{
		if(!Olympiad.isFakeOly())
			for(L2Player player : players)
			{
				QuestState qs = player.getQuestState("_551_OlympiadStarter");
				if(qs != null && qs.isStarted())
					qs.endOlympiad(win.getPlayers(), loss.getPlayers(), haveWin, _type);
				qs = player.getQuestState("_552_OlympiadVeteran");
				if(qs != null && qs.isStarted())
					qs.endOlympiad(win.getPlayers(), loss.getPlayers(), haveWin, _type);
				qs = player.getQuestState("_553_OlympiadUndefeated");
				if(qs != null && qs.isStarted())
					qs.endOlympiad(win.getPlayers(), loss.getPlayers(), haveWin, _type);
			}
	}

	public void winGame(OlympiadTeam winnerTeam, OlympiadTeam looseTeam)
	{
		if(_type != CompType.TEAM)
		{
			int team = (_team1 == winnerTeam) ? 1 : 2;
			TeamMember[] looserMembers = looseTeam.getMembers().toArray(new TeamMember[looseTeam.getMembers().size()]);
			TeamMember[] winnerMembers = winnerTeam.getMembers().toArray(new TeamMember[winnerTeam.getMembers().size()]);

			TeamMember member1 = valid(_team1 == winnerTeam ? winnerMembers : looserMembers, 0);
			TeamMember member2 = valid(_team2 == winnerTeam ? winnerMembers : looserMembers, 0);
			if(member1 != null && member2 != null)
			{
				//TODO: FUZZY награда для проигравших
				if (ConfigValue.EnableRewardForLoser){
					for (TeamMember l:looserMembers){
						final L2ItemInstance item = l.getPlayer().getInventory().addItem(ConfigValue.RewardIdForLoser, ConfigValue.RewardCountForLoser);
						l.getPlayer().sendMessage("Вы получили " + item.getName() + " " + ConfigValue.RewardCountForLoser + " шт.");

					}
				}
				//TODO FUZZY

				int diff = (int)((System.currentTimeMillis() - _startTime) / 1000);
				OlympiadHistory h = new OlympiadHistory(member1.getObjId(), member2.getObjId(), member1.getClassId(), member2.getClassId(), member1.getName(), member2.getName(), _startTime, diff, team, _type.ordinal());

				OlympiadHistoryManager.getInstance().saveHistory(h);
			}
		}
	}

	public void tie()
	{
		TeamMember[] teamMembers1 = _team1.getMembers().toArray(new TeamMember[_team1.getMembers().size()]);
		TeamMember[] teamMembers2 = _team2.getMembers().toArray(new TeamMember[_team2.getMembers().size()]);

		if(_type != CompType.TEAM)
		{
			TeamMember member1 = valid(teamMembers1, 0);
			TeamMember member2 = valid(teamMembers2, 0);
			if(member1 != null && member2 != null)
			{
				int diff = (int)((System.currentTimeMillis() - _startTime) / 1000);
				OlympiadHistory h = new OlympiadHistory(member1.getObjId(), member2.getObjId(), member1.getClassId(), member2.getClassId(), member1.getName(), member2.getName(), _startTime, diff, 0, _type.ordinal());

				OlympiadHistoryManager.getInstance().saveHistory(h);
			}
		}
	}

	public static <T> T valid(T[] array, int index)
	{
		if (array == null)
			return null;
		if (index < 0 || array.length <= index)
			return null;
		return array[index];
	}
}