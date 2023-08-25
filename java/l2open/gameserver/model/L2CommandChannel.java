package l2open.gameserver.model;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.serverpackets.ExMPCCPartyInfoUpdate;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.GArray;

public class L2CommandChannel
{
	private GArray<L2Party> _commandChannelParties;
	private L2Player _commandChannelLeader;
	private int _commandChannelLvl;
	private int _reflection;

	public static final int STRATEGY_GUIDE_ID = 8871;
	public static final int CLAN_IMPERIUM_ID = 391;

	/**
	 * Creates a New Command Channel and Add the Leaders party to the CC
	 * @param CommandChannelLeader
	 */
	public L2CommandChannel(L2Player leader)
	{
		_commandChannelLeader = leader;
		_commandChannelParties = new GArray<L2Party>();
		_commandChannelParties.add(leader.getParty());
		_commandChannelLvl = leader.getParty().getLevel();
		leader.getParty().setCommandChannel(this);
		broadcastToChannelMembers(Msg.ExMPCCOpen);
	}

	/**
	 * Adds a Party to the Command Channel
	 * @param Party
	 */
	public void addParty(L2Party party)
	{
		broadcastToChannelMembers(new ExMPCCPartyInfoUpdate(party, 1));
		_commandChannelParties.add(party);
		refreshLevel();
		party.setCommandChannel(this);
		party.broadcastToPartyMembers(Msg.ExMPCCOpen);
	}

	/**
	 * Removes a Party from the Command Channel
	 * @param Party
	 */
	public void removeParty(L2Party party)
	{
		_commandChannelParties.remove(party);
		refreshLevel();
		party.setCommandChannel(null);
		party.broadcastToPartyMembers(Msg.ExMPCCClose);
		Reflection reflection = getReflection();
		if(reflection != null)
			for(L2Player player : party.getPartyMembers())
				player.teleToLocation(reflection.getReturnLoc(), 0);
		if(_commandChannelParties.size() < 2)
			disbandChannel();
		else
			broadcastToChannelMembers(new ExMPCCPartyInfoUpdate(party, 0));
	}

	/**
	 * Распускает Command Channel
	 */
	public void disbandChannel()
	{
		broadcastToChannelMembers(Msg.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED);
		for(L2Party party : _commandChannelParties)
			if(party != null)
			{
				party.setCommandChannel(null);
				party.broadcastToPartyMembers(Msg.ExMPCCClose);
				if(isInReflection())
					party.broadcastToPartyMembers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
			}
		Reflection reflection = getReflection();
		if(reflection != null)
		{
			reflection.startCollapseTimer(60000);
			setReflection(null);
		}
		_commandChannelParties = null;
		_commandChannelLeader = null;
	}

	/**
	 * @return overall count members of the Command Channel
	 */
	public int getMemberCount()
	{
		int count = 0;
		for(L2Party party : _commandChannelParties)
			if(party != null)
				count += party.getMemberCount();
		return count;
	}

	/**
	 * Broadcast packet to every channel member
	 * @param L2GameServerPacket
	 */
	public void broadcastToChannelMembers(L2GameServerPacket gsp)
	{
		if(_commandChannelParties != null && !_commandChannelParties.isEmpty())
			for(L2Party party : _commandChannelParties)
				if(party != null)
					party.broadcastToPartyMembers(gsp);
	}
	
	public void broadcastCSToChannelMembers(L2GameServerPacket gsp, L2Player player)
	{
		if(_commandChannelParties != null && !_commandChannelParties.isEmpty())
			for(L2Party party : _commandChannelParties)
				if(party != null)
					party.broadcastCSToPartyMembers(player, gsp);
	}

	/**
	 * Broadcast packet to every party leader of command channel
	 */
	public void broadcastToChannelPartyLeaders(L2GameServerPacket gsp)
	{
		if(_commandChannelParties != null && !_commandChannelParties.isEmpty())
			for(L2Party party : _commandChannelParties)
				if(party != null)
				{
					L2Player leader = party.getPartyLeader();
					if(leader != null)
						leader.sendPacket(gsp);
				}
	}

	/**
	 * @return list of Parties in Command Channel
	 */
	public GArray<L2Party> getParties()
	{
		return _commandChannelParties;
	}

	/**
	 * @return list of all Members in Command Channel
	 */
	public GArray<L2Player> getMembers()
	{
		GArray<L2Player> members = new GArray<L2Player>();
		for(L2Party party : getParties())
			members.addAll(party.getPartyMembers());
		return members;
	}

	/**
	 * @return Level of CC
	 */
	public int getLevel()
	{
		return _commandChannelLvl;
	}

	/**
	 * @param sets the leader of the Command Channel
	 */
	public void setChannelLeader(L2Player newLeader)
	{
		_commandChannelLeader = newLeader;
		broadcastToChannelMembers(new SystemMessage(SystemMessage.COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_S1).addString(newLeader.getName()));
	}

	/**
	 * @return the leader of the Command Channel
	 */
	public L2Player getChannelLeader()
	{
		return _commandChannelLeader;
	}

	private void refreshLevel()
	{
		_commandChannelLvl = 0;
		for(L2Party pty : _commandChannelParties)
			if(pty.getLevel() > _commandChannelLvl)
				_commandChannelLvl = pty.getLevel();
	}

	public boolean isInReflection()
	{
		return _reflection > 0;
	}

	public void setReflection(Reflection reflection)
	{
		_reflection = reflection == null ? 0 : reflection.getId();
	}

	public Reflection getReflection()
	{
		return _reflection > 0 ? ReflectionTable.getInstance().get(_reflection) : null;
	}

	/**
	 * Проверяет возможность создания командного канала
	 */
	public static boolean checkAuthority(L2Player creator)
	{
		if(creator.isCreateCommandChannelWithItem())
			return true;
		// CC могут создавать только лидеры партий, состоящие в клане ранком не ниже барона
		if(creator.getClan() == null || !creator.isInParty() || !creator.getParty().isLeader(creator) || creator.getPledgeClass() < L2Player.RANK_WISEMAN)
		{
			creator.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
			return false;
		}

		// CC можно создать, если есть клановый скилл Clan Imperium
		boolean haveSkill = creator.getSkillLevel(CLAN_IMPERIUM_ID) > 0;

		// Ищем Strategy Guide в инвентаре
		boolean haveItem = creator.getInventory().getItemByItemId(STRATEGY_GUIDE_ID) != null;

		if(!haveSkill && !haveItem)
		{
			creator.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
			return false;
		}

		return true;
	}
}