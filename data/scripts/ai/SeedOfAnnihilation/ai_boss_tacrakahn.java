package ai.SeedOfAnnihilation;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.util.Location;
import l2open.util.NpcUtils;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для таркхана в СоА. 1оо% ПТС.
 */
public class ai_boss_tacrakahn extends Fighter
{
	private L2Character myself = null;
	private int TOTEM_TIMER = 3334;
	private int CHECK_TIMER = 3112;
	private int TIME_EXPIRED_TIMER = 3113;
	private int HURRY_UP_TIMER = 3114;
	private int ROAR_TIMER = 3115;
	private int BUFF_TIMER = 3116;
	private int PRIVATE_SPAWN_TIMER = 3117;
	private int SpecialSkill01_ID = 6380;
	private int SpecialSkill02_ID = 6381; //2
	private int PowerUpSkill01_ID = 6372;
	private int TotemSkill = 6370;
	private L2Party param1;
	private L2Character c_ai0;
	private int i_ai0 = 0;
	private int i_ai1 = 0;

	public ai_boss_tacrakahn(L2Character actor)
	{
		super(actor);
		myself = actor;
		i_ai0 = 0;
		i_ai1 = 0;
		AddTimerEx(CHECK_TIMER,( 5 * 1000 ));
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		CreateOnePrivateEx(18845,"SeedOfAnnihilation.ai_a_seed_boss_helper", "L2TerrainObject", getActor().getX(),getActor().getY(),getActor().getZ(),0L);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character speller)
	{
		if(skill.getId() == TotemSkill)
		{
			BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801127);
		}
		if(speller.getPlayer() != null)
		{
			if(param1 == null)
			{
				L2Party party0 = speller.getPlayer().getParty();
				if(IsNullParty(party0) == 1 )
				{
					SendScriptEvent(c_ai0,20091021,speller.getObjectId());
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
						SendScriptEvent(c_ai0,20091021,speller.getObjectId());
						return;
					}
				}
				else if(IsNullParty(party0) == 1)
				{
					SendScriptEvent(c_ai0,20091021,speller.getObjectId());
					return;
				}
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
					AddTimerEx(TOTEM_TIMER,( 10 * 1000 ));
					AddTimerEx(HURRY_UP_TIMER,( ( 9 * 60 ) * 1000 ));
					AddTimerEx(BUFF_TIMER,( 100 * 1000 ));
					AddTimerEx(PRIVATE_SPAWN_TIMER,( 30 * 1000 ));
					i_ai0 = 1;
					param1 = party0;
					BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801126);
					getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
				}
				else if(IsNullCreature(c_ai0) == 0)
				{
					SendScriptEvent(c_ai0,20091021,attacker.getObjectId());
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
						SendScriptEvent(c_ai0,20091021,attacker.getObjectId());
						return;
					}
				}
				else if(IsNullParty(party0) == 1)
				{
					SendScriptEvent(c_ai0,20091021,attacker.getObjectId());
					return;
				}
			}
			if(i_ai0 == 0)
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
					AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(SpecialSkill02_ID,2), 1);
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
			if(i_ai0 == 1 && !getActor().isDead() && param1 != null && canSpawn(1))
			{
				BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801128);
				SendScriptEvent(CreateOnePrivateEx(18843, "SeedOfAnnihilation.ai_totem_of_cocracon_torumba", "L2TerrainObject", 96530, 0), 8, getActor().getObjectId());
				SendScriptEvent(CreateOnePrivateEx(18843, "SeedOfAnnihilation.ai_totem_of_cocracon_torumba", "L2TerrainObject", 96530, 0), 8, getActor().getObjectId());
				SendScriptEvent(CreateOnePrivateEx(18843, "SeedOfAnnihilation.ai_totem_of_cocracon_torumba", "L2TerrainObject", 96530, 0), 8, getActor().getObjectId());
				SendScriptEvent(CreateOnePrivateEx(18843, "SeedOfAnnihilation.ai_totem_of_cocracon_torumba", "L2TerrainObject", 96530, 0), 8, getActor().getObjectId());
			}
			AddTimerEx(TOTEM_TIMER,(60 * 1000));
		}
		else if(timer_id == CHECK_TIMER)
		{
			if(param1 != null)
			{
				L2Territory terr = TerritoryTable.getInstance().getLocation(96530);
				if(!terr.isInside(myself))
				{
					BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801129);
					AddTimerEx(TIME_EXPIRED_TIMER,(5 * 1000));
				}
			}
			AddTimerEx(CHECK_TIMER,(5 * 1000));
		}
		else if(timer_id == HURRY_UP_TIMER)
		{
			BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801129);
			AddTimerEx(TIME_EXPIRED_TIMER,( 60 * 1000 ));
		}
		else if(timer_id == TIME_EXPIRED_TIMER)
		{
			BroadcastOnScreenMsgStr(myself,4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER,4000,true,true,1801130);
			ThreadPoolManager.getInstance().schedule(new SpawnTask(getActor().getSpawn().getLoc()), 10 * 60 * 1000);
			getActor().deleteMe();
		}
		else if(timer_id == ROAR_TIMER)
		{
			AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SpecialSkill01_ID,1),1);
			AddTimerEx(ROAR_TIMER,( 40 * 1000 ));
		}
		else if(timer_id == BUFF_TIMER)
		{
			if(i_ai1 == 1)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,1),1);
			}
			else if(i_ai1 == 2)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,2),1);
			}
			else if(i_ai1 == 3)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,3),1);
			}
			else if(i_ai1 == 4)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,4),1);
			}
			else if(i_ai1 == 5)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,5),1);
			}
			else if(i_ai1 == 6)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,6),1);
			}
			else if(i_ai1 == 7)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,7),1);
			}
			else if(i_ai1 == 8)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,8),1);
			}
			else if(i_ai1 == 9)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,9),1);
			}
			else if(i_ai1 == 10)
			{
				AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(PowerUpSkill01_ID,10),1);
			}
			if(i_ai1 < 10)
			{
				i_ai1++;
			}
			AddTimerEx(BUFF_TIMER,(100 * 1000));
		}
		else if(timer_id == PRIVATE_SPAWN_TIMER)
		{
			if(!getActor().isDead() && canSpawn(0))
			{
				int i1 = Rnd.get(3);
				switch (i1)
				{
					case 0:
						CreateOnePrivateEx(22747,"Fighter",getActor().getX(),getActor().getY(),getActor().getZ()).getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getActor().getMostHated());
						break;
					case 1:
						CreateOnePrivateEx(22748,"Fighter",getActor().getX(),getActor().getY(),getActor().getZ()).getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getActor().getMostHated());
						break;
					case 2:
						CreateOnePrivateEx(22749,"Fighter",getActor().getX(),getActor().getY(),getActor().getZ()).getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getActor().getMostHated());
						break;
				}
			}
			AddTimerEx(PRIVATE_SPAWN_TIMER,(30 * 1000));
		}
	}

	// Нужно узнать, сколько на оффе их может заспауниться...У спаун мейкера макс коунт стоит 10, но для спауна 18843 вообще нету мейкера, мб они вообще и не должны тогда спауниться...хз нужно тест на РПГ...
	private boolean canSpawn(int id)
	{
		int count=0;
		for(L2NpcInstance npc : L2World.getAroundNpc(getActor(), 2000, 200))
		{
			if(npc != null && !npc.isDead() && id == 1 && npc.getNpcId() == 18843)
				count++;
			else if(npc != null && !npc.isDead() && (npc.getNpcId() == 22747 || npc.getNpcId() == 22748 || npc.getNpcId() == 22749))
				count++;
		}
		return count < 9;
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 20091017)
		{
			c_ai0 = L2ObjectsStorage.getNpc(script_event_arg2);
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
			NpcUtils.spawnSingle(25696, loc);
		}
	}

	@Override
	public void NO_DESIRE()
	{
		param1 = null;
		super.NO_DESIRE();
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		i_ai1 = 0;
		i_ai0 = 0;
		super.MY_DYING(killer);
	}
}
