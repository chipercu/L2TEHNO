package l2open.gameserver.model.items.listeners;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2ArmorSet;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.xml.loader.XmlArmorsetLoader;
import l2open.util.Util;

import java.util.logging.Logger;

public final class ArmorSetListener implements PaperdollListener
{
	protected static final Logger _log = Logger.getLogger(ArmorSetListener.class.getName());
	private static final L2Skill COMMON_SET_SKILL = SkillTable.getInstance().getInfo(3006, 1);

	private Inventory _inv;

	public ArmorSetListener(Inventory inv)
	{
		_inv = inv;
	}

	public void notifyEquipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer() || !item.isEquipable())
			return;

		L2Player player = _inv.getOwner().getPlayer();

		// checks if player worns chest item
		L2ItemInstance chestItem = _inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if(chestItem == null)
			return;

		// checks if there is armorset for chest item that player worns
		L2ArmorSet armorSet = XmlArmorsetLoader.getInstance().getSet(chestItem.getItemId());
		if(armorSet == null)
			return;

		// checks if equipped item is part of set
		if(armorSet.containItem(slot, item.getItemId()))
		{
			if(armorSet.containAll(player))
			{
				L2Skill skill = armorSet.getSkill();
				if(skill != null)
				{
					player.addSkill(SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()), false);
					player.addSkill(SkillTable.getInstance().getInfo(3006, 1), false);
				}

				if(armorSet.containShield(player)) // has shield from set
				{
					L2Skill skills = armorSet.getShieldSkill();
					if(skills != null)
						player.addSkill(SkillTable.getInstance().getInfo(skills.getId(), skills.getLevel()), false);
				}
				if(armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
				{
					L2Skill skills = armorSet.getEnchant6skill();
					if(skills != null)
						player.addSkill(SkillTable.getInstance().getInfo(skills.getId(), skills.getLevel()), false);
				}
			}
		}
		else if(armorSet.containShield(item.getItemId()))
			if(armorSet.containAll(player))
			{
				L2Skill skills = armorSet.getShieldSkill();
				if(skills != null)
					player.addSkill(SkillTable.getInstance().getInfo(skills.getId(), skills.getLevel()), false);
			}
	}

	public void notifyUnequipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer() || !item.isEquipable())
			return;

		boolean remove = false;
		L2Skill removeSkillId1 = null; // set skill
		L2Skill removeSkillId2 = null; // shield skill
		L2Skill removeSkillId3 = null; // enchant +6 skill

		if(slot == Inventory.PAPERDOLL_CHEST)
		{
			L2ArmorSet armorSet = XmlArmorsetLoader.getInstance().getSet(item.getItemId());
			if(armorSet == null)
				return;

			remove = true;
			removeSkillId1 = armorSet.getSkill();
			removeSkillId2 = armorSet.getShieldSkill();
			removeSkillId3 = armorSet.getEnchant6skill();

		}
		else
		{
			L2ItemInstance chestItem = _inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if(chestItem == null)
				return;

			L2ArmorSet armorSet = XmlArmorsetLoader.getInstance().getSet(chestItem.getItemId());
			if(armorSet == null)
				return;

			if(armorSet.containItem(slot, item.getItemId())) // removed part of set
			{
				remove = true;
				removeSkillId1 = armorSet.getSkill();
				removeSkillId2 = armorSet.getShieldSkill();
				removeSkillId3 = armorSet.getEnchant6skill();
			}
			else if(armorSet.containShield(item.getItemId())) // removed shield
			{
				remove = true;
				removeSkillId2 = armorSet.getShieldSkill();
			}
		}

		L2Player player = _inv.getOwner().getPlayer();
		if(remove)
		{
			if(removeSkillId1 != null)
			{
				player.removeSkill(removeSkillId1, false, true);
				player.removeSkill(COMMON_SET_SKILL, false, true);

				// При снятии вещей из состава S80 или S84 сета снимаем плащ
				if(!ConfigValue.CloakUseAllow && !Util.contains(ConfigValue.CloakUseAllowList, item.getItemId()) && !_inv.isRefreshingListeners())
					for(int skill : ConfigValue.SkillsS80andS84Sets)
						if(skill == removeSkillId1.getId())
						{
							_inv.unEquipItemInSlot(Inventory.PAPERDOLL_BACK);
							player.sendPacket(Msg.THE_CLOAK_EQUIP_HAS_BEEN_REMOVED_BECAUSE_THE_ARMOR_SET_EQUIP_HAS_BEEN_REMOVED);
							break;
						}
			}
			if(removeSkillId2 != null)
				player.removeSkill(removeSkillId2, false, true);
			if(removeSkillId3 != null)
				player.removeSkill(removeSkillId3, false, true);
		}
	}
}