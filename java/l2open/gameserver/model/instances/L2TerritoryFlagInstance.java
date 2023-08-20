package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectTasks.ReturnTerritoryFlagTaskDroped;
import l2open.gameserver.model.L2ObjectTasks.ReturnTerritoryFlagTask;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Skill.SkillTargetType;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.reference.HardReference;

import java.util.concurrent.Future;

public class L2TerritoryFlagInstance extends L2SiegeGuardInstance
{
	public Future<?> _returnTerritoryFlagTask = null;
	public Future<?> _returnTerritoryFlagTask2 = null;
	public L2ItemInstance _item = null;
	private int _itemId = 0;
	private int _baseTerritoryId = 0;
	private int _currentTerritoryId = 0;

	public L2TerritoryFlagInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public L2ItemInstance getItem()
	{
		return _item;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}

	public void setBaseTerritoryId(int territoryId)
	{
		_baseTerritoryId = territoryId;
	}

	public int getBaseTerritoryId()
	{
		return _baseTerritoryId;
	}

	public void setCurrentTerritoryId(int territoryId)
	{
		_currentTerritoryId = territoryId;
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		L2Player player = attacker.getPlayer();
		if(player == null)
			return false;
		if(player.getTerritorySiege() == -1 || player.getTerritorySiege() == _currentTerritoryId)
			return false;
		if(player.getClan() == null || player.getClan().getHasCastle() == 0 || player.getClan().getHasCastle() == _currentTerritoryId)
			return false;
		return true;
	}

	@Override
	public synchronized void reduceCurrentHp(double i, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{
		if(!isVisible() || attacker == null || !attacker.isPlayer() || getDistance(attacker) > 200 || getZ() - attacker.getZ() > 100 || skill != null && skill.getTargetType() != SkillTargetType.TARGET_ONE)
			return;
		else if(Rnd.chance(95))
			return;

		L2Player player = attacker.getPlayer();
		if(player == null || player.getClan() == null || player.getClan().getHasCastle() == 0)
			return;
		else if(player.getTerritorySiege() == _currentTerritoryId || player.getClan().getHasCastle() == _currentTerritoryId)
			return;
		else if(player.isMounted())
			return;
		else if(ConfigValue.NoTakeTerrFlagTransform && player.getTransformation() != 0)
			return;
		else if(player.isCursedWeaponEquipped() || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
			return;
		else if(CastleManager.getInstance().getCastleByIndex(player.getClan().getHasCastle()).getFlags().length >= ConfigValue.TerritoryFlagCountOwn)
		{
			player.sendMessage("Внимание! Клан не может иметь больше "+ConfigValue.TerritoryFlagCountOwn+" флагов.");
			return;
		}

		decayMe();

		if(_returnTerritoryFlagTask != null)
		{
			_returnTerritoryFlagTask.cancel(false);
			_returnTerritoryFlagTask = null;
		}
		L2ItemInstance item = ItemTemplates.getInstance().createItem(_itemId);
		item.setCustomFlags(L2ItemInstance.FLAG_EQUIP_ON_PICKUP | L2ItemInstance.FLAG_NO_DESTROY | L2ItemInstance.FLAG_NO_TRADE | L2ItemInstance.FLAG_NO_UNEQUIP, false);
		player.getInventory().addItem(item, false, true, false);
		player.getInventory().equipItem(item, false);
		player.sendChanges();
		_item = item;
		player.sendPacket(Msg.YOU_VE_ACQUIRED_THE_WARD_MOVE_QUICKLY_TO_YOUR_FORCES__OUTPOST);
		String terrName = CastleManager.getInstance().getCastleByIndex(_baseTerritoryId).getName();
		TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.THE_S1_WARD_HAS_BEEN_DESTROYED_C2_NOW_HAS_THE_TERRITORY_WARD).addString(terrName).addName(player), true);

		if(ConfigValue.ReturnTerritoryFlag > 0)
			_returnTerritoryFlagTask2 = ThreadPoolManager.getInstance().schedule(new ReturnTerritoryFlagTask(player), ConfigValue.ReturnTerritoryFlag*1000L);

	}

	public void drop(L2Player player)
	{
		if(_returnTerritoryFlagTask2 != null)
		{
			_returnTerritoryFlagTask2.cancel(false);
			_returnTerritoryFlagTask2 = null;
		}
		if(player != null)
		{
			_item.setCustomFlags(0, false);
			player.getInventory().destroyItem(_item, 1, false);
			_item = null;
			setXYZInvisible(Location.findPointToStay(player.getLoc().x, player.getLoc().y, player.getLoc().z, ConfigValue.TerritoryFlagMinDistToDrop, ConfigValue.TerritoryFlagMaxDistToDrop, player.getGeoIndex()));
			//setXYZInvisible(player.getLoc().rnd(ConfigValue.TerritoryFlagMinDistToDrop, ConfigValue.TerritoryFlagMaxDistToDrop, false));
			spawnMe();
			TerritorySiege.setWardLoc(_baseTerritoryId, getLoc());
			player.broadcastUserInfo(true);
		}
		if(_returnTerritoryFlagTask != null)
		{
			_returnTerritoryFlagTask.cancel(false);
			_returnTerritoryFlagTask = null;
		}
		if(ConfigValue.ReturnDropedTerritoryFlag > -1)
			_returnTerritoryFlagTask = ThreadPoolManager.getInstance().schedule(new ReturnTerritoryFlagTaskDroped(this), ConfigValue.ReturnDropedTerritoryFlag*1000);
	}

	public void returnToCastle(L2Player player)
	{
		if(player != null)
		{
			if(_item != null)
			{
				_item.setCustomFlags(0, false);
				player.getInventory().destroyItem(_item, 1, false);
				_item = null;
			}
			TerritorySiege.removeFlag(this);
			TerritorySiege.spawnFlags(_baseTerritoryId); // Заспавнит только нужный нам флаг в замке
			TerritorySiege.setWardLoc(_baseTerritoryId, getLoc());
			player.sendPacket(Msg.THE_EFFECT_OF_TERRITORY_WARD_IS_DISAPPEARING);
			player.broadcastUserInfo(true);
		}
		else
		{
			if(_item != null)
				_item.setCustomFlags(0, false);
			_item = null;
			TerritorySiege.removeFlag(this);
			TerritorySiege.spawnFlags(_baseTerritoryId); // Заспавнит только нужный нам флаг в замке
			TerritorySiege.setWardLoc(_baseTerritoryId, getLoc());
		}
	}

	public void engrave(L2Player player)
	{
		if(player != null)
		{
			_item.setCustomFlags(0, false);
			player.getInventory().destroyItem(_item, 1, false);
			_item = null;
			if(CastleManager.getInstance().getCastleByIndex(player.getClan().getHasCastle()).getFlags().length >= ConfigValue.TerritoryFlagCountOwn)
			{
				player.broadcastUserInfo(true);
				TerritorySiege.removeFlag(this);
				TerritorySiege.spawnFlags(getBaseTerritoryId()); // Заспавнит только нужный нам флаг в замке
				player.sendMessage("Внимание! Клан не может иметь больше "+ConfigValue.TerritoryFlagCountOwn+" флагов.");
			}
			else
			{
				Castle oldOwner = CastleManager.getInstance().getCastleByIndex(_currentTerritoryId);
				oldOwner.removeFlag(_baseTerritoryId);

				Castle newOwner = CastleManager.getInstance().getCastleByIndex(player.getTerritorySiege());
				newOwner.addFlag(_baseTerritoryId);

				TerritorySiege.removeFlag(this);
				TerritorySiege.spawnFlags(_baseTerritoryId); // Заспавнит только нужный нам флаг в замке
				TerritorySiege.setWardLoc(_baseTerritoryId, getLoc());

				player.sendPacket(Msg.THE_EFFECT_OF_TERRITORY_WARD_IS_DISAPPEARING);
				String terrName = CastleManager.getInstance().getCastleByIndex(_baseTerritoryId).getName();
				TerritorySiege.announceToPlayer(new SystemMessage(SystemMessage.CLAN_S1_HAS_SUCCEEDED_IN_CAPTURING_S2_S_TERRITORY_WARD).addString(player.getClan().getName()).addString(terrName), true);
			}

			deleteMe();
		}
	}

	public boolean isParalyzeImmune()
	{
		return true;
	}

	public boolean isFearImmune()
	{
		return true;
	}

	public boolean isRootImmune()
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<L2TerritoryFlagInstance> getRef()
	{
		return (HardReference<L2TerritoryFlagInstance>) super.getRef();
	}
}