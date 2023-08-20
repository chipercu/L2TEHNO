package npc.model;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;

public class AurikInstance extends L2NpcInstance
{
	public AurikInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		showHtmlFile(player, "data/scripts/events/RewardofHonor/index.htm");
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(this.getNpcId() != ConfigValue.ERewardofHonorManager)
			return;

		if(command.equalsIgnoreCase("start"))
		{
			if(player.getVar("RewardofHonorStatus") != null)
			{
				showHtmlFile(player, "data/scripts/events/RewardofHonor/already.htm");
				return;
			}

			player.setVar("RewardofHonorStatus", "1");
			showHtmlFile(player, "data/scripts/events/RewardofHonor/start.htm");
		}
		else if(command.equalsIgnoreCase("about"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("data/scripts/events/RewardofHonor/about.htm");

			html.replace("%monster%", getMonsterList());
			html.replace("%count%", String.valueOf(ConfigValue.ERewardofHonorKills));

			player.sendPacket(html);
		}
		else if(command.equalsIgnoreCase("reward"))
		{
			showHtmlFile(player, "data/scripts/events/RewardofHonor/reward.htm");
		}
		else if(command.startsWith("reward_info"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("data/scripts/events/RewardofHonor/reward_info.htm");

			String text = "";
			String[] bypass = command.split(" ");
			int id = Integer.parseInt(bypass[1]);

			for(int i = 0; i < ConfigValue.ERewardofHonorReward[id].length; i++)
			{
				String template = Files.read("data/scripts/events/RewardofHonor/reward_info_template.htm", player);

				L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ERewardofHonorReward[id][i]);
				template = template.replace("%name%", item.getName());
				template = template.replace("%icon%", item.getIcon());
				template = template.replace("%grade%", String.valueOf(item.getItemGrade()));

				text += template;
			}
			html.replace("%list%", text);
			html.replace("%id%", String.valueOf(id));

			player.sendPacket(html);
		}
		else if(command.startsWith("get_reward"))
		{
			int var = player.getVarInt("RewardofHonorStatus");
			if(var != 2)
			{
				String html = var == 0 ? "need_kills" : "has_reward";
				showHtmlFile(player, "data/scripts/events/RewardofHonor/" + html + ".htm");
				return;
			}

			String[] bypass = command.split(" ");
			reward(player, Integer.parseInt(bypass[1]));

			player.setVar("RewardofHonorStatus", "0");
		}
		else
			super.onBypassFeedback(player, command);
	}

	private String getMonsterList()
	{
		String list = "";
		for(int i = 0; i < ConfigValue.ERewardofHonorMonster.length; i++)
			list += ((i != 0 ? (ConfigValue.ERewardofHonorMonster.length - 1 == i ? " или " : ", ") : "") + "<font color=\"LEVEL\">" + DifferentMethods.getNpcName(ConfigValue.ERewardofHonorMonster[i]) + "</font>");

		return list;
	}

	public void reward(L2Player player, int list)
	{
		for(int i : ConfigValue.ERewardofHonorReward[list])
			Functions.addItem(player, i, 1);
		showHtmlFile(player, "data/scripts/events/RewardofHonor/get_reward.htm");
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(file);
		player.sendPacket(html);
	}
}
