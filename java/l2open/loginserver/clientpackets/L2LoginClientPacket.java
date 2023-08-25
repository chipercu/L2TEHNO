package l2open.loginserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.network.ReceivablePacket;
import l2open.loginserver.L2LoginClient;

import java.util.logging.Logger;

/**
 *
 * @author KenM
 */
public abstract class L2LoginClientPacket extends ReceivablePacket<L2LoginClient>
{
	public static Logger _log = Logger.getLogger(L2LoginClientPacket.class.getName());

	/**
	 * @see l2open.extensions.network.ReceivablePacket#read()
	 */
	@Override
	protected final boolean read()
	{
		try
		{
			if(ConfigValue.DebugClientPackets)
				_log.info("Client send to LoginServer("+getClient().toString()+") packets: " + getType());

			return readImpl();
		}
		catch(Exception e)
		{
			_log.severe("ERROR READING: " + this.getClass().getSimpleName());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run()
	{
		try
		{
			runImpl();
		}
		catch(Exception e)
		{
			_log.severe("runImpl error: Client: " + getClient().toString());
			e.printStackTrace();
		}
		getClient().can_runImpl = true;
	}

	protected abstract boolean readImpl();

	protected abstract void runImpl() throws Exception;

	public String getType()
	{
		return "[C] " + getClass().getSimpleName();
	}
}
