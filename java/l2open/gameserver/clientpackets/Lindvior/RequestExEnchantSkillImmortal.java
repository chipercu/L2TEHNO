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

/**
 * Format (ch) dd
 */
public final class RequestExEnchantSkillImmortal extends L2GameClientPacket
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
			return;

		short slevel = activeChar.getSkillLevel(_skillId);
		if(slevel == -1)
			return;

		int enchantLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel());

		// already knows the skill with this level
		if(slevel >= enchantLevel)
			return;

		// Можем ли мы перейти с текущего уровня скилла на данную заточку
		if(slevel == sl.getBaseLevel() ? _skillLvl % 100 != 1 : slevel != enchantLevel - 1)
		{
			activeChar.sendMessage("Incorrect enchant level.");
			return;
		}

		L2Skill skill = SkillTable.getInstance().getInfo(_skillId, enchantLevel);
		if(skill == null)
			return;
		
		int bookId = 37044;
		if(Functions.getItemCount(activeChar, bookId) == 0)
		{
			activeChar.sendPacket(Msg.ITEMS_REQUIRED_FOR_SKILL_ENCHANT_ARE_INSUFFICIENT);
			return;
		}
		Functions.removeItem(activeChar, bookId, 1);

		activeChar.addSkill(skill, true);
		activeChar.sendPacket(new SystemMessage(SystemMessage.SUCCEEDED_IN_ENCHANTING_SKILL_S1).addSkillName(_skillId, _skillLvl), new ExEnchantSkillResult(1));
		activeChar.sendPacket(new SkillList(activeChar));
		updateSkillShortcuts(activeChar);
		Log.add(activeChar.getName() + "|Successfully immortal enchanted|" + _skillId + "|to+" + _skillLvl + "|100", "enchant_skills");

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