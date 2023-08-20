package l2open.gameserver.model;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.serverpackets.*;

import java.util.Vector;

public class PartyRoom
{
	private final int _id;
	private int _minLevel, _maxLevel, _lootDist, _maxMembers;
	private String _title;
	private final Vector<L2Player> members_list = new Vector<L2Player>();

	public PartyRoom(int id, int minLevel, int maxLevel, int maxMembers, int lootDist, String title, L2Player leader)
	{
		_id = id;
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_maxMembers = maxMembers;
		_lootDist = lootDist;
		_title = title;
		members_list.add(leader);
		leader.setPartyRoom(_id);
	}

	public void addMember(L2Player member)
	{
		if(members_list.contains(member))
			return;

		members_list.add(member);
		member.setPartyRoom(_id);
		for(L2Player player : members_list)
			if(player != null)
			{
				player.sendPacket(new PartyMatchList(this));
				player.sendPacket(new ExPartyRoomMember(this, player));
				player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_ENTERED_THE_PARTY_ROOM).addString(member.getName()));
			}
		PartyRoomManager.getInstance().removeFromWaitingList(member);
		member.broadcastUserInfo(true);
	}

	public void removeMember(L2Player member, boolean oust)
	{
		members_list.remove(member);
		member.setPartyRoom(0);
		if(members_list.isEmpty())
			PartyRoomManager.getInstance().removeRoom(getId());
		else
		{
			for(L2Player player : members_list)
				if(player != null)
					player.sendPacket(new PartyMatchList(this), new ExPartyRoomMember(this, player), new SystemMessage(oust ? SystemMessage.S1_HAS_BEEN_OUSTED_FROM_THE_PARTY_ROOM : SystemMessage.S1_HAS_LEFT_THE_PARTY_ROOM).addString(member.getName()));
		}

		member.sendPacket(new ExClosePartyRoom(), new PartyMatchDetail(member), oust ? Msg.YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM : Msg.YOU_HAVE_EXITED_FROM_THE_PARTY_ROOM);
		PartyRoomManager.getInstance().addToWaitingList(member);
		member.broadcastUserInfo(true);
	}

	public void broadcastPacket(L2GameServerPacket packet)
	{
		for(L2Player player : members_list)
			if(player != null)
				player.sendPacket(packet);
	}
	
	public void broadcastCSPacket(L2GameServerPacket packet, L2Player player)
	{
		for(L2Player member : members_list)
			if(member != null && !member.isInBlockList(player) && !member.isBlockAll())
				member.sendPacket(packet);
	}

	public void updateInfo()
	{
		for(L2Player player : members_list)
			if(player != null)
			{
				player.sendPacket(new PartyMatchList(this));
				player.sendPacket(new ExPartyRoomMember(this, player));
			}
	}

	public Vector<L2Player> getMembers()
	{
		return members_list;
	}

	public int getMembersSize()
	{
		return members_list.size();
	}

	public int getId()
	{
		return _id;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	public int getMaxMembers()
	{
		return _maxMembers;
	}

	public int getLootDist()
	{
		return _lootDist;
	}

	public String getTitle()
	{
		return _title;
	}

	public L2Player getLeader()
	{
		return members_list.isEmpty() ? null : members_list.get(0);
	}

	public void setMinLevel(int minLevel)
	{
		_minLevel = minLevel;
	}

	public void setMaxLevel(int maxLevel)
	{
		_maxLevel = maxLevel;
	}

	public void setMaxMembers(int maxMembers)
	{
		_maxMembers = maxMembers;
	}

	public void setLootDist(int lootDist)
	{
		_lootDist = lootDist;
	}

	public void setTitle(String title)
	{
		_title = title;
	}

	public int getLocation()
	{
		return PartyRoomManager.getInstance().getLocation(getLeader());
	}
}