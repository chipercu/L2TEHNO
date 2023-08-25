package ai.AntQueen;

import l2open.config.ConfigValue;
import l2open.gameserver.ai.*;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.util.MinionList;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * @date: 12.09.2013
 * аи для Антквин по птсу.
**/

public class ai_boss01_queen_ant extends Fighter
{
	private L2Character myself = null;
	private L2Territory zone = null;
	public String Privates = "nurse_ant:AntQueen.ai_boss01_nurse_ant:2:10sec;nurse_ant:AntQueen.ai_boss01_nurse_ant:2:10sec;nurse_ant:AntQueen.ai_boss01_nurse_ant:2:10sec;royal_guard_ant:AntQueen.ai_boss01_royal_guard_ant:1:"+(280 + Rnd.get(40))+"sec;royal_guard_ant:AntQueen.ai_boss01_royal_guard_ant:1:"+(280 + Rnd.get(40))+"sec;royal_guard_ant:AntQueen.ai_boss01_royal_guard_ant:1:"+(280 + Rnd.get(40))+"sec;royal_guard_ant:AntQueen.ai_boss01_royal_guard_ant:1:"+(280 + Rnd.get(40))+"sec";
	private L2Skill different_level_9_attacked = SkillTable.getInstance().getInfo(4515, 1);
	private L2Skill different_level_9_see_spelled = SkillTable.getInstance().getInfo(4215, 1);
	public int loc_id = -1;

	public void NO_DESIRE()
	{
		AddDoNothingDesire(40,5);
	}

	public ai_boss01_queen_ant(L2Character actor)
	{
		super(actor);
		myself = actor;
		//actor.p_block_move(true, null);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		if(Rnd.get(100) < 33)
		{
			InstantTeleportInMyTerritory(-19480,187344,-5600,200);
		}
		else if(Rnd.get(100) < 50)
		{
			InstantTeleportInMyTerritory(-17928,180912,-5520,200);
		}
		else
		{
			InstantTeleportInMyTerritory(-23808,182368,-5600,200);
		}
		EffectMusic(myself,10000,"BS01_A");
		myself.i_ai0 = 0;
		getActor().CreatePrivates(Privates);

		CreateOnePrivateEx(29002,"AntQueen.ai_boss01_queen_ant_larva",0,1,-21600,179482,-5846,Rnd.get(360),0,0,0);
		AddTimerEx(1001,10000);
		AddTimerEx(3002, 10000);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 1001)
		{
			if(Rnd.get(100) < 30 && getActor().isMoving)
			{
				if(Rnd.get(100) < 50)
				{
					AddEffectActionDesire(myself,3,( ( 50 * 1000 ) / 30 ),30);
				}
				else
				{
					AddEffectActionDesire(myself,4,( ( 50 * 1000 ) / 30 ),30);
				}
			}
			AddTimerEx(1001,10000);
		}
		else if(timer_id == 3002)
		{
			if(!getZone().isInside(myself))
			{
				InstantTeleport(myself,getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z);
				RemoveAllDesire(myself);
			}
			AddTimerEx(3002, 5000);
		}
	}

	@Override
	public void PARTY_DIED(L2Character attacker, L2Character party_member_die)
	{
		if(party_member_die != myself)
		{
			/*if(party_member_die.getNpcId() == 29003)
			{
				CreateOnePrivateEx(party_member_die.getNpcId(),"AntQueen." + party_member_die.getAI().getL2ClassShortName(), 10, 1, party_member_die.getX(), party_member_die.getY(), party_member_die.getZ(), party_member_die.getHeading(), 0, 0, 0);
			}
			else
			{
				CreateOnePrivateEx(party_member_die.getNpcId(),"AntQueen." + party_member_die.getAI().getL2ClassShortName(), (280 + Rnd.get(40)), 1, party_member_die.getX(), party_member_die.getY(), party_member_die.getZ(), party_member_die.getHeading(), 0, 0, 0);
			} */
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
					AddUseSkillDesire(attacker, different_level_9_attacked,100);
				}
			}
		}
		if((attacker.isPlayer() || attacker.isPet() || attacker.isSummon()) && GetAbnormalLevel(attacker,Skill_GetAbnormalType(different_level_9_attacked)) <= 0)
		{
			if(attacker.getPlayer().getMountType() == 1 && GetAbnormalLevel(attacker,Skill_GetAbnormalType(SkillTable.getInstance().getInfo(4258, 1))) <= 0)
			{
				if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4258, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4258, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4258) == 0)
				{
					AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4258, 1),1000000);
				}
			}
			if((skill != null && skill.getId() > 0) && skill.getElement().ordinal() == 0 && (Rnd.get(100) < 70 && getZone().isInside(attacker)))
			{
				if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4018) == 0)
				{
					AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4018, 1),1000000);
				}
			}
			else if(DistFromMe(attacker) > 500 && Rnd.get(100) < 10)
			{
				if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4019) == 0)
				{
					AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4019, 1),1000000);
				}
			}
			else if(DistFromMe(attacker) > 150 && Rnd.get(100) < 10)
			{
				if(Rnd.get(100) < 80 && DistFromMe(attacker) < 500)
				{
					if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4018) == 0)
					{
						AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4018, 1),1000000);
					}
				}
				else if( Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4019) == 0)
				{
					AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4019, 1),1000000);
				}
			}
			else if(Rnd.get(100) < 5 && DistFromMe(attacker) < 250)
			{
				if( Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4017, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4017, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4017) == 0)
				{
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(4017, 1),1000000);
				}
			}
			else if(Rnd.get(100) < 1)
			{
				AddEffectActionDesire(myself,1,( ( 60 * 1000 ) / 30 ),3000000);
			}
			if(attacker.isPlayer() || attacker.isPet() || attacker.isSummon())
			{
				AddAttackDesire(attacker,0,(int)(((((damage / myself.getMaxHp()) / 0.050000) * damage) * 10) * 100));
			}
			if(IsNullCreature(getActor().getMostHated()) == 0)
			{
				if(CanAttack(getActor().getMostHated()) == 0)
				{
					getActor().getMostHated().removeFromHatelist(getActor(), true);
				}
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void PARTY_ATTACKED(L2Character attacker, L2Character party_member_attacked, int damage)
	{
		if((attacker.isPlayer() || attacker.isPet() || attacker.isSummon()) && party_member_attacked != myself && party_member_attacked.getNpcId() != 29002)
		{
			if(DistFromMe(attacker) > 500 && Rnd.get(100) < 5)
			{
				if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4019) == 0)
				{
					AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4019, 1),1000000);
				}
			}
			else if(DistFromMe(attacker) > 150 && Rnd.get(100) < 5)
			{
				if(Rnd.get(100) < 80)
				{
					if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4018) == 0)
					{
						AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4018, 1),1000000);
					}
				}
				else if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4019) == 0)
				{
					AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4019, 1),1000000);
				}
			}
			else if(Rnd.get(100) < 2 && DistFromMe(attacker) < 250)
			{
				if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4017, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4017, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4017) == 0 )
				{
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(4017, 1),1000000);
				}
			}
			if(attacker.isPlayer() || attacker.isPet() || attacker.isSummon())
			{
				AddAttackDesire(attacker,0,(int)(((((damage / myself.getMaxHp()) / 0.050000) * damage) * 1) * 1000));
			}
		}
		super.PARTY_ATTACKED(attacker, party_member_attacked, damage);
	}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{
		if(attacker.isPlayer() || attacker.isPet() || attacker.isSummon())
		{
			if(DistFromMe(attacker) > 500 && Rnd.get(100) < 3)
			{
				if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4019) == 0)
				{
					AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4019, 1),1000000);
				}
			}
			else if(DistFromMe(attacker) > 150 && Rnd.get(100) < 3 )
			{
				if(Rnd.get(100) < 80)
				{
					if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4018) == 0)
					{
						AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4019, 1),1000000);
					}
				}
				else if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4019, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4019) == 0)
				{
					AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(4019, 1),1000000);
				}
			}
			else if(Rnd.get(100) < 2 && DistFromMe(attacker) < 250 )
			{
				if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4017, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4017, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4017) == 0 )
				{
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(4017, 1),1000000);
				}
			}
			if(attacker.isPlayer() || attacker.isPet() || attacker.isSummon())
			{
				AddAttackDesire(attacker,0,(int)(((damage / myself.getMaxHp()) / 0.050000) * 500));
			}
		}
		super.onEvtClanAttacked(attacked_member, attacker, damage);
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
		if(SkillTable.getInstance().getInfo(skill.getId(), 1).getEffectPoint() > 0 && Rnd.get(100) < 15)
		{
			if(Skill_GetConsumeMP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentMp() && Skill_GetConsumeHP(SkillTable.getInstance().getInfo(4018, 1)) < myself.getCurrentHp() && Skill_InReuseDelay(4018) == 0)
			{
				AddUseSkillDesire(speller,SkillTable.getInstance().getInfo(4018, 1),1000000);
			}
		}
		if(skill.getId() == 4020)
		{
			ai_boss01_nurse_ant.queen++;
		}
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return false;
		if(!getZone().isInside(actor))
		{
			actor.clearAggroList(true);

			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

			clearTasks();

			Location sloc = actor.getSpawnedLoc();
			if(sloc != null)
			{
				actor.broadcastSkill(new MagicSkillUse(actor, actor, 2036, 1, 500, 0));
				actor.teleToLocation(sloc.x, sloc.y, GeoEngine.getHeight(sloc, actor.getReflection().getGeoIndex()));
			}
		}
		return super.thinkActive();
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		EffectMusic(myself,10000,"BS02_D");
		super.MY_DYING(killer);
	}

	public L2Territory getZone()
	{
		if(zone == null)
			zone = TerritoryTable.getInstance().getLocation(loc_id);
		return zone;
	}
}