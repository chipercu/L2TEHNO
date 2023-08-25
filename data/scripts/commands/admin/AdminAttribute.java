package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;

public class AdminAttribute implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_setatreh, // 6
		admin_setatrec, // 10
		admin_setatreg, // 9
		admin_setatrel, // 11
		admin_setatreb, // 12
		admin_setatrew, // 7
		admin_setatres, // 8
		admin_setatrle, // 1
		admin_setatrre, // 2
		admin_setatrlf, // 4
		admin_setatrrf, // 5
		admin_setatren, // 3
		admin_setatrun, // 0
		admin_setatrbl, // 24
		admin_attribute
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		int armorType = -1;

		switch(command)
		{
			case admin_attribute:
				showMainPage(activeChar);
				return true;
			case admin_setatreh:
				armorType = Inventory.PAPERDOLL_HEAD;
				break;
			case admin_setatrec:
				armorType = Inventory.PAPERDOLL_CHEST;
				break;
			case admin_setatreg:
				armorType = Inventory.PAPERDOLL_GLOVES;
				break;
			case admin_setatreb:
				armorType = Inventory.PAPERDOLL_FEET;
				break;
			case admin_setatrel:
				armorType = Inventory.PAPERDOLL_LEGS;
				break;
			case admin_setatrew:
				armorType = Inventory.PAPERDOLL_RHAND;
				break;
			case admin_setatres:
				armorType = Inventory.PAPERDOLL_LHAND;
				break;
			case admin_setatrle:
				armorType = Inventory.PAPERDOLL_LEAR;
				break;
			case admin_setatrre:
				armorType = Inventory.PAPERDOLL_REAR;
				break;
			case admin_setatrlf:
				armorType = Inventory.PAPERDOLL_LFINGER;
				break;
			case admin_setatrrf:
				armorType = Inventory.PAPERDOLL_RFINGER;
				break;
			case admin_setatren:
				armorType = Inventory.PAPERDOLL_NECK;
				break;
			case admin_setatrun:
				armorType = Inventory.PAPERDOLL_UNDER;
				break;
			case admin_setatrbl:
				armorType = Inventory.PAPERDOLL_BELT;
				break;
		}

		if(armorType == -1 || wordList.length < 2)
		{
			showMainPage(activeChar);
			return true;
		}

		try
		{
			
			int ench = Integer.parseInt(wordList[1]);
			byte element = -2;
			
            if (wordList[2].equals("Fire")) element=0;
            if (wordList[2].equals("Water")) element=1;
            if (wordList[2].equals("Wind")) element=2;
            if (wordList[2].equals("Earth")) element=3;
            if (wordList[2].equals("Holy")) element=4;
            if (wordList[2].equals("Dark")) element=5;
       
			
					if (ench < 0 || ench > 600)
					{
						if(activeChar.getVar("lang@").equalsIgnoreCase("ru"))
							activeChar.sendMessage("Допустимое значение для заточки атрибутом является значение от 0 до 600.");
						else 
							activeChar.sendMessage("You must set the enchant level for ARMOR to be between 0-600.");
					}
					else
					setEnchant(activeChar, ench, element, armorType);
		}
		catch(StringIndexOutOfBoundsException e)
		{
			if(activeChar.getVar("lang@").equalsIgnoreCase("ru"))
				activeChar.sendMessage("Пожалуйста, укажите новое значение для заточки.");
			else 
				activeChar.sendMessage("Please specify a new enchant value.");
		}
		catch(NumberFormatException e)
		{
			if(activeChar.getVar("lang@").equalsIgnoreCase("ru"))
				activeChar.sendMessage("Пожалуйста, правильное значение для заточки.");
			else 
				activeChar.sendMessage("Please specify a valid new enchant value.");
		}

		// show the enchant menu after an action
		showMainPage(activeChar);
		return true;
	}

	private void setEnchant(L2Player activeChar, int value, byte element , int armorType)
	{
		L2Object target = activeChar.getTarget();
		if(target == null)
			target = activeChar;
		if(!target.isPlayer())
		{
			if(activeChar.getVar("lang@").equalsIgnoreCase("ru"))
				activeChar.sendMessage("Неверный тип цели.");
			else 
				activeChar.sendMessage("Wrong target type.");
            return;
		}

		L2Player player = (L2Player) target;

		int curEnchant = 0;
		
		L2ItemInstance item = player.getInventory().getPaperdollItem(armorType);
		if(item != null)
		{
			curEnchant = item.getEnchantLevel();
			if(item.isWeapon()) 
			{
				player.getInventory().unEquipItemInSlot(item.getEquipSlot());
				item.setAttributeElement(element, value, new int[] {0,0,0,0,0,0}, true);
				player.getInventory().equipItem(item, false);
				player.sendPacket(new InventoryUpdate().addModifiedItem(item));
				player.broadcastUserInfo(true);
			}
			if(item.isArmor()) 
			{
				if(!canEnchantArmorAttribute(element, item)) 
				{
					if(activeChar.getVar("lang@").equalsIgnoreCase("ru"))
						activeChar.sendMessage("Невозможно вставить аттрибут в броню, не соблюдены условия.");
					else 
						activeChar.sendMessage("Unable to insert an attribute in the armor, not the conditions.");
                    return;
                }
			
				player.getInventory().unEquipItemInSlot(item.getEquipSlot());
                int[] deffAttr = item.getDeffAttr();
                deffAttr[element] = value;
                item.setAttributeElement((byte)-2, 0, deffAttr, true);
                player.getInventory().equipItem(item, false);
                player.sendPacket(new InventoryUpdate().addModifiedItem(item));
                player.broadcastUserInfo(true);
			}
			String ElementName = "";  
			if(activeChar.getVar("lang@").equalsIgnoreCase("ru"))
			{
				switch ( element)
				{
					case 0 : ElementName = "Огня"; break;
					case 1 : ElementName = "Воды"; break;
					case 2 : ElementName = "Ветра"; break;
					case 3 : ElementName = "Земли"; break;
					case 4 : ElementName = "Святости"; break;
					case 5 : ElementName = "Тьмы"; break;
				}
				activeChar.sendMessage("Вы изменили Атрибут " + ElementName + " на " + value + " в " + item.getName() + " +" + curEnchant + ".");
				player.sendMessage("Админ изменил значение Атрибута " + ElementName + " на " + value + " в " + item.getName() + " +" + curEnchant + ".");
			} else {
				switch ( element)
				{
					case 0 : ElementName = "Fire"; break;
					case 1 : ElementName = "Water"; break;
					case 2 : ElementName = "Wind"; break;
					case 3 : ElementName = "Earth"; break;
					case 4 : ElementName = "Holy"; break;
					case 5 : ElementName = "Dark"; break;
				}
				activeChar.sendMessage("You have changed attribute " + ElementName + " on " + value + " in " + item.getName() + " +" + curEnchant + ".");
				player.sendMessage("Admin has changed the value of the attribute " + ElementName + " on " + value + " in " + item.getName() + " +" + curEnchant + ".");
			
			}
        }
	}
	
	private boolean canEnchantArmorAttribute(int attr, L2ItemInstance item) {
        switch(attr) {
            case 0:
                if(item.getDeffAttr()[1] != 0)
                    return false;
                break;
            case 1:
                if(item.getDeffAttr()[0] != 0)
                    return false;
                break;
            case 2:
                if(item.getDeffAttr()[3] != 0)
                    return false;
                break;
            case 3:
                if(item.getDeffAttr()[2] != 0)
                    return false;
                break;
            case 4:
                if(item.getDeffAttr()[5] != 0)
                    return false;
                break;
            case 5:
                if(item.getDeffAttr()[4] != 0)
                    return false;
                break;
        }
        return true;
    }
	
	private void showMainPage(L2Player activeChar)
	{
		activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/attribute.htm"));
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