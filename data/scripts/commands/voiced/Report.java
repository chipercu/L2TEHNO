package commands.voiced;

import java.sql.SQLException;

import l2open.config.ConfigValue;
import l2open.database.L2DatabaseFactory;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Util;
import l2open.extensions.multilang.CustomMessage;
/**
 * @author: Diagod
 * open-team.ru
 **/
public class Report extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "report", "bot"};

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
		if(ConfigValue.EnableBotReport)
		{
			if(command.startsWith("report") || command.startsWith("bot"))
			{
				L2Object target = player.getTarget();
				if(target != null && target.isPlayer())
				{
					String dialog = Files.read("data/scripts/actions/bot_report.htm", player);
					dialog = dialog.replace("<?name?>", String.valueOf(target.getName()));
					dialog = dialog.replace("<?target_id?>", String.valueOf(target.getObjectId())+" "+String.valueOf(target.getPlayer().getHWIDs())+" "+target.getName());
					show(dialog, player);
				}
			}
		}
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}