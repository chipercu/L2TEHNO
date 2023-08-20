package l2open.gameserver.model.instances;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.PrintfFormat;

import java.lang.ref.WeakReference;

public class L2MinionInstance extends L2MonsterInstance
{
	private WeakReference<L2MonsterInstance> _master = null;

	public L2MinionInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public L2MinionInstance(int objectId, L2NpcTemplate template, L2MonsterInstance leader)
	{
		super(objectId, template);
		_master = new WeakReference<L2MonsterInstance>(leader);
	}

	public L2MonsterInstance getLeader()
	{
		return _master != null ? _master.get() : null;
	}

	public void setLeader(L2MonsterInstance leader)
	{
		_master = new WeakReference<L2MonsterInstance>(leader);
	}

	public boolean isRaidFighter()
	{
		return getLeader() != null && getLeader().isRaid();
	}

	@Override
	public void doDie(L2Character killer)
	{
		if(getLeader() != null)
		{
			if(getLeader().isRaid())
				Log.add(PrintfFormat.LOG_BOSS_KILLED, new Object[] { getTypeName(),
						getName() + " {" + getLeader().getName() + "}", getNpcId(), killer, getX(), getY(), getZ(), "-" }, "bosses");
			getLeader().notifyMinionDied(this);
			getLeader().getAI().notifyEvent(CtrlEvent.EVT_PARTY_DEAD, killer, this);
		}
		super.doDie(killer);
	}

	@Override
	public boolean isFearImmune()
	{
		return isRaidFighter();
	}

	@Override
	public Location getSpawnedLoc()
	{
		return getLeader() != null ? getLeader().getMinionPosition() : getLoc();
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public int isUnDying()
	{
		return getTemplate().undying;
	}

	@Override
	public boolean isMinion()
	{
		return true;
	}
}