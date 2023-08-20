package l2open.gameserver.serverpackets;

public class DamageTextNewPacket extends L2GameServerPacket
{
	int _victim;		// object_id of target receiving damage
	int _skill_id;		// skill id of the attack (0 for no skill)
	int _skill_lvl;		// skill lvl of the attack
	int _color1;		// color for skillname text and border of skill icon
	int _color2;		// color for message text
	String _message;	// message text (can be damage, or skill-related texts such as "Half-Kill" - or anything)
	
	public DamageTextNewPacket(int victim_id, int skill_id, int skill_lvl, int color1, int color2, String message)
	{
		_victim = victim_id;
		_skill_id = skill_id;
		_skill_lvl = skill_lvl;
		_color1 = color1;
		_color2 = color2;
		_message = message;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFF);
		writeC(9);
		
		writeD(_victim);
		writeD(_skill_id);
		writeD(_skill_lvl);
		writeD(_color1);
		writeD(_color2);
		writeS(_message);
	}
	
}
