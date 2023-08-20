package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.model.CursedWeapon;
import l2open.gameserver.model.CursedWeapon.CursedWeaponState;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.xml.ItemTemplates;

public class AdminCursedWeapons implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_cw_info,
		admin_cw_remove,
		admin_cw_goto,
		admin_cw_reload,
		admin_cw_add,
		admin_cw_drop
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Menu)
			return false;

		CursedWeaponsManager cwm = CursedWeaponsManager.getInstance();

		CursedWeapon cw = null;
		switch(command)
		{
			case admin_cw_remove:
			case admin_cw_goto:
			case admin_cw_add:
			case admin_cw_drop:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Вы не указали id");
					return false;
				}
				for(CursedWeapon cwp : CursedWeaponsManager.getInstance().getCursedWeapons())
					if(cwp.getName().toLowerCase().contains(wordList[1].toLowerCase()))
						cw = cwp;
				if(cw == null)
					try
					{
						for(CursedWeapon cwp : CursedWeaponsManager.getInstance().getCursedWeapons())
							if(cwp.getItemId() == Integer.parseInt(wordList[1]))
								cw = cwp;
					}
					catch(Exception e)
					{}
				if(cw == null)
				{
					activeChar.sendMessage("Неизвестный id");
					return false;
				}
				break;
		}

		switch(command)
		{
			case admin_cw_info:
				activeChar.sendMessage("======= Cursed Weapons: =======");
				for(CursedWeapon c : cwm.getCursedWeapons())
				{
					activeChar.sendMessage("> " + c.getName() + " (" + c.getItemId() + ")");
					if(c.isActivated())
					{
						L2Player pl = c.getPlayer();
						activeChar.sendMessage("  Player holding: " + (pl == null ? c.getPlayerName()/** Правда так будет не всегда показывать имя но похуй))) **/ : pl.getName()));
						activeChar.sendMessage("  Player karma: " + c.getPlayerKarma());
						activeChar.sendMessage("  Time Remaining: " + c.getTimeLeft() / 60000 + " min.");
						activeChar.sendMessage("  Kills : " + c.getNbKills());
					}
					else if(c.isDropped())
					{
						activeChar.sendMessage("  Lying on the ground.");
						activeChar.sendMessage("  Time Remaining: " + c.getTimeLeft() / 60000 + " min.");
						activeChar.sendMessage("  Kills : " + c.getNbKills());
					}
					else
						activeChar.sendMessage("  Don't exist in the world.");
				}
				break;
			case admin_cw_reload:
				cwm.reload();
				activeChar.sendMessage("Cursed weapons reloaded.");
				break;
			case admin_cw_remove:
				if(cw == null)
					return false;
				CursedWeaponsManager.getInstance().endOfLife(cw);
				break;
			case admin_cw_goto:
				if(cw == null && cw.getWorldPosition() != null)
					return false;
				activeChar.teleToLocation(cw.getWorldPosition());
				break;
			case admin_cw_add:
				if(cw == null)
					return false;
				if(cw.isActive())
					activeChar.sendMessage("This cursed weapon is already active.");
				else
				{
					L2Object target = activeChar.getTarget();
					if(target != null && target.isPlayer() && !((L2Player) target).isInOlympiadMode() && cw.getPlayerId() <=0)
					{
						L2Player player = (L2Player) target;
						L2ItemInstance item = ItemTemplates.getInstance().createItem(cw.getItemId());

						if(item != null)
						{
							cw.setState(CursedWeaponState.DROPPED);

							if(cw.getEndTime() == 0)
								cw.setEndTime(System.currentTimeMillis() + cw.getRndDuration() * 60000);
							cw.setLoc(item.getLoc());
							item.setDropTime(0);

							L2GameServerPacket redSky = new ExRedSky(10);
							L2GameServerPacket eq = new Earthquake(player.getLoc(), 30, 12);
							for(L2Player aPlayer : L2ObjectsStorage.getPlayers())
								aPlayer.sendPacket(redSky, eq);
							player.getInventory().addItem(item);
						}
					}
				}
				break;
			case admin_cw_drop:
				if(cw == null)
					return false;
				if(cw.isActive())
					activeChar.sendMessage("This cursed weapon is already active.");
				else
				{
					L2Object target = activeChar.getTarget();
					if(target != null && target.isPlayer() && !((L2Player) target).isInOlympiadMode())
					{
						L2Player player = (L2Player) target;
						cw.create(null, player, true);
					}
				}
				break;
		}
		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}