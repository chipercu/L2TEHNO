package ai.PavelRuins;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * @date: 16.08.2012
 * @АИ для Boom Golem (100% PTS).
 */

public class ai_golem_boom1 extends DefaultAI
{
	private L2Character myself = null;
	private L2Character c0 = null;
	private int Skill01_ID = 6264;
	private int i_ai8 = 0;

	public ai_golem_boom1(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	/*public void AddUseSkillDesire(L2Character target, L2Skill skill, int weight)
	{
		_log.info("ai_golem_boom1: AddUseSkillDesire("+target.getName()+","+skill.getId()+","+weight+")");
		super.AddUseSkillDesire(target, skill, weight);
	}*/

	@Override
	public void NO_DESIRE()
	{
		c0 = GetCreatureFromID(i_ai8);
		if(IsNullCreature(c0) == 0 && !c0.isDead())
		{
			if(DistFromMe(c0) > 50 && DistFromMe(c0) < 2000 && !myself.isDead() && GeoEngine.canAttacTarget(getActor(), c0, false))
				getActor().teleToLocation(c0.getX(), c0.getY(), c0.getZ());
			else if(DistFromMe(c0) <= 50 && GeoEngine.canAttacTarget(getActor(), c0, false))
			{
				//AddUseSkillDesire(c0, SkillTable.getInstance().getInfo(Skill01_ID, 1),Integer.MAX_VALUE);
				getActor().doCast(SkillTable.getInstance().getInfo(Skill01_ID,1), c0, true);
			}
		}
		super.NO_DESIRE();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		i_ai8 = attacker.getObjectId();
		if(DistFromMe(attacker) > 50 && DistFromMe(attacker) < 2000 && !myself.isDead() && GeoEngine.canAttacTarget(getActor(), attacker, false))
			getActor().teleToLocation(attacker.getX(), attacker.getY(), attacker.getZ());
		else if(DistFromMe(attacker) <= 50 && !attacker.isDead() && GeoEngine.canAttacTarget(getActor(), attacker, false))
		{
				//AddUseSkillDesire(c0, SkillTable.getInstance().getInfo(Skill01_ID, 1),Integer.MAX_VALUE);
				getActor().doCast(SkillTable.getInstance().getInfo(Skill01_ID,1), c0, true);
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{
		i_ai8 = attacker.getObjectId();
		if(DistFromMe(attacker) > 50 && DistFromMe(attacker) < 2000 && !myself.isDead() && GeoEngine.canAttacTarget(getActor(), attacker, false))
			getActor().teleToLocation(attacker.getX(), attacker.getY(), attacker.getZ());
		else if(DistFromMe(attacker) <= 50 && !attacker.isDead() && GeoEngine.canAttacTarget(getActor(), attacker, false))
		{
				//AddUseSkillDesire(c0, SkillTable.getInstance().getInfo(Skill01_ID, 1),Integer.MAX_VALUE);
				getActor().doCast(SkillTable.getInstance().getInfo(Skill01_ID,1), c0, true);
		}
		super.onEvtClanAttacked(attacked_member, attacker, damage);
	}

	@Override
	public void SEE_CREATURE(L2Character target)
	{
		i_ai8 = target.getObjectId();
		if(DistFromMe(target) > 50 && DistFromMe(target) < 2000 && !myself.isDead() && GeoEngine.canAttacTarget(getActor(), target, false))
			getActor().teleToLocation(target.getX(), target.getY(), target.getZ());
		else if(DistFromMe(target) <= 50 && GeoEngine.canAttacTarget(getActor(), target, false))
		{
			//AddUseSkillDesire(c0, SkillTable.getInstance().getInfo(Skill01_ID, 1),Integer.MAX_VALUE);
			getActor().doCast(SkillTable.getInstance().getInfo(Skill01_ID,1), c0, true);
		}
		super.SEE_CREATURE(target);
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		c0 = GetCreatureFromID(i_ai8);
		if(IsNullCreature(c0) == 0)
		{
			if(DistFromMe(c0) > 50 && DistFromMe(c0) < 2000 && !myself.isDead() && GeoEngine.canAttacTarget(getActor(), c0, false))
				getActor().teleToLocation(c0.getX(), c0.getY(), c0.getZ());
			else if(DistFromMe(c0) <= 50 && !c0.isDead() && GeoEngine.canAttacTarget(getActor(), c0, false))
			{
				//AddUseSkillDesire(c0, SkillTable.getInstance().getInfo(Skill01_ID, 1),Integer.MAX_VALUE);
				getActor().doCast(SkillTable.getInstance().getInfo(Skill01_ID,1), c0, true);
			}
		}
	}
}