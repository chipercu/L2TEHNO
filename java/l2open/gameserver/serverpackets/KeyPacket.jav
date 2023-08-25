package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;

import ru.akumu.smartguard.SmartGuard;

import l2open.config.*;

import org.strixplatform.StrixPlatform;
import org.strixplatform.utils.StrixClientData;

public class KeyPacket extends L2GameServerPacket
{
	private byte[] _key;

	//TODO[K] - Guard section start
	private final StrixClientData clientData;
	// TODO[K] - Strix section end

	public KeyPacket(byte[] key)
	{
		_key = key;

		//TODO[K] - Guard section start
		clientData = null;
		// TODO[K] - Strix section end
	}

	//TODO[K] - Guard section start
	public KeyPacket(final byte[] key, final StrixClientData cd)
	{
		_key = key;
		clientData = cd;
	}
	// TODO[K] - Strix section end

	@Override
	public void writeImpl()
	{
		writeC(0x2E);
		if(_key == null || _key.length == 0)
		{
			writeC(0x00);
			//TODO[K] - Guard section start
			if(StrixPlatform.getInstance().isBackNotificationEnabled() && clientData != null)
			{
				writeC(clientData.getServerResponse().ordinal());
				//writeQ(); Resolved to send ban time expire.
			}
			// TODO[K] - Strix section end
			return;
		}
		writeC(0x01);
		writeB(_key);
		writeD(0x01);
		writeD(0x00);
		writeC(0x00);
		writeD(0x00); // Seed (obfuscation _key)
		//writeC(0x00);    // 1 - Classic client, 0 - Other(old and new) client
		//writeC(0x00);    // 1 - Arena(if classic active)
	}
}