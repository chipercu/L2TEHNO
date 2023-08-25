package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * Этот пакет отвечает за анимацию высасывания душ из трупов
 * @author SYS
 */
public class SpawnEmitter extends L2GameServerPacket
{
	private int _monsterObjId;
	private int _playerObjId;

	public SpawnEmitter(L2NpcInstance monster, L2Player player)
	{
		_playerObjId = player.getObjectId();
		_monsterObjId = monster.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		//ddd
		writeC(EXTENDED_PACKET);
		writeHG(0x5d);

		writeD(_monsterObjId);
		writeD(_playerObjId);
		writeD(0x00); //unk
	}
}