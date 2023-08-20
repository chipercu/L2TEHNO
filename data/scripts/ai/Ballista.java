package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * Данный AI используется NPC Ballista на осадах фортов
 * - может быть уничтожена с использованием Ballista Bomb(1-5 штук)
 * - не использует random walk
 * - не отвечает на атаки
 * 
 * @author SYS
 */
public class Ballista extends DefaultAI
{
	private static final int BALLISTA_BOMB_SKILL_ID = 2342;
	private int _bombsUseCounter;

	public Ballista(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || caster == null || skill.getId() != BALLISTA_BOMB_SKILL_ID)
			return;

		L2Player player = caster.getPlayer();
		Siege siege = SiegeManager.getSiege(actor, true);
		if(siege != null && player != null && siege.getSiegeUnit().getType() == ResidenceType.Fortress)
		{
			L2Clan clan = player.getClan();
			if(clan != null && siege == clan.getSiege() && clan.isDefender())
				return;
		}

		_bombsUseCounter++;
		if(Rnd.chance(20) || _bombsUseCounter > 4)
			actor.doDie(caster);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_bombsUseCounter = 0;
		super.MY_DYING(killer);
	}
}