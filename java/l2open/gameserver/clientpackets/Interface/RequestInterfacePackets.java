package l2open.gameserver.clientpackets.Interface;

import l2open.gameserver.clientpackets.L2GameClientPacket;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.Interface.ConfigPacket;
import l2open.gameserver.serverpackets.Interface.CustomFontsPacket;
import l2open.gameserver.serverpackets.Interface.KeyPacket;
import l2open.gameserver.serverpackets.Interface.ScreenTextInfoPacket;

//Opcodes: D0[c]:83[h]:10[d]
public class RequestInterfacePackets extends L2GameClientPacket
{
	byte[] data = null;
	int data_size;
	
	@Override
	protected void readImpl() {
		if(_buf.remaining() > 2) {
			data_size = readH();
			if(_buf.remaining() >= data_size) {
				data = new byte[data_size];
				readB(data);
			}
		}
	}

	@Override
	protected void runImpl() {
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;
		activeChar.sendPacket(new KeyPacket().sendKey(data, data_size));
		activeChar.sendPacket(new ConfigPacket());
		activeChar.sendPacket(new CustomFontsPacket().sendFontInfos());
		activeChar.sendPacket(new ScreenTextInfoPacket().sendTextInfos());
	}
}
