package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.items.L2ItemInstance;

public class RequestChangePetName extends L2GameClientPacket
{
	// format: cS

	private String _name;

	@Override
	public void readImpl()
	{
		_name = readS();
	}

	@Override
	public void runImpl()
	{
		L2Player cha = getClient().getActiveChar();
		L2Summon pet = cha.getPet();
		if(pet != null && (pet.getName() == null || pet.getName().isEmpty() || pet.getName().equalsIgnoreCase(pet.getTemplate().name)))
		{
			if(_name.length() < 1 || _name.length() > 8)
			{
				sendPacket(Msg.YOUR_PETS_NAME_CAN_BE_UP_TO_8_CHARACTERS);
				return;
			}
			pet.setName(_name);
			pet.broadcastPetInfo();

			if(pet.isPet())
			{
				L2PetInstance _pet = (L2PetInstance) pet;
				L2ItemInstance controlItem = _pet.getControlItem();
				if(controlItem != null)
				{
					controlItem.setCustomType2(1);
					controlItem.setPriceToSell(0); // Костыль, иначе CustomType2 = 1 не пишется в базу
					controlItem.updateDatabase();
					_pet.updateControlItem();
				}
			}

		}
	}
}