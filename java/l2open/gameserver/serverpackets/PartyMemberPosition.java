package l2open.gameserver.serverpackets;

import javolution.util.FastMap;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.network.L2GameClient;

import java.util.Collection;

public class PartyMemberPosition extends L2GameServerPacket
{
	private final FastMap<Integer, int[]> positions = new FastMap<Integer, int[]>();

	public PartyMemberPosition()
	{}

	public PartyMemberPosition add(Collection<L2Player> members)
	{
		if(members != null)
			for(L2Player member : members)
				add(member);
		return this;
	}

	public PartyMemberPosition add(L2Player actor)
	{
		if(actor != null)
			positions.put(actor.getObjectId(), new int[] { actor.getX(), actor.getY(), actor.getZ() });
		return this;
	}

	public void clear()
	{
		positions.clear();
	}

	public int size()
	{
		return positions.size();
	}

	@Override
	protected final void writeImpl()
	{
		L2GameClient client = getClient();
		if(client == null || positions.isEmpty())
			return;

		L2Player player = client.getActiveChar();
		if(player == null)
			return;

		int this_player_id = player.getObjectId();
		int sz = positions.containsKey(this_player_id) ? positions.size() - 1 : positions.size();
		if(sz < 1)
			return;

		writeC(0xba);
		writeD(sz);
		int[] pos;
		for(Integer id : positions.keySet())
			if(id != this_player_id)
			{
				pos = positions.get(id);
				writeD(id);
				writeD(pos[0]);
				writeD(pos[1]);
				writeD(pos[2]);
			}
	}
}