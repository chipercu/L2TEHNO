package commands.admin;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ItemList;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Util;
import l2open.util.Log;
import l2open.util.Rnd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminCreateItem implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_itemcreate,
		admin_create_item,
		admin_create_item_all,
		admin_create_item_target,
		admin_spreaditem,
		admin_summon,
		admin_delitem,
		admin_d_item,
		admin_item_create
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().UseGMShop)
			return false;

		switch(command)
		{
			case admin_d_item:
				if(wordList.length == 2 && activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
				{
					int item_id = Integer.parseInt(wordList[1]);
					for(L2ItemInstance item : activeChar.getTarget().getPlayer().getInventory().getItemsList())
						if(item_id == item.getItemId())
							activeChar.getTarget().getPlayer().getInventory().destroyItem(item, item.getCount(), true);
				}
				else if(wordList.length == 1)
					activeChar.sendMessage("Вы не указали ID предмета для удаления.");
				else
					activeChar.sendMessage("Не верный таргет.");
				break;
			case admin_delitem:
				for(L2ItemInstance item : activeChar.getInventory().getItemsList())
					activeChar.getInventory().destroyItem(item, item.getCount(), true);
				activeChar.sendMessage("Ваш рюкзак очищен!");
				break;
			case admin_itemcreate:
				AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
				break;
			case admin_create_item:
				try
				{
					if(wordList.length == 3)
						createItem(activeChar, Integer.parseInt(wordList[1]), Long.parseLong(wordList[2]));
					else if(wordList.length == 2)
						createItem(activeChar, Integer.parseInt(wordList[1]), 1);
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage("Specify a valid number.");
				}
				catch(StringIndexOutOfBoundsException e)
				{
					activeChar.sendMessage("Can't create this item.");
				}
				AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
				break;
			case admin_create_item_target:
				try
				{
					if(wordList.length == 3)
						createItem((L2Player)activeChar.getTarget(), Integer.parseInt(wordList[1]), Long.parseLong(wordList[2]));
					else if(wordList.length == 2)
						createItem((L2Player)activeChar.getTarget(), Integer.parseInt(wordList[1]), 1);
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage("Specify a valid number.");
				}
				catch(StringIndexOutOfBoundsException e)
				{
					activeChar.sendMessage("Can't create this item.");
				}
				AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
				break;
			case admin_spreaditem:
				try
				{
					int id = Integer.parseInt(wordList[1]);
					int num = wordList.length > 2 ? Integer.parseInt(wordList[2]) : 1;
					long count = wordList.length > 3 ? Long.parseLong(wordList[3]) : 1;
					for(int i = 0; i < num; i++)
					{
						L2ItemInstance createditem = ItemTemplates.getInstance().createItem(id);
						createditem.setCount(count);
						createditem.dropToTheGround(activeChar, Rnd.coordsRandomize(activeChar, 100));
					}
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage("Specify a valid number.");
				}
				catch(StringIndexOutOfBoundsException e)
				{
					activeChar.sendMessage("Can't create this item.");
				}
				break;
			case admin_summon:
				try
				{
					if(wordList.length == 3)
					{
						if(Integer.parseInt(wordList[1]) > 1000000)
							spawnMonster(activeChar, wordList[1], 30, Integer.parseInt(wordList[2]));
						else
							createItem(activeChar, Integer.parseInt(wordList[1]), Long.parseLong(wordList[2]));
					}
					else if(wordList.length == 2)
					{
						if(Integer.parseInt(wordList[1]) > 1000000)
							spawnMonster(activeChar, wordList[1], 30, 1);
						else
							createItem(activeChar, Integer.parseInt(wordList[1]), 1);
					}
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage("Specify a valid number.");
				}
				catch(StringIndexOutOfBoundsException e)
				{
					activeChar.sendMessage("Can't create this item.");
				}
				break;
			case admin_create_item_all:
				for(L2Player pl : L2ObjectsStorage.getPlayers())
				{
					if(pl != null && wordList.length == 3)
						createItem(pl, Integer.parseInt(wordList[1]), Long.parseLong(wordList[2]));
					else if(pl != null && wordList.length == 2)
						createItem(pl, Integer.parseInt(wordList[1]), 1);
				}
				break;
			case admin_item_create:
				for(L2Player pl : L2World.getAroundPlayers(activeChar, Integer.parseInt(wordList[4]), 200, true))
				{
					if(pl != null)
						createItem(pl, Integer.parseInt(wordList[1]), Long.parseLong(wordList[2]));
				}
				break;
			
		}

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void createItem(L2Player activeChar, int id, long num)
	{
		L2ItemInstance createditem = ItemTemplates.getInstance().createItem(id);
		createditem.setCount(num);
		activeChar.getInventory().addItem(createditem);
		Log.LogItem(activeChar, Log.Adm_AddItem, createditem);
		if(!createditem.isStackable())
			for(long i = 0; i < num - 1; i++)
			{
				createditem = ItemTemplates.getInstance().createItem(id);
				activeChar.getInventory().addItem(createditem);
				Log.LogItem(activeChar, Log.Adm_AddItem, createditem);
			}
		activeChar.sendPacket(new ItemList(activeChar, true), SystemMessage.obtainItems(id, num, 0));
	}

	public void spawnMonster(L2Player activeChar, String monsterId, int respawnTime, int mobCount)
	{
		L2Object target = activeChar.getTarget();
		if(target == null)
			target = activeChar;

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher regexp = pattern.matcher(monsterId);
		L2NpcTemplate template;
		if(regexp.matches())
		{
			// First parameter was an ID number
			int monsterTemplate = Integer.parseInt(monsterId) - 1000000;

			if(Util.contains_int(ConfigValue.NotMobSpawnId,monsterTemplate))
			{
				activeChar.sendMessage("This mob cannot be spawned.");
				return;
			}
			template = NpcTable.getTemplate(monsterTemplate);
		}
		else
		{
			// First parameter wasn't just numbers so go by name not ID
			monsterId = monsterId.replace('_', ' ');
			template = NpcTable.getTemplateByName(monsterId);
		}

		if(template == null)
		{
			activeChar.sendMessage("Incorrect monster template.");
			return;
		}

		try
		{
			L2Spawn spawn = new L2Spawn(template);
			spawn.setLoc(target.getLoc());
			spawn.setLocation(0);
			spawn.setAmount(mobCount);
			spawn.setHeading(activeChar.getHeading());
			spawn.setRespawnDelay(respawnTime);
			spawn.setReflection(activeChar.getReflection().getId());

			if(RaidBossSpawnManager.getInstance().isDefined(spawn.getNpcId()))
				activeChar.sendMessage("Raid Boss " + template.name + " already spawned.");
			else
			{
				spawn.init();
				if(respawnTime == 0)
					spawn.stopRespawn();
				activeChar.sendMessage("Created " + template.name + " on " + target.getObjectId() + ".");
			}
			if(ConfigValue.saveAdminSpawn)
				addNewSpawn(spawn, activeChar.getName());
		}
		catch(Exception e)
		{
			activeChar.sendMessage("Target is not ingame.");
		}
	}

	public void addNewSpawn(L2Spawn spawn, String adminName)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO `spawnlist` (location,count,npc_templateid,locx,locy,locz,heading,respawn_delay,loc_id,aiParam) values(?,?,?,?,?,?,?,?,?,-1)");
			statement.setString(1, "AdminSpawn: " + adminName);
			statement.setInt(2, spawn.getAmount());
			statement.setInt(3, spawn.getNpcId());
			statement.setInt(4, spawn.getLocx());
			statement.setInt(5, spawn.getLocy());
			statement.setInt(6, spawn.getLocz());
			statement.setInt(7, spawn.getHeading());
			statement.setInt(8, spawn.getRespawnDelay());
			statement.setInt(9, spawn.getLocation());
			statement.execute();
		}
		catch(Exception e1)
		{
			_log.warning("spawn couldnt be stored in db:" + e1);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
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