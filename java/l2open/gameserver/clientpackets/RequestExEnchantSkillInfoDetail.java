package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.L2EnchantSkillLearn;
import l2open.gameserver.serverpackets.ExEnchantSkillInfoDetail;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.Util;

public final class RequestExEnchantSkillInfoDetail extends L2GameClientPacket
{
	private static final int TYPE_NORMAL_ENCHANT = 0;
	private static final int TYPE_SAFE_ENCHANT = 1;
	private static final int TYPE_UNTRAIN_ENCHANT = 2;
	private static final int TYPE_CHANGE_ENCHANT = 3;
	private static final int TYPE_IMMORTAL_ENCHANT = 4;

	private int _type;
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		_type = readD();
		_skillId = readD();
		_skillLvl = readD();
	}

	@Override
	protected void runImpl()
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

		int bookId = 0;
		int sp = 0;
		int adenaCount = 0;
		float spMult = ConfigValue.NORMAL_ENCHANT_COST_MULTIPLIER;

		L2EnchantSkillLearn esd = null;

		try
		{
			switch(_type)
			{
				case TYPE_NORMAL_ENCHANT:
					if(_skillLvl % 100 == 1)
					{
						int index = Util.contains_i1(_skillId, ConfigValue.EnchantNormalCustomItem, 0);
						if(index > -1)
							bookId = ConfigValue.EnchantNormalCustomItem[index][1];
						else
							bookId = SkillTreeTable.NORMAL_ENCHANT_BOOK;
					}
					esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
					break;
				case TYPE_SAFE_ENCHANT:
					int index = Util.contains_i1(_skillId, ConfigValue.EnchantSafeCustomItem, 0);
					if(index > -1)
						bookId = ConfigValue.EnchantSafeCustomItem[index][1];
					else
						bookId = SkillTreeTable.SAFE_ENCHANT_BOOK;
					esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
					spMult = SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;
					break;
				case TYPE_UNTRAIN_ENCHANT:
					bookId = SkillTreeTable.UNTRAIN_ENCHANT_BOOK;
					esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl + 1);
					break;
				case TYPE_CHANGE_ENCHANT:
					bookId = SkillTreeTable.CHANGE_ENCHANT_BOOK;
					esd = SkillTreeTable.getEnchantsForChange(_skillId, _skillLvl).get(0);
					spMult = 1f / ConfigValue.SAFE_ENCHANT_COST_MULTIPLIER;
					break;
				case TYPE_IMMORTAL_ENCHANT:
					bookId = 37044;
					spMult = 0f;
					esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
					break;

				default:
					_log.severe("Unknown skill enchant type: " + _type);
					return;
			}
		}
		catch(Exception e)
		{
			_log.severe("RequestExEnchantSkillInfoDetail(84): Unknown skill enchant type: " + _type + " skillId: "+_skillId+" _skillLvl: "+_skillLvl+" char: '"+activeChar.getName()+"'");
		}

		if(esd == null)
			return;

		spMult *= esd.getCostMult();
		int[] cost = esd.getCost();

		sp = (int) (cost[1] * spMult);

		if(_type != TYPE_UNTRAIN_ENCHANT)
			adenaCount = (int) (cost[0] * spMult);

		// send skill enchantment detail
		activeChar.sendPacket(new ExEnchantSkillInfoDetail(_skillId, _skillLvl, sp, esd.getRate(activeChar), bookId, adenaCount));
	}
}