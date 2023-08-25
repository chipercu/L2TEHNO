package l2open.gameserver.communitybbs.Manager;

import l2open.config.*;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.util.Files;

import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import static l2open.gameserver.model.L2Zone.ZoneType.*;

public class TeleportBBSManager extends BaseBBSManager
{
	private static Logger _log = Logger.getLogger(TeleportBBSManager.class.getName());
	
	public class CBteleport
	{
		public int TpId = 0;	    // Teport location ID
		public String TpName = "";	// Location name
		public int PlayerId = 0;	// charID
		public int xC = 0;			// Location coords X
		public int yC = 0;			// Location coords Y
		public int zC = 0;			// Location coords Z
	}

	private static TeleportBBSManager _Instance = null;

	public static TeleportBBSManager getInstance()
	{
		if(_Instance == null)
			_Instance = new TeleportBBSManager();
		return _Instance;
	}
	
	public String points[][];

	@Override
	public void parsecmd(String command, L2Player activeChar)
	{
		if(activeChar.getEventMaster() != null && activeChar.getEventMaster().blockBbs())
			return;
		if(command.equals("_bbsteleport;"))
			showTp(activeChar);
		else if(command.startsWith("_bbsteleport;delete;"))
		{
			StringTokenizer stDell = new StringTokenizer(command, ";");
			stDell.nextToken();
			stDell.nextToken();
			int TpNameDell = Integer.parseInt(stDell.nextToken());
			delTp(activeChar, TpNameDell);
			showTp(activeChar);
		}
		else if(command.startsWith("_bbsteleport;save;"))
		{
			StringTokenizer stAdd = new StringTokenizer(command, ";");
			stAdd.nextToken();
			stAdd.nextToken();
			
			String TpNameAdd = null;
			if(stAdd.hasMoreTokens())
				TpNameAdd = stAdd.nextToken();
			if(TpNameAdd == null || TpNameAdd.equals(""))
			{
				activeChar.sendMessage("Вы не ввели имя закладки.");
				return;
			}
			AddTp(activeChar, TpNameAdd);
			showTp(activeChar);
		}
        else if(command.startsWith("_bbsteleport;teleport;"))
		{
			StringTokenizer stGoTp = new StringTokenizer(command, " :");
			stGoTp.nextToken();
			int xTp = Integer.parseInt(stGoTp.nextToken());
			int yTp = Integer.parseInt(stGoTp.nextToken());
			int zTp = Integer.parseInt(stGoTp.nextToken());
			int priceTp = ConfigValue.CBTeleportPrice;
			goTp(activeChar, xTp, yTp, zTp, priceTp);
			showTp(activeChar);
		}
		else
			ShowBoard.separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", activeChar);
	}
	private void goTp(L2Player activeChar, int xTp, int yTp, int zTp, int priceTp)
	{
		if(activeChar.isDead() || activeChar.isMovementDisabled() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.getVar("jailed") != null || activeChar.isCombatFlagEquipped() || activeChar.isTerritoryFlagEquipped())
		{
			activeChar.sendMessage("Телепортация невозможна.");
			return;
		}
		else if(ConfigValue.OnlyCBTeleportPeace && !activeChar.isInZonePeace())
		{
			activeChar.sendMessage("Телепортация доступна только из мирной зоны.");
			return;
		}
		else if(activeChar.getTeam() > 0)
		{
			activeChar.sendMessage("Телепортация на эвентах невозможна.");
			return;			
		}
		else if(ZoneManager.getInstance().checkIfInZone(ZoneType.epic, xTp, yTp, 0) || ZoneManager.getInstance().checkIfInZone(Siege, xTp, yTp, 0) || ZoneManager.getInstance().checkIfInZone(ClanHall, xTp, yTp, 0) || ZoneManager.getInstance().checkIfInZone(Castle, xTp, yTp, 0) || /*ZoneManager.getInstance().checkIfInZone(no_restart, xTp, yTp, 0) || ZoneManager.getInstance().checkIfInZone(no_spawn, xTp, yTp, 0) || ZoneManager.getInstance().checkIfInZone(no_summon, xTp, yTp, 0) || */ZoneManager.getInstance().checkIfInZone(Fortress, xTp, yTp, 0) || ZoneManager.getInstance().checkIfInZone(OlympiadStadia, xTp, yTp, 0))
        {
			activeChar.sendMessage("Телепортация в данную зону невозможна.");
            return;
        }

		// Не телепортируем в город в котором идёт осада.
		Castle castle = TownManager.getInstance().getClosestTown(xTp, yTp).getCastle();
		if(castle != null && castle.getSiege().isInProgress())
		{
			// Определяем, в город ли телепортируется чар
			boolean teleToTown = false;
			int townId = 0;
			for(L2Zone town : ZoneManager.getInstance().getZoneByType(ZoneType.Town))
				if(town.checkIfInZone(xTp, yTp))
				{
					teleToTown = true;
					townId = town.getIndex();
					break;
				}

			if(teleToTown && townId == castle.getTown())
			{
				activeChar.sendMessage("Телепортация невозможна. В данной зоне идёт осада.");
				return;
			}
		}

		if(activeChar.getLevel() > ConfigValue.LevelFreeTP && (priceTp > 0 && activeChar.getAdena() < priceTp))
		{
			activeChar.sendMessage("Недостаточно денег.");
			return;
		}
		else
		{
			if(activeChar.getLevel() > ConfigValue.LevelFreeTP && priceTp > 0)
				activeChar.reduceAdena(priceTp, true);
			activeChar.teleToLocation(xTp,yTp,zTp, 0);
		}
	}
	private void showTp(L2Player activeChar)
	{
        CBteleport tp;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        try 
		{
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM comteleport WHERE charId=?;");
            statement.setLong(1, activeChar.getObjectId());
            rs = statement.executeQuery();
            StringBuilder html = new StringBuilder();
            html.append("<table width=220>");
            while (rs.next()) 
			{
                tp = new CBteleport();
                tp.TpId = rs.getInt("TpId");
                tp.TpName = rs.getString("name");
                tp.PlayerId = rs.getInt("charId");
                tp.xC = rs.getInt("xPos");
                tp.yC = rs.getInt("yPos");
                tp.zC = rs.getInt("zPos");
                html.append("<tr>");
                html.append("<td>");
                html.append("<button value=\"" + tp.TpName + "\" action=\"bypass -h _bbsteleport;teleport; " + tp.xC + " " + tp.yC + " " + tp.zC + " " + 100000 + "\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                html.append("</td>");
                html.append("<td>");
                html.append("<button value=\"Удалить\" action=\"bypass -h _bbsteleport;delete;" + tp.TpId + "\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                html.append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
            String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "50.htm", activeChar);
            content = content.replace("%tp%", html.toString());
            separateAndSend(content, activeChar);
            return;

        } 
		catch (Exception e) 
		{
        } 
		finally
		{
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
    }
	private void delTp(L2Player activeChar, int TpNameDell)
	{
		ThreadConnection conDel = null;
		FiltredPreparedStatement statement = null;
		try
		{
			conDel = L2DatabaseFactory.getInstance().getConnection();
			statement = conDel.prepareStatement("DELETE FROM comteleport WHERE charId=? AND TpId=?;");
			statement.setInt(1, activeChar.getObjectId());
			statement.setInt(2, TpNameDell);
			statement.execute();
						}
		catch (Exception e)
		{
		}
		finally
		{
			try
			{
				conDel.close();
			}
			catch (Exception e)
			{
			}
		}

	}
	
	private void AddTp(L2Player activeChar, String TpNameAdd)
	{
        if(activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isAttackingNow())
        {
            activeChar.sendMessage("Сохранить закладку в вашем состоянии невозможно.");
            return;
        }
		else if(activeChar.isInCombat())
        {
            activeChar.sendMessage("Сохранить закладку в боевом режиме нельзя.");
            return;
        }
		else if(activeChar.isInZone(ZoneType.epic) || activeChar.isInZone(Siege) || activeChar.isInZone(ClanHall) || activeChar.isInZone(Castle) || /*activeChar.isInZone(no_restart) || activeChar.isInZone(no_spawn) || activeChar.isInZone(no_summon) || */activeChar.isInZone(Fortress) || activeChar.isFlying() || activeChar.isInZone(OlympiadStadia) || activeChar.getVar("jailed") != null)
        {
            activeChar.sendMessage("Нельзя сохранить данную локацию.");
            return;
        }
		else if(TpNameAdd.equals("") || TpNameAdd.equals(null))
		{
			activeChar.sendMessage("Вы не ввели имя закладки.");
			return;
		}
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT COUNT(*) FROM comteleport WHERE charId=?;");
			statement.setLong(1, activeChar.getObjectId());
			ResultSet rs = statement.executeQuery();
			rs.next();
			if(rs.getInt(1) <= 9)
			{	
				if(ConfigValue.CBTeleportSavePrice > 0 && activeChar.getAdena() < ConfigValue.CBTeleportSavePrice)
				{
					activeChar.sendMessage("Недостаточно денег.");
					return;
				}
				else
				{
					if(ConfigValue.CBTeleportSavePrice > 0)
						activeChar.reduceAdena(ConfigValue.CBTeleportSavePrice, true);
				}	
				statement = con.prepareStatement("SELECT COUNT(*) FROM comteleport WHERE charId=? AND name=?;");
				statement.setLong(1, activeChar.getObjectId());
				statement.setString(2, TpNameAdd);
				ResultSet rs1 = statement.executeQuery();
				rs1.next();
				if(rs1.getInt(1) == 0)
				{		
					statement = con.prepareStatement("INSERT INTO comteleport (charId,xPos,yPos,zPos,name) VALUES(?,?,?,?,?)");
					statement.setInt(1, activeChar.getObjectId());
					statement.setInt(2, activeChar.getX());
					statement.setInt(3, activeChar.getY());
					statement.setInt(4, activeChar.getZ());
					statement.setString(5, TpNameAdd);
					statement.execute();
				}
				else
				{
					statement = con.prepareStatement("UPDATE comteleport SET xPos=?, yPos=?, zPos=? WHERE charId=? AND name=?;");
					statement.setInt(1, activeChar.getObjectId());
					statement.setInt(2, activeChar.getX());
					statement.setInt(3, activeChar.getY());
					statement.setInt(4, activeChar.getZ());
					statement.setString(5, TpNameAdd);
					statement.execute();
				}			
			}
			else
				activeChar.sendMessage("Вы не можете сохранить более десяти закладок.");

		}
		catch (Exception e)
		{
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}
	}
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{
	
	}
}