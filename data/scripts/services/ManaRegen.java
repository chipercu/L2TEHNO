package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;

public class ManaRegen extends Functions implements ScriptFile
{
	private static final int ADENA = 57;
	private static final long PRICE = 5; //5 аден за 1 МП

	public void DoManaRegen()
	{
		L2Player player = (L2Player) getSelf();
		long mp = (long) Math.floor(player.getMaxMp() - player.getCurrentMp());
		long fullCost = mp * PRICE;
		if(fullCost <= 0)
		{
			player.sendPacket(Msg.NOTHING_HAPPENED);
			return;
		}
		if(getItemCount(player, ADENA) >= fullCost)
		{
			removeItem(player, ADENA, fullCost);
			player.sendPacket(new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED).addNumber(mp));
			player.setCurrentMp(player.getMaxMp());
		}
		else
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Mana Regen");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}