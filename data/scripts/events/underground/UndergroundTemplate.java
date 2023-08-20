package events.underground;

import l2open.common.ThreadPoolManager;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.GCSArray;
import l2open.util.Location;
import l2open.util.Util;
import l2open.util.reference.*;

public abstract class UndergroundTemplate extends Functions implements ScriptFile
{
	protected int _managerId;
	protected String _className;

	protected String _manager;
	protected int _status = 0;
	protected int _price = 10000;
	protected int _timeToStart = 10;
	protected boolean _timeOutTask;

	protected int _minLevel = 1;
	protected int _maxLevel = 85;

	protected Location _team1point;
	protected Location _team2point;
	protected Location _team3point;
	protected Location _team4point;

	protected HardReference<L2Player> _creator = HardReferences.emptyRef();
	protected HardReference<L2Player> _team1 = HardReferences.emptyRef();
	protected HardReference<L2Player> _team2 = HardReferences.emptyRef();
	protected HardReference<L2Player> _team3 = HardReferences.emptyRef();
	protected HardReference<L2Player> _team4 = HardReferences.emptyRef();

	protected GCSArray<HardReference<L2Player>> _teamsLive = new GCSArray<HardReference<L2Player>>();

	protected L2Zone _zone;
	protected ZoneListener _zoneListener;

	public static void sayToAll(String text)
	{
		Announcements.getInstance().announceToAll(text, Say2C.CRITICAL_ANNOUNCEMENT);
	}

	public void template_stop()
	{
		sayToAll(_manager + "Бой прерван по техническим причинам, деньги возвращены");
		returnAdenaToTeams();
		unParalyzeTeams();
		clearTeams();
		_status = 0;
		_timeOutTask = false;
	}

	public void template_create(L2Player player)
	{
		if(_status > 0)
			show("Дождитесь окончания боя", player);
		else
			show("data/scripts/events/underground/" + _managerId + "-1.html", player);
	}

	public void template_register(L2Player player)
	{
		if(_status > 1)
			show("Дождитесь окончания боя", player);
		else
			show("data/scripts/events/underground/" + _managerId + "-3.html", player);
	}

	public void template_check(L2Player player, String[] var)
	{
		if(var.length != 4)
		{
			show("Некорректные данные", player);
			return;
		}

		if(_status > 0)
		{
			show("Дождитесь окончания боя", player);
			return;
		}

		int place = 0;
		try
		{
			place = Integer.valueOf(var[0]);
			_minLevel = Integer.valueOf(var[1]);
			_maxLevel = Integer.valueOf(var[2]);
			_price = Integer.valueOf(var[3]);
			_timeToStart = Integer.valueOf(var[4]);
		}
		catch(Exception e)
		{
			show("Некорректные данные", player);
			return;
		}

		if(_price < 100000 || _price > 100000000)
		{
			show("Неправильная ставка", player);
			return;
		}

		if(getTeamLiveCount(player) < 7)
		{
			show("Для участия, необходима группа из 7 игроков", player);
			return;
		}

		if(player.getParty().getLevel() < _minLevel || player.getParty().getLevel() > _maxLevel)
		{
			show("Вы не подходите по уровню", player);
			return;
		}

		if(player.getAdena() < _price)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		_creator = player.getRef();
		player.reduceAdena(_price, true);

		switch(place)
		{
			case 1:
				_team1 = _creator;
				_team2 = HardReferences.emptyRef();
				_team3 = HardReferences.emptyRef();
				_team4 = HardReferences.emptyRef();
				break;
			case 2:
				_team2 = _creator;
				_team1 = HardReferences.emptyRef();
				_team3 = HardReferences.emptyRef();
				_team4 = HardReferences.emptyRef();
				break;
			case 3:
				_team3 = _creator;
				_team2 = HardReferences.emptyRef();
				_team1 = HardReferences.emptyRef();
				_team4 = HardReferences.emptyRef();
				break;
			case 4:
				_team4 = _creator;
				_team2 = HardReferences.emptyRef();
				_team3 = HardReferences.emptyRef();
				_team1 = HardReferences.emptyRef();
				break;
		}

		_status = 1;

		sayToAll(_manager + player.getName() + " создал бой, ставка " + _price + "а, начало через " + _timeToStart + " мин");
		executeTask("events.underground." + _className, "announce", new Object[0], 60000);
	}

	public void template_register_check(L2Player player, String[] var)
	{
		if(_status > 1)
		{
			show("Дождитесь окончания боя", player);
			return;
		}

		if(var.length != 1)
		{
			show("Некорректные данные", player);
			return;
		}

		int place = 0;

		try
		{
			place = Integer.valueOf(var[0]);
		}
		catch(Exception e)
		{
			show("Некорректные данные", player);
			return;
		}

		if(getTeamLiveCount(player) < 7)
		{
			show("Для участия, необходима группа из 7 игроков", player);
			return;
		}

		if(player.getParty().getLevel() < _minLevel || player.getParty().getLevel() > _maxLevel)
		{
			show("Вы не подходите по уровню", player);
			return;
		}

		if(player.getAdena() < _price)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		switch(place)
		{
			case 1:
				if(_team1.get() != null)
				{
					show("Это место уже занято", player);
					return;
				}
				_team1 = player.getRef();
				sayToAll(_manager + "Группа " + player.getName() + " зарегистрировалась на 1 платформу");
				break;
			case 2:
				if(_team2.get() != null)
				{
					show("Это место уже занято", player);
					return;
				}
				_team2 = player.getRef();
				sayToAll(_manager + "Группа " + player.getName() + " зарегистрировалась на 2 платформу");
				break;
			case 3:
				if(_team3.get() != null)
				{
					show("Это место уже занято", player);
					return;
				}
				_team3 = player.getRef();
				sayToAll(_manager + "Группа " + player.getName() + " зарегистрировалась на 3 платформу");
				break;
			case 4:
				if(_team4.get() != null)
				{
					show("Это место уже занято", player);
					return;
				}
				_team4 = player.getRef();
				sayToAll(_manager + "Группа " + player.getName() + " зарегистрировалась на 4 платформу");
				break;
		}

		player.reduceAdena(_price, true);
	}

	public void template_announce()
	{
		L2Player creator = _creator.get();

		if(_status != 1 || creator == null)
			return;

		if(_timeToStart > 1)
		{
			_timeToStart--;
			sayToAll(_manager + creator.getName() + " создал бой, ставка " + _price + "а" + ", начало через " + _timeToStart + " мин");
			executeTask("events.underground." + _className, "announce", new Object[0], 60000);
		}
		else if(getTeamsLiveList().size() >= 2)
		{
			sayToAll(_manager + "Подготовка к бою");
			executeTask("events.underground." + _className, "prepare", new Object[0], 5000);
		}
		else
		{
			sayToAll(_manager + "Бой не состоялся, нет противников");
			_status = 0;
			returnAdenaToTeams();
			clearTeams();
		}
	}

	public int getTeamLiveCount(L2Player player)
	{
		if(player == null || player.getParty() == null)
			return 0;
		int count = 0;
		for(L2Player member : player.getParty().getPartyMembers())
			if(member != null && !member.isDead() && member.isConnected() && !member.isLogoutStarted())
				count++;
		return count;
	}

	public GCSArray<HardReference<L2Player>> getTeamsLiveList()
	{
		GCSArray<HardReference<L2Player>> list = new GCSArray<HardReference<L2Player>>();
		if(getTeamLiveCount(_team1.get()) >= 7)
			list.add(_team1);
		if(getTeamLiveCount(_team2.get()) >= 7)
			list.add(_team2);
		if(getTeamLiveCount(_team3.get()) >= 7)
			list.add(_team3);
		if(getTeamLiveCount(_team4.get()) >= 7)
			list.add(_team4);
		return list;
	}

	public boolean isRegistered(L2Player player)
	{
		L2Player team1 = _team1.get();
		L2Player team2 = _team2.get();
		L2Player team3 = _team3.get();
		L2Player team4 = _team4.get();
		if(player == null)
			return false;
		if(team1 != null && team1.getParty() != null && team1.getParty() == player.getParty())
			return true;
		if(team2 != null && team2.getParty() != null && team2.getParty() == player.getParty())
			return true;
		if(team3 != null && team3.getParty() != null && team3.getParty() == player.getParty())
			return true;
		if(team4 != null && team4.getParty() != null && team4.getParty() == player.getParty())
			return true;
		return false;
	}

	public void template_prepare()
	{
		if(_status != 1)
			return;

		_teamsLive = getTeamsLiveList();

		if(_teamsLive.size() < 2)
		{
			sayToAll(_manager + "Бой не состоялся, нет противников");
			_status = 0;
			returnAdenaToTeams();
			clearTeams();
			return;
		}

		_status = 2;

		clearArena();
		paralyzeTeams();
		teleportTeamsToArena();

		sayToAll(_manager + "Бой начнется через 15 секунд");
		executeTask("events.underground." + _className, "start", new Object[0], 15000);
	}

	public void template_start()
	{
		if(_status != 2)
			return;

		//if(!checkTeams())
		//	return;

		sayToAll(_manager + "Go!!!");
		unParalyzeTeams();
		_status = 3;
		executeTask("events.underground." + _className, "timeOut", new Object[0], 180000);
		_timeOutTask = true;
	}

	public void clearArena()
	{
		for(L2Object obj : _zone.getObjects())
			if(obj != null && obj.isPlayer())
				((L2Playable) obj).teleToLocation(_zone.getSpawn());
	}

	public void paralyzeTeams()
	{
		L2Skill revengeSkill = SkillTable.getInstance().getInfo(L2Skill.SKILL_RAID_CURSE, 1);

		for(HardReference<L2Player> team_ref : _teamsLive)
		{
			L2Player team = team_ref.get();
			if(team != null && team.getParty() != null)
				for(L2Player player : team.getParty().getPartyMembers())
					if(player != null && !player.isDead() && player.isConnected() && !player.isLogoutStarted())
					{
						player.getEffectList().stopEffect(L2Skill.SKILL_MYSTIC_IMMUNITY);
						player.getEffectList().stopEffect(1540);
						player.getEffectList().stopEffect(396);
						revengeSkill.getEffects(player, player, false, false);
						if(player.getPet() != null)
							revengeSkill.getEffects(player, player.getPet(), false, false);
					}
		}
	}

	public void unParalyzeTeams()
	{
		for(HardReference<L2Player> team_ref : _teamsLive)
		{
			L2Player team = team_ref.get();
			if(team != null && team.getParty() != null)
				for(L2Player player : team.getParty().getPartyMembers())
					if(player != null && !player.isDead() && player.isConnected() && !player.isLogoutStarted())
					{
						player.getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);
						if(player.getPet() != null)
							player.getPet().getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);
					}
		}
	}

	public void teleportTeamsToArena()
	{
		teleportTeamsToArena(_team1.get());
		teleportTeamsToArena(_team2.get());
		teleportTeamsToArena(_team3.get());
		teleportTeamsToArena(_team4.get());
	}

	public void teleportTeamsToArena(L2Player team)
	{
		if(team != null && team.getParty() != null)
			for(L2Player player : team.getParty().getPartyMembers())
				if(player != null && !player.isDead() && player.isConnected() && !player.isLogoutStarted())
				{
					unRide(player);
					unSummonPet(player, true);
					player.teleToLocation(_team1point);
				}
	}

	public void template_timeOut()
	{
		if(_timeOutTask && _status == 3)
		{
			sayToAll(_manager + "Время истекло, ничья!");
			returnAdenaToTeams();
			unParalyzeTeams();
			clearTeams();
			_status = 0;
			_timeOutTask = false;
		}
	}

	public void returnAdenaToTeams()
	{
		for(HardReference<L2Player> team_ref : _teamsLive)
		{
			L2Player team = team_ref.get();
			if(team != null && team.getParty() != null)
				for(L2Player player : team.getParty().getPartyMembers())
					if(player != null && player.isConnected() && !player.isLogoutStarted())
						player.addAdena(_price);
		}
	}

	public void clearTeams()
	{
		for(HardReference<L2Player> team_ref : _teamsLive)
		{
			L2Player team = team_ref.get();
			if(team != null && team.getParty() != null)
				for(L2Player player : team.getParty().getPartyMembers())
					if(player != null && player.isConnected() && !player.isLogoutStarted())
						player.setTeam(0, true);
		}
		_team1 = HardReferences.emptyRef();
		_team2 = HardReferences.emptyRef();
		_team3 = HardReferences.emptyRef();
		_team4 = HardReferences.emptyRef();
	}

	/* TODO
	public boolean playerHasLost(L2Player player)
	{
		if(player.getTeam() == 1)
			_team1live.remove(player);
		else
			_team2live.remove(player);
		L2Skill revengeSkill = SkillTable.getInstance().getInfo(L2Skill.SKILL_RAID_CURSE, 1);
		revengeSkill.getEffects(player, player, false);
		return !checkTeams();
	}

	public void teamHasLost(Integer team_id)
	{
		if(team_id == 1)
		{
			sayToAll(_manager + "Команда 2 победила");
			payAdenaToTeam(2);
		}
		else
		{
			sayToAll(_manager + "Команда 1 победила");
			payAdenaToTeam(1);
		}
		unParalyzeTeams();
		clearTeams();
		_status = 0;
		_timeOutTask = false;
	}

	public void payAdenaToTeam(Integer team_id)
	{
		if(team_id == 1)
			for(L2Player player : _team1list)
			{
				if(player != null)
					player.addAdena(_price + _team2list.size() * _price / _team1list.size());
			}
		else
			for(L2Player player : _team2list)
				if(player != null)
					player.addAdena(_price + _team1list.size() * _price / _team2list.size());
	}

	public void onDie(L2Character self, L2Character killer)
	{
		if(_status >= 2 && self != null && self.isPlayer && self.getTeam() > 0 && (_team1list.contains(self) || _team2list.contains(self)))
		{
			L2Player player = self.getPlayer();
			L2Player kplayer = killer.getPlayer();
			// TODO реализовать тюрьму
			if(kplayer != null)
			{
				sayToAll(_manager + kplayer.getName() + " убил " + player.getName());
				if(player.getTeam() == kplayer.getTeam() || !_team1list.contains(kplayer) && !_team2list.contains(kplayer))
				{
					sayToAll(_manager + "Нарушение правил, игрок " + kplayer.getName() + " оштрафован на " + _price);
					kplayer.reduceAdena(_price);
				}
				playerHasLost(player);
			}
			else
			{
				sayToAll(_manager + player.getName() + " убит");
				playerHasLost(player);
			}
		}
	}

	public void OnPlayerExit(L2Player player)
	{
		if(player != null && _status > 0 && player.getTeam() > 0 && (_team1list.contains(player) || _team2list.contains(player)))
			switch(_status)
			{
				case 1:
					player.setTeam(0);
					_team1list.remove(player);
					_team2list.remove(player);
					sayToAll(_manager + player.getName() + " дисквалифицирован");
					if(player.equals(_creator))
					{
						sayToAll(_manager + "Бой прерван, деньги возвращены");
						returnAdenaToTeams();
						unParalyzeTeams();
						clearTeams();
						_status = 0;
						_timeOutTask = false;
					}
					break;
				case 2:
					player.setTeam(0);
					_team1list.remove(player);
					_team2list.remove(player);
					_team1live.remove(player);
					_team2live.remove(player);
					sayToAll(_manager + player.getName() + " дисквалифицирован");
					checkTeams();
					break;
				case 3:
					player.setTeam(0);
					_team1list.remove(player);
					_team2list.remove(player);
					_team1live.remove(player);
					_team2live.remove(player);
					sayToAll(_manager + player.getName() + " дисквалифицирован");
					checkTeams();
					break;
			}
	}

	public Location onEscape(L2Player player)
	{
		if(player != null && player.getTeam() > 0 && _status > 1 && player.isInZone(_zone))
			OnPlayerExit(player);
		return null;
	}
	*/

	public class ZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			L2Player player = object.getPlayer();
			if(_status >= 2 && player != null && !isRegistered(player))
				ThreadPoolManager.getInstance().schedule(new TeleportTask((L2Character) object, _zone.getSpawn()), 3000);
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			L2Player player = object.getPlayer();
			if(_status >= 2 && player != null && player.getTeam() > 0 && isRegistered(player))
			{
				double angle = Util.convertHeadingToDegree(object.getHeading()); // угол в градусах
				double radian = Math.toRadians(angle - 90); // угол в радианах
				int x = (int) (object.getX() + 50 * Math.sin(radian));
				int y = (int) (object.getY() - 50 * Math.cos(radian));
				int z = object.getZ();
				ThreadPoolManager.getInstance().schedule(new TeleportTask((L2Character) object, new Location(x, y, z)), 3000);
			}
		}
	}

	public class TeleportTask extends l2open.common.RunnableImpl
	{
		Location loc;
		L2Character target;

		public TeleportTask(L2Character target, Location loc)
		{
			this.target = target;
			this.loc = loc;
			target.startStunning();
		}

		public void runImpl()
		{
			target.stopStunning();
			target.teleToLocation(loc);
		}
	}
}