package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ShortCut;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.L2EnchantSkillLearn;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.Util;

/**
 * Format chdd
 * c: (id) 0xD0
 * h: (subid) 0x0F
 * d: skill id
 * d: skill lvl
 */
public class RequestExEnchantSkill extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLvl;

	@Override
	public void readImpl()
	{
		_skillId = readD();
		_skillLvl = readD();
	}

	/**
	public static final int YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_ON_THIS_LEVEL_YOU_CAN_USE_THE_CORRESPONDING_FUNCTION = 3026; // На данном уровне вы не можете улучшить умение. Эта функция доступна игрокам, достигшим 76-го уровня.
	public static final int YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_CORRESPONDING_FUNCTION = 3027; // Ваша профессия не позволяет использовать функцию улучшения умения. Она доступна после 3-й смены профессии.
	public static final int YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_THE_SKILL_ENHANCING = 3028; // Ваше состояние не позволяет улучшить умение. Эта функция доступна, если вы не перевоплощены, не сражаетесь и не сидите верхом на ездовом существе.
	**/
	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;
		else if(activeChar.getTransformation() != 0 || activeChar.isMounted() || activeChar.isInCombat() /*|| ConfigValue.MultiProfa && !activeChar.isSubClassActive()*/)
		{
			sendPacket(Msg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_THE_SKILL_ENHANCING);
			return;
		}
		else if(activeChar.getLevel() < 76)
		{
			sendPacket(Msg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_ON_THIS_LEVEL_YOU_CAN_USE_THE_CORRESPONDING_FUNCTION);
			return;
		}
		else if(activeChar.getClassId().getLevel() < 4)
		{
			sendPacket(Msg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_CORRESPONDING_FUNCTION);
			return;
		}

		L2EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
		if(sl == null)
		{
			if(activeChar.isGM())
				activeChar.sendMessage("Error: sl["+_skillId+"]["+_skillLvl+"] == null");
			return;
		}

		short slevel = activeChar.getSkillLevel(_skillId);
		if(slevel == -1)
		{
			if(activeChar.isGM())
				activeChar.sendMessage("Error: slevel == -1");
			return;
		}

		int enchantLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel());

		// already knows the skill with this level
		if(slevel >= enchantLevel)
		{
			if(activeChar.isGM())
				activeChar.sendMessage("Error: slevel["+slevel+"] >= enchantLevel["+enchantLevel+"]");
			return;
		}

		// Можем ли мы перейти с текущего уровня скилла на данную заточку
		if(slevel == sl.getBaseLevel() ? _skillLvl % 100 != 1 : slevel != enchantLevel - 1)
		{
			activeChar.sendMessage("Incorrect enchant level.");
			return;
		}

		L2Skill skill = SkillTable.getInstance().getInfo(_skillId, enchantLevel);
		if(skill == null)
		{
			activeChar.sendMessage("Internal error: not found skill level");
			return;
		}

		int[] cost = sl.getCost();
		int requiredSp = cost[1] * ConfigValue.NORMAL_ENCHANT_COST_MULTIPLIER * sl.getCostMult();
		int requiredAdena = cost[0] * ConfigValue.NORMAL_ENCHANT_COST_MULTIPLIER * sl.getCostMult();
		int rate = sl.getRate(activeChar);

		if(activeChar.getSp() < requiredSp)
		{
			sendPacket(Msg.SP_REQUIRED_FOR_SKILL_ENCHANT_IS_INSUFFICIENT);
			return;
		}

		if(activeChar.getAdena() < requiredAdena)
		{
			sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if(_skillLvl % 100 == 1) // only first lvl requires book (101, 201, 301 ...)
		{
			int index = Util.contains_i1(_skillId, ConfigValue.EnchantNormalCustomItem, 0);
			if(index > -1)
			{
				if(Functions.getItemCount(activeChar, ConfigValue.EnchantNormalCustomItem[index][1]) == 0)
				{
					activeChar.sendPacket(Msg.ITEMS_REQUIRED_FOR_SKILL_ENCHANT_ARE_INSUFFICIENT);
					return;
				}
				Functions.removeItem(activeChar, ConfigValue.EnchantNormalCustomItem[index][1], 1);
			}
			else
			{
				if(Functions.getItemCount(activeChar, SkillTreeTable.NORMAL_ENCHANT_BOOK) == 0)
				{
					activeChar.sendPacket(Msg.ITEMS_REQUIRED_FOR_SKILL_ENCHANT_ARE_INSUFFICIENT);
					return;
				}
				Functions.removeItem(activeChar, SkillTreeTable.NORMAL_ENCHANT_BOOK, 1);
			}
		}

		if(Rnd.chance(rate))
		{
			activeChar.addExpAndSp(0, -1 * requiredSp, false, false);
			Functions.removeItem(activeChar, 57, requiredAdena);
			activeChar.sendPacket(new SystemMessage(SystemMessage.SP_HAS_DECREASED_BY_S1).addNumber(requiredSp), new SystemMessage(SystemMessage.SUCCEEDED_IN_ENCHANTING_SKILL_S1).addSkillName(_skillId, _skillLvl), new SkillList(activeChar), new ExEnchantSkillResult(1));
			Log.add(activeChar.getName() + "|Successfully enchanted|" + _skillId + "|to+" + _skillLvl + "|" + rate, "enchant_skills");
		}
		else
		{
			activeChar.removeSkill(skill, true, false);
			skill = SkillTable.getInstance().getInfo(_skillId, sl.getBaseLevel());
			activeChar.sendPacket(new SystemMessage(SystemMessage.FAILED_IN_ENCHANTING_SKILL_S1).addSkillName(_skillId, _skillLvl), new ExEnchantSkillResult(0));
			Log.add(activeChar.getName() + "|Failed to enchant|" + _skillId + "|to+" + _skillLvl + "|" + rate, "enchant_skills");
		}
		activeChar.addSkill(skill, true);
		updateSkillShortcuts(activeChar);
		activeChar.sendPacket(new ExEnchantSkillInfo(_skillId, activeChar.getSkillDisplayLevel(_skillId)));
	}

	private void updateSkillShortcuts(L2Player player)
	{
		// update all the shortcuts to this skill
		for(L2ShortCut sc : player.getAllShortCuts())
			if(sc.id == _skillId && sc.type == L2ShortCut.TYPE_SKILL)
			{
				L2ShortCut newsc = new L2ShortCut(sc.slot, sc.page, sc.type, sc.id, _skillLvl);
				player.sendPacket(new ShortCutRegister(newsc));
				player.registerShortCut(newsc);
			}
	}
}