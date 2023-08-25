package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

import java.util.ArrayList;
import java.util.List;

public class ExEventMatchManage extends L2GameServerPacket
{
	int game_id;
	int game_state;
	int unk2;
	String team1_name;
	int lock_team1;
	String team2_name;
	int lock_team2;
	private List<EventMatchMemberInfo> members = new ArrayList<EventMatchMemberInfo>();

	public ExEventMatchManage(int _game_id, int _game_state, int _unk2, String _team1_name, int _lock_team1, String _team2_name, int _lock_team2)
	{
		game_id = _game_id;
		game_state = _game_state;
		unk2 = _unk2;
		team1_name = _team1_name;
		lock_team1 = _lock_team1;
		team2_name = _team2_name;
		lock_team2 = _lock_team2;
	}

	public void addPlayer(EventMatchMemberInfo member)
	{
		members.add(member);
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x30);

		writeD(game_id);		// game_id
		writeC(game_state);		// game_state: 1 - игра не началась, 2 - игра началась, 3 - игра приостановленна
		writeC(unk2);			// ?
		writeS(team1_name);		// team1_name
		writeC(lock_team1);		// lock_team1
		writeS(team2_name);		// team2_name
		writeC(lock_team2);		// lock_team2
		writeD(members.size());	// loop_count

		for(EventMatchMemberInfo emti : members)
		{
			writeC(emti.team_id);			// team_id
			writeC(emti.is_team_leader);	// is_team_leader
			writeD(emti.object_id);			// object_id
			writeS(emti.char_name);			// char_name
			writeD(emti.char_class_id);		// char_class_id
			writeD(emti.char_level);		// char_level
		}
	}

	public static class EventMatchMemberInfo
	{
		public int team_id;
		public int is_team_leader;
		public int object_id;
		public String char_name;
		public int char_class_id;
		public int char_level;

		public EventMatchMemberInfo(int _team_id, int _is_team_leader, int _object_id, String _char_name, int _char_class_id, int _char_level)
		{
			team_id = _team_id;
			is_team_leader = _is_team_leader;
			object_id = _object_id;
			char_name = _char_name;
			char_class_id = _char_class_id;
			char_level = _char_level;
		}
	}
}