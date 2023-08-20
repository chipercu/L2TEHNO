package communityboard;

import java.sql.ResultSet;
import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

public class UseCommand extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	private static enum Commands
	{
		_bbs_call_cfg,
		_bbs_call_acp,
		_bbs_call_anim,
		_bbs_call_relog,
		_bbs_call_ofline,
		_bbs_call_tal,
		_bbs_call_help,
		_bbs_call_pa
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		if(bypass.equals("_bbs_call_cfg"))
		{
			IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler("cfg");
			if(vch != null)
				vch.useVoicedCommand("cfg", player, "");
		}
		else if(bypass.equals("_bbs_call_acp"))
		{
			IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler("acp");
			if(vch != null)
				vch.useVoicedCommand("acp", player, player._enable_auto ? "off" : "on");
		}
		else if(bypass.equals("_bbs_call_anim"))
		{
			IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler("animation");
			if(vch != null)
				vch.useVoicedCommand("animation", player, player.show_buff_anim_dist() <= 0 ? "3000" : "0");
		}
		else if(bypass.equals("_bbs_call_relog"))
		{
			IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler("relog");
			if(vch != null)
				vch.useVoicedCommand("relog", player, "");
		}
		else if(bypass.equals("_bbs_call_ofline"))
		{
			IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler("offline");
			if(vch != null)
				vch.useVoicedCommand("offline", player, "");
		}
		else if(bypass.equals("_bbs_call_tal"))
		{
			if(player.getVarB("TalismanSumLife", false))
			{
				player.setVar("TalismanSumLife", "false");
				player.sendMessage("Суммирование талисманов включенно.");
			}
			else
			{
				player.setVar("TalismanSumLife", "true");
				player.sendMessage("Суммирование талисманов выключенно.");
			}
		}
		else if(bypass.equals("_bbs_call_help"))
		{
			IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler("help");
			if(vch != null)
				vch.useVoicedCommand("help", player, "");
		}
		else if(bypass.equals("_bbs_call_pa"))
		{
			separateAndSend(Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/premium.htm", player), player);
		}
		else
			separateAndSend(DifferentMethods.getErrorHtml(player, bypass), player);
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}

	@Override
	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}