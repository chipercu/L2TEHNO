package services;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.Files;

public class Activation extends Functions implements ScriptFile
{
	public void activation_page()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		player.block();
		player.setFlying(true); // хак позволяющий сделать логаут
		show(Files.read("data/scripts/services/change_data.htm", player), player);
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(ConfigValue.ActivationEnable && player != null && !PlayerData.getInstance().isActivation(player) && player.getVar("activation_acc") == null)
			player.callScripts("services.Activation", "activation_page");
	}

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}