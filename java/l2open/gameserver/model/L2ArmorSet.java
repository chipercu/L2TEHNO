package l2open.gameserver.model;

import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.util.GArray;

import java.util.ArrayList;

public final class L2ArmorSet {
	private final int _chest;
	private final GArray<Integer> _legs = new GArray<Integer>();
	private final GArray<Integer> _head = new GArray<Integer>();
	private final GArray<Integer> _gloves = new GArray<Integer>();
	private final GArray<Integer> _feet = new GArray<Integer>();
	private final GArray<Integer> _shield = new GArray<Integer>();

	private final L2Skill _skill;
	private final L2Skill _shieldSkill;
	private final L2Skill _enchant6skill;

    public L2ArmorSet(int chest, ArrayList<Integer> legs, ArrayList<Integer> head, ArrayList<Integer> gloves, ArrayList<Integer> feet, L2Skill skill, ArrayList<Integer> shield, L2Skill shield_skill, L2Skill enchant6skill) {
		_chest = chest;
		int[] analog;
        _legs.addAll(legs);
        _head.addAll(head);
        _gloves.addAll(gloves);
        _feet.addAll(feet);
        _shield.addAll(shield);

		_skill = skill;
		_shieldSkill = shield_skill;
		_enchant6skill = enchant6skill;
    }

    /**
	 * Checks if player have equipped all items from set (not checking shield)
	 * @param player whose inventory is being checked
	 * @return True if player equips whole set
	 */
	public boolean containAll(L2Player player)
	{
		Inventory inv = player.getInventory();

		L2ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		L2ItemInstance headItem = inv.getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		L2ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		L2ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

		int legs = 0;
		int head = 0;
		int gloves = 0;
		int feet = 0;

		if(legsItem != null)
			legs = legsItem.getItemId();
		if(headItem != null)
			head = headItem.getItemId();
		if(glovesItem != null)
			gloves = glovesItem.getItemId();
		if(feetItem != null)
			feet = feetItem.getItemId();

		return containAll(_chest, legs, head, gloves, feet);

	}

	public boolean containAll(int chest, int legs, int head, int gloves, int feet)
	{
		if(_chest != 0 && _chest != chest)
			return false;
		if(!_legs.isEmpty() && !_legs.contains(legs))
			return false;
		if(!_head.isEmpty() && !_head.contains(head))
			return false;
		if(!_gloves.isEmpty() && !_gloves.contains(gloves))
			return false;
		if(!_feet.isEmpty() && !_feet.contains(feet))
			return false;

		return true;
	}

	public boolean containItem(int slot, int itemId)
	{
		switch(slot)
		{
			case Inventory.PAPERDOLL_CHEST:
				return _chest == itemId;
			case Inventory.PAPERDOLL_LEGS:
				return _legs.contains(itemId);
			case Inventory.PAPERDOLL_HEAD:
				return _head.contains(itemId);
			case Inventory.PAPERDOLL_GLOVES:
				return _gloves.contains(itemId);
			case Inventory.PAPERDOLL_FEET:
				return _feet.contains(itemId);
			default:
				return false;
		}
	}

	public L2Skill getSkill()
	{
		return _skill;
	}

	public boolean containShield(L2Player player)
	{
		Inventory inv = player.getInventory();

		L2ItemInstance shieldItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if(shieldItem != null && _shield.contains(shieldItem.getItemId()))
			return true;

		return false;
	}

	public boolean containShield(int shield_id)
	{
		if(_shield.isEmpty())
			return false;

		return _shield.contains(shield_id);
	}

	public L2Skill getShieldSkill()
	{
		return _shieldSkill;
	}

	public L2Skill getEnchant6skill()
	{
		return _enchant6skill;
	}

	/**
	 * Checks if all parts of set are enchanted to +6 or more
	 * @param player
	 * @return
	 */
	public boolean isEnchanted6(L2Player player)
	{
		// Player don't have full set
		if(!containAll(player))
			return false;

		Inventory inv = player.getInventory();

		L2ItemInstance chestItem = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		L2ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		L2ItemInstance headItem = inv.getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		L2ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		L2ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

		if(chestItem.getEnchantLevel() < 6)
			return false;
		if(!_legs.isEmpty() && legsItem.getEnchantLevel() < 6)
			return false;
		if(!_gloves.isEmpty() && glovesItem.getEnchantLevel() < 6)
			return false;
		if(!_head.isEmpty() && headItem.getEnchantLevel() < 6)
			return false;
		if(!_feet.isEmpty() && feetItem.getEnchantLevel() < 6)
			return false;

		return true;
	}
}