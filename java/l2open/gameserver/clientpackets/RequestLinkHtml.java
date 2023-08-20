package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.util.Files;

public class RequestLinkHtml extends L2GameClientPacket
{
	private String _link;

	@Override
	public void readImpl()
	{
		_link = readS();
	}

	@Override
	public void runImpl()
	{
		L2Player actor = getClient().getActiveChar();
		if(actor == null)
			return;

		if(_link.contains("..") || !_link.endsWith(".htm"))
		{
			_log.warning("[RequestLinkHtml]["+actor.getName()+"] hack? link contains prohibited characters: '" + _link + "', skipped");
			return;
		}
		try
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
			npcReply.setHtml(Files.read_pts(_link, actor));
			sendPacket(npcReply);
		}
		catch(Exception e)
		{
			_log.warning("Bad RequestLinkHtml: " + e);
		}
	}
}