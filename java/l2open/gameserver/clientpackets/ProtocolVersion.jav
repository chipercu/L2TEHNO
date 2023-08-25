package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.KeyPacket;
import l2open.gameserver.serverpackets.SendStatus;

import ru.akumu.smartguard.SmartGuard;

import com.lameguard.session.LameClientV195;

import org.strixplatform.StrixPlatform;
import org.strixplatform.logging.Log;
import org.strixplatform.managers.ClientGameSessionManager;
import org.strixplatform.managers.ClientProtocolDataManager;
import org.strixplatform.utils.StrixClientData;

import java.util.logging.Logger;


public class ProtocolVersion extends L2GameClientPacket
{
	static Logger _log = Logger.getLogger(ProtocolVersion.class.getName());

	private int _version;
	//TODO[K] - Guard section start
	private byte[] data;
	private int dataChecksum;
	//TODO[K] - Guard section end

	@Override
	public void readImpl()
	{
		_version = readD();

		if(StrixPlatform.getInstance().isPlatformEnabled())
		{
			try
			{
				if(_buf.remaining() >= StrixPlatform.getInstance().getProtocolVersionDataSize())
				{
					data = new byte[StrixPlatform.getInstance().getClientDataSize()];
					readB(data);
					dataChecksum = readD();
				}
			}
			catch(final Exception e)
			{
				_log.info("Client [IP=" + getClient().getIpAddr() + "] used unprotected client. Disconnect...");
				getClient().close(new KeyPacket(null));
				return;
			}
		}
	}

	@Override
	public void runImpl()
	{
		L2GameClient _client = getClient();

		if(_version == -2)
			_client.closeNow(false);
		else
		{
			if(_version == -3)
			{
				_client.close(new SendStatus());
				return;
			}
			else if(_version < ConfigValue.MinProtocolRevision || _version > ConfigValue.MaxProtocolRevision)
			{
				_log.info("Client Protocol Revision: " + _version + ", client IP: " + _client.getIpAddr() + " not allowed. Supported protocols: from " + ConfigValue.MinProtocolRevision + " to " + ConfigValue.MaxProtocolRevision + ". Closing connection.");
				_client.close(new KeyPacket(null));
				return;
			}

			//TODO[K] - Strix section start
			if(!StrixPlatform.getInstance().isPlatformEnabled())
			{
				getClient().setRevision(_version);
				sendPacket(new KeyPacket(getClient().enableCrypt()));
				return;
			}
			else
			{
				if(data == null)
				{
					_log.info("Client [IP=" + getClient().getIpAddr() + "] used unprotected client. Disconnect...");
					getClient().close(new KeyPacket(null));
					return;
				}
				else
				{
					final StrixClientData clientData = ClientProtocolDataManager.getInstance().getDecodedData(data, dataChecksum);
					if(clientData != null)
					{
						if(!ClientGameSessionManager.getInstance().checkServerResponse(clientData))
						{
							getClient().close(new KeyPacket(null, clientData));
							return;
						}
						getClient().setStrixClientData(clientData);
						getClient().setRevision(_version);
						sendPacket(new KeyPacket(getClient().enableCrypt()));
						return;
					}
					_log.info("Decode client data failed. See Strix-Platform log file. Disconected client " + getClient().getIpAddr());
					getClient().close(new KeyPacket(null));
				}
			}
			//TODO[K] - Strix section end
		}
	}
}