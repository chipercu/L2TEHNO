package npc.model;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * Данный инстанс используется телепортерами из/в Pagan Temple
 * @author SYS
 */
public class L2TriolsMirrorInstance extends L2NpcInstance
{
	public L2TriolsMirrorInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(getNpcId() == 32040)
			player.teleToLocation(-12766, -35840, -10856); //to pagan
		else if(getNpcId() == 32039)
			player.teleToLocation(35079, -49758, -760); //from pagan
	}
}