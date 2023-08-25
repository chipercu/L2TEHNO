package items;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.RadarControl;
import l2open.util.Location;

public class Book implements IItemHandler, ScriptFile
{
	private static final int[] _itemIds = { 5588, 6317, 7561, 7063, 7064, 7065, 7066, 7082, 7083, 7084, 7085, 7086, 7087,
			7088, 7089, 7090, 7091, 7092, 7093, 7094, 7095, 7096, 7097, 7098, 7099, 7100, 7101, 7102, 7103, 7104, 7105, 7106,
			7107, 7108, 7109, 7110, 7111, 7112, 8059, 13130, 13131, 13132, 13133, 13134, 13135, 13136 };

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(!playable.isPlayer())
			return;

		L2Player activeChar = (L2Player) playable;
		Functions.show("data/html/help/" + item.getItemId() + ".htm", activeChar, null);
		if(item.getItemId() == 7063)
			activeChar.sendPacket(new RadarControl(0, 2, new Location(51995, -51265, -3104)));
		activeChar.sendActionFailed();
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