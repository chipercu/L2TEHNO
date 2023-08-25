package l2open.gameserver.serverpackets;

import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.ArrayList;
import java.util.List;

public class NpcSay extends L2GameServerPacket
{
	private int _objId;
	private int _type;
	private int _id;
	private String _text;
	private int npcStrings = -1;
	
	// TODO не найден номер
	public NpcSay(L2NpcInstance npc, int chatType, String text)
	{
		_objId = npc.getObjectId();
		_type = chatType;
		_text = text;
		_id = npc.getNpcId();
	}

	public NpcSay(L2NpcInstance npc, int chatType, int npcStringsId)
	{
		_objId = npc.getObjectId();
		_type = chatType;
		npcStrings = npcStringsId;
		_id = npc.getNpcId();
	}

	public NpcSay(L2NpcInstance npc, int chatType, int npcStringsId, String text)
	{
		_objId = npc.getObjectId();
		_type = chatType;
		_text = text;
		npcStrings = npcStringsId;
		_id = npc.getNpcId();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x30);
		writeD(_objId); // object_id отсылающего
		writeD(_type); // Тип чата, 0 = tell, 1 = shout, 2 = pm, 3 = party... Совпадает с Say2
		writeD(1000000 + _id); // npc id от кого отправлен пакет, клиент получает по нему имя.
		writeD(npcStrings);
		writeS(_text);
	}
}