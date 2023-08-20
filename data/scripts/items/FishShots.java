package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExAutoSoulShot;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;

public class FishShots implements IItemHandler, ScriptFile
{
	// All the item IDs that this handler knows.
	private static int[] _itemIds = { 6535, 6536, 6537, 6538, 6539, 6540 };
	private static int[] _skillIds = { 2181, 2182, 2183, 2184, 2185, 2186 };

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;
		int FishshotId = item.getItemId();

		boolean isAutoSoulShot = false;
		if(player.getAutoSoulShot().contains(FishshotId))
			isAutoSoulShot = true;

		L2ItemInstance weaponInst = player.getActiveWeaponInstance();
		L2Weapon weaponItem = player.getActiveWeaponItem();

		if(weaponInst == null || weaponItem.getItemType() != WeaponType.ROD)
		{
			if(!isAutoSoulShot)
				player.sendPacket(Msg.CANNOT_USE_SOULSHOTS);
			return;
		}

		if(item.getCount() < 1)
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(FishshotId);
				player.sendPacket(new ExAutoSoulShot(FishshotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addString(item.getName()));
				return;
			}
			player.sendPacket(Msg.NOT_ENOUGH_SPIRITSHOTS);
			return;
		}

		// spiritshot is already active
		if(weaponInst.getChargedFishshot())
			return;

		int grade = weaponItem.getCrystalType().externalOrdinal;

		if(grade == 0 && FishshotId != 6535 || grade == 1 && FishshotId != 6536 || grade == 2 && FishshotId != 6537 || grade == 3 && FishshotId != 6538 || grade == 4 && FishshotId != 6539 || grade == 5 && FishshotId != 6540)
		{
			if(isAutoSoulShot)
				return;
			player.sendPacket(Msg.THIS_FISHING_SHOT_IS_NOT_FIT_FOR_THE_FISHING_POLE_CRYSTAL);
			return;
		}

		weaponInst.setChargedFishshot(true);
		player.getInventory().destroyItem(item.getObjectId(), 1, false);
		player.sendPacket(Msg.POWER_OF_MANA_ENABLED);
		player.broadcastSkill(new MagicSkillUse(player, player, _skillIds[grade], 1, 0, 0), true);
	}

	public int[] getItemIds()
	{
		return _itemIds;
	}

	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}
