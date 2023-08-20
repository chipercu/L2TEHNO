package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

public class ExEventMatchUserInfo extends L2GameServerPacket
{
	private int obj_id;
	private String char_name;
	private int max_hp;
	private int cur_hp;
	private int max_mp;
	private int cur_mp;
	private int max_cp;
	private int cur_cp;
	private int char_level;
	private int char_class_id;

	public ExEventMatchUserInfo(L2Player player)
	{
		char_name = player.getName();
		obj_id = player.getObjectId();
		cur_cp = (int) player.getCurrentCp();
		max_cp = player.getMaxCp();
		cur_hp = (int) player.getCurrentHp();
		max_hp = player.getMaxHp();
		cur_mp = (int) player.getCurrentMp();
		max_mp = player.getMaxMp();
		char_level = player.getLevel();
		char_class_id = player.getClassId().getId();
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x02);

		writeD(obj_id); // obj_id
		writeS(char_name); // char_name
		writeD(cur_hp); // cur_hp
		writeD(max_hp); // max_hp
		writeD(cur_mp); // cur_mp
		writeD(max_mp); // max_mp
		writeD(cur_cp); // cur_cp
		writeD(max_cp); // max_cp
		writeD(char_level); // char_level
		writeD(char_class_id); // char_class_id
	}
}