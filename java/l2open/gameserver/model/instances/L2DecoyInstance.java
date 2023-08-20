package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class L2DecoyInstance extends L2NpcInstance
{
	protected static final Logger log = Logger.getLogger(L2DecoyInstance.class.getName());

	private L2Player _owner = null;
	private int _lifeTime, _timeRemaining;
	private ScheduledFuture<?> _decoyLifeTask, _hateSpam;

	public L2DecoyInstance(int objectId, L2NpcTemplate template, L2Player owner, int lifeTime)
	{
		super(objectId, template);

		_owner = owner;
		_lifeTime = lifeTime;
		_timeRemaining = _lifeTime;
		int skilllevel = getNpcId() < 13257 ? getNpcId() - 13070 : getNpcId() - 13250;
		_decoyLifeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new DecoyLifetime(), 1000, 1000);
		_hateSpam = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HateSpam(SkillTable.getInstance().getInfo(5272, skilllevel)), 1000, 3000);
	}

	@Override
	public void doDie(L2Character killer)
	{
		super.doDie(killer);
		if(_hateSpam != null)
		{
			_hateSpam.cancel(true);
			_hateSpam = null;
		}
		_lifeTime = 0;
	}

	class DecoyLifetime extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			try
			{
				double newTimeRemaining;
				decTimeRemaining(1000);
				newTimeRemaining = getTimeRemaining();
				if(newTimeRemaining < 0)
					unSummon();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	class HateSpam extends l2open.common.RunnableImpl
	{
		private L2Skill _skill;

		HateSpam(L2Skill skill)
		{
			_skill = skill;
		}

		public void runImpl()
		{
			try
			{
				setTarget(L2DecoyInstance.this);
				doCast(_skill, L2DecoyInstance.this, false);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void unSummon()
	{
		if(_decoyLifeTask != null)
		{
			_decoyLifeTask.cancel(true);
			_decoyLifeTask = null;
		}
		if(_hateSpam != null)
		{
			_hateSpam.cancel(true);
			_hateSpam = null;
		}
		deleteMe();
	}

	public void decTimeRemaining(int value)
	{
		_timeRemaining -= value;
	}

	public int getTimeRemaining()
	{
		return _timeRemaining;
	}

	public int getLifeTime()
	{
		return _lifeTime;
	}

	@Override
	public L2Player getPlayer()
	{
		return _owner;
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		L2Player owner = getPlayer();
		return owner != null && owner.isAutoAttackable(attacker);
	}

	@Override
	public void deleteMe()
	{
		L2Player owner = getPlayer();
		if(owner != null)
			owner.setDecoy(null);
		super.deleteMe();
	}

	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		if(player.getTarget() != this)
		{
			player.setTarget(this, false);
		}
		else if(isAutoAttackable(player))
			player.getAI().Attack(this, false, shift);
		else
			player.sendActionFailed();
	}

	@Override
	public float getColRadius()
	{
		L2Player player = getPlayer();
		if(player == null)
			return 0;
		if(player.getTransformation() != 0 && player.getTransformationTemplate() != 0)
			return NpcTable.getTemplate(player.getTransformationTemplate()).collisionRadius;
		return player.getBaseTemplate().collisionRadius;
	}

	@Override
	public float getColHeight()
	{
		L2Player player = getPlayer();
		if(player == null)
			return 0;
		if(player.getTransformation() != 0 && player.getTransformationTemplate() != 0)
			return NpcTable.getTemplate(player.getTransformationTemplate()).collisionHeight;
		return player.getBaseTemplate().collisionHeight;
	}
}