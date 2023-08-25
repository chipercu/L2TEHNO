package l2open.gameserver.model;

import l2open.gameserver.tables.player.PlayerData;

public class SubPledge
{
	private int _type;
	private int _leaderId;
	private String _name;
	private L2Clan _clan;

	public SubPledge(L2Clan clan, int type, int leaderId, String name)
	{
		_clan = clan;
		_type = type;
		_leaderId = leaderId;
		_name = name;
	}

	public int getType()
	{
		return _type;
	}

	public String getName()
	{
		return _name;
	}

	public int getLeaderId()
	{
		return _leaderId;
	}

	public void setLeaderId(int leaderId)
	{
		_leaderId = leaderId;
		PlayerData.getInstance().setSubPledgeLeaderId(_clan, leaderId, _type);
	}

	public String getLeaderName()
	{
		for(L2ClanMember member : _clan._members.values())
			if(member.getObjectId() == _leaderId)
				return member.getName();
		return "";
	}
}