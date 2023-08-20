package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;

public class L2FortEnvoyInstance extends L2NpcInstance
{
	private int _castleId = 0;
	private String _castleName = "";

	public L2FortEnvoyInstance(int objectId, L2NpcTemplate template, int castleId)
	{
		super(objectId, template);
		_castleId = castleId;
		_castleName = CastleManager.getInstance().getCastleByIndex(_castleId).getName();
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename = "/data/html/fortress/envoy-no.htm";
		if(!player.isClanLeader() || player.getClan() == null || getFortress().getId() != player.getClan().getHasFortress())
			filename = "/data/html/fortress/envoy-noclan.htm";
		else if(getFortress().getFortState() == 0)
			filename = "/data/html/fortress/envoy.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%castleName%", _castleName);

		player.sendPacket(html);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();

		String par = "";
		if(st.countTokens() >= 1)
			par = st.nextToken();

		if(actualCommand.equalsIgnoreCase("select"))
		{
			int val = Integer.parseInt(par);

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			if(val == 2)
				if(CastleManager.getInstance().getCastleByIndex(_castleId).getOwnerId() <= 0)
				{
					html.setFile("./data/html/fortress/envoy-no-castle-leader.htm");
					html.replace("%castleName%", _castleName);
					player.sendPacket(html);
					return;
				}

			getFortress().setFortState(val, _castleId);
			html.setFile("./data/html/fortress/envoy-ok.htm");
			html.replace("%castleName%", _castleName);

			player.sendPacket(html);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void spawnMe()
	{
		super.spawnMe();
		ThreadPoolManager.getInstance().schedule(new DeSpawn(), 1 * 60 * 60 * 1000);
	}

	public class DeSpawn extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			// если в течении часа не определились, то крепость становится независимой
			if(getFortress().getFortState() == 0)
				getFortress().setFortState(1, 0);
			decayMe();
		}
	}
}