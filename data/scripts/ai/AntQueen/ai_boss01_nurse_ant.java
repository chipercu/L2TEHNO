package ai.AntQueen;

import l2open.config.ConfigValue;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;
/**
 *@author: Drizzy
 *@date: 12.09.2013
 *АИ для хилеров АК
 **/

public class ai_boss01_nurse_ant extends DefaultAI
{
	private L2Character myself = null;
	private L2Skill different_level_9_attacked = SkillTable.getInstance().getInfo(4515, 1);
	private L2Skill different_level_9_see_spelled = SkillTable.getInstance().getInfo(4215, 1);
	public static long larva = 0;
	public static long queen = 0;
	private long _lastHeal = 0;

	public ai_boss01_nurse_ant(L2Character actor)
	{
		super(actor);
		myself = actor;
		MaxPursueRange = Integer.MAX_VALUE - 10;
	}

	@Override
	public void NO_DESIRE()
	{
		if(getActor().IsMyBossAlive() > 0)
		{
			AddFollowDesire(getActor().getMyLeader(),20);
		}
		else
		{
			AddMoveAroundDesire(40,20);
		}
		super.NO_DESIRE();
	}

	@Override
	public void PARTY_ATTACKED(L2Character attacker, L2Character party_member_attacked, int damage)
	{
		if(party_member_attacked.getNpcId()== 29001)
		{
			if(party_member_attacked.getCurrentHp() < party_member_attacked.getMaxHp())
			{
				if(DistFromMe(party_member_attacked) > 1900 && /*getActor().isCastingNow() &&*/ IsNullCreature(getMostHated()) == 0 && getMostHated().getNpcId() == 29002 && System.currentTimeMillis() - _lastHeal < 3000)
				{
				}
				else
				{
					AddUseSkillDesire(party_member_attacked,SkillTable.getInstance().getInfo(4020, 1),1000000);
				}
			}
		}
		if(party_member_attacked.getNpcId() == 29002)
		{
			if(party_member_attacked.getCurrentHp() < party_member_attacked.getMaxHp())
			{
				_lastHeal = System.currentTimeMillis();
				AddUseSkillDesire(party_member_attacked,SkillTable.getInstance().getInfo(4020, 1),100);
				AddUseSkillDesire(party_member_attacked,SkillTable.getInstance().getInfo(4024, 1),100);
			}
		}
		super.PARTY_ATTACKED(attacker, party_member_attacked, damage);
	}

	public L2Character getMostHated() //Метод для получения мостхейта (для нпсов), ибо в ядре у нас заглушка хейта на нпс.
	{
	  	if(queen >= larva)
			return L2ObjectsStorage.getByNpcId(29001); //Ant Queen
		else if(larva > queen)
			return L2ObjectsStorage.getByNpcId(29002); //Ant Larva
		return null;
	}

	@Override
	public void PARTY_DIED(L2Character attacker, L2Character party_member_die)
	{
		if(party_member_die != myself)
		{
			if(party_member_die == getActor().getMyLeader())
			{
				Despawn(myself);
			}
		}
		super.PARTY_DIED(attacker, party_member_die);
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if((attacker.getZ() - myself.getZ()) > 5 || ((attacker.getZ() - myself.getZ()) < -500))
		{
		}
		else if(attacker.getLevel() > (myself.getLevel() + 8))
		{
			if(GetAbnormalLevel(attacker,Skill_GetAbnormalType(different_level_9_attacked)) <= 0)
			{
				if(different_level_9_attacked.getId() == 4515)
				{
					AddUseSkillDesire(attacker, different_level_9_attacked,100);
					//attacker.removeFromHatelist(getActor(), false);
					return;
				}
				else
				{
					AddUseSkillDesire(attacker, different_level_9_attacked, 100);
				}
			}
		}
		else if(attacker.isPlayer() || attacker.isPet() || attacker.isSummon())
		{
			if(damage == 0)
			{
				damage = 1;
			}
			AddAttackDesire(attacker,1,((int)(((1.000000 * damage) / (myself.getLevel() + 7)) * 20000)));
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character speller)
	{
		if((speller.getZ() - myself.getZ()) > 5 || ((speller.getZ() - myself.getZ()) < -500))
		{
		}
		else if(speller.getLevel() > (myself.getLevel() + 8))
		{
			if(ConfigValue.AQHighCharPunishment == 0)
			{
				if(GetAbnormalLevel(speller,Skill_GetAbnormalType(different_level_9_see_spelled)) <= 0)
				{
					if(different_level_9_see_spelled.getId() == 4215)
					{
						AddUseSkillDesire(speller, different_level_9_see_spelled,100);
						//speller.removeFromHatelist(getActor(), false);
						//RemoveAttackDesire(speller.id);
						return;
					}
					else
					{
						AddUseSkillDesire(speller, different_level_9_see_spelled, 100);
					}
				}
			}
			else if(ConfigValue.AQHighCharPunishment == 1)
			{
				if(speller.isPlayer())
					speller.teleToClosestTown();
				else if(speller.isPlayable() && (speller.isSummon() || speller.isPet()) && speller.getPlayer() != null)
					speller.getPlayer().teleToClosestTown();
			}
		}
	}

	@Override
	public void SEE_CREATURE(L2Character creature)
	{
		if((creature.getZ() - myself.getZ()) > 5 || ((creature.getZ() - myself.getZ()) < -500))
		{
		}
		else if(creature.getLevel() > (myself.getLevel() + 8))
		{
			if(ConfigValue.AQHighCharPunishment == 0)
			{
				if(GetAbnormalLevel(creature,Skill_GetAbnormalType(different_level_9_attacked)) <= 0)
				{
					if(different_level_9_attacked.getId() == 4515)
					{
						AddUseSkillDesire(creature, different_level_9_attacked, 100);
						//creature.removeFromHatelist(getActor(), false);
						//RemoveAttackDesire(creature.id);
						return;
					}
					else
					{
						AddUseSkillDesire(creature, different_level_9_attacked, 100);
					}
				}
			}
			else if(ConfigValue.AQHighCharPunishment == 1)
			{
				if(creature.isPlayer())
					creature.teleToClosestTown();
				else if(creature.isPlayable() && (creature.isSummon() || creature.isPet()) && creature.getPlayer() != null)
					creature.getPlayer().teleToClosestTown();
			}
		}
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	public boolean isNotReturnHome()
	{
		return true;
	}
}
