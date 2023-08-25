package l2open.gameserver.serverpackets;

public class ExNavitAdventEffect extends L2GameServerPacket
{
	private int _timeToLeft;
	     
	public ExNavitAdventEffect(int timeToLeft)
    {
	    _timeToLeft = timeToLeft;
    }
	     
   @Override
    protected void writeImpl()
    {
        writeC(0xFE);
        writeH(getClient().isLindvior() ? 0xE4 : 0xE0);
        writeD(_timeToLeft);
    }
}