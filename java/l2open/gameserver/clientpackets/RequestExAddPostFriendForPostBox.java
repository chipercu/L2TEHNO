package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExConfirmAddingPostFriend;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.CharNameTable;
import l2open.gameserver.tables.CharacterPostFriend;
import org.napile.primitive.maps.IntObjectMap;

public class RequestExAddPostFriendForPostBox extends L2GameClientPacket
{
	private String _name;

	@Override
	public void readImpl()
	{
		_name = readS(ConfigValue.cNameMaxLen);
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		int targetObjectId = CharNameTable.getInstance().getObjectIdByName(_name);
		if(targetObjectId == 0)
		{
			player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.NAME_IS_NOT_EXISTS));
			return;
		}
		if(_name.equalsIgnoreCase(player.getName()))
		{
			player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.NAME_IS_NOT_REGISTERED));
			return;
		}
		IntObjectMap<String> postFriend = player.getPostFriends();
		if(postFriend.size() >= 100)
		{
			player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.LIST_IS_FULL));
			return;
		}
		if(postFriend.containsKey(targetObjectId))
		{
			player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.ALREADY_ADDED));
			return;
		}
		CharacterPostFriend.getInstance().insert(player, targetObjectId);
		postFriend.put(targetObjectId, CharNameTable.getInstance().getNameByObjectId(targetObjectId));
		player.sendPacket(new SystemMessage(SystemMessage.S1_SUCCESSFULLY_ADDED_TO_CONTACT_LIST).addString(_name), new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.SUCCESS));
	}
}