package l2open.gameserver.skills.enums;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 * @author : Diagod
 **/
public enum TargetType
{
	advance_base // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target == null)
				return null;
			else if(!(target instanceof L2SiegeHeadquarterInstance) || ((L2SiegeHeadquarterInstance) target).getNpcId() != 36590 || ((L2SiegeHeadquarterInstance) target).getClan() != activeChar.getPlayer().getClan())
			{
				activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
				return null;
			}
			else if(!activeChar.getPlayer().isTerritoryFlagEquipped())
				return null;

			int diff = activeChar.getZ()-target.getZ();
			// Если цель не в радиусе 150, если цель на 100 выше или ниже, не даем возможность кастануть...
			if(!activeChar.isInRangeZ(target, 150) || diff+100 < 0 || diff-100 > 0)
			{
				activeChar.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
				return null;
			}
			return target;
		}
	},
	artillery // TODO: Fix My! Хз как оно работает, нужно тестить на осадах!!!
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			// return target != null && !target.isDead() && (target.isDoor() || target instanceof L2ControlTowerInstance) ? target : null;
			L2Player own = null;
			if(activeChar instanceof L2Summon)
				own = ((L2Summon)activeChar).getPlayer();
			else
				return target;
			if(target == null || target == activeChar || target == own)
				return null;
			else if(target.isMonster())
				return target;

			L2Player pl = null;
			if(target.isPlayer())
				pl = (L2Player)target;
			else if(target instanceof L2Summon)
				pl = ((L2Summon)target).getPlayer();
			if(pl.isAutoAttackable(own))
				return target;
			return null;
		}
	},
	door_treasure // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target != null && (target instanceof L2DoorInstance || target instanceof L2ChestInstance))
				return target;
			else if(target != null)
				activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return null;
		}
	},
	enemy // TODO: Пересмотреть, а как же проверка на пати/клан/али/команду?
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target == null)
				return null;
			else if(target.isMonster())
				return target;
			else if(target.isAutoAttackable(activeChar) || (ctrl && target.isAttackable(activeChar))/*(ctrl && target != activeChar && !target.isSummon()) || (target.isSummon() && ((L2Summon)target).getPlayer() != activeChar)*/)
				return target;
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return null;
		}
	},
	enemy_not // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target == null)
				return null;
			else if(!activeChar.isPlayer() || target == activeChar)
				return target;
			else if(/*!target.isPlayable() || */target.isAutoAttackable(activeChar))
			{
				activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
				return null;
			}
			return target;
		}
	},
	enemy_only // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target == null)
				return null;
			else if(target.isMonster())
				return target;
			else if(target.isAutoAttackable(activeChar))
				return target;
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return null;
		}
	},
	fortress_flagpole // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target == null)
				return null;
			int diff = activeChar.getZ()-target.getZ();
			// Если цель не в радиусе 150, если цель на 100 выше или ниже, не даем возможность кастануть...
			if(!activeChar.isInRangeZ(target, 150) || diff+100 < 0 || diff-100 > 0)
			{
				activeChar.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
				return null;
			}
			else if(activeChar.isPlayer() && target instanceof L2StaticObjectInstance && ((L2StaticObjectInstance) target).getType() == 3)
				return target;
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return null;
		}
	},
	ground // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			return null; // Пофигу на таргет, это символ...
		}
	},
	holything // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target != null && target.isArtefact())
				return target;
			else if(target != null)
				activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return null;
		}
	},
	item // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target != null && target instanceof L2ItemInstance)
				return target;
			return null;
		}
	},
	none // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			return activeChar;
		}
	},
	npc_body // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target != null && ((L2Character)target).isDead() && (target.isNpc() || target.isSummon()))
				return target;
			else if(target != null)
				activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return null;
		}
	},
	others // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target != null && target == activeChar)
			{
				activeChar.sendPacket(Msg.YOU_CANNOT_USE_THIS_ON_YOURSELF);
				return null;
			}
			return target;
		}
	},
	pc_body // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			if(target != null && ((L2Character)target).isDead() && target.isPlayer()) // isPlayable - уточнить, мб в ХФ можно ресать и петов...
				return target;
			else if(target != null)
				activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return null;
		}
	},
	self // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			return activeChar;
		}
	},
	summon // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			return target != null && (target.isPet() || target.isSummon()) && target.getPlayer() == activeChar ? target : null;
		}
	},
	target // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			return target;
		}
	},
	wyvern_target // +
	{
		@Override
		public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
		{
			return target;
		}
	};

	public L2Object getTarget(L2Object target, L2Character activeChar, boolean ctrl)
	{
		return null;
	}
}