package l2open.gameserver.clientpackets;

import l2open.extensions.network.SelectorThread;
import l2open.gameserver.model.L2Player;
/**
 * format: ddd
 */
public class NetPing extends L2GameClientPacket
{
	private int corectPing;
	private int clientSesionId;
	private int ping;

	@Override
	public void runImpl()
	{
		//_log.info(getType() + " :: " + ping + " :: " + corectPing + " :: " + clientSesionId + " :: "+System.currentTimeMillis()+" :: "+(System.currentTimeMillis()-1362277045339L));
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(getClient().ping_send == 0)
		{
			activeChar.sendMessage("NetPing: " + ((System.currentTimeMillis()-ping-l2open.gameserver.GameStart.serverUpTime())/2)+" corect: "+corectPing+" SID: "+clientSesionId);
			getClient().ping_send = 1;
		}
		//_log.info("DelayNetPing: "+(System.currentTimeMillis()-SelectorThread._timeStart));
		getClient().pingTime = (System.currentTimeMillis()-ping-l2open.gameserver.GameStart.serverUpTime());
		getClient().onNetPing(ping);
	}

	@Override
	public void readImpl()
	{
		ping = readD();
		corectPing = readD();
		clientSesionId = readD();
	}
}