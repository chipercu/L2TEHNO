package npc.model;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Files;

public class L2TullyCubeInstance extends L2NpcInstance
{
	public L2TullyCubeInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(player, this, null, 0);
		String html = Files.read("data/html/Tully/32467.htm", player);
		if(-13492 > getZ() && -13692 < getZ())
			html += "<br><a action=\"bypass -h scripts_Util:Gatekeeper -12220 279696 -10491 0\">Teleport to the 8th floor</a>";
		else if(-11600 > getZ() && -11670 < getZ())
		{
			html += "<br><a action=\"bypass -h scripts_Util:Gatekeeper -12220 279713 -13595 0\">Teleport to the 6th floor</a>";
			html += "<br><a action=\"bypass -h scripts_Util:Gatekeeper -12220 279696 -10491 0\">Teleport to the 8th floor</a>";
		}
		else
			html += "<br><a action=\"bypass -h scripts_Util:Gatekeeper 21928 243924 11093 0\">Teleport to the roof</a>";
		msg.setHtml(html);
		player.sendPacket(msg);
	}
}