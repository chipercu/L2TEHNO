package ai.SeedOfAnnihilation;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.util.Location;
import l2open.util.NpcUtils;

/**
 * @author: Drizzy
 * @date: 16.05.2012
 * @about: АИ для РБ в СоА. Сделан по ПТС скриптам.
 */
public class ai_boss_torumba extends Fighter
{
	private L2Character myself = null;
	private int SpecialSkill01_ID = 6403;
	private int SpecialSkill02_ID = 6404;
	private int TARGET_CHECK_TIMER = 1111;
	private int TIME_EXPIRED_TIMER = 1112;
	private int HURRY_UP_TIMER = 1113;
	private int SWING_SKILL_TIMER = 1114;
	private int TRR_CHECK_TIMER = 1116;
	private int bomona_x = -174654;
	private int bomona_y = 184277;
	private int bomona_z = -15408;
	private L2Character c_ai0 = null;
	private L2Character c_ai1 = null;
	private int i_ai2 = 0;
	private long i_ai3 = 0;
	private L2Party param1;

	public ai_boss_torumba(L2Character actor)
	{
		super(actor);
		myself = actor;
		i_ai2 = 0;
		i_ai3 = 0;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		AddTimerEx(TRR_CHECK_TIMER,( 5 * 1000 ));
		CreateOnePrivateEx(18845, "SeedOfAnnihilation.ai_torumba_helper", "L2TerrainObject", getActor().getX(), getActor().getY(), getActor().getZ(), 0L);
		CreateOnePrivateEx(32739, "npc", "L2Npc", bomona_x, bomona_y, bomona_z, 0);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character speller)
	{
		if(speller.getPlayer() != null)
		{
			if(speller.getPlayer().getTransformation() != 126)
			{
				if(IsNullCreature(c_ai0) == 0)
				{
					SendScriptEvent(c_ai0, 20091020, speller.getObjectId());
					return;
				}
			}
			if(skill.getId() == 968)
			{
				getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, speller);
				if((System.currentTimeMillis()) - i_ai3 < 8000)
				{
					i_ai2++;
					if(i_ai2 == 10)
					{
						BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801132);
						getActor().setNpcState(1);
					}
					else if(i_ai2 == 20)
					{
						BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801133);
						getActor().setNpcState(2);
					}
					else if(i_ai2 == 30)
					{
						BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801134);
						getActor().setNpcState(3);
					}
					else if(i_ai2 == 40)
					{
						BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801135);
						getActor().setNpcState(4);
					}
					else if(i_ai2 == 50)
					{
						BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801136);
						getActor().setNpcState(5);
					}
					else if(i_ai2 == 60)
					{
						BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801137);
						getActor().setNpcState(6);
					}
					else if(i_ai2 == 70)
					{
						BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801138);
						getActor().setNpcState(7);
					}
					else if(i_ai2 == 80)
					{
						BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801139);
						getActor().setNpcState(8);
					}
					else if(i_ai2 == 90)
					{
						BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801140);
						getActor().setNpcState(9);
					}
					else if(i_ai2 == 100)
					{
						getActor().doDie(getActor());
						getActor().setNpcState(10);
					}
				}
				else
				{
					i_ai2 = 1;
					getActor().setNpcState(0);
				}
				i_ai3 = System.currentTimeMillis();
			}
		}
		super.onEvtSeeSpell(skill, speller);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(attacker != null)
		{
			if(attacker.getPlayer().getTransformation() != 126)
			{
				if(IsNullCreature(c_ai0) == 0)
				{
					SendScriptEvent(c_ai0,20091020,attacker.getObjectId());
					return;
				}
			}
			if(param1 == null)
			{
				L2Party party0 = attacker.getPlayer().getParty();
				if(party0 != null)
				{
					AddTimerEx(HURRY_UP_TIMER,((9 * 60) * 1000));
					AddTimerEx(SWING_SKILL_TIMER,(10 * 1000));
					AddTimerEx(TARGET_CHECK_TIMER,(7 * 1000));
					param1 = party0;
					BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801131);
					i_ai3 = System.currentTimeMillis();
					c_ai1 = attacker;
				}
				else if(IsNullCreature(c_ai0) == 0)
				{
					SendScriptEvent(c_ai0,20091020,attacker.getObjectId());
					return;
				}
			}
			else if(param1 != null)
			{
				L2Party party0 = attacker.getPlayer().getParty();
				if(party0 != null && party0 != param1)
				{
					SendScriptEvent(c_ai0,20091020,attacker.getObjectId());
					return;
				}
				else if(party0 == null)
				{
					SendScriptEvent(c_ai0,20091020,attacker.getObjectId());
					return;
				}
			}
			else
			{
				getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void SPELL_SUCCESSED(L2Skill skill, L2Character target)
	{
		if(skill.getId() == 6404 || skill.getId() == 6403)
		{
			SendScriptEvent(c_ai0, 20091018, target.getObjectId());
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 20091017)
		{
			c_ai0 = L2ObjectsStorage.getNpc(script_event_arg2);
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == HURRY_UP_TIMER)
		{
			BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801141);
			AddTimerEx(TIME_EXPIRED_TIMER,( 60 * 1000 ));
		}
		else if(timer_id == TIME_EXPIRED_TIMER)
		{
			BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801142);
			ThreadPoolManager.getInstance().schedule(new SpawnTask(getActor().getSpawn().getLoc()), 10 * 60 * 1000);
			getActor().deleteMe();
		}
		else if(timer_id == SWING_SKILL_TIMER)
		{
			AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SpecialSkill02_ID,1),1);
			AddTimerEx(SWING_SKILL_TIMER,( 7 * 1000 ));
		}
		else if(timer_id == TARGET_CHECK_TIMER)
		{
			if(c_ai1 == getActor().getMostHated())
			{
				AddUseSkillDesire(getActor().getMostHated(),SkillTable.getInstance().getInfo(SpecialSkill01_ID,1),1);
			}
			c_ai1 = getActor().getMostHated();
			AddTimerEx(TARGET_CHECK_TIMER,( 6 * 1000 ));
		}
		else if(timer_id == TRR_CHECK_TIMER)
		{
			if(param1 != null)
			{
				L2Territory terr = TerritoryTable.getInstance().getLocation(96570);
				if(!terr.isInside(myself))
				{
					BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801143);
					AddTimerEx(TIME_EXPIRED_TIMER,( 4 * 1000 ));
				}
			}
			AddTimerEx(TRR_CHECK_TIMER,( 5 * 1000 ));
		}
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
			NpcUtils.spawnSingle(25697, loc);
		}
	}

	@Override
	public void NO_DESIRE()
	{
		param1 = null;
		getActor().setNpcState(0);
		super.NO_DESIRE();
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		for(L2Character cha : L2World.getAroundNpc(getActor(), 3000, 500))
			if(cha.getNpcId() == 32739)
				cha.deleteMe();
		i_ai2 = 0;
		i_ai3 = 0;
		super.MY_DYING(killer);
	}
}