package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Events;
import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2ObjectTasks.TrapDestroyTask;
import l2open.gameserver.model.L2Skill.SkillTargetType;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.serverpackets.NpcInfo;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Util;
import l2open.util.reference.*;

import java.util.concurrent.ScheduledFuture;

/**
 * При спауне, если в радиусе 100, есть автоатакейбл цель, то ловушка видимая, так же если мы ее спауним в боевой зоне, она тоже видимая.
 * Если ловушка может заюзать на тебя скил, то она срабатывает.
 *-------------------------------------------------------
 * Если ловушку обнаружили, то она срабатывает при таких же условиях, как и при спауне.
 *-------------------------------------------------------
 * Если ловушку не обнаружить, она срабатывает, только если пройтись сквозь нее...
 *-------------------------------------------------------
 **/
public final class L2TrapInstance extends L2NpcInstance
{
	private HardReference<? extends L2Character> owner_ref = HardReferences.emptyRef();
	private final L2Skill _skill;
	private ScheduledFuture<?> _destroyTask;
	private boolean _detected = false;

	public L2TrapInstance(int objectId, L2NpcTemplate template, L2Character owner, L2Skill trapSkill)
	{
		this(objectId, template, owner, trapSkill, owner.getLoc());
	}

	public L2TrapInstance(int objectId, L2NpcTemplate template, L2Character owner, L2Skill trapSkill, Location loc)
	{
		super(objectId, template);
		owner_ref = owner.getRef();
		_skill = trapSkill;

		setLoc(loc);
		setReflection(owner.getReflection().getId());
		setLevel(owner.getLevel());
		setTitle(owner.getName());
	}

	@Override
	public L2CharacterAI getAI()
	{
		if(_ai == null)
			_ai = new L2Trap(this);
		return _ai;
	}

	@Override
	public void spawnMe()
	{
		super.spawnMe();

		/*boolean detonate=false;
		if(getOwner().isInZoneBattle() || getOwner().isInZone(L2Zone.ZoneType.Siege))
		{
			setDetected(true);
			for(L2Player player : L2World.getAroundPlayers(this))
				if(player != null)
					player.sendPacket(new NpcInfo(this, player));
		}
		else
			for(L2Character cha : L2World.getAroundCharacters(this, 100, 100))
			{
				_log.info("L2TrapInstance: isAutoAttackable["+getOwner().isAutoAttackable(cha)+"]["+cha.isAutoAttackable(getOwner())+"]");
				if(can_detonate(cha))
				{
					detonate=true;
					break;
				}
			}
		if(detonate)
		{
			detonate(null);
		}*/
			/*if(_skill.checkTarget(getOwner(), cha, null, false, false) == null)
			{ // 
				detonate(cha);
				break;
			}*/

		_destroyTask = ThreadPoolManager.getInstance().schedule(new TrapDestroyTask(this), 20000); // 60000
	}

	public boolean can_detonate(L2Character target)
	{
		return getOwner().isAutoAttackable(target) && target.isAutoAttackable(getOwner());
	}

	public void detonate(L2Character target)
	{
		L2Character owner = getOwner();
		if(owner == null || _skill == null)
		{
			destroy();
			return;
		}
		if(target == owner || target == this)
			return;
		else if(!target.isMonster() && !target.isPlayable())
			return;

		if(!isDetected())
		{
			setDetected(true);
			for(L2Player player : L2World.getAroundPlayers(this))
				if(player != null)
					player.sendPacket(new NpcInfo(this, player));
		}

		if(_skill.checkTarget(owner, target, null, false, false) == null)
		{
			GArray<L2Character> targets = new GArray<L2Character>();

			if(_skill.getTargetType() != SkillTargetType.TARGET_AREA)
				targets.add(target);
			else
				for(L2Character t : getAroundCharacters(_skill.getAffectRange(), 128))
					if(_skill.checkTarget(owner, t, null, false, false) == null)
						targets.add(target);

			_skill.useSkill(this, targets);
			destroy();
		}
	}

	public void destroy()
	{
		L2Character owner = getOwner();
		if(owner != null)
			owner.removeTrap();

		if(_destroyTask != null)
			_destroyTask.cancel(false);
		_destroyTask = null;

		doDie(null);
	}

	@Override
	public void doDie(L2Character killer)
	{
		super.doDie(killer);
		getAI().stopAITask();
	}

	@Override
	public int getKarma()
	{
		L2Character owner = getOwner();
		return owner == null ? 0 : owner.getKarma();
	}

	@Override
	public int getPvpFlag()
	{
		L2Character owner = getOwner();
		return owner == null ? 0 : owner.getPvpFlag();
	}

	@Override
	public int getPAtk(L2Character target)
	{
		L2Character owner = getOwner();
		return owner == null ? 0 : owner.getPAtk(target);
	}

	@Override
	public int getMAtk(L2Character target, L2Skill skill)
	{
		L2Character owner = getOwner();
		return owner == null ? 0 : owner.getMAtk(target, skill);
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return getOwner().isAutoAttackable(attacker);
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return false;
	}

	@Override
	public boolean isInvul()
	{
		return true;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{}

	@Override
	public void showChatWindow(L2Player player, String filename)
	{}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{}

	@Override
	public boolean isTrap()
	{
		return true;
	}

	/*if(actor.getEventFlag() > 0)
	{
		for(L2Playable obj : L2World.getAroundPlayables(actor, 500, 150)) // 450 ренж
			if(obj != null && !obj.isAlikeDead() && obj.isVisible() && !_see_creature_list.contains(obj))
			{
				SEE_CREATURE(obj);
				checkAggression(obj);
				if(actor.getDistance(obj) <= actor.getAgroRange())
					_see_creature_list.add(obj);
			}
		if(actor == null)
			_see_creature_list.clear();
		for(L2Playable obj : _see_creature_list)
		{
			try
			{
				if(obj == null || obj.isDead() || actor.getDistance(obj) > 600)
					_see_creature_list.remove(obj);
			}
			catch(Exception e)
			{}
		}
		if(_see_creature_list.size() == 0)
			_see_creature_list.clear();
	}*/

	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		if(player.getTarget() != this)
		{
			player.setTarget(this);
			if(player.getTarget() == this)
				player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel()));
		}
		if(player.isGM())
			Events.onAction(player, this, shift);
		player.sendActionFailed();
	}

	public L2Character getOwner()
	{
		return owner_ref.get();
	}

	public boolean isDetected()
	{
		return _detected;
	}

	public void setDetected(boolean detected)
	{
		_detected = detected;
	}
}