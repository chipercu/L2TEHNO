package l2open.gameserver.model.entity;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.instancemanager.UnderGroundColliseumManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Rnd;

import java.util.ArrayList;

public class Coliseum
{
	private L2Party previusWinners = null;
	private boolean isWaitingRoom1Free = true;
	private boolean isWaitingRoom2Free = true;
	private boolean isInUse;
	private int winCount = 0;
	private L2Party partyInRoom1 = null;
	private L2Party partyInRoom2 = null;
	private int minlevel;
	private int maxlevel;

	public int getMinLevel()
	{
		return minlevel;
	}

	public int getMaxLevel()
	{
		return maxlevel;
	}

	public class TryStart extends l2open.common.RunnableImpl
	{
		L2Party _party1;
		L2Party _party2;

		public TryStart(L2Party party, L2Party party2)
		{
			_party1 = party;
			_party2 = party2;
		}

		@Override
		public void runImpl()
		{
			startBattle(_party1, _party2);
		}
	}

	public class StopBattle extends l2open.common.RunnableImpl
	{
		L2Party _party1;
		L2Party _party2;
		int _winner = 0;

		public StopBattle(L2Party party, L2Party party2, int winner)
		{
			_party1 = party;
			_party2 = party2;
			_winner = winner;
		}

		@Override
		public void runImpl()
		{
			party_inbattle_list.remove(_party1);
			party_inbattle_list.remove(_party2);

			if(_winner == 0) // Вылетели при тп
			{
				setPreviusWinner(null);
				setWinCount(0);
			}
			else
			{
				if(_party1.getPartyLeader().getTeam() == _winner)
				{
					if(!getPreviusWinners().equals(_party1))
					{
						setPreviusWinner(_party1);
						setWinCount(1);
						for(L2Player member : _party1.getPartyMembers())
							member.setFame((int)(member.getFame() + 80 * member.getRateFame()), "Coliseum");
					}
					else
					{
						incWinCount();
						for(L2Player member : _party1.getPartyMembers())
							member.setFame((int)(member.getFame() + 80 + getWinCount() * 5 * member.getRateFame()), "Coliseum");
					}

					int[] teleloc = getFreeWaitingRoom();
					if(teleloc[0] == 0)
						teleloc = _zone.getSpawns().get(4);
					else if(isWaitingRoom2Free() && isWaitingRoom1Free())
					{
						setPreviusWinner(null);
						for(L2Player member : _party1.getPartyMembers())
							if(!member.isDead())
								member.teleToClosestTown();
					}
					else if(!isWaitingRoom1Free())
					{
						teleloc = _zone.getSpawns().get(1);
						startBattle(getPartyInRoom1(), _party1);
					}
				}

				if(_party2.getPartyLeader().getTeam() == _winner)
				{
					if(!getPreviusWinners().equals(_party2))
					{
						setPreviusWinner(_party2);
						setWinCount(1);
						for(L2Player member : _party2.getPartyMembers())
							member.setFame((int)(member.getFame() + 80* member.getRateFame()), "Coliseum");
					}
					else
					{
						incWinCount();
						for(L2Player member : _party2.getPartyMembers())
							member.setFame((int)(member.getFame() + 80 + getWinCount() * 5 * member.getRateFame()), "Coliseum");
					}

					int[] teleloc = getFreeWaitingRoom();
					if(teleloc[0] == 0)
						teleloc = _zone.getSpawns().get(4);
					else if(isWaitingRoom2Free() && isWaitingRoom1Free())
					{
						setPreviusWinner(null);
						for(L2Player member : _party2.getPartyMembers())
							if(!member.isDead())
								member.teleToClosestTown();
					}
					else if(!isWaitingRoom1Free())
					{
						teleloc = _zone.getSpawns().get(1);
						startBattle(getPartyInRoom1(), _party2);
					}
				}

				setIsInUse(false);
			}
		}
	}

	public class StartBattle extends l2open.common.RunnableImpl
	{
		int _number;
		L2Party _party1;
		L2Party _party2;

		public StartBattle(L2Party party, L2Party party2)
		{
			_number = getColiseumMatchNumber();
			_party1 = party;
			_party2 = party2;
		}

		public void runImpl()
		{
			party_waiting_list.remove(_party1);
			party_waiting_list.remove(_party2);

			if(getPartyInRoom1().equals(_party1) || getPartyInRoom1().equals(_party2))
				setPartyInRoom1(null);
			if(getPartyInRoom2().equals(_party1) || getPartyInRoom2().equals(_party2))
				setPartyInRoom2(null);

			boolean isParty1Ready = checkOffline(_party1);
			boolean isParty2Ready = checkOffline(_party2);
			if(!isParty1Ready)
			{
				//SM ? //CM ? //Not brodcast?
				party_waiting_list.remove(_party1);
				setIsWaitingRoom1Free(true);
				_party2.broadcastMessageToPartyMembers("opponents party is offline, wait for next opponent");
				return;
			}
			else if(!isParty2Ready)
			{
				party_waiting_list.remove(_party2);
				setIsWaitingRoom2Free(true);
				_party1.broadcastMessageToPartyMembers("opponents party is offline, wait for next opponent");
				return;
			}
			else
			{
				party_inbattle_list.addAll(party_waiting_list);
				party_waiting_list.remove(_party2);
				party_waiting_list.remove(_party1);

				setIsWaitingRoom1Free(true);
				setIsWaitingRoom2Free(true);
				teleportPlayers(_party1, _party2);
				setIsInUse(true);

				if(party_waiting_list.size() > 0)
				{
					ArrayList<L2Party> toDel = new ArrayList<L2Party>();
					for(L2Party party : party_waiting_list)
						if(party.getPartyMembers().size() > 6)
							party.broadcastMessageToPartyMembers("Free room at coliseum append");//FIXME: SM? CM? not broadcast?
						else
							toDel.add(party);

					for(L2Party party : toDel)
					{
						party.broadcastMessageToPartyMembers("conditions are nor right for Undergorund battle try register later");//FIXME: SM? CM? not broadcast?
						party_waiting_list.remove(party);
					}

					if(party_waiting_list.size() == 0)
						return;

					L2Party next = party_waiting_list.get(Rnd.get(party_waiting_list.size() - 1));
					for(L2Player member : next.getPartyMembers())
					{
						if(member.getLevel() > getMaxLevel() || member.getLevel() < getMinLevel())
						{
							next.broadcastToPartyMembers(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
							return;
						}

						if(member.isCursedWeaponEquipped() || member.isCombatFlagEquipped() || member.isTerritoryFlagEquipped())
						{
							next.broadcastToPartyMembers(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
							return;
						}

						int[] teleloc = getFreeWaitingRoom();
						if(teleloc[0] == 0)
						{
							Log.add("bug cannot find teleloc for coliseum id: " + getId(), "UC");
							return;
						}

						teleportToWaitingRoom(next, teleloc);
					}

					if(party_waiting_list.size() < 2)
						return;

					L2Party next2 = party_waiting_list.get(Rnd.get(party_waiting_list.size() - 1));
					while(next2 == next)
						next2 = party_waiting_list.get(Rnd.get(party_waiting_list.size() - 1));

					next = next2;
					for(L2Player member : next.getPartyMembers())
					{
						if(member.getLevel() > getMaxLevel() || member.getLevel() < getMinLevel())
						{
							next.broadcastToPartyMembers(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
							return;
						}

						int[] teleloc = getFreeWaitingRoom();
						if(teleloc[0] == 0)
						{
							Log.add("bug cannot find teleloc for coliseum id: " + getId(), "UC");
							return;
						}

						teleportToWaitingRoom(next, teleloc);
					}
				}
			}
		}

		//startTimer()
		//SpawnLifeTowers()
		//startCompWinners()
	}

	private boolean checkOffline(L2Party party)
	{
		party_waiting_list.add(party);
		return party.getMemberCount() == 0;
	}

	private ArrayList<L2Party> party_waiting_list = new ArrayList<L2Party>();
	private ArrayList<L2Party> party_inbattle_list = new ArrayList<L2Party>();

	private L2Zone _zone;
	private int _id = 0;
	private static Integer _event_cycle = 0;

	public Coliseum(int id)
	{
		_id = id;
		minlevel = id;
		maxlevel = id == 1 ? 85 : id + 9;
		try
		{
			load(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(_event_cycle == 0)
			init();
	}

	private void load(int id)
	{
		_event_cycle = getColiseumMatchNumber();//tempory need db table and save system
		if(_event_cycle == Integer.MAX_VALUE)
			_event_cycle = 1;
		else
			_event_cycle++;
	}

	private void init()
	{
		setcoliseummatchnumber(1);
	}

	private void startBattle(L2Party party, L2Party party2)
	{
		if(!isInUse())
		{
			if(getPreviusWinners() == null)
				ThreadPoolManager.getInstance().schedule(new StartBattle(party, party2), 10000);
			else
			{
				ThreadPoolManager.getInstance().schedule(new StartBattle(party, getPreviusWinners()), 10000);
				setIsWaitingRoom1Free(true);
				int[] teleloc = getFreeWaitingRoom();
				setIsWaitingRoom2Free(true);
				teleportToWaitingRoom(party2, teleloc);
			}
		}
		else
			ThreadPoolManager.getInstance().schedule(new TryStart(party, party2), 300000);
	}

	public static void register(L2Player player, int minLevel, int maxLevel)
	{
		Coliseum coli = UnderGroundColliseumManager.getInstance().getColiseumByLevelLimit(maxLevel);
		if(coli == null)
		{
			player.sendMessage("this is not work now, if you have any information about it, contact as");
			return;
		}
		int[] teleloc = coli.getFreeWaitingRoom();
		if(teleloc[0] == 0)
			player.sendMessage("this is not work now, if you have any information about it, contact as");
		coli.party_waiting_list.add(player.getParty());
		coli.teleportToWaitingRoom(player.getParty(), teleloc);
	}

	public void teleportToWaitingRoom(L2Party party, int[] teleloc)
	{
		if(isWaitingRoom1Free())
		{
			setIsWaitingRoom1Free(false);
			setPartyInRoom1(party);
		}
		else if(isWaitingRoom2Free())
		{
			setIsWaitingRoom2Free(false);
			setPartyInRoom2(party);
		}
		else
		{
			//TODO: CM? SM?
			party.getPartyLeader().sendMessage("rooms are not free you has been registred try to use teleport function later");
			return;
		}
		for(L2Player member : party.getPartyMembers())
			member.teleToLocation(new Location(teleloc[0], teleloc[1], teleloc[2]));
		if(!isWaitingRoom2Free)
			startBattle(getPartyInRoom1(), getPartyInRoom2());
	}

	//FIXME: если комнат ожидания больше чем две, переписать данный метод на более оптимальный
	public int[] getFreeWaitingRoom()
	{
		if(isWaitingRoom1Free())
			return _zone.getSpawns().get(0);
		else if(isWaitingRoom2Free())
			return _zone.getSpawns().get(1);
		else
			return new int[0];
	}

	public int getColiseumMatchNumber()
	{
		return _event_cycle;
	}

	public void setcoliseummatchnumber(int number)
	{
		_event_cycle = number;
	}

	/** Return true if object is inside the zone */
	public boolean checkIfInZone(int x, int y)
	{
		return getZone().checkIfInZone(x, y);
	}

	public int getId()
	{
		return _id;
	}

	public final L2Zone getZone()
	{
		if(_zone == null)
			_zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.UnderGroundColiseum, getId(), true);
		return _zone;
	}

	public void StopBattle(L2Party party, L2Party party2, int winner, long period)
	{
		ThreadPoolManager.getInstance().schedule(new StopBattle(party, party2, winner), period);
	}

	public void teleportPlayers(L2Party party, L2Party party2)
	{
		if(party == null && party2 == null || party2 == null && party != null && party.getPartyMembers().isEmpty() || party == null && party2 != null && party2.getPartyMembers().isEmpty() || party2 != null && party != null && party.getPartyMembers().isEmpty() && party2.getPartyMembers().isEmpty())
		{
			StopBattle(party, party2, 0, 20000);//все вылетели обнулим битву
			party_inbattle_list.remove(party);
			party_inbattle_list.remove(party2);
		}
		if(party2 != null && (party == null || party.getPartyMembers().isEmpty()))
		{
			StopBattle(party, party2, party2.getPartyLeader().getTeam(), 20000);//20 сек перед тп обратно второй пати
			party_inbattle_list.remove(party);
		}
		else if(party != null && (party2 == null || party2.getPartyMembers().isEmpty()))
		{
			StopBattle(party, party2, party.getPartyLeader().getTeam(), 20000);//20 сек перед тп обратно первой пати
			party_inbattle_list.remove(party2);
		}
		else
		{
			int[] locOnBGToTP;
			if(party != null)
				for(L2Player member : party.getPartyMembers())
				{
					member.setTeam(1, true);
					//locOnBattleGroundToTelePlayers
					locOnBGToTP = _zone.getSpawns().get(3);
					member.teleToLocation(new Location(locOnBGToTP[0], locOnBGToTP[1], locOnBGToTP[2]));
				}
			if(party2 != null)
				for(L2Player member : party2.getPartyMembers())
				{
					member.setTeam(2, true);
					locOnBGToTP = _zone.getSpawns().get(4);
					member.teleToLocation(new Location(locOnBGToTP[0], locOnBGToTP[1], locOnBGToTP[2]));
				}
			if(party != null)
				for(L2Player member : party.getPartyMembers())
					member.broadcastRelationChanged();
			if(party2 != null)
				for(L2Player member : party2.getPartyMembers())
					member.broadcastRelationChanged();
		}
	}

	public void setPreviusWinner(L2Party previusWinners)
	{
		this.previusWinners = previusWinners;
	}

	public L2Party getPreviusWinners()
	{
		return previusWinners;
	}

	public void setIsWaitingRoom1Free(boolean isWaitingRoom1Free)
	{
		this.isWaitingRoom1Free = isWaitingRoom1Free;
	}

	public boolean isWaitingRoom1Free()
	{
		return isWaitingRoom1Free;
	}

	public void setIsWaitingRoom2Free(boolean isWaitingRoom2Free)
	{
		this.isWaitingRoom2Free = isWaitingRoom2Free;
	}

	public boolean isWaitingRoom2Free()
	{
		return isWaitingRoom2Free;
	}

	public void setIsInUse(boolean isInUse)
	{
		this.isInUse = isInUse;
	}

	public boolean isInUse()
	{
		return isInUse;
	}

	public ArrayList<L2Party> getPartysInBattleGround()
	{
		return party_inbattle_list;
	}

	public ArrayList<L2Party> getWaitingPartys()
	{
		return party_waiting_list;
	}

	public void setWinCount(int winCount)
	{
		this.winCount = winCount;
	}

	public void incWinCount()
	{
		winCount++;
	}

	public int getWinCount()
	{
		return winCount;
	}

	public L2Party getPartyInRoom1()
	{
		return partyInRoom1;
	}

	public L2Party getPartyInRoom2()
	{
		return partyInRoom2;
	}

	public void setPartyInRoom1(L2Party p1)
	{
		partyInRoom1 = p1;
	}

	public void setPartyInRoom2(L2Party p2)
	{
		partyInRoom2 = p2;
	}
}
