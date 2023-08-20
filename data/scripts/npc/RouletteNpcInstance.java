package npc.model;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.Util;

import java.util.Map;
import java.util.HashMap;

public final class RouletteNpcInstance extends L2NpcInstance
{
	private static Map<Integer, Long> _char_list = new HashMap<Integer, Long>();
	private static Map<Integer, Long> _item_list = new HashMap<Integer, Long>();
	public static volatile long all_spent = 0;
	public RouletteNpcInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		all_spent = ServerVariables.getLong("all_spent", 0);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equals("start"))
		{
			if(Functions.getItemCount(player, (int)ConfigValue.RouletteNpcPrice[0]) > ConfigValue.RouletteNpcPrice[1])
			{
				Functions.removeItem(player, (int)ConfigValue.RouletteNpcPrice[0], (long)ConfigValue.RouletteNpcPrice[1]);

				all_spent = all_spent+ConfigValue.RouletteNpcPrice[1];
				long spent = _char_list.containsKey(player.getObjectId()) ? _char_list.get(player.getObjectId()) : 0;
				spent = spent+ConfigValue.RouletteNpcPrice[1];
				_char_list.put(player.getObjectId(), spent);

				String win_list = "";
				boolean win = false;
				boolean announce = false;
				for(int i=0;i<ConfigValue.RouletteNpcRewardRnd.length;i++)
					if(win(i) /*|| i==(ConfigValue.RouletteNpcRewardRnd.length-1)*/)
					{
						int item_id = (int)ConfigValue.RouletteNpcRewardRnd[i][0];
						long item_count = (long)ConfigValue.RouletteNpcRewardRnd[i][1];

						win_list+= (item_id+","+item_count+";");

						Functions.addItem(player, item_id, item_count);
						if(Util.contains(ConfigValue.RouletteNpcAnons, item_id))
							announce = true;
						win = true;
					}
				if(win)
				{
					if(announce)
						Announcements.getInstance().announceByCustomMessage("RouletteNpcWinAnnounce", new String[]{player.getName()});

					player.sendMessage(new CustomMessage("RouletteNpcWin", player));
					Log.add("WIN: "+player.getName()+":->spent="+spent+" all_spent="+all_spent+" WIN: "+win_list,"roulet_game");
				}
				else
				{
					player.sendMessage(new CustomMessage("RouletteNpcLoose", player));
					Log.add("LOOSE: "+player.getName()+":->spent="+spent+" all_spent="+all_spent,"roulet_game");
				}
			}
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
		}
		else
			super.onBypassFeedback(player, command);
	}

	private synchronized boolean win(int index)
	{
		int item_id = (int)ConfigValue.RouletteNpcRewardRnd[index][0];
		long item_count = (long)ConfigValue.RouletteNpcRewardRnd[index][1];

		int index_a = Util.getAIndexLong(item_id, ConfigValue.RouletteNpcRewardLimit, 0);
		if(index_a > -1)
		{
			long limit = ConfigValue.RouletteNpcRewardLimit[index_a][1];
			long spent = (_item_list.containsKey(item_id) ? _item_list.get(item_id) : 0);

			if(spent+item_count <= limit && Rnd.chance(ConfigValue.RouletteNpcRewardRnd[index][2]))
			{
				_item_list.put(item_id, spent+item_count);
				return true;
			}
			return false;
		}

		return Rnd.chance(ConfigValue.RouletteNpcRewardRnd[index][2]);
	}

	public void deleteMe()
	{
		ServerVariables.set("all_spent", String.valueOf(all_spent));
	}
}