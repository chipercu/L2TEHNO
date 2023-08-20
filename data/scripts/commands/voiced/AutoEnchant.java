package commands.voiced;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.*;
import l2open.util.Files;

import static services.Talks.Kief.getPoints;

public class AutoEnchant extends Functions implements IVoicedCommandHandler, ScriptFile {

	private String[] _commandList = new String[]{"autoenchant"};

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		
		if((ConfigValue.EnableAutoEnchant || activeChar.isGM()) && (!ConfigValue.EnableAutoEnchantOnlyPa || activeChar.hasBonus()) && command.equals("autoenchant"))
		{
			String dialog = Files.read("data/scripts/commands/voiced/autoenchant.htm", activeChar);

			if(args != null && !args.isEmpty())
			{
				String[] param = args.split(" ");
				if(param.length == 2 && !param[0].isEmpty() && !param[1].isEmpty())
				{
					String type = param[0];
					//activeChar.sendMessage("autocp["+command+"]: type="+type+" value="+value);
					
					if(type.equals("bless"))
					{
						activeChar.setVar("AutoEnchantAfterFailBlessed", param[1]);
					}
					else if(type.equals("destract"))
					{
						activeChar.setVar("AutoEnchantAfterFailDestract", param[1]);
					}
					else if(type.equals("crystal"))
					{
						activeChar.setVar("AutoEnchantAfterFailCrystal", param[1]);
					}
					else if(type.equals("ancient"))
					{
						activeChar.setVar("AutoEnchantAfterFailAncient", param[1]);
					}
					else if(type.equals("scrol"))
					{
						activeChar.setVar("AutoEnchantItemScrolCount", String.valueOf(Math.max(1, Integer.parseInt(param[1]))));
					}
					else if(type.equals("state"))
					{
						activeChar.setVar("AutoEnchantItemEnable", param[1]);
					}
				}
			}

			int AutoEnchantItemEnable = activeChar.getVarInt("AutoEnchantItemEnable", -1);
			dialog = dialog.replace("<?autoenchant_value?>", "<font color="+(AutoEnchantItemEnable == 0 ? "FF0000" : "00FF00")+">"+AutoEnchantItemEnable+"</font>");			
			dialog = dialog.replace("<?autoenchant_scrol?>", "<font color=00FF00>"+activeChar.getVarInt("AutoEnchantItemScrolCount", 1)+"</font>");			

			dialog = dialog.replace("<?enable_bless?>", (activeChar.getVarB("AutoEnchantAfterFailBlessed", false) ? "false\" value=\"Disable\"" : "true\" value=\"Enable\""));
			dialog = dialog.replace("<?enable_destract?>", (activeChar.getVarB("AutoEnchantAfterFailDestract", false) ? "false\" value=\"Disable\"" : "true\" value=\"Enable\""));
			dialog = dialog.replace("<?enable_crystal?>", (activeChar.getVarB("AutoEnchantAfterFailCrystal", false) ? "false\" value=\"Disable\"" : "true\" value=\"Enable\""));
			dialog = dialog.replace("<?enable_ancient?>", (activeChar.getVarB("AutoEnchantAfterFailAncient", false) ? "false\" value=\"Disable\"" : "true\" value=\"Enable\""));

			show(dialog, activeChar);
			return true;
		}
		return true;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}