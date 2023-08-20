package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

import java.util.ArrayList;
import java.util.List;

public class ExEventMatchTeamInfo extends L2GameServerPacket
{
	private List<EventMatchTeamInfo> members = new ArrayList<EventMatchTeamInfo>();

	private int game_id;
	private int team_id;

	public ExEventMatchTeamInfo(int _game_id, int _team_id)
	{
		game_id = _game_id;
		team_id = _team_id;
	}

	public void addPlayer(L2Player member)
	{
		members.add(new EventMatchTeamInfo(member));
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x1C);

		writeD(game_id); // game_id
		writeC(team_id); // team_id
		writeD(members.size()); // loop_count

		//dS dddddddddd
		for(EventMatchTeamInfo emti : members)
		{
			writeD(emti.obj_id); // obj_id
			writeS(emti.char_name); // char_name
			writeD(emti.cur_hp); // cur_hp
			writeD(emti.max_hp); // max_hp
			writeD(emti.cur_mp); // cur_mp
			writeD(emti.max_mp); // max_mp
			writeD(emti.cur_cp); // cur_cp
			writeD(emti.max_cp); // max_cp
			writeD(emti.char_level); // char_level
			writeD(emti.char_class_id); // char_class_id
			writeD(0x00); // ?
			writeD(0x00); // ?
		}
	}

	public static class EventMatchTeamInfo
	{
		public String char_name;
		public int obj_id, cur_cp, max_cp, cur_hp, max_hp, cur_mp, max_mp, char_level, char_class_id, race_id;

		public EventMatchTeamInfo(L2Player member)
		{
			char_name = member.getName();
			obj_id = member.getObjectId();
			cur_cp = (int) member.getCurrentCp();
			max_cp = member.getMaxCp();
			cur_hp = (int) member.getCurrentHp();
			max_hp = member.getMaxHp();
			cur_mp = (int) member.getCurrentMp();
			max_mp = member.getMaxMp();
			char_level = member.getLevel();
			char_class_id = member.getClassId().getId();
			race_id = member.getRace().ordinal();
		}
	}
}