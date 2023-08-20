package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.ValidateLocation;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2SiegeHeadquarterInstance extends L2NpcInstance
{
	private L2Player _player;
	private Siege _siege;
	private L2Clan _owner;
	private long _lastAnnouncedAttackedTime = 0;
	private boolean _invul = false;

	public L2SiegeHeadquarterInstance(L2Player player, int objectId, L2NpcTemplate template)
	{
		super(objectId, template);

		_player = player;
		_owner = _player.getClan();
		if(_owner == null)
		{
			deleteMe();
			return;
		}

		SiegeClan sc = null;

		if(_player.getEventMaster() == null || _player.getEventMaster()._ref == null || _player.getEventMaster()._ref.getId() != _player.getReflectionId())
		{
			_siege = SiegeManager.getSiege(_player, true);
			if(_siege != null)
				sc = _siege.getAttackerClan(_owner);
			else if(_player.getTerritorySiege() > -1)
				sc = TerritorySiege.getSiegeClan(_owner);
		}
		else if(_player.getEventMaster().siege_event && (_player.getEventMaster()._defender_clan == null || _player.getEventMaster()._defender_clan.getClanId() != _player.getClanId()))
			sc = _player.getEventMaster().getSiegeClan(_player);

		if(sc == null)
		{
			deleteMe();
			return;
		}

		sc.setHeadquarter(this);
	}

	@Override
	public String getName()
	{
		return _owner.getName();
	}

	public L2Clan getClan()
	{
		return _owner;
	}

	@Override
	public String getTitle()
	{
		return "";
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		L2Player player = attacker.getPlayer();
		if(player == null/* || isInvul()*/)
			return false;
		L2Clan clan = player.getClan();
		return clan == null || _owner != clan;
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return isAutoAttackable(attacker);
	}

	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		if(player.getTarget() != this)
		{
			player.setTarget(this);
			player.sendPacket(makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.p_max_hp));
		}
		else
		{
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
			if(isAutoAttackable(player))
				player.getAI().Attack(this, false, shift);
			else
				player.sendActionFailed();
		}
	}

	@Override
	public void doDie(L2Character killer)
	{
		if(_siege != null)
		{
			SiegeClan sc = _siege.getAttackerClan(_player.getClan());
			if(sc != null)
				sc.removeHeadquarter();
		}
		else if(_player.getEventMaster() != null)
		{
			SiegeClan sc = _player.getEventMaster().getSiegeClan(_player);
			if(sc != null)
				sc.removeHeadquarter();
		}
		super.doDie(killer);
	}

	@Override
	public void reduceCurrentHp(final double damage, final L2Character attacker, L2Skill skill, final boolean awake, final boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{
		if(System.currentTimeMillis() - _lastAnnouncedAttackedTime > 120000)
		{
			_lastAnnouncedAttackedTime = System.currentTimeMillis();
			_owner.broadcastToOnlineMembers(Msg.YOUR_BASE_IS_BEING_ATTACKED);
		}
		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, isDot, i2, sendMesseg, bow, crit, tp);
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean isInvul()
	{
		return _invul;
	}

	public void setInvul(boolean invul)
	{
		_invul = invul;
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
	public int getClanCrestId()
	{
		Integer result = 0;
		if(_owner != null && _owner.getHasCastle() != 0 && (CastleManager.getInstance().getCastleByOwner(_owner).getDominionLord() != 0 || ConfigValue.ShowClanCrestWithoutQuest))
			result = _owner.getCrestId();
		return result;
	}

	@Override
	public int getClanCrestLargeId()
	{
		Integer result = 0;
		if(_owner != null && _owner.getHasCastle() != 0 && (CastleManager.getInstance().getCastleByOwner(_owner).getDominionLord() != 0 || ConfigValue.ShowClanCrestWithoutQuest))
			result = _owner.getCrestLargeId();
		return result;
	}

	@Override
	public int getAllyCrestId()
	{
		Integer result = 0;
		if(_owner != null && _owner.getHasCastle() != 0 && _owner.getAlliance() != null)
			result = _owner.getAlliance().getAllyCrestId();
		return result;
	}
}