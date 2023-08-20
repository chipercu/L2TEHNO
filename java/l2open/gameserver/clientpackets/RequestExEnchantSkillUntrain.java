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

public final class RequestExEnchantSkillUntrain extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLvl = readD();
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;
		else if(activeChar.getTransformation() != 0 || activeChar.isMounted() || activeChar.isInCombat()/* || ConfigValue.MultiProfa && !activeChar.isSubClassActive()*/)
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

		short oldSkillLevel = activeChar.getSkillDisplayLevel(_skillId);
		if(oldSkillLevel == -1)
			return;

		L2EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, oldSkillLevel);
		if(sl == null)
			return;

		short slevel = activeChar.getSkillLevel(_skillId);
		if(slevel == -1)
			return;

		L2Skill newSkill;

		int enchantLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel());

		if(_skillLvl % 100 == 0)
		{
			_skillLvl = sl.getBaseLevel();
			newSkill = SkillTable.getInstance().getInfo(_skillId, _skillLvl);
		}
		else
		{
			// Можем ли мы перейти с текущего уровня скилла на данную заточку
			if(slevel == sl.getBaseLevel() ? _skillLvl % 100 != 1 : slevel != enchantLevel + 1)
			{
				activeChar.sendMessage("Incorrect enchant level.");
				return;
			}
			newSkill = SkillTable.getInstance().getInfo(_skillId, enchantLevel);
		}
		if(newSkill == null)
			return;

		if(Functions.getItemCount(activeChar, SkillTreeTable.UNTRAIN_ENCHANT_BOOK) == 0)
		{
			activeChar.sendPacket(Msg.ITEMS_REQUIRED_FOR_SKILL_ENCHANT_ARE_INSUFFICIENT);
			return;
		}

		Functions.removeItem(activeChar, SkillTreeTable.UNTRAIN_ENCHANT_BOOK, 1);

		activeChar.addExpAndSp(0, sl.getCost()[1] * sl.getCostMult(), false, false);
		activeChar.addSkill(newSkill, true);

		if(_skillLvl > 100)
		{
			SystemMessage sm = new SystemMessage(SystemMessage.Untrain_of_enchant_skill_was_successful_Current_level_of_enchant_skill_S1_has_been_decreased_by_1);
			sm.addSkillName(_skillId, _skillLvl);
			activeChar.sendPacket(sm);
		}
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessage.Untrain_of_enchant_skill_was_successful_Current_level_of_enchant_skill_S1_became_0_and_enchant_skill_will_be_initialized);
			sm.addSkillName(_skillId, _skillLvl);
			activeChar.sendPacket(sm);
		}

		Log.add(activeChar.getName() + "|Successfully untranes|" + _skillId + "|to+" + _skillLvl + "|---", "enchant_skills");

		activeChar.sendPacket(new ExEnchantSkillInfo(_skillId, newSkill.getDisplayLevel()), new ExEnchantSkillResult(1), new SkillList(activeChar));
		updateSkillShortcuts(activeChar);
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