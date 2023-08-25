package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.templates.L2NpcTemplate;

public final class L2TerrainObjectInstance extends L2NpcInstance
{
	public L2TerrainObjectInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		setHideName(true);
	}
	
	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		player.sendActionFailed();
	}
}