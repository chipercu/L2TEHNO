package l2open.gameserver.model.instances;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.instancemanager.FortressSiegeManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;

public class L2FortressDoormenInstance extends L2NpcInstance
{
	private static int Cond_All_False = 0;
	private static int Cond_Fortress_Owner = 1;

	public L2FortressDoormenInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		int condition = validateCondition(player);
		if(condition <= Cond_All_False)
			return;

		if(condition == Cond_Fortress_Owner)
			if((player.getClanPrivileges() & L2Clan.CP_CS_ENTRY_EXIT) == L2Clan.CP_CS_ENTRY_EXIT)
			{
				if(command.startsWith("open_doors"))
				{
					StringTokenizer st = new StringTokenizer(command.substring(10), ", ");
					st.nextToken(); // Bypass first value since its fortid

					while(st.hasMoreTokens())
					{
						int id = Integer.parseInt(st.nextToken());
						// Открывать двери в казармы во время осады нельзя.
						if(getFortress().getSiege().isInProgress() || TerritorySiege.isInProgress())
							if(FortressSiegeManager.getGuardDoors(getFortress().getId()).containsKey(id) || FortressSiegeManager.getCommandCenterDoors(getFortress().getId()).contains(id))
							{
								player.sendPacket(new NpcHtmlMessage(player, this, "/data/html/doormen/fortress/busy.htm", 0));
								break;
							}
						getFortress().openDoor(player, id);
					}
				}
				else if(command.startsWith("close_doors"))
				{
					StringTokenizer st = new StringTokenizer(command.substring(11), ", ");
					st.nextToken(); // Bypass first value since its fortid

					while(st.hasMoreTokens())
					{
						int id = Integer.parseInt(st.nextToken());
						// Закрывать двери в казармы во время осады нельзя.
						if(getFortress().getSiege().isInProgress() || TerritorySiege.isInProgress())
							if(FortressSiegeManager.getGuardDoors(getFortress().getId()).containsKey(id) || FortressSiegeManager.getCommandCenterDoors(getFortress().getId()).contains(id))
							{
								player.sendPacket(new NpcHtmlMessage(player, this, "/data/html/doormen/fortress/busy.htm", 0));
								break;
							}
						getFortress().closeDoor(player, id);
					}
				}
			}
			else
				player.sendMessage(new CustomMessage("common.Privilleges", player));
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename = "/data/html/doormen/fortress/no.htm";
		int condition = validateCondition(player);
		if(condition == Cond_Fortress_Owner) // Clan owns fortress
			filename = "/data/html/doormen/fortress/" + getTemplate().npcId + ".htm"; // Owner message window
		player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
	}

	private int validateCondition(L2Player player)
	{
		if(player.isGM())
			return Cond_Fortress_Owner;
		if(player.getClan() != null && getFortress() != null && getFortress().getId() >= 0 && getFortress().getOwnerId() == player.getClanId())
			return Cond_Fortress_Owner;
		return Cond_All_False;
	}
}