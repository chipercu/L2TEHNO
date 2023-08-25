package l2open.gameserver.model.entity.olympiad;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExOlympiadUserInfo;
import l2open.gameserver.serverpackets.ExOlympiadMatchStats;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.*;

import java.util.HashMap;

public class OlympiadTeam
{
	public OlympiadGame _game;
	private GCSArray<TeamMember> _members;
	private String _name = "";
	private int _side;

	public OlympiadTeam(OlympiadGame game, int side)
	{
		_game = game;
		_side = side;
		_members = new GCSArray<TeamMember>();
	}

	public void addMember(int obj_id)
	{
		String player_name = "";
		L2Player player = L2ObjectsStorage.getPlayer(obj_id);
		if(player != null)
			player_name = player.getName();
		else
		{
			StatsSet noble = Olympiad._nobles.get(new Integer(obj_id));
			if(noble != null)
				player_name = noble.getString(Olympiad.CHAR_NAME, "");
		}

		_members.add(new TeamMember(obj_id, player_name, _game, _side));

		switch(_game.getType())
		{
			case CLASSED:
			case NON_CLASSED:
				_name = player_name;
				break;
			case TEAM_RANDOM:
				_name = "Team " + _side;
				break;
			case TEAM:
				if(_name.isEmpty()) // Берется имя первого игрока в команде
					_name = player_name + " team";
				break;
		}
	}

	public int getScore(int objId)
	{
		for(TeamMember member : _members)
			if(member.getObjId() == objId)
				return member.getStat().getInteger("olympiad_points");
		return 0;
	}

	public int getAllDamage()
	{
		int allDamage = 0;
		//for(int i : _damage.values())
		//	allDamage += i;
		for(L2Player player : getPlayers())
			allDamage += player.getDamageMy();
		return allDamage;
	}

	public String getName()
	{
		return _name;
	}

	public void portPlayersToArena()
	{
		for(TeamMember member : _members)
			member.portPlayerToArena();
	}

	public void sendRelation()
	{
		for(TeamMember member : _members)
			member.sendRelation();
	}

	public void stopEffect()  
	{  
		for (TeamMember member : _members)  
			member.stopEffect();  
	}  

	public void portPlayersBack()
	{
		for(TeamMember member : _members)
			member.portPlayerBack();
	}
	
	public void regenPlayers()
	{
		for(TeamMember member : _members)
			member.regenPlayer();
	}

	public void preparePlayers()
	{
		for(TeamMember member : _members)
			member.preparePlayer();

		if(_members.size() <= 1)
			return;

		GArray<L2Player> list = new GArray<L2Player>();
		for(TeamMember member : _members)
		{
			L2Player player = member.getPlayer();
			if(player != null)
			{
				list.add(player);
				if(player.getParty() != null)
				{
					L2Party party = player.getParty();
					party.oustPartyMember(player, false);
				}
			}
		}

		if(list.size() <= 1)
			return;

		L2Player leader = list.get(0);
		if(leader == null)
			return;

		L2Party party = new L2Party(leader, 0);
		leader.setParty(party);

		for(L2Player player : list)
			if(player != leader)
				player.joinParty(party);
	}

	public void takePointsForCrash(boolean crash)
	{
		if(!crash)
			for(TeamMember member : _members)
				if(member == null || !member.checkPlayer())
					crash=true;
		if(crash)
			for(TeamMember member : _members)
				member.takePointsForCrash();
	}

	public boolean checkPlayers()
	{
		for(TeamMember member : _members)
			if(member.checkPlayer())
				return true;
		return false;
	}

	public boolean isAllDead()
	{
		for(TeamMember member : _members)
			if(!member.isDead() && member.checkPlayer())
				return false;
		return true;
	}

	public boolean contains(int objId)
	{
		for(TeamMember member : _members)
			if(member.getObjId() == objId)
				return true;
		return false;
	}

	public GArray<L2Player> getPlayers()
	{
		GArray<L2Player> players = new GArray<L2Player>();
		for(TeamMember member : _members)
		{
			L2Player player = member.getPlayer();
			if(player != null)
				players.add(player);
		}
		return players;
	}

	public GCSArray<TeamMember> getMembers()
	{
		return _members;
	}

	public void broadcast(L2GameServerPacket p)
	{
		for(TeamMember member : _members)
		{
			L2Player player = member.getPlayer();
			if(player != null)
				player.sendPacket(p);
		}
	}

	public boolean logout(L2Player player)
	{
		if(player != null)
			for(TeamMember member : _members)
			{
				L2Player pl = member.getPlayer();
				if(pl != null && pl == player)
					member.logout();
			}
		return checkPlayers();
	}

	public boolean doDie(L2Player player)
	{
		if(player != null)
			for(TeamMember member : _members)
			{
				L2Player pl = member.getPlayer();
				if(pl != null && pl == player)
					member.doDie();
			}
		return isAllDead();
	}

	public void winGame(OlympiadTeam looseTeam)
	{
		if(ConfigValue.EnableOlyTotalizator)
			Functions.callScripts("communityboard.manager.OlyTotalizator", "endBattle", new Object[] {this, looseTeam});

		int pointDiff = 0;
		int pDiff = 0;

		for(int i = 0; i < _members.size(); i++)
			try
			{
				TeamMember loos = looseTeam.getMembers().get(i);
				TeamMember win = getMembers().get(i);
				if(!Olympiad.isFakeOly())
					pointDiff = transferPoints(loos.getStat(), win.getStat());

				if(loos.getPlayer().getAttainment() != null)
					loos.getPlayer().getAttainment().oly_battle_end(false);
				if(win.getPlayer().getAttainment() != null)
					win.getPlayer().getAttainment().oly_battle_end(true);
				
				/*try
				{
					win.getPlayer().getInventory().addItem(ConfigValue.OlyWeanTeamAddItemId, ConfigValue.OlyWeanTeamAddItemCount);
					win.getPlayer().sendPacket(SystemMessage.obtainItems(ConfigValue.OlyWeanTeamAddItemId, ConfigValue.OlyWeanTeamAddItemCount, 0));
				}
				catch(Exception e)
				{
					//e.printStackTrace();
				}*/

				if(Olympiad.isFakeOly())
				{
					if(ConfigValue.OlyWeanTeamAddRndItemForPvp.length > 0)
					{
						long[] items = ConfigValue.OlyWeanTeamAddRndItemForPvp[Rnd.get(ConfigValue.OlyWeanTeamAddRndItemForPvp.length)];
						win.getPlayer().getInventory().addItem((int)items[0], items[1]);
					}
				}
				else if(ConfigValue.OlyWeanTeamAddItemId > 0)
				{
					try
					{
						win.getPlayer().getInventory().addItem(ConfigValue.OlyWeanTeamAddItemId, ConfigValue.OlyWeanTeamAddItemCount);
						win.getPlayer().sendPacket(SystemMessage.obtainItems(ConfigValue.OlyWeanTeamAddItemId, ConfigValue.OlyWeanTeamAddItemCount, 0));
					}
					catch(Exception e)
					{
						//e.printStackTrace();
					}
				}

				loos.getPlayer().sendPacket(new SystemMessage(SystemMessage.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(getName()).addNumber(pointDiff));
				loos.getPlayer().sendPacket(new SystemMessage(SystemMessage.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(looseTeam.getName()).addNumber(pointDiff));
				loos.getPlayer().sendPacket(new ExOlympiadMatchStats(true, this, looseTeam, pointDiff));

				win.getPlayer().sendPacket(new SystemMessage(SystemMessage.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(getName()).addNumber(pointDiff));
				win.getPlayer().sendPacket(new SystemMessage(SystemMessage.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(looseTeam.getName()).addNumber(pointDiff));
				win.getPlayer().sendPacket(new ExOlympiadMatchStats(true, this, looseTeam, pointDiff));
				pDiff += pointDiff;
				Log.add("Olympiad Result: " + loos.getPlayer().getName() + " vs " + win.getPlayer().getName() + " ... " + win.getPlayer().getName() + " win " + pointDiff + " points", "olympiad");
				l2open.util.Util.test("Olympiad Result: " + loos.getPlayer().getName() + " vs " + win.getPlayer().getName() + " ... " + win.getPlayer().getName() + " win " + pointDiff + " points: ", "olympiad", "olympiad");

				if(ConfigValue.OlympiadStatEnable)
				{
					OlympiadStat.updateInfo(loos.getPlayer(), 0, -pointDiff); // смерть от игрока, ПТС--
					OlympiadStat.updateInfo(win.getPlayer(), 1, pointDiff); // убийство, ПТС++
				}
			}
			catch(Exception e)
			{
				//e.printStackTrace();
			}
		if(!Olympiad.isFakeOly())
			for(L2Player player : getPlayers())
				try
				{
					L2ItemInstance item = player.getInventory().addItem(ConfigValue.AltOlyBattleRewItem, (long)(_game.getType().getReward()*player.getBonus().RATE_TOKEN));
					player.sendPacket(SystemMessage.obtainItems(item.getItemId(), (long)(_game.getType().getReward()*player.getBonus().RATE_TOKEN), 0));
				}
				catch(Exception e)
				{
					//e.printStackTrace();
				}

		/** ********************************************************** **/
		if(Olympiad.isCustomReward() && ConfigValue.CustomOlyWinRewardItems.length > 0)
			for(L2Player player : getPlayers())
				try
				{
					if(ConfigValue.CustomOlyWinRewardItems.length > 0)
						for(int i = 0; i < ConfigValue.CustomOlyWinRewardItems.length; i += 3)
							if(Rnd.chance(ConfigValue.CustomOlyWinRewardItems[i+2]))
							{
								L2ItemInstance item = player.getInventory().addItem((int)ConfigValue.CustomOlyWinRewardItems[i], (long)ConfigValue.CustomOlyWinRewardItems[i+1]);
								player.sendPacket(SystemMessage.obtainItems(item.getItemId(), (long)ConfigValue.CustomOlyWinRewardItems[i+1], 0));
							}
				}
				catch(Exception e)
				{
					//e.printStackTrace();
				}
		if(Olympiad.isCustomReward() && ConfigValue.CustomOlyLoserRewardItems.length > 0)
			for(L2Player player : looseTeam.getPlayers())
				try
				{
					if(ConfigValue.CustomOlyLoserRewardItems.length > 0)
						for(int i = 0; i < ConfigValue.CustomOlyLoserRewardItems.length; i += 3)
							if(Rnd.chance(ConfigValue.CustomOlyLoserRewardItems[i+2]))
							{
								L2ItemInstance item = player.getInventory().addItem((int)ConfigValue.CustomOlyLoserRewardItems[i], (long)ConfigValue.CustomOlyLoserRewardItems[i+1]);
								player.sendPacket(SystemMessage.obtainItems(item.getItemId(), (long)ConfigValue.CustomOlyLoserRewardItems[i+1], 0));
							}
				}
				catch(Exception e)
				{
					//e.printStackTrace();
				}
		/** ********************************************************** **/
		if(ConfigValue.OlyWinRewardItems.length > 0)
			for(L2Player player : getPlayers())
				try
				{
					if(ConfigValue.OlyWinRewardItems.length > 0)
						for(int i = 0; i < ConfigValue.OlyWinRewardItems.length; i += 3)
							if(Rnd.chance(ConfigValue.OlyWinRewardItems[i+2]))
							{
								L2ItemInstance item = player.getInventory().addItem((int)ConfigValue.OlyWinRewardItems[i], (long)ConfigValue.OlyWinRewardItems[i+1]);
								player.sendPacket(SystemMessage.obtainItems(item.getItemId(), (long)ConfigValue.OlyWinRewardItems[i+1], 0));
							}
				}
				catch(Exception e)
				{
					//e.printStackTrace();
				}
		if(ConfigValue.OlyLoserRewardItems.length > 0)
			for(L2Player player : looseTeam.getPlayers())
				try
				{
					if(ConfigValue.OlyLoserRewardItems.length > 0)
						for(int i = 0; i < ConfigValue.OlyLoserRewardItems.length; i += 3)
							if(Rnd.chance(ConfigValue.OlyLoserRewardItems[i+2]))
							{
								L2ItemInstance item = player.getInventory().addItem((int)ConfigValue.OlyLoserRewardItems[i], (long)ConfigValue.OlyLoserRewardItems[i+1]);
								player.sendPacket(SystemMessage.obtainItems(item.getItemId(), (long)ConfigValue.OlyLoserRewardItems[i+1], 0));
							}
				}
				catch(Exception e)
				{
					//e.printStackTrace();
				}
		/** ********************************************************** **/
		if(ConfigValue.OlyWinnerRewardClanRep > 0)
		{
			TeamMember win = getMembers().get(0);
			if(win != null && win.getPlayer() != null && win.getPlayer().getClan() != null)
				win.getPlayer().getClan().incReputation(1000, false, "OlympiadTeam:Winner:" + win.getPlayer());
		}
		if(ConfigValue.OlyLoserRewardClanRep > 0)
		{
			TeamMember loos = looseTeam.getMembers().get(0);
			if(loos != null && loos.getPlayer() != null && loos.getPlayer().getClan() != null)
				loos.getPlayer().getClan().incReputation(1000, false, "OlympiadTeam:Loser:" + loos.getPlayer());
		}
		/** ********************************************************** **/
		if(ConfigValue.OlyWinnerRewardFame > 0)
			for(L2Player player : getPlayers())
				player.setFame(player.getFame() + ConfigValue.OlyWinnerRewardFame, "OlympiadTeam:Winner");
		if(ConfigValue.OlyLoserRewardFame > 0)
			for(L2Player player : looseTeam.getPlayers())
				player.setFame(player.getFame() + ConfigValue.OlyLoserRewardFame, "OlympiadTeam:Loser");
		/** ********************************************************** **/
		_game.broadcastPacket(new SystemMessage(SystemMessage.S1_HAS_WON_THE_GAME).addString(getName()), true, true);
		//_game.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(getName()).addNumber(pointDiff), true, false);
		//_game.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(looseTeam.getName()).addNumber(pointDiff), true, false);
		//_game.broadcastPacket(new ExOlympiadMatchStats(true, this, looseTeam, pointDiff), true, false);

	    Log.add("Olympiad Result: " + getName() + " vs " + looseTeam.getName() + " ... (" + getAllDamage() + " vs " + looseTeam.getAllDamage() + ") " + getName() + " win " + pDiff + " points", "olympiad");
	}

	public void tie(OlympiadTeam otherTeam)
	{
		if(!Olympiad.isFakeOly())
		{
			for(int i = 0; i < _members.size(); i++)
				try
				{
					StatsSet stat1 = getMembers().get(i).getStat();
					StatsSet stat2 = otherTeam.getMembers().get(i).getStat();
					stat1.set(Olympiad.POINTS, stat1.getInteger(Olympiad.POINTS) - 2);
					stat2.set(Olympiad.POINTS, stat2.getInteger(Olympiad.POINTS) - 2);
					l2open.util.Util.test("Olympiad Result: " + getMembers().get(i).getPlayer().getName() + " vs " + otherTeam.getMembers().get(i).getPlayer().getName() + " ... tie ... -2 point: ", "olympiad", "olympiad");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}

		_game.broadcastPacket(Msg.THE_GAME_ENDED_IN_A_TIE, true, true);
		_game.broadcastPacket(new ExOlympiadMatchStats(false, this, otherTeam, 2), true, false);

		Log.add("Olympiad Result: " + getName() + " vs " + otherTeam.getName() + " ... tie", "olympiad");
	}

	private int transferPoints(StatsSet from, StatsSet to)
	{
		int fromPoints = from.getInteger(Olympiad.POINTS);
		int fromLoose = from.getInteger(Olympiad.COMP_LOOSE);
		int fromPlayed = from.getInteger(Olympiad.COMP_DONE);

		int toPoints = to.getInteger(Olympiad.POINTS);
		int toWin = to.getInteger(Olympiad.COMP_WIN);
		int toPlayed = to.getInteger(Olympiad.COMP_DONE);

		int pointDiff = Math.max(1, Math.min(fromPoints, toPoints) / _game.getType().getLooseMult());
		pointDiff = pointDiff > OlympiadGame.MAX_POINTS_LOOSE ? OlympiadGame.MAX_POINTS_LOOSE : pointDiff;
		
		from.set(Olympiad.POINTS, fromPoints - pointDiff);
		from.set(Olympiad.COMP_LOOSE, fromLoose + 1);
		from.set(Olympiad.COMP_DONE, fromPlayed + 1);

		to.set(Olympiad.POINTS, toPoints + pointDiff);
		to.set(Olympiad.COMP_WIN, toWin + 1);
		to.set(Olympiad.COMP_DONE, toPlayed + 1);

		return pointDiff;
	}

	public void saveNobleData()
	{
		for(TeamMember member : _members)
			member.saveNobleData();
	}
}