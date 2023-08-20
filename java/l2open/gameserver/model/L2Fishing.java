package l2open.gameserver.model;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.instances.L2PenaltyMonsterInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExFishingHpRegen;
import l2open.gameserver.serverpackets.ExFishingStartCombat;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.concurrent.Future;
import java.util.logging.Logger;

public class L2Fishing extends l2open.common.RunnableImpl
{
	protected static Logger _log = Logger.getLogger(L2Fishing.class.getName());
	private L2Player _fisher;
	private int _time;
	private int _stop = 0;
	private int _gooduse = 0;
	private int _anim = 0;
	private int _mode = 0;
	private int _deceptiveMode = 0;
	private Future<?> _fishAItask;
	// Fish datas
	private final int _fishID;
	private final int _fishMaxHP;
	private int _fishCurHP;
	private final double _regenHP;
	private final boolean _isUpperGrade;

	public void runImpl()
	{
		L2Player fisher = _fisher;
		if(fisher == null)
			return;
		if(_fishCurHP >= _fishMaxHP * 2)
		{
			// The fish got away
			fisher.sendPacket(Msg.THE_FISH_GOT_AWAY);
			doDie(false);
		}
		else if(_time <= 0)
		{
			// Time is up, so that fish got away
			fisher.sendPacket(Msg.TIME_IS_UP_SO_THAT_FISH_GOT_AWAY);
			doDie(false);
		}
		else
			AiTask();
	}

	// =========================================================
	public L2Fishing(L2Player fisher, FishData fish, boolean isNoob, boolean isUpperGrade)
	{
		_fisher = fisher;

		_fishMaxHP = fish.getHP();
		_fishCurHP = _fishMaxHP;
		_regenHP = fish.getHpRegen();
		_fishID = fish.getId();
		_time = fish.getCombatTime() / 1000;
		_isUpperGrade = isUpperGrade;
		int lureType;
		if(isUpperGrade)
		{
			_deceptiveMode = Rnd.chance(10) ? 1 : 0;
			lureType = 2;
		}
		else
		{
			_deceptiveMode = 0;
			lureType = isNoob ? 0 : 1;
		}
		_mode = Rnd.chance(20) ? 1 : 0;

		ExFishingStartCombat efsc = new ExFishingStartCombat(fisher, _time, _fishMaxHP, _mode, lureType, _deceptiveMode);
		fisher.broadcastPacket(efsc);

		// Succeeded in getting a bite
		fisher.sendPacket(Msg.SUCCEEDED_IN_GETTING_A_BITE);

		if(_fishAItask == null)
			_fishAItask = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000, 1000);

	}

	public void changeHp(int hp, int pen)
	{
		_fishCurHP -= hp;
		if(_fishCurHP < 0)
			_fishCurHP = 0;

		L2Player fisher = _fisher;
		if(fisher != null)
			fisher.broadcastPacket(new ExFishingHpRegen(fisher, _time, _fishCurHP, _mode, _gooduse, _anim, pen, _deceptiveMode));

		_gooduse = 0;
		_anim = 0;
		if(_fishCurHP > _fishMaxHP * 2)
		{
			_fishCurHP = _fishMaxHP * 2;
			doDie(false);
		}
		else if(_fishCurHP == 0)
			doDie(true);
	}

	public void doDie(boolean win)
	{
		if(_fishAItask != null)
		{
			_fishAItask.cancel(false);
			_fishAItask = null;
		}

		L2Player fisher = _fisher;
		if(fisher == null)
			return;

		if(win && fisher.getLure() != null)
		{
			if(fisher.getLure().getItemId() == 8548)
			{
				for(int i = 0; i < 9; i++)
				{
					L2Zone zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.poison, 204 + i, false);
					if(fisher.isInZone(zone))
					{
						if(Rnd.chance(55))
						{
							L2ItemInstance item = ItemTemplates.getInstance().createItem(8547);
							fisher.getInventory().addItem(item);
							fisher.sendPacket(SystemMessage.obtainItems(8547, 1, 0));
						}
						else if(!fisher.isInVehicle() && Rnd.chance(5))
							penaltyMonster();
					}
				}
			}
			else if(!fisher.isInVehicle() && Rnd.chance(5))
				penaltyMonster();
			else
			{
				L2ItemInstance item = ItemTemplates.getInstance().createItem(_fishID);
				fisher.getInventory().addItem(item);
				fisher.sendPacket(Msg.SUCCEEDED_IN_FISHING, new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1).addItemName(_fishID));
			}
		}
		fisher.endFishing(win);

		_fisher = null;
	}

	protected void AiTask()
	{
		_time--;

		if(_mode == 1 && _deceptiveMode == 0 || _mode == 0 && _deceptiveMode == 1)
			_fishCurHP += (int) _regenHP;

		if(_stop == 0)
		{
			_stop = 1;
			if(Rnd.chance(30))
				_mode = _mode == 0 ? 1 : 0;

			if(_isUpperGrade)
				if(Rnd.chance(10))
					_deceptiveMode = _deceptiveMode == 0 ? 1 : 0;
		}
		else
			_stop--;

		ExFishingHpRegen efhr = new ExFishingHpRegen(_fisher, _time, _fishCurHP, _mode, 0, _anim, 0, _deceptiveMode);
		if(_anim != 0)
			_fisher.broadcastPacket(efhr);
		else
			_fisher.sendPacket(efhr);
	}

	public void UseRealing(int dmg, int pen)
	{
		L2Player fisher = _fisher;
		if(fisher == null)
			return;
		_anim = 2;
		if(Rnd.chance(10))
		{
			fisher.sendPacket(Msg.FISH_HAS_RESISTED);
			_gooduse = 0;
			changeHp(0, pen);
			return;
		}
		if(_mode == 1)
		{
			if(_deceptiveMode == 0)
			{
				// Reeling is successful, Damage: $s1
				fisher.sendPacket(new SystemMessage(SystemMessage.REELING_IS_SUCCESSFUL_DAMAGE_S1).addNumber(dmg));
				if(pen == 50)
					fisher.sendPacket(new SystemMessage(SystemMessage.YOUR_REELING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addNumber(pen));

				_gooduse = 1;
				changeHp(dmg, pen);
			}
			else
			{
				// Reeling failed, Damage: $s1
				fisher.sendPacket(new SystemMessage(SystemMessage.REELING_FAILED_DAMAGE_S1).addNumber(dmg));
				_gooduse = 2;
				changeHp(-dmg, pen);
			}
		}
		else if(_deceptiveMode == 0)
		{
			// fisher failed, Damage: $s1
			fisher.sendPacket(new SystemMessage(SystemMessage.REELING_FAILED_DAMAGE_S1).addNumber(dmg));
			_gooduse = 2;
			changeHp(-dmg, pen);
		}
		else
		{
			// Reeling is successful, Damage: $s1
			fisher.sendPacket(new SystemMessage(SystemMessage.REELING_IS_SUCCESSFUL_DAMAGE_S1).addNumber(dmg));
			if(pen == 50)
				fisher.sendPacket(new SystemMessage(SystemMessage.REELING_IS_SUCCESSFUL_DAMAGE_S1).addNumber(pen));

			_gooduse = 1;
			changeHp(dmg, pen);
		}
	}

	public void UsePomping(int dmg, int pen)
	{
		L2Player fisher = _fisher;
		if(fisher == null)
			return;
		_anim = 1;
		if(Rnd.chance(10))
		{
			fisher.sendPacket(Msg.FISH_HAS_RESISTED);
			_gooduse = 0;
			changeHp(0, pen);
			return;
		}
		if(_mode == 0)
		{
			if(_deceptiveMode == 0)
			{
				// Pumping is successful. Damage: $s1
				fisher.sendPacket(new SystemMessage(SystemMessage.PUMPING_IS_SUCCESSFUL_DAMAGE_S1).addNumber(dmg));
				if(pen == 50)
					fisher.sendPacket(new SystemMessage(SystemMessage.YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addNumber(pen));

				_gooduse = 1;
				changeHp(dmg, pen);
			}
			else
			{
				// Pumping failed, Regained: $s1
				fisher.sendPacket(new SystemMessage(SystemMessage.PUMPING_FAILED_DAMAGE_S1).addNumber(dmg));
				_gooduse = 2;
				changeHp(-dmg, pen);
			}
		}
		else if(_deceptiveMode == 0)
		{
			// Pumping failed, Regained: $s1
			fisher.sendPacket(new SystemMessage(SystemMessage.PUMPING_FAILED_DAMAGE_S1).addNumber(dmg));
			_gooduse = 2;
			changeHp(-dmg, pen);
		}
		else
		{
			// Pumping is successful. Damage: $s1
			fisher.sendPacket(new SystemMessage(SystemMessage.PUMPING_IS_SUCCESSFUL_DAMAGE_S1).addNumber(dmg));
			if(pen == 50)
				fisher.sendPacket(new SystemMessage(SystemMessage.YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addNumber(pen));

			_gooduse = 1;
			changeHp(dmg, pen);
		}
	}

	private void penaltyMonster()
	{
		L2Player fisher = _fisher;
		if(fisher == null)
			return;

		int npcid = 18319 + Math.min(fisher.getLevel() / 11, 7); // 18319-18326
		L2NpcTemplate temp = NpcTable.getTemplate(npcid);
		if(temp != null)
		{
			fisher.sendPacket(Msg.YOU_HAVE_CAUGHT_A_MONSTER);
			try
			{
				Location def = fisher.getFishLoc();
				if(!GeoEngine.canMoveWithCollision(fisher.getX(), fisher.getY(), fisher.getZ(), def.x, def.y, def.z, fisher.getReflection().getGeoIndex()))
					def = fisher.getLoc();

				L2PenaltyMonsterInstance npc = new L2PenaltyMonsterInstance(IdFactory.getInstance().getNextId(), temp);
				npc.setSpawnedLoc(def);
				npc.setReflection(fisher.getReflection());
				npc.setHeading(fisher.getHeading() - 32768);
				npc.onSpawn();
				npc.spawnMe(npc.getSpawnedLoc());
				npc.SetPlayerToKill(fisher);
			}
			catch(Exception e)
			{
				_log.warning("Could not spawn Penalty Monster " + npcid + ", exception: " + e);
				e.printStackTrace();
			}
		}
		else
			_log.warning("Wrong NpcTemplate for Penalty Monster:" + npcid);
	}
}