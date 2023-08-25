package l2open.gameserver.serverpackets;
/**
 * @author Mobius
 * @version $Revision: 1.0 $
 */
public class ExWaitWaitingSubStituteInfo extends L2GameServerPacket
{
	/**
	 * Field turnOn.
	 */
	boolean turnOn;
	
	/**
	 * Constructor for ExWaitWaitingSubStituteInfo.
	 * @param _turnOn boolean
	 */
	public ExWaitWaitingSubStituteInfo(boolean _turnOn)
	{
		turnOn = _turnOn;
	}
	
	public static final int WAITING_CANCEL = 0;
	public static final int WAITING_OK = 1;
	
	private int _code=0;
	
	public ExWaitWaitingSubStituteInfo(int code)
	{
		_code = code;
	}
	
	/**
	 * Method writeImpl.
	 */
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x104);
        writeD(_code);
	}
}
