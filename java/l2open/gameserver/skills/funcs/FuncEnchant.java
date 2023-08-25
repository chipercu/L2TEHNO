package l2open.gameserver.skills.funcs;

import l2open.config.ConfigValue;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.*;
import l2open.gameserver.templates.L2Item.Grade;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.pts.loader.ArmorEnchantBonusData;

import java.util.logging.Logger;

public class FuncEnchant extends Func
{
	public FuncEnchant(Stats stat, int order, Object owner, double value)
	{
		super(stat, order, owner);
	}
	private static final Logger _log = Logger.getLogger(FuncEnchant.class.getName());

	@Override
	public void calc(Env env)
	{
		L2ItemInstance item = (L2ItemInstance) _funcOwner;

		if(item.getItem().getCrystalType() == Grade.NONE && item.getItemId() != 21580 && item.getItemId() != 21706 || item.getItem().isCloak())
			return;

		int enchant = item.getEnchantLevel();
		int overenchant = Math.max(0, enchant - 3);

		switch(_stat)
		{
			case SHIELD_DEFENCE:
			case p_magical_defence:
			case p_physical_defence:
			{
				env.value += enchant + overenchant * 2;
				return;
			}
			case p_max_hp:
			{
				int[] enchant_bonus = ArmorEnchantBonusData.bonus_grade[item.getItem().getCrystalType().externalOrdinal];
				int bonus_add = enchant_bonus[Math.max(Math.min(enchant_bonus.length, enchant)-1, 0)];
				if(item.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR)
					bonus_add = (int)(bonus_add*ArmorEnchantBonusData.onepiece_factor+0.5f);
				env.value += bonus_add;
				return;
			}
			case p_magical_attack:
			{
				switch(item.getItem().getCrystalType().cry)
				{
					case L2Item.CRYSTAL_S:
						env.value += 4 * (enchant + overenchant);
						break;
					case L2Item.CRYSTAL_A:
						env.value += 3 * (enchant + overenchant);
						break;
					case L2Item.CRYSTAL_B:
						env.value += 3 * (enchant + overenchant);
						break;
					case L2Item.CRYSTAL_C:
						env.value += 3 * (enchant + overenchant);
						break;
					case L2Item.CRYSTAL_D:
						env.value += 2 * (enchant + overenchant);
						break;
				}
				return;
			}
			case p_physical_attack:
			{
				Enum itemType = item.getItemType();
				boolean isBow = itemType == WeaponType.BOW;
				boolean isCrossBow = itemType == WeaponType.CROSSBOW;
				boolean isSword = (itemType == WeaponType.DUALFIST || itemType == WeaponType.DUAL || itemType == WeaponType.BIGSWORD || itemType == WeaponType.SWORD || itemType == WeaponType.RAPIER || itemType == WeaponType.ANCIENTSWORD) && item.getItem().getBodyPart() == L2Item.SLOT_LR_HAND;
				switch(item.getItem().getCrystalType().cry)
				{
					case L2Item.CRYSTAL_S:
						if(isBow)
							env.value += 10 * (enchant + overenchant);
						else if(isCrossBow)
							env.value += 7 * (enchant + overenchant);
						else if(isSword)
							env.value += 6 * (enchant + overenchant);
						else
							env.value += 5 * (enchant + overenchant);
						//_log.info("S_POWER_ATTACK("+((L2Weapon)item.getItem()).getPDamage()+"): +"+env.value+" enchant="+enchant+" overenchant="+overenchant);
						break;
					case L2Item.CRYSTAL_A:
						if(isBow)
							env.value += 8 * (enchant + overenchant);
						else if(isCrossBow)
							env.value += 6 * (enchant + overenchant);
						else if(isSword)
							env.value += 5 * (enchant + overenchant);
						else
							env.value += 4 * (enchant + overenchant);
						//_log.info("A_POWER_ATTACK("+((L2Weapon)item.getItem()).getPDamage()+"): +"+env.value+" enchant="+enchant+" overenchant="+overenchant);
						break;
					case L2Item.CRYSTAL_B:
					case L2Item.CRYSTAL_C:
						if(isBow)
							env.value += 6 * (enchant + overenchant);
						else if(isCrossBow)
							env.value += 5 * (enchant + overenchant);
						else if(isSword)
							env.value += 4 * (enchant + overenchant);
						else
							env.value += 3 * (enchant + overenchant);
						//_log.info("BC_POWER_ATTACK("+((L2Weapon)item.getItem()).getPDamage()+"): +"+env.value+" enchant="+enchant+" overenchant="+overenchant);
						break;
					case L2Item.CRYSTAL_D:
						if(isBow)
							env.value += 4 * (enchant + overenchant);
						else if(isCrossBow)
							env.value += 3 * (enchant + overenchant);
						else
							env.value += 2 * (enchant + overenchant);
						//_log.info("D_POWER_ATTACK("+((L2Weapon)item.getItem()).getPDamage()+"): +"+env.value+" enchant="+enchant+" overenchant="+overenchant);
						break;
				}
				return;
			}
		}
	}
}