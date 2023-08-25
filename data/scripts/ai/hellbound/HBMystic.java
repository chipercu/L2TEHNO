package ai.hellbound;

import l2open.gameserver.ai.Mystic;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;

public class HBMystic extends Mystic
{
	private L2Character _atacker;

	public HBMystic(L2Character actor)
	{
		super(actor);
	}

	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		_atacker = attacker;
		super.ATTACKED(_atacker, damage, skill);
	}

	protected void MY_DYING(L2Character killer)
	{
		int id = getActor().getNpcId();
		int hLevel = HellboundManager.getInstance().getLevel();

		switch (id)
		{
			case 22321:
				if (hLevel <= 1)
					HellboundManager.getInstance().addPoints(1);
				break;
			case 22328:
				if (hLevel <= 1)
					HellboundManager.getInstance().addPoints(3);
				break;
			case 22342:
			case 22343:
				if (hLevel == 3)
					HellboundManager.getInstance().addPoints(3);
				break;
			case 22449:
				HellboundManager.getInstance().addPoints(50);
				break;
			case 25536:
				HellboundManager.getInstance().addPoints(200);
				break;
			case 18465:
				if (hLevel == 4)
				{
					HellboundManager.getInstance().addPoints(10000);
					HellboundManager.getInstance().changeLevel(5);
				}
				break;
		}

		super.MY_DYING(killer);
	}
}