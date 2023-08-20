package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.network.L2GameClient;

public class ReplyGameGuardQuery extends L2GameClientPacket
{
    public byte[] _data;

	@Override
	public void readImpl()
	{
		if(ConfigValue.CCPGuardEnable)
		{
			_data = new byte[ConfigValue.CCPGuardSize];
			ccpGuard.Protection.doReadReplyGameGuard(getClient(), _buf, _data);
		}
	}

	@Override
	public void runImpl()
	{
		L2GameClient client = getClient();

      	if(client != null)
		{
			getClient().setGameGuardOk(true);
			if(ConfigValue.CCPGuardEnable)
			{
				if(_data == null)
					_data = new byte[ConfigValue.CCPGuardSize];
				ccpGuard.Protection.doReplyGameGuard(client, _data);
			}
		}
	}
}