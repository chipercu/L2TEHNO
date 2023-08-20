package l2open.gameserver.model.instances;

import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.castle.CastleSiege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.ValidateLocation;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Log;

public class L2ControlTowerInstance extends L2NpcInstance
{
	private CastleSiege _siege;
	private L2FakeTowerInstance _fakeTower;
	private int _maxHp;

	@Override
	public int getMaxHp()
	{
		return _maxHp;
	}

	public L2ControlTowerInstance(int objectId, L2NpcTemplate template, CastleSiege siege, int maxHp)
	{
		super(objectId, template);
		_siege = siege;
		_maxHp = maxHp;
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		if(attacker == null)
			return false;
		L2Player player = attacker.getPlayer();
		if(player == null)
			return false;
		L2Clan clan = player.getClan();
		//_log.info("player: "+player.getName()+" == "+(!(clan != null && _siege == clan.getSiege() && clan.isDefender())) + " _siege == clan.getSiege()="+(_siege == clan.getSiege())+" clan.isDefender()="+clan.isDefender());
		return !(clan != null && _siege == clan.getSiege() && clan.isDefender());
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return isAutoAttackable(attacker);
	}

	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		if(this != player.getTarget())
		{
			player.setTarget(this);
			player.sendPacket(makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.p_max_hp));
			//_log.info("L2ControlTowerInstance onAction 1: this="+this+" player.getTarget()="+player.getTarget());
		}
		else
		{
			//_log.info("L2ControlTowerInstance onAction 2");
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
			if(isAutoAttackable(player))
			{
				player.getAI().Attack(this, false, shift);
				//_log.info("Add Atack");
			}
			else
				player.sendActionFailed();
		}
	}

	/**
	 * Вызывает обработку смерти у вышек.
	 * @param killer убийца
	 */
	@Override
	public void doDie(L2Character killer)
	{
		onDeath();
		super.doDie(killer);
		Log.add("CONTROL_DEAD["+_siege.getSiegeUnit().getName()+"]["+getLoc()+"]: "+killer, "siege_info");
	}

	/**
	 * Спавнит фэйковую вышку на месте умершей
	 */
	@Override
	public void onDecay()
	{
		if(isDecayed())
		{
			//spawnFakeTower();
			return;
		}
		setDecayed(true);
		decayMe();
		spawnFakeTower();
	}

	/**
	 * Убирает фэйковую вышку на месте новорожденной
	 */
	@Override
	public void spawnMe()
	{
		Log.add("CONTROL_SPAWN["+_siege.getSiegeUnit().getName()+"]["+getLoc()+"]", "siege_info");
		unSpawnFakeTower();
		super.spawnMe();
	}

	/**
	 * Обработка смерти вышки
	 */
	public void onDeath()
	{
		if(getNpcId() != 13002)
			return;
		if(TerritorySiege.isInProgress())
		{
			TerritorySiege.killedCT(getCastle().getId());
			return;
		}
		Siege siege = SiegeManager.getSiege(this, true);
		if(siege != null)
			siege.killedCT();
	}

	/**
	 * Спавнит фэйковую вышку на месте умершей настоящей.
	 * Создается новый инстанс, и привязывается к текущему инстансу.
	 */
	public void spawnFakeTower()
	{
		if(_fakeTower == null)
		{
			L2FakeTowerInstance tower = new L2FakeTowerInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(getFakeTowerNpcId()));
			tower.spawnMe(getLoc());
			_fakeTower = tower;
		}
		else
		{
			_fakeTower.decayMe();
			_fakeTower.spawnMe();
		}
		Log.add("FAKE_CONTROL_SPAWN["+_siege.getSiegeUnit().getName()+"]["+getLoc()+"]", "siege_info");
	}

	/**
	 * Убирает из мира фэйковую вышку которая относится к данному инстансу.
	 * Ссылка на обьект не обнуляется, т.к. он еше будет использован в перспективе
	 */
	public void unSpawnFakeTower()
	{
		if(_fakeTower == null)
			return;

		Log.add("FAKE_CONTROL_UNSPAWN["+_siege.getSiegeUnit().getName()+"]["+getLoc()+"]", "siege_info");
		_fakeTower.decayMe();
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	/**
	 * Осадные вышки должны быть уязвимы во время осады, во время осады включается осадная зона
	 * Вывод - если не в осадной зоне, то неуязвимая
	 * @return уязвимая ли вышка
	 */
	@Override
	public boolean isInvul()
	{
		Siege siege = SiegeManager.getSiege(this, true);
		return siege == null;
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

	/**
	 * Возвращает ID Фэйковой вышки которая спавнится после смерти настоящей.
	 * Для Life Control Tower это 13003
	 * Для Flame Control Tower это 13005
	 * @return Fake Tower NPC ID
	 */
	private int getFakeTowerNpcId()
	{
		return getNpcId() + 1;
	}
}