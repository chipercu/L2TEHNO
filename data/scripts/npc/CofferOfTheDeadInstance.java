package npc.model;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.ArrayList;
import java.util.List;

public final class CofferOfTheDeadInstance extends L2NpcInstance
{
	private List<String> _hwid = new ArrayList<String>();
	public CofferOfTheDeadInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public boolean check(L2Player player)
	{
		if(_hwid.contains(player.getHWIDs()))
			return false;
		_hwid.add(player.getHWIDs());
		return true;
	}
}