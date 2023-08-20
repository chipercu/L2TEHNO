package l2open.gameserver.model.items.listeners;

import l2open.config.*;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.triggers.TriggerInfo;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2EtcItem;
import l2open.util.GArray;

import java.util.HashMap;

/**
 * Добавление/удалениe скилов, прописанных предметам в sql или в xml.
 */
public final class ItemSkillsListener implements PaperdollListener
{
	private Inventory _inv;

	public ItemSkillsListener(Inventory inv)
	{
		_inv = inv;
	}

	public void notifyUnequipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer())
			return;

		L2Player player = _inv.getOwner().getPlayer();
		L2Skill[] itemSkills = null;
		//L2Skill[][] enchantSkill = null;
		HashMap<Integer, L2Skill> enchantSkill = null;
		TriggerInfo[] triggers = null;

		L2Item it = item.getItem();
		if(it.getUnequipSkill() != null)
			try
			{
				GArray<L2Character> targets = new GArray<L2Character>();
				targets.add(player);
				player.callSkill(it.getUnequipSkill(), targets, false);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		
		try
		{
			itemSkills = it.getAttachedSkills();

			enchantSkill = it.getEnchant4Skill();

			triggers = it.getAttachedTriggers();

			if(itemSkills != null)
			{
				for(L2Skill itemSkill : itemSkills)
				{
					if(itemSkill.getId() >= 26046 && itemSkill.getId() <= 26048)
					{
						int level = player.getSkillLevel(itemSkill.getId());
						int newlevel = level - 1;
						if(newlevel > 0)
							player.addSkill(SkillTable.getInstance().getInfo(itemSkill.getId(), newlevel), false);
						else
							player.removeSkillById(itemSkill.getId(), false);
					}
					else
					{
						//if(it instanceof L2EtcItem && it.isStackable() && !itemSkill.isPassive())
						//	continue;
						player.removeSkill(itemSkill, false, false);
					}
					if(itemSkill.getNpcId() == player.getTransformationTemplate() && itemSkill.isTransformation())
					{
						player.setTransformation(0);
						for(L2Skill sk : it.getAttachedSkills())
							if(player.getEffectList().getEffectsBySkill(sk) != null)
							{
								for(L2Effect ef : player.getEffectList().getEffectsBySkill(sk))
									ef.exit(false, false);
							}
					}
				}
				if(update_icon)
					player.updateEffectIcons();
			}


			if(triggers != null)
				player.removeTrigger(triggers);

			if(enchantSkill != null)
			{
				/*for(L2Skill sk[]: enchantSkill)
					if(sk != null)
						for(L2Skill s2k : sk)
							if(s2k != null)
								player.removeSkill(s2k, false, true);*/
				for(L2Skill s2k : enchantSkill.values())
					if(s2k != null)
						player.removeSkill(s2k, false, false);
				if(update_icon)
					player.updateEffectIcons();
			}
		}
		finally
		{
			itemSkills = null;
			enchantSkill = null;
			triggers = null;
		}
	}

	public void notifyEquipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer())
			return;

		L2Player player = _inv.getOwner().getPlayer();
		L2Skill[] itemSkills = null;
		//L2Skill[][] enchantSkill = null;
		HashMap<Integer, L2Skill> enchantSkill = null;
		TriggerInfo[] _triggers = null;

		try
		{
			L2Item it = item.getItem();

			itemSkills = it.getAttachedSkills();
			_triggers = it.getAttachedTriggers();

			if(_triggers != null)
				player.addTrigger(_triggers);

			enchantSkill = it.getEnchant4Skill();

			// Для оружия и бижутерии, при несоотвествии грейда скилы не выдаем
			switch (it.getType2())
			{
				case L2Item.TYPE2_WEAPON:
					if(player.getWeaponsExpertisePenalty() > 0)
					{
						itemSkills = null;
						enchantSkill = null;
					}
					break;
				case L2Item.TYPE2_ACCESSORY:
					if(player.getArmorExpertisePenalty() > 0 && item.getCrystalType().ordinal() > player.expertiseIndex)
					{
						itemSkills = null;
						enchantSkill = null;
					}
					break;
			}

			if(itemSkills != null && itemSkills.length > 0)
				for(L2Skill itemSkill : itemSkills)
					if(itemSkill.getId() >= 26046 && itemSkill.getId() <= 26048)
					{
						int level = player.getSkillLevel(itemSkill.getId());
						int newlevel = level;
						if(level > 0)
						{
							if(SkillTable.getInstance().getInfo(itemSkill.getId(), level + 1) != null)
								newlevel = level + 1;
						}
						else
							newlevel = 1;
						if(newlevel != level)
							player.addSkill(SkillTable.getInstance().getInfo(itemSkill.getId(), newlevel), false);
					}
					else if(player.getSkillLevel(itemSkill.getId()) < itemSkill.getLevel())
					{
						//if(it instanceof L2EtcItem && it.isStackable() && !itemSkill.isPassive())
						//	continue;
						// делаем так, что бы работал релоад скилов...
						player.addSkill(SkillTable.getInstance().getInfo(itemSkill.getId(), itemSkill.getLevel()), false);
						if(itemSkill.isActive())
						{
							long reuseDelay = it.getReuseDelayOnEquip();
							if(reuseDelay > 0 && !player.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? itemSkill.getId()*65536L+itemSkill.getLevel() : itemSkill.getId()))
								player.disableSkill(itemSkill.getId(), itemSkill.getLevel(), reuseDelay);
						}
					}

			if(enchantSkill != null && item.getEnchantLevel() > 0)
				for(int i=1;i<=item.getEnchantLevel() && i<=(it.getType2() == L2Item.TYPE2_WEAPON ? ConfigValue.EnchantMaxWeapon : ConfigValue.EnchantMaxArmor);i++)
				{
					/*for(L2Skill sk : enchantSkill[i])
						if(sk != null)
							player.addSkill(sk, false);*/
					L2Skill sk = enchantSkill.get(i);
					if(sk != null)
					{
						// делаем так, что бы работал релоад скилов...
						player.addSkill(SkillTable.getInstance().getInfo(sk.getId(), sk.getLevel()), false);
					}
				}
		}
		finally
		{
			itemSkills = null;
			enchantSkill = null;
		}
	}
}