package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;

import l2open.config.*;

import org.strixguard.config.GuardConfig;

public class KeyPacket extends L2GameServerPacket
{
	private byte[] _key;

	public KeyPacket(byte[] key)
	{
		_key = key;
	}

	@Override
	public void writeImpl()
	{
		writeC(0x2E);
		if(_key == null || _key.length == 0)
		{
			writeC(0x00);
			return;
		}
		writeC(0x01);
		writeB(_key); // TODO[K] - send all key size! Guard key size == 16byte, and him need all!
		if(ConfigValue.StrixGuardEnable)
		{
			// TODO[K] - Guard Section
			//writeC(GuardConfig.MODULE_DRAW_ENABLED ? 0x01 : 0x00);
			//writeC(GuardConfig.MODULE_DRAW_FPS ? 0x01 : 0x00);
			//writeC(GuardConfig.MODULE_DRAW_PING ? 0x01 : 0x00);
			//writeS(GuardConfig.MODULE_DRAW_TEXT);

			writeC(GuardConfig.MODULE_DRAW_ENABLED ? 0x01 : 0x00);
			writeC(GuardConfig.MODULE_DRAW_FPS ? 0x01 : 0x00);
			writeC(GuardConfig.MODULE_DRAW_PING ? 0x01 : 0x00);
			writeC(GuardConfig.MODULE_DRAW_TIME ? 0x01 : 0x00);
			writeC(0x00); // RESERVED!!!
			//writeC(0x00);  // 1 - Classic client, 0 - Other(old and new) client
            //writeC(0x00);  // 1 - Arena(if classic active)
			writeS(GuardConfig.MODULE_DRAW_TEXT);
			// TODO[K] - Guard Section
		}
		else
		{
			writeD(0x01);
			writeD(ConfigValue.LameGuard ? 0x01 : 0x00);
			writeC(ConfigValue.LameGuard ? 0x01 : 0x00);
			writeD(0x00); // Seed (obfuscation key)
			//writeC(0x01);	// Classic
			//writeC(0x00);	// Arena
		}
	}
}