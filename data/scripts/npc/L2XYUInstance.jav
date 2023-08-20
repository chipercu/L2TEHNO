package npc.model;

import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.templates.L2NpcTemplate;
import events.FractionEvent2.FractionEvent2;

public class L2XYUInstance extends L2MonsterInstance
{
	public L2XYUInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void doDie(L2Character killer)
	{
		FractionEvent2.take_sfera(killer.getPlayer(), this);
		deleteMe();
	}
}