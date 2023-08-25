package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.model.barahlo.Drop;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.reference.HardReference;
import l2open.util.Rnd;

import java.util.ArrayList;
import java.util.List;

public class PokemonGoNpcInstance extends L2NpcInstance
{
	public PokemonGoNpcInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public List<Drop> _drops;
	public List<HardReference<L2Player>> _players = new ArrayList<HardReference<L2Player>>();
	public List<Integer> _players_i = new ArrayList<Integer>();

	public boolean can_cast(L2Player player)
	{
		if(!ConfigValue.PokemonGoEnable || _players_i.contains(player.getObjectId()))
		{
			player.sendMessage("Вы уже получили награду.");
			return false;
		}
		if(ConfigValue.PokemonGoHwid)
			for(HardReference<L2Player> ref : _players)
			{
				L2Player p = ref.get();
				if(p == null)
					continue;
				if(p.getHWIDs().equals(player.getHWIDs()))
				{
					player.sendMessage("С данного компьютера уже получена награда.");
					return false;
				}
			}
		return true;
	}

	public void give_drop(L2Player player)
	{
		if(!ConfigValue.PokemonGoEnable)
			return;
		if(ConfigValue.PokemonGoHwid)
			for(HardReference<L2Player> ref : _players)
			{
				L2Player p = ref.get();
				if(p == null)
					continue;
				if(p.getHWIDs().equals(player.getHWIDs()))
				{
					player.sendMessage("С данного компьютера уже получена награда.");
					return;
				}
			}

		if(_players_i.contains(player.getObjectId()))
			player.sendMessage("Вы уже получили награду.");
		else
		{
			_players.add(player.getRef());
			_players_i.add(player.getObjectId());
			for(Drop drop : _drops)
				if(Rnd.get(1000000) <= drop.chance)
				{
					long count = (long)Rnd.get(drop.min_count, drop.max_count);

					player.getInventory().addItem(drop.item_id, count);
					player.sendPacket(SystemMessage.obtainItems(drop.item_id, count, 0));
					return;
				}
		}
	}

	@Override
	public int getTeam()
	{
		return 1;
	}

	@Override
	public void deleteMe()
	{
		_players.clear();
		_players_i.clear();
		super.deleteMe();
	}

	/**
	 * 0 - Голубой
	 * 1 - Красный
	 * 2 - Жёлтый
	 * 3 - Розовый
	 **/
	/*@Override
	public int getFormId()
	{
		return 2;
	}*/
}