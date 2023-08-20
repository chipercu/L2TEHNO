package npc.model;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2MerchantInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * @author: Drizzy
 * @date: 27.04.2012 14:49
 * @comment: this instance use npc Separated Soul on Dragon Valler\Antharas Lair. They teleport player whose lvl >= 80.
 */
public class SeparatedSoulInstance extends L2MerchantInstance
{
	public SeparatedSoulInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/teleporter/" + file);
		player.sendPacket(html);
	}
	
	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		if(player.getLevel() >= 80)
		{
			if(command.equalsIgnoreCase("tp"))
			{
				player.teleToLocation(73122, 118351, -3704);
			}
			else if(command.equalsIgnoreCase("tp1"))
			{
				player.teleToLocation(99218, 110283, -3696);
			}
			else if(command.equalsIgnoreCase("tp2"))
			{
				player.teleToLocation(116992, 113716, -3056);
			}
			else if(command.equalsIgnoreCase("tp3"))
			{
				player.teleToLocation(113203, 121063, -3712);
			}
			else if(command.equalsIgnoreCase("tp4"))
			{
				player.teleToLocation(131116, 114333, -3704);
			}
			else if(command.equalsIgnoreCase("tp5"))
			{
				player.teleToLocation(146129, 111232, -3568);
			}
			else if(command.equalsIgnoreCase("tp6"))
			{
				player.teleToLocation(148447, 110582, -3944);
			}
			else if(command.equalsIgnoreCase("tp7"))
			{
				player.teleToLocation(117046, 76798, -2696);
			}
			else
				super.onBypassFeedback(player, command);
		}
		else
			showHtmlFile(player, getNpcId() + "-no.htm"); 	
	}
}