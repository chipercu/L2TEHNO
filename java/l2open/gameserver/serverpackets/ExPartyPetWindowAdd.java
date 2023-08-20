package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;

public class ExPartyPetWindowAdd extends L2GameServerPacket
{
	private int owner_obj_id, npc_id, _type, curHp, maxHp, curMp, maxMp, level;
	private int obj_id = 0;
	private String _name;

	public ExPartyPetWindowAdd(L2Summon summon)
	{
		if(summon == null)
			return;
		L2Player player = summon.getPlayer();
		if(player == null)
			return;

		obj_id = summon.getObjectId();
		owner_obj_id = player.getObjectId();
		npc_id = summon.getTemplate().npcId + 1000000;
		_type = summon.getSummonType();
		_name = summon.getName();
		curHp = (int) summon.getCurrentHp();
		maxHp = summon.getMaxHp();
		curMp = (int) summon.getCurrentMp();
		maxMp = summon.getMaxMp();
		level = summon.getLevel();
	}

	@Override
	protected final void writeImpl()
	{
		if(obj_id == 0)
			return;

		writeC(EXTENDED_PACKET);
		writeH(0x18);

		writeD(obj_id);
		writeD(npc_id);
		writeD(_type);
		writeD(owner_obj_id);
		writeS(_name);
		writeD(curHp);
		writeD(maxHp);
		writeD(curMp);
		writeD(maxMp);
		writeD(level);
	}
}