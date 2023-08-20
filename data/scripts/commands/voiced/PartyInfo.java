package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.util.*;

/**
 * @author: Diagod
 * open-team.ru
 **/
public class PartyInfo extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "party", "party_inst" };

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		command = command.intern();
		if(command.startsWith("party_inst"))
		{
			if(activeChar.getParty() == null)
				return false;

			String html = Files.read("data/scripts/commands/voiced/party_inst.htm", activeChar);
			StringBuilder dialog = new StringBuilder();

			int i=0;
			int expand=args.isEmpty() ? 0 : Integer.parseInt(args);
			for(L2Player player : activeChar.getParty().getPartyMembers())
			{
				dialog.append("<table width=730 border=0 bgcolor="+(i%2 == 0 ? "A7A19A" : "333333")+"><tr>");
				dialog.append("	<td width=120 height=50><center>"+player.getName()+"</center></td><td fixwidth=500 height=50>");
				int i2=0;

				for(String instance_name : InstancedZoneManager.getInstance().getNames())
				{
					//if(Rnd.chance(100))
					//	player.setVarInst(instance_name, String.valueOf(System.currentTimeMillis()));
					int limit = InstancedZoneManager.getInstance().getTimeToNextEnterInstance(instance_name, player);
					if(limit > 0)
					{
						i2++;
						if(i2 < 4 || expand == player.getObjectId())
							dialog.append("<font color="+(i2%2 == 0 ? "B59A75" : "FFFFFF")+">	"+instance_name+"["+limit / 60+":"+limit % 60+"] </font>");
					}
				}

				if(expand == player.getObjectId())
					dialog.append("</td><td width=16><button action=\"bypass -h user_party_inst\" value=\" \" width=16 height=16 back=l2ui_ch3.QuestWndMinusBtn fore=l2ui_ch3.QuestWndMinusBtn>");
				else if(i2 > 3)
					dialog.append("</td><td width=16><button action=\"bypass -h user_party_inst "+player.getObjectId()+"\" value=\" \" width=16 height=16 back=l2ui_ch3.QuestWndPlusBtn fore=l2ui_ch3.QuestWndPlusBtn>");
				dialog.append("</td></tr></table>");
				i++;
			}

			html = html.replace("<?replace?>", dialog.toString());
			ShowBoard.separateAndSend(html, activeChar);
			return true;
		}
		else if(command.startsWith("party"))
		{
			if(activeChar.getParty() == null)
				return false;

			String html = Files.read("data/scripts/commands/voiced/party.htm", activeChar);
			StringBuilder dialog = new StringBuilder();

			

			for(L2Player player : activeChar.getParty().getPartyMembers())
			{
				dialog.append("<tr>");
				dialog.append("	<td width=170><center>"+player.getName()+"</center></td>");
				dialog.append("	<td width=100><center>"+(player.hasBonus() ? "<font color=00FF00>Есть</font>" : "<font color=FF0000>Нет</font>")+"</center></td>");
				dialog.append("</tr>");
			}

			html = html.replace("<?replace?>", dialog.toString());
			show(html, activeChar);
			return true;
		}
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}