package l2open.gameserver.serverpackets;

import l2open.config.*;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.skills.Stats;

public class ExStorageMaxCount extends L2GameServerPacket {
    private int _inventory;
    private int _warehouse;
    private int _freight;
    private int _privateSell;
    private int _privateBuy;
    private int _recipeDwarven;
    private int _recipeCommon;
    private int _inventoryExtraSlots;
    private int clan;
    private int questInventoryLimit;

    public ExStorageMaxCount(L2Player player) {
        _inventory = player.getInventoryLimit();
        _warehouse = player.getWarehouseLimit();
        _freight = player.getFreightLimit();
        _privateBuy = _privateSell = player.getTradeLimit();
        _recipeDwarven = player.getDwarvenRecipeLimit();
        _recipeCommon = player.getCommonRecipeLimit();
        _inventoryExtraSlots = (int) player.calcStat(Stats.INVENTORY_LIMIT, 0, null, null);
        clan = (player.getClan() != null ? player.getClan().getWhBonus() : 0) + ConfigValue.MaximumWarehouseSlotsForClan;
        questInventoryLimit = ConfigValue.MaximumQuestInventorySlot;
    }

    @Override
    protected final void writeImpl()
	{
        writeC(EXTENDED_PACKET);
        writeH(0x2F);

        writeD(_inventory);
        writeD(_warehouse);
        writeD(clan);

        writeD(_privateSell);
        writeD(_privateBuy);
        writeD(_recipeDwarven);
        writeD(_recipeCommon);
        writeD(_inventoryExtraSlots);
        writeD(questInventoryLimit);// Quest inventory limit
		if(getClient().isLindvior())
		{
			writeD(40); //  Unknown (40 - offlike)
			writeD(40); //  Unknown (40 - offlike)
		}
    }
}