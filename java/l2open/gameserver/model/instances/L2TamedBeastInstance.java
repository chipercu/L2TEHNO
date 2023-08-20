package l2open.gameserver.model.instances;

import javolution.util.FastList;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcInfo;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.reference.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

// While a tamed beast behaves a lot like a pet (ingame) and does have
// an owner, in all other aspects, it acts like a mob.
// In addition, it can be fed in order to increase its duration.
// This class handles the running tasks, AI, and feed of the mob.
public final class L2TamedBeastInstance extends L2FeedableBeastInstance
{
	private static final int MAX_DISTANCE_FROM_OWNER = 2000;
	private static final int MAX_DURATION = 1200000; // 20 minutes
	private static final int DURATION_CHECK_INTERVAL = 60000; // 1 minute
	private static final int DURATION_INCREASE_INTERVAL = 20000; // 20 secs
	private static final int BUFF_INTERVAL = 5000; // 5 seconds

	private HardReference<? extends L2Player> owner_ref = HardReferences.emptyRef();
	private int _foodSkillId, _remainingTime = MAX_DURATION;
	private Location _homeLoc;
	private Future<?> _buffTask = null, _durationCheckTask = null;
	private ScheduledFuture<?> _FollowTask = null;
	private List<L2Skill> _beastSkills = null;
	private boolean _follow = false;

	public L2TamedBeastInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		setHome(this);
	}

	public L2TamedBeastInstance(int objectId, L2NpcTemplate template, L2Player owner, int foodSkillId, Location loc)
	{
		super(objectId, template);

		onSpawn();
		setFoodType(foodSkillId);
		setHome(loc);
		setRunning();
		spawnMe(loc);
		setRunning();

		setOwner(owner);
	}

	public void onReceiveFood()
	{
		// Eating food extends the duration by 20secs, to a max of 20minutes
		_remainingTime = _remainingTime + DURATION_INCREASE_INTERVAL;
		if(_remainingTime > MAX_DURATION)
			_remainingTime = MAX_DURATION;
	}

	public Location getHome()
	{
		return _homeLoc;
	}

	public void setHome(Location loc)
	{
		_homeLoc = loc;
	}

	public void setHome(L2Character c)
	{
		setHome(c.getLoc());
	}

	public int getRemainingTime()
	{
		return _remainingTime;
	}

	public void setRemainingTime(int duration)
	{
		_remainingTime = duration;
	}

	public int getFoodType()
	{
		return _foodSkillId;
	}

	public void setFoodType(int foodItemId)
	{
		if(foodItemId > 0)
		{
			_foodSkillId = foodItemId;

			// start the duration checks start the buff tasks
			if(_durationCheckTask != null)
				_durationCheckTask.cancel(true);
			_durationCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckDuration(this), DURATION_CHECK_INTERVAL, DURATION_CHECK_INTERVAL);
		}
	}

	public void addBeastSkill(L2Skill skill)
	{
		if (_beastSkills == null)
			_beastSkills = new FastList<L2Skill>();
		_beastSkills.add(skill);
	}

	public void castBeastSkills()
	{
		if (getPlayer() == null || _beastSkills == null)
			return;
		int delay = 100;
		for(L2Skill skill : _beastSkills)
		{
			ThreadPoolManager.getInstance().schedule(new buffCast(skill), delay);
			delay += (100 + skill.getHitTime());
		}
		ThreadPoolManager.getInstance().schedule(new buffCast(null), delay);
	}

	private class buffCast extends l2open.common.RunnableImpl
	{
		private L2Skill _skill;

		public buffCast(L2Skill skill)
		{
			_skill = skill;
		}

		public void runImpl()
		{
			if (_skill == null)
			{
				if(getFollow())
					startFollowTask();
			}
			else
			{
				setTarget(getPlayer());
				doCast(_skill, getPlayer(), true);
			}
		}
	}

	@Override
	public void doDie(L2Character killer)
	{
		super.doDie(killer);
		stopMove();

		if(_buffTask != null)
		{
			_buffTask.cancel(true);
			_buffTask = null;
		}

		if(_durationCheckTask != null)
		{
			_durationCheckTask.cancel(true);
			_durationCheckTask = null;
		}
		stopFollowTask();

		// clean up variables
		L2Player _owner = getPlayer();
		if (_owner != null && _owner.getTrainedBeast() != null)
			_owner.getTrainedBeast().remove(this);
		_buffTask = null;
		_durationCheckTask = null;
		_foodSkillId = 0;
		_remainingTime = 0;
		setFollow(false);
	}

	@Override
	public L2Player getPlayer()
	{
		return owner_ref.get();
	}

	public void setOwner(L2Player owner)
	{
		if(owner != null)
		{
			owner_ref = owner.getRef();
			setTitle(owner.getName());
			owner.setTrainedBeast(this);
			if(owner.getTrainedBeast().size() > 7)  //Удаляем пета если их больше 7
				doDespawn();
			//setShowSpawnAnimation(this.getObjectId());

			for(L2Player player : L2World.getAroundPlayers(this))
				if(player != null && _objectId != player.getObjectId())
					player.sendPacket(new NpcInfo(this, player));

			// always and automatically follow the owner.
			startFollowTask();

			// instead of calculating this value each time, let's get this now and pass it on
			int totalBuffsAvailable = 0;
			for(L2Skill skill : getTemplate().getSkills().values())
				// if the skill is a buff, check if the owner has it already
				if(skill.getSkillType() == L2Skill.SkillType.BUFF)
					totalBuffsAvailable++;

			// start the buff tasks
			if(_buffTask != null)
				_buffTask.cancel(true);
			_buffTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckOwnerBuffs(this, totalBuffsAvailable), BUFF_INTERVAL, BUFF_INTERVAL);
		}
		else
			doDespawn(); // despawn if no owner
	}

	public void doDespawn()
	{
		// stop running tasks
		stopMove();
		if(_buffTask != null)
		{
			_buffTask.cancel(true);
			_buffTask = null;
		}

		if(_durationCheckTask != null)
		{
			_durationCheckTask.cancel(true);
			_durationCheckTask = null;
		}
		stopFollowTask();

		// clean up variables
		L2Player owner = getPlayer();
		if(owner != null && owner.getTrainedBeast() != null)
			owner.getTrainedBeast().remove(this);
		setTarget(null);
		_foodSkillId = 0;
		_remainingTime = 0;
		setFollow(false);

		// remove the spawn
		onDecay();
	}

	// notification triggered by the owner when the owner is attacked.
	// tamed mobs will heal/recharge or debuff the enemy according to their skills
	public void onOwnerGotAttacked(L2Character attacker)
	{
		L2Player owner = getPlayer();

		// check if the owner is no longer around...if so, despawn
		if(owner == null || !owner.isOnline())
		{
			doDespawn();
			return;
		}
		// if the owner is too far away, stop anything else and immediately run towards the owner.
		if(!isInRange(owner, MAX_DISTANCE_FROM_OWNER))
		{
			if(getFollow())
				startFollowTask();
			return;
		}
		// if the owner is dead, do nothing...
		if(owner.isDead())
			return;

		double HPRatio = owner.getCurrentHpRatio();

		// if the owner has a lot of HP, then debuff the enemy with a random debuff among the available skills
		// use of more than one debuff at this moment is acceptable
		if(HPRatio >= 0.8 && attacker != null)
		{
			HashMap<Integer, L2Skill> skills = getTemplate().getSkills();
			for(L2Skill skill : skills.values())
				// if the skill is a debuff, check if the attacker has it already [ attacker.getEffect(L2Skill skill) ]
				if(skill.isOffensive() && attacker.getEffectList().getEffectsBySkill(skill) == null && Rnd.nextBoolean())
				{
					setTarget(attacker);
					doCast(skill, attacker, true);
				}
		}
		// for HP levels between 80% and 50%, do not react to attack events (so that MP can regenerate a bit)
		// for lower HP ranges, heal or recharge the owner with 1 skill use per attack.
		else if(HPRatio < 0.5)
		{
			int chance = 1;
			if(HPRatio < 0.25)
				chance = 2;

			// if the owner has a lot of HP, then debuff the enemy with a random debuff among the available skills
			HashMap<Integer, L2Skill> skills = getTemplate().getSkills();
			for(L2Skill skill : skills.values())
				if(!skill.isOffensive() && owner.getEffectList().getEffectsBySkill(skill) == null && (Rnd.get(5) < chance))
				{
					setTarget(owner);
					doCast(skill, owner, true);
					return;
				}
		}
	}

	private class CheckDuration extends l2open.common.RunnableImpl
	{
		private L2TamedBeastInstance _tamedBeast;

		CheckDuration(L2TamedBeastInstance tamedBeast)
		{
			_tamedBeast = tamedBeast;
		}

		public void runImpl()
		{
			int foodTypeSkillId = _tamedBeast.getFoodType();
			L2Player owner = _tamedBeast.getPlayer();

			if(owner != null && owner.getInventory() != null)
			{
				L2ItemInstance item = null;
				item = owner.getInventory().getItemByItemId(foodTypeSkillId);
				if(item != null && item.getCount() >= 1)
				{
					owner.getInventory().destroyItem(item, 1, true);
					_tamedBeast.broadcastPacket2(new SocialAction(_tamedBeast.getObjectId(), 3));
				}
				else
					_tamedBeast.deleteMe();
			}
			else
				_tamedBeast.deleteMe();
		}
	}

	private class CheckOwnerBuffs extends l2open.common.RunnableImpl
	{
		private L2TamedBeastInstance _tamedBeast;

		private int _numBuffs;

		CheckOwnerBuffs(L2TamedBeastInstance tamedBeast, int numBuffs)
		{
			_tamedBeast = tamedBeast;
			_numBuffs = numBuffs;
		}

		public void runImpl()
		{
			L2Player owner = _tamedBeast.getPlayer();

			// check if the owner is no longer around...if so, despawn
			if(owner == null || !owner.isOnline())
			{
				doDespawn();
				return;
			}

			setRunning();

			// if the owner is too far away, stop anything else and immediately run towards the owner.
			if(!isInRange(owner, MAX_DISTANCE_FROM_OWNER))
			{
				if(getFollow())
					startFollowTask();
				return;
			}
			// if the owner is dead, do nothing...
			if(owner.isDead())
				return;

			int totalBuffsOnOwner = 0;
			int i = 0;
			int rand = Rnd.get(_numBuffs);
			L2Skill buffToGive = null;

			HashMap<Integer, L2Skill> skills = _tamedBeast.getTemplate().getSkills();

			for(L2Skill skill : skills.values())
				// if the skill is a buff, check if the owner has it already
				if(skill.getSkillType() == L2Skill.SkillType.BUFF)
				{
					if(i == rand)
						buffToGive = skill;
					i++;
					if(owner.getEffectList().getEffectsBySkill(skill) != null)
						totalBuffsOnOwner++;
				}
			// if the owner has less than 60% of this beast's available buff, cast a random buff
			if(_numBuffs * 2 / 3 > totalBuffsOnOwner)
			{
				_tamedBeast.setTarget(owner);
				_tamedBeast.doCast(buffToGive, owner, true);
			}
		}
	}

	public void startFollowTask()
	{
		if (_FollowTask != null)
			stopFollowTask();
		if (getPlayer() != null)
			_FollowTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Follow(), 10, 1000);
	}

	public void stopFollowTask()
	{
		if (_FollowTask != null)
			_FollowTask.cancel(false);
		_FollowTask = null;
	}

	private class Follow extends l2open.common.RunnableImpl
	{
		private Follow()
		{
		}

		public void runImpl()
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getPlayer(), ConfigValue.FollowRange + Rnd.get(10,50));
		}
	}
	
	private boolean getFollow()
	{
		return _follow;
	}
	
	public void setFollow(boolean foll)
	{
		_follow = foll;
	}
}