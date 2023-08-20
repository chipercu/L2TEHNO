package l2open.gameserver.serverpackets;

import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.extensions.network.SendablePacket;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.base.MultiSellIngredient;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Util;

public abstract class L2GameServerPacket extends SendablePacket<L2GameClient>
{
	//Пускай все наследники используют этот логер
	//P.S. Можно удалить "Logger _log = Logger.getLogger" со всех ServerPacket, они будут наследовать вот этот.
	protected static final Logger _log = Logger.getLogger(L2GameServerPacket.class.getName());
	@Override
	protected void write()
	{
		try
		{
			
			if(ConfigValue.DebugServerPackets)
				if(ConfigValue.NotSeeServerPackets.split(",") == null || !Util.contains(ConfigValue.NotSeeServerPackets.split(","), getClass().getSimpleName()))
					if(ConfigValue.ADebugServerPacketsChar.isEmpty() || getClient() != null && getClient().getActiveChar() != null && ConfigValue.ADebugServerPacketsChar.equals(getClient().getActiveChar().getName()))
						_log.info("Server send to Client["+getClient().isLindvior()+"]("+(getClient().getActiveChar() == null ? "Auth" : (getClient().getActiveChar() == null ? "null" : getClient().getActiveChar().getName()))+") packets: " + getType());
			if(!getClient().isLindvior() || !writeImplLindvior())
				writeImpl();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

	protected abstract void writeImpl();

	protected boolean writeImplLindvior()
	{
		return false;
	}

	protected final static int EXTENDED_PACKET = 0xFE;

	@Override
	protected int getHeaderSize()
	{
		return 2;
	}

	@Override
	protected void writeHeader(int dataSize)
	{
		writeH(dataSize + getHeaderSize());
	}

	protected void writeItemElements(L2ItemInstance item)
	{
		writeH(item.getAttackElement());
		writeH(item.getAttackElementValue());
		for (int i = 0; i < 6; i++) 
			writeH(item.getDeffAttr()[i]);
	}

	protected void writeItemElements(TradeItem item)
	{
		writeH(item.getAttackElement()[0]); // attack element (-2 - none)
		writeH(item.getAttackElement()[1]); // attack element value
		writeH(item.getDefenceFire()); // водная стихия (fire pdef)
		writeH(item.getDefenceWater()); // огненная стихия (water pdef)
		writeH(item.getDefenceWind()); // земляная стихия (wind pdef)
		writeH(item.getDefenceEarth()); // воздушная стихия (earth pdef)
		writeH(item.getDefenceHoly()); // темная стихия (holy pdef)
		writeH(item.getDefenceUnholy()); // светлая стихия (dark pdef)
	}

	protected void writeItemElements(MultiSellIngredient item)
	{
		if(item.getItemId() <= 0)
		{
			writeH(-2); // attack element (-2 - none)
			writeH(0x00); // attack element value
			writeH(0x00); // водная стихия (fire pdef)
			writeH(0x00); // огненная стихия (water pdef)
			writeH(0x00); // земляная стихия (wind pdef)
			writeH(0x00); // воздушная стихия (earth pdef)
			writeH(0x00); // темная стихия (holy pdef)
			writeH(0x00); // светлая стихия (dark pdef)
			return;
		}
		L2Item i = ItemTemplates.getInstance().getTemplate(item.getItemId());
		if(i.isWeapon())
		{
			writeH(item.getElement()); // attack element (-2 - none)
			writeH(item.getElementValue()); // attack element value
			writeH(0); // водная стихия (fire pdef)
			writeH(0); // огненная стихия (water pdef)
			writeH(0); // земляная стихия (wind pdef)
			writeH(0); // воздушная стихия (earth pdef)
			writeH(0); // темная стихия (holy pdef)
			writeH(0); // светлая стихия (dark pdef)
		}
		else if(i.isArmor())
		{
			writeH(-2); // attack element (-2 - none)
			writeH(0); // attack element value
			writeH(item.getElement() == L2Item.ATTRIBUTE_FIRE ? item.getElementValue() : 0); // водная стихия (fire pdef)
			writeH(item.getElement() == L2Item.ATTRIBUTE_WATER ? item.getElementValue() : 0); // огненная стихия (water pdef)
			writeH(item.getElement() == L2Item.ATTRIBUTE_WIND ? item.getElementValue() : 0); // земляная стихия (wind pdef)
			writeH(item.getElement() == L2Item.ATTRIBUTE_EARTH ? item.getElementValue() : 0); // воздушная стихия (earth pdef)
			writeH(item.getElement() == L2Item.ATTRIBUTE_HOLY ? item.getElementValue() : 0); // темная стихия (holy pdef)
			writeH(item.getElement() == L2Item.ATTRIBUTE_DARK ? item.getElementValue() : 0); // светлая стихия (dark pdef)
		}
		else
		{
			writeH(-2); // attack element (-2 - none)
			writeH(0x00); // attack element value
			writeH(0x00); // водная стихия (fire pdef)
			writeH(0x00); // огненная стихия (water pdef)
			writeH(0x00); // земляная стихия (wind pdef)
			writeH(0x00); // воздушная стихия (earth pdef)
			writeH(0x00); // темная стихия (holy pdef)
			writeH(0x00); // светлая стихия (dark pdef)
		}
	}

	protected final void writeItemInfo(final TradeItem item)
	{
		writeItemInfo(item, item.getCount());
	}

	protected final void writeItemInfo(final TradeItem item, final long count)
	{
		writeD(item.getObjectId());
		writeD(item.getItemId());
		writeD(item.getEquipSlot());
		writeQ(count);
		writeH(item.getItem().getType2());
		writeH(item.getCustomType1());
		writeH(item.isEquipped() ? 1 : 0);
		writeD(item.getItem().getBodyPart());
		writeH(item.getEnchantLevel());
		writeH(item.getCustomType2());
		writeD(item.getAugmentationId());
		writeD(item.getShadowLifeTime());
		writeD(item.getTemporalLifeTime());
		if(getClient().isLindvior())
			writeH(0x01); //L2WT GOD
		writeItemElements(item);
		writeH(item.getEnchantOptions()[0]);
		writeH(item.getEnchantOptions()[1]);
		writeH(item.getEnchantOptions()[2]);
		if(getClient().isLindvior())
			writeD(item.getVisualId()); // getVisualId
	}

	protected final void writeItemInfo(final L2ItemInstance item)
	{
		writeD(item.getObjectId());
		writeD(item.getItem().getItemId());
		writeD(item.getEquipSlot());
		writeQ(item.getCount());
		writeH(item.getItem().getType2());
		writeH(item.getCustomType1());
		writeH(item.isEquipped() ? 1 : 0);
		writeD(item.getItem().getBodyPart());
		writeH(item.getEnchantLevel());
		writeH(item.getCustomType2());
		writeD(item.getAugmentationId());
		writeD(item.isShadowItem() ? item.getLifeTimeRemaining() : -1);
		writeD(item.isTemporalItem() ? item.getLifeTimeRemaining() : 0x00);
		if(getClient().isLindvior())
			writeH(0x01); //L2WT GOD
		writeItemElements(item);
		writeH(item.getEnchantOptions()[0]);
		writeH(item.getEnchantOptions()[1]);
		writeH(item.getEnchantOptions()[2]);
		if(getClient().isLindvior())
			writeD(item._visual_item_id); // getVisualId
	}

	protected void writeEx(int value)
	{
		writeC(0xFE);
		writeH(value);
	}

	protected void writeHG(int value)
	{
		writeH(getClient().isLindvior() ? (value+1) : value);
	}

	protected void writeHG37(int value)
	{
		writeH(getClient().isLindvior() ? (value+37) : value);
	}

	protected final void writeEnchant(final L2Character cha, boolean send_visual_enchant)
	{
		final L2ItemInstance wpn = cha.getActiveWeaponInstance();

		if(wpn == null || wpn.getVisualItemId() > 0 || (cha.isPlayer() && cha.getPlayer().isMounted()))
			writeC(0x00);
		else
			writeC(Math.min(127, (wpn.getVisualEnchantLevel() == -1 || !send_visual_enchant) ? wpn.getEnchantLevel() : wpn.getVisualEnchantLevel()));
	}

	protected final void writePaperdollInfo(final Inventory _inv, boolean user_info, boolean is_visual_id, boolean disable_cloak)
	{		
		L2ItemInstance chestItem = _inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		boolean wear = chestItem != null && chestItem.getVisualItemId() > 0 && is_visual_id || _inv.getOwner().getPlayer().getEventMaster() != null && _inv.getOwner().getPlayer().getEventMaster().setWear(_inv.getOwner().getPlayer()) || _inv.getOwner().isPlayer() && _inv.getOwner().getPlayer()._paperdoll_test != null && _inv.getOwner().getPlayer()._paperdoll_test[Inventory.PAPERDOLL_CHEST] > 0;

		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_UNDER, is_visual_id, user_info));
		if(user_info)
		{
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_REAR, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LEAR, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_NECK, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_RFINGER, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LFINGER, is_visual_id, false));
		}
		if(wear)
			writeD(0x00);
		else
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HEAD, is_visual_id, false));
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND, is_visual_id /*&& !getClient().isLindvior()*/, user_info));
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LHAND, is_visual_id /*&& !getClient().isLindvior()*/, user_info));
		if(wear)
			writeD(0x00);
		else
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES, is_visual_id, false));
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_CHEST, is_visual_id, user_info));
		if(wear)
		{
			writeD(0x00);
			writeD(0x00);
		}
		else
		{
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LEGS, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_FEET, is_visual_id, false));
		}

		if((user_info || _inv.getOwner().getPlayer().isInEvent() != 11 && _inv.getOwner().getPlayer().isInEvent() != 14) && !disable_cloak)
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_BACK, is_visual_id, user_info));
		else
			writeD(0x00);

		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LRHAND, is_visual_id, user_info));

		if(user_info || _inv.getOwner().getPlayer().isInEvent() != 11)
		{
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR, is_visual_id, user_info));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_DHAIR, is_visual_id, user_info));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_RBRACELET, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LBRACELET, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_DECO1, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_DECO2, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_DECO3, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_DECO4, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_DECO5, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_DECO6, is_visual_id, false));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_BELT, is_visual_id, false));
		}
		else
		{
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
		}
	}

	public String getType()
	{
		return "[S] " + getClass().getSimpleName();
	}

	@Override
	public String toString()
	{
		return getType() + "; buffer: " + getByteBuffer();
	}
}