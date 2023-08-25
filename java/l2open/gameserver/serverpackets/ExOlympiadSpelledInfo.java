package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.util.GArray;

public class ExOlympiadSpelledInfo extends L2GameServerPacket
{
	// chdd(dhd)
	private int char_obj_id = 0;
	private GArray<Effect> _effects;

	class Effect
	{
		int skillId;
		int level;
		int duration;

		public Effect(int skillId, int level, int duration)
		{
			this.skillId = skillId;
			this.level = level;
			this.duration = duration;
		}
	}

	public ExOlympiadSpelledInfo()
	{
		_effects = new GArray<Effect>();
	}

	public void addEffect(int skillId, int level, int duration)
	{
		_effects.add(new Effect(skillId, level, duration));
	}

	public void addSpellRecivedPlayer(L2Player cha)
	{
		if(cha != null)
			char_obj_id = cha.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		if(char_obj_id == 0)
			return;

		writeC(EXTENDED_PACKET);
		writeHG(0x7b);

		writeD(char_obj_id);
		writeD(_effects.size());
		for(Effect temp : _effects)
		{
			writeD(temp.skillId);
			writeH(temp.level);
			writeD(temp.duration);
		}
	}
}