package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Character;

public class SpecialCamera extends L2GameServerPacket
{	
	private final int _id;
	private final int _force;
	private final int _angle1;
	private final int _angle2;
	private final int _time;
	private final int _duration;
	private final int _relYaw;
	private final int _relPitch;
	private final int _isWide;
	private final int _relAngle;
	private final int _unk;

	public SpecialCamera(L2Character creature, int force, int angle1, int angle2, int time, int range, int duration, int relYaw, int relPitch, int isWide, int relAngle)
	{
		this(creature, force, angle1, angle2, time, duration, range, relYaw, relPitch, isWide, relAngle, 0);
	}
	
	public SpecialCamera(L2Character creature, L2Character talker, int force, int angle1, int angle2, int time, int duration, int relYaw, int relPitch, int isWide, int relAngle)
	{
		this(creature, force, angle1, angle2, time, duration, 0, relYaw, relPitch, isWide, relAngle, 0);
	}
	
	public SpecialCamera(L2Character creature, int force, int angle1, int angle2, int time, int range, int duration, int relYaw, int relPitch, int isWide, int relAngle, int unk)
	{
		_id = creature.getObjectId();
		_force = force;
		_angle1 = angle1;
		_angle2 = angle2;
		_time = time;
		_duration = duration;
		_relYaw = relYaw;
		_relPitch = relPitch;
		_isWide = isWide;
		_relAngle = relAngle;
		_unk = unk;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xD6);
		writeD(_id);
		writeD(_force);
		writeD(_angle1);
		writeD(_angle2);
		writeD(_time);
		writeD(_duration);
		writeD(_relYaw);
		writeD(_relPitch);
		writeD(_isWide);
		writeD(_relAngle);
		writeD(_unk);
	}
}