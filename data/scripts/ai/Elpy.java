package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

public class Elpy extends Fighter
{
	public Elpy(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(attacker != null && Rnd.chance(50))
		{
			int posX = actor.getX();
			int posY = actor.getY();
			int posZ = actor.getZ();

			int signx = posX < attacker.getX() ? -1 : 1;
			int signy = posY < attacker.getY() ? -1 : 1;

			int range = 200;

			posX += Math.round(signx * range);
			posY += Math.round(signy * range);
			posZ = GeoEngine.getHeight(posX, posY, posZ, actor.getReflection().getGeoIndex());

			if(GeoEngine.canMoveToCoord(attacker.getX(), attacker.getY(), attacker.getZ(), posX, posY, posZ, actor.getReflection().getGeoIndex()))
				addTaskMove(posX, posY, posZ, false);
		}
	}

	@Override
	public void checkAggression(L2Character target)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}