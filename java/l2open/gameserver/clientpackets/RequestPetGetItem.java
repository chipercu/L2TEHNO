package l2open.gameserver.clientpackets;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;

public class RequestPetGetItem extends L2GameClientPacket
{
	// format: cd
	private int _objectId;

	@Override
	public void readImpl()
	{
		_objectId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;
		L2ItemInstance item = (L2ItemInstance)activeChar.getVisibleObject(_objectId);
		if(item == null || (item.getCustomFlags() & L2ItemInstance.FLAG_EQUIP_ON_PICKUP) == L2ItemInstance.FLAG_EQUIP_ON_PICKUP)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getPet() instanceof L2PetInstance)
		{
			if(item.getDropTimeOwner() != 0 && item.getItemDropOwner() != null && item.getDropTimeOwner() > System.currentTimeMillis() && activeChar != item.getItemDropOwner() && (!activeChar.isInParty() || activeChar.isInParty() && item.getItemDropOwner().isInParty() && activeChar.getParty() != item.getItemDropOwner().getParty()))
			{
				SystemMessage sm;
				if(item.getItemId() == 57)
				{
					sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
					sm.addNumber(item.getCount());
				}
				else
				{
					sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1);
					sm.addItemName(item.getItemId());
				}
				sendPacket(sm);
				activeChar.sendActionFailed();
				return;
			}

			L2PetInstance pet = (L2PetInstance) activeChar.getPet();
			if(pet == null || pet.isDead() || pet.isOutOfControl())
			{
				activeChar.sendActionFailed();
				return;
			}
			if(pet.getCurrentFed() <= 0.01 * pet.getMaxFed())
			{
				pet.getPlayer().sendPacket(new SystemMessage(SystemMessage.WHEN_YOUR_PETS_HUNGER_GAUGE_IS_AT_0_YOU_CANNOT_USE_YOUR_PET));
				pet._actionAtack = 0;
				return;
			}
			pet.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item, null);
		}
		else
		{
			activeChar.sendActionFailed();
			return;
		}
	}
}