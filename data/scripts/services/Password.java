package services;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.Files;
import l2open.util.Util;

public class Password extends Functions implements ScriptFile
{
	public void set_pass(String param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		String[] arg = param.trim().split(";:;");
		if(arg.length != 4)
			show(Files.read("data/scripts/services/pass_new.htm", player), player);
		else
		{
			String pass1 = arg[0].trim().toLowerCase();
			String pass2 = arg[1].trim().toLowerCase();
			String question = arg[2].trim();
			String answer = arg[3].trim();

			if(question.isEmpty() || answer.isEmpty())
			{
				player.sendMessage("Введите секретный вопрос/ответ.");
				show(Files.read("data/scripts/services/pass_new.htm", player), player);
				return;
			}
			else if(!Util.isMatchingRegexp(pass1, ConfigValue.ApasswdTemplate))
			{
				player.sendMessage("Пароль содержит запрещенные символы.");
				show(Files.read("data/scripts/services/pass_new.htm", player), player);
				return;
			}

			if(pass1.equals(pass2))
			{
				PlayerData.getInstance().replace_2pass_and_answer(player, pass1, question, answer);
				player.is_block = false;
				player.i_ai3 = 0;
				player.sendActionFailed();
				notifyClanMembers(player);
			}
			else
			{
				player.sendMessage("Пароли не совпадают.");
				show(Files.read("data/scripts/services/pass_new.htm", player), player);
			}
		}
	}

	public void confirm_pass(String param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(param.trim().toLowerCase().equals(player.password.toLowerCase()))
		{
			player.is_block = false;
			player.i_ai3 = 0;
			player.i_ai9=0;
			player.sendActionFailed();
			notifyClanMembers(player);
		}
		else
		{
			player.i_ai9++;
			show(Files.read("data/scripts/services/pass_confirm.htm", player), player);
			if(player.i_ai9 >= 3)
				player.logout(false, false, true, true);
		}
	}

	public void confirm_pass()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		show(Files.read("data/scripts/services/pass_confirm.htm", player), player);
	}

	public void info_pass()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		show(Files.read("data/scripts/services/pass_reco.htm", player).replace("<?question?>", player.l2question), player);
	}

	public void pass_reco(String param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		String[] arg = param.trim().split(";:;");
		if(arg.length != 3)
			show(Files.read("data/scripts/services/pass_reco.htm", player).replace("<?question?>", player.l2question), player);
		else
		{
			String answer = arg[2].trim();
			String pass1 = arg[0].trim().toLowerCase();
			String pass2 = arg[1].trim().toLowerCase();

			if(!Util.isMatchingRegexp(pass1, ConfigValue.ApasswdTemplate))
			{
				player.sendMessage("Пароль содержит запрещенные символы.");
				show(Files.read("data/scripts/services/pass_reco.htm", player).replace("<?question?>", player.l2question), player);
				return;
			}
			if(answer.equals(player.l2answer) && pass1.equals(pass2))
			{
				PlayerData.getInstance().replace_2pass_and_answer(player, pass1, player.l2question, player.l2answer);
				player.is_block = false;
				player.i_ai9=0;
				player.sendActionFailed();
				notifyClanMembers(player);
			}
			else if(answer.equals(player.l2answer))
			{
				player.sendMessage("Пароли не совпадают.");
				show(Files.read("data/scripts/services/pass_reco.htm", player).replace("<?question?>", player.l2question), player);
				player.i_ai9=0;
			}
			else
			{
				player.i_ai9++;
				player.sendMessage("Не верный ответ на вопрос.");
				show(Files.read("data/scripts/services/pass_reco.htm", player).replace("<?question?>", player.l2question), player);
				if(player.i_ai9 >= 3)
					player.logout(false, false, true, true);
			}
		}
	}

	private void notifyClanMembers(L2Player activeChar)
	{
		L2Clan clan = activeChar.getClan();
		if(clan == null || clan.getClanMember(activeChar.getObjectId()) == null)
			return;

		if(!activeChar.isBlocked() && PlayerData.getInstance().isNoticeEnabled(clan) && !PlayerData.getInstance().getNotice(clan).isEmpty())
		{
			NpcHtmlMessage notice = new NpcHtmlMessage(5);
			notice.setHtml("<html><body><center><font color=\"LEVEL\">" + activeChar.getClan().getName() + " Clan Notice</font></center><br>" + PlayerData.getInstance().getNotice(activeChar.getClan()) + "</body></html>");
			activeChar.sendPacket(notice);
		}
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