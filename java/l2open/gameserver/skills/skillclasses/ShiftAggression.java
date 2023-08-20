package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Character.HateInfo;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class ShiftAggression extends L2Skill
{
	public ShiftAggression(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(activeChar.getPlayer() == null)
			return;

		L2Playable playable = (L2Playable) activeChar;

		for(L2Character target : targets)
			if(target != null)
			{
				if(!target.isPlayer())
					continue;

				L2Player player_target = (L2Player) target;

				for(L2NpcInstance npc : L2World.getAroundNpc(activeChar, getAffectRange(), 200))
				{
					HateInfo hateInfo = playable.getHateList().get(npc);
					if(hateInfo == null || hateInfo.hate <= 0)
						continue;
					player_target.addDamageHate(npc, 0, hateInfo.hate + 100);
					hateInfo.hate = 0;
				}
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}
