package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.util.GArray;

/**
 * format   ddd+[dSddddddddddddd{ddSddddd}]
 */
public class PartySmallWindowAll extends L2GameServerPacket
{
	private int leader_id, loot;
	private GArray<PartySmallWindowMemberInfo> members = new GArray<PartySmallWindowMemberInfo>();

	public PartySmallWindowAll(L2Party party, L2Player exclude)
	{
		leader_id = party.getPartyLeader().getObjectId();
		loot = party.getLootDistribution();

		for(L2Player member : party.getPartyMembers())
			if(!member.equals(exclude))
				members.add(new PartySmallWindowMemberInfo(member));
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4E);
		writeD(leader_id); // c3 party leader id
		writeD(loot); //c3 party loot type (0,1,2,....)
		writeD(members.size());
		for(PartySmallWindowMemberInfo member : members)
		{
			writeD(member._id);
			writeS(member._name);
			writeD(member.curCp);
			writeD(member.maxCp);
			writeD(member.curHp);
			writeD(member.maxHp);
			writeD(member.curMp);
			writeD(member.maxMp);
			if(getClient().isLindvior())
				writeD(0x00); // vitality
			writeD(member.level);
			writeD(member.class_id);
			writeD(0);//writeD(0x01); ??
			writeD(member.race_id);
			writeD(0); // writeD(member._isDisguised);
			writeD(0); // writeD(member._dominionId);

			if(getClient().isLindvior())
			{
				writeD(0x00); // replace Идет ли поиск замены игроку
				writeD(member.pet_id != 0 ? 0x01 : 0x00);
			}
			if(member.pet_id != 0)
			{
				writeD(member.pet_id);
				writeD(member.pet_NpcId);
				writeS(member.pet_Name);
				writeD(member.pet_curHp);
				writeD(member.pet_maxHp);
				writeD(member.pet_curMp);
				writeD(member.pet_maxMp);
				writeD(member.pet_level);
			}
			else if(!getClient().isLindvior())
				writeD(0);
		}
	}

	public static class PartySmallWindowMemberInfo
	{
		public String _name, pet_Name;
		public int _id, curCp, maxCp, curHp, maxHp, curMp, maxMp, level, class_id, race_id;
		public int pet_id, pet_NpcId, pet_curHp, pet_maxHp, pet_curMp, pet_maxMp, pet_level;

		public PartySmallWindowMemberInfo(L2Player member)
		{
			_name = member.getName();
			_id = member.getObjectId();
			curCp = (int) member.getCurrentCp();
			maxCp = member.getMaxCp();
			curHp = (int) member.getCurrentHp();
			maxHp = member.getMaxHp();
			curMp = (int) member.getCurrentMp();
			maxMp = member.getMaxMp();
			level = member.getLevel();
			class_id = member.getClassId().getId();
			race_id = member.getRace().ordinal();

			L2Summon pet = member.getPet();
			if(pet != null)
			{
				pet_id = pet.getObjectId();
				pet_NpcId = pet.getNpcId() + 1000000;
				pet_Name = pet.getName();
				pet_curHp = (int) pet.getCurrentHp();
				pet_maxHp = pet.getMaxHp();
				pet_curMp = (int) pet.getCurrentMp();
				pet_maxMp = pet.getMaxMp();
				pet_level = pet.getLevel();
			}
			else
				pet_id = 0;
		}
	}
}