package l2open.gameserver.model.entity.residence;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;

public class TeleportLocation
{
	public final long _price;
	public final L2Item _item;
	public final String[] _name;
	public final String _target;

	public TeleportLocation(String target, int item, long price, String[] name)
	{
		_target = target;
		_price = price;
		_name = name;
		_item = ItemTemplates.getInstance().getTemplate(item);
	}
			
	public String getName(L2Player player)
	{
		if(_name.length != 2)
			return _name[0];
		return player.isLangRus() ? _name[1] : _name[0];
	}
}