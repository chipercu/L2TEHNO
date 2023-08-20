package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.CrestCache;
import l2open.gameserver.model.L2Alliance;
import l2open.gameserver.model.L2Player;

import java.util.logging.Logger;

public class RequestSetAllyCrest extends L2GameClientPacket
{
	// format: cdb
	static Logger _log = Logger.getLogger(RequestSetPledgeCrest.class.getName());

	private int _length;
	private byte[] _data;

	@Override
	public void readImpl()
	{
		_length = readD();
		if(_length > _buf.remaining() || _length != 192)
		{
			_log.warning("Possibly server crushing packet: " + getType() + " with length " + _length);
			_buf.clear();
			return;
		}
		_data = new byte[_length];
		readB(_data);

		// сравниваем нашу шапку, она всегда статическая...
		for(int i=0;i<116;i++)
			if(CrestCache.header_ally_crest[i] != _data[i])
			{
				_log.info("Crest Err["+i+"]["+CrestCache.header_ally_crest[i]+"]["+_data[i]+"]");
				_length=0;
				_data = null;
				break;
			}
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2Alliance ally = activeChar.getAlliance();
		if(ally != null && activeChar.isAllyLeader())
		{
			if(ally.hasAllyCrest())
				CrestCache.removeAllyCrest(ally);

			if(_data != null && _length != 0)
				CrestCache.saveAllyCrest(ally, _data);

			ally.broadcastAllyStatus(false);
		}
	}
}