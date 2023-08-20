package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.CharacterPostFriend;
import org.apache.commons.lang.StringUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.IntObjectMap.Entry;

public class RequestExDeletePostFriendForPostBox extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl()
	{
		_name = readS();
	}

	@Override
	protected void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		if(StringUtils.isEmpty(_name))
			return;
		int key = 0;
		IntObjectMap<String> postFriends = player.getPostFriends();
		for(Entry entry : postFriends.entrySet())
			if(((String)entry.getValue()).equalsIgnoreCase(_name))
				key = entry.getKey();
		if(key == 0)
		{
			player.sendPacket(new SystemMessage(SystemMessage.NAME_NOT_REGISTERED_ON_CONTACT_LIST));
			return;
		}
		player.getPostFriends().remove(key);
		CharacterPostFriend.getInstance().delete(player, key);
		player.sendPacket(new SystemMessage(SystemMessage.S1_SUCCESFULLY_DELETED_FROM_CONTACT_LIST).addString(_name));
	}
}