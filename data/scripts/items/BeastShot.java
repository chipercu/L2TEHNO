package items;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;

public class BeastShot implements IItemHandler, ScriptFile
{
	private final static int[] _itemIds = { 6645, 6646, 6647, 20332, 20333, 20334 };

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		boolean isAutoSoulShot = false;
		if(player.getAutoSoulShot().contains(item.getItemId()))
			isAutoSoulShot = true;

		L2Summon pet = player.getPet();
		if(pet == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return;
		}

		if(pet.isDead())
		{
			if(!isAutoSoulShot)
				player.sendPacket(Msg.WHEN_PET_OR_SERVITOR_IS_DEAD_SOULSHOTS_OR_SPIRITSHOTS_FOR_PET_OR_SERVITOR_ARE_NOT_AVAILABLE);
			return;
		}

		int consumption = 0;
		int skillid = 0;

		switch(item.getItemId())
		{
			case 6645:
			case 20332:
				if(pet.getChargedSoulShot())
					return;
				consumption = pet.getSoulshotConsumeCount();
				if(item.getCount() < consumption)
				{
					player.sendPacket(Msg.YOU_DONT_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_PET_SERVITOR);
					return;
				}
				pet.chargeSoulShot();
				skillid = 2033;
				break;
			case 6646:
			case 20333:
				if(pet.getChargedSpiritShot() > 0)
					return;
				consumption = pet.getSpiritshotConsumeCount();
				if(item.getCount() < consumption)
				{
					player.sendPacket(Msg.YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PET_SERVITOR);
					return;
				}
				pet.chargeSpiritShot(L2ItemInstance.CHARGED_SPIRITSHOT);
				skillid = 2008;
				break;
			case 6647:
			case 20334:
				if(pet.getChargedSpiritShot() > 1)
					return;
				consumption = pet.getSpiritshotConsumeCount();
				if(item.getCount() < consumption)
				{
					player.sendPacket(Msg.YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PET_SERVITOR);
					return;
				}
				pet.chargeSpiritShot(L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT);
				skillid = 2009;
				break;
		}

		pet.broadcastSkill(new MagicSkillUse(pet, pet, skillid, 1, 0, 0), player.show_buff_anim_dist() > 10);
		if(!ConfigValue.InfinitySS)
			player.getInventory().destroyItem(item, consumption, false);
	}

	public final int[] getItemIds()
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