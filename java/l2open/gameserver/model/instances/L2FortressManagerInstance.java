package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class L2FortressManagerInstance extends L2ResidenceManager
{
	protected static int Cond_All_False = 0;
	protected static int Cond_Busy_Because_Of_Siege = 1;
	protected static int Cond_Owner = 2;

	public L2FortressManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename = "data/html/fortress/foreman-no.htm";
		int condition = validateCondition(player);
		if(condition > Cond_All_False)
			if(condition == Cond_Busy_Because_Of_Siege)
				filename = "data/html/fortress/foreman-busy.htm"; // Busy because of siege
			else if(condition == Cond_Owner) // Clan owns Residence
				filename = "data/html/fortress/foreman.htm"; // Owner message window
		player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
	}

	protected int validateCondition(L2Player player)
	{
		if(player.isGM())
			return Cond_Owner;
		if(player.getClan() != null)
			if(getResidence().getSiege().isInProgress() || TerritorySiege.isInProgress())
				return Cond_Busy_Because_Of_Siege;
			else if(getResidence().getOwnerId() == player.getClanId())
				return Cond_Owner;
		return Cond_All_False;
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();

		if(actualCommand.equalsIgnoreCase("receive_report"))
		{
			int ownedTime = (int) (System.currentTimeMillis() / 1000 - getFortress().getOwnDate());
			SimpleDateFormat format2 = new SimpleDateFormat("HH");
			SimpleDateFormat format3 = new SimpleDateFormat("mm");
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			if(getFortress().getFortState() < 2)
				html.setFile("data/html/fortress/foreman-report.htm");
			else
				html.setFile("data/html/fortress/report.htm");
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%hr%", format2.format(ownedTime));
			html.replace("%min%", format3.format(ownedTime));
			player.sendPacket(html);
			return;
		}

		super.onBypassFeedback(player, command);
	}

	@Override
	protected Residence getResidence()
	{
		return getFortress();
	}

	@Override
	public void broadcastDecoInfo()
	{}

	@Override
	protected int getPrivUseFunctions()
	{
		return L2Clan.CP_CS_USE_FUNCTIONS;
	}

	@Override
	protected int getPrivSetFunctions()
	{
		return L2Clan.CP_CS_SET_FUNCTIONS;
	}

	@Override
	protected int getPrivDismiss()
	{
		return L2Clan.CP_CS_DISMISS;
	}

	@Override
	protected int getPrivDoors()
	{
		return L2Clan.CP_CS_ENTRY_EXIT;
	}
}