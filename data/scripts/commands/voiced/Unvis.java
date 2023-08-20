package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;


public class Unvis extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "unvis" };

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean useVoicedCommand(String command, L2Player player, String args)
	{
		command = command.intern();
		if(command.equalsIgnoreCase("unvis"))
		{
			final L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if(chest == null)
			{
				//player.sendMessage("Тут текст 1.");
				return false;
			}
			else if(chest._visual_item_id <= 0)
			{
				//player.sendMessage("В данный сет запрещена вставка.");
				return false;
			}
			int itemid = chest._visual_item_id;
			player.getInventory().addItem(itemid, 1);
			chest.setVisualItemId(0);
			player.getInventory().refreshListeners(chest, -1);
			player.broadcastUserInfo(true);
			player.broadcastUserInfo(true);
		}
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}