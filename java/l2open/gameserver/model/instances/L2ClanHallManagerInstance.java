package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.serverpackets.AgitDecoInfo;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2ClanHallManagerInstance extends L2ResidenceManager
{
	protected static int Cond_All_False = 0;
	protected static int Cond_Busy_Because_Of_Siege = 1;
	protected static int Cond_Owner = 2;

	public L2ClanHallManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		super.onAction(player, shift, addDist);
		int condition = validateCondition(player);
		if(condition != Cond_Owner)
			return;
		ClanHall ch = getClanHall();
		if(ch == null || ch.getOwner() == null)
			return;
		long lease = ch.getLease();
		Castle castle = CastleManager.getInstance().getCastleByIndex(ch.getZone().getTaxById());
		long tax = lease * castle.getTaxPercent() / 100;
		if(ConfigValue.ClanHallBid_ItemId == 57)
			lease += tax;
		if(ConfigValue.ClanHallBid_ItemId == 57 && ch.getOwner().getAdenaCount() >= lease || ConfigValue.ClanHallBid_ItemId != 57 && ch.getOwner().getWarehouse().countOf(ConfigValue.ClanHallBid_ItemId) >= lease)
			return;
		if(ch.getPaidUntil() <= System.currentTimeMillis() + 24 * 60 * 60 * 1000L && ch.getPaidUntil() >= System.currentTimeMillis() + 12 * 60 * 60 * 1000L)
			player.sendPacket(new SystemMessage(SystemMessage.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW).addNumber(lease));
		else if(ch.isInDebt())
			player.sendPacket(Msg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename = "data/html/residence/chamberlain-no.htm";
		int condition = validateCondition(player);
		if(condition > Cond_All_False)
			if(condition == Cond_Busy_Because_Of_Siege)
				filename = "data/html/residence/chamberlain-busy.htm"; // Busy because of siege
			else if(condition == Cond_Owner) // Clan owns Residence
				filename = "data/html/residence/chamberlain.htm"; // Owner message window
		player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
	}

	protected int validateCondition(L2Player player)
	{
		if(player.isGM())
			return Cond_Owner;
		if(player.getClan() != null)
			if(getResidence().getSiege() != null && getResidence().getSiege().isInProgress())
				return Cond_Busy_Because_Of_Siege;
			else if(getResidence().getOwnerId() == player.getClanId())
				return Cond_Owner;
		return Cond_All_False;
	}

	@Override
	protected Residence getResidence()
	{
		return getClanHall();
	}

	public void sendDecoInfo(L2Player player)
	{
		ClanHall clanHall = getClanHall();
		if(clanHall != null)
			player.sendPacket(new AgitDecoInfo(getClanHall()));
	}

	@Override
	public void broadcastDecoInfo()
	{
		for(L2Player player : L2World.getAroundPlayers(this))
			if(player != null)
				sendDecoInfo(player);
	}

	@Override
	protected int getPrivUseFunctions()
	{
		return L2Clan.CP_CH_USE_FUNCTIONS;
	}

	@Override
	protected int getPrivSetFunctions()
	{
		return L2Clan.CP_CH_SET_FUNCTIONS;
	}

	@Override
	protected int getPrivDismiss()
	{
		return L2Clan.CP_CH_DISMISS;
	}

	@Override
	protected int getPrivDoors()
	{
		return L2Clan.CP_CH_ENTRY_EXIT;
	}
}