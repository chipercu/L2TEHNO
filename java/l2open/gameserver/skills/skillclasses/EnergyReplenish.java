package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class EnergyReplenish extends L2Skill
{
	private int _addEnergy;

	public EnergyReplenish(StatsSet set)
	{
		super(set);
		_addEnergy = set.getInteger("addEnergy");
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(!activeChar.isPlayer())
			return false;

		L2Player player = (L2Player)activeChar;
		L2ItemInstance item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		if(item == null || (item.getItem().getAgathionEnergy() - item.getAgathionEnergy()) < _addEnergy)
		{
			player.sendPacket(new SystemMessage(SystemMessage.YOUR_ENERGY_CANNOT_BE_REPLENISHED_BECAUSE_CONDITIONS_ARE_NOT_MET));
			return false;
		}

		return true;
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character cha : targets)
		{
			cha.setAgathionEnergy(cha.getAgathionEnergy() + _addEnergy);
			cha.sendPacket(new SystemMessage(SystemMessage.ENERGY_S1_REPLENISHED).addNumber(_addEnergy));
		}
	}
}
