package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.skills.Env;

public final class ConditionEquipEventFlag extends Condition
{
	public ConditionEquipEventFlag()
	{}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		L2ItemInstance flag = ((L2Player) env.character).getActiveWeaponInstance();
		//_log.info("ConditionEquipEventFlag: "+flag+" "+(flag == null ? "" : flag.getCustomType1()));
		return flag == null || flag.getCustomType1() != 77; // 77 это эвентовый флаг
	}
}
