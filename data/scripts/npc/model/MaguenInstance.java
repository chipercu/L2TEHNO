package npc.model;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public final class MaguenInstance extends L2MonsterInstance
{
	public MaguenInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		return;
	}
}
