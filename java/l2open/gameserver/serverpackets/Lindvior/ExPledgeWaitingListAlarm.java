package l2open.gameserver.serverpackets;

/**
 * @author Smo
 */
public class ExPledgeWaitingListAlarm extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0x151);
	}
}
