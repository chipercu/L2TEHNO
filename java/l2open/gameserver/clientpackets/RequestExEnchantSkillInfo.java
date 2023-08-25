package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.L2EnchantSkillLearn;
import l2open.gameserver.serverpackets.ExEnchantSkillInfo;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.Util;

public class RequestExEnchantSkillInfo extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLvl;

	@Override
	public void readImpl()
	{
		_skillId = readD();
		_skillLvl = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		String tradeBan = activeChar.getVar("tradeBan");
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
		{
			activeChar.sendMessage("Your trade is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
			return;
		}
		if(_skillLvl > 100)
		{
			L2EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
			if(sl == null)
			{
				activeChar.sendMessage("Not found enchant info for this skill");
				return;
			}

			L2Skill skill = SkillTable.getInstance().getInfo(_skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel()));

			if(skill == null || skill.getId() != _skillId)
			{
				activeChar.sendMessage("This skill doesn't yet have enchant info in Datapack");
				return;
			}

			if(activeChar.getSkillLevel(_skillId) != skill.getLevel())
			{
				activeChar.sendMessage("Skill not found");
				return;
			}
		}
		else if(activeChar.getSkillLevel(_skillId) != _skillLvl)
		{
			activeChar.sendMessage("Skill not found");
			return;
		}

		sendPacket(new ExEnchantSkillInfo(_skillId, _skillLvl));
	}
}