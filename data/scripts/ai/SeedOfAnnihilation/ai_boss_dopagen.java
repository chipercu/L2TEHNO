package ai.SeedOfAnnihilation;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.util.Location;
import l2open.util.NpcUtils;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для рб допагена в СоА. 1оо% ПТС.
 */
public class ai_boss_dopagen extends Fighter
{
	private L2Character myself = null;
	private int CHECK_TIMER = 1111;
	private int TOTEM_TIMER = 1112;
	private int TIME_EXPIRED_TIMER = 1113;
	private int HURRY_UP_TIMER = 1114;
	private int SpecialSkill02_ID = 6376;
	private int SpecialBuff01_ID = 6377;
	private L2Party param1;
	private L2Character c_ai0;
	private int i_ai1 = 0;
	private int i_ai0 = 0;

	public ai_boss_dopagen(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		i_ai0 = 0;
		i_ai1 = 0;
		super.onEvtSpawn();
		AddTimerEx(CHECK_TIMER,(5 * 1000));
		CreateOnePrivateEx(18845,"SeedOfAnnihilation.ai_a_seed_boss_helper", "L2TerrainObject", getActor().getX(),getActor().getY(),getActor().getZ(),0L);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character speller)
	{
		if(speller.getPlayer() != null)
			if(param1 == null)
			{
				L2Party party0 = speller.getPlayer().getParty();
				if(IsNullParty(party0) == 1 )
				{
					SendScriptEvent(c_ai0,20091022,speller.getObjectId());
					return;
				}
			}
			else if(param1 != null)
			{
				L2Party party0 = speller.getPlayer().getParty();
				if(IsNullParty(party0) == 0)
				{
					if(param1 != party0)
					{
						SendScriptEvent(c_ai0,20091022,speller.getObjectId());
						return;
					}
				}
				else if(IsNullParty(party0) == 1)
				{
					SendScriptEvent(c_ai0,20091022,speller.getObjectId());
					return;
				}
			}
		super.onEvtSeeSpell(skill, speller);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(param1 == null)
		{
			if(attacker.getPlayer() != null)
			{
				L2Party party0 = attacker.getPlayer().getParty();
				if(IsNullParty(party0) == 0)
				{
					AddTimerEx(TOTEM_TIMER,(10 * 1000));
					AddTimerEx(HURRY_UP_TIMER,((9 * 60) * 1000));
					param1 = party0;
					BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801144);
					getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
					i_ai0 = 1;
				}
				else if(IsNullCreature(c_ai0) == 0)
				{
					SendScriptEvent(c_ai0,20091022,attacker.getObjectId());
					return;
				}
			}
		}
		else if(param1 != null)
		{
			if(attacker.getPlayer() != null)
			{
				L2Party party0 = attacker.getPlayer().getParty();
				if(IsNullParty(party0) == 0)
				{
					if(param1 != party0)
					{
						SendScriptEvent(c_ai0,20091022,attacker.getObjectId());
						return;
					}
				}
				else if(IsNullParty(party0) == 1)
				{
					SendScriptEvent(c_ai0,20091022,attacker.getObjectId());
					return;
				}
			}
			if(i_ai0 == 0 )
			{
				i_ai0 = 1;
			}
			if(attacker.isPlayer())
			{
				getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage + 1);
			}
			else if(attacker.isPet() || attacker.isSummon())
			{
				if(attacker.getPet().getPlayer() != null)
				{
					getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker.getPet().getPlayer(), damage * 0.5 + 1);
					getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage + 1);
				}
				else
				{
					getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
				}
			}
			if(getActor().getRealDistance3D(attacker) < 250)
			{
				if(Rnd.get(100) < 5)
				{
					AddUseSkillDesire(attacker, SkillTable.getInstance().getInfo(SpecialSkill02_ID,2), 1);
				}
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TOTEM_TIMER)
		{
			BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801145);
			CreateOnePrivateEx(18844, "SeedOfAnnihilation.ai_totem_of_cocracon_dopargen", "L2TerrainObject", "type", 0, -213003, 175497, -11952, 0);
			CreateOnePrivateEx(18844, "SeedOfAnnihilation.ai_totem_of_cocracon_dopargen", "L2TerrainObject", "type", 1, -213003, 176830, -12280, 0);
		}
		else if(timer_id == CHECK_TIMER)
		{
			if(param1 != null)
			{
				L2Territory terr = TerritoryTable.getInstance().getLocation(96556);
				if(!terr.isInside(myself))
				{
					BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801146);
					AddTimerEx(TIME_EXPIRED_TIMER,(4 * 1000));
				}
			}
			BroadcastScriptEvent(8,getActor().getObjectId(),2000);
			AddTimerEx(CHECK_TIMER,(5 * 1000));
		}
		else if(timer_id == HURRY_UP_TIMER)
		{
			BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801147);
			AddTimerEx(TIME_EXPIRED_TIMER,(60 * 1000));
		}
		else if(timer_id == TIME_EXPIRED_TIMER)
		{
			BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801148);
			ThreadPoolManager.getInstance().schedule(new SpawnTask(getActor().getSpawn().getLoc()), 10 * 60 * 1000);
			getActor().deleteMe();
		}
	}

	@Override
	public void NO_DESIRE()
	{
		param1 = null;
		for(L2Character cha : L2World.getAroundNpc(getActor(), 3000, 500))
			if(cha.getNpcId() == 18844)
				cha.doDie(cha);
		super.NO_DESIRE();
	}

	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 20091017)
		{
			c_ai0 = L2ObjectsStorage.getNpc(script_event_arg2);
		}
		else if(script_event_arg1 == 20091024)
		{
			switch(i_ai1)
			{
				case 0:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,1),1);
					break;
				case 1:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,2),1);
					break;
				case 2:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,3),1);
					break;
				case 3:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,4),1);
					break;
				case 4:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,5),1);
					break;
				case 5:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,6),1);
					break;
				case 6:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,7),1);
					break;
				case 7:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,8),1);
					break;
				case 8:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,9),1);
					break;
				case 9:
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SpecialBuff01_ID,10),1);
					break;
			}
			if(i_ai1 < 9 )
			{
				i_ai1++;
			}
		}
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		if(skill.getId() == SpecialBuff01_ID)
		{
			L2Party party0 = param1;
			int i3 = party0.getMemberCount();
			for(int i2 = 0; i2 < i3; i2++)
			{
				L2Player c0 = party0.getPartyMembers().get(i2);
				if(IsNullCreature(c0) == 0)
				{
					SendScriptEvent(c_ai0,20091025,c0.getObjectId());
				}
			}
		}
		super.onEvtFinishCasting(skill,caster, target);
	}

	public class SpawnTask extends l2open.common.RunnableImpl
	{
		Location loc;
		private SpawnTask(Location _loc)
		{
			loc = _loc;
		}
		@Override
		public void runImpl()
		{
			NpcUtils.spawnSingle(25698, loc);
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		for(L2Character cha : L2World.getAroundNpc(getActor(), 3000, 500))
			if(cha.getNpcId() == 18844)
				cha.doDie(cha);
		i_ai1 = 0;
		i_ai0 = 0;
		super.MY_DYING(killer);
	}
}