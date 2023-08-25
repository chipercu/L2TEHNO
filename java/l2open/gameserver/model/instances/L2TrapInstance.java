package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Events;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2ObjectTasks.TrapDestroyTask;
import l2open.gameserver.model.L2Skill.SkillTargetType;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.serverpackets.NpcInfo;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.reference.*;

import java.util.concurrent.ScheduledFuture;

public class L2TrapInstance extends L2NpcInstance
{
	protected HardReference<? extends L2Character> owner_ref = HardReferences.emptyRef();
	protected final L2Skill _skill;
	protected L2RoundTerritory _territory;
	protected ScheduledFuture<?> _destroyTask;
	protected boolean _detected = false;

	public L2TrapInstance(int objectId, L2NpcTemplate template, L2Character owner, L2Skill trapSkill, Location loc, boolean event)
	{
		super(objectId, template);
		owner_ref = owner.getRef();
		_skill = trapSkill;

		setReflection(owner.getReflection().getId());
		setLevel(owner.getLevel());
		setTitle(owner.getName());
		spawnMe(loc);

		_territory = event ? new L2RoundTerritoryEventTrap(objectId, loc.x, loc.y, 150, loc.z - 100, loc.z + 100, this, owner) : new L2RoundTerritoryWithSkill(objectId, loc.x, loc.y, 150, loc.z - 100, loc.z + 100, this, trapSkill);
		L2World.addTerritory(_territory);

		for(L2Character cha : L2World.getAroundCharacters(this, 300, 200))
			cha.updateTerritories();

		_destroyTask = ThreadPoolManager.getInstance().schedule(new TrapDestroyTask(this), event ? ConfigValue.TheHungerGames_TrapTime*1000 : 60000);
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
		if(!target.isMonster() && !target.isPlayable())
			return;

		/*{
			setDetected(true);
			for(L2Player player : L2World.getAroundPlayers(this))
				if(player != null)
					player.sendPacket(new NpcInfo(this, player));
			broadcastUserInfo(true);
			broadcastRelationChanged();
		}*/


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
			//target.sendMessage(new CustomMessage("common.Trap", target));
			destroy();
		}
	}

	public void destroy()
	{
		L2World.removeTerritory(_territory);
		L2Character owner = getOwner();
		if(owner != null)
			owner.removeTrap();
		//deleteMe();
		doDie(this);
		if(_destroyTask != null)
			_destroyTask.cancel(false);
		_destroyTask = null;
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

	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		if(player.getTarget() != this)
		{
			player.setTarget(this, false);
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

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<L2TrapInstance> getRef()
	{
		return (HardReference<L2TrapInstance>) super.getRef();
	}
}