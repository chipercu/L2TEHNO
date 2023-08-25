package l2open.gameserver.model.entity.siege;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.instances.L2SiegeHeadquarterInstance;
import l2open.gameserver.tables.ClanTable;

public class SiegeClan
{
	private int _clanId = 0;
	public int engr_count = 0;

	private L2SiegeHeadquarterInstance _headquarter;

	private SiegeClanType _type;

	public SiegeClan(int clanId, SiegeClanType type)
	{
		_clanId = clanId;
		_type = type;
	}

	public void setHeadquarter(L2SiegeHeadquarterInstance headquarter)
	{
		_headquarter = headquarter;
	}

	public boolean removeHeadquarter()
	{
		if(_headquarter == null)
			return false;
		_headquarter.deleteMe();
		_headquarter = null;
		return true;
	}

	public int getClanId()
	{
		return _clanId;
	}

	public L2Clan getClan()
	{
		return ClanTable.getInstance().getClan(_clanId);
	}

	public L2SiegeHeadquarterInstance getHeadquarter()
	{
		return _headquarter;
	}

	public SiegeClanType getType()
	{
		return _type;
	}

	public void setTypeId(SiegeClanType type)
	{
		_type = type;
	}
}