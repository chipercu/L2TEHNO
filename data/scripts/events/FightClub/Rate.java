package events.FightClub;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.PlayerClass;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.reference.*;

public class Rate
{
	private String playerName;
	private int playerLevel;
	private String playerClass;
	private int _itemId;
	private String itemName;
	private long _itemCount;
	private HardReference<L2Player> _owner = HardReferences.emptyRef();

	public Rate(L2Player player, int itemId, long itemCount)
	{
		playerName = player.getName();
		playerLevel = player.getLevel();
		playerClass = PlayerClass.values()[player.getClassId().getId()].toString();
		_itemId = itemId;
		_itemCount = itemCount;
		itemName = ItemTemplates.getInstance().createItem(itemId).getName();
		_owner = player.getRef();
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public int getPlayerLevel()
	{
		return playerLevel;
	}

	public String getPlayerClass()
	{
		return playerClass;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getItemCount()
	{
		return _itemCount;
	}

	public String getItemName()
	{
		return itemName;
	}

	public int getObjectId()
	{
		if(_owner.get() == null)
			return -1;
		return _owner.get().getObjectId();
	}
}