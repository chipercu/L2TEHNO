package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;

public class BodyDestroyer extends DefaultAI
{
	private L2Player _firstAttaker = null;
	private static final int announceDeathSkill = 5256;
	
	public BodyDestroyer(L2Character actor)
	{
		super(actor);
	}
	
	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill sk)
	{	
		L2NpcInstance actor = getActor();
		if (actor == null || actor.isDead())
			return;
		
		L2Player plr = attacker.getPlayer();
		if(_firstAttaker == null && plr != null)
		{
			_firstAttaker = plr;
			attacker.addDamageHate(actor, 0, 9999);
			
			L2Skill skill = SkillTable.getInstance().getInfo(announceDeathSkill, 1);
			if (skill != null)
				skill.getEffects(actor, attacker, false, false);
		}
		super.ATTACKED(attacker, damage, sk);
	}
	
	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(_firstAttaker != null)
		{
			_firstAttaker.getEffectList().stopEffect(announceDeathSkill);
			_firstAttaker = null;
		}
		super.MY_DYING(killer);
	}
}