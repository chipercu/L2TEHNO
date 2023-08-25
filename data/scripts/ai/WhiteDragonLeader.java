package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;

public class WhiteDragonLeader extends Fighter
{
	public WhiteDragonLeader(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor != null)
			// ru: Враг зашел! Приготовьтесь защищаться!!
			actor.broadcastPacket(new ExShowScreenMessage("The enemies have attacked. Everyone come out and fight!!!! ... Urgh~!", 3000, ScreenMessageAlign.MIDDLE_CENTER, false));
		super.MY_DYING(killer);
	}

}