package l2open.gameserver.serverpackets.Interface;

import emudev.KeyChecker;
import l2open.gameserver.serverpackets.L2GameServerPacket;

public class KeyPacket extends L2GameServerPacket {
	byte[] arr;
	
	public KeyPacket sendKey(byte[] key, int size) {
		arr = KeyChecker.getKey(key, size);
		return this;
	}
	
	@Override
	protected void writeImpl() {
		writeEx(0xF5);//ch[0xFE:0xF5]
		writeH(arr.length);
		writeB(arr);
	}
}