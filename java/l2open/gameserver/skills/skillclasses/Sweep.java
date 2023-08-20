package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Log;

public class Sweep extends L2Skill
{
	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(isNotTargetAoE(activeChar))
			return super.checkCondition(activeChar, target, forceUse, dontMove, first);

		if(target == null)
			return false;

		if(!target.isMonster() || !((L2MonsterInstance) target).isDead() || ((L2MonsterInstance) target).isDying())
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}

		if(!((L2MonsterInstance) target).isSpoiled())
		{
			activeChar.sendPacket(Msg.SWEEPER_FAILED_TARGET_NOT_SPOILED);
			return false;
		}

		if(!((L2MonsterInstance) target).isSpoiled((L2Player) activeChar))
		{
			activeChar.sendPacket(Msg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	public Sweep(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!activeChar.isPlayer())
			return;

		L2Player player = (L2Player) activeChar;

		for(L2Character targ : targets)
		{
			if(targ == null || !targ.isMonster() || !targ.isDead() || !((L2MonsterInstance) targ).isSpoiled())
				continue;

			L2MonsterInstance target = (L2MonsterInstance) targ;

			if(!target.isSpoiled(player))
			{
				activeChar.sendPacket(Msg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
				continue;
			}

			L2ItemInstance[] items = target.takeSweep();

			if(items == null)
			{
				activeChar.getAI().setAttackTarget(null);
				target.endDecayTask();
				continue;
			}

			target.setSpoiled(false, null);

			for(L2ItemInstance item : items)
			{
				if(player.isInParty() && player.getParty().isDistributeSpoilLoot())
				{
					player.getParty().distributeItem(player, item);
					continue;
				}

				long itemCount = item.getCount();
				if(player.getInventoryLimit() <= player.getInventory().getSize() && (!item.isStackable() || player.getInventory().getItemByItemId(item.getItemId()) == null))
				{
					item.dropToTheGround(player, target);
					continue;
				}

				item = player.getInventory().addItem(item);
				Log.LogItem(player, target, Log.SweepItem, item);

				SystemMessage smsg;
				if(itemCount == 1)
				{
					smsg = new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1);
					smsg.addItemName(item.getItemId());
					player.sendPacket(smsg);
				}
				else
				{
					smsg = new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S2_S1);
					smsg.addItemName(item.getItemId());
					smsg.addNumber(itemCount);
					player.sendPacket(smsg);
				}
				if(player.isInParty())
					if(itemCount == 1)
					{
						smsg = new SystemMessage(SystemMessage.S1_HAS_OBTAINED_S2_BY_USING_SWEEPER);
						smsg.addString(player.getName());
						smsg.addItemName(item.getItemId());
						player.getParty().broadcastToPartyMembers(player, smsg);
					}
					else
					{
						smsg = new SystemMessage(SystemMessage.S1_HAS_OBTAINED_3_S2_S_BY_USING_SWEEPER);
						smsg.addString(player.getName());
						smsg.addItemName(item.getItemId());
						smsg.addNumber(itemCount);
						player.getParty().broadcastToPartyMembers(player, smsg);
					}
			}

			activeChar.getAI().setAttackTarget(null);
			target.endDecayTask();
		}
	}
}