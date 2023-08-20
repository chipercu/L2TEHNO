package ai;

import javolution.util.FastMap;

import java.util.concurrent.ScheduledFuture;

import l2open.common.*;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;
import l2open.util.Rnd;

public class ElcardiaAssistant extends DefaultAI
{
	private boolean _thinking = false;
	private ScheduledFuture<?> _followTask;
	private long _chatTimer;
	private final L2Skill vampRage = SkillTable.getInstance().getInfo(6727, 1);
	private final L2Skill holyResist = SkillTable.getInstance().getInfo(6729, 1);
	private final L2Skill blessBlood = SkillTable.getInstance().getInfo(6725, 1);
	private final L2Skill recharge = SkillTable.getInstance().getInfo(6728, 1);
	private final L2Skill heal = SkillTable.getInstance().getInfo(6724, 1);

	public ElcardiaAssistant(L2NpcInstance actor)
	{
		super(actor);
		_chatTimer = System.currentTimeMillis() + 8000L;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	private L2Player getMaster()
	{
		if(!getActor().getReflection().getPlayers().isEmpty())
			return getActor().getReflection().getPlayers().get(0);
		return null;
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		L2Character following = actor.getFollowTarget();
		if(following == null || !actor.isFollow)
		{
			L2Player master = getMaster();
			if(master != null)
			{
				actor.setFollowTarget(master);
				actor.setRunning();
				actor.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, master, 100);
			}
		}
		super.thinkActive();
		return false;
	}

	@Override
	protected void onEvtThink()
	{
		L2NpcInstance actor = getActor();
		if(_thinking || actor.isActionsDisabled() || actor.isAfraid() || actor.isDead() || actor.isMovementDisabled())
			return;

		_thinking = true;
		try
		{
			if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || getIntention() == CtrlIntention.AI_INTENTION_IDLE)
				thinkActive();
			else if(getIntention() == CtrlIntention.AI_INTENTION_FOLLOW)
				thinkFollow();
		}
		catch(Exception e)
		{
			_log.warning("ElcardiaAssistant: ");
			e.printStackTrace();
		}
		finally
		{
			_thinking = false;
		}
	}

	protected void thinkFollow()
	{
		L2NpcInstance actor = getActor();

		L2Character target = actor.getFollowTarget();

		//Находимся слишком далеко цели, либо цель не пригодна для следования, либо не можем перемещаться
		if(target == null || target.isAlikeDead() || actor.getDistance(target) > 4000 || actor.isMovementDisabled())
		{
			actor.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}

		//Уже следуем за этой целью
		if(actor.isFollow && actor.getFollowTarget() == target)
		{
			clientActionFailed();
			return;
		}

		//Находимся достаточно близко
		if(actor.isInRange(target, 100 + 20))
			clientActionFailed();

		if(_followTask != null)
		{
			_followTask.cancel(false);
			_followTask = null;
		}

		_followTask = ThreadPoolManager.getInstance().schedule(new ThinkFollow(), 250L);

		// -----------------
		Reflection ref = actor.getReflection();

		if(ref != null && _chatTimer < System.currentTimeMillis())
		{
			_chatTimer = System.currentTimeMillis() + 5000;
			L2Player masterplayer = target.getPlayer();
			FastMap<L2Skill, Integer> d_skill = new FastMap<L2Skill, Integer>();
			double distance = actor.getDistance(target);
			switch(ref.getInstancedZoneId())
			{
				case 156:
					QuestState qs = masterplayer.getQuestState("_10293_SevenSignsForbiddenBook");
					if(qs != null && !qs.isCompleted())
					{
						if(Rnd.chance(20))
							return;
						if(qs.getCond() == 1)
							Functions.npcSay(actor, 1029351);
						else if(qs.getCond() == 2)
							Functions.npcSay(actor, 1029350);
						else if(qs.getCond() >= 5)
						{
							if(Rnd.chance(50))
								Functions.npcSay(actor, 1029354);
							else
								Functions.npcSay(actor, 1029353);
						}
					}
					break;
				case 151:
				case 155:
					QuestState qs2 = masterplayer.getQuestState("_10294_SevenSignsMonasteryofSilence");
					if(qs2 != null && !qs2.isCompleted())
					{
						if(qs2.getCond() == 2)
						{
							if(Rnd.chance(20))
							{
								if(Rnd.chance(70))
									Functions.npcSay(actor, 1029452);
								else
									Functions.npcSay(actor, 1029451);
							}

							//skill use task
							if(target.getCurrentHpPercents() < 70)
								addDesiredSkill(d_skill, target, distance, heal);
							if(target.getCurrentMpPercents() < 50)
								addDesiredSkill(d_skill, target, distance, recharge);
							if(target.isInCombat())
								addDesiredSkill(d_skill, target, distance, blessBlood);

							addDesiredSkill(d_skill, target, distance, vampRage);
							addDesiredSkill(d_skill, target, distance, holyResist);

							L2Skill r_skill = selectTopSkill(d_skill);
							chooseTaskAndTargets(r_skill, target, distance);
							doTask();
						}
						else if(qs2.getCond() == 3)
							Functions.npcSay(actor, 1029453);
					}
					QuestState qs3 = masterplayer.getQuestState("_10295_SevenSignsSolinasTomb");
					if(qs3 != null && !qs3.isCompleted())
					{
						if(qs3.getCond() == 1)
						{
							if(Rnd.chance(20))
							{
								if(Rnd.chance(30))
									Functions.npcSay(actor, 1029552);
								else if(Rnd.chance(30))
									Functions.npcSay(actor, 1029550);
								else
									Functions.npcSay(actor, 1029551);
							}

							//skill use task
							if(target.getCurrentHpPercents() < 80)
								addDesiredSkill(d_skill, target, distance, heal);
							if(target.getCurrentMpPercents() < 70)
								addDesiredSkill(d_skill, target, distance, recharge);
							if(target.isInCombat())
								addDesiredSkill(d_skill, target, distance, blessBlood);

							addDesiredSkill(d_skill, target, distance, vampRage);
							addDesiredSkill(d_skill, target, distance, holyResist);

							L2Skill r_skill = selectTopSkill(d_skill);
							chooseTaskAndTargets(r_skill, target, distance);
							doTask();
						}
					}
					QuestState qs4 = masterplayer.getQuestState("_10296_SevenSignsPoweroftheSeal");
					if(qs4 != null && !qs4.isCompleted())
					{
						if(qs4.getCond() == 2)
						{
							//skill use task
							if(target.getCurrentHpPercents() < 80)
								addDesiredSkill(d_skill, target, distance, heal);
							if(target.getCurrentMpPercents() < 70)
								addDesiredSkill(d_skill, target, distance, recharge);
							if(target.isInCombat())
								addDesiredSkill(d_skill, target, distance, blessBlood);

							addDesiredSkill(d_skill, target, distance, vampRage);
							addDesiredSkill(d_skill, target, distance, holyResist);

							L2Skill r_skill = selectTopSkill(d_skill);
							chooseTaskAndTargets(r_skill, target, distance);
							doTask();
						}
					}
					break;
				default:
					break;
			}
		}
		// -----------------

	}

	protected class ThinkFollow extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;

			L2Character target = actor.getFollowTarget();

			if(target == null || actor.getDistance(target) > 4000)
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				actor.teleToLocation(120664, -86968, -3392);
				return;
			}

			if(!actor.isInRange(target, 100 + 20) && (!actor.isFollow || actor.getFollowTarget() != target))
			{
				//Location loc = new Location(target.getX() + Rnd.get(-60, 60), target.getY() + Rnd.get(-60, 60), target.getZ());
				actor.followToCharacter(target, 100, false, true);
			}
			_followTask = ThreadPoolManager.getInstance().schedule(this, 250L);
		}
	}

	@Override
	public void addTaskAttack(L2Character target)
	{

	}
}