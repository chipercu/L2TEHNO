package l2open.gameserver.skills.skillclasses;

import l2open.config.*;
import l2open.extensions.multilang.CustomMessage;
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
import l2open.util.Rnd;

public class Harvesting extends L2Skill
{
	public Harvesting(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!activeChar.isPlayer())
			return;

		L2Player player = (L2Player) activeChar;

		for(L2Character target : targets)
			if(target != null)
			{
				if(!target.isMonster())
					continue;

				L2MonsterInstance monster = (L2MonsterInstance) target;

				// Не посеяно
				if(!monster.isSeeded())
				{
					activeChar.sendPacket(Msg.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
					continue;
				}

				if(!monster.isSeeded(player))
				{
					activeChar.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
					continue;
				}

				double SuccessRate = ConfigValue.BasePercentChanceOfHarvestingSuccess;
				int diffPlayerTarget = Math.abs(activeChar.getLevel() - monster.getLevel());

				// Штраф, на разницу уровней между мобом и игроком
				// 5% на каждый уровень при разнице >5 - по умолчанию
				if(diffPlayerTarget > ConfigValue.MinDiffPlayerMob)
					SuccessRate -= (diffPlayerTarget - ConfigValue.MinDiffPlayerMob) * ConfigValue.DiffPlayerMobPenalty;

				// Минимальный шанс успеха всегда 1%
				if(SuccessRate < 1)
					SuccessRate = 1;

				if(ConfigValue.SkillsShowChance && !player.getVarB("SkillsHideChance"))
					player.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Harvesting.Chance", player).addNumber((long) SuccessRate));

				if(!Rnd.chance(SuccessRate))
				{
					activeChar.sendPacket(Msg.THE_HARVEST_HAS_FAILED);
					monster.takeHarvest();
					continue;
				}

				L2ItemInstance item = monster.takeHarvest();
				if(item == null)
				{
					System.out.println("Harvesting :: monster.takeHarvest() == null :: monster == " + monster);
					continue;
				}

				long itemCount = item.getCount();
				item = player.getInventory().addItem(item);
				Log.LogItem(player, target, Log.HarvesterItem, item);
				player.sendPacket(new SystemMessage(SystemMessage.S1_HARVESTED_S3_S2_S).addString("You").addNumber(itemCount).addItemName(item.getItemId()));
				if(player.isInParty())
				{
					SystemMessage smsg = new SystemMessage(SystemMessage.S1_HARVESTED_S3_S2_S).addString(player.getName()).addNumber(itemCount).addItemName(item.getItemId());
					player.getParty().broadcastToPartyMembers(player, smsg);
				}
			}
	}
}