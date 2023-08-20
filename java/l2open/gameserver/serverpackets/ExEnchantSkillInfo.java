package l2open.gameserver.serverpackets;

import l2open.gameserver.model.base.L2EnchantSkillLearn;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.GArray;

public class ExEnchantSkillInfo extends L2GameServerPacket
{
	private GArray<Integer> _routes;

	private int _id, _level, _canAdd, canDecrease;

	public ExEnchantSkillInfo(int id, int level)
	{
		_routes = new GArray<Integer>();
		_id = id;
		_level = level;

		// skill already enchanted?
		if(_level > 100)
		{
			canDecrease = 1;
			// get detail for next level
			L2EnchantSkillLearn esd = SkillTreeTable.getSkillEnchant(_id, _level + 1);

			// if it exists add it
			if(esd != null)
			{
				addEnchantSkillDetail(esd.getLevel());
				_canAdd = 1;
			}

			for(L2EnchantSkillLearn el : SkillTreeTable.getEnchantsForChange(_id, _level))
				addEnchantSkillDetail(el.getLevel());
		}
		else
			// not already enchanted
			for(L2EnchantSkillLearn esd : SkillTreeTable.getFirstEnchantsForSkill(_id))
			{
				addEnchantSkillDetail(esd.getLevel());
				_canAdd = 1;
			}
	}

	public void addEnchantSkillDetail(int level)
	{
		_routes.add(level);
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x2a);

		writeD(_id);
		writeD(_level);
		writeD(_canAdd); // can add enchant
		writeD(canDecrease); // can decrease enchant

		writeD(_routes.size());
		for(Integer route : _routes)
			writeD(route);
	}
}