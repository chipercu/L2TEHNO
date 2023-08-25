package commands.voiced;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;

import java.util.*;

public class AutoCp extends Functions implements IVoicedCommandHandler, ScriptFile {

	private String[] _commandList = new String[]{"autocp", "acp"};

	private static StringBuilder _items_hp = new StringBuilder();
	private static StringBuilder _items_mp = new StringBuilder();
	private static StringBuilder _items_cp = new StringBuilder();

	private static Map<String, Integer> _item = new HashMap<String, Integer>();

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		if((ConfigValue.AutoCpEnable || activeChar.isGM()) && (!ConfigValue.AutoCpOnlyPremim || activeChar.hasBonus()) && (command.equals("autocp") || command.equals("acp")))
		{
			String dialog = Files.read("data/scripts/commands/voiced/autocp.htm", activeChar);

			if(args != null && !args.isEmpty())
			{
				//activeChar.sendMessage("autocp["+command+"]: args='"+args+"'");
				String[] param = args.split(" ");
				if(args.startsWith("hpi"))
				{
					activeChar._item_id_auto_cp_hp = _item.get(args.substring(4));
					activeChar.setVar("hp_item", String.valueOf(activeChar._item_id_auto_cp_hp), -1);
				}
				else if(args.startsWith("mpi"))
				{
					activeChar._item_id_auto_cp_mp = _item.get(args.substring(4));
					activeChar.setVar("mp_item", String.valueOf(activeChar._item_id_auto_cp_mp), -1);
				}
				else if(args.startsWith("cpi"))
				{
					activeChar._item_id_auto_cp_cp = _item.get(args.substring(4));
					activeChar.setVar("cp_item", String.valueOf(activeChar._item_id_auto_cp_cp), -1);
				}
				else if(param.length == 1)
				{
					if(param[0].equals("on"))
					{
						activeChar._enable_auto = true;
						activeChar.setVar("enable_auto", "true", -1);

						activeChar.sendMessage("AutoCp Enable");
					}
					else if(param[0].equals("off"))
					{
						activeChar._enable_auto = false;
						activeChar.setVar("enable_auto", "false", -1);

						activeChar.sendMessage("AutoCp Disable");
					}
					else if(param[0].equals("cp_select"));
					else if(param[0].equals("hp_select"));
					else if(param[0].equals("mp_select"));

					return true;
				}
				else if(param.length == 2)
				{
					String type = param[0];
					//activeChar.sendMessage("autocp["+command+"]: type="+type+" value="+param[1]);
					{
						int value = Integer.parseInt(param[1]);

						if(type.equals("cp_percent"))
						{
							activeChar._enable_auto_cp_cp = value;
							activeChar.setVar("cp_percent", String.valueOf(value), -1);
						}
						else if(type.equals("hp_percent"))
						{
							activeChar._enable_auto_cp_hp = value;
							activeChar.setVar("hp_percent", String.valueOf(value), -1);
						}
						else if(type.equals("mp_percent"))
						{
							activeChar._enable_auto_cp_mp = value;
							activeChar.setVar("mp_percent", String.valueOf(value), -1);
						}
						else if(type.equals("cp_time"))
						{
							activeChar._time_auto_cp_cp = Math.max(value, 333);
							activeChar.setVar("cp_time", String.valueOf(Math.max(value, 333)), -1);
						}
						else if(type.equals("hp_time"))
						{
							activeChar._time_auto_cp_hp = Math.max(value, 333);
							activeChar.setVar("hp_time", String.valueOf(Math.max(value, 333)), -1);
						}
						else if(type.equals("mp_time"))
						{
							activeChar._time_auto_cp_mp = Math.max(value, 333);
							activeChar.setVar("mp_time", String.valueOf(Math.max(value, 333)), -1);
						}
					}
				}
			}
			String n1 = ItemTemplates.getInstance().getTemplate(activeChar._item_id_auto_cp_hp).getName()+";";
			String n2 = ItemTemplates.getInstance().getTemplate(activeChar._item_id_auto_cp_mp).getName()+";";
			String n3 = ItemTemplates.getInstance().getTemplate(activeChar._item_id_auto_cp_cp).getName()+";";

			String t1 = _items_hp.toString().replace(n1, "");
			String t2 = _items_mp.toString().replace(n2, "");
			String t3 = _items_cp.toString().replace(n3, "");

			dialog = dialog.replace("<?item_hp?>", n1+t1);
			dialog = dialog.replace("<?item_mp?>", n2+t2);
			dialog = dialog.replace("<?item_cp?>", n3+t3);

			dialog = dialog.replace("<?enable_auto_cp_cp?>", "<font color="+(activeChar._enable_auto_cp_cp == 0 ? "FF0000" : "00FF00")+">"+activeChar._enable_auto_cp_cp+"%</font>");
			dialog = dialog.replace("<?enable_auto_cp_hp?>", "<font color="+(activeChar._enable_auto_cp_hp == 0 ? "FF0000" : "00FF00")+">"+activeChar._enable_auto_cp_hp+"%</font>");
			dialog = dialog.replace("<?enable_auto_cp_mp?>", "<font color="+(activeChar._enable_auto_cp_mp == 0 ? "FF0000" : "00FF00")+">"+activeChar._enable_auto_cp_mp+"%</font>");

			dialog = dialog.replace("<?time_auto_cp_cp?>", "<font color=00FF00>"+activeChar._time_auto_cp_cp+"</font>");
			dialog = dialog.replace("<?time_auto_cp_hp?>", "<font color=00FF00>"+activeChar._time_auto_cp_hp+"</font>");
			dialog = dialog.replace("<?time_auto_cp_mp?>", "<font color=00FF00>"+activeChar._time_auto_cp_mp+"</font>");

			dialog = dialog.replace("<?enable_auto?>", "<font color="+(activeChar._enable_auto ? "00FF00" : "FF0000")+">"+(activeChar._enable_auto ? "Включено" : "Отключено")+"</font>");

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
		for(int id : ConfigValue.AutoCpPointsHp)
		{
			String name = ItemTemplates.getInstance().getTemplate(id).getName();
			_items_hp.append(name).append(";");
			_item.put(name, id);
		}
		for(int id : ConfigValue.AutoCpPointsMp)
		{
			String name = ItemTemplates.getInstance().getTemplate(id).getName();
			_items_mp.append(name).append(";");
			_item.put(name, id);
		}
		for(int id : ConfigValue.AutoCpPointsCp)
		{
			String name = ItemTemplates.getInstance().getTemplate(id).getName();
			_items_cp.append(name).append(";");
			_item.put(name, id);
		}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}