package npc.model;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import events.SavingSnowman.SavingSnowman;

/**
 * Данный инстанс используется мобом Thomas D. Turkey в эвенте Saving Snowman
 * @author SYS
 */
public class ThomasInstance extends L2MonsterInstance
{
	public ThomasInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void reduceCurrentHp(double i, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{
		i = 10;
		if(attacker.getActiveWeaponInstance() != null)
			switch(attacker.getActiveWeaponInstance().getItemId())
			{
				// Хроно оружие наносит больший урон
				case 4202: // Chrono Cithara
				case 5133: // Chrono Unitus
				case 5817: // Chrono Campana
				case 7058: // Chrono Darbuka
				case 8350: // Chrono Maracas
					i = 100;
					break;
				default:
					i = 10;
			}

		super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, isDot, i2, sendMesseg, bow, crit, tp);
	}

	@Override
	public void doDie(L2Character killer)
	{
		L2Character topdam = getTopDamager(getAggroList());
		if(topdam == null)
			topdam = killer;
		SavingSnowman.freeSnowman(topdam);
		super.doDie(killer);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}