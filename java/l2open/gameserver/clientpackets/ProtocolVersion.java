package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.KeyPacket;
import l2open.gameserver.serverpackets.SendStatus;

import com.lameguard.session.LameClientV195;

import org.strixguard.StrixGuard;
import org.strixguard.manager.StrixGuardManager;
import org.strixguard.manager.clientsession.DecryptedData;
import org.strixguard.network.crypt.CryptKeygen;

import java.util.logging.Logger;


public class ProtocolVersion extends L2GameClientPacket
{
	static Logger _log = Logger.getLogger(ProtocolVersion.class.getName());

	private KeyPacket pk;
	private int _version;
	private byte[] _data;
	private byte[] _check;
	private static final byte[] _xorB = { (byte) 0x4C, (byte) 0x32, (byte) 0x52, (byte) 0x2D, (byte) 0x44, (byte) 0x52,	(byte) 0x69, (byte) 0x4E };
	//TODO[K] - Guard section start
	private String data;
	//TODO[K] - Guard section end

	@Override
	public void readImpl()
	{
		L2GameClient _client = getClient();
		if(_buf.remaining() < 4)
		{
			// Проверки рейтинга типа l2top.in.ua
			_client.close(new KeyPacket(null));
			return;
		}
		_version = readD();

		if(_version == -2 || _buf.remaining() == 0)
		{
			_client.close(new KeyPacket(null));
			return;
		}
		if(ConfigValue.StrixGuardEnable)
		{
			//TODO[K] - Guard section start
			if(_buf.remaining() > 400)
			{
				data = readS();
			}
			else if(StrixGuard.getInstance().isProtectEnabled())
			{
				_client.close(new KeyPacket(null));
				_log.info("Used un-protected system patch. Token size: " + _buf.remaining() + " Patch version: " + _version);
				return;
			}
			//TODO[K] - Guard section end
		}
		else
		{
			if(_buf.remaining() >= 256)
			{
				_data = new byte[256];
				_check = new byte[4];
				readB(_data);
				if(_buf.remaining() >= 4)
					readB(_check);
			}
			if(!ConfigValue.LameGuard)
				pk = new KeyPacket(_client.enableCrypt());
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
			if(ConfigValue.StrixGuardEnable)
			{
				//TODO[K] - Guard section start
				if(!StrixGuard.getInstance().isProtectEnabled())
				{
					_client.setRevision(_version);
					sendPacket(new KeyPacket(_client.enableCrypt()));
					return;
				}
				else
				{
					try
					{
						final DecryptedData clientSession = StrixGuardManager.getInstance().validateClient(_client.getIpAddr(), data);
						if(clientSession != null)
						{
							_client.setRevision(_version);
							_client.setVMPHWID(clientSession.getVmpHwidInfo());
							_client.setHWID(clientSession.getVmpHwidInfo());
							_client.setWindowsHWID(clientSession.getWindowsHwidInfo());

							final byte[] key = CryptKeygen.getRandomKey();
							_client._crypt_strix.setKey(key); // если _crypt имеет private соглашение, переделываем на public либо реализовываем гет\сет методы.
							sendPacket(new KeyPacket(key));
							return;
						}
					}
					catch(final Exception e)
					{
						e.printStackTrace();
					}
				}
				_client.close(new KeyPacket(null));
				return;
				//TODO[K] - Guard section end
			}
			else if(!ConfigValue.LameGuard)
			{
				if(_version < ConfigValue.MinProtocolRevision || _version > ConfigValue.MaxProtocolRevision)
				{
					_log.info("Client Protocol Revision: " + _version + ", client IP: " + _client.getIpAddr() + " not allowed. Supported protocols: from " + ConfigValue.MinProtocolRevision + " to " + ConfigValue.MaxProtocolRevision + ". Closing connection.");
					_client.close(new KeyPacket(null));
					return;
				}
				_client.setRevision(_version);
				sendPacket(pk);
			}
			else
			{
				if (_version == -2L)
				{
					_client.closeNow(false);
					return;
				}
				else if (_version == -3L)
				{
					_client.close((new SendStatus()));
					return;
				}
				else if ((_version >= 267) && (_version <= 280))
				{
					try
					{
						if(com.lameguard.LameGuard.getInstance().checkData(_data, _check))
						{
							com.lameguard.session.ClientSession cs = com.lameguard.LameGuard.getInstance().checkClient(_client.getIpAddr(), _data);
							if(cs != null)
							{
								_client.setRevision(_version);
								Object cl = _client;
								if(cl instanceof LameClientV195)
								{
									((LameClientV195)cl).setProtected(true);
									((LameClientV195)cl).setHWID(cs.getHWID());
									((LameClientV195)cl).setInstanceCount(cs.getInstances());
									((LameClientV195)cl).setPatchVersion(cs.getPatch());
								}
								_client.setHWID(cs.getHWID());
								byte[] key = _client.enableCrypt();
								_data = com.lameguard.LameGuard.getInstance().assembleAnswer(cs, key);
								sendPacket(new KeyPacket(_data));
								return;
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}