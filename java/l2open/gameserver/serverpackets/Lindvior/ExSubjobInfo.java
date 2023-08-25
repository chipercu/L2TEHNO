package l2open.gameserver.serverpackets;

import java.util.Collection;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2SubClass;

/**
 * @author ALF
 * @author Darvin
 * @data 09.02.2012
 */

public class ExSubjobInfo extends L2GameServerPacket
{
	private Collection<L2SubClass> _subClasses;
	private final int _raceId, _classId;
	private final boolean _openStatus;

	public ExSubjobInfo(L2Player player, boolean openStatus)
	{
		_openStatus = openStatus;
		_raceId = player.getRace().ordinal();
		_classId = player.getClassId().ordinal();
		//_subClasses = player.getSubClassList().values();
	}
	
	@Override
	protected void writeImpl()
	{
		writeEx(0xEA);
		writeC(_openStatus ? 0x01 : 0x00);
		writeD(_classId);
		writeD(_raceId);
		writeD(0); // dell
		/*writeD(_subClasses.size());
		for(L2SubClass subClass : _subClasses)
		{
			writeD(subClass.getIndex());
			writeD(subClass.getClassId());
			writeD(subClass.getLevel());
			writeC(subClass.getType().ordinal());
		}*/
	}
}
